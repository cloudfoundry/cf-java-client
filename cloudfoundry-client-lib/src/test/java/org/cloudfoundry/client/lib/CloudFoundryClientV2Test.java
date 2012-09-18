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
import org.cloudfoundry.client.lib.domain.CloudEntity;
import org.cloudfoundry.client.lib.domain.CloudInfo;
import org.cloudfoundry.client.lib.domain.CloudOrganization;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.cloudfoundry.client.lib.domain.ServiceConfiguration;
import org.cloudfoundry.client.lib.domain.Staging;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;
import static org.junit.Assert.fail;

/**
 * Evolving test class for V2 cloud controller.
 *
 * You need to create organization, space and user account via the portal and set these values using
 * system properties.
 *
 * @author Thomas Risberg
 */
public class CloudFoundryClientV2Test extends AbstractCloudFoundryClientTest {

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

	private static final String TEST_NAMESPACE = System.getProperty("vcap.test.namespace",
			defaultNamespace(CCNG_USER_EMAIL));

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
		spaceClient.deleteAllApplications();
		spaceClient.deleteAllServices();
	}

	@After
	public void tearDown() {
		spaceClient.deleteAllApplications();
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
		assertNotNull(springFramework);

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
		//TODO: this is no longer availabe in v2
//		assertEquals("1.6", runtimesByName.get("java").getVersion());
	}

	@Test
	public void getApplicationMemoryChoices() {
		int springMemory = client.getDefaultApplicationMemory("spring");
		assertEquals(512, springMemory);
		int railsMemory = client.getDefaultApplicationMemory("rails");
		assertEquals(256, railsMemory);
		int[] choices = client.getApplicationMemoryChoices();
		assertNotNull(choices);
		assertNotSame(0, choices.length);
		assertTrue(client.getCloudInfo().getLimits().getMaxTotalMemory() >= choices[choices.length - 1]);
	}

	@Test
	public void createApplication() {
		List<String> uris = new ArrayList<String>();
		String appName = namespacedAppName(TEST_NAMESPACE, "travel_test0");
		uris.add(computeAppUrl(CCNG_URL, appName));
		Staging staging =  new Staging("spring");
		staging.setRuntime("java");
		spaceClient.createApplication(appName, staging,
				spaceClient.getDefaultApplicationMemory("spring"), uris, null);
		CloudApplication app = spaceClient.getApplication(appName);
		assertNotNull(app);
		assertEquals(appName, app.getName());
	}

	@Test
	public void getApplications() {
		String appName = createSpringTravelApp("1", null);
		List<CloudApplication> apps = spaceClient.getApplications();
		assertEquals(1, apps.size());
		assertEquals(appName, apps.get(0).getName());
		assertNotNull(apps.get(0));
		assertNotNull(apps.get(0).getMeta());
		assertNotNull(apps.get(0).getMeta().getGuid());
		assertEquals(2, apps.get(0).getMeta().getVersion());

		createSpringTravelApp("2", null);
		apps = spaceClient.getApplications();
		assertEquals(2, apps.size());
	}

	@Test
	public void getApplication() {
		String appName = createSpringTravelApp("3", null);
		CloudApplication app = spaceClient.getApplication(appName);
		assertNotNull(app);
		assertEquals(appName, app.getName());
	}

	@Test
	public void deleteApplication() {
		String appName = createSpringTravelApp("4", null);
		assertEquals(1, spaceClient.getApplications().size());
		spaceClient.deleteApplication(appName);
		assertEquals(0, spaceClient.getApplications().size());
	}

	@Test
	public void renameApplication() {
		String appName = createSpringTravelApp("5", null);
		CloudApplication app = spaceClient.getApplication(appName);
		assertNotNull(app);
		assertEquals(appName, app.getName());
		String newName = namespacedAppName(TEST_NAMESPACE, "travel_test" + "6");
		spaceClient.rename(appName, newName);
		CloudApplication newApp = spaceClient.getApplication(newName);
		assertNotNull(newApp);
		assertEquals(newName, newApp.getName());
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

	@Test
	public void updateApplicationService() throws IOException {
		String serviceName = "test_database";
		createMySqlService(serviceName);
		String appName = createSpringTravelApp("7", null);

		spaceClient.updateApplicationServices(appName, Collections.singletonList(serviceName));
		CloudApplication app = spaceClient.getApplication(appName);
		assertNotNull(app.getServices());
		assertTrue(app.getServices().size() > 0);
		assertEquals(serviceName, app.getServices().get(0));

		List<String> emptyList = Collections.emptyList();
		spaceClient.updateApplicationServices(appName, emptyList);
		app = spaceClient.getApplication(appName);
		assertNotNull(app.getServices());
		assertEquals(emptyList, app.getServices());
	}

	@Test
	public void createApplicationWithService() {
		String serviceName = "test_database";
		createMySqlService(serviceName);
		List<String> serviceNames= Collections.singletonList(serviceName);
		List<String> uris = new ArrayList<String>();
		String appName = namespacedAppName(TEST_NAMESPACE, "travel_test8");
		uris.add(computeAppUrl(CCNG_URL, appName));
		Staging staging =  new Staging("spring");
		staging.setRuntime("java");
		spaceClient.createApplication(appName, staging,
				spaceClient.getDefaultApplicationMemory("spring"), uris, serviceNames);
		CloudApplication app = spaceClient.getApplication(appName);
		assertNotNull(app);
		assertEquals(appName, app.getName());
		assertNotNull(app.getServices());
		assertEquals(1, app.getServices().size());
		assertEquals(serviceName, app.getServices().get(0));
	}

	@Test
	public void bindAndUnbindService() throws IOException {
		String serviceName = "test_database";
		createMySqlService(serviceName);

		String appName = createSpringTravelApp("bind1", null);

		CloudApplication app = spaceClient.getApplication(appName);
		assertNotNull(app.getServices());
		assertTrue(app.getServices().isEmpty());

		spaceClient.bindService(appName, serviceName);

		app = spaceClient.getApplication(appName);
		assertNotNull(app.getServices());
		assertEquals(1, app.getServices().size());
		assertEquals(serviceName, app.getServices().get(0));

		spaceClient.unbindService(appName, serviceName);

		app = spaceClient.getApplication(appName);
		assertNotNull(app.getServices());
		assertTrue(app.getServices().isEmpty());
	}

	@Test
	public void setEnvironmentThroughList() throws IOException {
		String appName = createSpringTravelApp("env1", null);
		CloudApplication app = spaceClient.getApplication(appName);
		assertTrue(app.getEnv().isEmpty());

		spaceClient.updateApplicationEnv(appName, asList("foo=bar", "bar=baz"));
		app = spaceClient.getApplication(app.getName());
		assertEquals(new HashSet<String>(asList("foo=bar", "bar=baz")), new HashSet<String>(app.getEnv()));

		spaceClient.updateApplicationEnv(appName, asList("foo=baz", "baz=bong"));
		app = spaceClient.getApplication(app.getName());
		assertEquals(new HashSet<String>(asList("foo=baz", "baz=bong")), new HashSet<String>(app.getEnv()));

		spaceClient.updateApplicationEnv(appName, new ArrayList<String>());
		app = spaceClient.getApplication(app.getName());
		assertTrue(app.getEnv().isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void setEnvironmentWithoutEquals() throws IOException {
		String appName = createSpringTravelApp("env2", null);
		CloudApplication app = spaceClient.getApplication(appName);
		assertTrue(app.getEnv().isEmpty());
		spaceClient.updateApplicationEnv(appName, asList("foo:bar", "bar=baz"));
	}

	@Test
	public void setEnvironmentThroughMap() throws IOException {
		String appName = createSpringTravelApp("env3", null);
		CloudApplication app = spaceClient.getApplication(appName);
		assertTrue(app.getEnv().isEmpty());

		Map<String, String> env1 = new HashMap<String, String>();
		env1.put("foo", "bar");
		env1.put("bar", "baz");
		spaceClient.updateApplicationEnv(appName, env1);
		app = spaceClient.getApplication(app.getName());
		assertEquals(env1, app.getEnvAsMap());
		assertEquals(new HashSet<String>(asList("foo=bar", "bar=baz")), new HashSet<String>(app.getEnv()));

		Map<String, String> env2 = new HashMap<String, String>();
		env2.put("foo", "baz");
		env2.put("baz", "bong");
		spaceClient.updateApplicationEnv(appName, env2);
		app = spaceClient.getApplication(app.getName());
		assertEquals(env2, app.getEnvAsMap());
		assertEquals(new HashSet<String>(asList("foo=baz", "baz=bong")), new HashSet<String>(app.getEnv()));

		spaceClient.updateApplicationEnv(appName, new HashMap<String, String>());
		app = spaceClient.getApplication(app.getName());
		assertTrue(app.getEnv().isEmpty());
		assertTrue(app.getEnvAsMap().isEmpty());
	}

	@Test
	public void updateApplicationMemory() throws IOException {
		String appName = createSpringTravelApp("mem1", null);
		CloudApplication app = spaceClient.getApplication(appName);

		assertEquals(client.getDefaultApplicationMemory("spring"), app.getMemory());

		client.updateApplicationMemory(appName, 256);
		app = client.getApplication(appName);
		assertEquals(256, app.getMemory());
	}

	@Test
	public void updateApplicationInstances() throws Exception {
		String appName = createSpringTravelApp("inst1", null);
		CloudApplication app = spaceClient.getApplication(appName);

		assertEquals(1, app.getInstances());

		client.updateApplicationInstances(appName, 3);
		app = client.getApplication(appName);
		assertEquals(3, app.getInstances());
	}

	@Test
	public void uploadApplication() throws IOException {
		String appName = createSpringTravelApp("upload1", null);
		CloudApplication app = spaceClient.getApplication(appName);

		assertNotNull(app);
		assertEquals(CloudApplication.AppState.STOPPED, app.getState());

		File file = SampleProjects.springTravel();
		spaceClient.uploadApplication(appName, file.getCanonicalPath());

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
		spaceClient.createApplication(appName, staging, 128, uris, services);
		spaceClient.uploadApplication(appName, file.getCanonicalPath());
		spaceClient.startApplication(appName);
		CloudApplication app = spaceClient.getApplication(appName);
		assertNotNull(app);
		assertEquals(CloudApplication.AppState.STARTED, app.getState());
//		TODO: uris is not currently part of v2 implementation
//		assertEquals(uris, app.getUris());
	}

	@Test
	public void uploadStandaloneApplicationWithURLs() throws IOException {
		String appName = namespacedAppName(TEST_NAMESPACE, "standalone-node");
		List<String> uris = new ArrayList<String>();
		uris.add(computeAppUrl(CCNG_URL, appName));
		List<String> services = new ArrayList<String>();
		Staging staging = new Staging("standalone");
		staging.setRuntime("node");
		staging.setCommand("node app.js");
		File file = SampleProjects.standaloneNode();
		spaceClient.createApplication(appName, staging, 64, uris, services);
		spaceClient.uploadApplication(appName, file.getCanonicalPath());
		spaceClient.startApplication(appName);
		CloudApplication app = spaceClient.getApplication(appName);
		assertNotNull(app);
		assertEquals(CloudApplication.AppState.STARTED, app.getState());
//		TODO: uris is not currently part of v2 implementation
//		assertEquals(Collections.singletonList(computeAppUrlNoProtocol(CCNG_URL, appName)), app.getUris());
	}

	@Test
	public void uploadApplicationWithServices() throws IOException {
		String serviceName = "test_database";
		List<String> serviceNames = new ArrayList<String>();
		serviceNames.add(serviceName);
		String appName = createSpringTravelApp("upload2", serviceNames);
		CloudApplication app = uploadSpringTravelApp(appName);
		assertEquals(1, app.getServices().size());
		assertEquals(serviceName, app.getServices().get(0));
	}

	@Test
	public void startApplication() throws IOException {
		String appName = createSpringTravelApp("start", null);
		uploadSpringTravelApp(appName);
		spaceClient.startApplication(appName);
		CloudApplication app = spaceClient.getApplication(appName);
		assertEquals(CloudApplication.AppState.STARTED, app.getState());
	}

	@Test
	public void stopApplication() throws IOException {
		String appName = createSpringTravelApp("stop", null);
		uploadSpringTravelApp(appName);
		spaceClient.startApplication(appName);
		CloudApplication app = spaceClient.getApplication(appName);
		assertEquals(CloudApplication.AppState.STARTED, app.getState());
		spaceClient.stopApplication(appName);
		app = spaceClient.getApplication(appName);
		assertEquals(CloudApplication.AppState.STOPPED, app.getState());
	}

	@Test
	public void reStartApplication() throws IOException {
		String appName = createSpringTravelApp("restart", null);
		uploadSpringTravelApp(appName);
		spaceClient.startApplication(appName);
		CloudApplication app = spaceClient.getApplication(appName);
		assertEquals(CloudApplication.AppState.STARTED, app.getState());
		spaceClient.restartApplication(appName);
		app = spaceClient.getApplication(appName);
		assertEquals(CloudApplication.AppState.STARTED, app.getState());
	}

	@Test
	public void uploadSinatraApp() throws IOException {
		String appName = namespacedAppName(TEST_NAMESPACE, "env");
		ClassPathResource cpr = new ClassPathResource("apps/env/");
		File explodedDir = cpr.getFile();
		Staging staging = new Staging("ruby19", "sinatra");
		createAndUploadExplodedTestApp(appName, explodedDir, staging);
		spaceClient.startApplication(appName);
		CloudApplication env = spaceClient.getApplication(appName);
		assertEquals(CloudApplication.AppState.STARTED, env.getState());
	}

	@Test
	public void uploadAppWithNonAsciiFileName() throws IOException {
		String appName = namespacedAppName(TEST_NAMESPACE, "non-ascii-file-name");
		List<String> uris = new ArrayList<String>();
		uris.add(computeAppUrl(CCNG_URL, appName));

		File war = SampleProjects.nonAsciFileName();
		List<String> serviceNames = new ArrayList<String>();

		spaceClient.createApplication(appName, new Staging("java", "spring"),
				spaceClient.getDefaultApplicationMemory(CloudApplication.SPRING), uris, serviceNames);
		spaceClient.uploadApplication(appName, war.getCanonicalPath());

		CloudApplication app = client.getApplication(appName);
		assertNotNull(app);
		assertEquals(CloudApplication.AppState.STOPPED, app.getState());

		spaceClient.startApplication(appName);

		app = spaceClient.getApplication(appName);
		assertNotNull(app);
		assertEquals(CloudApplication.AppState.STARTED, app.getState());

		spaceClient.deleteApplication(appName);
	}

	@Test
	public void updatePassword() throws MalformedURLException {
		String newPassword = "newPass123";
		spaceClient.updatePassword(newPassword);
		CloudFoundryClient clientWithChangedPassword =
				new CloudFoundryClient(new CloudCredentials(CCNG_USER_EMAIL, newPassword), new URL(CCNG_URL));
		clientWithChangedPassword.login();

		// Revert
		spaceClient.updatePassword(CCNG_USER_PASS);
		spaceClient.login();
	}

	private String createSpringTravelApp(String suffix, List<String> serviceNames) {
		List<String> uris = new ArrayList<String>();
		String appName = namespacedAppName(TEST_NAMESPACE, "travel_test-" + suffix);
		if (serviceNames != null) {
			for (String serviceName : serviceNames) {
				createMySqlService(serviceName);
			}
		}
		uris.add(computeAppUrl(CCNG_URL, appName));
		Staging staging =  new Staging("spring");
		staging.setRuntime("java");
		spaceClient.createApplication(appName, staging,
				spaceClient.getDefaultApplicationMemory("spring"), uris, serviceNames);
		return appName;
	}

	private void createApplication(String appName, List<String> serviceNames, Staging staging) {
		List<String> uris = new ArrayList<String>();
		uris.add(computeAppUrl(CCNG_URL, appName));
		if (serviceNames != null) {
			for (String serviceName : serviceNames) {
				createMySqlService(serviceName);
			}
		}
		spaceClient.createApplication(appName, staging, spaceClient.getDefaultApplicationMemory(staging.getFramework()),
				uris, serviceNames);
	}

	private CloudApplication uploadSpringTravelApp(String appName) throws IOException {
		File file = SampleProjects.springTravel();
		client.uploadApplication(appName, file.getCanonicalPath());
		return client.getApplication(appName);
	}

	private CloudApplication createAndUploadExplodedTestApp(String appName, File explodedDir, Staging staging)
			throws IOException {
		assertTrue("Expected exploded test app at " + explodedDir.getCanonicalPath(), explodedDir.exists());
		createApplication(appName, null, staging);
		client.uploadApplication(appName, explodedDir.getCanonicalPath());
		return client.getApplication(appName);
	}

	private void createMySqlService(String serviceName) {
		CloudService service = new CloudService(CloudEntity.Meta.defaultV2Meta(), serviceName);
		service.setType("database");
		service.setVersion("5.1");
		service.setProvider("core");
		service.setLabel("mysql");
		service.setPlan("D100");
		spaceClient.createService(service);
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
