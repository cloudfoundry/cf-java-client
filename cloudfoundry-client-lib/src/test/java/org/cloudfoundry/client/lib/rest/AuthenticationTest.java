package org.cloudfoundry.client.lib.rest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.HttpProxyConfiguration;
import org.cloudfoundry.client.lib.oauth2.OauthClient;
import org.cloudfoundry.client.lib.util.RestUtil;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

/**
 * Tests for authentication scenarios with 
 * {@link org.cloudfoundry.client.lib.rest.CloudControllerClientImpl}.
 *
 * @author Thomas Risberg
 */
public class AuthenticationTest {

	private static String INFO_WITH_AUTH = "{\"name\":\"vcap\",\"build\":2222,\"support\":" +
			"\"http://support.cloudfoundry.com\",\"version\":\"0.999\",\"description\":" +
			"\"VMware's Cloud Application Platform\",\"allow_debug\":false," +
			"\"authorization_endpoint\":\"https://uaa.cloud.me\"}";
	private static String INFO_WITHOUT_AUTH = "{\"name\":\"vcap\",\"build\":2222,\"support\":" +
			"\"http://support.cloudfoundry.com\",\"version\":\"0.999\",\"description\":" +
			"\"VMware's Cloud Application Platform\",\"allow_debug\":false}";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void loginTestForOauthClient() throws MalformedURLException {
		String token = "12345678";
		RestTemplate restTemplate = mock(RestTemplate.class);
		ClientHttpRequestFactory requestFactory = mock(ClientHttpRequestFactory.class);
		OAuth2AccessToken oauthToken = mock(OAuth2AccessToken.class);
		when(restTemplate.getRequestFactory()).thenReturn(requestFactory);
		when(restTemplate.execute(
				anyString(),
				any(HttpMethod.class),
				any(RequestCallback.class),
				any(ResponseExtractor.class),
				any(Map.class)
		)).thenReturn(oauthToken);
		when(oauthToken.getValue()).thenReturn(token);
		when(oauthToken.getTokenType()).thenReturn("bearer");

		// Run Test
		OauthClient oauthClient = new OauthClient(new URL("http://uaa.cloud.me"), restTemplate);
		OAuth2AccessToken ouathToken = oauthClient.getToken("test@cloud.me", "passwd");
		String loginToken = oauthToken.getValue();
		assertThat(loginToken, is(token));
	}

	@Test
	public void loginWithOauth2Authentication() throws MalformedURLException {
		String token = "12345678";
		RestTemplate restTemplate = mock(RestTemplate.class);
		ClientHttpRequestFactory clientHttpRequestFactory = mock(ClientHttpRequestFactory.class);
		RestUtil restUtil = mock(RestUtil.class);
		OAuth2AccessToken oauthToken = mock(OAuth2AccessToken.class);
		when(restUtil.createRestTemplate(any(HttpProxyConfiguration.class))).thenReturn(restTemplate);
		when(restUtil.createRequestFactory(any(HttpProxyConfiguration.class))).thenReturn(clientHttpRequestFactory);
		when(restTemplate.getForObject(
				eq("http://api.cloud.me/info"),
				any(Class.class)
		)).thenReturn(INFO_WITH_AUTH);
		when(restUtil.createOauthClient(any(URL.class), any(HttpProxyConfiguration.class))).thenReturn(
				new OauthClient(new URL("http://uaa.cloud.me"), restTemplate));
		when(restTemplate.execute(
				eq("http://uaa.cloud.me/oauth/authorize"),
				eq(HttpMethod.POST),
				any(RequestCallback.class),
		        any(ResponseExtractor.class),
				any(Map.class)
				)).thenReturn(oauthToken);
		when(oauthToken.getValue()).thenReturn(token);
		when(oauthToken.getTokenType()).thenReturn("bearer");

		// Run Test
		CloudControllerClientFactory ccf = new CloudControllerClientFactory(restUtil, null);
		CloudControllerClient ccc = ccf.newCloudController(
				new URL("http://api.cloud.me"),
				new CloudCredentials("test@cloud.me", "passwd"),
				null);
		String loginToken = ccc.login();
		assertThat(loginToken, is("bearer " + token));
	}

	@Test
	public void loginWithWrongPassword() throws MalformedURLException {
		thrown.expect(CloudFoundryException.class);
		RestTemplate restTemplate = mock(RestTemplate.class);
		ClientHttpRequestFactory clientHttpRequestFactory = mock(ClientHttpRequestFactory.class);
		RestUtil restUtil = mock(RestUtil.class);
		when(restUtil.createRestTemplate(any(HttpProxyConfiguration.class))).thenReturn(restTemplate);
		when(restUtil.createRequestFactory(any(HttpProxyConfiguration.class))).thenReturn(clientHttpRequestFactory);
		when(restTemplate.getForObject(
				eq("http://api.cloud.me/info"),
				any(Class.class)
		)).thenReturn(INFO_WITH_AUTH);
		when(restUtil.createOauthClient(any(URL.class), any(HttpProxyConfiguration.class))).thenReturn(
				new OauthClient(new URL("http://uaa.cloud.me"), restTemplate));
		when(restTemplate.execute(
				eq("http://uaa.cloud.me/oauth/authorize"),
				eq(HttpMethod.POST),
				any(RequestCallback.class),
		        any(ResponseExtractor.class),
				any(Map.class)
				)).thenThrow(new CloudFoundryException(HttpStatus.UNAUTHORIZED, "Error requesting access token."));

		// Run Test
		CloudControllerClientFactory ccf = new CloudControllerClientFactory(restUtil, null);
		CloudControllerClient ccc = ccf.newCloudController(
				new URL("http://api.cloud.me"),
				new CloudCredentials("test@cloud.me", "badpasswd"),
				null);
		ccc.login();
	}
}
