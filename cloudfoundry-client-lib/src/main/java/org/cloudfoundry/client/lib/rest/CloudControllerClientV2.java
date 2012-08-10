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

import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.UploadStatusCallback;
import org.cloudfoundry.client.lib.archive.ApplicationArchive;
import org.cloudfoundry.client.lib.domain.ApplicationStats;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudInfo;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.CloudServiceOffering;
import org.cloudfoundry.client.lib.domain.CloudServicePlan;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.cloudfoundry.client.lib.domain.CrashesInfo;
import org.cloudfoundry.client.lib.domain.InstancesInfo;
import org.cloudfoundry.client.lib.domain.ServiceConfiguration;
import org.cloudfoundry.client.lib.domain.Staging;
import org.cloudfoundry.client.lib.oauth2.OauthClient;
import org.cloudfoundry.client.lib.util.CloudEntityResourceMapper;
import org.cloudfoundry.client.lib.util.CloudUtil;
import org.cloudfoundry.client.lib.util.JsonUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.client.HttpClientErrorException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Empty implementation for cloud controller v2 REST API
 *
 * @author Thomas Risberg
 */
public class CloudControllerClientV2 extends AbstractCloudControllerClient {

	OauthClient oauthClient;

	CloudSpace sessionSpace;

	CloudEntityResourceMapper resourceMapper = new CloudEntityResourceMapper();

	Map<String, UUID> runtimeIdCache = new HashMap<String, UUID>();

	Map<String, UUID> frameworkIdCache = new HashMap<String, UUID>();

	public CloudControllerClientV2(URL cloudControllerUrl, CloudCredentials cloudCredentials,
								   URL authorizationEndpoint, CloudSpace sessionSpace) {
		super(cloudControllerUrl, cloudCredentials, authorizationEndpoint);
		this.oauthClient = new OauthClient(authorizationEndpoint);
		this.sessionSpace = sessionSpace;
	}

