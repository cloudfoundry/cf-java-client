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
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

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
		if (CCNG_USER_PASS == null) {
			fail("System property ccng.passwd must be specified, supply -Dccng.passwd=<password>");
		}

	}

	@Before
	public void setUp() throws MalformedURLException {
		client = new CloudFoundryClient(CCNG_USER_EMAIL, CCNG_USER_PASS, CCNG_URL);
		this.token = client.login();
		System.out.println("Using token: " + this.token);
//		List<CloudApplication> apps = client.getApplications();
//		assertEquals(0, apps.size());
	}

	@After
	public void tearDown() {
		client.login(); // in case a test logged out (currently logout())
//		client.deleteAllApplications();
//		client.deleteAllServices();
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
		setTestSpaceAsDefault();
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
		CloudFoundryClient bearerTokenClient = new CloudFoundryClient(rawToken, client.getCloudControllerUrl().toString());

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


	private void setTestSpaceAsDefault() {
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
		client.setCurrentSpace(testSpace);
		System.out.println("Using space " + testSpace.getName() + " of organization " + CCNG_USER_ORG);
	}
}
