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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.CloudInfo;
import org.cloudfoundry.client.lib.CloudService;
import org.cloudfoundry.client.lib.Staging;
import org.cloudfoundry.maven.common.Assert;
import org.cloudfoundry.maven.common.CommonUtils;
import org.springframework.http.HttpStatus;

/**
 * Push and optionally start an application.
 *
 * @author Gunnar Hillert
 * @author Stephan Oudmaijer
 *
 * @since 1.0.0
 *
 * @goal push
 *
 * @execute phase="package"
 *
 */
public class Push extends AbstractApplicationAwareCloudFoundryMojo {

	@Override
	protected void doExecute() throws MojoExecutionException {

		//Assert.configurationNotNull(this.getUrl(), "url", SystemProperties.URL);

		final java.util.List<String> uris = new ArrayList<String>(1);

		if (this.getUrl() != null) {
			uris.add(this.getUrl());
		}

		final String appname = this.getAppname();
		final String command = this.getCommand();
		final Map<String,String> env = this.getEnv();
		final String framework = this.getFramework();
		final Integer instances = this.getInstances();
		final Integer memory = this.getMemory();
		final File path = this.getPath();
		final String runtime = this.getRuntime();

		List<CloudService> nonServices = super.getNonCreatedServices();

		for (CloudService service: nonServices) {
			Assert.configurationServiceNotNull(service, null);
			try {
				super.getClient().createService(service);
				super.getLog().info(String.format("Creating Service '%s': OK", service.getName()));
			} catch (CloudFoundryException e) {
				throw new MojoExecutionException(String.format("Not able to create service '%s'.", service.getName()));
			}
		}

		final List<CloudService> services = this.getServices();
		List<String> serviceNames = new ArrayList<String>();

		for (CloudService service : services) {
			serviceNames.add(service.getName());
		}

		super.getLog().debug(String.format(
				"Pushing App - Appname: %s," +
				             " Command: %s," +
				                 " Env: %s," +
				           " Framework: %s," +
				           " Instances: %s," +
				              " Memory: %s," +
				                " Path: %s," +
					         " Runtime: %s," +
				            " Services: %s," +
				                " Uris: %s,",

			appname, command, env, framework, instances, memory, path, runtime, serviceNames, uris));

		super.getLog().debug("Create Application...");

		validateMemoryChoice(this.getClient(), memory);
		validateFrameworkChoice(this.getClient().getCloudInfo().getFrameworks(), framework);

		boolean found = true;

		try {
			this.getClient().getApplication(appname);
		} catch (CloudFoundryException e) {
			if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
				found = false;
			} else {
				throw new MojoExecutionException(String.format("Error while checking for existing application '%s'. Error message: '%s'. Description: '%s'",
						appname, e.getMessage(), e.getDescription()), e);
			}

		}

		if (found) {
			throw new MojoExecutionException(
					String.format("The application '%s' is already deployed.", appname));
		}

		try {
			final Staging staging = new Staging(framework);

			staging.setCommand(command);
			staging.setRuntime(runtime);

			this.getClient().createApplication(appname, staging, memory, uris, serviceNames);
		} catch (CloudFoundryException e) {
			throw new MojoExecutionException(String.format("Error while creating application '%s'. Error message: '%s'. Description: '%s'",
					this.getAppname(), e.getMessage(), e.getDescription()), e);
		}

		super.getLog().debug("Updating Application env...");

		try {
			this.getClient().updateApplicationEnv(appname, env);
		} catch (CloudFoundryException e) {
			throw new MojoExecutionException(String.format("Error while updating application env '%s'. Error message: '%s'. Description: '%s'",
					this.getAppname(), e.getMessage(), e.getDescription()), e);
		}

		super.getLog().debug("Deploy Application...");

		validatePath(path);

		try {
			uploadApplication(this.getClient(), path, appname);
		} catch (CloudFoundryException e) {
			throw new MojoExecutionException(String.format("Error while creating application '%s'. Error message: '%s'. Description: '%s'",
					this.getAppname(), e.getMessage(), e.getDescription()), e);
		}

		if (instances != null) {
			super.getLog().debug("Set the number of instances to " + instances);

			try {
				this.getClient().updateApplicationInstances(appname, instances);
			} catch (CloudFoundryException e) {
				throw new MojoExecutionException(String.format("Error while setting number of instances for application '%s'. Error message: '%s'. Description: '%s'",
						this.getAppname(), e.getMessage(), e.getDescription()), e);
			}
		}

		if (!isNoStart()) {

			super.getLog().debug("Start Application..." + appname);

			try {
				this.getClient().startApplication(appname);
			} catch (CloudFoundryException e) {
				throw new MojoExecutionException(String.format("Error while creating application '%s'. Error message: '%s'. Description: '%s'",
						this.getAppname(), e.getMessage(), e.getDescription()), e);
			}

		} else {
			super.getLog().debug("Not Starting Application.");
		}

		if (this.getUrl() != null) {
			super.getLog().info(String.format("'%s' was successfully deployed to: '%s'.", appname, this.getUrl()));
		} else {
			super.getLog().info(String.format("'%s' was successfully deployed.", appname, this.getUrl()));
		}

	}

	/**
	 * Helper method that validates that the memory size selected is valid and available.
	 *
	 * @param cloudFoundryClient
	 * @param desiredMemory
	 *
	 * @throws IllegalStateException if memory constraints are violated.
	 */
	protected void validateMemoryChoice(CloudFoundryClient cloudFoundryClient, Integer desiredMemory) {
		int[] memoryChoices = cloudFoundryClient.getApplicationMemoryChoices();
		validateMemoryChoice(memoryChoices, desiredMemory);
	}

	/**
	 * Helper method that validates that the memory size selected is valid and available.
	 *
	 * @param desiredMemory
	 * @throws IllegalStateException if memory constraints are violated.
	 */
	protected void validateMemoryChoice(int[] availableMemoryChoices, Integer desiredMemory) {

		boolean match = false;
		List<String> memoryChoicesAsString = new ArrayList<String>();
		for (int i : availableMemoryChoices) {
			if (Integer.valueOf(i).equals(desiredMemory)) {
				match = true;
			}
			memoryChoicesAsString.add(String.valueOf(i));
		}

		if (!match) {
			throw new IllegalStateException("Memory must be one of the following values: " +
					  CommonUtils.collectionToCommaDelimitedString(memoryChoicesAsString));
		}

	}

	/**
	 *
	 * @param frameworks
	 * @param desiredFramework
	 * @return true if valid
	 */
	protected boolean validateFrameworkChoice(Collection<CloudInfo.Framework> frameworks, String desiredFramework) {

		if( frameworks != null && !frameworks.isEmpty() ) {
			for(CloudInfo.Framework f : frameworks ) {
				if(f.getName().equals(desiredFramework)) {
					return true;
				}
			}
		}
		throw new IllegalStateException("Framework must be one of the following values: " +
					  CommonUtils.frameworksToCommaDelimitedString(frameworks));
	}

}
