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

package org.cloudfoundry.client.lib;

import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudDomain;
import org.cloudfoundry.client.lib.domain.CloudInfo;
import org.cloudfoundry.client.lib.domain.CloudOrganization;
import org.cloudfoundry.client.lib.domain.CloudRoute;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.cloudfoundry.client.lib.domain.ServiceConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;
import static org.junit.Assert.fail;

/**
 * Test class for V2 cloud controller.
 *
 * You need to create organization, space and user account via the portal and set these values using
 * system properties.
 *
 * @author Thomas Risberg
 */
@CloudVersions({"V2"})
public class CloudFoundryClientV2Test extends AbstractCloudFoundryClientTest {

	private CloudFoundryClient authenticatedClient;

	private CloudFoundryClient connectedClient;

	private CloudInfo cloudInfo;

	private CloudSpace testSpace = null;

	// Pass -Dccng.target=http://api.cloudfoundry.com, vcap.me, or your own cloud -- must point to a v2 cloud controller
	private static final String CCNG_API_URL = System.getProperty("ccng.target", "http://ccng.cloudfoundry.com");

    private static final String CCNG_API_PROXY_HOST = System.getProperty("http.proxyHost", null);

    private static final int CCNG_API_PROXY_PORT = Integer.getInteger("http.proxyPort", 80);

	private static final String CCNG_USER_EMAIL = System.getProperty("ccng.email", "java-authenticatedClient-test-user@vmware.com");

	private static final String CCNG_USER_PASS = System.getProperty("ccng.passwd");

	private static final String CCNG_USER_ORG = System.getProperty("ccng.org", "rbcon.com");

	private static final String CCNG_USER_SPACE = System.getProperty("ccng.space", "test");

	private static final String TEST_NAMESPACE = System.getProperty("vcap.test.namespace", defaultNamespace(CCNG_USER_EMAIL));

	private static final  String TEST_DOMAIN = System.getProperty("vcap.test.domain", defaultNamespace(CCNG_USER_EMAIL) + ".com");
	private static String defaultDomainName = null;
    private HttpProxyConfiguration httpProxyConfiguration;
	
	@BeforeClass
	public static void printTargetInfo() {
		System.out.println("Running tests on " + CCNG_API_URL + " on behalf of " + CCNG_USER_EMAIL);
		System.out.println("Using space " + CCNG_USER_SPACE + " of organization " + CCNG_USER_ORG );
		if (CCNG_USER_PASS == null) {
			fail("System property ccng.passwd must be specified, supply -Dccng.passwd=<password>");
		}

	}

	@Before
	public void setUp() throws MalformedURLException {
        if (CCNG_API_PROXY_HOST != null) {
            httpProxyConfiguration = new HttpProxyConfiguration(CCNG_API_PROXY_HOST, CCNG_API_PROXY_PORT);
        }
        cloudInfo = new CloudFoundryClient(new URL(CCNG_API_URL), httpProxyConfiguration).getCloudInfo();
		authenticatedClient = new CloudFoundryClient(new CloudCredentials(CCNG_USER_EMAIL, CCNG_USER_PASS), new URL(CCNG_API_URL), httpProxyConfiguration);
		authenticatedClient.login();
		connectedClient = setTestSpaceAsDefault(authenticatedClient);
		connectedClient.deleteAllApplications();
		connectedClient.deleteAllServices();
		clearTestDomainAndRoutes();
		connectedClient.addDomain(TEST_DOMAIN);
		defaultDomainName = getDefaultDomain(getConnectedClient().getDomainsForOrg()).getName();
	}

	@After
	public void tearDown() {
		connectedClient.deleteAllApplications();
		connectedClient.deleteAllServices();
		clearTestDomainAndRoutes();
	}

	@Test
	public void infoAvailableAndIsV2() throws Exception {
		CloudInfo info = authenticatedClient.getCloudInfo();
		assertNotNull(info.getName());
		assertNotNull(info.getSupport());
		assertNotNull(info.getBuild());
		assertEquals(CloudInfo.CC_MAJOR_VERSION.V2, info.getCloudControllerMajorVersion());
	}

	@Test
	public void spacesAvailable() throws Exception {
		List<CloudSpace> spaces = authenticatedClient.getSpaces();
		assertNotNull(spaces);
		assertTrue(spaces.size() > 0);
	}	
	
	@Test
	public void startApplicationWithInfo() throws IOException {
		String appName = createSpringTravelApp("start", null);
		uploadSpringTravelApp(appName);
		StartingInfo info = getConnectedClient().startApplication(appName);
		CloudApplication app = getConnectedClient().getApplication(appName);
		assertEquals(CloudApplication.AppState.STARTED, app.getState());
		assertNotNull(info);
		assertNotNull(info.getStagingFile());
	}

	@Test
	public void canSetDefaultSpace() throws Exception {
		setTestSpaceAsDefault(authenticatedClient);
	}

