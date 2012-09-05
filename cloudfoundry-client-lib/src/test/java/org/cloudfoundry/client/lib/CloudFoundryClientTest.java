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

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeNotNull;
import static org.junit.Assume.assumeTrue;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudApplication.AppState;
import org.cloudfoundry.client.lib.domain.CloudApplication.DebugMode;
import org.cloudfoundry.client.lib.domain.CloudInfo;
import org.cloudfoundry.client.lib.domain.CloudInfo.Framework;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.cloudfoundry.client.lib.domain.ServiceConfiguration;
import org.cloudfoundry.client.lib.domain.ServiceConfiguration.Tier;
import org.cloudfoundry.client.lib.domain.ApplicationStats;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.CrashesInfo;
import org.cloudfoundry.client.lib.domain.InstanceInfo;
import org.cloudfoundry.client.lib.domain.InstanceStats;
import org.cloudfoundry.client.lib.domain.InstancesInfo;
import org.cloudfoundry.client.lib.domain.Staging;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

/**
 * Note that this test relies on deleteAllApplications() working correctly. If there is a temporary breaking of that
 * API, use 'vmc delete --all" to reset. Also each test relies on other methods working correctly, so these tests aren't
 * as independent as they should be.
 * 
 * @author Ramnivas Laddad
 * @author A.B.Srinivasan
 * @author Jennifer Hickey
 */
public class CloudFoundryClientTest extends AbstractCloudFoundryClientTest {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private CloudFoundryClient client;

	// Pass -Dvcap.target=http://api.cloudfoundry.com, vcap.me, or your own cloud
	private static final String ccUrl = System.getProperty("vcap.target", "https://api.cloudfoundry.com");

	private static final String TEST_USER_EMAIL = System.getProperty("vcap.email", "java-client-test-user@vmware.com");

	private static final String TEST_USER_PASS = System.getProperty("vcap.passwd");

	private static final String TEST_ADMIN_EMAIL = System.getProperty("vcap.admin.email");

	private static final String TEST_ADMIN_PASS = System.getProperty("vcap.admin.passwd");

	private static final String TEST_NAMESPACE = System.getProperty("vcap.test.namespace",
			defaultNamespace(TEST_USER_EMAIL));

	private static final int TEST_APP_MEMORY = Integer.valueOf(System.getProperty("test.app.memory", "512"));

	private final boolean serviceSupported = !ccUrl.contains("vmforce");

	private boolean multiUrlSupported = !ccUrl.contains("vmforce");

	private String token;

	@BeforeClass
	public static void printTargetInfo() {
		System.out.println("Running tests on " + ccUrl + " on behalf of " + TEST_USER_EMAIL);
		if (TEST_USER_PASS == null) {
			fail("System property vcap.passwd must be specified, supply -Dvcap.passwd=<password>");
		}
	}

	@Before
	public void setUp() throws MalformedURLException {
		client = new CloudFoundryClient(new CloudCredentials(TEST_USER_EMAIL, TEST_USER_PASS), new URL(ccUrl));
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
		List<CloudApplication> apps = client.getApplications();
		assertEquals(0, apps.size());
	}

	@After
	public void tearDown() {
		client.login(); // in case a test logged out (currently logout())
		client.deleteAllApplications();
		client.deleteAllServices();
		// client.unregister();
	}

	@Test
	public void accessThroughJustToken() throws MalformedURLException {
		String token = client.login();
		// Use the token to create a new client based on that token
		CloudFoundryClient tokenBasedClient = new CloudFoundryClient(new CloudCredentials(token), new URL(ccUrl));
		// Now try some operation that requires authenticated access
		assertEquals(0, tokenBasedClient.getApplications().size());
	}

	@Test
	public void getApplications() {
		List<String> uris1 = new ArrayList<String>();
		String appName1 = namespacedAppName(TEST_NAMESPACE, "travel_test1");
		uris1.add(computeAppUrl(ccUrl, appName1));
		client.createApplication(appName1, CloudApplication.SPRING,
				TEST_APP_MEMORY, uris1, null);
		List<CloudApplication> apps = client.getApplications();
		assertEquals(1, apps.size());

		List<String> uris2 = new ArrayList<String>();
		String appName2 = namespacedAppName(TEST_NAMESPACE, "travel_test2");
		uris2.add(computeAppUrl(ccUrl, appName2));
		client.createApplication(appName2, CloudApplication.SPRING,
				TEST_APP_MEMORY, uris2, null);
		apps = client.getApplications();
		assertEquals(2, apps.size());
	}

