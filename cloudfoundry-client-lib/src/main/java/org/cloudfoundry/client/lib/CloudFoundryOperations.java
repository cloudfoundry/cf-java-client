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

package org.cloudfoundry.client.lib;

import org.cloudfoundry.client.lib.archive.ApplicationArchive;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * The interface defining operations making up the Cloud Foundry Java client's API.
 *
 * @author Ramnivas Laddad
 * @author A.B.Srinivasan
 * @author Jennifer Hickey
 * @author Dave Syer
 * @author Thomas Risberg
 */
@SuppressWarnings("unused")
public interface CloudFoundryOperations {

	/**
	 * Run commands as a different user.  The authenticated user must be
	 * privileged to run as this user.
	 *
	 * @param proxyUser the user to be proxied
	 */
	void setProxyUser(String proxyUser);

	/**
	 * Get the URL used for the cloud controller.
	 *
	 * @return the cloud controller URL
	 */
	URL getCloudControllerUrl();

	/**
	 * Get CloudInfo for the current cloud.
	 *
	 * @return CloudInfo object containing the cloud info
	 */
	CloudInfo getCloudInfo();

	/**
	 * Register new user account with the provided credentials.
	 *
	 * @param email the email account
	 * @param password the password
	 */
	void register(String email, String password);

	/**
	 * Update the password for the logged in user.
	 *
	 * @param newPassword the new password
	 */
	void updatePassword(String newPassword);

	/**
	 * Unregister and log out the currently logged in user
	 */
	void unregister();

	/**
	 * Log in using the provided credentials.
	 *
	 * @param email email
	 * @param password password
	 * @return authentication token
	 */
	String login(String email, String password);

	/**
	 * Login using the credentials already set for the client.
	 *
	 * @return authentication token
	 */
	String login();

	/**
	 * Logout closing the current session.
	 */
	void logout();

	/**
	 * Get all cloud applications.
	 *
	 * @return list of cloud applications
	 */
	List<CloudApplication> getApplications();

	/**
	 * Get cloud application with the specified name.
	 *
	 * @param appName name of the app
	 * @return the cloud application
	 */
	CloudApplication getApplication(String appName);

	/**
	 * Get application stats for the app with the specified name.
	 *
	 * @param appName name of the app
	 * @return the cloud application stats
	 */
	ApplicationStats getApplicationStats(String appName);

	/**
	 * Get choices for application memory quota.
	 *
	 * @return memory choices in MB
	 */
	int[] getApplicationMemoryChoices();

	/**
	 * Get default memory quota for the given framework.
	 *
	 * @param framework name of framework
	 * @return default memory quota in MB
	 */
	int getDefaultApplicationMemory(String framework);

	/**
	 * Create application.
	 *
	 * @param appName application name
	 * @param staging staging info
	 * @param memory memory to use in MB
	 * @param uris list of URIs for the app
	 * @param serviceNames list of service names to bind to app
	 */
	void createApplication(String appName, Staging staging, int memory, List<String> uris,
						   List<String> serviceNames);

	/**
	 * Create application.
	 *
	 * @param appName application name
	 * @param framework name of framework to use
	 * @param memory memory to use in MB
	 * @param uris list of URIs for the app
	 * @param serviceNames list of service names to bind to app
	 */
	void createApplication(String appName, String framework, int memory, List<String> uris,
						   List<String> serviceNames);

	/**
	 * Create application.
	 *
	 * @param appName application name
	 * @param framework name of framework to use
	 * @param memory memory to use in MB
	 * @param uris list of URIs for the app
	 * @param serviceNames list of service names to bind to app
	 * @param checkExists check if app exists before creating it
	 */
	void createApplication(String appName, String framework, int memory, List<String> uris,
						   List<String> serviceNames, boolean checkExists);

	/**
	 * Create application.
	 *
	 * @param appName application name
	 * @param staging staging info
	 * @param memory memory to use in MB
	 * @param uris list of URIs for the app
	 * @param serviceNames list of service names to bind to app
	 * @param checkExists check if app exists before creating it
	 */
	void createApplication(String appName, Staging staging, int memory, List<String> uris,
						   List<String> serviceNames, boolean checkExists);

	/**
	 * Create a service.
	 *
	 * @param service cloud service info
	 */
	void createService(CloudService service);

	/**
	 * Upload an application.
	 *
	 * @param appName application name
	 * @param file path to the application archive or folder
	 * @throws java.io.IOException
	 */
	void uploadApplication(String appName, String file) throws IOException;