	@Test
	public void getServiceConfigurations() {
		List<ServiceConfiguration> configurations = connectedClient.getServiceConfigurations();

		assertNotNull(configurations);
		assertTrue(configurations.size() >= 2);

		ServiceConfiguration configuration = null;
		for (ServiceConfiguration sc : configurations) {
			if (sc.getCloudServiceOffering().getLabel().equals(getMysqlLabel())) {
				configuration = sc;
				break;
			}
		}
		assertNotNull(configuration);
//		TODO: type is not currently part of the service definitions in v2
//		assertEquals("key-value", configuration.getType());
		assertEquals(getMysqlLabel(), configuration.getCloudServiceOffering().getLabel());
		assertNotNull(configuration.getCloudServiceOffering().getCloudServicePlans());
		assertTrue(configuration.getCloudServiceOffering().getCloudServicePlans().size() > 0);
	}

	@Test
	public void updatePassword() throws MalformedURLException {
		// Not working currently
		assumeTrue(false);

		String newPassword = "newPass123";
		connectedClient.updatePassword(newPassword);
		CloudFoundryClient clientWithChangedPassword =
				new CloudFoundryClient(new CloudCredentials(CCNG_USER_EMAIL, newPassword), new URL(CCNG_API_URL), httpProxyConfiguration);
		clientWithChangedPassword.login();

		// Revert
		connectedClient.updatePassword(CCNG_USER_PASS);
		connectedClient.login();
	}

	@Test
	public void paginationWorksForUris() throws IOException {
		String appName = namespacedAppName("page-url1");
		CloudApplication app = createAndUploadSimpleTestApp(appName);
		//connectedClient.startApplication(appName);

		List<String> originalUris = app.getUris();
		assertEquals(Collections.singletonList(computeAppUrl(appName)), originalUris);

		List<String> uris = new ArrayList<String>(app.getUris());
		for (int i = 2; i < 55; i++) {
			uris.add(computeAppUrl(namespacedAppName("page-url" + i)));
		}
		connectedClient.updateApplicationUris(appName, uris);

		app = connectedClient.getApplication(appName);
		List<String> appUris = app.getUris();
		assertNotNull(appUris);
		assertEquals(uris.size(), appUris.size());
		for (String uri : uris) {
			assertTrue("Missing URI: " + uri, appUris.contains(uri));
		}
		connectedClient.deleteApplication(appName);
	}

	@Test
	public void manageDomainsAndRoutes() throws IOException {
		// Test that default domain is found
		List<CloudDomain> allDomains = getConnectedClient().getDomainsForOrg();
		CloudDomain defaultDomain = getDefaultDomain(allDomains);
		assertNotNull(getDefaultDomain(allDomains));

		// Test adding test domain - should be there for org and space
		getConnectedClient().addDomain(TEST_DOMAIN);
		allDomains = getConnectedClient().getDomainsForOrg();
		assertNotNull(getDefaultDomain(allDomains));
		assertNotNull(getDomainNamed(TEST_DOMAIN, allDomains));
		List<CloudDomain> spaceDomains = getConnectedClient().getDomains();
		assertNotNull(getDefaultDomain(spaceDomains));
		assertNotNull(getDomainNamed(TEST_DOMAIN, spaceDomains));

		// Test removing test domain from space
		getConnectedClient().removeDomain(TEST_DOMAIN);
		allDomains = getConnectedClient().getDomainsForOrg();
		assertNotNull(getDefaultDomain(allDomains));
		assertNotNull(getDomainNamed(TEST_DOMAIN, allDomains));
		spaceDomains = getConnectedClient().getDomains();
		assertNotNull(getDefaultDomain(spaceDomains));
		assertNull(getDomainNamed(TEST_DOMAIN, spaceDomains));

		// Test accessing/adding/deleting routes
		getConnectedClient().addDomain(TEST_DOMAIN);
		getConnectedClient().addRoute("my_route1", TEST_DOMAIN);
		getConnectedClient().addRoute("my_route2", TEST_DOMAIN);
		List<CloudRoute> routes = getConnectedClient().getRoutes(TEST_DOMAIN);
		assertNotNull(getRouteWithHost("my_route1", routes));
		assertNotNull(getRouteWithHost("my_route2", routes));
		getConnectedClient().deleteRoute("my_route2", TEST_DOMAIN);
		routes = getConnectedClient().getRoutes(TEST_DOMAIN);
		assertNotNull(getRouteWithHost("my_route1", routes));
		assertNull(getRouteWithHost("my_route2", routes));

		// Test that apps with route are counted
		String appName = namespacedAppName("my_route3");
		CloudApplication app = createAndUploadSimpleTestApp(appName);
		List<String> uris = app.getUris();
		uris.add("my_route3." + TEST_DOMAIN);
		getConnectedClient().updateApplicationUris(appName, uris);
		routes = getConnectedClient().getRoutes(TEST_DOMAIN);
		assertNotNull(getRouteWithHost("my_route1", routes));
		assertNotNull(getRouteWithHost("my_route3", routes));
		assertEquals(0, getRouteWithHost("my_route1", routes).getAppsUsingRoute());
		assertFalse(getRouteWithHost("my_route1", routes).inUse());
		assertEquals(1, getRouteWithHost("my_route3", routes).getAppsUsingRoute());
		assertTrue(getRouteWithHost("my_route3", routes).inUse());
		List<CloudRoute> defaultDomainRoutes = getConnectedClient().getRoutes(defaultDomain.getName());
		assertNotNull(getRouteWithHost(appName, defaultDomainRoutes));
		assertEquals(1, getRouteWithHost(appName, defaultDomainRoutes).getAppsUsingRoute());
		assertTrue(getRouteWithHost(appName, defaultDomainRoutes).inUse());

		// test that removing domain that has routes throws exception
		try {
			getConnectedClient().deleteDomain(TEST_DOMAIN);
			fail("should have thrown exception");
		}
		catch (IllegalStateException ex) {
			assertTrue(ex.getMessage().contains("in use"));
		}
	}

