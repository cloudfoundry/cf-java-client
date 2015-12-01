package org.cloudfoundry.client.lib.util;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.cloudfoundry.client.lib.HttpProxyConfiguration;
import org.cloudfoundry.client.lib.oauth2.OauthClient;
import org.cloudfoundry.client.lib.rest.CloudControllerResponseErrorHandler;
import org.cloudfoundry.client.lib.rest.CloudFoundryFormHttpMessageConverter;
import org.cloudfoundry.client.lib.rest.LoggingRestTemplate;
import org.cloudfoundry.client.lib.rest.LoggregatorHttpMessageConverter;
import org.cloudfoundry.client.lib.rest.UploadApplicationPayloadHttpMessageConverter;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.http.conn.ssl.SSLSocketFactory.STRICT_HOSTNAME_VERIFIER;

/**
 * Some helper utilities for creating classes used for the REST support.
 *
 * @author Thomas Risberg
 */
public class RestUtil {

    public OauthClient createOauthClient(URL authorizationUrl, HttpProxyConfiguration httpProxyConfiguration, boolean
            trustSelfSignedCerts) {
        return new OauthClient(authorizationUrl, createRestTemplate(httpProxyConfiguration, trustSelfSignedCerts));
    }

    public ClientHttpRequestFactory createRequestFactory(HttpProxyConfiguration httpProxyConfiguration, boolean
            trustSelfSignedCerts) {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        DefaultHttpClient httpClient = (DefaultHttpClient) requestFactory.getHttpClient();

        if (trustSelfSignedCerts) {
            registerSslSocketFactory(httpClient);
        }

        if (httpProxyConfiguration != null) {
            if (httpProxyConfiguration.isAuthRequired()) {
                httpClient.getCredentialsProvider().setCredentials(
                        new AuthScope(httpProxyConfiguration.getProxyHost(), httpProxyConfiguration.getProxyPort()),
                        new UsernamePasswordCredentials(httpProxyConfiguration.getUsername(), httpProxyConfiguration
                                .getPassword()));
            }

            HttpHost proxy = new HttpHost(httpProxyConfiguration.getProxyHost(), httpProxyConfiguration.getProxyPort());
            httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        }

        return requestFactory;
    }

    public RestTemplate createRestTemplate(HttpProxyConfiguration httpProxyConfiguration, boolean
            trustSelfSignedCerts) {
        RestTemplate restTemplate = new LoggingRestTemplate();
        restTemplate.setRequestFactory(createRequestFactory(httpProxyConfiguration, trustSelfSignedCerts));
        restTemplate.setErrorHandler(new CloudControllerResponseErrorHandler());
        restTemplate.setMessageConverters(getHttpMessageConverters());

        return restTemplate;
    }

    private FormHttpMessageConverter getFormHttpMessageConverter() {
        FormHttpMessageConverter formPartsMessageConverter = new CloudFoundryFormHttpMessageConverter();
        formPartsMessageConverter.setPartConverters(getFormPartsMessageConverters());
        return formPartsMessageConverter;
    }

    private List<HttpMessageConverter<?>> getFormPartsMessageConverters() {
        List<HttpMessageConverter<?>> partConverters = new ArrayList<HttpMessageConverter<?>>();
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
        stringConverter.setSupportedMediaTypes(Collections.singletonList(JsonUtil.JSON_MEDIA_TYPE));
        stringConverter.setWriteAcceptCharset(false);
        partConverters.add(stringConverter);
        partConverters.add(new ResourceHttpMessageConverter());
        partConverters.add(new UploadApplicationPayloadHttpMessageConverter());
        return partConverters;
    }

    private List<HttpMessageConverter<?>> getHttpMessageConverters() {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(new ByteArrayHttpMessageConverter());
        messageConverters.add(new StringHttpMessageConverter());
        messageConverters.add(new ResourceHttpMessageConverter());
        messageConverters.add(new UploadApplicationPayloadHttpMessageConverter());
        messageConverters.add(getFormHttpMessageConverter());
        messageConverters.add(new MappingJacksonHttpMessageConverter());
        messageConverters.add(new LoggregatorHttpMessageConverter());
        return messageConverters;
    }

    private void registerSslSocketFactory(HttpClient httpClient) {
        try {
            SSLSocketFactory socketFactory = new SSLSocketFactory(new TrustSelfSignedStrategy(),
                    STRICT_HOSTNAME_VERIFIER);
            httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));
        } catch (GeneralSecurityException gse) {
            throw new RuntimeException("An error occurred setting up the SSLSocketFactory", gse);
        }
    }
}