	@SuppressWarnings("unchecked")
	public CloudInfo getInfo() {
		String infoJson = getRestTemplate().getForObject(getCloudControllerUrl() + "/v2/info", String.class);
		Map<String, Object> infoMap = JsonUtil.convertJsonToMap(infoJson);

		Map<String, Object> userMap = getUserInfo(getCloudControllerUrl(), (String) infoMap.get("user"));

		//TODO: replace with v2 api call once, or if, they become available
		String infoV1Json = getRestTemplate().getForObject(getCloudControllerUrl() + "/info", String.class);
		Map<String, Object> infoV1Map = (Map<String, Object>) JsonUtil.convertJsonToMap(infoV1Json);
		Map<String, Object> limitMap = (Map<String, Object>) infoV1Map.get("limits");
		Map<String, Object> usageMap = (Map<String, Object>) infoV1Map.get("usage");

		String name = CloudUtil.parse(String.class, infoMap.get("name"));
		String support = CloudUtil.parse(String.class, infoMap.get("support"));
		String authorizationEndpoint = CloudUtil.parse(String.class, infoMap.get("authorization_endpoint"));
		int build = CloudUtil.parse(Integer.class, infoMap.get("build"));
		String version = "" + CloudUtil.parse(Number.class, infoMap.get("version"));
		String description = CloudUtil.parse(String.class, infoMap.get("description"));
		CloudInfo.Limits limits = new CloudInfo.Limits(limitMap);
		CloudInfo.Usage usage = new CloudInfo.Usage(usageMap);
		boolean debug = CloudUtil.parse(Boolean.class, infoV1Map.get("allow_debug"));

		return new CloudInfo(name, support, authorizationEndpoint, build, version, (String)userMap.get("user_name"),
				description, limits, usage, debug, getInfoForFrameworks(), getInfoForRuntimes());
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
			spaces.add(resourceMapper.mapResource(resource, CloudSpace.class));
		}
		return spaces;
	}

	public String login() {
		OAuth2AccessToken token = oauthClient.getToken(cloudCredentials.getEmail(),
				cloudCredentials.getPassword());
		this.token = token.getTokenType() + " " + token.getValue();
		return this.token;
	}

	public void logout() {
		token = null;
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

	@SuppressWarnings("unchecked")
	public List<CloudService> getServices() {
		Map<String, Object> urlVars = new HashMap<String, Object>();
		String urlPath = "v2";
		if (sessionSpace != null) {
			urlVars.put("space", sessionSpace.getMeta().getGuid());
			urlPath = urlPath + "/spaces/{space}";
		}
		urlPath = urlPath + "/service_instances?inline-relations-depth={depth}";
		urlVars.put("depth", 2);
		String resp = getRestTemplate().getForObject(getUrl(urlPath), String.class, urlVars);
		Map<String, Object> respMap = JsonUtil.convertJsonToMap(resp);
		List<Map<String, Object>> resourceList = (List<Map<String, Object>>) respMap.get("resources");
		List<CloudService> services = new ArrayList<CloudService>();
		for (Map<String, Object> resource : resourceList) {
			services.add(resourceMapper.mapResource(resource, CloudService.class));
		}
		return services;
	}

	public void createService(CloudService service) {
		List<CloudServiceOffering> offerings = getServiceOfferings(service.getLabel());
		CloudServicePlan cloudServicePlan = null;
		for (CloudServiceOffering offering : offerings) {
			for (CloudServicePlan plan : offering.getCloudServicePlans())
			if (service.getPlan() != null && service.getPlan().equals(plan.getName())) {
				cloudServicePlan = plan;
				break;
			}
		}
		HashMap<String, Object> serviceRequest = new HashMap<String, Object>();
		serviceRequest.put("space_guid", sessionSpace.getMeta().getGuid());
		serviceRequest.put("name", service.getName());
		serviceRequest.put("service_plan_guid", cloudServicePlan.getMeta().getGuid());
		serviceRequest.put("credentials", new HashMap());
		getRestTemplate().postForObject(getUrl("/v2/service_instances"), serviceRequest, String.class);
	}

	@SuppressWarnings("unchecked")
	public CloudService getService(String serviceName) {
		String urlPath = "v2";
		Map<String, Object> urlVars = new HashMap<String, Object>();
		if (sessionSpace != null) {
			urlVars.put("space", sessionSpace.getMeta().getGuid());
			urlPath = urlPath + "/spaces/{space}";
		}
		urlVars.put("q", "name:" + serviceName);
		urlPath = urlPath + "/service_instances?inline-relations-depth=2&q={q}";
		String resp = getRestTemplate().getForObject(getUrl(urlPath), String.class, urlVars);
		Map<String, Object> respMap = JsonUtil.convertJsonToMap(resp);
		List<Map<String, Object>> resourceList = (List<Map<String, Object>>) respMap.get("resources");
		CloudService cloudService = null;
		if (resourceList.size() > 0) {
			cloudService = resourceMapper.mapResource(resourceList.get(0), CloudService.class);
		}
		return cloudService;
	}

	public void deleteService(String serviceName) {
		CloudService cloudService = getService(serviceName);
		doDeleteService(cloudService);
	}

	public void deleteAllServices() {
		List<CloudService> cloudServices = getServices();
		for (CloudService cloudService : cloudServices) {
			doDeleteService(cloudService);
		}
	}

	@SuppressWarnings("unchecked")
	public List<ServiceConfiguration> getServiceConfigurations() {
		String urlPath = "/v2/services?inline-relations-depth=1";
		String resp = getRestTemplate().getForObject(getUrl(urlPath), String.class);
		Map<String, Object> respMap = JsonUtil.convertJsonToMap(resp);
		List<Map<String, Object>> resourceList = (List<Map<String, Object>>) respMap.get("resources");
		List<ServiceConfiguration> serviceConfigurations = new ArrayList<ServiceConfiguration>();
		for (Map<String, Object> resource : resourceList) {
			CloudServiceOffering serviceOffering = resourceMapper.mapResource(resource, CloudServiceOffering.class);
			serviceConfigurations.add(new ServiceConfiguration(serviceOffering));
		}
		return serviceConfigurations;
	}

	@SuppressWarnings("unchecked")
	public List<CloudApplication> getApplications() {
		Map<String, Object> urlVars = new HashMap<String, Object>();
		String urlPath = "v2";
		if (sessionSpace != null) {
			urlVars.put("space", sessionSpace.getMeta().getGuid());
			urlPath = urlPath + "/spaces/{space}";
		}
		urlPath = urlPath + "/apps?inline-relations-depth={depth}";
		urlVars.put("depth", 2);
		String resp = getRestTemplate().getForObject(getUrl(urlPath), String.class, urlVars);
		Map<String, Object> respMap = JsonUtil.convertJsonToMap(resp);
		List<Map<String, Object>> resourceList = (List<Map<String, Object>>) respMap.get("resources");
		List<CloudApplication> apps = new ArrayList<CloudApplication>();
		for (Map<String, Object> resource : resourceList) {
			apps.add(resourceMapper.mapResource(resource, CloudApplication.class));
		}
		return apps;
	}

	@SuppressWarnings("unchecked")
	public CloudApplication getApplication(String appName) {
		Map<String, Object> resource = findApplicationResource(appName, 2);
		CloudApplication cloudApp = null;
		if (resource != null) {
			cloudApp = resourceMapper.mapResource(resource, CloudApplication.class);
		}
		return cloudApp;
	}

	public ApplicationStats getApplicationStats(String appName) {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public int[] getApplicationMemoryChoices() {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public int getDefaultApplicationMemory(String framework) {
		//TODO: implement this method
		return 512;
	}

	public void createApplication(String appName, Staging staging, int memory, List<String> uris,
								  List<String> serviceNames, boolean checkExists) {
		if (checkExists) {
			try {
				getAppId(appName);
				return;
			} catch (HttpClientErrorException e) {
				if (e.getStatusCode() != HttpStatus.NOT_FOUND) {
					throw e;
				}
			}
		}

		HashMap<String, Object> appRequest = new HashMap<String, Object>();
		appRequest.put("space_guid", sessionSpace.getMeta().getGuid());
		appRequest.put("name", appName);
		appRequest.put("framework_guid", getFrameworkId(staging.getFramework()));
		appRequest.put("runtime_guid", getRuntimeId(staging.getRuntime()));
		appRequest.put("memory", memory);
		appRequest.put("instances", 1);
		//TODO: these need to be added?
//		appRequest.put("uris", ?);
//		appRequest.put("command", staging.getCommand());
		appRequest.put("state", CloudApplication.AppState.STOPPED);
		String postResp = getRestTemplate().postForObject(getUrl("/v2/apps"), appRequest, String.class);

		if (serviceNames != null && serviceNames.size() != 0) {
			updateApplicationServices(appName, serviceNames);
		}
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
		UUID appId = getAppId(appName);
		doDeleteApplication(appId);
	}

	public void deleteAllApplications() {
		List<CloudApplication> cloudApps = getApplications();
		for (CloudApplication cloudApp : cloudApps) {
			doDeleteApplication(cloudApp.getMeta().getGuid());
		}
	}

	public void updateApplicationMemory(String appName, int memory) {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public void updateApplicationInstances(String appName, int instances) {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public void updateApplicationServices(String appName, List<String> services) {
		CloudApplication app = getApplication(appName);
		List<UUID> addServices = new ArrayList<UUID>();
		List<UUID> deleteServices = new ArrayList<UUID>();
		// services to add
		for (String serviceName : services) {
			if (!app.getServices().contains(serviceName)) {
				CloudService cloudService = getService(serviceName);
				if (cloudService != null) {
					addServices.add(cloudService.getMeta().getGuid());
				}
				else {
					throw new CloudFoundryException(HttpStatus.NOT_FOUND, "Service with name " + serviceName +
							" not found in current space " + sessionSpace.getName());
				}
			}
		}
		// services to delete
		for (String serviceName : app.getServices()) {
			if (!services.contains(serviceName)) {
				CloudService cloudService = getService(serviceName);
				if (cloudService != null) {
					deleteServices.add(cloudService.getMeta().getGuid());
				}
			}
		}
		for (UUID serviceId : addServices) {
			HashMap<String, Object> serviceRequest = new HashMap<String, Object>();
			serviceRequest.put("service_instance_guid", serviceId);
			serviceRequest.put("app_guid", app.getMeta().getGuid());
			serviceRequest.put("credentials", new HashMap());
			getRestTemplate().postForObject(getUrl("v2/service_bindings"), serviceRequest, String.class);
		}
		for (UUID serviceId : deleteServices) {
			UUID serviceBindingId = getServiceBindingId(app.getMeta().getGuid(), serviceId);
			getRestTemplate().delete(getUrl("v2/service_bindings/{guid}"), serviceBindingId);
		}
	}

	public void updateApplicationUris(String appName, List<String> uris) {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	public void updateApplicationEnv(String appName, Map<String, String> env) {
		UUID appId = getAppId(appName);
		HashMap<String, Object> appRequest = new HashMap<String, Object>();
		appRequest.put("environment_json", env);
		getRestTemplate().put(getUrl("v2/apps/{guid}"), appRequest, appId);
	}

	public void updateApplicationEnv(String appName, List<String> env) {
		Map<String, String> envHash = new HashMap<String, String>();
		for (String s : env) {
			if (!s.contains("=")) {
				throw new IllegalArgumentException("Environment setting without an '=' sign encountered: " + s);
			}
			String key = s.substring(0, s.indexOf('=')).trim();
			String value = s.substring(s.indexOf('=') + 1).trim();
			envHash.put(key, value);
		}
		updateApplicationEnv(appName, envHash);
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
		UUID appId = getAppId(appName);
		HashMap<String, Object> appRequest = new HashMap<String, Object>();
		appRequest.put("name", newName);
		getRestTemplate().put(getUrl("v2/apps/{guid}"), appRequest, appId);
	}

	private void doDeleteService(CloudService cloudService) {
		getRestTemplate().delete(getUrl("/v2/service_instances/{guid}"), cloudService.getMeta().getGuid());
	}

	private void doDeleteApplication(UUID appId) {
		getRestTemplate().delete(getUrl("/v2/apps/{guid}"), appId);
	}

	@SuppressWarnings("unchecked")
	private Collection<CloudInfo.Framework> getInfoForFrameworks() {
		String frameworksJson = getRestTemplate().getForObject(getCloudControllerUrl() + "/v2/frameworks", String.class);
		List<Map<String, Object>> frameworkList =
				(List<Map<String, Object>>) JsonUtil.convertJsonToMap(frameworksJson).get("resources");
		Collection<CloudInfo.Framework> frameworks = new ArrayList<CloudInfo.Framework>();
		for (Map<String, Object> frameworkMap : frameworkList) {
			Map<String, Object> frameworkEntity = (Map<String, Object>) frameworkMap.get("entity");
			CloudInfo.Framework framework = new CloudInfo.Framework(frameworkEntity);
			frameworks.add(framework);
		}
		return frameworks;
	}

	@SuppressWarnings("unchecked")
	private Map<String, CloudInfo.Runtime> getInfoForRuntimes() {
		String runtimesJson = getRestTemplate().getForObject(getCloudControllerUrl() + "/v2/runtimes", String.class);
		List<Map<String, Object>> runtimesList =
				(List<Map<String, Object>>) JsonUtil.convertJsonToMap(runtimesJson).get("resources");
		Map<String, CloudInfo.Runtime> runtimes = new HashMap<String, CloudInfo.Runtime>();
		for (Map<String, Object> runtimeMap : runtimesList) {
			Map<String, Object> runtimeEntity = (Map<String, Object>) runtimeMap.get("entity");
			CloudInfo.Runtime runtime = new CloudInfo.Runtime(runtimeEntity);
			runtimes.put(runtime.getName(), runtime);
		}
		return runtimes;
	}

	@SuppressWarnings("unchecked")
	private List<CloudServiceOffering> getServiceOfferings(String label) {
		String resp = getRestTemplate().getForObject(getCloudControllerUrl() +
				"/v2/services?inline-relations-depth=2", String.class);
		Map<String, Object> respMap = JsonUtil.convertJsonToMap(resp);
		List<Map<String, Object>> resourceList = (List<Map<String, Object>>) respMap.get("resources");
		List<CloudServiceOffering> results = new ArrayList<CloudServiceOffering>();
		for (Map<String, Object> resource : resourceList) {
			CloudServiceOffering cloudServiceOffering =
					resourceMapper.mapResource(resource, CloudServiceOffering.class);
			if (label.equals(cloudServiceOffering.getLabel())) {
				results.add(cloudServiceOffering);
			}
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	private UUID getServiceBindingId(UUID appId, UUID serviceId ) {
		String resp = getRestTemplate().getForObject(getUrl("v2/apps/{guid}/service_bindings"), String.class, appId);
		UUID serviceBindingId = null;
		Map<String, Object> respMap = JsonUtil.convertJsonToMap(resp);
		List<Map<String, Object>> resourceList = (List<Map<String, Object>>) respMap.get("resources");
		if (resourceList != null && resourceList.size() > 0) {
			for (Map<String, Object> resource : resourceList) {
				Map<String, Object> bindingMeta = (Map<String, Object>) resource.get("metadata");
				Map<String, Object> bindingEntity = (Map<String, Object>) resource.get("entity");
				String serviceInstanceGuid = (String) bindingEntity.get("service_instance_guid");
				if (serviceInstanceGuid != null && serviceInstanceGuid.equals(serviceId.toString())) {
					String bindingGuid = (String) bindingMeta.get("guid");
					serviceBindingId = UUID.fromString(bindingGuid);
					break;
				}
			}
		}
		return serviceBindingId;
	}

	@SuppressWarnings("unchecked")
	private UUID getAppId(String appName) {
		Map<String, Object> resource = findApplicationResource(appName, 1);
		UUID guid = null;
		if (resource != null) {
			Map<String, Object> appMeta = (Map<String, Object>) resource.get("metadata");
			guid = UUID.fromString(String.valueOf(appMeta.get("guid")));
		}
		return guid;
	}

	private Map<String, Object> findApplicationResource(String appName, int depth) {
		Map<String, Object> urlVars = new HashMap<String, Object>();
		String urlPath = "v2";
		if (sessionSpace != null) {
			urlVars.put("space", sessionSpace.getMeta().getGuid());
			urlPath = urlPath + "/spaces/{space}";
		}
		urlVars.put("q", "name:" + appName);
		urlPath = urlPath + "/apps?inline-relations-depth={depth}&q={q}";
		urlVars.put("depth", depth);
		String resp = getRestTemplate().getForObject(getUrl(urlPath), String.class, urlVars);
		Map<String, Object> respMap = JsonUtil.convertJsonToMap(resp);
		List<Map<String, Object>> resourceList = (List<Map<String, Object>>) respMap.get("resources");
		if (resourceList.size() > 0) {
			return resourceList.get(0);
		}
		else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private UUID getFrameworkId(String framework) {
		if (!frameworkIdCache.containsKey(framework)) {
			String resp =
					getRestTemplate().getForObject(getCloudControllerUrl() + "/v2/frameworks", String.class);
			List<Map<String, Object>> resourceList =
					(List<Map<String, Object>>) JsonUtil.convertJsonToMap(resp).get("resources");
			for (Map<String, Object> resource : resourceList) {
				String name = resourceMapper.getNameOfResource(resource);
				UUID guid = resourceMapper.getGuidOfResource(resource);
				frameworkIdCache.put(name, guid);
			}
		}
		return frameworkIdCache.get(framework);
	}

	@SuppressWarnings("unchecked")
	private UUID getRuntimeId(String runtime) {
		if (!runtimeIdCache.containsKey(runtime)) {
			String resp =
					getRestTemplate().getForObject(getCloudControllerUrl() + "/v2/runtimes", String.class);
			List<Map<String, Object>> resourceList =
					(List<Map<String, Object>>) JsonUtil.convertJsonToMap(resp).get("resources");
			for (Map<String, Object> resource : resourceList) {
				String name = resourceMapper.getNameOfResource(resource);
				UUID guid = resourceMapper.getGuidOfResource(resource);
				runtimeIdCache.put(name, guid);
			}
		}
		return runtimeIdCache.get(runtime);
	}

	@SuppressWarnings("unused")
	private Map<String, Object> getUserInfo(URL cloudControllerUrl, String user) {
//		String userJson = getRestTemplate().getForObject(cloudControllerUrl + "/v2/users/{guid}", String.class, user);
//		Map<String, Object> userInfo = (Map<String, Object>) JsonUtil.convertJsonToMap(userJson);
//		return userInfo();
		//TODO: remove this temporary hack once the /v2/users/ uri can be accessed by mere mortals
		String userJson = "{}";
		int x = token.indexOf('.');
		int y = token.indexOf('.', x + 1);
		String encodedString = token.substring(x + 1, y);
		try {
			byte[] decodedBytes = new sun.misc.BASE64Decoder().decodeBuffer(encodedString);
			userJson = new String(decodedBytes, 0, decodedBytes.length, "UTF-8");
		} catch (IOException e) {}
		return(JsonUtil.convertJsonToMap(userJson));
	}

}
