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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipFile;

import org.cloudfoundry.client.lib.CloudApplication.AppState;
import org.cloudfoundry.client.lib.CloudApplication.DebugMode;
import org.cloudfoundry.client.lib.archive.ApplicationArchive;
import org.cloudfoundry.client.lib.archive.DirectoryApplicationArchive;
import org.cloudfoundry.client.lib.archive.ZipApplicationArchive;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

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

	private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
	private static final String PROXY_USER_HEADER_KEY = "Proxy-User";

	private static final MediaType JSON_MEDIA_TYPE = new MediaType(
			MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(),
			Charset.forName("UTF-8"));

	private RestTemplate restTemplate = new RestTemplate();
	private URL cloudControllerUrl;

	private String token;
	private String email;
	private String password;
	private String proxyUser;

	private CloudInfo info;

	/**
	 * Construct client for anonymous user. Useful only to get to the '/info' endpoint.
	 */
	public CloudFoundryClient(String cloudControllerUrl) throws MalformedURLException {
		this(null, null, null, new URL(cloudControllerUrl));
	}

	public CloudFoundryClient(String email, String password, String cloudControllerUrl) throws MalformedURLException {
		this(email, password, null, new URL(cloudControllerUrl));
	}

	public CloudFoundryClient(String token, String cloudControllerUrl) throws MalformedURLException {
		this((String)null, (String)null, token, new URL(cloudControllerUrl));
	}

	public CloudFoundryClient(String email, String password, String token, URL cloudControllerUrl) {
		this(email, password, token, cloudControllerUrl, new SimpleClientHttpRequestFactory());
	}

	public CloudFoundryClient(String email, String password, String token, URL cloudControllerUrl, ClientHttpRequestFactory requestFactory) {
		Assert.notNull(cloudControllerUrl, "URL for cloud controller cannot be null");
		Assert.notNull(requestFactory, "RequestFactory for cloud controller cannot be null");

		this.cloudControllerUrl = cloudControllerUrl;
		this.email = email;
        this.password = password;
        this.token = token;
        this.restTemplate.setRequestFactory(new CloudFoundryClientHttpRequestFactory(requestFactory));
        this.restTemplate.setErrorHandler(new ErrorHandler());
        this.restTemplate.setMessageConverters(getHttpMessageConverters());
    }

    private List<HttpMessageConverter<?>> getHttpMessageConverters() {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(new ByteArrayHttpMessageConverter());
        messageConverters.add(new StringHttpMessageConverter());
        messageConverters.add(new ResourceHttpMessageConverter());
        messageConverters.add(new UploadApplicationPayloadHttpMessageConverter());
        messageConverters.add(getFormHttpMessageConverter());
        messageConverters.add(new MappingJacksonHttpMessageConverter());
        return messageConverters;
    }

    private FormHttpMessageConverter getFormHttpMessageConverter() {
        FormHttpMessageConverter formPartsMessageConverter = new CloudFoundryFormHttpMessageConverter();
        formPartsMessageConverter.setPartConverters(getFormPartsMessageConverters());
        return formPartsMessageConverter;
    }

    private List<HttpMessageConverter<?>> getFormPartsMessageConverters() {
        List<HttpMessageConverter<?>> partConverters = new ArrayList<HttpMessageConverter<?>>();
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
        stringConverter.setSupportedMediaTypes(Collections.singletonList(JSON_MEDIA_TYPE));
        stringConverter.setWriteAcceptCharset(false);
        partConverters.add(stringConverter);
        partConverters.add(new ResourceHttpMessageConverter());
        partConverters.add(new UploadApplicationPayloadHttpMessageConverter());
        return partConverters;
    }

    /**
     * Protected access to the rest templates for subclasses to use.
	 *
     * @return the underling rest template
     */
    protected final RestTemplate getRestTemplate() {
        return restTemplate;
    }

	public void setProxyUser(String proxyUser) {
		this.proxyUser = proxyUser;
	}

	public URL getCloudControllerUrl() {
		return cloudControllerUrl;
	}

	public CloudInfo getCloudInfo() {
		if (info == null) {
			@SuppressWarnings("unchecked")
			Map<String,Object> infoMap = restTemplate.getForObject(getUrl("info"), Map.class);
			info = new CloudInfo(infoMap);
		}
		return info;
	}

	public void register(String email, String password) {
		Map<String, String> payload = new HashMap<String, String>();
		payload.put("email", email);
		payload.put("password", password);

		restTemplate.postForLocation(getUrl("users"), payload);
	}

	@SuppressWarnings("unchecked")
	public void updatePassword(String newPassword) {
		Map<String, String> userInfo = restTemplate.getForObject(getUrl("users/{id}"), Map.class, email);
		userInfo.put("password", newPassword);
		restTemplate.put(getUrl("users/{id}"), userInfo, email);
	}

	public void unregister() {
		restTemplate.delete(getUrl("users/{email}"), email);
		token = null;
	}

	public String login(String email, String password) {
		this.email = email;
		this.password = password;
		this.info = null;
		return login();
	}

	@SuppressWarnings("unchecked")
	public String login() {
		Map<String, String> payload = new HashMap<String, String>();
		payload.put("password", password);
		Map<String, String> response = restTemplate.postForObject(getUrl("users/{id}/tokens"), payload, Map.class, email);
		token = response.get("token");

		return token;
	}

	public void logout() {
		token = null;
		proxyUser = null;
	}

	@SuppressWarnings("unchecked")
	public List<CloudApplication> getApplications() {
		List<Map<String, Object>> appsAsMap = restTemplate.getForObject(getUrl("apps"), List.class);
		List<CloudApplication> apps = new ArrayList<CloudApplication>();
		for (Map<String, Object> appAsMap : appsAsMap) {
			apps.add(new CloudApplication(appAsMap));
		}
		return apps;
	}

	@SuppressWarnings("unchecked")
	public CloudApplication getApplication(String appName) {
		Map<String, Object> appAsMap = restTemplate.getForObject(getUrl("apps/{appName}"), Map.class, appName);
		return new CloudApplication(appAsMap);
	}

	public ApplicationStats getApplicationStats(String appName) {
		@SuppressWarnings("unchecked")
		Map<String, Object> statsAsMap = restTemplate.getForObject(getUrl("apps/{appName}/stats"), Map.class, appName);
		return new ApplicationStats(statsAsMap);
	}

	public int[] getApplicationMemoryChoices() {
		// TODO: Get it from cloudcontroller's 'info/resources' end point
		int[] generalChoices = new int[] {64, 128, 256, 512, 1024, 2048};
		int maxMemory = getCloudInfo().getLimits().getMaxTotalMemory();

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

	public void createApplication(String appName, Staging staging, int memory, List<String> uris,
								  List<String> serviceNames) {
		createApplication(appName, staging, memory, uris, serviceNames, false);
	}

	public void createApplication(String appName, String framework, int memory, List<String> uris,
								  List<String> serviceNames) {
		createApplication(appName, framework, memory, uris, serviceNames, false);
	}

	public void createApplication(String appName, String framework, int memory, List<String> uris,
								  List<String> serviceNames, boolean checkExists) {
		createApplication(appName, new Staging(framework), memory, uris, serviceNames, checkExists);
	}

	public void createApplication(String appName, Staging staging, int memory, List<String> uris,
								  List<String> serviceNames, boolean checkExists) {

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
				memory, 1, uris, serviceNames, AppState.STOPPED);
		payload.setCommand(staging.getCommand());
		restTemplate.postForLocation(getUrl("apps"), payload);
		CloudApplication postedApp = getApplication(appName);
		if (serviceNames != null && serviceNames.size() != 0) {
			postedApp.setServices(serviceNames);
			updateApplication(postedApp);
		}
	}

	public void createService(CloudService service) {
		restTemplate.postForLocation(getUrl("services"), service);
	}


    public void uploadApplication(String appName, String file) throws IOException {
        uploadApplication(appName, new File(file));
    }

    public void uploadApplication(String appName, File file) throws IOException {
        uploadApplication(appName, file, null);
    }

    public void uploadApplication(String appName, File file, UploadStatusCallback callback) throws IOException {
        Assert.notNull(file,"File must not be null");
        if(file.isDirectory()) {
            uploadApplicationFolder(appName,file,callback);
        }
        else {
            uploadApplicationZipFile(appName,file,callback);
        }
    }

    private void uploadApplicationFolder(String appName, File file, UploadStatusCallback callback) throws IOException {
        ApplicationArchive archive = new DirectoryApplicationArchive(file);
        uploadApplication(appName, archive, callback);
    }

    private void uploadApplicationZipFile(String appName, File file, UploadStatusCallback callback) throws IOException {
        ZipFile zipFile = new ZipFile(file);
        try {
            ApplicationArchive archive = new ZipApplicationArchive(zipFile);
            uploadApplication(appName, archive, callback);
        } finally {
            zipFile.close();
        }
    }

    public void uploadApplication(String appName, ApplicationArchive archive) throws IOException {
        uploadApplication(appName, archive, null);
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
        restTemplate.put(getUrl("apps/{appName}/application"), generatePartialResourceRequest(payload, knownRemoteResources), appName);
    }

    private CloudResources getKnownRemoteResources(ApplicationArchive archive) throws IOException {
        CloudResources archiveResources = new CloudResources(archive);
        return restTemplate.postForObject(getUrl("resources"), archiveResources, CloudResources.class);
    }

    private HttpEntity<MultiValueMap<String, ?>> generatePartialResourceRequest(UploadApplicationPayload application,
		CloudResources knownRemoteResources) throws IOException {
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>(2);
        if(application.getNumEntries() > 0) {
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

	public void startApplication(String appName) {
		CloudApplication app = getApplication(appName);
		app.setState(AppState.STARTED);
		app.setDebug(null);
		updateApplication(app);
	}

	public void debugApplication(String appName, DebugMode mode) {
		CloudApplication app = getApplication(appName);
		app.setState(AppState.STARTED);
		app.setDebug(mode);
		updateApplication(app);
	}

	public void stopApplication(String appName) {
		CloudApplication app = getApplication(appName);
		if (app != null) {
			app.setState(AppState.STOPPED);
			updateApplication(app);
		}
	}

	public void restartApplication(String appName) {
		stopApplication(appName);
		startApplication(appName);
	}

	public void deleteApplication(String appName) {
		restTemplate.delete(getUrl("apps/{appName}"), appName);
	}

	public void deleteAllApplications() {
		List<CloudApplication> apps = getApplications();
		for (CloudApplication app : apps) {
			deleteApplication(app.getName());
		}
	}

	public void deleteAllServices() {
		List<CloudService> services = getServices();
		for (CloudService service : services) {
			deleteService(service.getName());
		}
	}

	public void updateApplicationMemory(String appName, int memory) {
		CloudApplication app = getApplication(appName);
		if (app == null) {
			throw new IllegalArgumentException("Application " + appName + " does not exist");
		}
		app.setMemory(memory);
		updateApplication(app);
	}

	public void updateApplicationInstances(String appName, int instances) {
		CloudApplication app = getApplication(appName);
		if (app == null) {
			throw new IllegalArgumentException("Application " + appName + " does not exist");
		}
		app.setInstances(instances);
		updateApplication(app);
	}

	public void updateApplicationServices(String appName, List<String> services) {
		CloudApplication app = getApplication(appName);
		if (app == null) {
			throw new IllegalArgumentException("Application " + appName + " does not exist");
		}
		app.setServices(services);
		updateApplication(app);
	}

	public void updateApplicationUris(String appName, List<String> uris) {
		CloudApplication app = getApplication(appName);
		if (app == null) {
			throw new IllegalArgumentException("Application " + appName + " does not exist");
		}
		app.setUris(uris);
		updateApplication(app);
	}

	public void updateApplicationEnv(String appName, Map<String, String> env) {
		CloudApplication app = getApplication(appName);
		if (app == null) {
			throw new IllegalArgumentException("Application " + appName + " does not exist");
		}
		app.setEnv(env);
		updateApplication(app);
	}

	public void updateApplicationEnv(String appName, List<String> env) {
		CloudApplication app = getApplication(appName);
		if (app == null) {
			throw new IllegalArgumentException("Application " + appName + " does not exist");
		}
		app.setEnv(env);
		updateApplication(app);
	}

	public String getFile(String appName, int instanceIndex, String filePath) {
		return restTemplate.getForObject(getUrl("apps/{appName}/instances/{instanceIndex}/files/{filePath}"),
				String.class,
				appName, instanceIndex, filePath);
	}

	// list services, un/provision services, modify instance

	public List<CloudService> getServices() {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> servicesAsMap = restTemplate.getForObject(getUrl("services"), List.class);
		List<CloudService> services = new ArrayList<CloudService>();
		for (Map<String, Object> serviceAsMap : servicesAsMap) {
			services.add(new CloudService(serviceAsMap));
		}
		return services;
	}

	public CloudService getService(String service) {
		@SuppressWarnings("unchecked")
		Map<String, Object> serviceAsMap = restTemplate.getForObject(
				getUrl("services/{service}"), Map.class, service);
		return new CloudService(serviceAsMap);
	}

	public void deleteService(String service) {
		restTemplate.delete(getUrl("services/{service}"), service);
	}

	@SuppressWarnings("unchecked")
	public List<ServiceConfiguration> getServiceConfigurations() {
		Map<String, Object> configurationAsMap = restTemplate.getForObject(
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

	public void bindService(String appName, String serviceName) {
		CloudApplication application = getApplication(appName);
		if (application.getServices() == null) {
			application.setServices(Collections.singletonList(serviceName));
		}
		else {
			application.getServices().add(serviceName);
		}
		updateApplication(application);
	}

	public void unbindService(String appName, String serviceName) {
		CloudApplication application = getApplication(appName);
		if (application.getServices() != null) {
			application.getServices().remove(serviceName);
			updateApplication(application);
		}
	}

	public InstancesInfo getApplicationInstances(String appName) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = restTemplate.getForObject(getUrl("apps/{appName}/instances"), Map.class, appName);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> instanceData = (List<Map<String, Object>>)map.get("instances");
		return new InstancesInfo(instanceData);
	}

	public CrashesInfo getCrashes(String appName) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = restTemplate.getForObject(getUrl("apps/{appName}/crashes"), Map.class, appName);
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
		restTemplate.put(getUrl("apps/{appName}"), app, appName);
	}

	/**
	 * Update application.
	 *
	 * @param app the appplication info
	 */
	private void updateApplication(CloudApplication app) {
		restTemplate.put(getUrl("apps/{appName}"), app, app.getName());
	}

	private String getUrl(String path) {
		return cloudControllerUrl + "/" + path;
	}

    private class CloudFoundryClientHttpRequestFactory implements ClientHttpRequestFactory {

    	private static final String LEGACY_TOKEN_PREFIX = "0408";
		private ClientHttpRequestFactory delegate;

        public CloudFoundryClientHttpRequestFactory(ClientHttpRequestFactory delegate) {
            this.delegate = delegate;
        }

        public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
            ClientHttpRequest request = delegate.createRequest(uri, httpMethod);
            if (token != null) {
                String header = token;
                if (!header.startsWith(LEGACY_TOKEN_PREFIX) && !header.toLowerCase().startsWith("bearer")) {
                	header = "Bearer " + header; // UAA token without OAuth prefix
                }
				request.getHeaders().add(AUTHORIZATION_HEADER_KEY, header );
            }
            if (proxyUser !=  null) {
                request.getHeaders().add(PROXY_USER_HEADER_KEY, proxyUser);
            }
            return request;
        }
    }

    private static class ErrorHandler extends DefaultResponseErrorHandler {
        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            HttpStatus statusCode = response.getStatusCode();
            switch (statusCode.series()) {
                case CLIENT_ERROR:
                    CloudFoundryException exception = new CloudFoundryException(statusCode, response.getStatusText());
                    ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
                    try {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> map = mapper.readValue(response.getBody(), Map.class);
                        exception.setDescription(CloudUtil.parse(String.class, map.get("description")));
                    } catch (JsonParseException e) {
                        // ignore
                    }
                    throw exception;
                case SERVER_ERROR:
                    throw new HttpServerErrorException(statusCode, response.getStatusText());
                default:
                    throw new RestClientException("Unknown status code [" + statusCode + "]");
            }
        }
    }

    private static class CloudFoundryFormHttpMessageConverter extends FormHttpMessageConverter {
        @Override
        protected String getFilename(Object part) {
            if(part instanceof UploadApplicationPayload) {
                return ((UploadApplicationPayload)part).getArchive().getFilename();
            }
            return super.getFilename(part);
        }
    }
}
