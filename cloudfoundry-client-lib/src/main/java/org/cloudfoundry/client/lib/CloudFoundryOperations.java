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

package org.cloudfoundry.client.lib;

import org.cloudfoundry.client.lib.archive.ApplicationArchive;
import org.cloudfoundry.client.lib.domain.ApplicationStats;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudDomain;
import org.cloudfoundry.client.lib.domain.CloudInfo;
import org.cloudfoundry.client.lib.domain.CloudRoute;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.cloudfoundry.client.lib.domain.CrashesInfo;
import org.cloudfoundry.client.lib.domain.InstancesInfo;
import org.cloudfoundry.client.lib.domain.ServiceConfiguration;
import org.cloudfoundry.client.lib.domain.Staging;

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
	 * Does the currently targeted cloud controller support orgs and spaces?
	 */
	boolean supportsSpaces();

	/**
	 * Get list of CloudSpaces for the current cloud.
	 *
	 * @return List of CloudSpace objects containing the space info
	 */
	List<CloudSpace> getSpaces();

	/**
	 * Get list of application plans for the current org.
	 *
	 * @return List of application plans
	 */
	List<String> getApplicationPlans();

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
	 * Update the password for the logged in user using
	 * the username/old_password provided in the credentials.
	 *
	 * @param credentials current credentials
	 * @param newPassword the new password
	 */
	void updatePassword(CloudCredentials credentials, String newPassword);

	/**
	 * Unregister and log out the currently logged in user
	 */
	void unregister();

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
	 * @param applicationPlan the application plan for the deployed app
     * @param buildpackUrl a custom buildpack url (e.g. https://github.com/cloudfoundry/java-buildpack.git) or null to use the default one
	 */
	void createApplication(String appName, Staging staging, int memory, List<String> uris,
                           List<String> serviceNames, String applicationPlan, String buildpackUrl);

	/**
	 * Create application.
	 *
	 * @param appName application name
	 * @param staging staging info
	 * @param memory memory to use in MB
	 * @param uris list of URIs for the app
	 * @param serviceNames list of service names to bind to app
	 * @param applicationPlan the application plan for the deployed app
	 */
	void createApplication(String appName, Staging staging, int memory, List<String> uris,
                           List<String> serviceNames, String applicationPlan);

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
	 * @param staging staging info
	 * @param memory memory to use in MB
	 * @param uris list of URIs for the app
	 * @param serviceNames list of service names to bind to app
	 * @param applicationPlan the application plan for the deployed app
	 * @param checkExists check if app exists before creating it
	 */
	void createApplication(String appName, Staging staging, int memory, List<String> uris,
						   List<String> serviceNames, String applicationPlan, boolean checkExists);

	/**
	 * Create application.
	 *
	 * @param appName application name
	 * @param staging staging info
	 * @param memory memory to use in MB
	 * @param uris list of URIs for the app
	 * @param serviceNames list of service names to bind to app
	 * @param applicationPlan the application plan for the deployed app
	 * @param checkExists check if app exists before creating it
     * @param buildpackUrl a custom buildpack url (e.g. "https://github.com/cloudfoundry/java-buildpack.git") or null to use the default one
	 */
	void createApplication(String appName, Staging staging, int memory, List<String> uris,
                           List<String> serviceNames, String applicationPlan, boolean checkExists, String buildpackUrl);

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
	 * Start application. May return starting info if the response obtained after the start request contains headers.
	 * If the response does not contain headers, null is returned instead.
	 *
	 * @param appName
	 *            name of application
	 * @return Starting info containing response headers, if headers are present in the response. If there are no headers, return null.
	 */
	StartingInfo startApplication(String appName);

	/**
	 * Debug application.
	 *
	 * @param appName name of application
	 * @param mode debug mode info
	 */
	void debugApplication(String appName, CloudApplication.DebugMode mode);

	/**
	 * Stop application.
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
	 * Update application staging information.
	 *
	 * @param appName name of appplication
	 * @param staging staging information for the app
	 */
	void updateApplicationStaging(String appName, Staging staging);

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
	 * Update application plan for the specified app.
	 *
	 * @param appName name of application
	 * @param applicationPlan the plan to use
	 */
	void updateApplicationPlan(String appName, String applicationPlan);

	/**
	 * Get logs from the deployed application. The logs
	 * will be returned in a Map keyed by the path of the log file
	 * (logs/stderr.log, logs/stdout.log).
	 * @param appName name of the application
	 * @return a Map containing the logs. The logs will be returned with the path to the log file used as the key and
	 * the full content of the log file will be returned as a String value for the corresponding key.
	 */
	Map<String, String> getLogs(String appName);

	/**
	 * Get logs from most recent crash of the deployed application. The logs
	 * will be returned in a Map keyed by the path of the log file
	 * (logs/stderr.log, logs/stdout.log).
	 *
	 * @param appName name of the application
	 * @return a Map containing the logs. The logs will be returned with the path to the log file used as the key and
	 * the full content of the log file will be returned as a String value for the corresponding key.
	 */
	Map<String, String> getCrashLogs(String appName);

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
	 * Get a the content, starting at a specific position, of a file from the deployed application.
	 *
	 * @param appName name of the application
	 * @param instanceIndex instance index
	 * @param filePath path to the file
	 * @param startPosition the starting position of the file contents (inclusive)
	 * @return the contents of the file
	 */
	String getFile(String appName, int instanceIndex, String filePath, int startPosition);

	/**
	 * Get a range of content of a file from the deployed application. The range begins at the specified startPosition
	 * and extends to the character at endPosition - 1.
	 *
	 * @param appName name of the application
	 * @param instanceIndex instance index
	 * @param filePath path to the file
	 * @param startPosition the starting position of the file contents (inclusive)
	 * @param endPosition the ending position of the file contents (exclusive)
	 * @return the contents of the file
	 */
	String getFile(String appName, int instanceIndex, String filePath, int startPosition, int endPosition);

	/**
	 * Get a the last bytes, with length as specified, of content of a file from the deployed application.
	 *
	 * @param appName name of the application
	 * @param instanceIndex instance index
	 * @param filePath path to the file
	 * @param length the length of the file contents to retrieve
	 * @return the contents of the file
	 */
	String getFileTail(String appName, int instanceIndex, String filePath, int length);

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

	/**
	 * Get list of all domain registered for the current organization
	 * of this session.
	 *
	 * @return list of domains
	 */
	List<CloudDomain> getDomainsForOrg();

	/**
	 * Get list of all domain registered for the given space.
	 *
	 * @return list of domains
	 */
	List<CloudDomain> getDomains();

	/**
	 * Add domain to the current space of this session. If the domain
	 * doesn't exist for the organization it will be created.
	 *
	 * @param domainName the domain to add
	 */
	void addDomain(String domainName);

	/**
	 * Remove a domain from the space of the current session.
	 *
	 * @param domainName the domain to delete
	 */
	void removeDomain(String domainName);

	/**
	 * Delete a domain registered to the current organization of this session.
	 *
	 * @param domainName the domain to delete
	 */
	void deleteDomain(String domainName);

	/**
	 * Get the info for all routes for a domain belonging to the current space
	 * of this session.
	 *
	 * @param domainName the domain the routes belong to
	 * @return list of routes
	 */
	List<CloudRoute> getRoutes(String domainName);

	/**
	 * Register a new route to the space of the current session.
	 *
	 * @param host the host of the route to register
	 * @param domainName the domain of the route to register
	 */
	void addRoute(String host, String domainName);

	/**
	 * Delete a registered route from the space of the current session.
	 *
	 * @param host the host of the route to delete
	 * @param domainName the domain of the route to delete
	 */
	void deleteRoute(String host, String domainName);

	/**
	 * Update http proxy configuration settings.
	 *
	 * @param httpProxyConfiguration the new configuration settings
	 */
	void updateHttpProxyConfiguration(HttpProxyConfiguration httpProxyConfiguration);

	/**
	 * Register a new RestLogCallback
	 *
	 * @param callBack the callback to be registered
	 */
	void registerRestLogListener(RestLogCallback callBack);

	/**
	 * Un-register a RestLogCallback
	 *
	 * @param callBack the callback to be un-registered
	 */
	void unRegisterRestLogListener(RestLogCallback callBack);
}
