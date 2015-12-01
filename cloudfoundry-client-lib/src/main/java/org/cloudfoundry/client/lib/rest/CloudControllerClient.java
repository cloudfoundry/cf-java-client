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
import org.cloudfoundry.client.lib.domain.CloudQuota;
import org.cloudfoundry.client.lib.domain.CloudRoute;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.CloudServiceBroker;
import org.cloudfoundry.client.lib.domain.CloudServiceOffering;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.cloudfoundry.client.lib.domain.CloudStack;
import org.cloudfoundry.client.lib.domain.CrashesInfo;
import org.cloudfoundry.client.lib.domain.InstancesInfo;
import org.cloudfoundry.client.lib.domain.Staging;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Interface defining operations available for the cloud controller REST client implementations
 *
 * @author Thomas Risberg
 */
public interface CloudControllerClient {

    // User and Info methods

    void addDomain(String domainName);

    void addRoute(String host, String domainName);

    void bindService(String appName, String serviceName);

    void createApplication(String appName, Staging staging, Integer memory, List<String> uris,
                           List<String> serviceNames);

    void createApplication(String appName, Staging staging, Integer disk, Integer memory,
                           List<String> uris, List<String> serviceNames);

    void createQuota(CloudQuota quota);

    void createService(CloudService service);

    void createServiceBroker(CloudServiceBroker serviceBroker);

    void createSpace(String spaceName);

    void createUserProvidedService(CloudService service, Map<String, Object> credentials);

    void createUserProvidedService(CloudService service, Map<String, Object> credentials, String syslogDrainUrl);

    // Service methods

    void debugApplication(String appName, CloudApplication.DebugMode mode);

    void deleteAllApplications();

    void deleteAllServices();

    void deleteApplication(String appName);

    void deleteDomain(String domainName);

    List<CloudRoute> deleteOrphanedRoutes();

    void deleteQuota(String quotaName);

    void deleteRoute(String host, String domainName);

    void deleteService(String service);

    void deleteServiceBroker(String name);

    void deleteSpace(String spaceName);

    CloudApplication getApplication(String appName);

    CloudApplication getApplication(UUID appGuid);

    InstancesInfo getApplicationInstances(String appName);

    // App methods

    InstancesInfo getApplicationInstances(CloudApplication app);

    ApplicationStats getApplicationStats(String appName);

    List<CloudApplication> getApplications();

    URL getCloudControllerUrl();

    Map<String, String> getCrashLogs(String appName);

    CrashesInfo getCrashes(String appName);

    CloudDomain getDefaultDomain();

    List<CloudDomain> getDomains();

    List<CloudDomain> getDomainsForOrg();

    String getFile(String appName, int instanceIndex, String filePath, int startPosition, int endPosition);

    CloudInfo getInfo();

    Map<String, String> getLogs(String appName);

    // Quota operations
    CloudOrganization getOrgByName(String orgName, boolean required);

    List<CloudOrganization> getOrganizations();

    List<CloudDomain> getPrivateDomains();

    CloudQuota getQuotaByName(String quotaName, boolean required);

    List<CloudQuota> getQuotas();

    List<ApplicationLog> getRecentLogs(String appName);

    List<CloudRoute> getRoutes(String domainName);

    CloudService getService(String service);

    CloudServiceBroker getServiceBroker(String name);

    List<CloudServiceBroker> getServiceBrokers();

    List<CloudServiceOffering> getServiceOfferings();

    List<CloudService> getServices();

    List<CloudDomain> getSharedDomains();

    CloudSpace getSpace(String spaceName);

    List<CloudSpace> getSpaces();

    CloudStack getStack(String name);

    List<CloudStack> getStacks();

    String getStagingLogs(StartingInfo info, int offset);

    OAuth2AccessToken login();

    void logout();

    void openFile(String appName, int instanceIndex, String filePath, ClientHttpResponseCallback callback);

    void register(String email, String password);

    void registerRestLogListener(RestLogCallback callBack);

    void removeDomain(String domainName);

    void rename(String appName, String newName);

    StartingInfo restartApplication(String appName);

    // Space management

    void setQuotaToOrg(String orgName, String quotaName);

    void setResponseErrorHandler(ResponseErrorHandler errorHandler);

    StartingInfo startApplication(String appName);

    // Domains and routes management

    void stopApplication(String appName);

    StreamingLogToken streamLogs(String appName, ApplicationLogListener listener);

    void unRegisterRestLogListener(RestLogCallback callBack);

    void unbindService(String appName, String serviceName);

    void unregister();

    void updateApplicationDiskQuota(String appName, int disk);

    void updateApplicationEnv(String appName, Map<String, String> env);

    void updateApplicationEnv(String appName, List<String> env);

    void updateApplicationInstances(String appName, int instances);

    void updateApplicationMemory(String appName, int memory);

    void updateApplicationServices(String appName, List<String> services);

    void updateApplicationStaging(String appName, Staging staging);

    // Misc. utility methods

    void updateApplicationUris(String appName, List<String> uris);

    void updatePassword(String newPassword);

    void updatePassword(CloudCredentials credentials, String newPassword);

    void updateQuota(CloudQuota quota, String name);

    void updateServiceBroker(CloudServiceBroker serviceBroker);

    void updateServicePlanVisibilityForBroker(String name, boolean visibility);

    void uploadApplication(String appName, File file, UploadStatusCallback callback) throws IOException;

    void uploadApplication(String appName, String fileName, InputStream inputStream, UploadStatusCallback callback)
            throws IOException;

    void uploadApplication(String appName, ApplicationArchive archive, UploadStatusCallback callback) throws
            IOException;
}
