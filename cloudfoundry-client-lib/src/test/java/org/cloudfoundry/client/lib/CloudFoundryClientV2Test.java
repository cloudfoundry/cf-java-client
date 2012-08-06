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
import org.cloudfoundry.client.lib.domain.CloudOrganization;
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
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;
import static org.junit.Assert.fail;

/**
 * Evolving test class for V2 cloud controller.
 *
 * You need to create organization, space and user account via the portal and set these values using
 * system properties.
 *
 * @author: trisberg
 */
public class CloudFoundryClientV2Test {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private CloudFoundryClient client;

	private CloudFoundryClient spaceClient;

	// Pass -Dccng.target=http://api.cloudfoundry.com, vcap.me, or your own cloud -- must point to a v2 cloud controller
	private static final String CCNG_URL = System.getProperty("ccng.target", "http://ccng.cloudfoundry.com");

	private static final String CCNG_USER_EMAIL = System.getProperty("ccng.email", "java-client-test-user@vmware.com");

	private static final String CCNG_USER_PASS = System.getProperty("ccng.passwd");

	private static final String CCNG_USER_ORG = System.getProperty("ccng.org", "rbcon.com");

	private static final String CCNG_USER_SPACE = System.getProperty("ccng.space", "test");

	private String token;

	@BeforeClass
	public static void printTargetInfo() {
		System.out.println("Running tests on " + CCNG_URL + " on behalf of " + CCNG_USER_EMAIL);
		System.out.println("Using space " + CCNG_USER_SPACE + " of organization " + CCNG_USER_ORG );
		if (CCNG_USER_PASS == null) {
			fail("System property ccng.passwd must be specified, supply -Dccng.passwd=<password>");
		}

	}

	@Before
	public void setUp() throws MalformedURLException {
		client = new CloudFoundryClient(new CloudCredentials(CCNG_USER_EMAIL, CCNG_USER_PASS), new URL(CCNG_URL));
		this.token = client.login();
		spaceClient = setTestSpaceAsDefault(client);
//		TODO: verify clean account
//		List<CloudApplication> apps = client.getApplications();
//		assertEquals(0, apps.size());
		spaceClient.deleteAllServices();
	}

	@After
	public void tearDown() {
		client.login(); // in case a test logged out (currently logout())
//		TODO: clean out account
//		client.deleteAllApplications();
		spaceClient.deleteAllServices();
	}

	@Test
	public void infoAvailableAndIsV2() throws Exception {
		CloudInfo info = client.getCloudInfo();
		assertNotNull(info.getName());
		assertNotNull(info.getSupport());
		assertNotNull(info.getBuild());
		assertEquals(CloudInfo.CC_MAJOR_VERSION.V2, info.getCloudControllerMajorVersion());
	}

	@Test
	public void spacesAvailable() throws Exception {
		List<CloudSpace> spaces = client.getSpaces();
		assertNotNull(spaces);
		assertTrue(spaces.size() > 0);
	}

	@Test
	public void canSetDefaultSpace() throws Exception {
		setTestSpaceAsDefault(client);
	}

	@Test
	public void infoForUserAvailable() throws Exception {
		CloudInfo info = client.getCloudInfo();

		assertNotNull(info.getName());
		assertNotNull(info.getSupport());
		assertNotNull(info.getBuild());
		assertNotNull(info.getSupport());
		assertNotNull(info.getSupport());

		assertEquals(CCNG_USER_EMAIL, info.getUser());
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
		CloudFoundryClient bearerTokenClient =
				new CloudFoundryClient(new CloudCredentials(rawToken), client.getCloudControllerUrl());

		CloudInfo info = bearerTokenClient.getCloudInfo();

		assertNotNull(info.getName());
		assertNotNull(info.getSupport());
		assertNotNull(info.getBuild());
		assertNotNull(info.getSupport());
		assertNotNull(info.getSupport());

		assertEquals(CCNG_USER_EMAIL, info.getUser());
		assertNotNull(info.getLimits());
		// Just ensure that we got back some sensible values
		assertTrue(info.getLimits().getMaxApps() > 0 && info.getLimits().getMaxApps() < 1000);
		assertTrue(info.getLimits().getMaxServices() > 0 && info.getLimits().getMaxServices() < 1000);
		assertTrue(info.getLimits().getMaxTotalMemory() > 0 && info.getLimits().getMaxTotalMemory() < 100000);
		assertTrue(info.getLimits().getMaxUrisPerApp() > 0 && info.getLimits().getMaxUrisPerApp() < 100);
	}

