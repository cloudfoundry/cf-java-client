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
import org.cloudfoundry.client.lib.domain.CloudInfo;
import org.cloudfoundry.client.lib.domain.CloudOrganization;
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
import static org.junit.Assert.assertNotNull;
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

	// Pass -Dccng.target=http://api.cloudfoundry.com, vcap.me, or your own cloud -- must point to a v2 cloud controller
	private static final String CCNG_API_URL = System.getProperty("ccng.target", "http://ccng.cloudfoundry.com");

	private static final String CCNG_USER_EMAIL = System.getProperty("ccng.email", "java-authenticatedClient-test-user@vmware.com");

	private static final String CCNG_USER_PASS = System.getProperty("ccng.passwd");

	private static final String CCNG_USER_ORG = System.getProperty("ccng.org", "rbcon.com");

	private static final String CCNG_USER_SPACE = System.getProperty("ccng.space", "test");

	private static final String TEST_NAMESPACE = System.getProperty("vcap.test.namespace",
			defaultNamespace(CCNG_USER_EMAIL));

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
		cloudInfo = new CloudFoundryClient(new URL(CCNG_API_URL)).getCloudInfo();
		authenticatedClient = new CloudFoundryClient(new CloudCredentials(CCNG_USER_EMAIL, CCNG_USER_PASS), new URL(CCNG_API_URL));
		authenticatedClient.login();
		connectedClient = setTestSpaceAsDefault(authenticatedClient);
		connectedClient.deleteAllApplications();
		connectedClient.deleteAllServices();
	}

	@After
	public void tearDown() {
		connectedClient.deleteAllApplications();
		connectedClient.deleteAllServices();
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
			if (sc.getCloudServiceOffering().getLabel().equals("redis")) {
				configuration = sc;
				break;
			}
		}
		assertNotNull(configuration);
//		TODO: type is not currently part of the service definitions in v2
//		assertEquals("key-value", configuration.getType());
		assertEquals("redis", configuration.getCloudServiceOffering().getLabel());
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
				new CloudFoundryClient(new CloudCredentials(CCNG_USER_EMAIL, newPassword), new URL(CCNG_API_URL));
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
		assertEquals(Collections.singletonList(computeAppUrlNoProtocol(CCNG_API_URL, appName)), originalUris);

		List<String> uris = new ArrayList<String>(app.getUris());
		for (int i = 2; i < 55; i++) {
			uris.add(computeAppUrlNoProtocol(CCNG_API_URL, namespacedAppName("page-url" + i)));
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

	private CloudFoundryClient setTestSpaceAsDefault(CloudFoundryClient client) throws MalformedURLException {
		List<CloudSpace> spaces = client.getSpaces();
		CloudSpace testSpace = null;
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
		connectedClient = new CloudFoundryClient(new CloudCredentials(CCNG_USER_EMAIL, CCNG_USER_PASS), new URL(CCNG_API_URL), testSpace);
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
}
