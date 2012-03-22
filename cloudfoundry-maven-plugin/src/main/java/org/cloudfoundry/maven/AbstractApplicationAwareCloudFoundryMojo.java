/*
 * Copyright 2009-2011 the original author or authors.
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
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudfoundry.client.lib.CloudApplication;
import org.cloudfoundry.maven.common.DefaultConstants;
import org.cloudfoundry.maven.common.SystemProperties;

/**
 * Abstract goal for the Cloud Foundry Maven plugin that bundles access to commonly
 * used plugin parameters.
 *
 * @author Gunnar Hillert
 * @since 1.0.0
 *
 */
abstract class AbstractApplicationAwareCloudFoundryMojo extends
        AbstractCloudFoundryMojo {

    /**
     * @parameter expression="${cf.appname}"
     */
    private String appname;

    /**
     * @parameter expression="${cf.url}"
     */
    private String url;

    /**
     * The path of the WAR file to deploy.
     *
     * @parameter expression = "${project.build.directory}/${project.build.finalName}.war"
     * @required
     */
    private File warfile;

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
     * Comma separated list of services to use by the application.
     *
     * @parameter expression="${cf.services}"
     */
    private String services;


    /**
     * Framework type, defaults to CloudApplication.Spring
     *
     * @parameter expression="${cf.framework}" default-value="spring"
     */
    private String framework;

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
            return this.getArtifactId();
        } else {
            return appname;
        }

    }

    /**
     * If the framework was specified via the command line ({@link SystemProperties})
     * then use that property. Otherwise return the framework as injected via Maven or
     * if framework is Null return the default value (CloudApplication.Spring) instead.
     *
     * @return Returns the framework, will never return Null.
     */
    public String getFramework() {

        final String frameworkProperty = getCommandlineProperty(SystemProperties.FRAMEWORK);

        if (frameworkProperty != null) {
            return frameworkProperty;
        }

        return this.framework;
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

        if (this.url == null) {

            if (this.getTarget() != null) {

                final URI targetUri = this.getTarget();
                final String[] tokenizedTarget = targetUri.getSchemeSpecificPart().split("\\.");

                if (tokenizedTarget.length >=2) {

                    String domain = tokenizedTarget[tokenizedTarget.length-2];

                    if (domain.startsWith("//")) {
                        domain = domain.substring(2);
                    }

                    return this.getAppname() + "." + domain
                                             + "." + tokenizedTarget[tokenizedTarget.length-1];
                } else {
                    this.getLog().warn(String.format("Unable to derive a suitable " +
                                                     "Url from the provided Target Url '%s'", targetUri.toString()));
                    return null;
                }

            } else {
                return this.getAppname() + "." + "<undefined target>";
            }

        } else {
            return this.url;
        }
    }

    /**
     * Validate that the warfile exists and that it is a file.
     *
     * @param warFile
     */
    protected void validateWarFile(File warFile) {

        if (!warFile.exists()) {
            throw new IllegalStateException(String.format("The warfile does not exist at '%s'.", warFile.getAbsolutePath()));
        }

        if (!warFile.isFile()) {
            throw new IllegalStateException(String.format("The warfile at '%s' is not a file.", warFile.getAbsolutePath()));
        }

    }

    /**
     * Returns the warfile-parameter. The parameter will typically return the
     * resolved Maven expression: ${project.build.directory}/${project.build.finalName}.war
     *
     * If the parameter is set via the command line (aka system property, then
     * that value is used). If not the pom.xml configuration parameter is used,
     * if available.
     *
     * Null is never returned.
     *
     * For a list of available properties see {@link SystemProperties}
     *
     * @return Returns the resolved warfile
     * @throws IllegalStateException Thrown if no warfile can be resolved
     */
    public File getWarfile() {

        final String urlProperty = getCommandlineProperty(SystemProperties.WARFILE);

        if (urlProperty != null) {

            File warFile = new File(urlProperty);

            validateWarFile(warFile);
            return warFile;
        }

        if (this.warfile == null) {
            throw new IllegalStateException("Warfile is not defined."); //TODO
        } else {
            return this.warfile;
        }
    }

    /**
     * Returns the memory-parameter.
     *
     * If the parameter is set via the command line (aka system property, then
     * that value is used). If not the pom.xml configuration parameter is used,
     * if available.
     *
     * If the value is not defined the 512 (MB) is returned as default.
     *
     * For a list of available properties see {@link SystemProperties}
     *
     * @return Returns the configured memory choice
     * @throws IllegalStateException Thrown if no warfile can be resolved
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
            return null;
        } else {
            return this.instances;
        }

    }

    /**
     *
     * @return
     */
    public List<String> getServices() {

        final List<String> servicesList = new ArrayList<String>(0);

        final String urlProperty = getCommandlineProperty(SystemProperties.SERVICES);

        if (urlProperty != null) {

            for (String fragment : urlProperty.split(",")) {
                servicesList.add(fragment.trim());
            }

            return servicesList;

        }

        if (this.services == null) {
            return servicesList;
        } else {

            for (String fragment : this.services.split(",")) {
                servicesList.add(fragment.trim());
            }

            return servicesList;
        }

    }

    /**
     *
     * @return
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

}
