package org.cloudfoundry.client.lib.oauth2;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.client.RestTemplate;

import java.net.URL;


@RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class OauthClientTest {

	@Mock
	RestTemplate restTemplate;

	@Mock
	ResourceOwnerPasswordAccessTokenProvider provider;

	@Mock
	ClientHttpRequestFactory requestFactory;

	/**
	 * Verify proxies in original rest template are propagated into ResourceOwnerPasswordAccessTokenProvider as well.
	 *
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testGetTokenPreservesProxies() throws Exception {
		//given
		OauthClient oauthClient = new OauthClient(new URL("http://api.run.pivotal.io"), restTemplate) {
			@Override
			protected ResourceOwnerPasswordAccessTokenProvider createResourceOwnerPasswordAccessTokenProvider() {
				return provider;
			}
		};

		Mockito.when(restTemplate.getRequestFactory()).thenReturn(requestFactory);

		//when
		OAuth2AccessToken token = oauthClient.getToken("login", "password", "clientid");

		//then
		Mockito.verify(provider).setRequestFactory(requestFactory);
	}
}
