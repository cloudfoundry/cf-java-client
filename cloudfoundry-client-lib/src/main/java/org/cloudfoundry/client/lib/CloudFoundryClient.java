/*
 * Copyright 2009-2011 the original author or authors.
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.cloudfoundry.client.lib.CloudApplication.AppState;
import org.cloudfoundry.client.lib.CloudApplication.DebugMode;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * A Java client to exercise the Cloud Foundry API.
 *
 * @author Ramnivas Laddad
 * @author A.B.Srinivasan
 */

public class CloudFoundryClient {

	private class ErrorHandler extends DefaultResponseErrorHandler {
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

	private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
	private static final String PROXY_USER_HEADER_KEY = "Proxy-User";

	private RestTemplate restTemplate = new RestTemplate();
	private URL cloudControllerUrl;

	private String token;
	private String email;
	private String password;
	private String proxyUser;

	/*package*/ CloudInfo info;

	//private String baseDeploymentUrl;

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
		//this.baseDeploymentUrl = baseDeploymentUrl;
		restTemplate.setRequestFactory(new AppCloudClientHttpRequestFactory(requestFactory));
		restTemplate.setErrorHandler(new ErrorHandler());

		// install custom HttpMessageConverters
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		List<HttpMessageConverter<?>> partConverters = new ArrayList<HttpMessageConverter<?>>();
		FormHttpMessageConverter formPartsMessageConverter = new FormHttpMessageConverter();
		StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverterWithoutMediaType();
		stringHttpMessageConverter.setWriteAcceptCharset(false);
		partConverters.add(stringHttpMessageConverter);
		partConverters.add(new ResourceHttpMessageConverter());
		formPartsMessageConverter.setPartConverters(partConverters);
		messageConverters.add(new ByteArrayHttpMessageConverter());
		messageConverters.add(new StringHttpMessageConverter());
		messageConverters.add(new ResourceHttpMessageConverter());
		messageConverters.add(formPartsMessageConverter);
		messageConverters.add(new MappingJacksonHttpMessageConverter());
		restTemplate.setMessageConverters(messageConverters);
	}

	/**
	 * Run commands as a different user.  The authenticated user must be
	 * privileged to run as this user.
	 */
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

	/**
	 * Get choices for application memory quota
	 *
	 * @param framework
	 * @return memory choices in MB
	 */
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

	/**
	 * Get default memory quota for the given framework
	 * @param framework
	 * @return default memory quota in MB
	 */
	public int getDefaultApplicationMemory(String framework) {
		// Currently, we don't use framework as the only one supported is the Spring Framework
		return 512;
	}

	public void createApplication(String appName, String framework, int memory, List<String> uris,
			List<String> serviceNames) {
		createApplication(appName, framework, memory, uris, serviceNames, false);
	}

