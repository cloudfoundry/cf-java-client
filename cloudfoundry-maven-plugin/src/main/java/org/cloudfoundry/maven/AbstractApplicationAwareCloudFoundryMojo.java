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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.MojoExecutionException;
import org.cloudfoundry.client.lib.StartingInfo;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.InstanceInfo;
import org.cloudfoundry.client.lib.domain.InstanceState;
import org.cloudfoundry.client.lib.domain.InstancesInfo;
import org.cloudfoundry.maven.common.Assert;
import org.cloudfoundry.client.lib.CloudFoundryClient;

import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.domain.CloudDomain;
import org.cloudfoundry.client.lib.domain.CloudService;

import org.cloudfoundry.maven.common.CommonUtils;
import org.cloudfoundry.maven.common.DefaultConstants;
import org.cloudfoundry.maven.common.SystemProperties;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.http.HttpStatus;

/**
 * Abstract goal for the Cloud Foundry Maven plugin that bundles access to commonly
 * used plugin parameters.
 *
 * @author Gunnar Hillert
 * @author Stephan Oudmaijer
 * @author Ali Moghadam
 * @author Scott Frederick
 *
 * @since 1.0.0
 *
 */
@SuppressWarnings("UnusedDeclaration")
abstract class AbstractApplicationAwareCloudFoundryMojo extends AbstractCloudFoundryMojo {
	private static final int MAX_STATUS_CHECKS = 60;

	/**
	 * @parameter expression="${cf.appname}"
	 */
	private String appname;

	/**
	 * @parameter expression="${cf.url}"
	 */
	private String url;

	/**
	 * @parameter expression="${urls}"
	 */
	private List<String> urls;

	/**
	 * A string of the form groupId:artifactId:version:packaging[:classifier].
	 * @parameter expression = "${cf.artifact}" default-value="${project.groupId}:${project.artifactId}:${project.version}:${project.packaging}"
	 */
	private String artifact;

	/**
	 * The path of one of the following:
	 *
	 * <ul>
	 * 		<li>War file to deploy</li>
	 * 		<li>Zip or Jar file to deploy</li>
	 * 		<li>Exploded War directory</li>
	 * 		<li>Directory containing a stand-alone application to deploy</li>
	 * </ul>
	 *
	 * @parameter expression = "${cf.path}"
	 */
	private File path;

	/**
	 * The buidpack to use for the application.
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
	 * Set the memory reservation for the application
	 *
	 * @parameter expression="${cf.memory}"
	 */
	private Integer memory;

	/**
	 * Set the expected number <N> of instances
	 *
	 * @parameter expression="${cf.instances}"
	 */
	private Integer instances;

	/**
	 * list of services to use by the application.
	 *
	 * @parameter expression="${services}"
	 */
	private List<CloudService> services;

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
	 * Do not auto-start the application
	 *
	 * @parameter expression="${cf.no-start}"
	 */
	private Boolean noStart;

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
	* @component
	*/
	private ArtifactFactory artifactFactory;

	/**
	* @component
	*/
	private ArtifactResolver artifactResolver;

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	/**
	 * If the application name was specified via the command line ({@link SystemProperties})
	 * then use that property. Otherwise return the appname as injected via Maven or
	 * if appname is Null return the artifactId instead.
	 *
	 * @return Returns the appName, will never return Null.
	 */
	public String getAppname() {

		final String appnameProperty = getCommandlineProperty(SystemProperties.APP_NAME);

		if (appnameProperty != null) {
			return appnameProperty;
		}

		if (this.appname == null) {
			return getArtifactId();
		} else {
			return appname;
		}

	}

	/**
	 * Environment properties can only be specified from the maven pom.
	 *
	 * Example:
	 *
	 * {code}
	 * <env>
	 *     <JAVA_OPTS>-XX:MaxPermSize=256m</JAVA_OPTS>
	 * </env>
	 * {code}
	 *
	 * @return Returns the env, will never return Null.
	 */
	public Map<String,String> getEnv() {
		return this.env;
	}

	/**
	 * If the application name was specified via the command line ({@link SystemProperties})
	 * then that property is used. Otherwise return the appname.
	 *
	 * @return Returns the Cloud Foundry application url. Returns null in case
	 * the target url cannot be used to determine a suitable default.
	 */
	public String getUrl() {

		final String urlProperty = getCommandlineProperty(SystemProperties.URL);

		if (urlProperty != null) {
			return urlProperty;
		}

		if (this.url == null && this.urls == null) {

			if (getTarget() != null) {

				final URI targetUri = getTarget();
				final String[] tokenizedTarget = targetUri.getSchemeSpecificPart().split("\\.");

				if (tokenizedTarget.length >=2) {

					String domain = tokenizedTarget[tokenizedTarget.length-2];

					if (domain.startsWith("//")) {
						domain = domain.substring(2);
					}

					return getAppname() + "." + domain
											 + "." + tokenizedTarget[tokenizedTarget.length-1];
				} else {
					getLog().warn(String.format("Unable to derive a suitable " +
													 "Url from the provided Target Url '%s'", targetUri.toString()));
					return null;
				}

			} else {
				return getAppname() + "." + "<undefined target>";
			}

		} else {
			return this.url;
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
			throw new IllegalStateException(String.format("The file or directory does not exist at '%s'.", absolutePath));
		}

		if (path.isDirectory() && path.list().length == 0) {
			throw new IllegalStateException(String.format("No files found in directory '%s'.", absolutePath));
		}

	}

