/*

 * Copyright 2009-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.maven;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.MojoExecutionException;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.StartingInfo;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudDomain;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.InstanceInfo;
import org.cloudfoundry.client.lib.domain.InstanceState;
import org.cloudfoundry.client.lib.domain.InstancesInfo;
import org.cloudfoundry.maven.common.Assert;
import org.cloudfoundry.maven.common.CommonUtils;
import org.cloudfoundry.maven.common.DefaultConstants;
import org.cloudfoundry.maven.common.SystemProperties;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Abstract goal for the Cloud Foundry Maven plugin that bundles access to commonly used plugin parameters.
 *
 * @author Gunnar Hillert
 * @author Stephan Oudmaijer
 * @author Ali Moghadam
 * @author Scott Frederick
 * @since 1.0.0
 */
@SuppressWarnings("UnusedDeclaration")
abstract class AbstractApplicationAwareCloudFoundryMojo extends AbstractCloudFoundryMojo {

    private static final int DEFAULT_APP_STARTUP_TIMEOUT = 5;

    /**
     * @parameter default-value="${localRepository}"
     * @readonly
     * @required
     */
    protected ArtifactRepository localRepository;

    /**
     * @parameter default-value="${project.remoteArtifactRepositories}"
     * @readonly
     * @required
     */
    protected java.util.List<ArtifactRepository> remoteRepositories;

    /**
     * The app startup timeout to use for the application.
     *
     * @parameter expression = "${cf.appStartupTimeout}"
     */
    private Integer appStartupTimeout;

    /**
     * @parameter expression="${cf.appname}"
     */
    private String appname;

    /**
     * A string of the form groupId:artifactId:version:packaging[:classifier].
     *
     * @parameter expression = "${cf.artifact}" default-value="${project.groupId}:${project.artifactId}:${project
     * .version}:${project.packaging}"
     */
    private String artifact;

    /**
     * @component
     */
    private ArtifactFactory artifactFactory;

    /**
     * @component
     */
    private ArtifactResolver artifactResolver;

    /**
     * The buildpack to use for the application.
     *
     * @parameter expression = "${cf.buildpack}"
     */
    private String buildpack;

    /**
     * The start command to use for the application.
     *
     * @parameter expression = "${cf.command}"
     */
    private String command;

    /**
     * Set the disk quota for the application
     *
     * @parameter expression="${cf.diskQuota}"
     */
    private Integer diskQuota;

    /**
     * list of domains to use by the application.
     *
     * @parameter expression="${domains}"
     */
    private List<String> domains;

    /**
     * Environment variables
     *
     * @parameter expression="${cf.env}"
     */
    private Map<String, String> env = new HashMap<String, String>();

    /**
     * The health check timeout to use for the application.
     *
     * @parameter expression = "${cf.healthCheckTimeout}"
     */
    private Integer healthCheckTimeout;

    /**
     * Set the expected number <N> of instances
     *
     * @parameter expression="${cf.instances}"
     */
    private Integer instances;

    /**
     * Set the memory reservation for the application
     *
     * @parameter expression="${cf.memory}"
     */
    private Integer memory;

    /**
     * Merge existing env vars when updating the application
     *
     * @parameter expression="${cf.mergeEnv}"
     */
    private Boolean mergeEnv;

    /**
     * Do not auto-start the application
     *
     * @parameter expression="${cf.no-start}"
     */
    private Boolean noStart;

    /**
     * The path of one of the following:
     *
     * <ul> <li>War file to deploy</li> <li>Zip or Jar file to deploy</li> <li>Exploded War directory</li>
     * <li>Directory
     * containing a stand-alone application to deploy</li> </ul>
     *
     * @parameter expression = "${cf.path}"
     */
    private File path;

    /**
     * list of services to use by the application.
     *
     * @parameter expression="${services}"
     */
    private List<CloudServiceWithUserProvided> services;

    /**
     * The stack to use for the application.
     *
     * @parameter expression = "${cf.stack}"
     */
    private String stack;

    /**
     * @parameter expression="${cf.url}"
     */
    private String url;

