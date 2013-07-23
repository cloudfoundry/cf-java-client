package org.cloudfoundry.client.lib;

import org.cloudfoundry.client.lib.domain.ApplicationStats;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudEntity;
import org.cloudfoundry.client.lib.domain.CloudInfo;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.CrashInfo;
import org.cloudfoundry.client.lib.domain.CrashesInfo;
import org.cloudfoundry.client.lib.domain.InstanceInfo;
import org.cloudfoundry.client.lib.domain.InstanceState;
import org.cloudfoundry.client.lib.domain.InstanceStats;
import org.cloudfoundry.client.lib.domain.InstancesInfo;
import org.cloudfoundry.client.lib.domain.ServiceConfiguration;
import org.cloudfoundry.client.lib.domain.Staging;
import org.cloudfoundry.client.lib.util.RestUtil;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

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
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeThat;
import static org.junit.Assume.assumeTrue;

/**
 * Abstract test class defining many common test methods for CloudControllerClient implementations of v1 and v2 APIs
 *
 * Note that this integration tests rely on other methods working correctly, so these tests aren't
 * independent unit tests for all methods, they are intended to test the completeness of the functionality of
 * each API version implementation.
 *
 * @author Ramnivas Laddad
 * @author A.B.Srinivasan
 * @author Jennifer Hickey
 * @author Thomas Risberg
 */
public abstract class AbstractCloudFoundryClientTest {

	private static final String V2_SERVICE_TEST_MYSQL_PLAN = "spark";

	@ClassRule
	public static CloudVersionRule cloudVersionRule = new CloudVersionRule();

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	//
	// Basic Services tests
	//

	@Test
	public void createService() throws MalformedURLException {
		assumeTrue(getCompleteApiSupported());

		createMySqlService("mysql-test");
	}

	@Test
	public void getService() throws MalformedURLException {
		assumeTrue(getCompleteApiSupported());

		String serviceName = "mysql-test";
		createMySqlService(serviceName);

		CloudService service = getConnectedClient().getService(serviceName);
		assertNotNull(service);
		assertEquals(serviceName, service.getName());
		// Allow more time deviations due to local clock being out of sync with cloud
		int timeTolerance = 300 * 1000; // 5 minutes
		assertTrue("Creation time should be very recent",
				Math.abs(System.currentTimeMillis() - service.getMeta().getCreated().getTime()) < timeTolerance);
	}

	@Test
	public void deleteService() throws MalformedURLException {
		assumeTrue(getCompleteApiSupported());

		String serviceName = "mysql-test";
		createMySqlService(serviceName);
		getConnectedClient().deleteService(serviceName);
	}

	@Test
	public void getServices() {
		assumeTrue(getCompleteApiSupported());

		String serviceName = "mysql-test";
		createMySqlService(serviceName);
		createMySqlService("mysql-test2");

		List<CloudService> services = getConnectedClient().getServices();
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
		if (getInfo().getCloudControllerMajorVersion() == CloudInfo.CC_MAJOR_VERSION.V1) {
			assertEquals("mysql", service.getVendor());
			assertEquals("database", service.getType());
			assertEquals("5.1", service.getVersion());
			assertEquals("free", service.getTier());
		} else {
			assertEquals(getMysqlLabel(), service.getLabel());
			assertEquals("cleardb", service.getProvider());
			assertEquals("n/a", service.getVersion());
			assertEquals(V2_SERVICE_TEST_MYSQL_PLAN, service.getPlan());
		}
	}


	//
	// Basic Application tests
	//

	@Test
	public void createApplication() {
		List<String> uris = new ArrayList<String>();
		String appName = namespacedAppName("travel_test-0");
		uris.add(computeAppUrl(appName));
		Staging staging =  new Staging("spring");
		staging.setRuntime("java");
		getConnectedClient().createApplication(appName, staging,
				getTestAppMemory("spring"), uris, null);
		CloudApplication app = getConnectedClient().getApplication(appName);
		assertNotNull(app);
		assertEquals(appName, app.getName());
	}

	@Test
	public void getApplication() {
		String appName = createSpringTravelApp("1", null);
		CloudApplication app = getConnectedClient().getApplication(appName);
		assertNotNull(app);
		assertEquals(appName, app.getName());
	}

	@Test
	public void getApplicationNonExistent() {
		thrown.expect(CloudFoundryException.class);
		thrown.expect(hasProperty("statusCode", is(HttpStatus.NOT_FOUND)));
		thrown.expectMessage(containsString("Not Found"));
		String appName = namespacedAppName("non_existent");
		getConnectedClient().getApplication(appName);
	}

	@Test
	public void getApplications() {
		String appName = createSpringTravelApp("2", null);
		List<CloudApplication> apps = getConnectedClient().getApplications();
		assertEquals(1, apps.size());
		assertEquals(appName, apps.get(0).getName());
		assertNotNull(apps.get(0));
		assertNotNull(apps.get(0).getMeta());
		if (getInfo().getCloudControllerMajorVersion() == CloudInfo.CC_MAJOR_VERSION.V2) {
			assertNotNull(apps.get(0).getMeta().getGuid());
		}

		createSpringTravelApp("3", null);
		apps = getConnectedClient().getApplications();
		assertEquals(2, apps.size());
	}

	@Test
	public void deleteApplication() {
		String appName = createSpringTravelApp("4", null);
		assertEquals(1, getConnectedClient().getApplications().size());
		getConnectedClient().deleteApplication(appName);
		assertEquals(0, getConnectedClient().getApplications().size());
	}

