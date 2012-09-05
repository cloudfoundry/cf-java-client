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
import org.cloudfoundry.client.lib.HttpProxyConfiguration;
import org.cloudfoundry.client.lib.domain.CloudInfo;
import org.cloudfoundry.client.lib.oauth2.OauthClient;
import org.cloudfoundry.client.lib.util.CloudUtil;
import org.cloudfoundry.client.lib.util.JsonUtil;
import org.cloudfoundry.client.lib.util.StringHttpMessageConverterWithoutMediaType;
import org.cloudfoundry.client.lib.util.UploadApplicationPayloadHttpMessageConverter;
import org.cloudfoundry.client.lib.UploadStatusCallback;
import org.cloudfoundry.client.lib.archive.ApplicationArchive;
import org.cloudfoundry.client.lib.archive.DirectoryApplicationArchive;
import org.cloudfoundry.client.lib.archive.ZipApplicationArchive;
import org.cloudfoundry.client.lib.domain.ApplicationStats;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudResources;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.CrashesInfo;
import org.cloudfoundry.client.lib.domain.InstancesInfo;
import org.cloudfoundry.client.lib.domain.ServiceConfiguration;
import org.cloudfoundry.client.lib.domain.Staging;
import org.cloudfoundry.client.lib.domain.UploadApplicationPayload;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipFile;

/**
 * Implementation for cloud controller v1 REST API
 *
 * @author Ramnivas Laddad
 * @author A.B.Srinivasan
 * @author Jennifer Hickey
 * @author Dave Syer
 * @author Thomas Risberg
 */
public class CloudControllerClientV1 extends AbstractCloudControllerClient {

	private OauthClient oauthClient;

	public CloudControllerClientV1(URL cloudControllerUrl,
								   HttpProxyConfiguration httpProxyConfiguration,
								   CloudCredentials cloudCredentials,
								   URL authorizationUrl) {
		super(cloudControllerUrl, httpProxyConfiguration, cloudCredentials, authorizationUrl);
		initializeOauthClient(httpProxyConfiguration);
	}

	public CloudInfo getInfo() {
		String resp = getRestTemplate().getForObject(getCloudControllerUrl() + "/info", String.class);
		Map<String, Object> infoMap = JsonUtil.convertJsonToMap(resp);
		return new CloudInfo(infoMap);
	}

	public boolean supportsSpaces() {
		return false;
	}

	public String login() {
		if (cloudCredentials.getEmail() == null) {
			Assert.hasLength(cloudCredentials.getToken(), "No authentication details provided");
			token = cloudCredentials.getToken();
			return token;
		}
		Assert.hasLength(cloudCredentials.getEmail(), "Email cannot be null or empty");
		Assert.hasLength(cloudCredentials.getPassword(), "Password cannot be null or empty");
		if (oauthClient != null) {
			OAuth2AccessToken token = oauthClient.getToken(cloudCredentials.getEmail(),
					cloudCredentials.getPassword());
			this.token = token.getTokenType() + " " + token.getValue();
			return this.token;
		}
		else {
			Map<String, String> payload = new HashMap<String, String>();
			payload.put("password", cloudCredentials.getPassword());
			Map<String, String> response = getRestTemplate().postForObject(
					getUrl("users/{id}/tokens"), payload, Map.class, cloudCredentials.getEmail());
			token = response.get("token");
			return token;
		}
	}

	public void logout() {
		token = null;
	}

	public void register(String email, String password) {
		Map<String, String> payload = new HashMap<String, String>();
		payload.put("email", email);
		payload.put("password", password);
		getRestTemplate().postForLocation(getUrl("users"), payload);
	}

	public void updatePassword(CloudCredentials credentials, String newPassword) {

		if (oauthClient != null) {
			oauthClient.changePassword(token, credentials.getPassword(), newPassword);
		} else {
			Map<String, String> userInfo = getRestTemplate().getForObject(getUrl("users/{id}"), Map.class,
				credentials.getEmail());
			userInfo.put("password", newPassword);
			getRestTemplate().put(getUrl("users/{id}"), userInfo, credentials.getEmail());
		}

		CloudCredentials newCloudCredentials = new CloudCredentials(credentials.getEmail(), newPassword);
		if (cloudCredentials.getProxyUser() != null) {
			cloudCredentials = newCloudCredentials.proxyForUser(cloudCredentials.getProxyUser());
		} else {
			cloudCredentials = newCloudCredentials;
		}
	}