	/**
	 * Returns the memory-parameter.
	 *
	 * If the parameter is set via the command line (aka system property, then
	 * that value is used). If not the pom.xml configuration parameter is used,
	 * if available.
	 *
	 * If the value is not defined, 512 (MB) is returned as default.
	 *
	 * @return Returns the configured memory choice
	 */
	public Integer getMemory() {

		final String urlProperty = getCommandlineProperty(SystemProperties.MEMORY);

		if (urlProperty != null) {
			return Integer.valueOf(urlProperty);
		}

		if (this.memory == null) {
			return DefaultConstants.MEMORY;
		} else {
			return this.memory;
		}

	}

	/**
	 * Specifies the file or directory that shall be pushed to Cloud Foundry.
	 *
	 * This property defaults to the Maven property
	 * "${project.build.directory}/${project.build.finalName}.war"
	 *
	 *
	 * @return null if not found.
	 */
	public File getPath() throws MojoExecutionException {

		final String pathProperty = getCommandlineProperty(SystemProperties.PATH);

		if (pathProperty != null) {
			final File path = new File(pathProperty);

			validatePath(path);
			return path;
		}

		if (this.path != null) {
			return this.path;
		}
		File resolvedArtifact = this.getArtifact();
		if (resolvedArtifact != null) {
			return resolvedArtifact;
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
			    artifactResolver.resolve(resolvedArtifact, remoteRepositories, localRepository );
			} catch (ArtifactNotFoundException ex) {
			    throw new MojoExecutionException("Could not find deploy artifact ["+artifact+"]", ex);
			} catch (ArtifactResolutionException ex) {
			    throw new MojoExecutionException("Could not resolve deploy artifact ["+artifact+"]", ex);
			}
			return resolvedArtifact.getFile();
		}
		return null;
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
			? artifactFactory.createBuildArtifact( groupId, artifactId, version, packaging )
			: artifactFactory.createArtifactWithClassifier( groupId, artifactId, version, packaging, classifier));
	}

	/**
	 * Returns the start command to use, if set. Otherwise Null is returned.
	 * If the parameter is set via the command line (aka system property, then
	 * that value is used). If not the pom.xml configuration parameter is used,
	 * if available.
	 *
	 * For a list of available properties see {@link SystemProperties}.
	 *
	 * @return Returns the command or null
	 */
	public String getCommand() {

		final String commandProperty = getCommandlineProperty(SystemProperties.COMMAND);

		if (commandProperty != null) {
			return commandProperty;
		}

		return this.command;

	}

	/**
	 * Returns the buildpack to use, if set. Otherwise Null is returned.
	 * If the parameter is set via the command line (aka system property, then
	 * that value is used). If not the pom.xml configuration parameter is used,
	 * if available.
	 *
	 * For a list of available properties see {@link SystemProperties}.
	 *
	 * @return Returns the buildpack or null
	 */
	public String getBuildpack() {

		final String buildpackProperty = getCommandlineProperty(SystemProperties.BUILDPACK);

		if (buildpackProperty != null) {
			return buildpackProperty;
		}

		return this.buildpack;

	}

	/**
	 * Returns the number of instances-parameter, if set. Otherwise Null is returned.
	 * If the parameter is set via the command line (aka system property, then
	 * that value is used). If not the pom.xml configuration parameter is used,
	 * if available.
	 *
	 * For a list of available properties see {@link SystemProperties}
	 *
	 * @return Returns the number of configured instance or null
	 */
	public Integer getInstances() {

		final String instancesProperty = getCommandlineProperty(SystemProperties.INSTANCES);

		if (instancesProperty != null) {
			return Integer.valueOf(instancesProperty);
		}

		if (this.instances == null) {
			return DefaultConstants.DEFAULT_INSTANCE;
		} else {
			return this.instances;
		}

	}

	/**
	 * Returns the services names that shall be bound to the application.
	 *
	 * @return Never null
	 */
	public List<CloudService> getServices() {
		if (this.services == null) {
			return new ArrayList<CloudService>(0);
		} else {
			return this.services;
		}
	}

	/**
	 * Returns the custom domain names that shall be created and added to the application.
	 *
	 * @return Never null
	 */
	public List<String> getCustomDomains() {
		if (this.domains == null) {
			return new ArrayList<String>(0);
		} else {
			return this.domains;
		}
	}

	/**
	 * Returns the list of urls that shall be associated with the application.
	 *
	 * @return Never null
	 */
	public List<String> getUrls() {
		if (this.urls == null) {
			return new ArrayList<String>(0);
		} else {
			return this.urls;
		}
	}

	/**
	 * If true, this property specifies that the application shall not automatically
	 * started upon "push". If not set, this property defaults to <code>false</code>
	 *
	 * @return Never null
	 */
	public Boolean isNoStart() {
		final String urlProperty = getCommandlineProperty(SystemProperties.NO_START);

		if (urlProperty != null) {
			return Boolean.valueOf(urlProperty);
		}

		if (this.noStart == null) {
			return DefaultConstants.NO_START;
		} else {
			return this.noStart;
		}
	}

	public void createServices() throws MojoExecutionException {
		List<CloudService> currentServices = getClient().getServices();
		List<String> currentServicesNames = new ArrayList<String>(currentServices.size());

		for (CloudService currentService : currentServices) {
			currentServicesNames.add(currentService.getName());
		}

		for (CloudService service: getServices()) {
			if (currentServicesNames.contains(service.getName())) {
				getLog().debug(String.format("Service '%s' already exists", service.getName()));
			}
			else {
				getLog().info(String.format("Creating Service '%s'", service.getName()));
				Assert.configurationServiceNotNull(service, null);

				try {
					client.createService(service);
				} catch (CloudFoundryException e) {
					throw new MojoExecutionException(String.format("Not able to create service '%s'.", service.getName()));
				}
			}
		}
	}

	/**
	 * Executes the actual war deployment to Cloud Foundry.
	 *
	 * @param client The Cloud Foundry client to use
	 * @param file The file or directory to upload
	 * @param appName The name of the application this file upload is for
	 */
	protected void uploadApplication(CloudFoundryClient client, File file, String appName) {

		boolean isDirectory = file.isDirectory();

		if (isDirectory) {
			getLog().debug(String.format("Deploying directory %s to %s.", file.getAbsolutePath(), appName));
		} else {
			getLog().debug(String.format("Deploying file %s (%s Kb) to %s.", file.getAbsolutePath(), file.length() / 1024, appName));
		}

		try {
			client.uploadApplication(appName, file);
		} catch (IOException e) {
			throw new IllegalStateException("Error while uploading application.", e);
		}
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

	protected void showStartingStatus(CloudApplication app) {
		getLog().info(String.format("Checking status of application '%s'", getAppname()));

		responseErrorHandler.addExpectedStatus(HttpStatus.BAD_REQUEST);

		int statusChecks = 0;

		while (statusChecks < MAX_STATUS_CHECKS) {
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

			statusChecks++;

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// ignore
			}
		}

		responseErrorHandler.clearExpectedStatus();
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

	protected void showStartResults(CloudApplication app, List<String> uris) throws MojoExecutionException {
		List<InstanceInfo> instances = getApplicationInstances(app);

		int expectedInstances = getExpectedInstances(instances);
		int runningInstances = getRunningInstances(instances);
		int flappingInstances = getFlappingInstances(instances);

		if (flappingInstances > 0 || runningInstances == 0) {
			throw new MojoExecutionException("Application start unsuccessful");
		} else if (runningInstances > 0) {
			if (uris.isEmpty()) {
				getLog().info(String.format("Application '%s' is available", app.getName()));
			} else {
				getLog().info(String.format("Application '%s' is available at '%s'",
						app.getName(), CommonUtils.collectionToCommaDelimitedString(uris, "http://")));
			}
		}
	}

	/**
	 * Adds custom domains when provided in the pom file.
	 */
	protected void addDomains() {
		List<CloudDomain> domains  = getClient().getDomains();

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

	private List<InstanceInfo> getApplicationInstances(CloudApplication app) {
		InstancesInfo instancesInfo = client.getApplicationInstances(app);
		if (instancesInfo != null) {
			return instancesInfo.getInstances();
		}
		return null;
	}

	private int getExpectedInstances(List<InstanceInfo> instances) {
		return instances == null ? 0 : instances.size();
	}

	private int getRunningInstances(List<InstanceInfo> instances) {
		return getInstanceCount(instances, InstanceState.RUNNING);
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

	protected List<String> getAllUris() throws MojoExecutionException {
		final List<String> uris = new ArrayList<String>(0);

		Assert.configurationUrls(getUrl(), getUrls());

		if (getUrl() != null) {
			uris.add(getUrl());
		} else if (!getUrls().isEmpty()) {
			for (String uri : getUrls()) {
				uris.add(uri);
			}
		}
		return uris;
	}
}