	@Test
	public void uploadApplication() throws IOException {
		String appName = createSpringTravelApp("upload1", null);

		File file = SampleProjects.springTravel();
		getConnectedClient().uploadApplication(appName, file.getCanonicalPath());

		CloudApplication app = getConnectedClient().getApplication(appName);
		assertNotNull(app);
		assertEquals(CloudApplication.AppState.STOPPED, app.getState());

		String url = computeAppUrlNoProtocol(appName);
		assertEquals(url, app.getUris().get(0));
	}

    @Test
    public void uploadApplicationWithBuildPack() throws IOException {
        String buildpackUrl = "https://github.com/cloudfoundry/java-buildpack.git";
        String appName = createSpringTravelApp("upload1", null, buildpackUrl);

        File file = SampleProjects.springTravel();
        getConnectedClient().uploadApplication(appName, file.getCanonicalPath());

        CloudApplication app = getConnectedClient().getApplication(appName);
        assertNotNull(app);
        assertEquals(CloudApplication.AppState.STOPPED, app.getState());

        String url = computeAppUrlNoProtocol(appName);
        assertEquals(url, app.getUris().get(0));

        assertEquals(buildpackUrl, app.getBuildpackUrl());
    }


    @Test
	public void startApplication() throws IOException {
		String appName = createSpringTravelApp("start", null);
		uploadSpringTravelApp(appName);
		getConnectedClient().startApplication(appName);
		CloudApplication app = getConnectedClient().getApplication(appName);
		assertEquals(CloudApplication.AppState.STARTED, app.getState());
	}

	@Test
	public void stopApplication() throws IOException {
		String appName = createSpringTravelApp("stop", null);
		uploadSpringTravelApp(appName);
		getConnectedClient().startApplication(appName);
		CloudApplication app = getConnectedClient().getApplication(appName);
		assertEquals(CloudApplication.AppState.STARTED, app.getState());
		getConnectedClient().stopApplication(appName);
		app = getConnectedClient().getApplication(appName);
		assertEquals(CloudApplication.AppState.STOPPED, app.getState());
	}

	@Test
	public void reStartApplication() throws IOException {
		String appName = createSpringTravelApp("restart", null);
		uploadSpringTravelApp(appName);
		getConnectedClient().startApplication(appName);
		boolean passSingleInstance = getInstanceInfosWithTimeout(appName, 1, true);
		assertTrue("Couldn't get the right application state in 50 tries", passSingleInstance);

		CloudApplication app = getConnectedClient().getApplication(appName);
		assertEquals(CloudApplication.AppState.STARTED, app.getState());
		getConnectedClient().restartApplication(appName);
		app = getConnectedClient().getApplication(appName);
		assertEquals(CloudApplication.AppState.STARTED, app.getState());
	}

	//
	// Application and Services tests
	//

