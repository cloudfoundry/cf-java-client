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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cloudfoundry.client.lib.*;
import org.cloudfoundry.client.lib.domain.CloudDomain;
import org.cloudfoundry.client.lib.domain.CloudRoute;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.cloudfoundry.client.lib.domain.CrashInfo;
import org.cloudfoundry.client.lib.domain.CrashesInfo;
import org.cloudfoundry.client.lib.util.CloudUtil;
import org.cloudfoundry.client.lib.util.RestUtil;
import org.cloudfoundry.client.lib.util.UploadApplicationPayloadHttpMessageConverter;
import org.cloudfoundry.client.lib.domain.UploadApplicationPayload;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.CommonsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.web.client.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.util.Arrays.asList;

/**
 * Abstract implementation of the CloudControllerClient intended to serve as the base for v1 and v2 implementations.
 *
 * @author Ramnivas Laddad
 * @author A.B.Srinivasan
 * @author Jennifer Hickey
 * @author Dave Syer
 * @author Thomas Risberg
 */
public abstract class AbstractCloudControllerClient implements CloudControllerClient {

	private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
	private static final String PROXY_USER_HEADER_KEY = "Proxy-User";

	protected static final MediaType JSON_MEDIA_TYPE = new MediaType(
			MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(),
			Charset.forName("UTF-8"));

	// This map only contains framework/runtime mapping for frameworks that we actively support
	private static Map<String, Integer> FRAMEWORK_DEFAULT_MEMORY = new HashMap<String, Integer>() {{
		put("spring", 512);
		put("lift", 512);
		put("grails", 512);
		put("java_web", 512);
	}};

	private static final int DEFAULT_MEMORY = 256;

	private static final int FILES_MAX_RETRIES = 10;

	private static final String LOGS_LOCATION = "logs";

	private RestTemplate restTemplate;

	private URL cloudControllerUrl;

	protected RestUtil restUtil;

	protected CloudCredentials cloudCredentials;

	protected URL authorizationEndpoint;

	protected String token;

	protected List<String> freeApplicationPlans = Arrays.asList("free");

    private final Log logger;


	public AbstractCloudControllerClient(URL cloudControllerUrl, RestUtil restUtil, CloudCredentials cloudCredentials,
										 URL authorizationEndpoint, HttpProxyConfiguration httpProxyConfiguration) {
        Assert.notNull(cloudControllerUrl, "CloudControllerUrl cannot be null");
		Assert.notNull(restUtil, "RestUtil cannot be null");
        logger = LogFactory.getLog(getClass().getName());
		this.restUtil = restUtil;
		this.cloudCredentials = cloudCredentials;
		if (cloudCredentials != null && cloudCredentials.getToken() != null) {
			this.token = cloudCredentials.getToken();
		}
		this.cloudControllerUrl = cloudControllerUrl;
		if (authorizationEndpoint != null) {
			this.authorizationEndpoint = determineAuthorizationEndPointToUse(authorizationEndpoint, cloudControllerUrl);
		} else {
			this.authorizationEndpoint = null;
		}
		this.restTemplate = restUtil.createRestTemplate(httpProxyConfiguration);
		configureCloudFoundryRequestFactory(restTemplate);

		this.restTemplate.setErrorHandler(new ErrorHandler());
		this.restTemplate.setMessageConverters(getHttpMessageConverters());
	}

	protected URL determineAuthorizationEndPointToUse(URL authorizationEndpoint, URL cloudControllerUrl) {
		if (cloudControllerUrl.getProtocol().equals("http") && authorizationEndpoint.getProtocol().equals("https")) {
			try {
				URL newUrl = new URL("http", authorizationEndpoint.getHost(), authorizationEndpoint.getPort(),
						authorizationEndpoint.getFile());
				return newUrl;
			} catch (MalformedURLException e) {
				// this shouldn't happen
				return authorizationEndpoint;
			}
		}
		return authorizationEndpoint;
	}

	public URL getCloudControllerUrl() {
		return this.cloudControllerUrl;
	}

	public List<CloudSpace> getSpaces() {
		ArrayList<CloudSpace> list = new ArrayList<CloudSpace>();
		return list;
	}

	public List<String> getApplicationPlans() {
		return Collections.unmodifiableList(freeApplicationPlans);
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
		Integer memory = FRAMEWORK_DEFAULT_MEMORY.get(framework);
		if (memory == null) {
			return DEFAULT_MEMORY;
		}
		return memory;
	}

	public void updatePassword(String newPassword) {
		updatePassword(cloudCredentials, newPassword);
	}

	public void updateHttpProxyConfiguration(HttpProxyConfiguration httpProxyConfiguration) {
		ClientHttpRequestFactory requestFactory = restUtil.createRequestFactory(httpProxyConfiguration);
		restTemplate.setRequestFactory(requestFactory);
		configureCloudFoundryRequestFactory(restTemplate);
	}