	public void unregister() {
		getRestTemplate().delete(getUrl("users/{email}"), cloudCredentials.getEmail());
		token = null;
	}

	public List<CloudService> getServices() {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> servicesAsMap = getRestTemplate().getForObject(getUrl("services"), List.class);
		List<CloudService> services = new ArrayList<CloudService>();
		for (Map<String, Object> serviceAsMap : servicesAsMap) {
			services.add(new CloudService(serviceAsMap));
		}
		return services;
	}

	public void createService(CloudService service) {
		getRestTemplate().postForLocation(getUrl("services"), service);
	}

	public CloudService getService(String service) {
		@SuppressWarnings("unchecked")
		Map<String, Object> serviceAsMap = getRestTemplate().getForObject(
				getUrl("services/{service}"), Map.class, service);
		return new CloudService(serviceAsMap);
	}

	public void deleteService(String service) {
		getRestTemplate().delete(getUrl("services/{service}"), service);
	}

	public void deleteAllServices() {
		List<CloudService> services = getServices();
		for (CloudService service : services) {
			deleteService(service.getName());
		}
	}

	public List<ServiceConfiguration> getServiceConfigurations() {
		Map<String, Object> configurationAsMap = getRestTemplate().getForObject(
				getUrl("info/services"), Map.class);
		if (configurationAsMap == null) {
			return Collections.emptyList();
		}

		List<ServiceConfiguration> configurations = new ArrayList<ServiceConfiguration>();

		for (Map.Entry<String, Object> typeEntry : configurationAsMap.entrySet()) {
			Map<String, Object> vendorMap = CloudUtil.parse(Map.class, typeEntry.getValue());
			if (vendorMap == null) {
				continue;
			}

			for (Map.Entry<String, Object> vendorEntry : vendorMap.entrySet()) {
				Map<String, Object> versionMap = CloudUtil.parse(Map.class, vendorEntry.getValue());
				if (versionMap == null) {
					continue;
				}

				for (Map.Entry<String, Object> serviceEntry : versionMap.entrySet()) {
					Map<String, Object> attributes = CloudUtil.parse(Map.class, serviceEntry.getValue());
					if (attributes != null) {
						configurations.add(new ServiceConfiguration(attributes));
					}
				}
			}
		}
		return configurations;
	}

	public List<CloudApplication> getApplications() {
		List<Map<String, Object>> appsAsMap = getRestTemplate().getForObject(getUrl("apps"), List.class);
		List<CloudApplication> apps = new ArrayList<CloudApplication>();
		for (Map<String, Object> appAsMap : appsAsMap) {
			apps.add(new CloudApplication(appAsMap));
		}
		return apps;
	}

	public CloudApplication getApplication(String appName) {
		Map<String, Object> appAsMap = getRestTemplate().getForObject(getUrl("apps/{appName}"), Map.class, appName);
		return new CloudApplication(appAsMap);
	}

	public ApplicationStats getApplicationStats(String appName) {
		@SuppressWarnings("unchecked")
		Map<String, Object> statsAsMap = getRestTemplate().getForObject(getUrl("apps/{appName}/stats"), Map.class, appName);
		return new ApplicationStats(statsAsMap);
	}

	public int[] getApplicationMemoryChoices() {
		// TODO: Get it from cloudcontroller's 'info/resources' end point
		int[] generalChoices = new int[] {64, 128, 256, 512, 1024, 2048};
		int maxMemory = getInfo().getLimits().getMaxTotalMemory();

		int length = 0;
		for (int generalChoice : generalChoices) {
			if (generalChoice <= maxMemory) {
				length++;
			}
		}

		int[] result = new int[length];
		System.arraycopy(generalChoices, 0, result, 0, length);
		return result;
	}

	public int getDefaultApplicationMemory(String framework) {
		// TODO: Currently, we don't use framework as the only one supported is the Spring Framework
		return 512;
	}

