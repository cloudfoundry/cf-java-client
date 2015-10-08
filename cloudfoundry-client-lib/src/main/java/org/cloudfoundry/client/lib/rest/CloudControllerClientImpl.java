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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipFile;

import javax.websocket.ClientEndpointConfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cloudfoundry.client.lib.ApplicationLogListener;
import org.cloudfoundry.client.lib.ClientHttpResponseCallback;
import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.CloudOperationException;
import org.cloudfoundry.client.lib.RestLogCallback;
import org.cloudfoundry.client.lib.StartingInfo;
import org.cloudfoundry.client.lib.StreamingLogToken;
import org.cloudfoundry.client.lib.UploadStatusCallback;
import org.cloudfoundry.client.lib.archive.ApplicationArchive;
import org.cloudfoundry.client.lib.archive.DirectoryApplicationArchive;
import org.cloudfoundry.client.lib.archive.ZipApplicationArchive;
import org.cloudfoundry.client.lib.domain.ApplicationLog;
import org.cloudfoundry.client.lib.domain.ApplicationLogs;
import org.cloudfoundry.client.lib.domain.ApplicationStats;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudDomain;
import org.cloudfoundry.client.lib.domain.CloudEvent;
import org.cloudfoundry.client.lib.domain.CloudInfo;
import org.cloudfoundry.client.lib.domain.CloudJob;
import org.cloudfoundry.client.lib.domain.CloudOrganization;
import org.cloudfoundry.client.lib.domain.CloudQuota;
import org.cloudfoundry.client.lib.domain.CloudResource;
import org.cloudfoundry.client.lib.domain.CloudResources;
import org.cloudfoundry.client.lib.domain.CloudRoute;
import org.cloudfoundry.client.lib.domain.CloudSecurityGroup;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.CloudServiceBroker;
import org.cloudfoundry.client.lib.domain.CloudServiceInstance;
import org.cloudfoundry.client.lib.domain.CloudServiceOffering;
import org.cloudfoundry.client.lib.domain.CloudServicePlan;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.cloudfoundry.client.lib.domain.CloudStack;
import org.cloudfoundry.client.lib.domain.CrashInfo;
import org.cloudfoundry.client.lib.domain.CrashesInfo;
import org.cloudfoundry.client.lib.domain.InstanceState;
import org.cloudfoundry.client.lib.domain.InstanceStats;
import org.cloudfoundry.client.lib.domain.InstancesInfo;
import org.cloudfoundry.client.lib.domain.SecurityGroupRule;
import org.cloudfoundry.client.lib.domain.Staging;
import org.cloudfoundry.client.lib.domain.UploadApplicationPayload;
import org.cloudfoundry.client.lib.domain.CloudUser;
import org.cloudfoundry.client.lib.oauth2.OauthClient;
import org.cloudfoundry.client.lib.util.CloudEntityResourceMapper;
import org.cloudfoundry.client.lib.util.CloudUtil;
import org.cloudfoundry.client.lib.util.JsonUtil;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

/**
 * Abstract implementation of the CloudControllerClient intended to serve as the base.
 *
 * @author Ramnivas Laddad
 * @author A.B.Srinivasan
 * @author Jennifer Hickey
 * @author Dave Syer
 * @author Thomas Risberg
 * @author Alexander Orlov
 * @author Scott Frederick
 */
public class CloudControllerClientImpl implements CloudControllerClient {

	private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
	private static final String PROXY_USER_HEADER_KEY = "Proxy-User";

	private static final String LOGS_LOCATION = "logs";
	private static final long JOB_POLLING_PERIOD = TimeUnit.SECONDS.toMillis(5);
	private static final long JOB_TIMEOUT = TimeUnit.MINUTES.toMillis(3);

	private OauthClient oauthClient;

	private CloudSpace sessionSpace;

	private CloudEntityResourceMapper resourceMapper = new CloudEntityResourceMapper();

	private RestTemplate restTemplate;

	private URL cloudControllerUrl;

	private LoggregatorClient loggregatorClient;

	protected CloudCredentials cloudCredentials;

	private final Log logger;

	/**
	 * Only for unit tests. This works around the fact that the initialize method is called within the constructor and
	 * hence can not be overloaded, making it impossible to write unit tests that don't trigger network calls.
	 */
	protected CloudControllerClientImpl() {
		logger = LogFactory.getLog(getClass().getName());
	}

	public CloudControllerClientImpl(URL cloudControllerUrl, RestTemplate restTemplate,
	                                 OauthClient oauthClient, LoggregatorClient loggregatorClient,
	                                 CloudCredentials cloudCredentials, CloudSpace sessionSpace) {
		logger = LogFactory.getLog(getClass().getName());

		initialize(cloudControllerUrl, restTemplate, oauthClient, loggregatorClient, cloudCredentials);

		this.sessionSpace = sessionSpace;
	}

	public CloudControllerClientImpl(URL cloudControllerUrl, RestTemplate restTemplate,
	                                 OauthClient oauthClient, LoggregatorClient loggregatorClient,
	                                 CloudCredentials cloudCredentials, String orgName, String spaceName) {
		logger = LogFactory.getLog(getClass().getName());
		CloudControllerClientImpl tempClient =
				new CloudControllerClientImpl(cloudControllerUrl, restTemplate,
						oauthClient, loggregatorClient, cloudCredentials, null);

		initialize(cloudControllerUrl, restTemplate, oauthClient, loggregatorClient, cloudCredentials);

		this.sessionSpace = validateSpaceAndOrg(spaceName, orgName, tempClient);
	}

	private void initialize(URL cloudControllerUrl, RestTemplate restTemplate, OauthClient oauthClient,
	                        LoggregatorClient loggregatorClient, CloudCredentials cloudCredentials) {
		Assert.notNull(cloudControllerUrl, "CloudControllerUrl cannot be null");
		Assert.notNull(restTemplate, "RestTemplate cannot be null");
		Assert.notNull(oauthClient, "OauthClient cannot be null");

		oauthClient.init(cloudCredentials);

		this.cloudCredentials = cloudCredentials;

		this.cloudControllerUrl = cloudControllerUrl;

		this.restTemplate = restTemplate;
		configureCloudFoundryRequestFactory(restTemplate);

		this.oauthClient = oauthClient;

		this.loggregatorClient = loggregatorClient;
	}

	private CloudSpace validateSpaceAndOrg(String spaceName, String orgName, CloudControllerClientImpl client) {
		List<CloudSpace> spaces = client.getSpaces();

		for (CloudSpace space : spaces) {
			if (space.getName().equals(spaceName)) {
				CloudOrganization org = space.getOrganization();
				if (orgName == null || org.getName().equals(orgName)) {
					return space;
				}
			}
		}

		throw new IllegalArgumentException("No matching organization and space found for org: " + orgName + " space: " + spaceName);
	}

	@Override
	public void setResponseErrorHandler(ResponseErrorHandler errorHandler) {
		this.restTemplate.setErrorHandler(errorHandler);
	}

	@Override
	public URL getCloudControllerUrl() {
		return this.cloudControllerUrl;
	}

	@Override
	public void updatePassword(String newPassword) {
		updatePassword(cloudCredentials, newPassword);
	}

	@Override
	public Map<String, String> getLogs(String appName) {
		String urlPath = getFileUrlPath();
		String instance = String.valueOf(0);
		return doGetLogs(urlPath, appName, instance);
	}

	@Override
	public List<ApplicationLog> getRecentLogs(String appName) {
		UUID appId = getAppId(appName);

		String endpoint = getInfo().getLoggregatorEndpoint();
		String uri = loggregatorClient.getRecentHttpEndpoint(endpoint);

		ApplicationLogs logs = getRestTemplate().getForObject(uri + "?app={guid}", ApplicationLogs.class, appId);

		Collections.sort(logs);

		return logs;
	}

	@Override
	public StreamingLogToken streamLogs(String appName, ApplicationLogListener listener) {
		return streamLoggregatorLogs(appName, listener, false);
	}

	@Override
	public Map<String, String> getCrashLogs(String appName) {
		String urlPath = getFileUrlPath();
		CrashesInfo crashes = getCrashes(appName);
		if (crashes.getCrashes().isEmpty()) {
			return Collections.emptyMap();
		}
		TreeMap<Date, String> crashInstances = new TreeMap<Date, String>();
		for (CrashInfo crash : crashes.getCrashes()) {
			crashInstances.put(crash.getSince(), crash.getInstance());
		}
		String instance = crashInstances.get(crashInstances.lastKey());
		return doGetLogs(urlPath, appName, instance);
	}

	@Override
	public String getFile(String appName, int instanceIndex, String filePath, int startPosition, int endPosition) {
		String urlPath = getFileUrlPath();
		Object appId = getFileAppId(appName);
		return doGetFile(urlPath, appId, instanceIndex, filePath, startPosition, endPosition);
	}


	@Override
	public void openFile(String appName, int instanceIndex, String filePath, ClientHttpResponseCallback callback) {
		String urlPath = getFileUrlPath();
		Object appId = getFileAppId(appName);
		doOpenFile(urlPath, appId, instanceIndex, filePath, callback);
	}

	@Override
	public void registerRestLogListener(RestLogCallback callBack) {
		if (getRestTemplate() instanceof LoggingRestTemplate) {
			((LoggingRestTemplate)getRestTemplate()).registerRestLogListener(callBack);
		}
	}

	@Override
	public void unRegisterRestLogListener(RestLogCallback callBack) {
		if (getRestTemplate() instanceof LoggingRestTemplate) {
			((LoggingRestTemplate)getRestTemplate()).unRegisterRestLogListener(callBack);
		}
	}