    /**
     * @parameter expression="${cf.urls}"
     */
    private List<String> urls;

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public void createServices() throws MojoExecutionException {
        List<CloudService> currentServices = getClient().getServices();
        List<String> currentServicesNames = new ArrayList<String>(currentServices.size());

        for (CloudService currentService : currentServices) {
            currentServicesNames.add(currentService.getName());
        }

        for (CloudServiceWithUserProvided service : getServices()) {
            if (currentServicesNames.contains(service.getName())) {
                getLog().debug(String.format("Service '%s' already exists", service.getName()));
            } else {
                getLog().info(String.format("Creating Service '%s'", service.getName()));
                Assert.configurationServiceNotNull(service, null);

                try {
                    if (service.getLabel().equals("user-provided")) {
                        service.setLabel(null);
                        client.createUserProvidedService(service, service.getUserProvidedCredentials(), service
                                .getSyslogDrainUrl());
                    } else {
                        client.createService(service);
                    }
                } catch (CloudFoundryException e) {
                    throw new MojoExecutionException(String.format("Not able to create service '%s'.", service
                            .getName()));
                }
            }
        }
    }

    /**
     * Returns the app startup timeout to use, if set. Otherwise Null is returned. If the parameter is set via the
     * command line (aka system property, then that value is used). If not the pom.xml configuration parameter is used,
     * if available.
     *
     * For a list of available properties see {@link SystemProperties}.
     *
     * @return Returns the app startup timeout or null
     */
    public Integer getAppStartupTimeout() {
        return getIntegerValue(SystemProperties.APP_STARTUP_TIMEOUT, this.appStartupTimeout);
    }

    /**
     * If the application name was specified via the command line ({@link SystemProperties}) then use that property.
     * Otherwise return the appname as injected via Maven or if appname is Null return the artifactId instead.
     *
     * @return Returns the appName, will never return Null.
     */
    public String getAppname() {

        final String property = getCommandlineProperty(SystemProperties.APP_NAME);

        if (property != null) {
            return property;
        } else if (this.appname == null) {
            return getArtifactId();
        } else {
            return appname;
        }

    }

    /**
     * Returns the buildpack to use, if set. Otherwise Null is returned. If the parameter is set via the command line
     * (aka system property, then that value is used). If not the pom.xml configuration parameter is used, if
     * available.
     *
     * For a list of available properties see {@link SystemProperties}.
     *
     * @return Returns the buildpack or null
     */
    public String getBuildpack() {
        return getStringValue(SystemProperties.BUILDPACK, this.buildpack);
    }

    /**
     * Returns the start command to use, if set. Otherwise Null is returned. If the parameter is set via the command
     * line (aka system property, then that value is used). If not the pom.xml configuration parameter is used, if
     * available.
     *
     * For a list of available properties see {@link SystemProperties}.
     *
     * @return Returns the command or null
     */
    public String getCommand() {
        return getStringValue(SystemProperties.COMMAND, this.command);
    }

    /**
     * Returns the custom domain names that shall be created and added to the application.
     *
     * @return Never null
     */
    public List<String> getCustomDomains() {
        return this.domains == null ? new ArrayList<String>(0) : this.domains;
    }

    /**
     * Returns the diskQuota parameter.
     *
     * @return Returns the configured disk quota choice
     */
    public Integer getDiskQuota() {
        return getIntegerValue(SystemProperties.DISK_QUOTA, this.diskQuota);
    }

    /**
     * Environment properties can only be specified from the maven pom.
     *
     * Example:
     *
     * {code} <env> <JAVA_OPTS>-XX:MaxPermSize=256m</JAVA_OPTS> </env> {code}
     *
     * @return Returns the env, will never return Null.
     */
    public Map<String, String> getEnv() {
        return this.env;
    }

    /**
     * Returns the health check timeout to use, if set. Otherwise Null is returned. If the parameter is set via the
     * command line (aka system property, then that value is used). If not the pom.xml configuration parameter is used,
     * if available.
     *
     * For a list of available properties see {@link SystemProperties}.
     *
     * @return Returns the health check timeout or null
     */
    public Integer getHealthCheckTimeout() {
        return getIntegerValue(SystemProperties.HEALTH_CHECK_TIMEOUT, this.healthCheckTimeout);
    }

    /**
     * Returns the number of instances-parameter, if set. Otherwise Null is returned. If the parameter is set via the
     * command line (aka system property, then that value is used). If not the pom.xml configuration parameter is used,
     * if available.
     *
     * For a list of available properties see {@link SystemProperties}
     *
     * @return Returns the number of configured instance or null
     */
    public Integer getInstances() {
        return getIntegerValue(SystemProperties.INSTANCES, this.instances, DefaultConstants.DEFAULT_INSTANCE);
    }

