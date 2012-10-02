/*

 * Copyright 2009-2012 the original author or authors.
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

import org.cloudfoundry.client.lib.CloudApplication;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.CloudService;
import org.cloudfoundry.maven.common.DefaultConstants;
import org.cloudfoundry.maven.common.SystemProperties;
import org.springframework.util.Assert;

/**
 * Abstract goal for the Cloud Foundry Maven plugin that bundles access to commonly
 * used plugin parameters.
 *
 * @author Gunnar Hillert
 * @author Stephan Oudmaijer
 *
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
	 */
	private File warfile;

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
	 * The start command to use if this app is a standalone app (has framework
	 * named "standalone".
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
	 * Set the runtime for the application, defaults to 'java'.
	 *
	 * @parameter expression="${cf.runtime}"
	 */
	private String runtime;

	/**
	 * Set the expected number <N> of instances
	 *
	 * @parameter expression="${cf.instances}"
	 */
	private Integer instances;

	/**
	 * Comma separated list of services to use by the application.
	 *
	 * @parameter expression="${services}"
	 */
	private List<CloudService> services;

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
			return getArtifactId();
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

		if (this.framework == null) {
			return CloudApplication.SPRING;
		} else {
			return this.framework;
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

		if (this.url == null && !CloudApplication.STANDALONE.equals(getFramework())) {

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

		Assert.notNull(path, "The path must not be null.");

		final String absolutePath = path.getAbsolutePath();

		if (!path.exists()) {
			throw new IllegalStateException(String.format("The file or directory does not exist at '%s'.", absolutePath));
		}

		if (path.isDirectory() && path.list().length == 0) {
			throw new IllegalStateException(String.format("No files found in directory '%s'.", absolutePath));
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
	 * @deprecated Use {@link AbstractApplicationAwareCloudFoundryMojo#getPath()} instead (-Dcf.path)
	 */
	@Deprecated
	public File getWarfile() {
		return getPath();
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
	 * Specifies the file or directory that shall be pushed to Cloud Foundry (or updated).
	 * If your {@link #framework} property resolves to {@link CloudApplication#STANDALONE}
	 * then you must explicitly set the deployment path.
	 *
	 * Otherwise, this property defaults to the Maven property
	 * "${project.build.directory}/${project.build.finalName}.war"
	 *
	 *
	 * @return Never returns null. Will throw an {@IllegalStateException} if not set.
	 */
	public File getPath() {

		final String pathProperty = getCommandlineProperty(SystemProperties.PATH);

		if (pathProperty != null) {

			final File path = new File(pathProperty);

			validatePath(path);
			return path;
		}

		if (this.path == null) {

			if (CloudApplication.STANDALONE.equals(getFramework())) {
				throw new IllegalStateException(
						String.format("The selected framework is '%s'. Please specify the 'path' property.",
								CloudApplication.STANDALONE));
			}

			return this.warfile;

		} else {
			return this.path;
		}

	}

	/**
	 * This property returns the specified runtime for the application. If not
	 * set, this property defaults to "java" as defined by {@link DefaultConstants#RUNTIME}.
	 *
	 * @return Never null
	 *
	 * @see SystemProperties
	 */
	public String getRuntime() {

		final String runtimeProperty = getCommandlineProperty(SystemProperties.RUNTIME);

		if (runtimeProperty != null) {
			return runtimeProperty;
		}

		if (this.runtime == null) {
			return DefaultConstants.RUNTIME;
		} else {
			return this.runtime;
		}

	}

	/**
	 * Returns the start command to use, if set. Otherwise Null is returned.
	 * If the parameter is set via the command line (aka system property, then
	 * that value is used). If not the pom.xml configuration parameter is used,
	 * if available.
	 *
	 * For a list of available properties see {@link SystemProperties}.
	 *
	 * @return Returns the number of configured instance or null
	 */
	public String getCommand() {

		final String commandProperty = getCommandlineProperty(SystemProperties.COMMAND);

		if (commandProperty != null) {
			return commandProperty;
		}

		if (this.command == null) {

			if (CloudApplication.STANDALONE.equals(getFramework())) {
				throw new IllegalStateException(
						String.format("The selected framework is '%s'. Please specify the 'command' property.",
								CloudApplication.STANDALONE));
			}

			return null;

		} else {
			return this.command;
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
		final List<CloudService> servicesList = new ArrayList<CloudService>(0);

		if (this.services == null ) {
			return servicesList;
		} else {
			return this.services;
		}
	}

	/**
	 * Returns a list of services which have not yet been created
	 *
	 * @return List of non created services
	 */
	public List<CloudService> getNonCreatedServices() {
		List<CloudService> currentServices = getClient().getServices();
		List<String>currentServicesNames = new ArrayList<String>();
		List<CloudService> returnServices = new ArrayList<CloudService>(0);

		for (CloudService currentService : currentServices) {
			currentServicesNames.add(currentService.getName());
		}

		for (CloudService service: getServices()) {
			if (!currentServicesNames.contains(service.getName())) {
				returnServices.add(service);
			}
		}

		return returnServices;
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

	/**
	 * Executes the actual war deployment to Cloud Foundry.
	 *
	 * @param client The Cloud Foundry client to use
	 * @param fiel The file or directory to upload
	 * @param appName The name of the application this warfile upload is for
	 */
	protected void uploadApplication(CloudFoundryClient client, File file, String appName) {

		boolean isDirectory = file.isDirectory();

		if (isDirectory) {
			getLog().info(String.format("Deploying directory %s to %s.", file.getAbsolutePath(), appName));
		} else {
			getLog().info(String.format("Deploying file %s (%s Kb) to %s.", file.getAbsolutePath(), file.length() / 1024, appName));
		}

		try {
			client.uploadApplication(appName, file);
		} catch (IOException e) {
			throw new IllegalStateException("Error while uploading application.", e);
		}

	}

}