	public void createApplication(String appName, Staging staging, int memory, List<String> uris, List<String> serviceNames, boolean checkExists) {
		if (checkExists) {
			try {
				getApplication(appName);
				return;
			} catch (HttpClientErrorException e) {
				if (e.getStatusCode() != HttpStatus.NOT_FOUND) {
					throw e;
				}
			}
		}

		if (serviceNames == null) {
			serviceNames = new ArrayList<String>();
		}
		CloudApplication payload = new CloudApplication(appName, staging.getRuntime(), staging.getFramework(),
				memory, 1, uris, serviceNames, CloudApplication.AppState.STOPPED);
		payload.setCommand(staging.getCommand());
		getRestTemplate().postForLocation(getUrl("apps"), payload);
		CloudApplication postedApp = getApplication(appName);
		if (serviceNames != null && serviceNames.size() != 0) {
			postedApp.setServices(serviceNames);
			updateApplication(postedApp);
		}
	}

	public void uploadApplication(String appName, File file, UploadStatusCallback callback) throws IOException {
		Assert.notNull(file, "File must not be null");
		if (file.isDirectory()) {
			doUploadApplicationFolder(appName, file, callback);
		} else {
			doUploadApplicationZipFile(appName, file, callback);
		}
	}

	public void uploadApplication(String appName, ApplicationArchive archive, UploadStatusCallback callback) throws IOException {
		Assert.notNull(appName, "AppName must not be null");
		Assert.notNull(archive, "Archive must not be null");
		if (callback == null) {
			callback = UploadStatusCallback.NONE;
		}
		CloudResources knownRemoteResources = getKnownRemoteResources(archive);
		callback.onCheckResources();
		callback.onMatchedFileNames(knownRemoteResources.getFilenames());
		UploadApplicationPayload payload = new UploadApplicationPayload(archive, knownRemoteResources);
		callback.onProcessMatchedResources(payload.getTotalUncompressedSize());
		HttpEntity<?> entity = generatePartialResourceRequest(payload, knownRemoteResources);
		String url = getUrl("apps/{appName}/application");
		try {
			getRestTemplate().put(url, entity, appName);
		} catch (HttpServerErrorException hsee) {
			if (HttpStatus.INTERNAL_SERVER_ERROR.equals(hsee.getStatusCode())) {
				// this is for supporting legacy Micro Cloud Foundry 1.1 and older
				uploadAppUsingLegacyApi(url, entity, appName);
			} else {
				throw hsee;
			}
		}
	}

	public void startApplication(String appName) {
		CloudApplication app = getApplication(appName);
		app.setState(CloudApplication.AppState.STARTED);
		app.setDebug(null);
		doUpdateApplication(app);
	}

	public void debugApplication(String appName, CloudApplication.DebugMode mode) {
		CloudApplication app = getApplication(appName);
		app.setState(CloudApplication.AppState.STARTED);
		app.setDebug(mode);
		doUpdateApplication(app);
	}

	public void stopApplication(String appName) {
		CloudApplication app = getApplication(appName);
		if (app != null) {
			app.setState(CloudApplication.AppState.STOPPED);
			doUpdateApplication(app);
		}
	}

	public void restartApplication(String appName) {
		stopApplication(appName);
		startApplication(appName);
	}

	public void deleteApplication(String appName) {
		getRestTemplate().delete(getUrl("apps/{appName}"), appName);
	}

	public void deleteAllApplications() {
		List<CloudApplication> apps = getApplications();
		for (CloudApplication app : apps) {
			deleteApplication(app.getName());
		}
	}

	public void updateApplicationMemory(String appName, int memory) {
		CloudApplication app = getApplication(appName);
		if (app == null) {
			throw new IllegalArgumentException("Application " + appName + " does not exist");
		}
		app.setMemory(memory);
		doUpdateApplication(app);
	}

	public void updateApplicationInstances(String appName, int instances) {
		CloudApplication app = getApplication(appName);
		if (app == null) {
			throw new IllegalArgumentException("Application " + appName + " does not exist");
		}
		app.setInstances(instances);
		doUpdateApplication(app);
	}

	public void updateApplicationServices(String appName, List<String> services) {
		CloudApplication app = getApplication(appName);
		if (app == null) {
			throw new IllegalArgumentException("Application " + appName + " does not exist");
		}
		app.setServices(services);
		doUpdateApplication(app);
	}

	public void updateApplicationUris(String appName, List<String> uris) {
		CloudApplication app = getApplication(appName);
		if (app == null) {
			throw new IllegalArgumentException("Application " + appName + " does not exist");
		}
		app.setUris(uris);
		doUpdateApplication(app);
	}