    /**
     * Returns the memory-parameter.
     *
     * If the parameter is set via the command line (aka system property, then that value is used). If not the pom.xml
     * configuration parameter is used, if available.
     *
     * If the value is not defined, null is returned.  Triggering an empty value to be sent to the Cloud Controller
     * where its default will be used.
     *
     * @return Returns the configured memory choice
     */
    public Integer getMemory() {
        return getIntegerValue(SystemProperties.MEMORY, this.memory);
    }

    /**
     * Specifies the file or directory that shall be pushed to Cloud Foundry.
     *
     * This property defaults to the Maven property "${project.build.directory}/${project.build.finalName}.war"
     *
     * @return null if not found.
     */
    public File getPath() throws MojoExecutionException {
        final String property = getCommandlineProperty(SystemProperties.PATH);

        if (property != null) {
            final File path = new File(property);
            validatePath(path);
            return path;
        } else if (this.path != null) {
            return this.path;
        } else {
            File resolvedArtifact = this.getArtifact();
            if (resolvedArtifact != null) {
                return resolvedArtifact;
            }
            return null;
        }
    }

    /**
     * Returns the services names that shall be bound to the application.
     *
     * @return Never null
     */
    public List<CloudServiceWithUserProvided> getServices() {
        return this.services == null ? new ArrayList<CloudServiceWithUserProvided>(0) : this.services;
    }

    /**
     * Returns the stack to use, if set. Otherwise Null is returned. If the parameter is set via the command line (aka
     * system property, then that value is used). If not the pom.xml configuration parameter is used, if available.
     *
     * For a list of available properties see {@link SystemProperties}.
     *
     * @return Returns the stack or null
     */
    public String getStack() {
        return getStringValue(SystemProperties.STACK, this.stack);
    }

    /**
     * If the URL was specified via the command line ({@link SystemProperties}) then that property is used. Otherwise
     * return the appname.
     *
     * @return Returns the Cloud Foundry application url.
     */
    public String getUrl() {

        final String property = getCommandlineProperty(SystemProperties.URL);

        if (property != null) {
            return property;
        } else {
            return this.url;
        }

    }

    /**
     * Returns the list of urls that shall be associated with the application.
     *
     * @return Never null
     */
    public List<String> getUrls() {
        return this.urls == null ? new ArrayList<String>(0) : this.urls;
    }

    /**
     * If true, merge the application's existing env vars with those set in the plugin configuration when updating an
     * application. If not set, this property defaults to <code>false</code>
     *
     * @return Never null
     */
    public Boolean isMergeEnv() {
        return getBooleanValue(SystemProperties.MERGE_ENV, this.mergeEnv, DefaultConstants.MERGE_ENV);
    }

    /**
     * If true, this property specifies that the application shall not automatically started upon "push". If not set,
     * this property defaults to <code>false</code>
     *
     * @return Never null
     */
    public Boolean isNoStart() {
        return getBooleanValue(SystemProperties.NO_START, this.noStart, DefaultConstants.NO_START);
    }

    /**
     * Adds custom domains when provided in the pom file.
     */
    protected void addDomains() {
        List<CloudDomain> domains = getClient().getDomains();

        List<String> currentDomains = new ArrayList<String>(domains.size());
        for (CloudDomain domain : domains) {
            currentDomains.add(domain.getName());
        }

        for (String domain : getCustomDomains()) {
            if (!currentDomains.contains(domain)) {
                getClient().addDomain(domain);
            }
        }
    }

    protected List<String> getAllUris() throws MojoExecutionException {
        Assert.configurationUrls(getUrl(), getUrls());
        List<String> uris;
        if (getUrl() != null) {
            uris = Arrays.asList(getUrl());
        } else if (!getUrls().isEmpty()) {
            uris = getUrls();
        } else {
            String defaultUri = getAppname() + "." + getClient().getDefaultDomain().getName();
            uris = Arrays.asList(defaultUri);
        }

        return replaceRandomWords(uris);
    }

