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

import org.cloudfoundry.client.lib.domain.CloudInfo;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.cloudfoundry.client.lib.domain.ServiceConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.net.MalformedURLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

/**
 *
 * @author Thomas Risberg
 */
public class CloudFoundryClientTestV2 {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private CloudFoundryClient client;

	// Pass -Dccng.target=http://api.cloudfoundry.com, vcap.me, or your own transfer
	private static final String ccUrl = System.getProperty("ccng.target", "http://ccng.cloudfoundry.com");

	private static final String TEST_USER_EMAIL = System.getProperty("ccng.email", "java-client-test-user@vmware.com");

	private static final String TEST_USER_PASS = System.getProperty("ccng.passwd");

	private static final String TEST_ADMIN_EMAIL = System.getProperty("ccng.admin.email");

	private static final String TEST_ADMIN_PASS = System.getProperty("ccng.admin.passwd");

	private static final String TEST_NAMESPACE = System.getProperty("ccng.test.namespace",
			TEST_USER_EMAIL.substring(0, TEST_USER_EMAIL.indexOf('@')).replaceAll("\\.", "_"));

	private final boolean serviceSupported = !ccUrl.contains("vmforce");

	private boolean multiUrlSupported = !ccUrl.contains("vmforce");

	private String token;

	@BeforeClass
	public static void printTargetInfo() {
		System.out.println("Running tests on " + ccUrl + " on behalf of " + TEST_USER_EMAIL);
		if (TEST_USER_PASS == null) {
			fail("System property ccng.passwd must be specified, supply -Dccng.passwd=<password>");
		}
	}

	@Before
	public void setUp() throws MalformedURLException {
		client = new CloudFoundryClient(TEST_USER_EMAIL, TEST_USER_PASS, ccUrl);
		try {
			client.register(TEST_USER_EMAIL, TEST_USER_PASS);
		}
		catch (Exception ex) {
			// Ignore... may happen if tear down failed to run properly
			// or the user was register outside this test or we don't have
			// privileges to register a new user.
			// Even if we make a wrong assumption here, the login that follows
			// will fail.
		}
		this.token = client.login();
		System.out.println("Using token: " + this.token);
		List<CloudSpace> spaces = client.getSpaces();
		CloudSpace test = null;
		for (CloudSpace space : spaces) {
			if ("test".equals(space.getName())) {
				test = space;
			}
		}
		client.setCurrentSpace(test);
		System.out.println("Using space " + test.getName());
//		List<CloudApplication> apps = client.getApplications();
//		assertEquals(0, apps.size());
	}

	@After
	public void tearDown() {
//		client.login(); // in case a test logged out (currently logout())
//		client.deleteAllApplications();
//		client.deleteAllServices();
	}

	//@Test
	public void accessThroughJustToken() throws MalformedURLException {
		String token = client.login();
		// Use the token to create a new client based on that token
		CloudFoundryClient tokenBasedClient = new CloudFoundryClient(token, ccUrl);
		// Now try some operation that requires authenticated access
		assertEquals(0, tokenBasedClient.getApplications().size());
	}

	@Test
	public void testSpaces() throws Exception {
		assertTrue(client.supportsSpaces());
		List<CloudSpace> spaces = client.getSpaces();
		assertNotNull(spaces);
		assertTrue(spaces.size() > 0);
		assertNotNull(spaces.get(0).getName());
	}

	@Test
	public void infoForUserAvailable() throws Exception {
		CloudInfo info = client.getCloudInfo();

		assertNotNull(info.getName());
		assertNotNull(info.getSupport());
		assertNotNull(info.getBuild());

//		assertEquals(TEST_USER_EMAIL, info.getUser());
		assertNotNull(info.getUser());
		assertNotNull(info.getLimits());
		// Just ensure that we got back some sensible values
		assertTrue(info.getLimits().getMaxApps() > 0 && info.getLimits().getMaxApps() < 1000);
		assertTrue(info.getLimits().getMaxServices() > 0 && info.getLimits().getMaxServices() < 1000);
		assertTrue(info.getLimits().getMaxTotalMemory() > 0 && info.getLimits().getMaxTotalMemory() < 100000);
		assertTrue(info.getLimits().getMaxUrisPerApp() > 0 && info.getLimits().getMaxUrisPerApp() < 100);
	}

	@Test
	public void infoForUserAvailableWithBearerToken() throws Exception {

		assumeTrue(token.toLowerCase().startsWith("bearer"));
		String rawToken = token.substring("bearer ".length());
		CloudFoundryClient bearerTokenClient = new CloudFoundryClient(rawToken, client.getCloudControllerUrl().toString());

		CloudInfo info = bearerTokenClient.getCloudInfo();

		assertNotNull(info.getName());
		assertNotNull(info.getSupport());
		assertNotNull(info.getBuild());

//		assertEquals(TEST_USER_EMAIL, info.getUser());
		assertNotNull(info.getUser());
		assertNotNull(info.getLimits());
		// Just ensure that we got back some sensible values
		assertTrue(info.getLimits().getMaxApps() > 0 && info.getLimits().getMaxApps() < 1000);
		assertTrue(info.getLimits().getMaxServices() > 0 && info.getLimits().getMaxServices() < 1000);
		assertTrue(info.getLimits().getMaxTotalMemory() > 0 && info.getLimits().getMaxTotalMemory() < 100000);
		assertTrue(info.getLimits().getMaxUrisPerApp() > 0 && info.getLimits().getMaxUrisPerApp() < 100);
	}

	@Test
	public void getServices() {
		assumeTrue(serviceSupported);
		String serviceName = "mysql-test";
		//createDatabaseService(serviceName);
		//createDatabaseService("another-name");

		List<CloudService> services = client.getServices();
		assertNotNull(services);
		assertEquals(2, services.size());
		CloudService service = null;
		for (CloudService cs : services) {
			if (cs.getName().equals(serviceName)) {
				service = cs;
			}
		}
		assertNotNull(service);
		assertEquals(serviceName, service.getName());
		assertEquals("mysql", service.getType());
		assertEquals("core", service.getVendor());
		assertEquals("5.1", service.getVersion());
		assertEquals("D100", service.getTier());
	}

	@Test
	public void getService() {
		assumeTrue(serviceSupported);
		String serviceName = "mysql-test";
//		createDatabaseService(serviceName);

		CloudService service = client.getService(serviceName);
		assertNotNull(service);
		assertEquals(serviceName, service.getName());
		// Allow more time deviations due to local clock being out of sync with transfer
//		int timeTolerance = 300 * 1000; // 5 minutes
//		assertTrue("Creation time should be very recent",
//				Math.abs(System.currentTimeMillis() - service.getMeta().getCreated().getTime()) < timeTolerance);
	}

//	private CloudService createDatabaseService(String serviceName) {
//		List<ServiceConfiguration> serviceConfigurations = client.getServiceConfigurations();
//		ServiceConfiguration databaseServiceConfiguration = null;
//		for (ServiceConfiguration sc : serviceConfigurations) {
//			if (sc.getVendor().equals("mysql")) {
//				databaseServiceConfiguration = sc;
//				break;
//			}
//		}
//		CloudService service = new CloudService();
//		service.setTier("free");
//		service.setType("database");
//		service.setVersion(databaseServiceConfiguration.getVersion());
//		service.setName(serviceName);
//		service.setVendor(databaseServiceConfiguration.getVendor());
//
//		client.createService(service);
//		return service;
//	}
}
