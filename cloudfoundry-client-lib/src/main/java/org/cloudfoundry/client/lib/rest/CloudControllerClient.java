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
import java.io.InputStream;
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
import org.cloudfoundry.client.lib.domain.CloudSecurityGroup;
import org.cloudfoundry.client.lib.domain.CloudDomain;
import org.cloudfoundry.client.lib.domain.CloudEvent;
import org.cloudfoundry.client.lib.domain.CloudInfo;
import org.cloudfoundry.client.lib.domain.CloudOrganization;
import org.cloudfoundry.client.lib.domain.CloudQuota;
import org.cloudfoundry.client.lib.domain.CloudRoute;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.CloudServiceBroker;
import org.cloudfoundry.client.lib.domain.CloudServiceInstance;
import org.cloudfoundry.client.lib.domain.CloudServiceOffering;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.cloudfoundry.client.lib.domain.CloudStack;
import org.cloudfoundry.client.lib.domain.CrashesInfo;
import org.cloudfoundry.client.lib.domain.InstancesInfo;
import org.cloudfoundry.client.lib.domain.Staging;
import org.cloudfoundry.client.lib.domain.CloudUser;
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

	void createUserProvidedService(CloudService service, Map<String, Object> credentials, String syslogDrainUrl);

	CloudService getService(String service);

	CloudServiceInstance getServiceInstance(String serviceName);

	void deleteService(String service);

	void deleteAllServices();

	List<CloudServiceOffering> getServiceOfferings();

	List<CloudServiceBroker> getServiceBrokers();

    CloudServiceBroker getServiceBroker(String name);

    void createServiceBroker(CloudServiceBroker serviceBroker);

    void updateServiceBroker(CloudServiceBroker serviceBroker);

    void deleteServiceBroker(String name);

    void updateServicePlanVisibilityForBroker(String name, boolean visibility);

    // App methods

	List<CloudApplication> getApplications();

	CloudApplication getApplication(String appName);

	CloudApplication getApplication(UUID appGuid);

	ApplicationStats getApplicationStats(String appName);

	Map<String, Object> getApplicationEnvironment(UUID appGuid);

	Map<String, Object> getApplicationEnvironment(String appName);

    void createApplication(String appName, Staging staging, Integer memory, List<String> uris,
	                       List<String> serviceNames);

	void createApplication(String appName, Staging staging, Integer disk, Integer memory,
	                       List<String> uris, List<String> serviceNames);

	void uploadApplication(String appName, File file, UploadStatusCallback callback) throws IOException;

	void uploadApplication(String appName, String fileName, InputStream inputStream, UploadStatusCallback callback) throws IOException;

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

	List<CloudEvent> getEvents();

	List<CloudEvent> getApplicationEvents(String appName);

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

	// Space management

	void createSpace(String spaceName);

	CloudSpace getSpace(String spaceName);

	void deleteSpace(String spaceName);

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

	List<CloudRoute> deleteOrphanedRoutes();

	// Misc. utility methods

	void registerRestLogListener(RestLogCallback callBack);

	void unRegisterRestLogListener(RestLogCallback callBack);

	// Quota operations
	CloudOrganization getOrgByName(String orgName, boolean required);

	List<CloudQuota> getQuotas();

	CloudQuota getQuotaByName(String quotaName, boolean required);

	void createQuota(CloudQuota quota);

	void updateQuota(CloudQuota quota, String name);

	void deleteQuota(String quotaName);

	void setQuotaToOrg(String orgName, String quotaName);

	List<UUID> getSpaceManagers(String orgName, String spaceName);

	List<UUID> getSpaceDevelopers(String orgName, String spaceName);

	List<UUID> getSpaceAuditors(String orgName, String spaceName);

	void associateManagerWithSpace(String orgName, String spaceName, String userGuid);

	void associateDeveloperWithSpace(String orgName, String spaceName, String userGuid);

	void associateAuditorWithSpace(String orgName, String spaceName, String userGuid);

	// Security Group Operations

	List<CloudSecurityGroup> getSecurityGroups();

	CloudSecurityGroup getSecurityGroup(String securityGroupName);

	void createSecurityGroup(CloudSecurityGroup securityGroup);

	void createSecurityGroup(String name, InputStream jsonRulesFile);

	void updateSecurityGroup(CloudSecurityGroup securityGroup);

	void updateSecurityGroup(String name, InputStream jsonRulesFile);

	void deleteSecurityGroup(String securityGroupName);

	List<CloudSecurityGroup> getStagingSecurityGroups();

	void bindStagingSecurityGroup(String securityGroupName);

	void unbindStagingSecurityGroup(String securityGroupName);

	List<CloudSecurityGroup> getRunningSecurityGroups();

	void bindRunningSecurityGroup(String securityGroupName);

	void unbindRunningSecurityGroup(String securityGroupName);

	List<CloudSpace> getSpacesBoundToSecurityGroup(String securityGroupName);

	void bindSecurityGroup(String orgName, String spaceName, String securityGroupName);

	void unbindSecurityGroup(String orgName, String spaceName, String securityGroupName);

	Map<String, CloudUser> getOrganizationUsers(String orgName);

	void associateUserWithOrg(String orgName, String username);
}
