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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudApplication.DebugMode;
import org.cloudfoundry.client.lib.archive.ApplicationArchive;
import org.cloudfoundry.client.lib.domain.CloudDomain;
import org.cloudfoundry.client.lib.domain.CloudRoute;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.cloudfoundry.client.lib.rest.CloudControllerClient;
import org.cloudfoundry.client.lib.rest.CloudControllerClientFactory;
import org.cloudfoundry.client.lib.domain.ApplicationStats;
import org.cloudfoundry.client.lib.domain.CloudInfo;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.CrashesInfo;
import org.cloudfoundry.client.lib.domain.InstancesInfo;
import org.cloudfoundry.client.lib.domain.ServiceConfiguration;
import org.cloudfoundry.client.lib.domain.Staging;
import org.cloudfoundry.client.lib.util.RestUtil;
import org.springframework.util.Assert;

/**
 * A Java client to exercise the Cloud Foundry API.
 *
 * @author Ramnivas Laddad
 * @author A.B.Srinivasan
 * @author Jennifer Hickey
 * @author Dave Syer
 * @author Thomas Risberg
 */
@SuppressWarnings("unused")
public class CloudFoundryClient implements CloudFoundryOperations {

	private CloudControllerClient cc;

	private CloudInfo info;

	/**
	 * Construct client for anonymous user. Useful only to get to the '/info' endpoint.
	 */
	public CloudFoundryClient(URL cloudControllerUrl) {
		this(null, cloudControllerUrl, null, null);
	}

	public CloudFoundryClient(CloudCredentials credentials, URL cloudControllerUrl) {
		this(credentials, cloudControllerUrl, null, null);
	}

	public CloudFoundryClient(CloudCredentials credentials, URL cloudControllerUrl, CloudSpace sessionSpace) {
		this(credentials, cloudControllerUrl, null, sessionSpace);
    }

	/**
	 * Constructors to use with an http proxy configuration.
	 */
	public CloudFoundryClient(URL cloudControllerUrl, HttpProxyConfiguration httpProxyConfiguration) {
		this(null, cloudControllerUrl, httpProxyConfiguration, null);
	}

	public CloudFoundryClient(CloudCredentials credentials, URL cloudControllerUrl,
							  HttpProxyConfiguration httpProxyConfiguration) {
		this(credentials, cloudControllerUrl, httpProxyConfiguration, null);
	}

	public CloudFoundryClient(CloudCredentials credentials, URL cloudControllerUrl,
							  HttpProxyConfiguration httpProxyConfiguration, CloudSpace sessionSpace) {
		Assert.notNull(cloudControllerUrl, "URL for cloud controller cannot be null");
		CloudControllerClientFactory cloudControllerClientFactory =
				new CloudControllerClientFactory(new RestUtil(), httpProxyConfiguration);
		this.cc = cloudControllerClientFactory.newCloudController(cloudControllerUrl, credentials, sessionSpace);
    }


	public URL getCloudControllerUrl() {
		return cc.getCloudControllerUrl();
	}

	public CloudInfo getCloudInfo() {
		if (info == null) {
			info = cc.getInfo();
		}
		return info;
	}

	public boolean supportsSpaces() {
		return cc.supportsSpaces();
	}

	public List<CloudSpace> getSpaces() {
		return cc.getSpaces();
	}

	public List<String> getApplicationPlans() {
		return cc.getApplicationPlans();
	}

	public void register(String email, String password) {
		cc.register(email, password);
	}

	public void updatePassword(String newPassword) {
		cc.updatePassword(newPassword);
	}

	public void updatePassword(CloudCredentials credentials, String newPassword) {
		cc.updatePassword(credentials, newPassword);
	}

	public void unregister() {
		cc.unregister();
	}

	public String login() {
		return cc.login();
	}

	public void logout() {
		cc.logout();
	}

	public List<CloudApplication> getApplications() {
		return cc.getApplications();
	}

	public CloudApplication getApplication(String appName) {
		return cc.getApplication(appName);
	}

	public ApplicationStats getApplicationStats(String appName) {
		return cc.getApplicationStats(appName);
	}

