package org.cloudfoundry.client.lib.util;

import org.cloudfoundry.client.lib.HttpProxyConfiguration;
import org.cloudfoundry.client.lib.rest.LoggingRestTemplate;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.CommonsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Some helper utilities for creating classes used for the REST support.
 *
 * @author: Thomas Risberg
 */
public class RestUtil {

	public static RestTemplate createRestTemplate(HttpProxyConfiguration httpProxyConfiguration) {
		RestTemplate restTemplate = new LoggingRestTemplate();
		restTemplate.setRequestFactory(createRequestFactory(httpProxyConfiguration));
		return restTemplate;
	}

	public static ClientHttpRequestFactory createRequestFactory(HttpProxyConfiguration httpProxyConfiguration) {
		CommonsClientHttpRequestFactory requestFactory = new CommonsClientHttpRequestFactory();
		if (httpProxyConfiguration != null) {
			requestFactory.getHttpClient().getHostConfiguration().setProxy(httpProxyConfiguration.getProxyHost(),
					httpProxyConfiguration.getProxyPort());
		}
		return requestFactory;
	}
}