    protected void showInstancesStatus(List<InstanceInfo> instances, int runningInstances, int expectedInstances) {
        Map<String, Integer> stateCounts = new HashMap<String, Integer>();

        for (InstanceInfo instance : instances) {
            final String state = instance.getState().toString();
            final Integer stateCount = stateCounts.get(state);
            if (stateCount == null) {
                stateCounts.put(state, 1);
            } else {
                stateCounts.put(state, stateCount + 1);
            }
        }

        List<String> stateStrings = new ArrayList<String>();
        for (Map.Entry<String, Integer> stateCount : stateCounts.entrySet()) {
            stateStrings.add(String.format("%s %s", stateCount.getValue(), stateCount.getKey().toLowerCase()));
        }

        getLog().info(String.format("  %d of %d instances running (%s)", runningInstances, expectedInstances,
                CommonUtils.collectionToCommaDelimitedString(stateStrings)));
    }

    protected void showStagingStatus(StartingInfo startingInfo) {
        if (startingInfo != null) {
            responseErrorHandler.addExpectedStatus(HttpStatus.NOT_FOUND);

            int offset = 0;
            String staging = client.getStagingLogs(startingInfo, offset);
            while (staging != null) {
                getLog().info(staging);
                offset += staging.length();
                staging = client.getStagingLogs(startingInfo, offset);
            }

            responseErrorHandler.clearExpectedStatus();
        }
    }

    protected void showStartResults(CloudApplication app, List<String> uris) throws MojoExecutionException {
        List<InstanceInfo> instances = getApplicationInstances(app);

        int expectedInstances = getExpectedInstances(instances);
        int runningInstances = getRunningInstances(instances);
        int flappingInstances = getFlappingInstances(instances);

        if (flappingInstances > 0) {
            throw new MojoExecutionException("Application start unsuccessful");
        } else if (runningInstances == 0) {
            throw new MojoExecutionException("Application start timed out");
        } else if (runningInstances > 0) {
            if (uris.isEmpty()) {
                getLog().info(String.format("Application '%s' is available", app.getName()));
            } else {
                getLog().info(String.format("Application '%s' is available at '%s'",
                        app.getName(), CommonUtils.collectionToCommaDelimitedString(uris, "http://")));
            }
        }
    }

    protected void showStartingStatus(CloudApplication app) {
        getLog().info(String.format("Checking status of application '%s'", getAppname()));

        responseErrorHandler.addExpectedStatus(HttpStatus.BAD_REQUEST);

        long appStartupExpiry = getAppStartupExpiry();

        while (System.currentTimeMillis() < appStartupExpiry) {
            List<InstanceInfo> instances = getApplicationInstances(app);

            if (instances != null) {
                int expectedInstances = getExpectedInstances(instances);
                int runningInstances = getRunningInstances(instances);
                int flappingInstances = getFlappingInstances(instances);

                showInstancesStatus(instances, runningInstances, expectedInstances);

                if (flappingInstances > 0)
                    break;

                if (runningInstances == expectedInstances)
                    break;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // ignore
            }
        }

        responseErrorHandler.clearExpectedStatus();
    }

    /**
     * Executes the actual war deployment to Cloud Foundry.
     *
     * @param client  The Cloud Foundry client to use
     * @param file    The file or directory to upload
     * @param appName The name of the application this file upload is for
     */
    protected void uploadApplication(CloudFoundryClient client, File file, String appName) {

        boolean isDirectory = file.isDirectory();

        if (isDirectory) {
            getLog().debug(String.format("Deploying directory %s to %s.", file.getAbsolutePath(), appName));
        } else {
            getLog().debug(String.format("Deploying file %s (%s Kb) to %s.", file.getAbsolutePath(), file.length() /
                    1024, appName));
        }

        try {
            client.uploadApplication(appName, file);
        } catch (IOException e) {
            throw new IllegalStateException("Error while uploading application.", e);
        }
    }

    /**
     * Validate that the path denoting a directory or file does exists.
     *
     * @param path Must not be null
     */
    protected void validatePath(File path) {

        Assert.notNull(path, "A path could not be found to deploy.  Please specify a path or artifact GAV.");

        final String absolutePath = path.getAbsolutePath();

        if (!path.exists()) {
            throw new IllegalStateException(String.format("The file or directory does not exist at '%s'.",
                    absolutePath));
        }

        if (path.isDirectory() && path.list().length == 0) {
            throw new IllegalStateException(String.format("No files found in directory '%s'.", absolutePath));
        }

    }