	public int[] getApplicationMemoryChoices() {
		return cc.getApplicationMemoryChoices();
	}

	public int getDefaultApplicationMemory(String framework) {
		return cc.getDefaultApplicationMemory(framework);
	}

	public void createApplication(String appName, Staging staging, int memory, List<String> uris,
								  List<String> serviceNames) {
		cc.createApplication(appName, staging, memory, uris, serviceNames, false);
	}

	public void createApplication(String appName, Staging staging, int memory, List<String> uris,
								  List<String> serviceNames, boolean checkExists) {
		cc.createApplication(appName, staging, memory, uris, serviceNames, checkExists);
	}

    public void createApplication(String appName, Staging staging, int memory, List<String> uris,
                                  List<String> serviceNames, String applicationPlan) {
        cc.createApplication(appName, staging, memory, uris, serviceNames, applicationPlan, false, null);
    }

    public void createApplication(String appName, Staging staging, int memory, List<String> uris,
                                  List<String> serviceNames, String applicationPlan, String buildpackUrl) {
		cc.createApplication(appName, staging, memory, uris, serviceNames, applicationPlan, false, buildpackUrl);
	}

	public void createApplication(String appName, Staging staging, int memory, List<String> uris,
                                  List<String> serviceNames, String applicationPlan, boolean checkExists) {
		cc.createApplication(appName, staging, memory, uris, serviceNames, applicationPlan, checkExists, null);
	}
	public void createApplication(String appName, Staging staging, int memory, List<String> uris,
                                  List<String> serviceNames, String applicationPlan, boolean checkExists, String buildpackUrl) {
		cc.createApplication(appName, staging, memory, uris, serviceNames, applicationPlan, checkExists, buildpackUrl);
	}

	public void createApplication(String appName, String framework, int memory, List<String> uris,
								  List<String> serviceNames) {
		cc.createApplication(appName, new Staging(framework), memory, uris, serviceNames, false);
	}

	public void createApplication(String appName, String framework, int memory, List<String> uris,
								  List<String> serviceNames, boolean checkExists) {
		cc.createApplication(appName, new Staging(framework), memory, uris, serviceNames, checkExists);
	}

	public void createService(CloudService service) {
		cc.createService(service);
	}


    public void uploadApplication(String appName, String file) throws IOException {
        cc.uploadApplication(appName, new File(file), null);
    }

    public void uploadApplication(String appName, File file) throws IOException {
        cc.uploadApplication(appName, file, null);
    }

    public void uploadApplication(String appName, File file, UploadStatusCallback callback) throws IOException {
		cc.uploadApplication(appName, file, callback);
    }

    public void uploadApplication(String appName, ApplicationArchive archive) throws IOException {
        cc.uploadApplication(appName, archive, null);
    }

    public void uploadApplication(String appName, ApplicationArchive archive, UploadStatusCallback callback) throws IOException {
		cc.uploadApplication(appName, archive, callback);
    }


	public StartingInfo startApplication(String appName) {
		return cc.startApplication(appName);
	}

	public void debugApplication(String appName, DebugMode mode) {
		cc.debugApplication(appName, mode);
	}

	public void stopApplication(String appName) {
		cc.stopApplication(appName);
	}

	public void restartApplication(String appName) {
		cc.restartApplication(appName);
	}

	public void deleteApplication(String appName) {
		cc.deleteApplication(appName);
	}

	public void deleteAllApplications() {
		cc.deleteAllApplications();
	}

	public void deleteAllServices() {
		cc.deleteAllServices();
	}

	public void updateApplicationMemory(String appName, int memory) {
		cc.updateApplicationMemory(appName, memory);
	}

	public void updateApplicationInstances(String appName, int instances) {
		cc.updateApplicationInstances(appName, instances);
	}

	public void updateApplicationServices(String appName, List<String> services) {
		cc.updateApplicationServices(appName, services);
	}

	public void updateApplicationStaging(String appName, Staging staging) {
		cc.updateApplicationStaging(appName, staging);
	}

