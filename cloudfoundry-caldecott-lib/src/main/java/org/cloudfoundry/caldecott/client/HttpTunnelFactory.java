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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cloudfoundry.client.lib.HttpProxyConfiguration;
import org.springframework.http.client.CommonsClientHttpRequestFactory;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

/**
 * The factory class used to create an HttpTunnel instance.
 *
 * @author Thomas Risberg
 */
public class HttpTunnelFactory implements TunnelFactory {

	protected final Log logger = LogFactory.getLog(getClass());

	private final String url;
	private final String host;
	private final int port;
	private final String auth;
	private RestOperations restOperations;
	private HttpProxyConfiguration httpProxyConfiguration;

	public HttpTunnelFactory(String url, String host, int port, String auth) {
		this.url = url;
		this.host = host;
		this.port = port;
		this.auth = auth;
	}

	public HttpTunnelFactory(String url, String host, int port, String auth, HttpProxyConfiguration httpProxyConfiguration) {
		this(url, host, port, auth);
		this.httpProxyConfiguration = httpProxyConfiguration;
	}

	public HttpTunnelFactory(String url, String host, int port, String auth, RestOperations restOperations) {
		this(url, host, port, auth);
		this.restOperations = restOperations;
	}

	public Tunnel createTunnel() {
		if (logger.isDebugEnabled()) {
			logger.debug("Creating HttpTunnel for " + url + " on " + host + ":" + port);
		}
		if (restOperations!= null) {
			return new HttpTunnel(url, host, port, auth, restOperations);
		}
		else {
			return new HttpTunnel(url, host, port, auth, createRestTemplate());
		}
	}

	private RestTemplate createRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		CommonsClientHttpRequestFactory requestFactory = new CommonsClientHttpRequestFactory();
		requestFactory.setConnectTimeout(20000);
		requestFactory.setReadTimeout(20000);
		if (httpProxyConfiguration != null) {
			requestFactory.getHttpClient().getHostConfiguration().setProxy(httpProxyConfiguration.getProxyHost(),
					httpProxyConfiguration.getProxyPort());
		}
		restTemplate.setRequestFactory(requestFactory);
		return restTemplate;
	}

}
