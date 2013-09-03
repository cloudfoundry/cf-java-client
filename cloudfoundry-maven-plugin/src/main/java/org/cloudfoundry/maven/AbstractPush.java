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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;

import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.CloudFoundryException;

import org.cloudfoundry.client.lib.StartingInfo;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.Staging;

import org.cloudfoundry.maven.common.Assert;
import org.cloudfoundry.maven.common.CommonUtils;

import org.springframework.http.HttpStatus;

/**
 * Push and optionally start an application.
 *
 * @author Gunnar Hillert
 * @author Stephan Oudmaijer
 * @author Ali Moghadam
 * @author Scott Frederick
 * @since 1.0.0
 */
public class AbstractPush extends AbstractApplicationAwareCloudFoundryMojo {

	@Override
	protected void doExecute() throws MojoExecutionException {
		final String appname = getAppname();
		final String command = getCommand();
		final String buildpack = getBuildpack();
		final Map<String, String> env = getEnv();
		final Integer instances = getInstances();
		final Integer memory = getMemory();
		final File path = getPath();
		final List<String> uris = getAllUris();
		final List<String> serviceNames = getServiceNames();

		validateMemoryChoice(getClient(), memory);
		validatePath(path);

		addDomains();

		createServices();

		getLog().debug(String.format(
				"Pushing App - Appname: %s," +
						" Command: %s," +
						" Env: %s," +
						" Instances: %s," +
						" Memory: %s," +
						" Path: %s," +
						" Services: %s," +
						" Uris: %s,",
				appname, command, env, instances, memory, path, serviceNames, uris));

		getLog().info(String.format("Creating application '%s'", appname));

		createApplication(appname, command, buildpack, memory, uris, serviceNames);

		getLog().debug("Updating application env...");

		try {
			getClient().updateApplicationEnv(appname, env);
		} catch (CloudFoundryException e) {
			throw new MojoExecutionException(String.format("Error while updating application env '%s'. Error message: '%s'. Description: '%s'",
					getAppname(), e.getMessage(), e.getDescription()), e);
		}

		getLog().info(String.format("Uploading '%s'", path));

		try {
			uploadApplication(getClient(), path, appname);
		} catch (CloudFoundryException e) {
			throw new MojoExecutionException(String.format("Error while creating application '%s'. Error message: '%s'. Description: '%s'",
					getAppname(), e.getMessage(), e.getDescription()), e);
		}

		if (instances != null) {
			getLog().debug("Setting the number of instances to " + instances);

			try {
				getClient().updateApplicationInstances(appname, instances);
			} catch (CloudFoundryException e) {
				throw new MojoExecutionException(String.format("Error while setting number of instances for application '%s'. Error message: '%s'. Description: '%s'",
						getAppname(), e.getMessage(), e.getDescription()), e);
			}
		}

		if (!isNoStart()) {
			getLog().info("Starting application");

			try {
				final StartingInfo startingInfo = getClient().startApplication(appname);
				showStagingStatus(startingInfo);

				final CloudApplication app = getClient().getApplication(appname);
				showStartingStatus(app);
				showStartResults(app, uris);
			} catch (CloudFoundryException e) {
				throw new MojoExecutionException(String.format("Error while creating application '%s'. Error message: '%s'. Description: '%s'",
						getAppname(), e.getMessage(), e.getDescription()), e);
			}
		}
	}

	private void createApplication(String appname, String command, String buildpack,
								   Integer memory, List<String> uris, List<String> serviceNames) throws MojoExecutionException {
		boolean found;
		try {
			getClient().getApplication(appname);
			found = true;
		} catch (CloudFoundryException e) {
			if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
				found = false;
			} else {
				throw new MojoExecutionException(String.format("Error while checking for existing application '%s'. Error message: '%s'. Description: '%s'",
						appname, e.getMessage(), e.getDescription()), e);
			}
		}

		try {
			final Staging staging = new Staging(command, buildpack);
			if (!found) {
				getClient().createApplication(appname, staging, memory, uris, serviceNames);
			} else {
				client.stopApplication(appname);
				client.updateApplicationStaging(appname, staging);
				client.updateApplicationMemory(appname, memory);
				client.updateApplicationUris(appname, uris);
				client.updateApplicationServices(appname, serviceNames);
			}
		} catch (CloudFoundryException e) {
			throw new MojoExecutionException(String.format("Error while creating application '%s'. Error message: '%s'. Description: '%s'",
					getAppname(), e.getMessage(), e.getDescription()), e);
		}
	}

	private List<String> getServiceNames() {
		final List<CloudService> services = getServices();
		List<String> serviceNames = new ArrayList<String>();

		for (CloudService service : services) {
			serviceNames.add(service.getName());
		}
		return serviceNames;
	}

	/**
	 * Helper method that validates that the memory size selected is valid and available.
	 *
	 * @param cloudFoundryClient
	 * @param desiredMemory
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
}