	@Test
	public void createApplicationWithService() {
		assumeTrue(getCompleteApiSupported());

		String serviceName = "test_database";
		createMySqlService(serviceName);
		List<String> serviceNames= Collections.singletonList(serviceName);
		List<String> uris = new ArrayList<String>();
		String appName = namespacedAppName("travel_test8");
		uris.add(computeAppUrl(appName));
		Staging staging =  new Staging("spring");
		staging.setRuntime("java");
		getConnectedClient().createApplication(appName, staging,
				getTestAppMemory("spring"), uris, serviceNames);
		CloudApplication app = getConnectedClient().getApplication(appName);
		assertNotNull(app);
		assertEquals(appName, app.getName());
		assertNotNull(app.getServices());
		assertEquals(1, app.getServices().size());
		assertEquals(serviceName, app.getServices().get(0));
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
	public void deleteServiceThatIsBoundToApp() throws MalformedURLException {
		assumeTrue(getCompleteApiSupported());

		String serviceName = "mysql-del-svc";
		List<String> serviceNames = new ArrayList<String>();
		serviceNames.add(serviceName);
		String appName = createSpringTravelApp("del-svc", serviceNames);

		CloudApplication app = getConnectedClient().getApplication(appName);
		assertNotNull(app.getServices());
		assertEquals(1, app.getServices().size());
		assertEquals(serviceName, app.getServices().get(0));

		getConnectedClient().deleteService(serviceName);
	}

	@Test
	public void updateApplicationService() throws IOException {
		assumeTrue(getCompleteApiSupported());

		String serviceName = "test_database";
		createMySqlService(serviceName);
		String appName = createSpringTravelApp("7", null);

		getConnectedClient().updateApplicationServices(appName, Collections.singletonList(serviceName));
		CloudApplication app = getConnectedClient().getApplication(appName);
		assertNotNull(app.getServices());
		assertTrue(app.getServices().size() > 0);
		assertEquals(serviceName, app.getServices().get(0));

		List<String> emptyList = Collections.emptyList();
		getConnectedClient().updateApplicationServices(appName, emptyList);
		app = getConnectedClient().getApplication(appName);
		assertNotNull(app.getServices());
		assertEquals(emptyList, app.getServices());
	}

	@Test
	public void bindAndUnbindService() throws IOException {
		assumeTrue(getCompleteApiSupported());

		String serviceName = "test_database";
		createMySqlService(serviceName);

		String appName = createSpringTravelApp("bind1", null);

		CloudApplication app = getConnectedClient().getApplication(appName);
		assertNotNull(app.getServices());
		assertTrue(app.getServices().isEmpty());

		getConnectedClient().bindService(appName, serviceName);

		app = getConnectedClient().getApplication(appName);
		assertNotNull(app.getServices());
		assertEquals(1, app.getServices().size());
		assertEquals(serviceName, app.getServices().get(0));

		getConnectedClient().unbindService(appName, serviceName);

		app = getConnectedClient().getApplication(appName);
		assertNotNull(app.getServices());
		assertTrue(app.getServices().isEmpty());
	}

	//
	// App configuration tests
	//

	@Test
	public void setEnvironmentThroughList() throws IOException {
		String appName = createSpringTravelApp("env1", null);
		CloudApplication app = getConnectedClient().getApplication(appName);
		assertTrue(app.getEnv().isEmpty());

		getConnectedClient().updateApplicationEnv(appName, asList("foo=bar", "bar=baz"));
		app = getConnectedClient().getApplication(app.getName());
		assertEquals(new HashSet<String>(asList("foo=bar", "bar=baz")), new HashSet<String>(app.getEnv()));

		getConnectedClient().updateApplicationEnv(appName, asList("foo=baz", "baz=bong"));
		app = getConnectedClient().getApplication(app.getName());
		assertEquals(new HashSet<String>(asList("foo=baz", "baz=bong")), new HashSet<String>(app.getEnv()));

		getConnectedClient().updateApplicationEnv(appName, new ArrayList<String>());
		app = getConnectedClient().getApplication(app.getName());
		assertTrue(app.getEnv().isEmpty());
	}

	@Test
	public void setEnvironmentWithoutEquals() throws IOException {
		thrown.expect(IllegalArgumentException.class);
		String appName = createSpringTravelApp("env2", null);
		CloudApplication app = getConnectedClient().getApplication(appName);
		assertTrue(app.getEnv().isEmpty());
		getConnectedClient().updateApplicationEnv(appName, asList("foo:bar", "bar=baz"));
	}

	@Test
	public void setEnvironmentThroughMap() throws IOException {
		String appName = createSpringTravelApp("env3", null);
		CloudApplication app = getConnectedClient().getApplication(appName);
		assertTrue(app.getEnv().isEmpty());

		Map<String, String> env1 = new HashMap<String, String>();
		env1.put("foo", "bar");
		env1.put("bar", "baz");
		getConnectedClient().updateApplicationEnv(appName, env1);
		app = getConnectedClient().getApplication(app.getName());
		assertEquals(env1, app.getEnvAsMap());
		assertEquals(new HashSet<String>(asList("foo=bar", "bar=baz")), new HashSet<String>(app.getEnv()));

		Map<String, String> env2 = new HashMap<String, String>();
		env2.put("foo", "baz");
		env2.put("baz", "bong");
		getConnectedClient().updateApplicationEnv(appName, env2);
		app = getConnectedClient().getApplication(app.getName());
		assertEquals(env2, app.getEnvAsMap());
		assertEquals(new HashSet<String>(asList("foo=baz", "baz=bong")), new HashSet<String>(app.getEnv()));

		getConnectedClient().updateApplicationEnv(appName, new HashMap<String, String>());
		app = getConnectedClient().getApplication(app.getName());
		assertTrue(app.getEnv().isEmpty());
		assertTrue(app.getEnvAsMap().isEmpty());
	}

	@Test
	public void updateApplicationMemory() throws IOException {
		String appName = createSpringTravelApp("mem1", null);
		CloudApplication app = getConnectedClient().getApplication(appName);

		assertEquals(getTestAppMemory("spring"), app.getMemory());

		getConnectedClient().updateApplicationMemory(appName, 256);
		app = getConnectedClient().getApplication(appName);
		assertEquals(256, app.getMemory());
	}

	@Test
	public void updateApplicationInstances() throws Exception {
		String appName = createSpringTravelApp("inst1", null);
		CloudApplication app = getConnectedClient().getApplication(appName);

		assertEquals(1, app.getInstances());

		getConnectedClient().updateApplicationInstances(appName, 3);
		app = getConnectedClient().getApplication(appName);
		assertEquals(3, app.getInstances());

		getConnectedClient().updateApplicationInstances(appName, 1);
		app = getConnectedClient().getApplication(appName);
		assertEquals(1, app.getInstances());
	}

	@Test
	public void updateApplicationUris() throws IOException {
		assumeTrue(getCompleteApiSupported());

		String appName = namespacedAppName("url1");
		CloudApplication app = createAndUploadAndStartSimpleSpringApp(appName);

		List<String> originalUris = app.getUris();
		assertEquals(Collections.singletonList(computeAppUrlNoProtocol(appName)), originalUris);

		List<String> uris = new ArrayList<String>(app.getUris());
		uris.add(computeAppUrlNoProtocol(namespacedAppName("url2")));
		getConnectedClient().updateApplicationUris(appName, uris);
		app = getConnectedClient().getApplication(appName);
		List<String> newUris = app.getUris();
		assertNotNull(newUris);
		assertEquals(uris.size(), newUris.size());
		for (String uri : uris) {
			assertTrue(newUris.contains(uri));
		}
		getConnectedClient().updateApplicationUris(appName, originalUris);
		app = getConnectedClient().getApplication(appName);
		assertEquals(originalUris, app.getUris());
	}


	//
	// Advanced Application tests
	//

	@Test
	public void uploadStandaloneApplication() throws IOException {
		String appName = namespacedAppName("standalone-ruby");
		List<String> uris = new ArrayList<String>();
		List<String> services = new ArrayList<String>();
		createStandaloneRubyTestApp(appName, uris, services);
		getConnectedClient().startApplication(appName);
		CloudApplication app = getConnectedClient().getApplication(appName);
		assertNotNull(app);
		assertEquals(CloudApplication.AppState.STARTED, app.getState());
		assertEquals(uris, app.getUris());
	}

	@Test
	public void uploadStandaloneApplicationWithURLs() throws IOException {
		String appName = namespacedAppName("standalone-node");
		List<String> uris = new ArrayList<String>();
		uris.add(computeAppUrl(appName));
		List<String> services = new ArrayList<String>();
		Staging staging = new Staging("standalone");
		staging.setRuntime("node");
		staging.setCommand("node app.js");
		File file = SampleProjects.standaloneNode();
		getConnectedClient().createApplication(appName, staging, 64, uris, services);
		getConnectedClient().uploadApplication(appName, file.getCanonicalPath());
		getConnectedClient().startApplication(appName);
		CloudApplication app = getConnectedClient().getApplication(appName);
		assertNotNull(app);
		assertEquals(CloudApplication.AppState.STARTED, app.getState());
		assertEquals(Collections.singletonList(computeAppUrlNoProtocol(appName)), app.getUris());
	}

	@Test
	public void updateStandaloneApplicationCommand() throws IOException {
		String appName = namespacedAppName("standalone-ruby");
		List<String> uris = new ArrayList<String>();
		List<String> services = new ArrayList<String>();
		createStandaloneRubyTestApp(appName, uris, services);
		getConnectedClient().startApplication(appName);
		CloudApplication app = getConnectedClient().getApplication(appName);
		assertNotNull(app);
		assertEquals(CloudApplication.AppState.STARTED, app.getState());
		assertEquals(uris, app.getUris());
		assertEquals("ruby simple.rb", app.getStaging().getCommand());
		getConnectedClient().stopApplication(appName);

		Staging newStaging = app.getStaging();
		newStaging.setCommand("ruby simple.rb test");
		getConnectedClient().updateApplicationStaging(appName, newStaging);
		app = getConnectedClient().getApplication(appName);
		assertNotNull(app);
		assertEquals(uris, app.getUris());
		assertEquals("ruby simple.rb test", app.getStaging().getCommand());
	}

	@Test
	public void renameApplication() {
		//TODO: Doesn't work for V1
		assumeThat(getInfo().getCloudControllerMajorVersion(), is(CloudInfo.CC_MAJOR_VERSION.V2));
		String appName = createSpringTravelApp("5", null);
		CloudApplication app = getConnectedClient().getApplication(appName);
		assertNotNull(app);
		assertEquals(appName, app.getName());
		String newName = namespacedAppName("travel_test-6");
		getConnectedClient().rename(appName, newName);
		CloudApplication newApp = getConnectedClient().getApplication(newName);
		assertNotNull(newApp);
		assertEquals(newName, newApp.getName());
	}

	@Test
	public void createAndReCreateApplication() {
		String appName = createSpringTravelApp("A", null);
		assertEquals(1, getConnectedClient().getApplications().size());
		getConnectedClient().deleteApplication(appName);
		appName = createSpringTravelApp("A", null);
		assertEquals(1, getConnectedClient().getApplications().size());
		getConnectedClient().deleteApplication(appName);
	}

	@Test
	public void getApplicationsMatchGetApplication() {
		String appName = createSpringTravelApp("1", null);
		List<CloudApplication> apps = getConnectedClient().getApplications();
		assertEquals(1, apps.size());
		CloudApplication app = getConnectedClient().getApplication(appName);
		assertEquals(app.getName(), apps.get(0).getName());
		assertEquals(app.getState(), apps.get(0).getState());
		assertEquals(app.getInstances(), apps.get(0).getInstances());
		assertEquals(app.getMemory(), apps.get(0).getMemory());
		assertEquals(app.getMeta().getGuid(), apps.get(0).getMeta().getGuid());
		assertEquals(app.getMeta().getCreated(), apps.get(0).getMeta().getCreated());
		assertEquals(app.getMeta().getUpdated(), apps.get(0).getMeta().getUpdated());
		assertEquals(app.getUris(), apps.get(0).getUris());
	}

	@Test
	public void getApplicationInstances() throws Exception {
		String appName = namespacedAppName("instance1");
		CloudApplication app = createAndUploadAndStartSimpleSpringApp(appName);
		assertEquals(1, app.getInstances());

		boolean passSingleInstance = getInstanceInfosWithTimeout(appName, 1, true);
		assertTrue("Couldn't get the right application state in 50 tries", passSingleInstance);

		boolean passSingleMultipleInstances = getInstanceInfosWithTimeout(appName, 3, true);
		assertTrue("Couldn't get the right application state in 50 tries", passSingleMultipleInstances);

		getConnectedClient().stopApplication(appName);
		InstancesInfo instInfo = getConnectedClient().getApplicationInstances(appName);
		assertEquals(0, instInfo.getInstances().size());
	}

	private boolean getInstanceInfosWithTimeout(String appName, int count, boolean shouldBeRunning) {
		if (count > 1) {
			getConnectedClient().updateApplicationInstances(appName, count);
			CloudApplication app = getConnectedClient().getApplication(appName);
			assertEquals(count, app.getInstances());
		}

		InstancesInfo instances = null;
		boolean pass = false;
		for (int i = 0; i < 50; i++) {
			try {
				instances = getInstancesWithTimeout(getConnectedClient(), appName);
				assertNotNull(instances);

				List<InstanceInfo> infos = instances.getInstances();
				assertEquals(count, infos.size());

				int passCount = 0;
				for (InstanceInfo info : infos) {
					if (shouldBeRunning) {
						if (InstanceState.RUNNING.equals(info.getState()) ||
								InstanceState.STARTING.equals(info.getState())) {
							passCount++;
						}
					} else {
						if (InstanceState.CRASHED.equals(info.getState()) ||
								InstanceState.FLAPPING.equals(info.getState())) {
							passCount++;
						}
					}
				}
				if (passCount == infos.size()) {
					pass = true;
					break;
				}
			} catch (CloudFoundryException ex) {
				// ignore (we may get this when staging is still ongoing)
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// ignore
			}
		}
		return pass;
	}

	@Test
	public void getApplicationRunningInstances() throws Exception {
		String appName = namespacedAppName("inst2");
		createAndUploadAndStartSimpleSpringApp(appName);

		boolean passSingleInstance = getInstanceInfosWithTimeout(appName, 1, true);
		assertTrue("Couldn't get the right application state in 50 tries", passSingleInstance);

		getConnectedClient().updateApplicationInstances(appName, 2);
		CloudApplication app = getConnectedClient().getApplication(appName);
		assertEquals(2, app.getInstances());
		getConnectedClient().startApplication(appName);

		boolean running = getInstanceInfosWithTimeout(appName, 2, true);
		assertTrue("App failed to start 2 instances", running);		
	}

	@Test
	@Ignore("Ignore until the Java buildpack detects app crashes upon OOM correctly")
	public void getCrashes() throws IOException, InterruptedException {
		String appName = namespacedAppName("crashes1");
		createAndUploadSimpleSpringApp(appName);
		getConnectedClient().updateApplicationEnv(appName, Collections.singletonMap("crash", "true"));
		getConnectedClient().startApplication(appName);

		boolean pass = getInstanceInfosWithTimeout(appName, 1, false);
		assertTrue("Couldn't get the right application state in 50 tries", pass);

		CrashesInfo crashes = getConnectedClient().getCrashes(appName);
		assertNotNull(crashes);
		assertTrue(!crashes.getCrashes().isEmpty());
		for (CrashInfo info : crashes.getCrashes()) {
			assertNotNull(info.getInstance());
			assertNotNull(info.getSince());
		}
	}

	@Test
	public void accessRandomApplicationUrl() throws Exception {
		String appName = UUID.randomUUID().toString();
		createAndUploadAndStartSimpleSpringApp(appName);
		CloudApplication app = getConnectedClient().getApplication(appName);
		getConnectedClient().startApplication(appName);
		assertEquals(1, app.getInstances());
		for (int i = 0; i < 100 && app.getRunningInstances() < 1; i++) {
			Thread.sleep(1000);
			app = getConnectedClient().getApplication(appName);
		}
		app = getConnectedClient().getApplication(appName);
		assertEquals(1, app.getRunningInstances());
		RestUtil restUtil = new RestUtil();
		RestTemplate rest = restUtil.createRestTemplate(null);
		String results = rest.getForObject("http://" + app.getUris().get(0), String.class);
		assertTrue(results.contains("Hello world!"));
	}

	@Test
	public void getApplicationStats() throws Exception {
		String appName = namespacedAppName("stats2");
		CloudApplication app = createAndUploadAndStartSimpleSpringApp(appName);

		assertEquals(CloudApplication.AppState.STARTED, app.getState());

		ApplicationStats stats = getConnectedClient().getApplicationStats(appName);

		assertNotNull(stats);
		assertNotNull(stats.getRecords());
		// TODO: Make this pattern reusable
		for (int i = 0; i < 10 && stats.getRecords().size() < 1; i++) {
			Thread.sleep(1000);
			stats = getConnectedClient().getApplicationStats(appName);
		}
		assertEquals(1, stats.getRecords().size());
		InstanceStats firstInstance = stats.getRecords().get(0);
		assertEquals("0", firstInstance.getId());
		for (int i = 0; i < 10 && firstInstance.getUsage() == null; i++) {
			Thread.sleep(1000);
			stats = getConnectedClient().getApplicationStats(appName);
			firstInstance = stats.getRecords().get(0);
		}
		assertNotNull(firstInstance.getUsage());

		// Allow more time deviations due to local clock being out of sync with cloud
		int timeTolerance = 300 * 1000; // 5 minutes
		assertTrue("Usage time should be very recent",
				Math.abs(System.currentTimeMillis() - firstInstance.getUsage().getTime().getTime()) < timeTolerance);

		getConnectedClient().updateApplicationInstances(appName, 3);
		stats = getConnectedClient().getApplicationStats(appName);
		assertNotNull(stats);
		assertNotNull(stats.getRecords());
		assertEquals(3, stats.getRecords().size());
	}

	@Test
	public void getApplicationStatsStoppedApp() throws IOException {
		String appName = namespacedAppName("stats2");
		createAndUploadAndStartSimpleSpringApp(appName);
		getConnectedClient().stopApplication(appName);

		ApplicationStats stats = getConnectedClient().getApplicationStats(appName);
		assertEquals(Collections.emptyList(), stats.getRecords());
	}

	@Test
	public void uploadSinatraApp() throws IOException {
		String appName = namespacedAppName("env");
		ClassPathResource cpr = new ClassPathResource("apps/env/");
		File explodedDir = cpr.getFile();
		Staging staging = new Staging("ruby19", "sinatra");
		createAndUploadExplodedTestApp(appName, explodedDir, staging);
		getConnectedClient().startApplication(appName);
		CloudApplication env = getConnectedClient().getApplication(appName);
		assertEquals(CloudApplication.AppState.STARTED, env.getState());
	}

	@Test
	public void uploadAppWithNonAsciiFileName() throws IOException {
		String appName = namespacedAppName("non-ascii-file-name");
		List<String> uris = new ArrayList<String>();
		uris.add(computeAppUrl(appName));

		File war = SampleProjects.nonAsciFileName();
		List<String> serviceNames = new ArrayList<String>();

		getConnectedClient().createApplication(appName, new Staging("java", "spring"),
				getTestAppMemory("spring"), uris, serviceNames);
		getConnectedClient().uploadApplication(appName, war.getCanonicalPath());

		CloudApplication app = getConnectedClient().getApplication(appName);
		assertNotNull(app);
		assertEquals(CloudApplication.AppState.STOPPED, app.getState());

		getConnectedClient().startApplication(appName);

		app = getConnectedClient().getApplication(appName);
		assertNotNull(app);
		assertEquals(CloudApplication.AppState.STARTED, app.getState());

		getConnectedClient().deleteApplication(appName);
	}

	@Test
	public void startExplodedApplication() throws IOException {
		String appName = namespacedAppName("exploded_app");
		createAndUploadExplodedSpringTestApp(appName);
		getConnectedClient().startApplication(appName);
		CloudApplication app = getConnectedClient().getApplication(appName);
		assertEquals(CloudApplication.AppState.STARTED, app.getState());
	}

	//
	// Configuration/Metadata tests
	//

	@Test
	public void infoAvailableWithoutLoggingIn() throws Exception {
		CloudFoundryClient infoClient = new CloudFoundryClient(new URL(getApiUrl()));
		CloudInfo info = infoClient.getCloudInfo();
		assertNotNull(info.getName());
		assertNotNull(info.getSupport());
		assertNotNull(info.getBuild());
		assertTrue(info.getUser() == null);
	}

	@Test
	public void infoForUserAvailable() throws Exception {
		CloudInfo info = getConnectedClient().getCloudInfo();

		assertNotNull(info.getName());
		assertNotNull(info.getSupport());
		assertNotNull(info.getBuild());
		assertNotNull(info.getSupport());
		assertNotNull(info.getSupport());

		assertEquals(getTestUser(), info.getUser());
		assertNotNull(info.getLimits());
		// Just ensure that we got back some sensible values
		assertTrue(info.getLimits().getMaxApps() > 0 && info.getLimits().getMaxApps() < 1000);
		assertTrue(info.getLimits().getMaxServices() > 0 && info.getLimits().getMaxServices() < 1000);
		assertTrue(info.getLimits().getMaxTotalMemory() > 0 && info.getLimits().getMaxTotalMemory() < 100000);
		assertTrue(info.getLimits().getMaxUrisPerApp() > 0 && info.getLimits().getMaxUrisPerApp() < 100);
	}

	@Test
	public void getApplicationMemoryChoices() {
		int springMemory = getConnectedClient().getDefaultApplicationMemory("spring");
		assertEquals(512, springMemory);
		int railsMemory = getConnectedClient().getDefaultApplicationMemory("rails");
		assertEquals(256, railsMemory);
		int[] choices = getConnectedClient().getApplicationMemoryChoices();
		assertNotNull(choices);
		assertNotSame(0, choices.length);
		assertTrue(getConnectedClient().getCloudInfo().getLimits().getMaxTotalMemory() >= choices[choices.length - 1]);
	}

	//
	// Files and Log tests
	//

	@Test
	public void getLogs() throws Exception {
		String appName = namespacedAppName("simple_logs");
		createAndUploadAndStartSimpleSpringApp(appName);
		boolean pass = getInstanceInfosWithTimeout(appName, 1, true);
		assertTrue("Couldn't get the right application state", pass);

		Thread.sleep(10000); // let's have some time to get some logs generated
		Map<String, String> logs = getConnectedClient().getLogs(appName);
		assertNotNull(logs);
		assertTrue(logs.size() > 0);
		for (String log : logs.keySet()) {
			assertNotNull(logs.get(log));
		}
	}

	@Test
	@Ignore("Ignore until the Java buildpack detects app crashes upon OOM correctly")
	public void getCrashLogs() throws Exception {
		String appName = namespacedAppName("simple_crashlogs");
		createAndUploadSimpleSpringApp(appName);
		getConnectedClient().updateApplicationEnv(appName, Collections.singletonMap("crash", "true"));
		getConnectedClient().startApplication(appName);

		boolean pass = getInstanceInfosWithTimeout(appName, 1, false);
		assertTrue("Couldn't get the right application state in 50 tries", pass);

		Map<String, String> logs = getConnectedClient().getCrashLogs(appName);
		assertNotNull(logs);
		assertTrue(logs.size() > 0);
		for (String log : logs.keySet()) {
			assertNotNull(logs.get(log));
		}
	}

	@Test
	public void getFile() throws Exception {
		String appName = namespacedAppName("simple_getFile");
		createAndUploadAndStartSimpleSpringApp(appName);
		boolean running = getInstanceInfosWithTimeout(appName, 1, true);
		assertTrue("App failed to start", running);		
		doGetFile(getConnectedClient(), appName);
	}

	@Test
	public void getRestLog() throws IOException {
		final List<RestLogEntry> log1 = new ArrayList<RestLogEntry>();
		final List<RestLogEntry> log2 = new ArrayList<RestLogEntry>();
		getConnectedClient().registerRestLogListener(new RestLogCallback() {
			public void onNewLogEntry(RestLogEntry logEntry) {
				log1.add(logEntry);
			}
		});
		RestLogCallback callback2 = new RestLogCallback() {
			public void onNewLogEntry(RestLogEntry logEntry) {
				log2.add(logEntry);
			}
		};
		getConnectedClient().registerRestLogListener(callback2);
		getApplications();
		getConnectedClient().deleteAllApplications();
		assertTrue(log1.size() > 0);
		assertEquals(log1, log2);
		getConnectedClient().unRegisterRestLogListener(callback2);
		getApplications();
		getConnectedClient().deleteAllApplications();
		assertTrue(log1.size() > log2.size());
	}

	//
	// Shared test methods
	//

	protected void doGetFile(CloudFoundryClient client, String appName) throws Exception {
		String appDir = "tomcat";
		
		if (getInfo().getCloudControllerMajorVersion().equals(CloudInfo.CC_MAJOR_VERSION.V2)) {
			appDir = "app";
		}
		String fileName = appDir + "/webapps/ROOT/WEB-INF/web.xml";
		String emptyPropertiesfileName = appDir + "/webapps/ROOT/WEB-INF/classes/empty.properties";

		// File is often not available immediately after starting an app... so allow upto 30 seconds wait
		for (int i = 0; i < 30; i++) {
			try {
				client.getFile(appName, 0, fileName);
				break;
			} catch (HttpServerErrorException ex) {
				Thread.sleep(1000);
			}
		}

		// Test downloading full file
		String fileContent = client.getFile(appName, 0, fileName);
		assertNotNull(fileContent);
		assertTrue(fileContent.length() > 5);

		// Test downloading range of file with start and end position
		int end = fileContent.length() - 3;
		int start = end/2;
		String fileContent2 = client.getFile(appName, 0, fileName, start, end);
		assertEquals(fileContent.substring(start, end), fileContent2);

		// Test downloading range of file with just start position
		String fileContent3 = client.getFile(appName, 0, fileName, start);
		assertEquals(fileContent.substring(start), fileContent3);

		// Test downloading range of file with start position and end position exceeding the length
		int positionPastEndPosition = fileContent.length() + 999;
		String fileContent4 = client.getFile(appName, 0, fileName, start, positionPastEndPosition);
		assertEquals(fileContent.substring(start), fileContent4);

		// Test downloading end portion of file with length
		int length = fileContent.length() / 2;
		String fileContent5 = client.getFileTail(appName, 0, fileName, length);
		assertEquals(fileContent.substring(fileContent.length() - length), fileContent5);

		// Test downloading one byte of file with start and end position
		String fileContent6 = client.getFile(appName, 0, fileName, start, start + 1);
		assertEquals(fileContent.substring(start, start + 1), fileContent6);
		assertEquals(1, fileContent6.length());

		// Test downloading range of file with invalid start position
		int invalidStartPosition = fileContent.length() + 999;
		try {
			client.getFile(appName, 0, fileName, invalidStartPosition);
			fail("should have thrown exception");
		} catch (CloudFoundryException e) {
			assertTrue(e.getStatusCode().equals(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE));
		}

		// Test downloading empty file
		String fileContent7 = client.getFile(appName, 0, emptyPropertiesfileName);
		assertNotNull(fileContent7);
		assertTrue(fileContent7.length() == 0);

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
			client.getFile(appName, 0, fileName, 29, 28);
			fail("should have thrown exception");
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("end position"));
		}
		try {
			client.getFile(appName, 0, fileName, 29, 28);
			fail("should have thrown exception");
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("29"));
		}
		try {
			client.getFileTail(appName, 0, fileName, 0);
			fail("should have thrown exception");
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("length"));
		}
	}

	//
	// Abstract getters for test config to be provided by sub-classes
	//

	protected abstract String getApiUrl();

	protected abstract CloudInfo getInfo();

	protected abstract String getNamespace();

	protected abstract CloudFoundryClient getConnectedClient();

	protected abstract String getTestUser();

	protected abstract int getTestAppMemory(String framework);

	protected abstract boolean getCompleteApiSupported();

	protected abstract String getMysqlLabel();
	//
	// Shared helper methods
	//

	protected CloudApplication createAndUploadTravelTestApp(String name) throws IOException {
		createSpringApplication(name, null);
		uploadSpringTravelApp(name);
		return getConnectedClient().getApplication(name);
	}

	protected CloudApplication createAndUploadSimpleTestApp(String name) throws IOException {
		createAndUploadSimpleSpringApp(name);
		return getConnectedClient().getApplication(name);
	}

	protected String computeAppUrlNoProtocol(String appName) {
		String ccUrl = getApiUrl();
		//TODO: remove ccng at some point?
		return ccUrl.replace("api", appName).replace("ccng", appName).replace("http://", "").replace("https://", "");
	}

	protected String namespacedAppName(String basename) {
		return getNamespace() + "-" + basename;
	}

	protected static String defaultNamespace(String email) {
		return email.substring(0, email.indexOf('@')).replaceAll("\\.", "-").replaceAll("\\+", "-");
	}

	//
	// helper methods
	//

	protected String createSpringTravelApp(String suffix, List<String> serviceNames) {
        return createSpringTravelApp(suffix, serviceNames, null);
	}
	protected String createSpringTravelApp(String suffix, List<String> serviceNames, String buildpackUrl) {
		String appName = namespacedAppName("travel_test-" + suffix);
		createSpringApplication(appName, serviceNames, buildpackUrl);
		return appName;
	}

	protected CloudApplication uploadSpringTravelApp(String appName) throws IOException {
		File file = SampleProjects.springTravel();
		getConnectedClient().uploadApplication(appName, file.getCanonicalPath());
		return getConnectedClient().getApplication(appName);
	}

	private CloudApplication createAndUploadExplodedSpringTestApp(String appName)
			throws IOException {
		Staging staging =  new Staging("spring");
		staging.setRuntime("java");
		File explodedDir = SampleProjects.springTravelUnpacked(temporaryFolder);
		assertTrue("Expected exploded test app at " + explodedDir.getCanonicalPath(), explodedDir.exists());
		createTestApp(appName, null, staging);
		getConnectedClient().uploadApplication(appName, explodedDir.getCanonicalPath());
		return getConnectedClient().getApplication(appName);
	}

	private CloudApplication createAndUploadAndStartSimpleSpringApp(String appName) throws IOException {
		createAndUploadSimpleSpringApp(appName);
		getConnectedClient().startApplication(appName);
		return getConnectedClient().getApplication(appName);
	}

	private CloudApplication createAndUploadSimpleSpringApp(String appName) throws IOException {
		createSpringApplication(appName, null);
		File war = SampleProjects.simpleSpringApp();
		getConnectedClient().uploadApplication(appName, war.getCanonicalPath());
		return getConnectedClient().getApplication(appName);
	}

	private CloudApplication createAndUploadExplodedTestApp(String appName, File explodedDir, Staging staging)
			throws IOException {
		assertTrue("Expected exploded test app at " + explodedDir.getCanonicalPath(), explodedDir.exists());
		createTestApp(appName, null, staging);
		getConnectedClient().uploadApplication(appName, explodedDir.getCanonicalPath());
		return getConnectedClient().getApplication(appName);
	}

	private void createStandaloneRubyTestApp(String appName, List<String> uris, List<String> services) throws IOException {
		Staging staging = new Staging("standalone");
		staging.setRuntime("ruby19");
		staging.setCommand("ruby simple.rb");
		File file = SampleProjects.standaloneRuby();
		getConnectedClient().createApplication(appName, staging, 128, uris, services);
		getConnectedClient().uploadApplication(appName, file.getCanonicalPath());
	}

	private void createSpringApplication(String appName, List<String> serviceNames) {
        createSpringApplication(appName, serviceNames, null);
	}
	private void createSpringApplication(String appName, List<String> serviceNames, String buildpackUrl) {
		Staging staging =  new Staging("spring");
		staging.setRuntime("java");
		createTestApp(appName, serviceNames, staging, buildpackUrl);
	}

    private void createTestApp(String appName, List<String> serviceNames, Staging staging, String buildPackUrl) {
        List<String> uris = new ArrayList<String>();
        uris.add(computeAppUrl(appName));
        if (serviceNames != null) {
            for (String serviceName : serviceNames) {
                createMySqlService(serviceName);
            }
        }
        getConnectedClient().createApplication(appName, staging,
                getTestAppMemory(staging.getFramework()),
                uris, serviceNames, "paid", buildPackUrl);
    }


    private void createTestApp(String appName, List<String> serviceNames, Staging staging) {
        createTestApp(appName, serviceNames, staging, null);
	}

	private void createMySqlService(String serviceName) {
		List<ServiceConfiguration> serviceConfigurations = getConnectedClient().getServiceConfigurations();
		ServiceConfiguration databaseServiceConfiguration = null;
		for (ServiceConfiguration sc : serviceConfigurations) {
			if ((sc.getVendor() != null && sc.getVendor().equals("mysql")) ||
				(sc.getCloudServiceOffering() != null && sc.getCloudServiceOffering().getLabel().equals(getMysqlLabel()))) {
				databaseServiceConfiguration = sc;
				break;
			}
		}
		if (databaseServiceConfiguration == null) {
			throw new IllegalStateException("No ServiceConfiguration found for MySQL.");
		}
		CloudService service = new CloudService(CloudEntity.Meta.defaultMeta(), serviceName);
		if (getInfo().getCloudControllerMajorVersion() == CloudInfo.CC_MAJOR_VERSION.V1) {
			service.setTier("free");
			service.setType("database");
			service.setVersion(databaseServiceConfiguration.getVersion());
			service.setVendor(databaseServiceConfiguration.getVendor());
		} else {
			service.setProvider("core");
			service.setLabel(getMysqlLabel());
			service.setVersion(databaseServiceConfiguration.getVersion());
			service.setPlan(V2_SERVICE_TEST_MYSQL_PLAN);
		}
		getConnectedClient().createService(service);
	}

	protected String computeAppUrl(String appName) {
		String ccUrl = getApiUrl();
		int ix1 =  2 + ccUrl.indexOf("//");
		int ix2 = ccUrl.indexOf('.');
		return ccUrl.substring(0, ix1) + appName + ccUrl.substring(ix2);
	}

	private InstancesInfo getInstancesWithTimeout(CloudFoundryClient client, String appName) {
		long start = System.currentTimeMillis();
		while (true) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				// ignore
			}
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

	private void startApplicationWithTimeout(String appName) {
		for (int i = 0; i < 50; i++) {
			try {
				getConnectedClient().startApplication(appName);
				break;
			} catch (CloudFoundryException ex) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// ignore
				}
			}
		}
	}

}