	public void updateApplicationPlan(String appName, String applicationPlan) {
		// subclasses should override this method if they support application plans
	}

	public Map<String, String> getLogs(String appName) {
		String urlPath = getFileUrlPath();
		String instance = String.valueOf(0);
		return doGetLogs(urlPath, appName, instance);
	}

	public Map<String, String> getCrashLogs(String appName) {
		String urlPath = getFileUrlPath();
		CrashesInfo crashes = getCrashes(appName);
		TreeMap<Date, String> crashInstances = new TreeMap<Date, String>();
		for (CrashInfo crash : crashes.getCrashes()) {
			crashInstances.put(crash.getSince(), crash.getInstance());
		}
		String instance = crashInstances.get(crashInstances.lastKey());
		return doGetLogs(urlPath, appName, instance);
	}

    public List<String> getStagingLogs(StartingInfo info) {
        ArrayList<String> logs = new ArrayList<String>();
        String stagingFile = info.getStagingFile();
        if (stagingFile != null) {
            // staging logs may come in multiple parts
            int offset=0;
            String decodedStagingFile = stagingFile;
            try {
                decodedStagingFile = URLDecoder.decode(stagingFile, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                logger.error("unexpected inability to UTF-8 decode", e);
            }
            HashMap<String, Object> logsRequest = new HashMap<String, Object>();
            for (int i = 0; i < 60; i++) {
                try {
                    logsRequest.put("offset", offset);
                    String logObject = getRestTemplate().getForObject(decodedStagingFile + "&tail&tail_offset={offset}", String.class, logsRequest);
                    offset += logObject.length();
                    if (logObject.length() == 0) {
                        break; //try to avoid asking again and generating parasite 404 warn traces.
                    }
                    String[] lines = logObject.split("\n");
                    logs.addAll(Arrays.asList(lines));
                    if (logger.isDebugEnabled()) {
                        logger.debug("staging logs are:\n" + logObject);
                    }
                } catch (CloudFoundryException e) {
                    //likely 404, the directory server won't serve again the content, too bad
                    logger.debug("caught exception during retry#" + i + " for fetching staging logs. Aborting. Caught:" + e, e);
                    break;
                } catch (ResourceAccessException e) {
                    //Likely read timeout, the directory server won't serve again the content, too bad
                    logger.debug("caught exception during retry#" + i + " for fetching staging logs. Aborting. Caught:" + e, e);
                    break;
                }
            }
        }
        return logs;
    }

	public String getFile(String appName, int instanceIndex, String filePath, int startPosition, int endPosition) {
		String urlPath = getFileUrlPath();
		Object appId = getFileAppId(appName);
		return doGetFile(urlPath, appId, instanceIndex, filePath, startPosition, endPosition);
	}

	public List<CloudDomain> getDomainsForOrg() {
		// subclasses that support this feature must override this
		throw new UnsupportedOperationException("Feature is not implemented for this version.");
	}

	public List<CloudDomain> getDomains() {
		// subclasses that support this feature must override this
		throw new UnsupportedOperationException("Feature is not implemented for this version.");
	}

	public void addDomain(String domainName) {
		// subclasses that support this feature must override this
		throw new UnsupportedOperationException("Feature is not implemented for this version.");
	}

	public void deleteDomain(String domainName) {
		// subclasses that support this feature must override this
		throw new UnsupportedOperationException("Feature is not implemented for this version.");
	}

	public void removeDomain(String domainName) {
		// subclasses that support this feature must override this
	}

	public List<CloudRoute> getRoutes(String domainName) {
		// subclasses that support this feature must override this
		throw new UnsupportedOperationException("Feature is not implemented for this version.");
	}

	public void addRoute(String host, String domainName) {
		// subclasses that support this feature must override this
		throw new UnsupportedOperationException("Feature is not implemented for this version.");
	}

	public void deleteRoute(String host, String domainName) {
		// subclasses that support this feature must override this
		throw new UnsupportedOperationException("Feature is not implemented for this version.");
	}

	public void registerRestLogListener(RestLogCallback callBack) {
		if (getRestTemplate() instanceof LoggingRestTemplate) {
			((LoggingRestTemplate)getRestTemplate()).registerRestLogListener(callBack);
		}
	}

	public void unRegisterRestLogListener(RestLogCallback callBack) {
		if (getRestTemplate() instanceof LoggingRestTemplate) {
			((LoggingRestTemplate)getRestTemplate()).unRegisterRestLogListener(callBack);
		}
	}

	protected RestTemplate getRestTemplate() {
		return this.restTemplate;
	}

	protected String getUrl(String path) {
		return cloudControllerUrl + (path.startsWith("/") ? path : "/" + path);
	}

	protected abstract String getFileUrlPath();

	protected abstract Object getFileAppId(String appName);

	private void configureCloudFoundryRequestFactory(RestTemplate restTemplate) {
		ClientHttpRequestFactory requestFactory = restTemplate.getRequestFactory();
		restTemplate.setRequestFactory(
				new CloudFoundryClientHttpRequestFactory(requestFactory));
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

	private class CloudFoundryClientHttpRequestFactory implements ClientHttpRequestFactory {

		private static final String LEGACY_TOKEN_PREFIX = "0408";
        private Integer defaultSocketTimeout = 0;
        private ClientHttpRequestFactory delegate;

        public CloudFoundryClientHttpRequestFactory(ClientHttpRequestFactory delegate) {
			this.delegate = delegate;
            captureDefaultReadTimeout(delegate);
        }

        public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
			ClientHttpRequest request = delegate.createRequest(uri, httpMethod);
			if (token != null) {
				String header = token;
				if (!header.startsWith(LEGACY_TOKEN_PREFIX) && !header.toLowerCase().startsWith("bearer")) {
					header = "Bearer " + header; // UAA token without OAuth prefix
				}
				request.getHeaders().add(AUTHORIZATION_HEADER_KEY, header);
			}
			if (cloudCredentials != null && cloudCredentials.getProxyUser() != null) {
				request.getHeaders().add(PROXY_USER_HEADER_KEY, cloudCredentials.getProxyUser());
			}
            increaseReadTimeoutForStreamedTailedLogs(uri);

            return request;
		}

        private void captureDefaultReadTimeout(ClientHttpRequestFactory delegate) {
            if (delegate instanceof CommonsClientHttpRequestFactory) {
                CommonsClientHttpRequestFactory commonsClientHttpRequestFactory = (CommonsClientHttpRequestFactory) delegate;
                defaultSocketTimeout = (Integer) commonsClientHttpRequestFactory.getHttpClient().getParams().getParameter("http.socket.timeout");
                if (defaultSocketTimeout == null) {
                    try {
                        defaultSocketTimeout = new Socket().getSoTimeout();
                    } catch (SocketException e) {
                        defaultSocketTimeout = 0;
                    }
                }
            }
        }

        private void increaseReadTimeoutForStreamedTailedLogs(URI uri) {
            //May temporary increase readtimeout on other unrelated concurrent threads, but per-request read timeout don't seem easily accessible
            if (delegate instanceof CommonsClientHttpRequestFactory) {
                CommonsClientHttpRequestFactory commonsClientHttpRequestFactory = (CommonsClientHttpRequestFactory) delegate;
                String uriString = uri.toString();
                if (uriString.contains("staging_tasks") && uriString.contains("tail")) {
                    commonsClientHttpRequestFactory.setReadTimeout(5*60*1000);
                } else {
                    commonsClientHttpRequestFactory.setReadTimeout(defaultSocketTimeout);
                }
            }
        }
    }

	public static class ErrorHandler extends DefaultResponseErrorHandler {
		@Override
		public void handleError(ClientHttpResponse response) throws IOException {
			HttpStatus statusCode = response.getStatusCode();
			switch (statusCode.series()) {
				case CLIENT_ERROR:
					CloudFoundryException exception = new CloudFoundryException(statusCode, response.getStatusText());
					ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
					if (response.getBody() != null) {
						try {
							@SuppressWarnings("unchecked")
							Map<String, Object> map = mapper.readValue(response.getBody(), Map.class);
							exception.setDescription(CloudUtil.parse(String.class, map.get("description")));
						} catch (JsonParseException e) {
							exception.setDescription("Client error");
						} catch (IOException e) {
							exception.setDescription("Client error");
						}
					} else {
						exception.setDescription("Client error");
					}
					throw exception;
				case SERVER_ERROR:
					throw new HttpServerErrorException(statusCode, response.getStatusText());
				default:
					throw new RestClientException("Unknown status code [" + statusCode + "]");
			}
		}
	}

	public static class CloudFoundryFormHttpMessageConverter extends FormHttpMessageConverter {
		@Override
		protected String getFilename(Object part) {
			if (part instanceof UploadApplicationPayload) {
				return ((UploadApplicationPayload) part).getArchive().getFilename();
			}
			return super.getFilename(part);
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

		//simple retry
		int tries = 0;
		String response = null;
		while (response == null) {
			tries++;
			try {
				response = doGetFileByRange(urlPath, app, instance, filePath, start, end, range);
			} catch (HttpServerErrorException e) {
				if (e.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE)) {
					if (tries > FILES_MAX_RETRIES) {
						throw e;
					}
				} else {
					throw e;
				}
			}
		}

		return response;
	}

	private String doGetFileByRange(String urlPath, Object app, String instance, String filePath, int start, int end,
									String range) {

		boolean supportsRanges = false;
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
							if (response.getStatusCode().equals(HttpStatus.PARTIAL_CONTENT)) {
								return true;
							}
							return false;
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

}
