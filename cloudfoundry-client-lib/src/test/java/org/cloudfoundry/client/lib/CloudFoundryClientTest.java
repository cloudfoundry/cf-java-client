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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeNotNull;
import static org.junit.Assume.assumeTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudApplication.AppState;
import org.cloudfoundry.client.lib.domain.CloudApplication.DebugMode;
import org.cloudfoundry.client.lib.domain.CloudInfo;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.cloudfoundry.client.lib.domain.ServiceConfiguration;
import org.cloudfoundry.client.lib.domain.ServiceConfiguration.Tier;
import org.cloudfoundry.client.lib.domain.InstanceInfo;
import org.cloudfoundry.client.lib.domain.InstancesInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.HttpStatus;

/**
 * Test class for V1 cloud controller.
 *
 * @author Ramnivas Laddad
 * @author A.B.Srinivasan
 * @author Jennifer Hickey
 * @author Thomas Risberg
 */
@CloudVersions({"V1"})
public class CloudFoundryClientTest extends AbstractCloudFoundryClientTest {

	private CloudInfo cloudInfo;

	private CloudFoundryClient plainClient;

	private CloudFoundryClient connectedClient;

	// Pass -Dvcap.target=http://api.cloudfoundry.com, vcap.me, or your own cloud
	private static final String TEST_API_URL = System.getProperty("vcap.target", "https://api.cloudfoundry.com");

	private static final String TEST_USER_EMAIL = System.getProperty("vcap.email", "java-connectedClient-test-user@vmware.com");

	private static final String TEST_USER_PASS = System.getProperty("vcap.passwd");

	private static final String TEST_ADMIN_EMAIL = System.getProperty("vcap.admin.email");

	private static final String TEST_ADMIN_PASS = System.getProperty("vcap.admin.passwd");

	private static final String TEST_NAMESPACE = System.getProperty("vcap.test.namespace",
			defaultNamespace(TEST_USER_EMAIL));

	private static final int TEST_APP_MEMORY = Integer.valueOf(System.getProperty("test.app.memory", "512"));

	private final boolean completeApiSupported = !TEST_API_URL.contains("vmforce");

	private String token;

	@BeforeClass
	public static void printTargetInfo() {
		System.out.println("Running tests on " + TEST_API_URL + " on behalf of " + TEST_USER_EMAIL);
		if (TEST_USER_PASS == null) {
			fail("System property vcap.passwd must be specified, supply -Dvcap.passwd=<password>");
		}
	}

	@Before
	public void setUp() throws MalformedURLException {
		cloudInfo = new CloudFoundryClient(new URL(TEST_API_URL)).getCloudInfo();
		plainClient =
				new CloudFoundryClient(new CloudCredentials(TEST_USER_EMAIL, TEST_USER_PASS), new URL(TEST_API_URL));
		connectedClient =
				new CloudFoundryClient(new CloudCredentials(TEST_USER_EMAIL, TEST_USER_PASS), new URL(TEST_API_URL));
		token = connectedClient.login();
		connectedClient.deleteAllApplications();
		connectedClient.deleteAllServices();
	}

	@After
	public void tearDown() {
		connectedClient.deleteAllApplications();
		connectedClient.deleteAllServices();
	}

	@Test
	public void accessThroughJustToken() throws MalformedURLException {
		String token = plainClient.login();
		// Use the token to create a new connectedClient based on that token
		CloudFoundryClient tokenBasedClient = new CloudFoundryClient(new CloudCredentials(token), new URL(TEST_API_URL));
		// Now try some operation that requires authenticated access
		assertEquals(0, tokenBasedClient.getApplications().size());
	}

	@Test
	public void updatePassword() throws MalformedURLException {
		String newPassword = "newPass123";
		connectedClient.updatePassword(newPassword);
		CloudFoundryClient clientWithChangedPassword =
				new CloudFoundryClient(new CloudCredentials(TEST_USER_EMAIL, newPassword), new URL(TEST_API_URL));
		clientWithChangedPassword.login();

		// Revert
		connectedClient.updatePassword(TEST_USER_PASS);
		connectedClient.login();
	}