	@Test
	public void canDeployPaidApplication() throws IOException {
		String appName = namespacedAppName("prod1");
		CloudApplication app = createAndUploadSimpleTestApp(appName);
		assertNotNull(app);
		assertEquals("paid", app.getPlan());
	}

	@Test
	public void canChangeApplicationPlan() throws IOException {
		String appName = namespacedAppName("prod2");
		CloudApplication app = createAndUploadSimpleTestApp(appName);
		assertNotNull(app);
		assertEquals("paid", app.getPlan());
		getConnectedClient().updateApplicationPlan(appName, "free");
		app = getConnectedClient().getApplication(appName);
		assertEquals("free", app.getPlan());
	}

	@Test
	public void getApplicationPlans() throws IOException {
		List<String> appPlans = getConnectedClient().getApplicationPlans();
		assertNotNull(appPlans);
		assertTrue(appPlans.contains("free"));
		if (testSpace.getOrganization().isBillingEnabled()) {
			assertEquals(2, appPlans.size());
			assertTrue(appPlans.contains("paid"));
		} else {
			assertEquals(1, appPlans.size());
		}
	}

	private void clearTestDomainAndRoutes() {
		for (CloudDomain domain : getConnectedClient().getDomainsForOrg()) {
			if (domain.getName().equals(TEST_DOMAIN)) {
				List<CloudRoute> routes = getConnectedClient().getRoutes(TEST_DOMAIN);
				for (CloudRoute route : routes) {
					connectedClient.deleteRoute(route.getHost(), route.getDomain().getName());
				}
				getConnectedClient().deleteDomain(TEST_DOMAIN);
			}
		}
	}

	private CloudRoute getRouteWithHost(String hostName, List<CloudRoute> routes) {
		for (CloudRoute route : routes) {
			if (route.getHost().equals(hostName)) {
				return route;
			}
		}
		return null;
	}

	private CloudDomain getDomainNamed(String domainName, List<CloudDomain> domains) {
		for (CloudDomain domain : domains) {
			if (domain.getName().equals(domainName)) {
				return domain;
			}
		}
		return null;
	}

	private CloudDomain getDefaultDomain(List<CloudDomain> domains) {
		for (CloudDomain domain : domains) {
			if (domain.getOwner().getName().equals("none")) {
				return domain;
			}
		}
		return null;
	}

	private CloudFoundryClient setTestSpaceAsDefault(CloudFoundryClient client) throws MalformedURLException {
		List<CloudSpace> spaces = client.getSpaces();
		for (CloudSpace space : spaces) {
			CloudOrganization org = space.getOrganization();
			String orgName = null;
			if (org != null) {
				orgName = org.getName();
			}
			if (CCNG_USER_ORG.equals(orgName) && CCNG_USER_SPACE.equals(space.getName())) {
				testSpace = space;
			}
		}
		assertNotNull("Space to use for testing (" + CCNG_USER_SPACE + ") not found for organization (" +
				CCNG_USER_ORG + ") - check your account or system properties", testSpace);
		connectedClient = new CloudFoundryClient(new CloudCredentials(CCNG_USER_EMAIL, CCNG_USER_PASS), new URL(CCNG_API_URL), httpProxyConfiguration, testSpace);
		connectedClient.login();
		return connectedClient;
	}

	// Getters for test config

	@Override
	protected String getApiUrl() {
		return CCNG_API_URL;
	}

	@Override
	protected CloudInfo getInfo() {
		return cloudInfo;
	}

	@Override
	protected String getNamespace() {
		return TEST_NAMESPACE;
	}

	@Override
	protected CloudFoundryClient getConnectedClient() {
		return connectedClient;
	}

	@Override
	protected String getTestUser() {
		return CCNG_USER_EMAIL;
	}

	@Override
	protected int getTestAppMemory(String framework) {
		return connectedClient.getDefaultApplicationMemory(framework);
	}

	@Override
	protected boolean getCompleteApiSupported() {
		return true;
	}
	
	@Override
	protected String computeAppUrl(String appName) {
		return appName + "." + defaultDomainName;
	}

	@Override
	protected String computeAppUrlNoProtocol(String appName) {
		return computeAppUrl(appName);
	}
	
	@Override
	protected String getMysqlLabel() {
		return "cleardb";
	}
	
}