	public void updateApplicationEnv(String appName, Map<String, String> env) {
		CloudApplication app = getApplication(appName);
		if (app == null) {
			throw new IllegalArgumentException("Application " + appName + " does not exist");
		}
		app.setEnv(env);
		doUpdateApplication(app);
	}

	public void updateApplicationEnv(String appName, List<String> env) {
		CloudApplication app = getApplication(appName);
		if (app == null) {
			throw new IllegalArgumentException("Application " + appName + " does not exist");
		}
		app.setEnv(env);
		doUpdateApplication(app);
	}

	public String getFile(String appName, int instanceIndex, String filePath, int startPosition, int endPosition) {
		Assert.isTrue(startPosition >= -1, "Invalid start position value: " + startPosition);
		Assert.isTrue(endPosition >= -1, "Invalid end position value: " + endPosition);
		Assert.isTrue(startPosition < 0 || endPosition < 0 || endPosition >= startPosition,
				"The end position (" + endPosition + ") can't be less than the start position (" + startPosition + ")");

		int start, end;
		if (startPosition == -1 && endPosition == -1) {
			start = 0;
			end = -1;
		} else {
			start = startPosition;
			end = endPosition;
		}

		final String range =
				"bytes=" + (start == -1 ? "" : start) + "-" + (end == -1 ? "" : end);

		boolean supportsRanges = getRestTemplate().execute(getUrl("apps/{appName}/instances/{instanceIndex}/files/{filePath}"),
				HttpMethod.HEAD,
				new RequestCallback() {
					public void doWithRequest(ClientHttpRequest request) throws IOException {
						request.getHeaders().set("Range", "bytes=0-");
					}
				},
				new ResponseExtractor<Boolean>() {
					public Boolean extractData(ClientHttpResponse response) throws IOException {
						if (response.getStatusCode().equals(HttpStatus.PARTIAL_CONTENT)) {
							return true;
						}
						return false;
					}
				},
				appName, instanceIndex, filePath);
		HttpHeaders headers = new HttpHeaders();
		if (supportsRanges) {
			headers.set("Range", range);
		}
		HttpEntity<Object> requestEntity = new HttpEntity<Object>(headers);
		ResponseEntity<String> responseEntity =
				getRestTemplate().exchange(getUrl("apps/{appName}/instances/{instanceIndex}/files/{filePath}"),
						HttpMethod.GET, requestEntity, String.class, appName, instanceIndex, filePath);
		String response = responseEntity.getBody();
		boolean partialFile = false;
		if (responseEntity.getStatusCode().equals(HttpStatus.PARTIAL_CONTENT)) {
			partialFile = true;
		}
		if (!partialFile) {
			if (start == -1) {
				return response.substring(response.length() - end);
			} else {
				if (start >= response.length()) {
					throw new CloudFoundryException(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE,
							"The starting position " + start + " is past the end of the content.");
				}
				if (end != -1) {
					if (end >= response.length()) {
						end = response.length() - 1;
					}
					return response.substring(start, end + 1);
				} else {
					return response.substring(start);
				}
			}
		}
		return response;
	}

	public void bindService(String appName, String serviceName) {
		CloudApplication application = getApplication(appName);
		if (application.getServices() == null) {
			application.setServices(Collections.singletonList(serviceName));
		}
		else {
			application.getServices().add(serviceName);
		}
		doUpdateApplication(application);
	}

	public void unbindService(String appName, String serviceName) {
		CloudApplication application = getApplication(appName);
		if (application.getServices() != null) {
			application.getServices().remove(serviceName);
			doUpdateApplication(application);
		}
	}