	@Test
	public void loginWrongCredentials() throws IOException {
		CloudFoundryClient client2 = new CloudFoundryClient(
				new CloudCredentials(TEST_USER_EMAIL, "wrong_password"), new URL(TEST_API_URL));
		try {
			client2.login();
			fail("Expected CloudFoundryException");
		}
		catch (CloudFoundryException expected) {
			assertEquals(HttpStatus.FORBIDDEN, expected.getStatusCode());
			assertNotNull(expected.getDescription());
		}
	}

	@Test
	public void logout() throws MalformedURLException {
		CloudFoundryClient testClient =
				new CloudFoundryClient(new CloudCredentials(TEST_USER_EMAIL, TEST_USER_PASS), new URL(TEST_API_URL));
		testClient.logout();

		try {
			testClient.getApplications();
			fail();
		}
		catch (CloudFoundryException ex) {
			if (ex.getStatusCode() != HttpStatus.FORBIDDEN) {
				fail();
			}
		}
	}
	
	@Test
	public void frameworksInfoAvailable() {
		CloudInfo info = getConnectedClient().getCloudInfo();

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
		assertNotNull(springFramework);

		List<CloudInfo.Runtime> springRuntimes = springFramework.getRuntimes();
		assertNotNull(springRuntimes);
		assertTrue(springRuntimes.size() > 0);
	}

	@Test
	public void runtimeInfoAvailable() {
		CloudInfo info = getConnectedClient().getCloudInfo();

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
		if (getInfo().getCloudControllerMajorVersion() == CloudInfo.CC_MAJOR_VERSION.V1) {
			assertTrue(runtimesByName.get("java").getVersion().startsWith("1.6"));
		}
	}

	@Test
	public void debugApplicationThrowsExceptionWhenDebuggingIsNotSupported() throws IOException, InterruptedException {
		assumeTrue(!connectedClient.getCloudInfo().getAllowDebug());

		try {
			String appName = namespacedAppName("debug1");
			createAndUploadTravelTestApp(appName);
			connectedClient.debugApplication(appName, DebugMode.run);
			fail("Should have caught a CloudFoundryException");
		}
		catch (CloudFoundryException e) {}
	}

	@Test
	public void debugApplicationWhenDebuggingIsSupported() throws IOException, InterruptedException {
		assumeTrue(connectedClient.getCloudInfo().getAllowDebug());

		String appName = namespacedAppName("debug2");
		createAndUploadTravelTestApp(appName);
		connectedClient.debugApplication(appName, DebugMode.run);
		CloudApplication app = connectedClient.getApplication(appName);
		assertEquals(AppState.STARTED, app.getState());

		assertEquals(DebugMode.run, app.getDebug());
		InstancesInfo applicationInstances = connectedClient.getApplicationInstances(appName);
		List<InstanceInfo> instances = applicationInstances.getInstances();
		assertEquals(1, instances.size());
		InstanceInfo firstInstanceInfo = instances.get(0);

		for (int i = 0; i < 10 && firstInstanceInfo.getDebugIp() == null; i++) {
			Thread.sleep(1000);
			applicationInstances = connectedClient.getApplicationInstances(appName);
			instances = applicationInstances.getInstances();
			firstInstanceInfo = instances.get(0);
		}
		assertNotNull(instances.get(0).getDebugIp());
		assertNotSame(0, instances.get(0).getDebugPort());
	}

