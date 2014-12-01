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

import org.cloudfoundry.client.lib.CloudFoundryException;

import org.cloudfoundry.client.lib.StartingInfo;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.Staging;

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
		final String stack = getStack();
		final Integer healthCheckTimeout = getHealthCheckTimeout();
		final Map<String, String> env = getEnv();
		final Integer instances = getInstances();
		final Integer memory = getMemory();
		final Integer disk = getDiskQuota();
		final File path = getPath();
		final List<String> uris = getAllUris();
		final List<String> serviceNames = getServiceNames();

		validatePath(path);

		addDomains();

		createServices();

		getLog().debug(String.format(
				"Pushing App - Appname: %s," +
						" Command: %s," +
						" Env: %s," +
						" Instances: %s," +
						" Memory: %s," +
						" DiskQuota: %s," +
						" Path: %s," +
						" Services: %s," +
						" Uris: %s,",
				appname, command, env, instances, memory, disk, path, serviceNames, uris));

		getLog().info(String.format("Creating application '%s'", appname));

		createApplication(appname, command, buildpack, stack, healthCheckTimeout, disk, memory, uris, serviceNames, env);

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

	private void createApplication(String appname, String command, String buildpack, String stack, Integer healthCheckTimeout,
	                               Integer diskQuota, Integer memory, List<String> uris, List<String> serviceNames, Map<String, String> env) throws MojoExecutionException {
		CloudApplication application = null;
		try {
			application = client.getApplication(appname);
		} catch (CloudFoundryException e) {
			if (!HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
				throw new MojoExecutionException(String.format("Error while checking for existing application '%s'. Error message: '%s'. Description: '%s'",
						appname, e.getMessage(), e.getDescription()), e);
			}
		}

		try {
			final Staging staging = new Staging(command, buildpack, stack, healthCheckTimeout);
			if (application == null) {
				client.createApplication(appname, staging, diskQuota, memory, uris, serviceNames);
				client.updateApplicationEnv(appname, env);
			} else {
				client.stopApplication(appname);
				client.updateApplicationStaging(appname, staging);
				if (memory != null) {
					client.updateApplicationMemory(appname, memory);
				}
				if (diskQuota != null) {
					client.updateApplicationDiskQuota(appname, diskQuota);
				}
				client.updateApplicationUris(appname, uris);
				client.updateApplicationServices(appname, serviceNames);
				client.updateApplicationEnv(appname, getMergedEnv(application, env));
			}
		} catch (CloudFoundryException e) {
			throw new MojoExecutionException(String.format("Error while creating application '%s'. Error message: '%s'. Description: '%s'",
					getAppname(), e.getMessage(), e.getDescription()), e);
		}
	}

	private Map<String, String> getMergedEnv(CloudApplication application, Map<String, String> env) {
		if (!isMergeEnv()) {
			return env;
		}

		Map<String, String> mergedEnv = application.getEnvAsMap();
		mergedEnv.putAll(env);

		return mergedEnv;
	}

	private List<String> getServiceNames() {
		final List<? extends CloudService> services = getServices();
		List<String> serviceNames = new ArrayList<String>();

		for (CloudService service : services) {
			serviceNames.add(service.getName());
		}
		return serviceNames;
	}
}