	@Test
	public void getApplication() {
		List<String> uris = new ArrayList<String>();
		String appName = namespacedAppName(TEST_NAMESPACE, "travel_test3");
		uris.add(computeAppUrl(ccUrl, appName));
		client.createApplication(appName, CloudApplication.SPRING,
				TEST_APP_MEMORY, uris, null);
		CloudApplication app = client.getApplication(appName);
		assertNotNull(app);
		assertEquals(appName, app.getName());
	}

	@Test
	public void getApplicationNonExistent() {
		String appName = namespacedAppName(TEST_NAMESPACE, "non_existent");
		try {
			client.getApplication(appName);
			fail("Expected CloudFoundryException");
		}
		catch (CloudFoundryException expected) {
			assertEquals(HttpStatus.NOT_FOUND, expected.getStatusCode());
		}
	}

	@Test
	public void updatePassword() throws MalformedURLException {
		String newPassword = "newPass123";
		client.updatePassword(newPassword);
		CloudFoundryClient clientWithChangedPassword =
				new CloudFoundryClient(new CloudCredentials(TEST_USER_EMAIL, newPassword), new URL(ccUrl));
		clientWithChangedPassword.login();

		// Revert
		client.updatePassword(TEST_USER_PASS);
		client.login();
	}

	@Test
	public void loginWrongCredentials() throws IOException {
		CloudFoundryClient client2 = new CloudFoundryClient(
				new CloudCredentials(TEST_USER_EMAIL, "wrong_password"), new URL(ccUrl));
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
	public void logout() {
		client.logout();

		try {
			client.getApplications();
			fail();
		}
		catch (CloudFoundryException ex) {
			if (ex.getStatusCode() != HttpStatus.FORBIDDEN) {
				fail();
			}
		}
	}

	@Test
	public void uploadApplication() throws IOException {
		CloudApplication app = createAndUploadTestApp(namespacedAppName(TEST_NAMESPACE, "travel_test3"));

		assertNotNull(app);
		assertEquals(AppState.STOPPED, app.getState());
	}

	@Test
	public void uploadStandaloneApplication() throws IOException {
		List<String> uris = new ArrayList<String>();
		List<String> services = new ArrayList<String>();
		Staging staging = new Staging("standalone");
		staging.setRuntime("ruby19");
		staging.setCommand("ruby simple.rb");
		String appName = namespacedAppName(TEST_NAMESPACE, "standalone-ruby");
		File file = SampleProjects.standaloneRuby();
		client.createApplication(appName, staging, 128, uris, services);
		client.uploadApplication(appName, file.getCanonicalPath());
		client.startApplication(appName);
		CloudApplication app = client.getApplication(appName);
		assertNotNull(app);
		assertEquals(AppState.STARTED, app.getState());
		assertEquals(uris, app.getUris());
	}

	@Test
	public void uploadStandaloneApplicationWithURLs() throws IOException {
		String appName = namespacedAppName(TEST_NAMESPACE, "standalone-node");
		List<String> uris = new ArrayList<String>();
		uris.add(computeAppUrl(ccUrl, appName));
		List<String> services = new ArrayList<String>();
		Staging staging = new Staging("standalone");
		staging.setRuntime("node");
		staging.setCommand("node app.js");
		File file = SampleProjects.standaloneNode();
		client.createApplication(appName, staging, 64, uris, services);
		client.uploadApplication(appName, file.getCanonicalPath());
		client.startApplication(appName);
		CloudApplication app = client.getApplication(appName);
		assertNotNull(app);
		assertEquals(AppState.STARTED, app.getState());
		assertEquals(Collections.singletonList(computeAppUrlNoProtocol(ccUrl, appName)), app.getUris());
	}

	@Test
	public void uploadApplicationWithServices() throws IOException {
		assumeTrue(serviceSupported);

		String serviceName = "test_database";
		List<String> serviceNames = new ArrayList<String>();
		serviceNames.add(serviceName);
		String appName = namespacedAppName(TEST_NAMESPACE, "travel_test3");
		CloudApplication app = createAndUploadTestApp(appName, serviceNames);
		assertEquals(1, app.getServices().size());
		assertEquals(serviceName, app.getServices().get(0));
	}

	@Test
	public void startApplication() throws IOException {
		CloudApplication app = createAndUploadAndStart(namespacedAppName(TEST_NAMESPACE, "travel_test3"));
		assertEquals(AppState.STARTED, app.getState());
	}

	@Test
	public void debugApplicationThrowsExceptionWhenDebuggingIsNotSupported() throws IOException, InterruptedException {
		assumeTrue(!client.getCloudInfo().getAllowDebug());

		try {
			String appName = namespacedAppName(TEST_NAMESPACE, "travel_test3");
			createAndUploadTestApp(appName);
			client.debugApplication(appName, DebugMode.run);
			fail("Should have caught a CloudFoundryException");
		}
		catch (CloudFoundryException e) {}
	}

	@Test
	public void debugApplicationWhenDebuggingIsSupported() throws IOException, InterruptedException {
		assumeTrue(client.getCloudInfo().getAllowDebug());

		String appName = namespacedAppName(TEST_NAMESPACE, "travel_test3");
		createAndUploadTestApp(appName);
		client.debugApplication(appName, DebugMode.run);
		CloudApplication app = client.getApplication(appName);
		assertEquals(AppState.STARTED, app.getState());

		assertEquals(DebugMode.run, app.getDebug());
		InstancesInfo applicationInstances = client.getApplicationInstances(appName);
		List<InstanceInfo> instances = applicationInstances.getInstances();
		assertEquals(1, instances.size());
		InstanceInfo firstInstanceInfo = instances.get(0);

		for (int i = 0; i < 10 && firstInstanceInfo.getDebugIp() == null; i++) {
			Thread.sleep(1000);
			applicationInstances = client.getApplicationInstances(appName);
			instances = applicationInstances.getInstances();
			firstInstanceInfo = instances.get(0);
		}
		assertNotNull(instances.get(0).getDebugIp());
		assertNotSame(0, instances.get(0).getDebugPort());
	}

	@Test
	public void startExplodedApplication() throws IOException {
		String appName = namespacedAppName(TEST_NAMESPACE, "travel_test5");
		CloudApplication app = createAndUploadExplodedSpringTestApp(appName, null);
		client.startApplication(appName);
		app = client.getApplication(appName);
		assertEquals(AppState.STARTED, app.getState());
	}

	@Test
	public void stopApplication() throws IOException {
		String appName = namespacedAppName(TEST_NAMESPACE, "travel_test3");
		CloudApplication app = createAndUploadAndStart(appName);
		assertEquals(AppState.STARTED, app.getState());
		client.stopApplication(appName);
		app = client.getApplication(appName);
		assertEquals(AppState.STOPPED, app.getState());
	}

	@Test
	public void getFile() throws Exception {
		String appName = namespacedAppName(TEST_NAMESPACE, "travel_getFile");
		createAndUploadAndStart(appName);
		String fileName = "tomcat/webapps/ROOT/WEB-INF/web.xml";

		// Test downloading full file
		String fileContent = null;
		for (int i = 0; i < 20 && (fileContent == null || fileContent.length() == 0); i++) {
			try {
				fileContent = client.getFile(appName, 0, fileName);
			}
			catch (Exception ex) {
				Thread.sleep(1000);
			}
		}
		assertNotNull(fileContent);
		assertTrue(fileContent.length() > 5);

		// Test downloading range of file with start and end position
		int end = fileContent.length() - 3;
		int start = end/2;
		String fileContent2 = null;
		for (int i = 0; i < 20 && (fileContent2 == null || fileContent2.length() == 0); i++) {
			try {
				fileContent2 = client.getFile(appName, 0, fileName, start, end);
			}
			catch (Exception ex) {
				Thread.sleep(1000);
			}
		}
		assertEquals(fileContent.substring(start, end + 1), fileContent2);

		// Test downloading range of file with just start position
		String fileContent3 = null;
		for (int i = 0; i < 20 && (fileContent3 == null || fileContent3.length() == 0); i++) {
			try {
				fileContent3 = client.getFile(appName, 0, fileName, start);
			}
			catch (Exception ex) {
				Thread.sleep(1000);
			}
		}
		assertEquals(fileContent.substring(start), fileContent3);

		// Test downloading range of file with start position and end position exceeding the length
		String fileContent4 = null;
		int positionPastEndPosition = fileContent.length() + 999;
		for (int i = 0; i < 20 && (fileContent4 == null || fileContent4.length() == 0); i++) {
			try {
				fileContent4 = client.getFile(appName, 0, fileName, start, positionPastEndPosition);
			}
			catch (Exception ex) {
				Thread.sleep(1000);
			}
		}
		assertEquals(fileContent.substring(start), fileContent4);

		// Test downloading end portion of file with length
		String fileContent5 = null;
		int length = fileContent.length() / 2;
		for (int i = 0; i < 20 && (fileContent5 == null || fileContent5.length() == 0); i++) {
			try {
				fileContent5 = client.getFileTail(appName, 0, fileName, length);
			}
			catch (Exception ex) {
				Thread.sleep(1000);
			}
		}
		assertEquals(fileContent.substring(fileContent.length() - length), fileContent5);

		// Test downloading one byte of file with start and end position
		String fileContent6 = null;
		for (int i = 0; i < 20 && (fileContent6 == null || fileContent6.length() == 0); i++) {
			try {
				fileContent6 = client.getFile(appName, 0, fileName, start, start);
			}
			catch (Exception ex) {
				Thread.sleep(1000);
			}
		}
		assertEquals(fileContent.substring(start, start + 1), fileContent6);

		// Test downloading range of file with invalid start position
		int invalidStartPosition = fileContent.length() + 999;
		try {
			client.getFile(appName, 0, fileName, invalidStartPosition);
			fail("should have thrown exception");
		} catch (CloudFoundryException e) {
			assertTrue(e.getStatusCode().equals(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE));
		}

		// Test downloading with invalid parameters - should all throw exceptions
		try {
			client.getFile(appName, 0, fileName, -2);
			fail("should have thrown exception");
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("start position"));
		}
		try {
			client.getFile(appName, 0, fileName, 10, -2);
			fail("should have thrown exception");
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("end position"));
		}
		try {
			client.getFile(appName, 0, fileName, 30, 29);
			fail("should have thrown exception");
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("start position") && e.getMessage().contains("end position"));
		}
		try {
			client.getFileTail(appName, 0, fileName, 0);
			fail("should have thrown exception");
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("length"));
		}
	}

	@Test
	public void updateApplictionInstances() throws IOException {
		String appName = namespacedAppName(TEST_NAMESPACE, "travel_test3");
		CloudApplication app = createAndUploadAndStart(appName);

		client.updateApplicationInstances(appName, 2);
		app = client.getApplication(appName);
		assertEquals(2, app.getInstances());

		client.updateApplicationInstances(appName, 3);
		app = client.getApplication(appName);
		assertEquals(3, app.getInstances());

		client.updateApplicationInstances(appName, 1);
		app = client.getApplication(appName);
		assertEquals(1, app.getInstances());
	}

	@Test
	public void getApplicationRunningInstances() throws Exception {
		String appName = namespacedAppName(TEST_NAMESPACE, "travel_test");
		CloudApplication app = createAndUploadAndStart(appName);
		client.updateApplicationInstances(appName, 2);
		app = client.getApplication(appName);
		assertEquals(2, app.getInstances());
		for (int i = 0; i < 180 && client.getApplication(appName).getRunningInstances() < 2; i++) {
			// max three minutes cumulative wait
			Thread.sleep(1000);
		}
		assertEquals(2, client.getApplication(appName).getRunningInstances());
	}

	@Test
	public void getApplicationStats() throws Exception {
		String appName = namespacedAppName(TEST_NAMESPACE, "travel_test3");
		createAndUploadAndStart(appName);

		ApplicationStats stats = client.getApplicationStats(appName);
		assertNotNull(stats);
		assertNotNull(stats.getRecords());
		// TODO: Make this pattern reusable
		for (int i = 0; i < 10 && stats.getRecords().size() < 1; i++) {
			Thread.sleep(1000);
			stats = client.getApplicationStats(appName);
		}
		assertEquals(1, stats.getRecords().size());
		InstanceStats firstInstance = stats.getRecords().get(0);
		assertEquals("0", firstInstance.getId());
		for (int i = 0; i < 10 && firstInstance.getUsage() == null; i++) {
			Thread.sleep(1000);
			stats = client.getApplicationStats(appName);
			firstInstance = stats.getRecords().get(0);
		}

		// Allow more time deviations due to local clock being out of sync with cloud
		int timeTolerance = 300 * 1000; // 5 minutes
		assertTrue("Usage time should be very recent",
				Math.abs(System.currentTimeMillis() - firstInstance.getUsage().getTime().getTime()) < timeTolerance);

		client.updateApplicationInstances(appName, 3);
		stats = client.getApplicationStats(appName);
		assertNotNull(stats);
		assertNotNull(stats.getRecords());
		assertEquals(3, stats.getRecords().size());
	}

	@Test
	public void getApplicationStatsStoppedApp() throws IOException {
		String appName = namespacedAppName(TEST_NAMESPACE, "travel_test3");
		createAndUploadTestApp(appName);

		ApplicationStats stats = client.getApplicationStats(appName);
		assertEquals(Collections.emptyList(), stats.getRecords());
	}

	@Test
	public void setEnvironment() throws IOException {
		String appName = namespacedAppName(TEST_NAMESPACE, "add-env-test");
		assertFalse(hasApplication(client.getApplications(), appName));
		CloudApplication app = createAndUploadTestApp(appName);
		assertTrue(app.getEnv().isEmpty());

		client.updateApplicationEnv(appName, asList("foo=bar", "bar=baz"));
		app = client.getApplication(app.getName());
		assertEquals(new HashSet<String>(asList("foo=bar", "bar=baz")), new HashSet<String>(app.getEnv()));

		client.updateApplicationEnv(appName, asList("foo=baz", "baz=bong"));
		app = client.getApplication(app.getName());
		assertEquals(new HashSet<String>(asList("foo=baz", "baz=bong")), new HashSet<String>(app.getEnv()));

		client.updateApplicationEnv(appName, new ArrayList<String>());
		app = client.getApplication(app.getName());
		assertTrue(app.getEnv().isEmpty());
	}

	@Test
	public void setEnvironmentThoughMap() throws IOException {
		String appName = namespacedAppName(TEST_NAMESPACE, "add-env-test");
		assertFalse(hasApplication(client.getApplications(), appName));
		CloudApplication app = createAndUploadTestApp(appName);
		assertTrue(app.getEnv().isEmpty());

		Map<String, String> env1 = new HashMap<String, String>();
		env1.put("foo", "bar");
		env1.put("bar", "baz");
		client.updateApplicationEnv(appName, env1);
		app = client.getApplication(app.getName());
		assertEquals(env1, app.getEnvAsMap());
		assertEquals(new HashSet<String>(asList("foo=bar", "bar=baz")), new HashSet<String>(app.getEnv()));

		Map<String, String> env2 = new HashMap<String, String>();
		env2.put("foo", "baz");
		env2.put("baz", "bong");
		client.updateApplicationEnv(appName, env2);
		app = client.getApplication(app.getName());
		assertEquals(env2, app.getEnvAsMap());
		assertEquals(new HashSet<String>(asList("foo=baz", "baz=bong")), new HashSet<String>(app.getEnv()));

		client.updateApplicationEnv(appName, new HashMap<String, String>());
		app = client.getApplication(app.getName());
		assertTrue(app.getEnv().isEmpty());
		assertTrue(app.getEnvAsMap().isEmpty());
	}

	@Test
	public void getServices() {
		assumeTrue(serviceSupported);
		String serviceName = "test_database";
		createDatabaseService(serviceName);

		List<CloudService> services = client.getServices();
		assertNotNull(services);
		assertEquals(1, services.size());
		assertEquals(serviceName, services.get(0).getName());
	}

	@Test
	public void getService() {
		assumeTrue(serviceSupported);
		String serviceName = "test_database";
		createDatabaseService(serviceName);

		CloudService service = client.getService(serviceName);
		assertNotNull(service);
		assertEquals(1, service.getMeta().getVersion());
		assertEquals(serviceName, service.getName());
		// Allow more time deviations due to local clock being out of sync with cloud
		int timeTolerance = 300 * 1000; // 5 minutes
		assertTrue("Creation time should be very recent",
				Math.abs(System.currentTimeMillis() - service.getMeta().getCreated().getTime()) < timeTolerance);
	}

	@Test
	public void deleteService() {
		assumeTrue(serviceSupported);

		String serviceName = "test_database";
		createDatabaseService(serviceName);

		List<CloudService> services = client.getServices();
		assertEquals(1, services.size());
		assertEquals(serviceName, services.get(0).getName());

		client.deleteService(serviceName);
		services = client.getServices();
		assertEquals(0, services.size());
	}

	@Test
	public void updateApplicationMemory() throws IOException {
		String appName = namespacedAppName(TEST_NAMESPACE, "travel_memory_test");
		CloudApplication app = createAndUploadTestApp(appName);

		assertEquals(TEST_APP_MEMORY, app.getMemory());

		client.updateApplicationMemory(appName, 256);
		app = client.getApplication(appName);
		assertEquals(256, app.getMemory());
	}

	@Test
	public void updateApplicationService() throws IOException {
		assumeTrue(serviceSupported);

		String serviceName = "test_database";
		createDatabaseService(serviceName);
		String appName = namespacedAppName(TEST_NAMESPACE, "travel_test3");
		createAndUploadTestApp(appName);

		client.updateApplicationServices(appName, Collections.singletonList(serviceName));
		CloudApplication app = client.getApplication(appName);
		assertNotNull(app.getServices());
		assertEquals(serviceName, app.getServices().get(0));

		List<String> emptyList = Collections.emptyList();
		client.updateApplicationServices(appName, emptyList);
		app = client.getApplication(appName);
		assertNotNull(app.getServices());
		assertEquals(emptyList, app.getServices());
	}

	@Test
	public void updateApplicationUris() throws IOException {
		String appName = namespacedAppName(TEST_NAMESPACE, "travel_test3");
		CloudApplication app = createAndUploadAndStart(appName);
		assertEquals(Collections.singletonList(computeAppUrlNoProtocol(ccUrl, appName)), app.getUris());

		assumeTrue(multiUrlSupported);

		List<String> uris = new ArrayList<String>(app.getUris());
		uris.add(computeAppUrlNoProtocol(ccUrl, "travel_test3_b"));
		client.updateApplicationUris(appName, uris);
		app = client.getApplication(appName);
		assertEquals(uris, app.getUris());
	}

	@Test
	public void getServiceConfigurations() {
		assumeTrue(serviceSupported);

		List<ServiceConfiguration> configurations = client.getServiceConfigurations();
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
		assertEquals(1, configuration.getMeta().getVersion());
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
	public void getApplicationMemoryChoices() {
		int[] choices = client.getApplicationMemoryChoices();
		assertNotNull(choices);
		assertNotSame(0, choices.length);
		assertTrue(client.getCloudInfo().getLimits().getMaxTotalMemory() >= choices[choices.length - 1]);
	}

	@Test
	public void infoAnonymouslyAvailable() throws Exception {
		CloudFoundryClient anonymousClient = new CloudFoundryClient(new URL(ccUrl));
		CloudInfo info = anonymousClient.getCloudInfo();

		assertNotNull(info.getName());
		assertNotNull(info.getSupport());
		assertNotNull(info.getBuild());
		assertNotNull(info.getSupport());
		assertNotNull(info.getSupport());

		assertTrue(info.getUser() == null);
	}

	@Test
	public void infoForUserAvailable() throws Exception {
		CloudInfo info = client.getCloudInfo();

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

		assertEquals(TEST_USER_EMAIL, info.getUser());
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

		Collection<Framework> frameworks = info.getFrameworks();
		assertNotNull(frameworks);
		Map<String, Framework> frameworksByName = new HashMap<String, Framework>();
		for (Framework framework : frameworks) {
			frameworksByName.put(framework.getName(), framework);
		}

		assertTrue(frameworksByName.containsKey("spring"));
		assertTrue(frameworksByName.containsKey("grails"));
		assertTrue(frameworksByName.containsKey("rails3"));
		assertTrue(frameworksByName.containsKey("sinatra"));
		assertTrue(frameworksByName.containsKey("node"));
		assertTrue(frameworksByName.containsKey("lift"));

		// a basic check that runtime info is correct
		Framework springFramework = frameworksByName.get("spring");
		List<CloudInfo.Runtime> springRuntimes = springFramework.getRuntimes();
		assertNotNull(springRuntimes);
		assertTrue(springRuntimes.size() > 0);
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
		assertEquals("1.6", runtimesByName.get("java").getVersion());
	}

	@Test
	public void getCrashes() throws IOException {
		String appName = namespacedAppName(TEST_NAMESPACE, "travel_test42");
		createAndUploadTestApp(appName);

		CrashesInfo crashes = client.getCrashes(appName);
		assertNotNull(crashes);
		assertTrue(crashes.getCrashes().isEmpty());
		// TODO very simplistic test - should trigger a crash and inspect results
	}

	@Test
	public void getApplicationInstances() throws Exception {
		String appName = namespacedAppName(TEST_NAMESPACE, "travel_test42");
		createAndUploadAndStart(appName);

		CloudApplication app = client.getApplication(appName);
		assertEquals(1, app.getInstances());

		InstancesInfo instances = getInstancesWithTimeout(appName);
		assertNotNull(instances);
		assertEquals(1, instances.getInstances().size());

		client.updateApplicationInstances(appName, 3);
		app = client.getApplication(appName);
		assertEquals(3, app.getInstances());

		boolean pass = false;
		for (int i = 0; i < 240; i++) {
			instances = getInstancesWithTimeout(appName);
			assertNotNull(instances);

			List<InstanceInfo> infos = instances.getInstances();
			assertEquals(3, infos.size());

			int passCount = 0;
			for (InstanceInfo info : infos) {
				if ("RUNNING".equals(info.getState()) || "STARTING".equals(info.getState())) {
					passCount++;
				}
			}
			if (passCount == infos.size()) {
				pass = true;
				break;
			}
			Thread.sleep(500);
		}
		assertTrue("Couldn't get the right application state in 2 minutes", pass);
	}

	@Test
	public void bindAndUnbindService() throws IOException {
		assumeTrue(serviceSupported);

		String serviceName = "test_database";
		createDatabaseService(serviceName);
		String appName = namespacedAppName(TEST_NAMESPACE, "travel_test42");
		createAndUploadTestApp(appName);

		CloudApplication app = client.getApplication(appName);
		assertNotNull(app.getServices());
		assertTrue(app.getServices().isEmpty());

		client.bindService(appName, serviceName);

		app = client.getApplication(appName);
		assertNotNull(app.getServices());
		assertEquals(1, app.getServices().size());
		assertEquals(serviceName, app.getServices().get(0));

		client.unbindService(appName, serviceName);

		app = client.getApplication(appName);
		assertNotNull(app.getServices());
		assertTrue(app.getServices().isEmpty());
	}

	@Test
	public void proxyUser() throws IOException {
		assumeNotNull(TEST_ADMIN_EMAIL, TEST_ADMIN_PASS);
		String appName = namespacedAppName(TEST_NAMESPACE, "admin-test");
		CloudCredentials adminCredentials = new CloudCredentials(TEST_ADMIN_EMAIL, TEST_ADMIN_PASS);
		CloudFoundryClient adminClient = new CloudFoundryClient(adminCredentials, new URL(ccUrl));

		assertFalse(hasApplication(client.getApplications(), appName));
		createAndUploadTestApp(appName);

		// apps not proxied
		assertTrue(hasApplication(client.getApplications(), appName));
		assertFalse(hasApplication(adminClient.getApplications(), appName));

		// apps proxied
		CloudFoundryClient proxyClient =
				new CloudFoundryClient(adminCredentials.proxyForUser(TEST_USER_EMAIL), new URL(ccUrl));
		assertTrue(hasApplication(proxyClient.getApplications(), appName));
	}

	@Test
	public void uploadSinatraApp() throws IOException {
		String appName = namespacedAppName(TEST_NAMESPACE, "env");
		ClassPathResource cpr = new ClassPathResource("apps/env/");
		File explodedDir = cpr.getFile();
		String framework = "sinatra";
		List<String> services = new ArrayList<String>();
		CloudApplication env = createAndUploadExplodedTestApp(appName, explodedDir, framework, 128, services);
		client.startApplication(appName);
		env = client.getApplication(appName);
		assertEquals(AppState.STARTED, env.getState());
	}

	@Test
	public void uploadAppWithNonAsciiFileName() throws IOException {
		String appName = namespacedAppName(TEST_NAMESPACE, "non-ascii-file-name");
		List<String> uris = new ArrayList<String>();
		uris.add(computeAppUrl(ccUrl, appName));

		File war = SampleProjects.nonAsciFileName();
		List<String> serviceNames = new ArrayList<String>();

		client.createApplication(appName, CloudApplication.SPRING,
				TEST_APP_MEMORY, uris, serviceNames);
		client.uploadApplication(appName, war.getCanonicalPath());

		CloudApplication app = client.getApplication(appName);
		assertNotNull(app);
		assertEquals(AppState.STOPPED, app.getState());

		client.startApplication(appName);

		app = client.getApplication(appName);
		assertNotNull(app);
		assertEquals(AppState.STARTED, app.getState());

		client.deleteApplication(appName);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void setSpaceIsNotSupportedForV1() throws IOException {
		CloudFoundryClient spaceClient =
				new CloudFoundryClient(
						new CloudCredentials(TEST_USER_EMAIL, TEST_USER_PASS),
						new URL(ccUrl),
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

	private InstancesInfo getInstancesWithTimeout(String appName) throws InterruptedException {
		long start = System.currentTimeMillis();
		while (true) {
			Thread.sleep(2000);
			try {
				return client.getApplicationInstances(appName);
			}
			catch (HttpServerErrorException e) {
				// error 500, keep waiting
			}
			if (System.currentTimeMillis() - start > 30000) {
				fail("Timed out waiting for startup");
				break; // for the compiler
			}
		}

		return null; // for the compiler
	}

	private CloudApplication createAndUploadTestApp(String appName, List<String> serviceNames) throws IOException {
		List<String> uris = new ArrayList<String>();
		uris.add(computeAppUrl(ccUrl, appName));

		File file = SampleProjects.springTravel();

		if (serviceNames != null) {
			for (String serviceName : serviceNames) {
				createDatabaseService(serviceName);
			}
		}

		client.createApplication(appName, CloudApplication.SPRING,
				TEST_APP_MEMORY, uris, serviceNames);
		client.uploadApplication(appName, file.getCanonicalPath());
		return client.getApplication(appName);
	}

	private void createApplication(String appName, List<String> serviceNames, String framework, int memory) {
		List<String> uris = new ArrayList<String>();
		uris.add(computeAppUrl(ccUrl, appName));
		if (serviceNames != null) {
			for (String serviceName : serviceNames) {
				createDatabaseService(serviceName);
			}
		}
		client.createApplication(appName, framework, memory, uris,
				serviceNames);
	}

	private CloudApplication createAndUploadExplodedSpringTestApp(String appName, List<String> serviceNames)
			throws IOException {
		File explodedDirPath = SampleProjects.springTravelUnpacked(temporaryFolder);
		return createAndUploadExplodedTestApp(appName, explodedDirPath, CloudApplication.SPRING, TEST_APP_MEMORY, serviceNames);
	}

	private CloudApplication createAndUploadExplodedTestApp(String appName, File explodedDir, String framework,
			int memory, List<String> serviceNames) throws IOException {
		assertTrue("Expected exploded test app at " + explodedDir.getCanonicalPath(), explodedDir.exists());
		createApplication(appName, null, framework, memory);
		client.uploadApplication(appName, explodedDir.getCanonicalPath());
		return client.getApplication(appName);
	}

	private CloudApplication createAndUploadTestApp(String appName) throws IOException {
		return createAndUploadTestApp(appName, null);
	}

	private CloudApplication createAndUploadAndStart(String appName) throws IOException {
		createAndUploadTestApp(appName);
		client.startApplication(appName);
		return client.getApplication(appName);
	}

	private CloudService createDatabaseService(String serviceName) {
		List<ServiceConfiguration> serviceConfigurations = client.getServiceConfigurations();
		ServiceConfiguration databaseServiceConfiguration = null;
		for (ServiceConfiguration sc : serviceConfigurations) {
			if (sc.getVendor().equals("mysql")) {
				databaseServiceConfiguration = sc;
				break;
			}
		}
		CloudService service = new CloudService();
		service.setTier("free");
		service.setType("database");
		service.setVersion(databaseServiceConfiguration.getVersion());
		service.setName(serviceName);
		service.setVendor(databaseServiceConfiguration.getVendor());

		client.createService(service);
		return service;
	}

}
