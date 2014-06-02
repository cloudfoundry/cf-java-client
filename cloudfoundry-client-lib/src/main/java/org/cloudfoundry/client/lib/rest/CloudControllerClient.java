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

package org.cloudfoundry.client.lib.rest;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.cloudfoundry.client.lib.ApplicationLogListener;
import org.cloudfoundry.client.lib.ClientHttpResponseCallback;
import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.RestLogCallback;
import org.cloudfoundry.client.lib.StartingInfo;
import org.cloudfoundry.client.lib.StreamingLogToken;
import org.cloudfoundry.client.lib.UploadStatusCallback;
import org.cloudfoundry.client.lib.archive.ApplicationArchive;
import org.cloudfoundry.client.lib.domain.ApplicationLog;
import org.cloudfoundry.client.lib.domain.ApplicationStats;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudDomain;
import org.cloudfoundry.client.lib.domain.CloudInfo;
import org.cloudfoundry.client.lib.domain.CloudOrganization;
import org.cloudfoundry.client.lib.domain.CloudRoute;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.CloudServiceOffering;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.cloudfoundry.client.lib.domain.CloudStack;
import org.cloudfoundry.client.lib.domain.CrashesInfo;
import org.cloudfoundry.client.lib.domain.InstancesInfo;
import org.cloudfoundry.client.lib.domain.Staging;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.client.ResponseErrorHandler;

/**
 * Interface defining operations available for the cloud controller REST client implementations
 *
 * @author Thomas Risberg
 */
public interface CloudControllerClient {

	// User and Info methods

	void setResponseErrorHandler(ResponseErrorHandler errorHandler);

	URL getCloudControllerUrl();

	CloudInfo getInfo();

	List<CloudSpace> getSpaces();

	List<CloudOrganization> getOrganizations();

	OAuth2AccessToken login();

	void logout();

	void register(String email, String password);

	void updatePassword(String newPassword);

	void updatePassword(CloudCredentials credentials, String newPassword);

	void unregister();

	// Service methods

	List<CloudService> getServices();

	void createService(CloudService service);

	void createUserProvidedService(CloudService service, Map<String, Object> credentials);

	CloudService getService(String service);

	void deleteService(String service);

	void deleteAllServices();

	List<CloudServiceOffering> getServiceOfferings();

	// App methods

	List<CloudApplication> getApplications();

	CloudApplication getApplication(String appName);
	
	CloudApplication getApplication(UUID appGuid);

	ApplicationStats getApplicationStats(String appName);

	void createApplication(String appName, Staging staging, Integer memory, List<String> uris,
	                       List<String> serviceNames);

	void createApplication(String appName, Staging staging, Integer disk, Integer memory,
	                       List<String> uris, List<String> serviceNames);

	void uploadApplication(String appName, File file, UploadStatusCallback callback) throws IOException;

	void uploadApplication(String appName, ApplicationArchive archive, UploadStatusCallback callback) throws IOException;

	StartingInfo startApplication(String appName);

	void debugApplication(String appName, CloudApplication.DebugMode mode);

	void stopApplication(String appName);

	StartingInfo restartApplication(String appName);

	void deleteApplication(String appName);

	void deleteAllApplications();

	void updateApplicationDiskQuota(String appName, int disk);

	void updateApplicationMemory(String appName, int memory);

	void updateApplicationInstances(String appName, int instances);

	void updateApplicationServices(String appName, List<String> services);

	void updateApplicationStaging(String appName, Staging staging);

	void updateApplicationUris(String appName, List<String> uris);

	void updateApplicationEnv(String appName, Map<String, String> env);

	void updateApplicationEnv(String appName, List<String> env);

	Map<String, String> getLogs(String appName);

	StreamingLogToken streamLogs(String appName, ApplicationLogListener listener);

	List<ApplicationLog> getRecentLogs(String appName);

	Map<String, String> getCrashLogs(String appName);

	String getFile(String appName, int instanceIndex, String filePath, int startPosition, int endPosition);

	void openFile(String appName, int instanceIndex, String filePath, ClientHttpResponseCallback callback);

	void bindService(String appName, String serviceName);

	void unbindService(String appName, String serviceName);

	InstancesInfo getApplicationInstances(String appName);

	InstancesInfo getApplicationInstances(CloudApplication app);

	CrashesInfo getCrashes(String appName);

	void rename(String appName, String newName);

	String getStagingLogs(StartingInfo info, int offset);

	List<CloudStack> getStacks();

	CloudStack getStack(String name);

	// Domains and routes management


	List<CloudDomain> getDomainsForOrg();

	List<CloudDomain> getDomains();

	List<CloudDomain> getPrivateDomains();

	List<CloudDomain> getSharedDomains();

	CloudDomain getDefaultDomain();

	void addDomain(String domainName);

	void deleteDomain(String domainName);

	void removeDomain(String domainName);

	List<CloudRoute> getRoutes(String domainName);

	void addRoute(String host, String domainName);

	void deleteRoute(String host, String domainName);

	// Misc. utility methods

	void registerRestLogListener(RestLogCallback callBack);

	void unRegisterRestLogListener(RestLogCallback callBack);
}
