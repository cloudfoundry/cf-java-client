package org.cloudfoundry.client.lib.rest;

import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.HttpProxyConfiguration;
import org.cloudfoundry.client.lib.oauth2.OauthClient;
import org.cloudfoundry.client.lib.util.RestUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class CloudControllerClientImplTest {

	private static final String CCNG_API_URL = System.getProperty("ccng.target", "http://api.run.pivotal.io");
	private static final String CCNG_USER_EMAIL = System.getProperty("ccng.email", "java-authenticatedClient-test-user@vmware.com");
	private static final String CCNG_USER_PASS = System.getProperty("ccng.passwd");
	private static final String CCNG_USER_ORG = System.getProperty("ccng.org", "gopivotal.com");
	private static final String CCNG_USER_SPACE = System.getProperty("ccng.space", "test");

	@Mock
	private RestUtil restUtil;
	@Mock
	private RestTemplate restTemplate;
	@Mock
	private ClientHttpRequestFactory clientHttpRequestFactory;
	@Mock
	private OauthClient oauthClient;
	@Mock
	private LoggregatorClient loggregatorClient;

	private CloudControllerClientImpl controllerClient;

	/**
	 * Failed attempt to instantiate CloudControllerClientImpl with existing constructors. Just here to illustrate the
	 * need to move the initialize() method out of the constructor.
	 */
	public void setUpWithNonEmptyConstructorWithoutLuck() throws Exception {
		restUtil = mock(RestUtil.class);
		when(restUtil.createRestTemplate(any(HttpProxyConfiguration.class), false)).thenReturn(restTemplate);
		when(restUtil.createOauthClient(any(URL.class), any(HttpProxyConfiguration.class), false)).thenReturn(oauthClient);
		when(restTemplate.getRequestFactory()).thenReturn(clientHttpRequestFactory);

		restUtil.createRestTemplate(null, false);
		restUtil.createOauthClient(new URL(CCNG_API_URL), null, false);

		controllerClient = new CloudControllerClientImpl(new URL("http://api.dummyendpoint.com/login"),
				restTemplate, oauthClient, loggregatorClient,
				new CloudCredentials(CCNG_USER_EMAIL, CCNG_USER_PASS),
				CCNG_USER_ORG, CCNG_USER_SPACE);
	}

	@Before
	public void setUpWithEmptyConstructor() throws Exception {
		controllerClient = new CloudControllerClientImpl();
	}

	@Test
	public void extractUriInfo_selects_most_specific_subdomain() throws Exception {
		//given
		String uri = "myhost.sub1.sub2.domain.com";
		Map<String, UUID> domains = new LinkedHashMap<String, UUID>(); //Since impl iterates key, need to control iteration order with a LinkedHashMap
		domains.put("domain.com", UUID.randomUUID());
		domains.put("sub1.sub2.domain.com", UUID.randomUUID());
		Map<String, String> uriInfo = new HashMap<String, String>(2);

		//when
		controllerClient.extractUriInfo(domains, uri, uriInfo);

		//then
		Assert.assertEquals(domains.get("sub1.sub2.domain.com"), domains.get(uriInfo.get("domainName")));
		Assert.assertEquals("myhost", uriInfo.get("host"));
	}

}