	public InstancesInfo getApplicationInstances(String appName) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = getRestTemplate().getForObject(getUrl("apps/{appName}/instances"), Map.class, appName);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> instanceData = (List<Map<String, Object>>)map.get("instances");
		return new InstancesInfo(instanceData);
	}

	public CrashesInfo getCrashes(String appName) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = getRestTemplate().getForObject(getUrl("apps/{appName}/crashes"), Map.class, appName);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> crashData = (List<Map<String, Object>>)map.get("crashes");
		return new CrashesInfo(crashData);
	}

	public void rename(String appName, String newName) {
		CloudApplication app = getApplication(appName);
		if (app == null) {
			throw new IllegalArgumentException("Application " + appName + " does not exist");
		}

		app.setName(newName);
		getRestTemplate().put(getUrl("apps/{appName}"), app, appName);
	}


	private void initializeOauthClient(HttpProxyConfiguration httpProxyConfiguration) {
		if (authorizationEndpoint != null) {
			this.oauthClient = new OauthClient(authorizationEndpoint, httpProxyConfiguration);
		}
	}

	/**
	 * Update application.
	 *
	 * @param app the appplication info
	 */
	private void updateApplication(CloudApplication app) {
		getRestTemplate().put(getUrl("apps/{appName}"), app, app.getName());
	}

	private void doUploadApplicationFolder(String appName, File file, UploadStatusCallback callback) throws IOException {
		ApplicationArchive archive = new DirectoryApplicationArchive(file);
		uploadApplication(appName, archive, callback);
	}

	private void doUploadApplicationZipFile(String appName, File file, UploadStatusCallback callback) throws IOException {
		ZipFile zipFile = new ZipFile(file);
		try {
			ApplicationArchive archive = new ZipApplicationArchive(zipFile);
			uploadApplication(appName, archive, callback);
		} finally {
			zipFile.close();
		}
	}

	/**
	 * Upload an app using the legacy API used for older vcap versions like Micro Cloud Foundry 1.1 and older
	 * As of Micro Cloud Foundry 1.2 and for any recent CloudFoundry.com deployment the current method of setting
	 * the content type as JSON works fine.
	 *
	 * @param path app path
	 * @param entity HttpEntity for the payload
	 * @param appName name of app
	 * @throws HttpServerErrorException
	 */
	private void uploadAppUsingLegacyApi(String path, HttpEntity<?> entity, String appName) throws HttpServerErrorException {
        RestTemplate legacyRestTemplate = new RestTemplate();
        legacyRestTemplate.setRequestFactory(this.getRestTemplate().getRequestFactory());
        legacyRestTemplate.setErrorHandler(new ErrorHandler());
        legacyRestTemplate.setMessageConverters(getLegacyMessageConverters());
        legacyRestTemplate.put(path, entity, appName);
    }

	/**
	 * Get message converters to use for supporting legacy Micro Cloud Foundry 1.1 and older
	 *
	 * @return List of message converters
	 */
	private List<HttpMessageConverter<?>> getLegacyMessageConverters() {
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		messageConverters.add(new ByteArrayHttpMessageConverter());
		messageConverters.add(new StringHttpMessageConverter());
		messageConverters.add(new ResourceHttpMessageConverter());
		messageConverters.add(new UploadApplicationPayloadHttpMessageConverter());
		FormHttpMessageConverter formPartsMessageConverter = new CloudFoundryFormHttpMessageConverter();
		List<HttpMessageConverter<?>> partConverters = new ArrayList<HttpMessageConverter<?>>();
		StringHttpMessageConverter stringConverter = new StringHttpMessageConverterWithoutMediaType();
		stringConverter.setWriteAcceptCharset(false);
		partConverters.add(stringConverter);
		partConverters.add(new ResourceHttpMessageConverter());
		partConverters.add(new UploadApplicationPayloadHttpMessageConverter());
		formPartsMessageConverter.setPartConverters(partConverters);
		messageConverters.add(formPartsMessageConverter);
		messageConverters.add(new MappingJacksonHttpMessageConverter());
		return messageConverters;
	}

	private CloudResources getKnownRemoteResources(ApplicationArchive archive) throws IOException {
		CloudResources archiveResources = new CloudResources(archive);
		return getRestTemplate().postForObject(getUrl("resources"), archiveResources, CloudResources.class);
	}

	private HttpEntity<MultiValueMap<String, ?>> generatePartialResourceRequest(UploadApplicationPayload application,
																				CloudResources knownRemoteResources) throws IOException {
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>(2);
		if (application.getNumEntries() > 0) {
			//If the entire app contents are cached, send nothing
			body.add("application", application);
		}
		ObjectMapper mapper = new ObjectMapper();
		String knownRemoteResourcesPayload = mapper.writeValueAsString(knownRemoteResources);
		body.add("resources", knownRemoteResourcesPayload);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		return new HttpEntity<MultiValueMap<String, ?>>(body, headers);
	}

	/**
	 * Update application.
	 *
	 * @param app the appplication info
	 */
	private void doUpdateApplication(CloudApplication app) {
		getRestTemplate().put(getUrl("apps/{appName}"), app, app.getName());
	}

}
