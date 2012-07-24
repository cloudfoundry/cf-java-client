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

package org.cloudfoundry.client.lib.rest;

import org.cloudfoundry.client.lib.UploadStatusCallback;
import org.cloudfoundry.client.lib.archive.ApplicationArchive;
import org.cloudfoundry.client.lib.domain.ApplicationStats;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.cloudfoundry.client.lib.domain.CrashesInfo;
import org.cloudfoundry.client.lib.domain.InstancesInfo;
import org.cloudfoundry.client.lib.domain.ServiceConfiguration;
import org.cloudfoundry.client.lib.domain.Staging;
import org.cloudfoundry.client.lib.oauth2.OauthClient;
import org.cloudfoundry.client.lib.util.CloudEntityResourceMapper;
import org.cloudfoundry.client.lib.util.JsonUtil;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Empty implementation for cloud controller v2 REST API
 *
 * @author Thomas Risberg
 */
public class CloudControllerClientV2 extends AbstractCloudControllerClient {

	OauthClient oauthClient;

	CloudSpace sessionSpace;

	CloudEntityResourceMapper resourceMapper = new CloudEntityResourceMapper();

	public CloudControllerClientV2(URL cloudControllerUrl, CloudAuthenticationConfiguration authenticationConfiguration,
								   String token) {
		super(cloudControllerUrl, authenticationConfiguration, token);
		this.oauthClient = new OauthClient(authenticationConfiguration.getAuthorizationUrl());
	}

	public CloudControllerClientV2(URL cloudControllerUrl, CloudAuthenticationConfiguration authenticationConfiguration,
								   String token, CloudSpace sessionSpace) {
		super(cloudControllerUrl, authenticationConfiguration, token);
		this.oauthClient = new OauthClient(authenticationConfiguration.getAuthorizationUrl());
		this.sessionSpace = sessionSpace;
	}

	public CloudControllerClientV2(URL cloudControllerUrl, CloudAuthenticationConfiguration authenticationConfiguration,
								   String token, ClientHttpRequestFactory requestFactory) {
		super(cloudControllerUrl, authenticationConfiguration, token, requestFactory);
		this.oauthClient = new OauthClient(authenticationConfiguration.getAuthorizationUrl());
	}

	public boolean supportsSpaces() {
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<CloudSpace> getSpaces() {
		String resp = getRestTemplate().getForObject(getUrl("v2/spaces?inline-relations-depth=1"), String.class);
		Map<String, Object> respMap = JsonUtil.convertJsonToMap(resp);
		List<Map<String, Object>> resourceList = (List<Map<String, Object>>) respMap.get("resources");
		List<CloudSpace> spaces = new ArrayList<CloudSpace>();
		for (Map<String, Object> resource : resourceList) {
			spaces.add(resourceMapper.mapJsonResource(resource, CloudSpace.class));
		}
		return spaces;
	}

	public String login() {
		OAuth2AccessToken token = oauthClient.getToken(authenticationConfiguration.getEmail(),
				authenticationConfiguration.getPassword());
		this.token = token.getTokenType() + " " + token.getValue();
		return this.token;
	}

	public void logout() {
		token = null;
		authenticationConfiguration.setProxyUser(null);
	}

	public void register(String email, String password) {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public void updatePassword(String newPassword) {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public void unregister() {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public List<CloudService> getServices() {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public void createService(CloudService service) {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public CloudService getService(String serviceName) {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public void deleteService(String service) {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public void deleteAllServices() {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public List<ServiceConfiguration> getServiceConfigurations() {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public List<CloudApplication> getApplications() {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public CloudApplication getApplication(String appName) {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public ApplicationStats getApplicationStats(String appName) {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public int[] getApplicationMemoryChoices() {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public int getDefaultApplicationMemory(String framework) {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public void createApplication(String appName, Staging staging, int memory, List<String> uris,
								  List<String> serviceNames, boolean checkExists) {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public void uploadApplication(String appName, File file, UploadStatusCallback callback) throws IOException {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public void uploadApplication(String appName, ApplicationArchive archive, UploadStatusCallback callback)
			throws IOException {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public void startApplication(String appName) {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public void debugApplication(String appName, CloudApplication.DebugMode mode) {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public void stopApplication(String appName) {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public void restartApplication(String appName) {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public void deleteApplication(String appName) {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public void deleteAllApplications() {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public void updateApplicationMemory(String appName, int memory) {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public void updateApplicationInstances(String appName, int instances) {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public void updateApplicationServices(String appName, List<String> services) {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public void updateApplicationUris(String appName, List<String> uris) {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public void updateApplicationEnv(String appName, Map<String, String> env) {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public void updateApplicationEnv(String appName, List<String> env) {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public String getFile(String appName, int instanceIndex, String filePath) {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public void bindService(String appName, String serviceName) {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public void unbindService(String appName, String serviceName) {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public InstancesInfo getApplicationInstances(String appName) {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public CrashesInfo getCrashes(String appName) {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public void rename(String appName, String newName) {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}


}