    Artifact createArtifactFromGAV() throws MojoExecutionException {
        String[] tokens = StringUtils.split(artifact, ":");
        if (tokens.length < 4 || tokens.length > 5) {
            throw new MojoExecutionException(
                    "Invalid artifact, you must specify groupId:artifactId:version:packaging[:classifier] "
                            + artifact);
        }
        String groupId = tokens[0];
        String artifactId = tokens[1];
        String version = tokens[2];
        String packaging = null;
        if (tokens.length >= 4) {
            packaging = tokens[3];
        }
        String classifier = null;
        if (tokens.length == 5) {
            classifier = tokens[4];
        }
        return (classifier == null
                ? artifactFactory.createBuildArtifact(groupId, artifactId, version, packaging)
                : artifactFactory.createArtifactWithClassifier(groupId, artifactId, version, packaging, classifier));
    }

    private long getAppStartupExpiry() {
        long timeout = System.currentTimeMillis();
        if (getAppStartupTimeout() != null) {
            timeout += minutesToMillis(getAppStartupTimeout());
        } else if (getHealthCheckTimeout() != null) {
            timeout += secondsToMillis(getHealthCheckTimeout());
        } else {
            timeout += minutesToMillis(DEFAULT_APP_STARTUP_TIMEOUT);
        }

        return timeout;
    }

    private List<InstanceInfo> getApplicationInstances(CloudApplication app) {
        InstancesInfo instancesInfo = client.getApplicationInstances(app);
        if (instancesInfo != null) {
            return instancesInfo.getInstances();
        }
        return null;
    }

    /**
     * Provides the File to deploy based on the GAV set in the "artifact" property.
     *
     * @return Returns null of no artifact specified.
     */
    private File getArtifact() throws MojoExecutionException {
        if (artifact != null) {
            Artifact resolvedArtifact = createArtifactFromGAV();

            try {
                artifactResolver.resolve(resolvedArtifact, remoteRepositories, localRepository);
            } catch (ArtifactNotFoundException ex) {
                throw new MojoExecutionException("Could not find deploy artifact [" + artifact + "]", ex);
            } catch (ArtifactResolutionException ex) {
                throw new MojoExecutionException("Could not resolve deploy artifact [" + artifact + "]", ex);
            }
            return resolvedArtifact.getFile();
        }
        return null;
    }

    private Boolean getBooleanValue(SystemProperties propertyName, Boolean configValue, Boolean defaultValue) {
        final String property = getCommandlineProperty(propertyName);

        if (property != null) {
            return Boolean.valueOf(property);
        }

        if (configValue != null) {
            return configValue;
        }

        return defaultValue;
    }

    private int getExpectedInstances(List<InstanceInfo> instances) {
        return instances == null ? 0 : instances.size();
    }

    private int getFlappingInstances(List<InstanceInfo> instances) {
        return getInstanceCount(instances, InstanceState.FLAPPING);
    }

    private int getInstanceCount(List<InstanceInfo> instances, InstanceState state) {
        int count = 0;
        if (instances != null) {
            for (InstanceInfo instance : instances) {
                if (instance.getState().equals(state)) {
                    count++;
                }
            }
        }
        return count;
    }

    private Integer getIntegerValue(SystemProperties propertyName, Integer configValue) {
        final String property = getCommandlineProperty(propertyName);
        return property != null ? Integer.valueOf(property) : configValue;
    }

    private Integer getIntegerValue(SystemProperties propertyName, Integer configValue, Integer defaultValue) {
        final Integer value = getIntegerValue(propertyName, configValue);
        return value != null ? value : defaultValue;
    }

    private int getRunningInstances(List<InstanceInfo> instances) {
        return getInstanceCount(instances, InstanceState.RUNNING);
    }

    private String getStringValue(SystemProperties propertyName, String configValue) {
        final String property = getCommandlineProperty(propertyName);
        return property != null ? property : configValue;
    }

    private long minutesToMillis(Integer duration) {
        return TimeUnit.MINUTES.toMillis(duration);
    }

    private List<String> replaceRandomWords(List<String> uris) {
        List<String> finalUris = new ArrayList<String>(uris.size());
        for (String uri : uris) {
            if (uri.contains("${randomWord}")) {
                finalUris.add(uri.replace("${randomWord}", RandomStringUtils.randomAlphabetic(5)));
            } else {
                finalUris.add(uri);
            }
        }
        return finalUris;
    }

    private long secondsToMillis(Integer duration) {
        return TimeUnit.SECONDS.toMillis(duration);
    }
}