	public void createApplication(String appName, String framework, int memory, List<String> uris,
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
		CloudApplication payload = new CloudApplication(appName, null, framework, memory, 1, uris, serviceNames, AppState.STOPPED);
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

	public void uploadApplication(String appName, File warFile) throws IOException {
	    uploadApplicationBits(appName, warFile.getCanonicalPath(), null);

	}

	public void uploadApplication(String appName, File warFile, UploadStatusCallback callback) throws IOException {
	    uploadApplicationBits(appName, warFile.getCanonicalPath(), callback);
	}

	private MultiValueMap<String, ?> generatePartialResourcePayload(Resource application, String resources) {
		MultiValueMap<String, Object> payload = new LinkedMultiValueMap<String, Object>(2);
		payload.add("application", application);
		if (resources != null) {
			payload.add("resources", resources);
		}
		return payload;
	}

	public void uploadApplication(String appName, String warFile) throws IOException {
	    uploadApplicationBits(appName, warFile, null);
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

	public <T> T getFile(String appName, int instanceIndex, String filePath, RequestCallback requestCallback, ResponseExtractor<T> responseHandler) {
		return restTemplate.execute(getUrl("apps/{appName}/instances/{instanceIndex}/files/{filePath}"), HttpMethod.GET, requestCallback, responseHandler,
				appName, instanceIndex, filePath);
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

	/**
	 * Associate (provision) a service with an application.
	 * @param appName the application name
	 * @param serviceName the service name
	 */
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

	/**
	 * Un-associate (unprovision) a service from an application.
	 * @param appName the application name
	 * @param serviceName the service name
	 */
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

	/**
	 * Rename an application.
	 * @param appName the current name
	 * @param newName the new name
	 */
	public void rename(String appName, String newName) {
		CloudApplication app = getApplication(appName);
		if (app == null) {
			throw new IllegalArgumentException("Application " + appName + " does not exist");
		}

		app.setName(newName);
		restTemplate.put(getUrl("apps/{appName}"), app, appName);
	}

	private void updateApplication(CloudApplication app) {
		restTemplate.put(getUrl("apps/{appName}"), app, app.getName());
	}

	private String getUrl(String path) {
		return cloudControllerUrl + "/" + path;
	}

	private class AppCloudClientHttpRequestFactory implements ClientHttpRequestFactory {
		private ClientHttpRequestFactory delegate;

		public AppCloudClientHttpRequestFactory(ClientHttpRequestFactory delegate) {
			this.delegate = delegate;
		}

		public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
			ClientHttpRequest request = delegate.createRequest(uri, httpMethod);
			if (token != null) {
				request.getHeaders().add(AUTHORIZATION_HEADER_KEY, token);
			}
			if (proxyUser !=  null) {
				request.getHeaders().add(PROXY_USER_HEADER_KEY, proxyUser);
			}
			return request;
		}
	}

	private static final String HEX_CHARS = "0123456789ABCDEF";

	private static String bytesToHex(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		final StringBuilder hex = new StringBuilder(2 * bytes.length);
		for (final byte b : bytes) {
			hex.append(HEX_CHARS.charAt((b & 0xF0) >> 4)).append(HEX_CHARS.charAt((b & 0x0F)));
		}
		return hex.toString();
	}

	private List<Map<String, Object>> generateResourcePayload(String root, String dir, List<Map<String, Object>> payload) throws IOException {
	    File explodeDir = new File(dir);
	    File[] entries = explodeDir.listFiles();
	    for (File entry: entries) {
	        if (!entry.isDirectory()) {
	            String sha1sum = computeSha1Digest(new FileInputStream(entry));
	            Map<String, Object> entryPayload = new HashMap<String, Object>();
	            entryPayload.put("size", entry.length());
	            entryPayload.put("sha1", sha1sum);
	            entryPayload.put("fn", entry.getAbsolutePath().replaceFirst(root + File.separator, ""));
	            payload.add(entryPayload);
	        } else {
	            payload = generateResourcePayload(root, entry.getAbsolutePath(), payload);
	        }
	    }
	    return payload;
	}

	private String computeSha1Digest(InputStream in) throws IOException {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}

		byte[] buffer = new byte[CloudUtil.BUFFER_SIZE];
		while(true) {
			int read = in.read(buffer);
			if (read == -1) {
				break;
			}
			digest.update(buffer, 0, read);
		}
		in.close();
		return bytesToHex(digest.digest());
	}

    private void uploadApplicationBits(String appName, String file,
            UploadStatusCallback callback) throws IOException {
        boolean incremental = true;

        String explodedDirPath = prepExplodedDir(appName, file);

        String resources = null;
        if (incremental) {
            resources = matchResources(callback, explodedDirPath);
        }

        byte[] appBytes = createZip(explodedDirPath);
        if (incremental && callback != null) callback.onProcessMatchedResources(appBytes.length);

        restTemplate.put(
                    getUrl("apps/{appName}/application"),
                    generatePartialResourcePayload(
                            new InputStreamResourceWithName(new ByteArrayInputStream(appBytes), appBytes.length, file), resources),
                    appName);
    }

    private String prepExplodedDir(String appName, String file) throws IOException {
        String explodedDirPath = FileUtils.getTempDirectory().getCanonicalPath() +
            "/.vmc_java_" + appName + "_files";
        File explodedDir = new File(explodedDirPath);
        if (explodedDir.exists()) {
            FileUtils.forceDelete(explodedDir);
        }
        if (CloudUtil.isWar(file)) {
            explodedDir.mkdir();
            CloudUtil.unpackWar(file, explodedDirPath);
        } else {
            File path = new File(file);
            if (path.getCanonicalFile().isDirectory()) {
                copyDirToExplodedDir(path, explodedDirPath);
            } else {
                FileUtils.copyFileToDirectory(path, explodedDir);
            }
        }
        return explodedDirPath;
    }

    private String matchResources(UploadStatusCallback callback,
            String explodedDirPath) throws IOException,
            JsonGenerationException, JsonMappingException {
        List<Map<String, Object>> matchedResources = getMatchedResources(
                explodedDirPath);
        if (callback != null) callback.onCheckResources();
        String resources = null;
        if (matchedResources != null) {
            Set<String> matchedFileNames = getMatchedFileNames(matchedResources);
            if (callback != null) callback.onMatchedFileNames(matchedFileNames);
            deleteMatchedFiles(explodedDirPath, explodedDirPath, matchedFileNames);
            ObjectMapper objectMapper = new ObjectMapper();
            StringWriter writer = new StringWriter();
            objectMapper.writeValue(writer, matchedResources);
            resources = writer.toString();
        }
        return resources;
    }

    private Set<String> getMatchedFileNames(
            List<Map<String, Object>> matchedResources) {
        Set<String> matchedFileNames = new HashSet<String>();
        for (Map<String, Object> entry : matchedResources) {
            matchedFileNames.add((String) entry.get("fn"));
        }
        return matchedFileNames;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getMatchedResources(
            String explodeDirPath) throws IOException {
        List<Map<String, Object>> matchedResources = null;
        List<Map<String, Object>> payload = new ArrayList<Map<String, Object>>();
        payload = generateResourcePayload(explodeDirPath, explodeDirPath, payload);
        matchedResources = restTemplate.postForObject(
                getUrl("resources"),
                payload,
                List.class);
        return matchedResources;
    }

    private void deleteMatchedFiles(String root, String explodeDirPath,
            Set<String> matchedFileNames) throws IOException {
        File explodeDir = new File(explodeDirPath);
        File[] entries = explodeDir.listFiles();
        Set<File> deletedFiles = new HashSet<File>();
        for (File entry: entries) {
            if (entry.isDirectory()) {
                deleteMatchedFiles(root, entry.getCanonicalPath(), matchedFileNames);
            } else {
                if (entry.isFile() && matchedFileNames.contains(entry.getAbsolutePath().replaceFirst(root + File.separator, ""))) {
                    deletedFiles.add(entry);
                }
            }
        }
        if (deletedFiles.size() > 0) {
            for (File deletedFile:deletedFiles) {
                FileUtils.forceDelete(deletedFile);
            }
        }
    }

    private void copyDirToExplodedDir(File path, String explodedDirPath) throws IOException {
        File explodeDir = new File(explodedDirPath);
        File[] fileList = path.getCanonicalFile().listFiles();
        for (File fileToBeCopied: fileList) {
            if (CloudUtil.isSymLink(fileToBeCopied.getAbsolutePath())) {
                System.out.println("Detected a sym link for " +
                        fileToBeCopied.getCanonicalPath());
            }
            if (fileToBeCopied.getCanonicalFile().isDirectory()) {
                FileUtils.copyDirectoryToDirectory(fileToBeCopied, explodeDir);
            } else {
                FileUtils.copyFileToDirectory(fileToBeCopied, explodeDir);
            }
        }
        File gitDir = new File(explodedDirPath + "/.git");
        if (gitDir.exists()) {
            FileUtils.deleteDirectory(gitDir);
        }
    }

    // A temporary workaround till we have better support for handling empty multi-part
    // payloads on the server (or the nginx fronting the server) side.
    private static final String EMPTY_FILE = ".__empty__";
    private File ensureDirNotEmpty(String dirPath) throws IOException {
        File dirToZip = new File(dirPath);
        String[] dirList = dirToZip.list();
        if (dirList.length == 0) {
            File emptyFile = new File(dirToZip.getCanonicalPath() +
                    File.separatorChar + EMPTY_FILE);
            FileUtils.touch(emptyFile);
        }
        return dirToZip;
    }

    private void zipDir(File dirOrFileToZip, ZipOutputStream zos, String path) throws IOException {
      if (dirOrFileToZip.isDirectory()) {
        String subPath = createDirZipEntry(dirOrFileToZip, zos, path);
        String[] dirList = dirOrFileToZip.list();
        for (int i = 0; i < dirList.length; i++) {
          File f = new File(dirOrFileToZip, dirList[i]);
          zipDir(f, zos, subPath);
        }
      } else {
          createZipFileEntry(dirOrFileToZip, zos, path);
      }
    }

    private void createZipFileEntry(File dirOrFileToZip, ZipOutputStream zos,
            String path) throws FileNotFoundException,
            IOException {
        int count;
        byte[] buffer = new byte[CloudUtil.BUFFER_SIZE];

        FileInputStream fis = new FileInputStream(dirOrFileToZip);
          try {
              ZipEntry entry = new ZipEntry(path + dirOrFileToZip.getName());
              entry.setTime(dirOrFileToZip.lastModified());
              zos.putNextEntry(entry);
              while ((count = fis.read(buffer)) != -1) {
                  zos.write(buffer, 0, count);
              }
              zos.flush();
              zos.closeEntry();
          } finally {
              fis.close();
          }
    }

    private String createDirZipEntry(File dirOrFileToZip, ZipOutputStream zos,
            String path) throws IOException {
        String subPath =
            (path == null) ? "" : (path + dirOrFileToZip.getName() + '/');
        if (path != null) {
          ZipEntry ze = new ZipEntry(subPath);
          ze.setTime(dirOrFileToZip.lastModified());
          zos.putNextEntry(ze);
          zos.flush();
          zos.closeEntry();
        }
        return subPath;
    }

    private boolean trimEmptySubDirs(String dirPath) throws IOException {
        boolean empty = true;
        File dirToZip = new File(dirPath);
        Set<File> dirsToDelete = new HashSet<File>();

        File[] fileList = dirToZip.listFiles();
        for (File file: fileList) {
            if (file.isFile()) {
                empty = false;
                continue;
            }
            if (file.listFiles().length == 0) {
                dirsToDelete.add(file);
                empty = empty && true;
            } else {
                boolean subDirEmpty = trimEmptySubDirs(file.getCanonicalPath());
                if (subDirEmpty) {
                    dirsToDelete.add(file);
                }
                empty = empty && subDirEmpty;
            }
        }
        if (dirsToDelete.size() > 0) {
            for (File deletedDir:dirsToDelete) {
               FileUtils.forceDelete(deletedDir);
            }
        }
        return empty;
    }

    private byte[] createZip(String dirPath) throws IOException {
        trimEmptySubDirs(dirPath);
        File dirToZip = ensureDirNotEmpty(dirPath);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(out);
        try {
            zipDir(dirToZip, zos, null);
        } finally {
            if (zos != null) {
                zos.flush();
                zos.close();
            }
        }
        return out.toByteArray();
    }

}