	@Test
	public void frameworksInfoAvailable() {
		CloudInfo info = client.getCloudInfo();

		Collection<CloudInfo.Framework> frameworks = info.getFrameworks();
		assertNotNull(frameworks);
		Map<String, CloudInfo.Framework> frameworksByName = new HashMap<String, CloudInfo.Framework>();
		for (CloudInfo.Framework framework : frameworks) {
			frameworksByName.put(framework.getName(), framework);
		}

		assertTrue(frameworksByName.containsKey("spring"));
		assertTrue(frameworksByName.containsKey("grails"));
		assertTrue(frameworksByName.containsKey("rails3"));
		assertTrue(frameworksByName.containsKey("sinatra"));
		assertTrue(frameworksByName.containsKey("node"));
		assertTrue(frameworksByName.containsKey("lift"));

		// a basic check that runtime info is correct
		CloudInfo.Framework springFramework = frameworksByName.get("spring");
		//TODO: this is no longer availabe in v2
//		List<CloudInfo.Runtime> springRuntimes = springFramework.getRuntimes();
//		assertNotNull(springRuntimes);
//		assertTrue(springRuntimes.size() > 0);
	}

	@Test
	public void runtimeInfoAvailable() {
		CloudInfo info = client.getCloudInfo();

		Collection<CloudInfo.Runtime> runtimes = info.getRuntimes();
		Map<String, CloudInfo.Runtime> runtimesByName = new HashMap<String, CloudInfo.Runtime>();
		for (CloudInfo.Runtime runtime : runtimes) {
			runtimesByName.put(runtime.getName(), runtime);
		}

		assertTrue(runtimesByName.containsKey("java"));
		assertTrue(runtimesByName.containsKey("ruby19"));
		assertTrue(runtimesByName.containsKey("ruby18"));
		assertTrue(runtimesByName.containsKey("node"));

		// a basic check that versions are right
		//TODO: this is no longer availabe in v2
//		assertEquals("1.6", runtimesByName.get("java").getVersion());
	}

	@Test
	public void getServices() {
		String serviceName = "mysql-test";
		createMySqlService(serviceName);
		createMySqlService("another-name");

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
		assertEquals("mysql", service.getLabel());
		assertEquals("core", service.getProvider());
		assertEquals("5.1", service.getVersion());
		assertEquals("D100", service.getPlan());
	}

	@Test
	public void getService() throws MalformedURLException {
		String serviceName = "mysql-test";
		createMySqlService(serviceName);

		spaceClient = setTestSpaceAsDefault(client);
		CloudService service = spaceClient.getService(serviceName);
		assertNotNull(service);
		assertEquals(2, service.getMeta().getVersion());
		assertEquals(serviceName, service.getName());
		// Allow more time deviations due to local clock being out of sync with cloud
		int timeTolerance = 300 * 1000; // 5 minutes
		assertTrue("Creation time should be very recent",
				Math.abs(System.currentTimeMillis() - service.getMeta().getCreated().getTime()) < timeTolerance);
	}

	@Test
	public void createService() throws MalformedURLException {
		createMySqlService("mysql-test");
	}

	private void createMySqlService(String serviceName) {
		CloudService service = new CloudService();
		service.setType("database");
		service.setVersion("5.1");
		service.setName(serviceName);
		service.setProvider("core");
		service.setLabel("mysql");
		service.setPlan("D100");
		spaceClient.createService(service);
	}

	@Test
	public void deleteService() throws MalformedURLException {
		String serviceName = "mysql-test";
		createMySqlService(serviceName);
		spaceClient.deleteService(serviceName);
	}

	@Test
	public void getServiceConfigurations() {
		List<ServiceConfiguration> configurations = spaceClient.getServiceConfigurations();

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
		assertEquals(2, configuration.getMeta().getVersion());
//		TODO: type is not currently part of the service definitions in v2
//		assertEquals("key-value", configuration.getType());
		assertEquals("redis", configuration.getCloudServiceOffering().getLabel());
		assertNotNull(configuration.getCloudServiceOffering().getCloudServicePlans());
		assertTrue(configuration.getCloudServiceOffering().getCloudServicePlans().size() > 0);
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
		spaceClient = new CloudFoundryClient(new CloudCredentials(CCNG_USER_EMAIL, CCNG_USER_PASS), new URL(CCNG_URL), testSpace);
		spaceClient.login();
		return spaceClient;
	}

}
