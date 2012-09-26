/*
 * Copyright 2009-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.caldecott.client;

import org.cloudfoundry.caldecott.TunnelException;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.domain.Staging;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A utility class for accessing information regarding tunnels and data services.
 *
 * @author Thomas Risberg
 */
public class TunnelHelper {

	private static final String TUNNEL_APP_NAME = "caldecott";
	private static final String[] TUNNEL_URI_SCHEMES = {"https:", "http:"};
	private static final String TUNNEL_AUTH_KEY = "CALDECOTT_AUTH";
	private static final Map<String, String> TUNNEL_URI_CACHE = new ConcurrentHashMap<String, String>();

	private static final RestTemplate restTemplate = new RestTemplate();

	private static ObjectMapper objectMapper = new ObjectMapper();

	public static String getTunnelAppName() {
		return TUNNEL_APP_NAME;
	}

	public static CloudApplication getTunnelAppInfo(CloudFoundryClient client) {
		return client.getApplication(TunnelHelper.getTunnelAppName());
	}

	public static void deployTunnelApp(CloudFoundryClient client) {
		ClassPathResource cpr = new ClassPathResource("caldecott_helper.zip");
		try {
			File temp = copyCaldecottZipFile(cpr);
			client.createApplication(TUNNEL_APP_NAME, new Staging("ruby19", "sinatra"), 64,
					Arrays.asList(new String[]{getRandomUrl(client, TUNNEL_APP_NAME)}),
					Arrays.asList(new String[] {}), false);
			client.uploadApplication(TUNNEL_APP_NAME, temp);
			client.updateApplicationEnv(TUNNEL_APP_NAME,
					Collections.singletonMap("CALDECOTT_AUTH", UUID.randomUUID().toString()));
			client.startApplication(TUNNEL_APP_NAME);
			temp.delete();
		} catch (IOException e) {
			throw new TunnelException("Unable to deploy the Caldecott server application", e);
		}
	}

	public static void bindServiceToTunnelApp(CloudFoundryClient client, String serviceName) {
		if (getTunnelAppInfo(client).getServices().contains(serviceName)) {
			return;
		}
		client.stopApplication(getTunnelAppName());
		client.bindService(getTunnelAppName(), serviceName);
		client.startApplication(getTunnelAppName());
	}

	public static String getTunnelUri(CloudFoundryClient client) {
		String uriAuthority = client.getApplication(TunnelHelper.getTunnelAppName()).getUris().get(0);
		if (TUNNEL_URI_CACHE.containsKey(uriAuthority)) {
			return TUNNEL_URI_CACHE.get(uriAuthority);
		}
		String uriScheme = testUriSchemes(client, TUNNEL_URI_SCHEMES, uriAuthority);
		String uri = uriScheme + "//" + uriAuthority;
		TUNNEL_URI_CACHE.put(uriAuthority, uri);
		return uri;
	}

	public static String getTunnelAuth(CloudFoundryClient client) {
		String auth = client.getApplication(TunnelHelper.getTunnelAppName()).getEnvAsMap().get(TUNNEL_AUTH_KEY);
		return auth;
	}

	public static Map<String, String> getTunnelServiceInfo(CloudFoundryClient client, String serviceName) {
		String urlToUse = getTunnelUri(client) + "/services/" + serviceName;
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Auth-Token", getTunnelAuth(client));
		HttpEntity<?> requestEntity = new HttpEntity(requestHeaders);
		HttpEntity<String> response = restTemplate.exchange(urlToUse, HttpMethod.GET, requestEntity, String.class);
		String json = response.getBody().trim();
		Map<String, String> svcInfo = new HashMap<String, String>();
		try {
			svcInfo = convertJsonToMap(json);
		} catch (IOException e) {
			return new HashMap<String, String>();
		}
		if (svcInfo.containsKey("url")) {
			String svcUrl = svcInfo.get("url");
			try {
				URI uri = new URI(svcUrl);
				String[] userInfo;
				if (uri.getUserInfo().contains(":")) {
					userInfo = uri.getUserInfo().split(":");
				}
				else {
					userInfo = new String[2];
					userInfo[0] = uri.getUserInfo();
					userInfo[1] = "";
				}
				svcInfo.put("user", userInfo[0]);
				svcInfo.put("username", userInfo[0]);
				svcInfo.put("password", userInfo[1]);
				svcInfo.put("host", uri.getHost());
				svcInfo.put("hostname", uri.getHost());
				svcInfo.put("port", ""+uri.getPort());
				svcInfo.put("path", (uri.getPath().startsWith("/") ? uri.getPath().substring(1): uri.getPath()));
				svcInfo.put("vhost", svcInfo.get("path"));
			} catch (URISyntaxException e) {}
		}
		return svcInfo;
	}

	private static String testUriSchemes(CloudFoundryClient client, String[] uriSchemes, String uriAuthority) {
		int i = 0;
		int retries = 0;
		String scheme = null;
		while (i < uriSchemes.length) {
			scheme = uriSchemes[i];
			String uriToUse = scheme + "//" + uriAuthority;
			try {
				getTunnelProtocolVersion(client, uriToUse);
				break;
			} catch (HttpClientErrorException e) {
				if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
					if (retries < 10) {
						retries++;
					} else {
						throw new TunnelException("Not able to locate tunnel server at: " + uriToUse, e);
					}
				} else {
					throw new TunnelException("Error accessing tunnel server at: " + uriToUse, e);
				}
			} catch (ResourceAccessException e) {
				if (e.getMessage().contains("refused") || e.getMessage().contains("unable")) {
					i++;
				}
				else {
					throw e;
				}
			} catch (RuntimeException e) {
				throw e;
			}
		}
		return scheme;
	}

	private static File copyCaldecottZipFile(ClassPathResource cpr) throws IOException {
		File temp = File.createTempFile("caldecott", "zip");
		InputStream in = cpr.getInputStream();
		OutputStream out = new FileOutputStream(temp);
		int read = 0;
		byte[] bytes = new byte[1024];
		while ((read = in.read(bytes)) != -1) {
			out.write(bytes, 0, read);
		}
		in.close();
		out.flush();
		out.close();
		return temp;
	}

	public static String getRandomUrl(CloudFoundryClient client, String appname) {
		int range = 0x01000000;
		int r = new Random().nextInt(range - 1);
		StringBuilder url = new StringBuilder();
		url.append(appname);
		url.append("-");
		url.append(Integer.toHexString((r)));
		String domain = client.getCloudControllerUrl().getHost();
		if (domain.startsWith("api.")) {
			domain = domain.substring(3);
		}
		else {
			url.append("-");
		}
		url.append(domain);
		return url.toString();
	}

	public static String getTunnelProtocolVersion(CloudFoundryClient client, String uri) {
		String uriToUse = uri + "/info";
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Auth-Token", getTunnelAuth(client));
		HttpEntity<?> requestEntity = new HttpEntity(requestHeaders);
		HttpEntity<String> response = restTemplate.exchange(uriToUse, HttpMethod.GET, requestEntity, String.class);
		return response.getBody().trim();
	}

	public static Map<String, String> convertJsonToMap(String json) throws IOException {
		Map<String, String> svcInfo =
				objectMapper.readValue(json, TypeFactory.mapType(HashMap.class, String.class, String.class));
		return svcInfo;
	}

}