	@Test
	public void getServiceConfigurations() {
		assumeTrue(completeApiSupported);

		List<ServiceConfiguration> configurations = connectedClient.getServiceConfigurations();
		assertNotNull(configurations);
		assertTrue(configurations.size() >= 2);

		ServiceConfiguration configuration = null;
		for (ServiceConfiguration sc : configurations) {
			if (sc.getVendor().equals("redis")) {
				configuration = sc;
				break;
			}
		}
		assertNotNull(configuration);
		assertEquals("key-value", configuration.getType());
		assertEquals("redis", configuration.getVendor());
		assertNotNull(configuration.getTiers());
		assertTrue(configuration.getTiers().size() > 0);

		Collections.sort(configuration.getTiers(), new Comparator<Tier>() {
			public int compare(Tier o1, Tier o2) {
				return o1.getOrder() - o2.getOrder();
			}
		});
		Tier tier = configuration.getTiers().get(0);
		assertEquals(1, tier.getOrder());
		assertEquals("free", tier.getType());
	}

	@Test
	public void infoForUserAvailableWithBearerToken() throws Exception {

		assumeTrue(token.toLowerCase().startsWith("bearer"));
		String rawToken = token.substring("bearer ".length());
		CloudFoundryClient bearerTokenClient =
				new CloudFoundryClient(new CloudCredentials(rawToken), connectedClient.getCloudControllerUrl());

		CloudInfo info = bearerTokenClient.getCloudInfo();

		assertNotNull(info.getName());
		assertNotNull(info.getSupport());
		assertNotNull(info.getBuild());
		assertNotNull(info.getSupport());
		assertNotNull(info.getSupport());

		assertEquals(TEST_USER_EMAIL, info.getUser());
		assertNotNull(info.getLimits());
		// Just ensure that we got back some sensible values
		assertTrue(info.getLimits().getMaxApps() > 0 && info.getLimits().getMaxApps() < 1000);
		assertTrue(info.getLimits().getMaxServices() > 0 && info.getLimits().getMaxServices() < 1000);
		assertTrue(info.getLimits().getMaxTotalMemory() > 0 && info.getLimits().getMaxTotalMemory() < 100000);
		assertTrue(info.getLimits().getMaxUrisPerApp() > 0 && info.getLimits().getMaxUrisPerApp() < 100);
	}

	@Test
	public void proxyUser() throws IOException {
		assumeNotNull(TEST_ADMIN_EMAIL, TEST_ADMIN_PASS);
		String appName = namespacedAppName("admin-test");
		CloudCredentials adminCredentials = new CloudCredentials(TEST_ADMIN_EMAIL, TEST_ADMIN_PASS);
		CloudFoundryClient adminClient = new CloudFoundryClient(adminCredentials, new URL(TEST_API_URL));

		assertFalse(hasApplication(connectedClient.getApplications(), appName));
		createAndUploadTravelTestApp(appName);

		// apps not proxied
		assertTrue(hasApplication(connectedClient.getApplications(), appName));
		assertFalse(hasApplication(adminClient.getApplications(), appName));

		// apps proxied
		CloudFoundryClient proxyClient =
				new CloudFoundryClient(adminCredentials.proxyForUser(TEST_USER_EMAIL), new URL(TEST_API_URL));
		assertTrue(hasApplication(proxyClient.getApplications(), appName));
	}

	@Test
	public void setSpaceIsNotSupportedForV1() throws IOException {
		thrown.expect(UnsupportedOperationException.class);
		new CloudFoundryClient(
				new CloudCredentials(TEST_USER_EMAIL, TEST_USER_PASS),
				new URL(TEST_API_URL),
				new CloudSpace(null, "test", null));
	}

	private boolean hasApplication(List<CloudApplication> applications, String targetName) {
		for (CloudApplication application : applications) {
			if (application.getName().equals(targetName)) {
				return true;
			}
		}
		return false;
	}

	// Getters for test config

	@Override
	protected String getApiUrl() {
		return TEST_API_URL;
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
		return TEST_USER_EMAIL;
	}

	@Override
	protected int getTestAppMemory(String framework) {
		if (framework.equals("spring")) {
			return TEST_APP_MEMORY;
		}
		return connectedClient.getDefaultApplicationMemory(framework);
	}

	@Override
	protected boolean getCompleteApiSupported() {
		return completeApiSupported;
	}
}