	/**
	 * Upload an application to cloud foundry.
	 * @param appName the application name
	 * @param file the application archive or folder
	 * @throws java.io.IOException
	 */
	void uploadApplication(String appName, File file) throws IOException;

	/**
	 * Upload an application to cloud foundry.
	 * @param appName the application name
	 * @param file the application archive
	 * @param callback a callback interface used to provide progress information or <tt>null</tt>
	 * @throws java.io.IOException
	 */
	void uploadApplication(String appName, File file, UploadStatusCallback callback) throws IOException;

	/**
	 * Upload an application to cloud foundry.
	 * @param appName the application name
	 * @param archive the application archive
	 * @throws java.io.IOException
	 */
	void uploadApplication(String appName, ApplicationArchive archive) throws IOException;

	/**
	 * Upload an application to cloud foundry.
	 * @param appName the application name
	 * @param archive the application archive
	 * @param callback a callback interface used to provide progress information or <tt>null</tt>
	 * @throws java.io.IOException
	 */
	void uploadApplication(String appName, ApplicationArchive archive, UploadStatusCallback callback) throws IOException;

	/**
	 * Start appplication.
	 *
	 * @param appName name of application
	 */
	void startApplication(String appName);

	/**
	 * Debug application.
	 *
	 * @param appName name of application
	 * @param mode debug mode info
	 */
	void debugApplication(String appName, CloudApplication.DebugMode mode);

	/**
	 * Stop applicataion.
	 *
	 * @param appName name of application
	 */
	void stopApplication(String appName);

	/**
	 * Restart application.
	 *
	 * @param appName name of application
	 */
	void restartApplication(String appName);

	/**
	 * Delete application.
	 *
	 * @param appName name of application
	 */
	void deleteApplication(String appName);

	/**
	 * Delete all applications.
	 */
	void deleteAllApplications();

	/**
	 * Delete all services.
	 */
	void deleteAllServices();

	/**
	 * Update application memory.
	 *
	 * @param appName name of application
	 * @param memory new memory setting
	 */
	void updateApplicationMemory(String appName, int memory);

	/**
	 * Update application instances.
	 *
	 * @param appName name of application
	 * @param instances number of instances to use
	 */
	void updateApplicationInstances(String appName, int instances);

	/**
	 * Update application services.
	 *
	 * @param appName name of appplication
	 * @param services list of services that should be bound to app
	 */
	void updateApplicationServices(String appName, List<String> services);

	/**
	 * Update application URIs.
	 *
	 * @param appName name of application
	 * @param uris list of URIs the app should use
	 */
	void updateApplicationUris(String appName, List<String> uris);

	/**
	 * Update application env using a map where the key specifies the name of the environment variable
	 * and the value the value of the environment variable..
	 *
	 * @param appName name of application
	 * @param env map of environment settings
	 */
	void updateApplicationEnv(String appName, Map<String, String> env);

	/**
	 * Update application env using a list of strings each with one environment setting.
	 *
	 * @param appName name of application
	 * @param env list of environment settings
	 */
	void updateApplicationEnv(String appName, List<String> env);

	/**
	 * Get file from the deployed application.
	 *
	 * @param appName name of the application
	 * @param instanceIndex instance index
	 * @param filePath path to the file
	 * @return the contents of the file
	 */
	String getFile(String appName, int instanceIndex, String filePath);

	/**
	 * Get list of cloud services.
	 *
	 * @return list of cloud services
	 */
	List<CloudService> getServices();

	/**
	 * Get cloud service.
	 *
	 * @param service name of service
	 * @return the cloud service info
	 */
	CloudService getService(String service);

	/**
	 * Delete cloud service.
	 *
	 * @param service name of service
	 */
	void deleteService(String service);

	/**
	 * Get all service configurations.
	 *
	 * @return list of service configurations
	 */
	List<ServiceConfiguration> getServiceConfigurations();

	/**
	 * Associate (provision) a service with an application.
	 *
	 * @param appName the application name
	 * @param serviceName the service name
	 */
	void bindService(String appName, String serviceName);

	/**
	 * Un-associate (unprovision) a service from an application.
	 * @param appName the application name
	 * @param serviceName the service name
	 */
	void unbindService(String appName, String serviceName);

	/**
	 * Get application instances info for application.
	 *
	 * @param appName name of application.
	 * @return instances info
	 */
	InstancesInfo getApplicationInstances(String appName);

	/**
	 * Get crashes info for application.
	 * @param appName name of application
	 * @return crashes info
	 */
	CrashesInfo getCrashes(String appName);

	/**
	 * Rename an application.
	 *
	 * @param appName the current name
	 * @param newName the new name
	 */
	void rename(String appName, String newName);
}