	/**
	 * Returns null if no further content is available. Two errors that will
	 * lead to a null value are 404 Bad Request errors, which are handled in the
	 * implementation, meaning that no further log file contents are available,
	 * or ResourceAccessException, also handled in the implementation,
	 * indicating a possible timeout in the server serving the content. Note
	 * that any other CloudFoundryException or RestClientException exception not
	 * related to the two errors mentioned above may still be thrown (e.g. 500
	 * level errors, Unauthorized or Forbidden exceptions, etc..)
	 *
	 * @return content if available, which may contain multiple lines, or null
	 *         if no further content is available.
	 *
	 */
	@Override
	public String getStagingLogs(StartingInfo info, int offset) {
		String stagingFile = info.getStagingFile();
		if (stagingFile != null) {
			CloudFoundryClientHttpRequestFactory cfRequestFactory = null;
			try {
				HashMap<String, Object> logsRequest = new HashMap<String, Object>();
				logsRequest.put("offset", offset);

				cfRequestFactory = getRestTemplate().getRequestFactory() instanceof CloudFoundryClientHttpRequestFactory ? (CloudFoundryClientHttpRequestFactory) getRestTemplate()
						.getRequestFactory() : null;
				if (cfRequestFactory != null) {
					cfRequestFactory
							.increaseReadTimeoutForStreamedTailedLogs(5 * 60 * 1000);
				}
				return getRestTemplate().getForObject(
						stagingFile + "&tail&tail_offset={offset}",
						String.class, logsRequest);
			} catch (CloudFoundryException e) {
				if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
					// Content is no longer available
					return null;
				} else {
					throw e;
				}
			} catch (ResourceAccessException e) {
				// Likely read timeout, the directory server won't serve
				// the content again
				logger.debug("Caught exception while fetching staging logs. Aborting. Caught:" + e,
						e);
			} finally {
				if (cfRequestFactory != null) {
					cfRequestFactory
							.increaseReadTimeoutForStreamedTailedLogs(-1);
				}
			}
		}
		return null;
	}

	protected RestTemplate getRestTemplate() {
		return this.restTemplate;
	}

	protected String getUrl(String path) {
		return cloudControllerUrl + (path.startsWith("/") ? path : "/" + path);
	}

	protected void configureCloudFoundryRequestFactory(RestTemplate restTemplate) {
		ClientHttpRequestFactory requestFactory = restTemplate.getRequestFactory();
		if (!(requestFactory instanceof CloudFoundryClientHttpRequestFactory)) {
			restTemplate.setRequestFactory(
					new CloudFoundryClientHttpRequestFactory(requestFactory));
		}
	}

	private class CloudFoundryClientHttpRequestFactory implements ClientHttpRequestFactory {

		private ClientHttpRequestFactory delegate;
		private Integer defaultSocketTimeout = 0;

		public CloudFoundryClientHttpRequestFactory(ClientHttpRequestFactory delegate) {
			this.delegate = delegate;
			captureDefaultReadTimeout();
		}

		@Override
		public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
			ClientHttpRequest request = delegate.createRequest(uri, httpMethod);

			String authorizationHeader = oauthClient.getAuthorizationHeader();
			if (authorizationHeader != null) {
				request.getHeaders().add(AUTHORIZATION_HEADER_KEY, authorizationHeader);
			}

			if (cloudCredentials != null && cloudCredentials.getProxyUser() != null) {
				request.getHeaders().add(PROXY_USER_HEADER_KEY, cloudCredentials.getProxyUser());
			}

			return request;
		}

        private void captureDefaultReadTimeout() {
            // As of HttpClient 4.3.x, obtaining the default parameters is deprecated and removed,
            // so we fallback to java.net.Socket.

            if (defaultSocketTimeout == null) {
                try {
                    defaultSocketTimeout = new Socket().getSoTimeout();
                } catch (SocketException e) {
                    defaultSocketTimeout = 0;
                }
            }
        }

		public void increaseReadTimeoutForStreamedTailedLogs(int timeout) {
			// May temporary increase read timeout on other unrelated concurrent
			// threads, but per-request read timeout don't seem easily
			// accessible
			if (delegate instanceof HttpComponentsClientHttpRequestFactory) {
				HttpComponentsClientHttpRequestFactory httpRequestFactory =
						(HttpComponentsClientHttpRequestFactory) delegate;

				if (timeout > 0) {
					httpRequestFactory.setReadTimeout(timeout);
				} else {
					httpRequestFactory
							.setReadTimeout(defaultSocketTimeout);
				}
			}
		}
	}

	protected Map<String, String> doGetLogs(String urlPath, String appName, String instance) {
		Object appId = getFileAppId(appName);
		String logFiles = doGetFile(urlPath, appId, instance, LOGS_LOCATION, -1, -1);
		String[] lines = logFiles.split("\n");
		List<String> fileNames = new ArrayList<String>();
		for (String line : lines) {
			String[] parts = line.split("\\s");
			if (parts.length > 0 && parts[0] != null) {
				fileNames.add(parts[0]);
			}
		}
		Map<String, String> logs = new HashMap<String, String>(fileNames.size());
		for(String fileName : fileNames) {
			String logFile = LOGS_LOCATION + "/" + fileName;
			logs.put(logFile, doGetFile(urlPath, appId, instance, logFile, -1, -1));
		}
		return logs;
	}

	@SuppressWarnings("unchecked")
	protected void doOpenFile(String urlPath, Object app, int instanceIndex, String filePath,
			ClientHttpResponseCallback callback) {
		getRestTemplate().execute(getUrl(urlPath), HttpMethod.GET, null, new ResponseExtractorWrapper(callback), app,
				String.valueOf(instanceIndex), filePath);
	}

	protected String doGetFile(String urlPath, Object app, int instanceIndex, String filePath, int startPosition, int endPosition) {
		return doGetFile(urlPath, app, String.valueOf(instanceIndex), filePath, startPosition, endPosition);
	}

	protected String doGetFile(String urlPath, Object app, String instance, String filePath, int startPosition, int endPosition) {
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

		return doGetFileByRange(urlPath, app, instance, filePath, start, end, range);
	}

	private String doGetFileByRange(String urlPath, Object app, String instance, String filePath, int start, int end,
									String range) {

		boolean supportsRanges;
		try {
			supportsRanges = getRestTemplate().execute(getUrl(urlPath),
					HttpMethod.HEAD,
					new RequestCallback() {
						public void doWithRequest(ClientHttpRequest request) throws IOException {
							request.getHeaders().set("Range", "bytes=0-");
						}
					},
					new ResponseExtractor<Boolean>() {
						public Boolean extractData(ClientHttpResponse response) throws IOException {
							return response.getStatusCode().equals(HttpStatus.PARTIAL_CONTENT);
						}
					},
					app, instance, filePath);
		} catch (CloudFoundryException e) {
			if (e.getStatusCode().equals(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)) {
				// must be a 0 byte file
				return "";
			} else {
				throw e;
			}
		}
		HttpHeaders headers = new HttpHeaders();
		if (supportsRanges) {
			headers.set("Range", range);
		}
		HttpEntity<Object> requestEntity = new HttpEntity<Object>(headers);
		ResponseEntity<String> responseEntity = getRestTemplate().exchange(getUrl(urlPath),
				HttpMethod.GET, requestEntity, String.class, app, instance, filePath);
		String response = responseEntity.getBody();
		boolean partialFile = false;
		if (responseEntity.getStatusCode().equals(HttpStatus.PARTIAL_CONTENT)) {
			partialFile = true;
		}
		if (!partialFile && response != null) {
			if (start == -1) {
				return response.substring(response.length() - end);
			} else {
				if (start >= response.length()) {
					if (response.length() == 0) {
						return "";
					}
					throw new CloudFoundryException(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE,
							"The starting position " + start + " is past the end of the file content.");
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

	@SuppressWarnings("unchecked")
	@Override
	public CloudInfo getInfo() {
		// info comes from two end points: /info and /v2/info

		String infoV2Json = getRestTemplate().getForObject(getUrl("/v2/info"), String.class);
		Map<String, Object> infoV2Map = JsonUtil.convertJsonToMap(infoV2Json);

		Map<String, Object> userMap = getUserInfo((String) infoV2Map.get("user"));

		String infoJson = getRestTemplate().getForObject(getUrl("/info"), String.class);
		Map<String, Object> infoMap = JsonUtil.convertJsonToMap(infoJson);
		Map<String, Object> limitMap = (Map<String, Object>) infoMap.get("limits");
		Map<String, Object> usageMap = (Map<String, Object>) infoMap.get("usage");

		String name = CloudUtil.parse(String.class, infoV2Map.get("name"));
		String support = CloudUtil.parse(String.class, infoV2Map.get("support"));
		String authorizationEndpoint = CloudUtil.parse(String.class, infoV2Map.get("authorization_endpoint"));
		String build = CloudUtil.parse(String.class, infoV2Map.get("build"));
		String version = "" + CloudUtil.parse(Number.class, infoV2Map.get("version"));
		String description = CloudUtil.parse(String.class, infoV2Map.get("description"));

		CloudInfo.Limits limits = null;
		CloudInfo.Usage usage = null;
		boolean debug = false;
		if (oauthClient.getToken() != null) {
			limits = new CloudInfo.Limits(limitMap);
			usage = new CloudInfo.Usage(usageMap);
			debug = CloudUtil.parse(Boolean.class, infoMap.get("allow_debug"));
		}

		String loggregatorEndpoint = CloudUtil.parse(String.class, infoV2Map.get("logging_endpoint"));

		return new CloudInfo(name, support, authorizationEndpoint, build, version, (String)userMap.get("user_name"),
				description, limits, usage, debug, loggregatorEndpoint);
	}

	@Override
	public void createSpace(String spaceName) {
		assertSpaceProvided("create a new space");
		UUID orgGuid = sessionSpace.getOrganization().getMeta().getGuid();
		UUID spaceGuid = getSpaceGuid(spaceName, orgGuid);
		if (spaceGuid == null) {
			doCreateSpace(spaceName, orgGuid);
		}
	}

	@Override
	public CloudSpace getSpace(String spaceName) {
		String urlPath = "/v2/spaces?inline-relations-depth=1&q=name:{name}";
		HashMap<String, Object> spaceRequest = new HashMap<String, Object>();
		spaceRequest.put("name", spaceName);
		List<Map<String, Object>> resourceList = getAllResources(urlPath, spaceRequest);
		CloudSpace space = null;
		if (resourceList.size() > 0) {
			Map<String, Object> resource = resourceList.get(0);
			space = resourceMapper.mapResource(resource, CloudSpace.class);
		}
		return space;
	}

	@Override
	public void deleteSpace(String spaceName) {
		assertSpaceProvided("delete a space");
		UUID orgGuid = sessionSpace.getOrganization().getMeta().getGuid();
		UUID spaceGuid = getSpaceGuid(spaceName, orgGuid);
		if (spaceGuid != null) {
			doDeleteSpace(spaceGuid);
		}
	}

	private UUID doCreateSpace(String spaceName, UUID orgGuid) {
		String urlPath = "/v2/spaces";
		HashMap<String, Object> spaceRequest = new HashMap<String, Object>();
		spaceRequest.put("organization_guid", orgGuid);
		spaceRequest.put("name", spaceName);
		String resp = getRestTemplate().postForObject(getUrl(urlPath), spaceRequest, String.class);
		Map<String, Object> respMap = JsonUtil.convertJsonToMap(resp);
		return resourceMapper.getGuidOfResource(respMap);
	}

	private UUID getSpaceGuid(String spaceName, UUID orgGuid) {
		Map<String, Object> urlVars = new HashMap<String, Object>();
		String urlPath = "/v2/organizations/{orgGuid}/spaces?inline-relations-depth=1&q=name:{name}";
		urlVars.put("orgGuid", orgGuid);
		urlVars.put("name", spaceName);
		List<Map<String, Object>> resourceList = getAllResources(urlPath, urlVars);
		if (resourceList.size() > 0) {
			Map<String, Object> resource = resourceList.get(0);
			return resourceMapper.getGuidOfResource(resource);
		}
		return null;
	}

	private UUID getSpaceGuid(String orgName, String spaceName) {
		CloudOrganization org = getOrgByName(orgName, true);
		return getSpaceGuid(spaceName, org.getMeta().getGuid());
	}

	private void doDeleteSpace(UUID spaceGuid) {
		getRestTemplate().delete(getUrl("/v2/spaces/{guid}?async=false"), spaceGuid);
	}

	@Override
	public List<CloudSpace> getSpaces() {
		String urlPath = "/v2/spaces?inline-relations-depth=1";
		List<Map<String, Object>> resourceList = getAllResources(urlPath, null);
		List<CloudSpace> spaces = new ArrayList<CloudSpace>();
		for (Map<String, Object> resource : resourceList) {
			spaces.add(resourceMapper.mapResource(resource, CloudSpace.class));
		}
		return spaces;
	}

	@Override
	public List<UUID> getSpaceManagers(String orgName, String spaceName) {
		String urlPath = "/v2/spaces/{guid}/managers";
		return getSpaceUserGuids(orgName, spaceName, urlPath);
	}

	@Override
	public List<UUID> getSpaceDevelopers(String orgName, String spaceName) {
		String urlPath = "/v2/spaces/{guid}/developers";
		return getSpaceUserGuids(orgName, spaceName, urlPath);
	}

	@Override
	public List<UUID> getSpaceAuditors(String orgName, String spaceName) {
		String urlPath = "/v2/spaces/{guid}/auditors";
		return getSpaceUserGuids(orgName, spaceName, urlPath);
	}

	private List<UUID> getSpaceUserGuids(String orgName, String spaceName, String urlPath) {
		if (orgName == null || spaceName == null) {
			assertSpaceProvided("get space users");
		}

		UUID spaceGuid;
		if (spaceName == null) {
			spaceGuid = sessionSpace.getMeta().getGuid();
		} else {
			CloudOrganization organization = (orgName == null ? sessionSpace.getOrganization() : getOrgByName(orgName, true));
			spaceGuid = getSpaceGuid(spaceName, organization.getMeta().getGuid());
		}

		Map<String, Object> urlVars = new HashMap<String, Object>();
		urlVars.put("guid", spaceGuid);

		List<UUID> managersGuid = new ArrayList<UUID>();
		List<Map<String, Object>> resourceList = getAllResources(urlPath, urlVars);
		for (Map<String, Object> resource : resourceList) {
			UUID userGuid = resourceMapper.getGuidOfResource(resource);
			managersGuid.add(userGuid);
		}
		return managersGuid;
	}

	@Override
	public void associateManagerWithSpace(String orgName, String spaceName, String userGuid) {
		String urlPath = "/v2/spaces/{guid}/managers/{userGuid}";
		associateRoleWithSpace(orgName, spaceName, userGuid, urlPath);
	}

	@Override
	public void associateDeveloperWithSpace(String orgName, String spaceName, String userGuid) {
		String urlPath = "/v2/spaces/{guid}/developers/{userGuid}";
		associateRoleWithSpace(orgName, spaceName, userGuid, urlPath);
	}

	@Override
	public void associateAuditorWithSpace(String orgName, String spaceName, String userGuid) {
		String urlPath = "/v2/spaces/{guid}/auditors/{userGuid}";
		associateRoleWithSpace(orgName, spaceName, userGuid, urlPath);
	}

	private void associateRoleWithSpace(String orgName, String spaceName, String userGuid, String urlPath) {
		assertSpaceProvided("associate roles");

		CloudOrganization organization = (orgName == null ? sessionSpace.getOrganization() : getOrgByName(orgName, true));
		UUID orgGuid = organization.getMeta().getGuid();

		UUID spaceGuid = getSpaceGuid(spaceName, orgGuid);
		HashMap<String, Object> spaceRequest = new HashMap<String, Object>();
		spaceRequest.put("guid", spaceGuid);

		String userId = (userGuid==null?getCurrentUserId():userGuid);

		getRestTemplate().put(getUrl(urlPath), spaceRequest, spaceGuid, userId);
	}

	private String getCurrentUserId() {
		String username = getInfo().getUser();
		Map<String, Object> userMap = getUserInfo(username);
		String userId= (String) userMap.get("user_id");
		return userId;
	}

	@Override
	public Map<String, CloudUser> getOrganizationUsers(String orgName) {
		String urlPath = "/v2/organizations/{guid}/users";
		CloudOrganization organization = getOrgByName(orgName, true);

		UUID orgGuid=organization.getMeta().getGuid();
		Map<String, Object> urlVars = new HashMap<String, Object>();
		urlVars.put("guid", orgGuid);

		List<Map<String, Object>> resourceList = getAllResources(urlPath, urlVars);
		Map<String,CloudUser> orgUsers = new HashMap<String,CloudUser>();
		for (Map<String, Object> resource : resourceList) {
			CloudUser user = resourceMapper.mapResource(resource, CloudUser.class);
			orgUsers.put(user.getUsername(),user);
		}
		return orgUsers;
	}

	@Override
	public void associateUserWithOrg(String orgName, String username) {
		CloudOrganization organization = getOrgByName(orgName, true);
		String orgUrl=organization.getMeta().getUrl()+"/users";

		HashMap<String, Object> orgRequest = new HashMap<String, Object>();
		orgRequest.put("username", username);
		getRestTemplate().put(getUrl(orgUrl), orgRequest);
	}

	@Override
	public List<CloudOrganization> getOrganizations() {
		String urlPath = "/v2/organizations?inline-relations-depth=0";
		List<Map<String, Object>> resourceList = getAllResources(urlPath, null);
		List<CloudOrganization> orgs = new ArrayList<CloudOrganization>();
		for (Map<String, Object> resource : resourceList) {
			orgs.add(resourceMapper.mapResource(resource, CloudOrganization.class));
		}
		return orgs;
	}

	@Override
	public OAuth2AccessToken login() {
		oauthClient.init(cloudCredentials);
		return oauthClient.getToken();
	}

	@Override
	public void logout() {
		oauthClient.clear();
	}

	@Override
	public void register(String email, String password) {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	@Override
	public void updatePassword(CloudCredentials credentials, String newPassword) {
		oauthClient.changePassword(credentials.getPassword(), newPassword);
		CloudCredentials newCloudCredentials = new CloudCredentials(credentials.getEmail(), newPassword);
		if (cloudCredentials.getProxyUser() != null) {
			cloudCredentials = newCloudCredentials.proxyForUser(cloudCredentials.getProxyUser());
		} else {
			cloudCredentials = newCloudCredentials;
		}
	}

	@Override
	public void unregister() {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	@Override
	public List<CloudService> getServices() {
		Map<String, Object> urlVars = new HashMap<String, Object>();
		String urlPath = "/v2";
		if (sessionSpace != null) {
			urlVars.put("space", sessionSpace.getMeta().getGuid());
			urlPath = urlPath + "/spaces/{space}";
		}
		urlPath = urlPath + "/service_instances?inline-relations-depth=1&return_user_provided_service_instances=true";
		List<Map<String, Object>> resourceList = getAllResources(urlPath, urlVars);
		List<CloudService> services = new ArrayList<CloudService>();
		for (Map<String, Object> resource : resourceList) {
			if (hasEmbeddedResource(resource, "service_plan")) {
				fillInEmbeddedResource(resource, "service_plan", "service");
			}
			services.add(resourceMapper.mapResource(resource, CloudService.class));
		}
		return services;
	}

	@Override
	public void createService(CloudService service) {
		assertSpaceProvided("create service");
		Assert.notNull(service, "Service must not be null");
		Assert.notNull(service.getName(), "Service name must not be null");
		Assert.notNull(service.getLabel(), "Service label must not be null");
		Assert.notNull(service.getPlan(), "Service plan must not be null");

		CloudServicePlan cloudServicePlan = findPlanForService(service);

		HashMap<String, Object> serviceRequest = new HashMap<String, Object>();
		serviceRequest.put("space_guid", sessionSpace.getMeta().getGuid());
		serviceRequest.put("name", service.getName());
		serviceRequest.put("service_plan_guid", cloudServicePlan.getMeta().getGuid());
		getRestTemplate().postForObject(getUrl("/v2/service_instances"), serviceRequest, String.class);
	}

	private CloudServicePlan findPlanForService(CloudService service) {
		List<CloudServiceOffering> offerings = getServiceOfferings(service.getLabel());
		for (CloudServiceOffering offering : offerings) {
			if (service.getVersion() == null || service.getVersion().equals(offering.getVersion())) {
				for (CloudServicePlan plan : offering.getCloudServicePlans()) {
					if (service.getPlan() != null && service.getPlan().equals(plan.getName())) {
						return plan;
					}
				}
			}
		}
		throw new IllegalArgumentException("Service plan " + service.getPlan() + " not found");
	}

	@Override
	public void createUserProvidedService(CloudService service, Map<String, Object> credentials) {
		createUserProvidedServiceDelegate(service, credentials, "");
	}

	@Override
	public void createUserProvidedService(CloudService service, Map<String, Object> credentials, String syslogDrainUrl) {
		createUserProvidedServiceDelegate(service, credentials, syslogDrainUrl);
	}

	private void createUserProvidedServiceDelegate(CloudService service, Map<String, Object> credentials, String syslogDrainUrl) {
		assertSpaceProvided("create service");
		Assert.notNull(credentials, "Service credentials must not be null");
		Assert.notNull(service, "Service must not be null");
		Assert.notNull(service.getName(), "Service name must not be null");
		Assert.isNull(service.getLabel(), "Service label is not valid for user-provided services");
		Assert.isNull(service.getProvider(), "Service provider is not valid for user-provided services");
		Assert.isNull(service.getVersion(), "Service version is not valid for user-provided services");
		Assert.isNull(service.getPlan(), "Service plan is not valid for user-provided services");

		HashMap<String, Object> serviceRequest = new HashMap<>();
		serviceRequest.put("space_guid", sessionSpace.getMeta().getGuid());
		serviceRequest.put("name", service.getName());
		serviceRequest.put("credentials", credentials);
		if (syslogDrainUrl != null && !syslogDrainUrl.equals("")) {
			serviceRequest.put("syslog_drain_url", syslogDrainUrl);
		}

		getRestTemplate().postForObject(getUrl("/v2/user_provided_service_instances"), serviceRequest, String.class);
	}

	@Override
	public CloudService getService(String serviceName) {
		Map<String, Object> resource = doGetServiceInstance(serviceName, 0);

		if (resource == null) {
			return null;
		}

		return resourceMapper.mapResource(resource, CloudService.class);
	}

	@Override
	public CloudServiceInstance getServiceInstance(String serviceName) {
		Map<String, Object> resource = doGetServiceInstance(serviceName, 1);

		if (resource == null) {
			return null;
		}

		return resourceMapper.mapResource(resource, CloudServiceInstance.class);
	}

	private Map<String, Object> doGetServiceInstance(String serviceName, int inlineDepth) {
		String urlPath = "/v2";
		Map<String, Object> urlVars = new HashMap<String, Object>();
		if (sessionSpace != null) {
			urlVars.put("space", sessionSpace.getMeta().getGuid());
			urlPath = urlPath + "/spaces/{space}";
		}
		urlVars.put("q", "name:" + serviceName);
		urlPath = urlPath + "/service_instances?q={q}&return_user_provided_service_instances=true";
		if (inlineDepth > 0) {
			urlPath = urlPath + "&inline-relations-depth=" + inlineDepth;
		}

		List<Map<String, Object>> resources = getAllResources(urlPath, urlVars);

		if (resources.size() > 0) {
			Map<String, Object> serviceResource = resources.get(0);
			if (hasEmbeddedResource(serviceResource, "service_plan")) {
				fillInEmbeddedResource(serviceResource, "service_plan", "service");
			}
			return serviceResource;
		}
		return null;
	}

	@Override
	public void deleteService(String serviceName) {
		CloudService cloudService = getService(serviceName);
		doDeleteService(cloudService);
	}

	@Override
	public void deleteAllServices() {
		List<CloudService> cloudServices = getServices();
		for (CloudService cloudService : cloudServices) {
			doDeleteService(cloudService);
		}
	}

	@Override
	public List<CloudServiceOffering> getServiceOfferings() {
		String urlPath = "/v2/services?inline-relations-depth=1";
		List<Map<String, Object>> resourceList = getAllResources(urlPath, null);
		List<CloudServiceOffering> serviceOfferings = new ArrayList<CloudServiceOffering>();
		for (Map<String, Object> resource : resourceList) {
			CloudServiceOffering serviceOffering = resourceMapper.mapResource(resource, CloudServiceOffering.class);
			serviceOfferings.add(serviceOffering);
		}
		return serviceOfferings;
	}

	@Override
	public List<CloudServiceBroker> getServiceBrokers() {
		String urlPath = "/v2/service_brokers?inline-relations-depth=1";
		List<Map<String, Object>> resourceList = getAllResources(urlPath, null);
		List<CloudServiceBroker> serviceBrokers = new ArrayList<CloudServiceBroker>();
		for (Map<String, Object> resource : resourceList) {
			CloudServiceBroker broker = resourceMapper.mapResource(resource, CloudServiceBroker.class);
			serviceBrokers.add(broker);
		}
		return serviceBrokers;
	}

	@Override
	public CloudServiceBroker getServiceBroker(String name) {
		String urlPath = "/v2/service_brokers?q={q}";
		Map<String, Object> urlVars = new HashMap<>();
		urlVars.put("q", "name:" + name);
		List<Map<String, Object>> resourceList = getAllResources(urlPath, urlVars);
		CloudServiceBroker serviceBroker = null;
		if (resourceList.size() > 0) {
			final Map<String, Object> resource = resourceList.get(0);
			serviceBroker = resourceMapper.mapResource(resource, CloudServiceBroker.class);
		}
		return serviceBroker;
	}

	@Override
	public void createServiceBroker(CloudServiceBroker serviceBroker) {
		Assert.notNull(serviceBroker, "Service Broker must not be null");
		Assert.notNull(serviceBroker.getName(), "Service Broker name must not be null");
		Assert.notNull(serviceBroker.getUrl(), "Service Broker URL must not be null");
		Assert.notNull(serviceBroker.getUsername(), "Service Broker username must not be null");
		Assert.notNull(serviceBroker.getPassword(), "Service Broker password must not be null");

		HashMap<String, Object> serviceRequest = new HashMap<>();
		serviceRequest.put("name", serviceBroker.getName());
		serviceRequest.put("broker_url", serviceBroker.getUrl());
		serviceRequest.put("auth_username", serviceBroker.getUsername());
		serviceRequest.put("auth_password", serviceBroker.getPassword());
		getRestTemplate().postForObject(getUrl("/v2/service_brokers"), serviceRequest, String.class);
	}

	@Override
	public void updateServiceBroker(CloudServiceBroker serviceBroker) {
		Assert.notNull(serviceBroker, "Service Broker must not be null");
		Assert.notNull(serviceBroker.getName(), "Service Broker name must not be null");
		Assert.notNull(serviceBroker.getUrl(), "Service Broker URL must not be null");
		Assert.notNull(serviceBroker.getUsername(), "Service Broker username must not be null");
		Assert.notNull(serviceBroker.getPassword(), "Service Broker password must not be null");

		CloudServiceBroker existingBroker = getServiceBroker(serviceBroker.getName());
		Assert.notNull(existingBroker, "Cannot update broker if it does not first exist");

		HashMap<String, Object> serviceRequest = new HashMap<>();
		serviceRequest.put("name", serviceBroker.getName());
		serviceRequest.put("broker_url", serviceBroker.getUrl());
		serviceRequest.put("auth_username", serviceBroker.getUsername());
		serviceRequest.put("auth_password", serviceBroker.getPassword());
		getRestTemplate().put(getUrl("/v2/service_brokers/{guid}"), serviceRequest, existingBroker.getMeta().getGuid());
	}

	@Override
	public void deleteServiceBroker(String name) {
		CloudServiceBroker existingBroker = getServiceBroker(name);
		Assert.notNull(existingBroker, "Cannot update broker if it does not first exist");

		getRestTemplate().delete(getUrl("/v2/service_brokers/{guid}"), existingBroker.getMeta().getGuid());
	}

	@Override
	public void updateServicePlanVisibilityForBroker(String name, boolean visibility) {
		CloudServiceBroker broker = getServiceBroker(name);

		String urlPath = "/v2/services?q={q}";
		Map<String, Object> urlVars = new HashMap<>();
		urlVars.put("q", "service_broker_guid:" + broker.getMeta().getGuid());
		List<Map<String, Object>> serviceResourceList = getAllResources(urlPath, urlVars);

		for (Map<String, Object> serviceResource : serviceResourceList) {
			Map<String, Object> metadata = (Map<String, Object>) serviceResource.get("metadata");
			String serviceGuid = (String) metadata.get("guid");

			urlPath = "/v2/service_plans?q={q}";
			urlVars = new HashMap<>();
			urlVars.put("q", "service_guid:" + serviceGuid);
			List<Map<String, Object>> planResourceList = getAllResources(urlPath, urlVars);
			for (Map<String, Object> planResource : planResourceList) {
				metadata = (Map<String, Object>) planResource.get("metadata");
				String planGuid = (String) metadata.get("guid");

				HashMap<String, Object> planUpdateRequest = new HashMap<>();
				planUpdateRequest.put("public", visibility);
				getRestTemplate().put(getUrl("/v2/service_plans/{guid}"), planUpdateRequest, planGuid);
			}
		}
	}

	@Override
	public List<CloudApplication> getApplications() {
		Map<String, Object> urlVars = new HashMap<String, Object>();
		String urlPath = "/v2";
		if (sessionSpace != null) {
			urlVars.put("space", sessionSpace.getMeta().getGuid());
			urlPath = urlPath + "/spaces/{space}";
		}
		urlPath = urlPath + "/apps?inline-relations-depth=1";
		List<Map<String, Object>> resourceList = getAllResources(urlPath, urlVars);
		List<CloudApplication> apps = new ArrayList<CloudApplication>();
		for (Map<String, Object> resource : resourceList) {
			processApplicationResource(resource, true);
			apps.add(mapCloudApplication(resource));
		}
		return apps;
	}

	@Override
	public CloudApplication getApplication(String appName) {
		Map<String, Object> resource = findApplicationResource(appName, true);
		if (resource == null) {
			throw new CloudFoundryException(HttpStatus.NOT_FOUND, "Not Found", "Application not found");
		}
		return mapCloudApplication(resource);
	}

	@Override
	public CloudApplication getApplication(UUID appGuid) {
		Map<String, Object> resource = findApplicationResource(appGuid, true);
		if (resource == null) {
			throw new CloudFoundryException(HttpStatus.NOT_FOUND, "Not Found", "Application not found");
		}
		return mapCloudApplication(resource);
	}

	@Override
	public Map<String, Object> getApplicationEnvironment(UUID appGuid) {
    	String url = getUrl("/v2/apps/{guid}/env");
		Map<String, Object> urlVars = new HashMap();
		urlVars.put("guid", appGuid);
		String resp = restTemplate.getForObject(url, String.class, urlVars);
		return JsonUtil.convertJsonToMap(resp);
	}

	@Override
	public Map<String, Object> getApplicationEnvironment(String appName) {
		UUID appId = getAppId(appName);
		return getApplicationEnvironment(appId);
	}

	@SuppressWarnings("unchecked")
	private CloudApplication mapCloudApplication(Map<String, Object> resource) {
		UUID appId = resourceMapper.getGuidOfResource(resource);
		CloudApplication cloudApp = null;
		if (resource != null) {
			int running = getRunningInstances(appId,
				CloudApplication.AppState.valueOf(
					CloudEntityResourceMapper.getEntityAttribute(resource, "state", String.class)));
			((Map<String, Object>)resource.get("entity")).put("running_instances", running);
			cloudApp = resourceMapper.mapResource(resource, CloudApplication.class);
			cloudApp.setUris(findApplicationUris(cloudApp.getMeta().getGuid()));
		}
		return cloudApp;
	}

	private int getRunningInstances(UUID appId, CloudApplication.AppState appState) {
		int running = 0;
		ApplicationStats appStats = doGetApplicationStats(appId, appState);
		if (appStats != null && appStats.getRecords() != null) {
			for (InstanceStats inst : appStats.getRecords()) {
				if (InstanceState.RUNNING == inst.getState()){
					running++;
				}
			}
		}
		return running;
	}

	@Override
	public ApplicationStats getApplicationStats(String appName) {
		CloudApplication app = getApplication(appName);
		return doGetApplicationStats(app.getMeta().getGuid(), app.getState());
	}

	@SuppressWarnings("unchecked")
	private ApplicationStats doGetApplicationStats(UUID appId, CloudApplication.AppState appState) {
		List<InstanceStats> instanceList = new ArrayList<InstanceStats>();
		if (appState.equals(CloudApplication.AppState.STARTED)) {
			Map<String, Object> respMap = getInstanceInfoForApp(appId, "stats");
			for (String instanceId : respMap.keySet()) {
				InstanceStats instanceStats =
						new InstanceStats(instanceId, (Map<String, Object>) respMap.get(instanceId));
				instanceList.add(instanceStats);
			}
		}
		return new ApplicationStats(instanceList);
	}

	private Map<String, Object> getInstanceInfoForApp(UUID appId, String path) {
		String url = getUrl("/v2/apps/{guid}/" + path);
		Map<String, Object> urlVars = new HashMap<String, Object>();
		urlVars.put("guid", appId);
		String resp = getRestTemplate().getForObject(url, String.class, urlVars);
		return JsonUtil.convertJsonToMap(resp);
	}

	@Override
	public void createApplication(String appName, Staging staging, Integer memory, List<String> uris,
	                              List<String> serviceNames) {
		createApplication(appName, staging, null, memory, uris, serviceNames);
	}

	@Override
	public void createApplication(String appName, Staging staging, Integer disk, Integer memory,
	                              List<String> uris, List<String> serviceNames) {
		HashMap<String, Object> appRequest = new HashMap<String, Object>();
		appRequest.put("space_guid", sessionSpace.getMeta().getGuid());
		appRequest.put("name", appName);
		appRequest.put("memory", memory);
		if (disk != null) {
			appRequest.put("disk_quota", disk);
		}
		appRequest.put("instances", 1);
		addStagingToRequest(staging, appRequest);
		appRequest.put("state", CloudApplication.AppState.STOPPED);

		String appResp = getRestTemplate().postForObject(getUrl("/v2/apps"), appRequest, String.class);
		Map<String, Object> appEntity = JsonUtil.convertJsonToMap(appResp);
		UUID newAppGuid = CloudEntityResourceMapper.getMeta(appEntity).getGuid();

		if (serviceNames != null && serviceNames.size() > 0) {
			updateApplicationServices(appName, serviceNames);
		}

		if (uris != null && uris.size() > 0) {
			addUris(uris, newAppGuid);
		}
	}

	private void addStagingToRequest(Staging staging, HashMap<String, Object> appRequest) {
		if (staging.getBuildpackUrl() != null) {
			appRequest.put("buildpack", staging.getBuildpackUrl());
		}
		if (staging.getCommand() != null) {
			appRequest.put("command", staging.getCommand());
		}
		if (staging.getStack() != null) {
			appRequest.put("stack_guid", getStack(staging.getStack()).getMeta().getGuid());
		}
		if (staging.getHealthCheckTimeout() != null) {
			appRequest.put("health_check_timeout", staging.getHealthCheckTimeout());
		}
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getAllResources(String urlPath, Map<String, Object> urlVars) {
		List<Map<String, Object>> allResources = new ArrayList<Map<String, Object>>();
		String resp;
		if (urlVars != null) {
			resp = getRestTemplate().getForObject(getUrl(urlPath), String.class, urlVars);
		} else {
			resp = getRestTemplate().getForObject(getUrl(urlPath), String.class);
		}
		Map<String, Object> respMap = JsonUtil.convertJsonToMap(resp);
		List<Map<String, Object>> newResources = (List<Map<String, Object>>) respMap.get("resources");
		if (newResources != null && newResources.size() > 0) {
			allResources.addAll(newResources);
		}
		String nextUrl = (String) respMap.get("next_url");
		while (nextUrl != null && nextUrl.length() > 0) {
			nextUrl = addPageOfResources(nextUrl, allResources);
		}
		return allResources;
	}

	@SuppressWarnings("unchecked")
	private String addPageOfResources(String nextUrl, List<Map<String, Object>> allResources) {
		String resp = getRestTemplate().getForObject(getUrl(nextUrl), String.class);
		Map<String, Object> respMap = JsonUtil.convertJsonToMap(resp);
		List<Map<String, Object>> newResources = (List<Map<String, Object>>) respMap.get("resources");
		if (newResources != null && newResources.size() > 0) {
			allResources.addAll(newResources);
		}
		return (String) respMap.get("next_url");
	}

	private void addUris(List<String> uris, UUID appGuid) {
		Map<String, UUID> domains = getDomainGuids();
		for (String uri : uris) {
			Map<String, String> uriInfo = new HashMap<String, String>(2);
			extractUriInfo(domains, uri, uriInfo);
			UUID domainGuid = domains.get(uriInfo.get("domainName"));
			bindRoute(uriInfo.get("host"), domainGuid, appGuid);
		}
	}

	private void removeUris(List<String> uris, UUID appGuid) {
		Map<String, UUID> domains = getDomainGuids();
		for (String uri : uris) {
			Map<String, String> uriInfo = new HashMap<String, String>(2);
			extractUriInfo(domains, uri, uriInfo);
			UUID domainGuid = domains.get(uriInfo.get("domainName"));
			unbindRoute(uriInfo.get("host"), domainGuid, appGuid);
		}
	}

	protected void extractUriInfo(Map<String, UUID> domains, String uri, Map<String, String> uriInfo) {
		URI newUri = URI.create(uri);
		String authority = newUri.getScheme() != null ? newUri.getAuthority(): newUri.getPath();
		for (String domain : domains.keySet()) {
			if (authority != null && authority.endsWith(domain)) {
				String previousDomain = uriInfo.get("domainName");
				if (previousDomain == null || domain.length() > previousDomain.length()) {
					//Favor most specific subdomains
					uriInfo.put("domainName", domain);
					if (domain.length() < authority.length()) {
						uriInfo.put("host", authority.substring(0, authority.indexOf(domain) - 1));
					} else if (domain.length() == authority.length()) {
						uriInfo.put("host", "");
					}
				}
			}
		}
		if (uriInfo.get("domainName") == null) {
			throw new IllegalArgumentException("Domain not found for URI " + uri);
		}
		if (uriInfo.get("host") == null) {
			throw new IllegalArgumentException("Invalid URI " + uri +
					" -- host not specified for domain " + uriInfo.get("domainName"));
		}
	}

	private Map<String, UUID> getDomainGuids() {
		Map<String, Object> urlVars = new HashMap<String, Object>();
		String urlPath = "/v2";
		if (sessionSpace != null) {
			urlVars.put("space", sessionSpace.getMeta().getGuid());
			urlPath = urlPath + "/spaces/{space}";
		}
		String domainPath = urlPath + "/domains?inline-relations-depth=1";
		List<Map<String, Object>> resourceList = getAllResources(domainPath, urlVars);
		Map<String, UUID> domains = new HashMap<String, UUID>(resourceList.size());
		for (Map<String, Object> d : resourceList) {
			domains.put(
					CloudEntityResourceMapper.getEntityAttribute(d, "name", String.class),
					CloudEntityResourceMapper.getMeta(d).getGuid());
		}
		return domains;
	}

	private UUID getDomainGuid(String domainName, boolean required) {
		Map<String, Object> urlVars = new HashMap<String, Object>();
		String urlPath = "/v2/domains?inline-relations-depth=1&q=name:{name}";
		urlVars.put("name", domainName);
		List<Map<String, Object>> resourceList = getAllResources(urlPath, urlVars);
		UUID domainGuid = null;
		if (resourceList.size() > 0) {
			Map<String, Object> resource = resourceList.get(0);
			domainGuid = resourceMapper.getGuidOfResource(resource);
		}
		if (domainGuid == null && required) {
			throw new IllegalArgumentException("Domain '" + domainName + "' not found.");
		}
		return domainGuid;
	}

	private void bindRoute(String host, UUID domainGuid, UUID appGuid) {
		UUID routeGuid = getRouteGuid(host, domainGuid);
		if (routeGuid == null) {
			routeGuid = doAddRoute(host, domainGuid);
		}
		String bindPath = "/v2/apps/{app}/routes/{route}";
		Map<String, Object> bindVars = new HashMap<String, Object>();
		bindVars.put("app", appGuid);
		bindVars.put("route", routeGuid);
		HashMap<String, Object> bindRequest = new HashMap<String, Object>();
		getRestTemplate().put(getUrl(bindPath), bindRequest, bindVars);
	}

	private void unbindRoute(String host, UUID domainGuid, UUID appGuid) {
		UUID routeGuid = getRouteGuid(host, domainGuid);
		if (routeGuid != null) {
			String bindPath = "/v2/apps/{app}/routes/{route}";
			Map<String, Object> bindVars = new HashMap<String, Object>();
			bindVars.put("app", appGuid);
			bindVars.put("route", routeGuid);
			getRestTemplate().delete(getUrl(bindPath), bindVars);
		}
	}

	private UUID getRouteGuid(String host, UUID domainGuid) {
		Map<String, Object> urlVars = new HashMap<String, Object>();
		String urlPath = "/v2";
		urlPath = urlPath + "/routes?inline-relations-depth=0&q=host:{host}";
		urlVars.put("host", host);
		List<Map<String, Object>> allRoutes = getAllResources(urlPath, urlVars);
		UUID routeGuid = null;
		for (Map<String, Object> route : allRoutes) {
			UUID routeSpace = CloudEntityResourceMapper.getEntityAttribute(route, "space_guid", UUID.class);
			UUID routeDomain = CloudEntityResourceMapper.getEntityAttribute(route, "domain_guid", UUID.class);
			if (sessionSpace.getMeta().getGuid().equals(routeSpace) &&
					domainGuid.equals(routeDomain)) {
				routeGuid = CloudEntityResourceMapper.getMeta(route).getGuid();
			}
		}
		return routeGuid;
	}

	private UUID doAddRoute(String host, UUID domainGuid) {
		assertSpaceProvided("add route");

		HashMap<String, Object> routeRequest = new HashMap<String, Object>();
		routeRequest.put("host", host);
		routeRequest.put("domain_guid", domainGuid);
		routeRequest.put("space_guid", sessionSpace.getMeta().getGuid());
		String routeResp = getRestTemplate().postForObject(getUrl("/v2/routes"), routeRequest, String.class);
		Map<String, Object> routeEntity = JsonUtil.convertJsonToMap(routeResp);
		return CloudEntityResourceMapper.getMeta(routeEntity).getGuid();
	}

	@Override
	public void uploadApplication(String appName, File file, UploadStatusCallback callback) throws IOException {
		Assert.notNull(file, "File must not be null");
		if (file.isDirectory()) {
			ApplicationArchive archive = new DirectoryApplicationArchive(file);
			uploadApplication(appName, archive, callback);
		} else {
			try (ZipFile zipFile = new ZipFile(file)) {
				ApplicationArchive archive = new ZipApplicationArchive(zipFile);
				uploadApplication(appName, archive, callback);
			}
		}
	}

	@Override
	public void uploadApplication(String appName, String fileName, InputStream inputStream, UploadStatusCallback callback) throws IOException {
		Assert.notNull(fileName, "FileName must not be null");
		Assert.notNull(inputStream, "InputStream must not be null");

		File file = createTemporaryUploadFile(inputStream);

		try (ZipFile zipFile = new ZipFile(file)) {
			ApplicationArchive archive = new ZipApplicationArchive(zipFile);
			uploadApplication(appName, archive, callback);
		}

		file.delete();
	}

	@Override
	public void uploadApplication(String appName, ApplicationArchive archive, UploadStatusCallback callback)
			throws IOException {
		Assert.notNull(appName, "AppName must not be null");
		Assert.notNull(archive, "Archive must not be null");
		UUID appId = getAppId(appName);

		if (callback == null) {
			callback = UploadStatusCallback.NONE;
		}
		CloudResources knownRemoteResources = getKnownRemoteResources(archive);
		callback.onCheckResources();
		callback.onMatchedFileNames(knownRemoteResources.getFilenames());
		UploadApplicationPayload payload = new UploadApplicationPayload(archive, knownRemoteResources);
		callback.onProcessMatchedResources(payload.getTotalUncompressedSize());
		HttpEntity<?> entity = generatePartialResourceRequest(payload, knownRemoteResources);
		ResponseEntity<Map<String, Object>> responseEntity =
			getRestTemplate().exchange(getUrl("/v2/apps/{guid}/bits?async=true"),
				HttpMethod.PUT, entity,
				new ParameterizedTypeReference<Map<String, Object>>() {}, appId);
		processAsyncJob(responseEntity.getBody(), callback);
	}

	private void processAsyncJob(Map<String, Object> jobResource, UploadStatusCallback callback) {
		CloudJob job = resourceMapper.mapResource(jobResource, CloudJob.class);
		do {
			boolean unsubscribe = callback.onProgress(job.getStatus().toString());
			if (unsubscribe) {
				return;
			}
			if (job.getStatus() == CloudJob.Status.FAILED) {
				return;
			}

			try {
				Thread.sleep(JOB_POLLING_PERIOD);
			} catch (InterruptedException ex) {
				return;
			}

			ResponseEntity<Map<String, Object>> jobProgressEntity =
					getRestTemplate().exchange(getUrl(job.getMeta().getUrl()),
						HttpMethod.GET, HttpEntity.EMPTY,
						new ParameterizedTypeReference<Map<String, Object>>() {});
			job = resourceMapper.mapResource(jobProgressEntity.getBody(), CloudJob.class);
		} while (job.getStatus() != CloudJob.Status.FINISHED);
	}

	private CloudResources getKnownRemoteResources(ApplicationArchive archive) throws IOException {
		CloudResources archiveResources = new CloudResources(archive);
		String json = JsonUtil.convertToJson(archiveResources);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(JsonUtil.JSON_MEDIA_TYPE);
		HttpEntity<String> requestEntity = new HttpEntity<String>(json, headers);
		ResponseEntity<String> responseEntity =
			getRestTemplate().exchange(getUrl("/v2/resource_match"), HttpMethod.PUT, requestEntity, String.class);
		List<CloudResource> cloudResources = JsonUtil.convertJsonToCloudResourceList(responseEntity.getBody());
		return new CloudResources(cloudResources);
	}

	private HttpEntity<MultiValueMap<String, ?>> generatePartialResourceRequest(UploadApplicationPayload application,
			CloudResources knownRemoteResources) throws IOException {
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>(2);
		body.add("application", application);
		ObjectMapper mapper = new ObjectMapper();
		String knownRemoteResourcesPayload = mapper.writeValueAsString(knownRemoteResources);
		body.add("resources", knownRemoteResourcesPayload);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		return new HttpEntity<MultiValueMap<String, ?>>(body, headers);
	}

	private File createTemporaryUploadFile(InputStream inputStream) throws IOException {
		File file = File.createTempFile("cfjava", null);
		FileOutputStream outputStream = new FileOutputStream(file);
		FileCopyUtils.copy(inputStream, outputStream);
		outputStream.close();
		return file;
	}

	@Override
	public StartingInfo startApplication(String appName) {
		CloudApplication app = getApplication(appName);
		if (app.getState() != CloudApplication.AppState.STARTED) {
			HashMap<String, Object> appRequest = new HashMap<String, Object>();
			appRequest.put("state", CloudApplication.AppState.STARTED);

			HttpEntity<Object> requestEntity = new HttpEntity<Object>(
					appRequest);
			ResponseEntity<String> entity = getRestTemplate().exchange(
					getUrl("/v2/apps/{guid}?stage_async=true"), HttpMethod.PUT, requestEntity,
					String.class, app.getMeta().getGuid());

			HttpHeaders headers = entity.getHeaders();

			// Return a starting info, even with a null staging log value, as a non-null starting info
			// indicates that the response entity did have headers. The API contract is to return starting info
			// if there are headers in the response, null otherwise.
			if (headers != null && !headers.isEmpty()) {
				String stagingFile = headers.getFirst("x-app-staging-log");

				if (stagingFile != null) {
					try {
						stagingFile = URLDecoder.decode(stagingFile, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						logger.error("unexpected inability to UTF-8 decode", e);
					}
				}
				// Return the starting info even if decoding failed or staging file is null
				return new StartingInfo(stagingFile);
			}
		}
		return null;
	}

	@Override
	public void debugApplication(String appName, CloudApplication.DebugMode mode) {
		throw new UnsupportedOperationException("Feature is not yet implemented.");
	}

	@Override
	public void stopApplication(String appName) {
		CloudApplication app = getApplication(appName);
		if (app.getState() != CloudApplication.AppState.STOPPED) {
			HashMap<String, Object> appRequest = new HashMap<String, Object>();
			appRequest.put("state", CloudApplication.AppState.STOPPED);
			getRestTemplate().put(getUrl("/v2/apps/{guid}"), appRequest, app.getMeta().getGuid());
		}
	}

	@Override
	public StartingInfo restartApplication(String appName) {
		stopApplication(appName);
		return startApplication(appName);
	}

	@Override
	public void deleteApplication(String appName) {
		UUID appId = getAppId(appName);
		doDeleteApplication(appId);
	}

	@Override
	public void deleteAllApplications() {
		List<CloudApplication> cloudApps = getApplications();
		for (CloudApplication cloudApp : cloudApps) {
			deleteApplication(cloudApp.getName());
		}
	}

	@Override
	public void updateApplicationDiskQuota(String appName, int disk) {
		UUID appId = getAppId(appName);
		HashMap<String, Object> appRequest = new HashMap<String, Object>();
		appRequest.put("disk_quota", disk);
		getRestTemplate().put(getUrl("/v2/apps/{guid}"), appRequest, appId);
	}

	@Override
	public void updateApplicationMemory(String appName, int memory) {
		UUID appId = getAppId(appName);
		HashMap<String, Object> appRequest = new HashMap<String, Object>();
		appRequest.put("memory", memory);
		getRestTemplate().put(getUrl("/v2/apps/{guid}"), appRequest, appId);
	}

	@Override
	public void updateApplicationInstances(String appName, int instances) {
		UUID appId = getAppId(appName);
		HashMap<String, Object> appRequest = new HashMap<String, Object>();
		appRequest.put("instances", instances);
		getRestTemplate().put(getUrl("/v2/apps/{guid}"), appRequest, appId);
	}

	@Override
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
			doBindService(app.getMeta().getGuid(), serviceId);
		}
		for (UUID serviceId : deleteServices) {
			doUnbindService(app.getMeta().getGuid(), serviceId);
		}
	}

	public List<CloudQuota> getQuotas() {
		String urlPath = "/v2/quota_definitions";
		List<Map<String, Object>> resourceList = getAllResources(urlPath, null);
		List<CloudQuota> quotas = new ArrayList<CloudQuota>();
		for (Map<String, Object> resource : resourceList) {
			quotas.add(resourceMapper.mapResource(resource, CloudQuota.class));
		}
		return quotas;
	}

	/**
	 * Create quota from a CloudQuota instance (Quota Plan)
	 *
	 * @param quota
	 */
	public void createQuota(CloudQuota quota) {
		String setPath = "/v2/quota_definitions";
		HashMap<String, Object> setRequest = new HashMap<String, Object>();
		setRequest.put("name", quota.getName());
		setRequest.put("memory_limit", quota.getMemoryLimit());
		setRequest.put("total_routes", quota.getTotalRoutes());
		setRequest.put("total_services", quota.getTotalServices());
		setRequest.put("non_basic_services_allowed", quota.isNonBasicServicesAllowed());
		getRestTemplate().postForObject(getUrl(setPath), setRequest, String.class);
	}

	public void updateQuota(CloudQuota quota, String name) {
		CloudQuota oldQuota = this.getQuotaByName(name, true);

		String setPath = "/v2/quota_definitions/{quotaGuid}";

		Map<String, Object> setVars = new HashMap<String, Object>();
		setVars.put("quotaGuid", oldQuota.getMeta().getGuid());

		HashMap<String, Object> setRequest = new HashMap<String, Object>();
		setRequest.put("name", quota.getName());
		setRequest.put("memory_limit", quota.getMemoryLimit());
		setRequest.put("total_routes", quota.getTotalRoutes());
		setRequest.put("total_services", quota.getTotalServices());
		setRequest.put("non_basic_services_allowed", quota.isNonBasicServicesAllowed());

		getRestTemplate().put(getUrl(setPath), setRequest, setVars);
	}

	public void deleteQuota(String quotaName) {
		CloudQuota quota = this.getQuotaByName(quotaName, true);
		String setPath = "/v2/quota_definitions/{quotaGuid}";
		Map<String, Object> setVars = new HashMap<String, Object>();
		setVars.put("quotaGuid", quota.getMeta().getGuid());
		getRestTemplate().delete(getUrl(setPath), setVars);
	}

	/**
	 * Set quota to organization
	 *
	 * @param orgName
	 * @param quotaName
	 */
	public void setQuotaToOrg(String orgName, String quotaName) {
		CloudQuota quota = this.getQuotaByName(quotaName, true);
		CloudOrganization org = this.getOrgByName(orgName, true);

		doSetQuotaToOrg(org.getMeta().getGuid(), quota.getMeta().getGuid());
	}

	/**
	 * Get organization by given name.
	 *
	 * @param orgName
	 * @param required
	 * @return CloudOrganization instance
	 */
	public CloudOrganization getOrgByName(String orgName, boolean required) {
		Map<String, Object> urlVars = new HashMap<String, Object>();
		String urlPath = "/v2/organizations?inline-relations-depth=1&q=name:{name}";
		urlVars.put("name", orgName);
		CloudOrganization org = null;
		List<Map<String, Object>> resourceList = getAllResources(urlPath,
				urlVars);
		if (resourceList.size() > 0) {
			Map<String, Object> resource = resourceList.get(0);
			org = resourceMapper.mapResource(resource, CloudOrganization.class);
		}

		if (org == null && required) {
			throw new IllegalArgumentException("Organization '" + orgName
					+ "' not found.");
		}

		return org;
	}

	/**
	 * Get quota by given name.
	 *
	 * @param quotaName
	 * @param required
	 * @return CloudQuota instance
	 */
	public CloudQuota getQuotaByName(String quotaName, boolean required) {
		Map<String, Object> urlVars = new HashMap<String, Object>();
		String urlPath = "/v2/quota_definitions?q=name:{name}";
		urlVars.put("name", quotaName);
		CloudQuota quota = null;
		List<Map<String, Object>> resourceList = getAllResources(urlPath, urlVars);
		if (resourceList.size() > 0) {
			Map<String, Object> resource = resourceList.get(0);
			quota = resourceMapper.mapResource(resource, CloudQuota.class);
		}

		if (quota == null && required) {
			throw new IllegalArgumentException("Quota '" + quotaName
					+ "' not found.");
		}

		return quota;
	}

	private void doSetQuotaToOrg(UUID orgGuid, UUID quotaGuid) {
		String setPath = "/v2/organizations/{org}";
		Map<String, Object> setVars = new HashMap<String, Object>();
		setVars.put("org", orgGuid);
		HashMap<String, Object> setRequest = new HashMap<String, Object>();
		setRequest.put("quota_definition_guid", quotaGuid);

		getRestTemplate().put(getUrl(setPath), setRequest, setVars);
	}

	private void doBindService(UUID appId, UUID serviceId) {
		HashMap<String, Object> serviceRequest = new HashMap<String, Object>();
		serviceRequest.put("service_instance_guid", serviceId);
		serviceRequest.put("app_guid", appId);
		getRestTemplate().postForObject(getUrl("/v2/service_bindings"), serviceRequest, String.class);
	}

	private void doUnbindService(UUID appId, UUID serviceId) {
		UUID serviceBindingId = getServiceBindingId(appId, serviceId);
		getRestTemplate().delete(getUrl("/v2/service_bindings/{guid}"), serviceBindingId);
	}

	@Override
	public void updateApplicationStaging(String appName, Staging staging) {
		UUID appId = getAppId(appName);
		HashMap<String, Object> appRequest = new HashMap<String, Object>();
		addStagingToRequest(staging, appRequest);
		getRestTemplate().put(getUrl("/v2/apps/{guid}"), appRequest, appId);
	}

	@Override
	public void updateApplicationUris(String appName, List<String> uris) {
		CloudApplication app = getApplication(appName);
		List<String> newUris = new ArrayList<String>(uris);
		newUris.removeAll(app.getUris());
		List<String> removeUris = app.getUris();
		removeUris.removeAll(uris);
		removeUris(removeUris, app.getMeta().getGuid());
		addUris(newUris, app.getMeta().getGuid());
	}

	@Override
	public void updateApplicationEnv(String appName, Map<String, String> env) {
		UUID appId = getAppId(appName);
		HashMap<String, Object> appRequest = new HashMap<String, Object>();
		appRequest.put("environment_json", env);
		getRestTemplate().put(getUrl("/v2/apps/{guid}"), appRequest, appId);
	}

	@Override
	public void updateApplicationEnv(String appName, List<String> env) {
		Map<String, String> envHash = new HashMap<String, String>();
		for (String s : env) {
			if (!s.contains("=")) {
				throw new IllegalArgumentException("Environment setting without '=' is invalid: " + s);
			}
			String key = s.substring(0, s.indexOf('=')).trim();
			String value = s.substring(s.indexOf('=') + 1).trim();
			envHash.put(key, value);
		}
		updateApplicationEnv(appName, envHash);
	}

	@Override
	public List<CloudEvent> getEvents() {
		String urlPath = "/v2/events";
		return doGetEvents(urlPath, null);
	}

	@Override
	public List<CloudEvent> getApplicationEvents(String appName) {
		UUID appId = getAppId(appName);
		Map<String, Object> urlVars = new HashMap<String, Object>();
		urlVars.put("appId", appId);
		String urlPath = "/v2/events?q=actee:{appId}";
		return doGetEvents(urlPath, urlVars);
	}

	private List<CloudEvent> doGetEvents(String urlPath, Map<String, Object> urlVars) {
		List<Map<String, Object>> resourceList = getAllResources(urlPath, urlVars);
		List<CloudEvent> events = new ArrayList<CloudEvent>();
		for (Map<String, Object> resource : resourceList) {
			if (resource != null) {
				events.add(resourceMapper.mapResource(resource, CloudEvent.class));
			}
		}
		return events;
	}

	@Override
	public void bindService(String appName, String serviceName) {
		CloudService cloudService = getService(serviceName);
		UUID appId = getAppId(appName);
		doBindService(appId, cloudService.getMeta().getGuid());
	}

	@Override
	public void unbindService(String appName, String serviceName) {
		CloudService cloudService = getService(serviceName);
		UUID appId = getAppId(appName);
		doUnbindService(appId, cloudService.getMeta().getGuid());
	}

	@Override
	public InstancesInfo getApplicationInstances(String appName) {
		CloudApplication app = getApplication(appName);
		return getApplicationInstances(app);
	}

	@Override
	public InstancesInfo getApplicationInstances(CloudApplication app) {
		if (app.getState().equals(CloudApplication.AppState.STARTED)) {
			return doGetApplicationInstances(app.getMeta().getGuid());
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private InstancesInfo doGetApplicationInstances(UUID appId) {
		try {
			List<Map<String, Object>> instanceList = new ArrayList<Map<String, Object>>();
			Map<String, Object> respMap = getInstanceInfoForApp(appId, "instances");
			List<String> keys = new ArrayList<String>(respMap.keySet());
			Collections.sort(keys);
			for (String instanceId : keys) {
				Integer index;
				try {
					index = Integer.valueOf(instanceId);
				} catch (NumberFormatException e) {
					index = -1;
				}
				Map<String, Object> instanceMap = (Map<String, Object>) respMap.get(instanceId);
				instanceMap.put("index", index);
				instanceList.add(instanceMap);
			}
			return new InstancesInfo(instanceList);
		} catch (CloudFoundryException e) {
			if (e.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
				return null;
			} else {
				throw e;
			}

		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public CrashesInfo getCrashes(String appName) {
		UUID appId = getAppId(appName);
		if (appId == null) {
			throw new IllegalArgumentException("Application '" + appName + "' not found.");
		}
		Map<String, Object> urlVars = new HashMap<String, Object>();
		urlVars.put("guid", appId);
		String resp = getRestTemplate().getForObject(getUrl("/v2/apps/{guid}/crashes"), String.class, urlVars);
		Map<String, Object> respMap = JsonUtil.convertJsonToMap("{ \"crashes\" : " + resp + " }");
		List<Map<String, Object>> attributes = (List<Map<String, Object>>) respMap.get("crashes");
		return new CrashesInfo(attributes);
	}

	@Override
	public void rename(String appName, String newName) {
		UUID appId = getAppId(appName);
		HashMap<String, Object> appRequest = new HashMap<String, Object>();
		appRequest.put("name", newName);
		getRestTemplate().put(getUrl("/v2/apps/{guid}"), appRequest, appId);
	}

	@Override
	public List<CloudStack> getStacks() {
		String urlPath = "/v2/stacks";
		List<Map<String, Object>> resources = getAllResources(urlPath, null);
		List<CloudStack> stacks = new ArrayList<CloudStack>();
		for (Map<String, Object> resource : resources) {
			stacks.add(resourceMapper.mapResource(resource, CloudStack.class));
		}
		return stacks;
	}

	@Override
	public CloudStack getStack(String name) {
		String urlPath = "/v2/stacks?q={q}";
		Map<String, Object> urlVars = new HashMap<String, Object>();
		urlVars.put("q", "name:" + name);
		List<Map<String, Object>> resources = getAllResources(urlPath, urlVars);
		if (resources.size() > 0) {
			Map<String, Object> resource = resources.get(0);
			return resourceMapper.mapResource(resource, CloudStack.class);
		}
		return null;
	}

	@Override
	public List<CloudDomain> getDomainsForOrg() {
		assertSpaceProvided("access organization domains");
		return doGetDomains(sessionSpace.getOrganization());
	}

	@Override
	public List<CloudDomain> getDomains() {
		return doGetDomains((CloudOrganization) null);
	}

	@Override
	public List<CloudDomain> getPrivateDomains() {
		return doGetDomains("/v2/private_domains");
	}

	@Override
	public List<CloudDomain> getSharedDomains() {
		return doGetDomains("/v2/shared_domains");
	}

	@Override
	public CloudDomain getDefaultDomain() {
		List<CloudDomain> sharedDomains = getSharedDomains();
		if (sharedDomains.isEmpty()) {
			return null;
		} else {
			return sharedDomains.get(0);
		}
	}

	@Override
	public void addDomain(String domainName) {
		assertSpaceProvided("add domain");
		UUID domainGuid = getDomainGuid(domainName, false);
		if (domainGuid == null) {
			doCreateDomain(domainName);
		}
	}

	@Override
	public void deleteDomain(String domainName) {
		assertSpaceProvided("delete domain");
		UUID domainGuid = getDomainGuid(domainName, true);
		List<CloudRoute> routes = getRoutes(domainName);
		if (routes.size() > 0) {
			throw new IllegalStateException("Unable to remove domain that is in use --" +
					" it has " + routes.size() + " routes.");
		}
		doDeleteDomain(domainGuid);
	}

	@Override
	public void removeDomain(String domainName) {
		deleteDomain(domainName);
	}

	@Override
	public List<CloudRoute> getRoutes(String domainName) {
		assertSpaceProvided("get routes for domain");
		UUID domainGuid = getDomainGuid(domainName, true);
		return doGetRoutes(domainGuid);
	}

	@Override
	public void addRoute(String host, String domainName) {
		assertSpaceProvided("add route for domain");
		UUID domainGuid = getDomainGuid(domainName, true);
		doAddRoute(host, domainGuid);
	}

	@Override
	public void deleteRoute(String host, String domainName) {
		assertSpaceProvided("delete route for domain");
		UUID domainGuid = getDomainGuid(domainName, true);
		UUID routeGuid = getRouteGuid(host, domainGuid);
		if (routeGuid == null) {
			throw new IllegalArgumentException("Host '" + host + "' not found for domain '" + domainName + "'.");
		}
		doDeleteRoute(routeGuid);
	}

	protected String getFileUrlPath() {
		return "/v2/apps/{appId}/instances/{instance}/files/{filePath}";
	}

	protected Object getFileAppId(String appName) {
		return getAppId(appName);
	}

	private void assertSpaceProvided(String operation) {
		Assert.notNull(sessionSpace, "Unable to " + operation + " without specifying organization and space to use.");
	}

	private void doDeleteRoute(UUID routeGuid) {
		Map<String, Object> urlVars = new HashMap<String, Object>();
		String urlPath = "/v2/routes/{route}";
		urlVars.put("route", routeGuid);
		getRestTemplate().delete(getUrl(urlPath), urlVars);
	}

	/**
	 * Delete routes that do not have any application which is assigned to them.
	 *
	 * @return deleted routes or an empty list if no routes have been found
	 */
	@Override
	public List<CloudRoute> deleteOrphanedRoutes() {
		List<CloudRoute> orphanRoutes = new ArrayList<>();
		for (CloudDomain cloudDomain : getDomainsForOrg()) {
			orphanRoutes.addAll(fetchOrphanRoutes(cloudDomain.getName()));
		}

		List<CloudRoute> deletedCloudRoutes = new ArrayList<>();
		for (CloudRoute orphanRoute : orphanRoutes) {
			deleteRoute(orphanRoute.getHost(), orphanRoute.getDomain().getName());
			deletedCloudRoutes.add(orphanRoute);
		}

		return deletedCloudRoutes;
	}

	private List<CloudRoute> fetchOrphanRoutes(String domainName) {
		List<CloudRoute> orphanRoutes = new ArrayList<>();
		for (CloudRoute cloudRoute : getRoutes(domainName)) {
			if (isOrphanRoute(cloudRoute)) {
				orphanRoutes.add(cloudRoute);
			}
		}

		return orphanRoutes;
	}

	private boolean isOrphanRoute(CloudRoute cloudRoute) {
		return cloudRoute.getAppsUsingRoute() == 0;
	}

	private List<CloudDomain> doGetDomains(CloudOrganization org) {
		Map<String, Object> urlVars = new HashMap<String, Object>();
		String urlPath = "/v2";
		if (org != null) {
			urlVars.put("org", org.getMeta().getGuid());
			urlPath = urlPath + "/organizations/{org}";
		}
		urlPath = urlPath + "/domains";
		return doGetDomains(urlPath, urlVars);
	}

	private List<CloudDomain> doGetDomains(String urlPath) {
		return doGetDomains(urlPath, null);
	}

	private List<CloudDomain> doGetDomains(String urlPath, Map<String, Object> urlVars) {
		List<Map<String, Object>> domainResources = getAllResources(urlPath, urlVars);
		List<CloudDomain> domains = new ArrayList<CloudDomain>();
		for (Map<String, Object> resource : domainResources) {
			domains.add(resourceMapper.mapResource(resource, CloudDomain.class));
		}
		return domains;
	}

	private UUID doCreateDomain(String domainName) {
		String urlPath = "/v2/private_domains";
		HashMap<String, Object> domainRequest = new HashMap<String, Object>();
		domainRequest.put("owning_organization_guid", sessionSpace.getOrganization().getMeta().getGuid());
		domainRequest.put("name", domainName);
		domainRequest.put("wildcard", true);
		String resp = getRestTemplate().postForObject(getUrl(urlPath), domainRequest, String.class);
		Map<String, Object> respMap = JsonUtil.convertJsonToMap(resp);
		return resourceMapper.getGuidOfResource(respMap);
	}

	private void doDeleteDomain(UUID domainGuid) {
		Map<String, Object> urlVars = new HashMap<String, Object>();
		String urlPath = "/v2/private_domains/{domain}";
		urlVars.put("domain", domainGuid);
		getRestTemplate().delete(getUrl(urlPath), urlVars);
	}

	private List<CloudRoute> doGetRoutes(UUID domainGuid) {
		Map<String, Object> urlVars = new HashMap<String, Object>();
		String urlPath = "/v2";
//		TODO: NOT implemented ATM:
//		if (sessionSpace != null) {
//			urlVars.put("space", sessionSpace.getMeta().getGuid());
//			urlPath = urlPath + "/spaces/{space}";
//		}
		urlPath = urlPath + "/routes?inline-relations-depth=1";
		List<Map<String, Object>> allRoutes = getAllResources(urlPath, urlVars);
		List<CloudRoute> routes = new ArrayList<CloudRoute>();
		for (Map<String, Object> route : allRoutes) {
//			TODO: move space_guid to path once implemented (see above):
			UUID space = CloudEntityResourceMapper.getEntityAttribute(route, "space_guid", UUID.class);
			UUID domain = CloudEntityResourceMapper.getEntityAttribute(route, "domain_guid", UUID.class);
			if (sessionSpace.getMeta().getGuid().equals(space) && domainGuid.equals(domain)) {
				//routes.add(CloudEntityResourceMapper.getEntityAttribute(route, "host", String.class));
				routes.add(resourceMapper.mapResource(route, CloudRoute.class));
			}
		}
		return routes;
	}

	private void doDeleteService(CloudService cloudService) {
		List<UUID> appIds = getAppsBoundToService(cloudService);
		if (appIds.size() > 0) {
			for (UUID appId : appIds) {
				doUnbindService(appId, cloudService.getMeta().getGuid());
			}
		}
		ResponseEntity<Map<String, Object>> response =
			getRestTemplate().exchange(getUrl("/v2/service_instances/{guid}?async=true"),
				HttpMethod.DELETE, HttpEntity.EMPTY,
				new ParameterizedTypeReference<Map<String, Object>>() {},
				cloudService.getMeta().getGuid());
		waitForAsyncJobCompletion(response.getBody());
	}

	private void waitForAsyncJobCompletion(Map<String, Object> jobResponse) {
		long timeout = System.currentTimeMillis() + JOB_TIMEOUT;
		while (System.currentTimeMillis() < timeout) {
			CloudJob job = resourceMapper.mapResource(jobResponse, CloudJob.class);

			if (job.getStatus() == CloudJob.Status.FINISHED) {
				return;
			}

			if (job.getStatus() == CloudJob.Status.FAILED) {
				throw new CloudOperationException(job.getErrorDetails().getDescription());
			}

			try {
				Thread.sleep(JOB_POLLING_PERIOD);
			} catch (InterruptedException e) {
				return;
			}

			jobResponse = getRestTemplate().exchange(getUrl(job.getMeta().getUrl()),
				HttpMethod.GET, HttpEntity.EMPTY,
				new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();
		}
	}

	@SuppressWarnings("unchecked")
	private List<UUID> getAppsBoundToService(CloudService cloudService) {
		List<UUID> appGuids = new ArrayList<UUID>();
		String urlPath = "/v2";
		Map<String, Object> urlVars = new HashMap<String, Object>();
		if (sessionSpace != null) {
			urlVars.put("space", sessionSpace.getMeta().getGuid());
			urlPath = urlPath + "/spaces/{space}";
		}
		urlVars.put("q", "name:" + cloudService.getName());
		urlPath = urlPath + "/service_instances?q={q}";
		List<Map<String, Object>> resourceList = getAllResources(urlPath, urlVars);
		for (Map<String, Object> resource : resourceList) {
			fillInEmbeddedResource(resource, "service_bindings");
			List<Map<String, Object>> bindings =
					CloudEntityResourceMapper.getEntityAttribute(resource, "service_bindings", List.class);
			for (Map<String, Object> binding : bindings) {
				String appId = CloudEntityResourceMapper.getEntityAttribute(binding, "app_guid", String.class);
				if (appId != null) {
					appGuids.add(UUID.fromString(appId));
				}
			}
		}
		return appGuids;
	}

	private void doDeleteApplication(UUID appId) {
		getRestTemplate().delete(getUrl("/v2/apps/{guid}?recursive=true"), appId);
	}

	private List<CloudServiceOffering> getServiceOfferings(String label) {
		Assert.notNull(label, "Service label must not be null");
		List<Map<String, Object>> resourceList = getAllResources("/v2/services?inline-relations-depth=1", null);
		List<CloudServiceOffering> results = new ArrayList<CloudServiceOffering>();
		for (Map<String, Object> resource : resourceList) {
			CloudServiceOffering cloudServiceOffering =
					resourceMapper.mapResource(resource, CloudServiceOffering.class);
			if (cloudServiceOffering.getLabel() != null && label.equals(cloudServiceOffering.getLabel())) {
				results.add(cloudServiceOffering);
			}
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	private UUID getServiceBindingId(UUID appId, UUID serviceId ) {
		Map<String, Object> urlVars = new HashMap<String, Object>();
		urlVars.put("guid", appId);
		List<Map<String, Object>> resourceList = getAllResources("/v2/apps/{guid}/service_bindings", urlVars);
		UUID serviceBindingId = null;
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
		Map<String, Object> resource = findApplicationResource(appName, false);
		UUID guid = null;
		if (resource != null) {
			Map<String, Object> appMeta = (Map<String, Object>) resource.get("metadata");
			guid = UUID.fromString(String.valueOf(appMeta.get("guid")));
		}
		return guid;
	}

	private StreamingLogToken streamLoggregatorLogs(String appName, ApplicationLogListener listener, boolean recent) {
		ClientEndpointConfig.Configurator configurator = new ClientEndpointConfig.Configurator() {
			public void beforeRequest(Map<String, List<String>> headers) {
				String authorizationHeader = oauthClient.getAuthorizationHeader();
				if (authorizationHeader != null) {
					headers.put(AUTHORIZATION_HEADER_KEY, Arrays.asList(authorizationHeader));
				}
			}
		};

		String endpoint = getInfo().getLoggregatorEndpoint();
		String mode = recent ? "dump" : "tail";
		UUID appId = getAppId(appName);
		return loggregatorClient.connectToLoggregator(endpoint, mode, appId, listener, configurator);
	}

	private class AccumulatingApplicationLogListener implements ApplicationLogListener {
		private List<ApplicationLog> logs = new ArrayList<ApplicationLog>();

		@Override
		public void onMessage(ApplicationLog log) {
			logs.add(log);
		}

		@Override
		public void onError(Throwable exception) {
			synchronized (this) {
				this.notify();
			}
		}

		@Override
		public void onComplete() {
			synchronized (this) {
				this.notify();
			}
		}

		public List<ApplicationLog> getLogs() {
			Collections.sort(logs);
			return logs;
		}
	}

	private Map<String, Object> findApplicationResource(UUID appGuid, boolean fetchServiceInfo) {
		Map<String, Object> urlVars = new HashMap<String, Object>();
		String urlPath = "/v2/apps/{app}?inline-relations-depth=1";
		urlVars.put("app", appGuid);
		String resp = getRestTemplate().getForObject(getUrl(urlPath), String.class, urlVars);

		return processApplicationResource(JsonUtil.convertJsonToMap(resp), fetchServiceInfo);
	}


	private Map<String, Object> findApplicationResource(String appName, boolean fetchServiceInfo) {
		Map<String, Object> urlVars = new HashMap<String, Object>();
		String urlPath = "/v2";
		if (sessionSpace != null) {
			urlVars.put("space", sessionSpace.getMeta().getGuid());
			urlPath = urlPath + "/spaces/{space}";
		}
		urlVars.put("q", "name:" + appName);
		urlPath = urlPath + "/apps?inline-relations-depth=1&q={q}";

		List<Map<String, Object>> allResources = getAllResources(urlPath, urlVars);
		if(!allResources.isEmpty()) {
			return processApplicationResource(allResources.get(0), fetchServiceInfo);
		}
		return null;
	}

	private Map<String, Object> processApplicationResource(Map<String, Object> resource, boolean fetchServiceInfo) {
		if (fetchServiceInfo) {
			fillInEmbeddedResource(resource, "service_bindings", "service_instance");
		}
		fillInEmbeddedResource(resource, "stack");
		return resource;
	}

	private List<String> findApplicationUris(UUID appGuid) {
		String urlPath = "/v2/apps/{app}/routes?inline-relations-depth=1";
		Map<String, Object> urlVars = new HashMap<String, Object>();
		urlVars.put("app", appGuid);
		List<Map<String, Object>> resourceList = getAllResources(urlPath, urlVars);
		List<String> uris =  new ArrayList<String>();
		for (Map<String, Object> resource : resourceList) {
			Map<String, Object> domainResource = CloudEntityResourceMapper.getEmbeddedResource(resource, "domain");
			String host = CloudEntityResourceMapper.getEntityAttribute(resource, "host", String.class);
			String domain = CloudEntityResourceMapper.getEntityAttribute(domainResource, "name", String.class);
			if (host != null && host.length() > 0)
				uris.add(host + "." + domain);
			else
				uris.add(domain);
		}
		return uris;
	}

	@SuppressWarnings("restriction")
	private Map<String, Object> getUserInfo(String user) {
//		String userJson = getRestTemplate().getForObject(getUrl("/v2/users/{guid}"), String.class, user);
//		Map<String, Object> userInfo = (Map<String, Object>) JsonUtil.convertJsonToMap(userJson);
//		return userInfo();
		//TODO: remove this temporary hack once the /v2/users/ uri can be accessed by mere mortals
		String userJson = "{}";
		OAuth2AccessToken accessToken = oauthClient.getToken();
		if (accessToken != null) {
			String tokenString = accessToken.getValue();
			int x = tokenString.indexOf('.');
			int y = tokenString.indexOf('.', x + 1);
			String encodedString = tokenString.substring(x + 1, y);
			try {
				byte[] decodedBytes = new sun.misc.BASE64Decoder().decodeBuffer(encodedString);
				userJson = new String(decodedBytes, 0, decodedBytes.length, "UTF-8");
			} catch (IOException e) {}
		}
		return(JsonUtil.convertJsonToMap(userJson));
	}

	@SuppressWarnings("unchecked")
	private void fillInEmbeddedResource(Map<String, Object> resource, String... resourcePath) {
		if (resourcePath.length == 0) {
			return;
		}
		Map<String, Object> entity = (Map<String, Object>) resource.get("entity");

		String headKey = resourcePath[0];
		String[] tailPath = Arrays.copyOfRange(resourcePath, 1, resourcePath.length);

		if (!entity.containsKey(headKey)) {
			String pathUrl = entity.get(headKey + "_url").toString();
			Object response = getRestTemplate().getForObject(getUrl(pathUrl), Object.class);
			if (response instanceof Map) {
				Map<String, Object> responseMap = (Map<String, Object>) response;
				if (responseMap.containsKey("resources")) {
					response = responseMap.get("resources");
				}
			}
			entity.put(headKey, response);
		}
		Object embeddedResource = entity.get(headKey);

		if (embeddedResource instanceof Map) {
			Map<String, Object> embeddedResourceMap = (Map<String, Object>) embeddedResource;
			//entity = (Map<String, Object>) embeddedResourceMap.get("entity");
			fillInEmbeddedResource(embeddedResourceMap, tailPath);
		} else if (embeddedResource instanceof List) {
			List<Object> embeddedResourcesList = (List<Object>) embeddedResource;
			for (Object r: embeddedResourcesList) {
				fillInEmbeddedResource((Map<String, Object>)r, tailPath);
			}
		} else {
			// no way to proceed
			return;
		}
	}

	@SuppressWarnings("unchecked")
	private boolean hasEmbeddedResource(Map<String, Object> resource, String resourceKey) {
		Map<String, Object> entity = (Map<String, Object>) resource.get("entity");
		return entity.containsKey(resourceKey) || entity.containsKey(resourceKey + "_url");
	}

	private static class ResponseExtractorWrapper implements ResponseExtractor {

		private ClientHttpResponseCallback callback;

		public ResponseExtractorWrapper(ClientHttpResponseCallback callback) {
			this.callback = callback;
		}

		@Override
		public Object extractData(ClientHttpResponse clientHttpResponse) throws IOException {
			callback.onClientHttpResponse(clientHttpResponse);
			return null;
		}

	}

	// Security Group operations

	@Override
	public List<CloudSecurityGroup> getSecurityGroups() {
		String urlPath = "/v2/security_groups";
		List<Map<String, Object>> resourceList = getAllResources(urlPath, null);
		List<CloudSecurityGroup> groups = new ArrayList<CloudSecurityGroup>();
		for (Map<String, Object> resource : resourceList) {
			groups.add(resourceMapper.mapResource(resource,
					CloudSecurityGroup.class));
		}
		return groups;
	}

	@Override
	public CloudSecurityGroup getSecurityGroup(String securityGroupName) {
		return doGetSecurityGroup(securityGroupName, false);
	}

	private CloudSecurityGroup doGetSecurityGroup(String securityGroupName, boolean required) {
		Map<String, Object> urlVars = new HashMap<String, Object>();
		String urlPath = "/v2/security_groups?q=name:{name}";
		urlVars.put("name", securityGroupName);
		CloudSecurityGroup securityGroup = null;
		List<Map<String, Object>> resourceList = getAllResources(urlPath,
				urlVars);
		if (resourceList.size() > 0) {
			Map<String, Object> resource = resourceList.get(0);
			securityGroup = resourceMapper.mapResource(resource,
					CloudSecurityGroup.class);
		}else if(required && resourceList.size() == 0){
			throw new IllegalArgumentException("Security group named '" + securityGroupName
					+ "' not found.");
		}

		return securityGroup;
	}

	@Override
	public void createSecurityGroup(CloudSecurityGroup securityGroup) {
		doCreateSecurityGroup(securityGroup.getName(),
				convertToList(securityGroup.getRules()));
	}

	private List<Map<String, Object>> convertToList(
			List<SecurityGroupRule> rules) {
		List<Map<String, Object>> ruleList = new ArrayList<Map<String, Object>>();
		for (SecurityGroupRule rule : rules) {
			Map<String, Object> ruleMap = new HashMap<String, Object>();
			ruleMap.put("protocol", rule.getProtocol());
			ruleMap.put("destination", rule.getDestination());
			if (rule.getPorts() != null) {
				ruleMap.put("ports", rule.getPorts());
			}
			if(rule.getLog() != null){
				ruleMap.put("log", rule.getLog());
			}
			if(rule.getType() != null){
				ruleMap.put("type", rule.getType());
			}
			if(rule.getCode() != null){
				ruleMap.put("code", rule.getCode());
			}
			ruleList.add(ruleMap);
		}
		return ruleList;
	}

	@Override
	public void createSecurityGroup(String name, InputStream jsonRulesFile) {
		doCreateSecurityGroup(name, JsonUtil.convertToJsonList(jsonRulesFile));
	}

	private void doCreateSecurityGroup(String name, List<Map<String, Object>> rules) {
		String path = "/v2/security_groups";
		HashMap<String, Object> request = new HashMap<String, Object>();
		request.put("name", name);
		request.put("rules", rules);
		getRestTemplate().postForObject(getUrl(path), request, String.class);
	}

	@Override
	public void updateSecurityGroup(CloudSecurityGroup securityGroup) {
		CloudSecurityGroup oldGroup = doGetSecurityGroup(securityGroup.getName(), true);
		doUpdateSecurityGroup(oldGroup, securityGroup.getName(), convertToList(securityGroup.getRules()));
	}

	@Override
	public void updateSecurityGroup(String name, InputStream jsonRulesFile) {
		CloudSecurityGroup oldGroup = doGetSecurityGroup(name, true);
		doUpdateSecurityGroup(oldGroup, name, JsonUtil.convertToJsonList(jsonRulesFile));
	}

	private void doUpdateSecurityGroup(CloudSecurityGroup currentGroup, String name, List<Map<String, Object>> rules){
		String path = "/v2/security_groups/{guid}";

		Map<String, Object> pathVariables = new HashMap<String, Object>();
		pathVariables.put("guid", currentGroup.getMeta().getGuid());

		HashMap<String, Object> request = new HashMap<String, Object>();
		request.put("name", name);
		request.put("rules", rules);
		// Updates of bindings to spaces and default staging/running groups must be done
		// through explicit calls to those methods and not through this generic update

		getRestTemplate().put(getUrl(path), request, pathVariables);
	}

	@Override
	public void deleteSecurityGroup(String securityGroupName) {
		CloudSecurityGroup group = doGetSecurityGroup(securityGroupName, true);

		String path = "/v2/security_groups/{guid}";
		Map<String, Object> pathVariables = new HashMap<String, Object>();
		pathVariables.put("guid", group.getMeta().getGuid());

		getRestTemplate().delete(getUrl(path), pathVariables);
	}

	@Override
	public void bindStagingSecurityGroup(String securityGroupName) {
		CloudSecurityGroup group = doGetSecurityGroup(securityGroupName, true);

		String path = "/v2/config/staging_security_groups/{guid}";

		Map<String, Object> pathVariables = new HashMap<String, Object>();
		pathVariables.put("guid", group.getMeta().getGuid());

		getRestTemplate().put(getUrl(path), null, pathVariables);
	}

	@Override
	public List<CloudSecurityGroup> getStagingSecurityGroups() {
		String urlPath = "/v2/config/staging_security_groups";
		List<Map<String, Object>> resourceList = getAllResources(urlPath, null);
		List<CloudSecurityGroup> groups = new ArrayList<CloudSecurityGroup>();
		for (Map<String, Object> resource : resourceList) {
			groups.add(resourceMapper.mapResource(resource,
					CloudSecurityGroup.class));
		}
		return groups;
	}

	@Override
	public void unbindStagingSecurityGroup(String securityGroupName) {
		CloudSecurityGroup group = doGetSecurityGroup(securityGroupName, true);

		Map<String, Object> urlVars = new HashMap<String, Object>();
		String urlPath = "/v2/config/staging_security_groups/{guid}";
		urlVars.put("guid", group.getMeta().getGuid());
		getRestTemplate().delete(getUrl(urlPath), urlVars);
	}

	@Override
	public List<CloudSecurityGroup> getRunningSecurityGroups() {
		String urlPath = "/v2/config/running_security_groups";
		List<Map<String, Object>> resourceList = getAllResources(urlPath, null);
		List<CloudSecurityGroup> groups = new ArrayList<CloudSecurityGroup>();
		for (Map<String, Object> resource : resourceList) {
			groups.add(resourceMapper.mapResource(resource,
					CloudSecurityGroup.class));
		}
		return groups;
	}

	@Override
	public void bindRunningSecurityGroup(String securityGroupName) {
		CloudSecurityGroup group = doGetSecurityGroup(securityGroupName, true);

		String path = "/v2/config/running_security_groups/{guid}";

		Map<String, Object> pathVariables = new HashMap<String, Object>();
		pathVariables.put("guid", group.getMeta().getGuid());

		getRestTemplate().put(getUrl(path), null, pathVariables);
	}

	@Override
	public void unbindRunningSecurityGroup(String securityGroupName) {
		CloudSecurityGroup group = doGetSecurityGroup(securityGroupName, true);

		Map<String, Object> urlVars = new HashMap<String, Object>();
		String urlPath = "/v2/config/running_security_groups/{guid}";
		urlVars.put("guid", group.getMeta().getGuid());
		getRestTemplate().delete(getUrl(urlPath), urlVars);
	}

	@Override
	public List<CloudSpace> getSpacesBoundToSecurityGroup(String securityGroupName) {
		Map<String, Object> urlVars = new HashMap<String, Object>();
		// Need to go a few levels out to get the Organization that Spaces needs
		String urlPath = "/v2/security_groups?q=name:{name}&inline-relations-depth=2";
		urlVars.put("name", securityGroupName);
		List<Map<String, Object>> resourceList = getAllResources(urlPath,
				urlVars);
		List<CloudSpace> spaces = new ArrayList<CloudSpace>();
		if (resourceList.size() > 0) {
			Map<String, Object> resource = resourceList.get(0);

			Map<String, Object> securityGroupResource = CloudEntityResourceMapper.getEntity(resource);
			List<Map<String, Object>> spaceResources = CloudEntityResourceMapper.getEmbeddedResourceList(securityGroupResource, "spaces");
			for(Map<String, Object> spaceResource: spaceResources){
				spaces.add(resourceMapper.mapResource(spaceResource, CloudSpace.class));
			}
		}else {
			throw new IllegalArgumentException("Security group named '" + securityGroupName
					+ "' not found.");
		}
		return spaces;
	}

	@Override
	public void bindSecurityGroup(String orgName, String spaceName, String securityGroupName) {
		UUID spaceGuid = getSpaceGuid(orgName, spaceName);
		CloudSecurityGroup group = doGetSecurityGroup(securityGroupName, true);

		String path = "/v2/security_groups/{group_guid}/spaces/{space_guid}";

		Map<String, Object> pathVariables = new HashMap<String, Object>();
		pathVariables.put("group_guid", group.getMeta().getGuid());
		pathVariables.put("space_guid", spaceGuid);

		getRestTemplate().put(getUrl(path), null, pathVariables);
	}

	@Override
	public void unbindSecurityGroup(String orgName, String spaceName, String securityGroupName) {
		UUID spaceGuid = getSpaceGuid(orgName, spaceName);
		CloudSecurityGroup group = doGetSecurityGroup(securityGroupName, true);

		String path = "/v2/security_groups/{group_guid}/spaces/{space_guid}";

		Map<String, Object> pathVariables = new HashMap<String, Object>();
		pathVariables.put("group_guid", group.getMeta().getGuid());
		pathVariables.put("space_guid", spaceGuid);

		getRestTemplate().delete(getUrl(path), pathVariables);
	}



}