	public void updateApplicationUris(String appName, List<String> uris) {
		cc.updateApplicationUris(appName, uris);
	}

	public void updateApplicationEnv(String appName, Map<String, String> env) {
		cc.updateApplicationEnv(appName, env);
	}

	public void updateApplicationEnv(String appName, List<String> env) {
		cc.updateApplicationEnv(appName, env);
	}

	public void updateApplicationPlan(String appName, String applicationPlan) {
		cc.updateApplicationPlan(appName, applicationPlan);
	}

	public Map<String, String> getLogs(String appName) {
		return cc.getLogs(appName);
	}

	public Map<String, String> getCrashLogs(String appName) {
		return cc.getCrashLogs(appName);
	}

	public String getFile(String appName, int instanceIndex, String filePath) {
		return cc.getFile(appName, instanceIndex, filePath, 0, -1);
	}

	public String getFile(String appName, int instanceIndex, String filePath, int startPosition) {
		Assert.isTrue(startPosition >= 0,
				startPosition + " is not a valid value for start position, it should be 0 or greater.");
		return cc.getFile(appName, instanceIndex, filePath, startPosition, -1);
	}

	public String getFile(String appName, int instanceIndex, String filePath, int startPosition, int endPosition) {
		Assert.isTrue(startPosition >= 0,
				startPosition + " is not a valid value for start position, it should be 0 or greater.");
		Assert.isTrue(endPosition > startPosition,
				endPosition + " is not a valid value for end position, it should be greater than startPosition " +
						"which is " + startPosition + ".");
		return cc.getFile(appName, instanceIndex, filePath, startPosition, endPosition - 1);
	}

	public String getFileTail(String appName, int instanceIndex, String filePath, int length) {
		Assert.isTrue(length > 0, length + " is not a valid value for length, it should be 1 or greater.");
		return cc.getFile(appName, instanceIndex, filePath, -1, length);
	}

	// list services, un/provision services, modify instance

	public List<CloudService> getServices() {
		return cc.getServices();
	}

	public CloudService getService(String service) {
		return cc.getService(service);
	}

	public void deleteService(String service) {
		cc.deleteService(service);
	}

	public List<ServiceConfiguration> getServiceConfigurations() {
		return cc.getServiceConfigurations();
	}

	public void bindService(String appName, String serviceName) {
		cc.bindService(appName, serviceName);
	}

	public void unbindService(String appName, String serviceName) {
		cc.unbindService(appName, serviceName);
	}

	public InstancesInfo getApplicationInstances(String appName) {
		return cc.getApplicationInstances(appName);
	}

	public CrashesInfo getCrashes(String appName) {
		return cc.getCrashes(appName);
	}

	public void rename(String appName, String newName) {
		cc.rename(appName, newName);
	}

	public List<CloudDomain> getDomainsForOrg() {
		return cc.getDomainsForOrg();
	}

	public List<CloudDomain> getDomains() {
		return cc.getDomains();
	}

	public void addDomain(String domainName) {
		cc.addDomain(domainName);
	}

	public void deleteDomain(String domainName) {
		cc.deleteDomain(domainName);
	}

	public void removeDomain(String domainName) {
		cc.removeDomain(domainName);
	}

	public List<CloudRoute> getRoutes(String domainName) {
		return cc.getRoutes(domainName);
	}

	public void addRoute(String host, String domainName) {
		cc.addRoute(host, domainName);
	}

	public void deleteRoute(String host, String domainName) {
		cc.deleteRoute(host, domainName);
	}

	public void updateHttpProxyConfiguration(HttpProxyConfiguration httpProxyConfiguration) {
		cc.updateHttpProxyConfiguration(httpProxyConfiguration);
	}

	public void registerRestLogListener(RestLogCallback callBack) {
		cc.registerRestLogListener(callBack);
	}

	public void unRegisterRestLogListener(RestLogCallback callBack) {
		cc.unRegisterRestLogListener(callBack);
	}

    public List<String> getStagingLogs(StartingInfo startingInfo) {
        return cc.getStagingLogs(startingInfo);
    }

}
