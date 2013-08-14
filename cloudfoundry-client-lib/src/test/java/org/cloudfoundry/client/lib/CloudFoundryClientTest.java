package org.cloudfoundry.client.lib;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.cloudfoundry.client.lib.domain.ApplicationStats;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudDomain;
import org.cloudfoundry.client.lib.domain.CloudEntity;
import org.cloudfoundry.client.lib.domain.CloudInfo;
import org.cloudfoundry.client.lib.domain.CloudOrganization;
import org.cloudfoundry.client.lib.domain.CloudRoute;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.CloudServiceOffering;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.cloudfoundry.client.lib.domain.CrashInfo;
import org.cloudfoundry.client.lib.domain.CrashesInfo;
import org.cloudfoundry.client.lib.domain.InstanceInfo;
import org.cloudfoundry.client.lib.domain.InstanceState;
import org.cloudfoundry.client.lib.domain.InstanceStats;
import org.cloudfoundry.client.lib.domain.InstancesInfo;
import org.cloudfoundry.client.lib.domain.Staging;
import org.cloudfoundry.client.lib.util.RestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Note that this integration tests rely on other methods working correctly, so these tests aren't
 * independent unit tests for all methods, they are intended to test the completeness of the functionality of
 * each API version implementation.
 *
 * @author Ramnivas Laddad
 * @author A.B.Srinivasan
 * @author Jennifer Hickey
 * @author Thomas Risberg
 */
public class CloudFoundryClientTest {

	private CloudFoundryClient connectedClient;

	// Pass -Dccng.target=http://api.cloudfoundry.com, vcap.me, or your own cloud -- must point to a v2 cloud controller
	private static final String CCNG_API_URL = System.getProperty("ccng.target", "http://ccng.cloudfoundry.com");

    private static final String CCNG_API_PROXY_HOST = System.getProperty("http.proxyHost", null);

    private static final int CCNG_API_PROXY_PORT = Integer.getInteger("http.proxyPort", 80);

	private static final String CCNG_USER_EMAIL = System.getProperty("ccng.email", "java-authenticatedClient-test-user@vmware.com");

	private static final String CCNG_USER_PASS = System.getProperty("ccng.passwd");

	private static final String CCNG_USER_ORG = System.getProperty("ccng.org", "gopivotal.com");

	private static final String CCNG_USER_SPACE = System.getProperty("ccng.space", "test");

	private static final String TEST_NAMESPACE = System.getProperty("vcap.test.namespace", defaultNamespace(CCNG_USER_EMAIL));

	private static final  String TEST_DOMAIN = System.getProperty("vcap.test.domain", defaultNamespace(CCNG_USER_EMAIL) + ".com");
	
	private static final boolean SILENT_TEST_TIMINGS = Boolean.getBoolean("silent.testTimings");
	
	private static String defaultDomainName = null;
    private HttpProxyConfiguration httpProxyConfiguration;

    private static final String SERVICE_TEST_MYSQL_PLAN = "spark";
    private static final int DEFAULT_MEMORY = 512; // MB

    private static boolean tearDownComplete = false;
    
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Rule
	public TestRule watcher = new TestWatcher() {
		private long startTime;
		
		@Override
		protected void starting(Description description) {
			if (!SILENT_TEST_TIMINGS) {			
				System.out.println("Starting test " + description.getMethodName());
			}
			startTime = System.currentTimeMillis();
		}
		
		@Override
		protected void finished(Description description) {
			if (!SILENT_TEST_TIMINGS) {
				System.out.println("Test " + description.getMethodName() + " took " + (System.currentTimeMillis() - startTime) + " ms");
			}
		}
	};	

	@BeforeClass
	public static void printTargetCloudInfo() {
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
        connectedClient = new CloudFoundryClient(new CloudCredentials(CCNG_USER_EMAIL, CCNG_USER_PASS), 
												new URL(CCNG_API_URL), CCNG_USER_ORG, CCNG_USER_SPACE, httpProxyConfiguration);
		connectedClient.login();
		defaultDomainName = getDefaultDomain(connectedClient.getDomainsForOrg()).getName();
		
		// Optimization to avoid redoing the work already done is tearDown()
		if (!tearDownComplete) {
			tearDown();
		}
		tearDownComplete = false;
		connectedClient.addDomain(TEST_DOMAIN);
		
		// connectedClient.registerRestLogListener(new RestLogger("CF_REST"));
	}

	@After
	public void tearDown() {
		// Clean after ourselves so that there are no leftover apps, services, domains, and routes
		connectedClient.deleteAllApplications();
		connectedClient.deleteAllServices();
		clearTestDomainAndRoutes();
		tearDownComplete = true;
	}

	@Test
	public void infoAvailable() throws Exception {
		CloudInfo info = connectedClient.getCloudInfo();
		assertNotNull(info.getName());
		assertNotNull(info.getSupport());
		assertNotNull(info.getBuild());
	}

	@Test
	public void spacesAvailable() throws Exception {
		List<CloudSpace> spaces = connectedClient.getSpaces();
		assertNotNull(spaces);
		assertTrue(spaces.size() > 0);
	}
	
	@Test
	public void orgsAvailable() throws Exception {
		List<CloudOrganization> orgs = connectedClient.getOrganizations();
		assertNotNull(orgs);
		assertTrue(orgs.size() > 0);
	}

	//
	// Basic Application tests
	//

	@Test
	public void createApplication() {
		List<String> uris = new ArrayList<String>();
		String appName = namespacedAppName("travel_test-0");
		uris.add(computeAppUrl(appName));
		Staging staging =  new Staging();
		connectedClient.createApplication(appName, staging, DEFAULT_MEMORY, uris, null);
		CloudApplication app = connectedClient.getApplication(appName);
		assertNotNull(app);
		assertEquals(appName, app.getName());
	}

	@Test
	public void getApplicationByName() {
		String appName = createSpringTravelApp("1", null);
		CloudApplication app = connectedClient.getApplication(appName);
		assertNotNull(app);
		assertEquals(appName, app.getName());
	}
	
	@Test
	public void getApplicationByGuid() {
		String appName = createSpringTravelApp("3", null);
		CloudApplication app = connectedClient.getApplication(appName);
		CloudApplication guidApp = connectedClient.getApplication(app.getMeta().getGuid());
		assertEquals(app.getName(), guidApp.getName());
	}

	@Test
	public void getApplicationNonExistent() {
		thrown.expect(CloudFoundryException.class);
		thrown.expect(hasProperty("statusCode", is(HttpStatus.NOT_FOUND)));
		thrown.expectMessage(containsString("Not Found"));
		String appName = namespacedAppName("non_existent");
		connectedClient.getApplication(appName);
	}

	@Test
	public void getApplications() {
		String appName = createSpringTravelApp("2", null);
		List<CloudApplication> apps = connectedClient.getApplications();
		assertEquals(1, apps.size());
		assertEquals(appName, apps.get(0).getName());
		assertNotNull(apps.get(0));
		assertNotNull(apps.get(0).getMeta());
		assertNotNull(apps.get(0).getMeta().getGuid());

		createSpringTravelApp("3", null);
		apps = connectedClient.getApplications();
		assertEquals(2, apps.size());
	}

	@Test
	public void deleteApplication() {
		String appName = createSpringTravelApp("4", null);
		assertEquals(1, connectedClient.getApplications().size());
		connectedClient.deleteApplication(appName);
		assertEquals(0, connectedClient.getApplications().size());
	}

    @Test
    public void uploadApplicationWithBuildPack() throws IOException {
        String buildpackUrl = "https://github.com/cloudfoundry/java-buildpack.git";
        String appName = createSpringTravelApp("upload1", null, buildpackUrl);

        File file = SampleProjects.springTravel();
        connectedClient.uploadApplication(appName, file.getCanonicalPath());

        CloudApplication app = connectedClient.getApplication(appName);
        assertNotNull(app);
        assertEquals(CloudApplication.AppState.STOPPED, app.getState());

        String url = computeAppUrlNoProtocol(appName);
        assertEquals(url, app.getUris().get(0));

        assertEquals(buildpackUrl, app.getStaging().getBuildpackUrl());
    }


    @Test
	public void startStopApplication() throws IOException {
		String appName = createSpringTravelApp("upload-start-stop", null);
		CloudApplication app = uploadSpringTravelApp(appName);
		assertNotNull(app);
		assertEquals(CloudApplication.AppState.STOPPED, app.getState());
		
		String url = computeAppUrlNoProtocol(appName);
		assertEquals(url, app.getUris().get(0));

		StartingInfo info = connectedClient.startApplication(appName);
		app = connectedClient.getApplication(appName);
		assertEquals(CloudApplication.AppState.STARTED, app.getState());
		assertNotNull(info);
		assertNotNull(info.getStagingFile());
		
		connectedClient.stopApplication(appName);
		app = connectedClient.getApplication(appName);
		assertEquals(CloudApplication.AppState.STOPPED, app.getState());
    }

	@Test
	public void paginationWorksForUris() throws IOException {
		String appName = namespacedAppName("page-url1");
		CloudApplication app = createAndUploadSimpleTestApp(appName);

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
	}

	//
	// App configuration tests
	//

	@Test
	public void setEnvironmentThroughList() throws IOException {
		String appName = createSpringTravelApp("env1", null);
		CloudApplication app = connectedClient.getApplication(appName);
		assertTrue(app.getEnv().isEmpty());

		connectedClient.updateApplicationEnv(appName, asList("foo=bar", "bar=baz"));
		app = connectedClient.getApplication(app.getName());
		assertEquals(new HashSet<String>(asList("foo=bar", "bar=baz")), new HashSet<String>(app.getEnv()));

		connectedClient.updateApplicationEnv(appName, asList("foo=baz", "baz=bong"));
		app = connectedClient.getApplication(app.getName());
		assertEquals(new HashSet<String>(asList("foo=baz", "baz=bong")), new HashSet<String>(app.getEnv()));

		connectedClient.updateApplicationEnv(appName, new ArrayList<String>());
		app = connectedClient.getApplication(app.getName());
		assertTrue(app.getEnv().isEmpty());
	}

	@Test
	public void setEnvironmentWithoutEquals() throws IOException {
		thrown.expect(IllegalArgumentException.class);
		String appName = createSpringTravelApp("env2", null);
		CloudApplication app = connectedClient.getApplication(appName);
		assertTrue(app.getEnv().isEmpty());
		connectedClient.updateApplicationEnv(appName, asList("foo:bar", "bar=baz"));
	}

	@Test
	public void setEnvironmentThroughMap() throws IOException {
		String appName = createSpringTravelApp("env3", null);
		CloudApplication app = connectedClient.getApplication(appName);
		assertTrue(app.getEnv().isEmpty());

		Map<String, String> env1 = new HashMap<String, String>();
		env1.put("foo", "bar");
		env1.put("bar", "baz");
		connectedClient.updateApplicationEnv(appName, env1);
		app = connectedClient.getApplication(app.getName());
		assertEquals(env1, app.getEnvAsMap());
		assertEquals(new HashSet<String>(asList("foo=bar", "bar=baz")), new HashSet<String>(app.getEnv()));

		Map<String, String> env2 = new HashMap<String, String>();
		env2.put("foo", "baz");
		env2.put("baz", "bong");
		connectedClient.updateApplicationEnv(appName, env2);
		app = connectedClient.getApplication(app.getName());
		assertEquals(env2, app.getEnvAsMap());
		assertEquals(new HashSet<String>(asList("foo=baz", "baz=bong")), new HashSet<String>(app.getEnv()));

		connectedClient.updateApplicationEnv(appName, new HashMap<String, String>());
		app = connectedClient.getApplication(app.getName());
		assertTrue(app.getEnv().isEmpty());
		assertTrue(app.getEnvAsMap().isEmpty());
	}

	@Test
	public void updateApplicationMemory() throws IOException {
		String appName = createSpringTravelApp("mem1", null);
		CloudApplication app = connectedClient.getApplication(appName);

		connectedClient.updateApplicationMemory(appName, 256);
		app = connectedClient.getApplication(appName);
		assertEquals(256, app.getMemory());
	}

	@Test
	public void updateApplicationInstances() throws Exception {
		String appName = createSpringTravelApp("inst1", null);
		CloudApplication app = connectedClient.getApplication(appName);

		assertEquals(1, app.getInstances());

		connectedClient.updateApplicationInstances(appName, 3);
		app = connectedClient.getApplication(appName);
		assertEquals(3, app.getInstances());

		connectedClient.updateApplicationInstances(appName, 1);
		app = connectedClient.getApplication(appName);
		assertEquals(1, app.getInstances());
	}

	@Test
	public void updateApplicationUris() throws IOException {
		String appName = namespacedAppName("url1");
		CloudApplication app = createAndUploadAndStartSimpleSpringApp(appName);

		List<String> originalUris = app.getUris();
		assertEquals(Collections.singletonList(computeAppUrlNoProtocol(appName)), originalUris);

		List<String> uris = new ArrayList<String>(app.getUris());
		uris.add(computeAppUrlNoProtocol(namespacedAppName("url2")));
		connectedClient.updateApplicationUris(appName, uris);
		app = connectedClient.getApplication(appName);
		List<String> newUris = app.getUris();
		assertNotNull(newUris);
		assertEquals(uris.size(), newUris.size());
		for (String uri : uris) {
			assertTrue(newUris.contains(uri));
		}
		connectedClient.updateApplicationUris(appName, originalUris);
		app = connectedClient.getApplication(appName);
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
		connectedClient.startApplication(appName);
		CloudApplication app = connectedClient.getApplication(appName);
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
		Staging staging = new Staging("node app.js", null);
		File file = SampleProjects.standaloneNode();
		connectedClient.createApplication(appName, staging, 64, uris, services);
		connectedClient.uploadApplication(appName, file.getCanonicalPath());
		connectedClient.startApplication(appName);
		CloudApplication app = connectedClient.getApplication(appName);
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
		connectedClient.startApplication(appName);
		CloudApplication app = connectedClient.getApplication(appName);
		assertNotNull(app);
		assertEquals(CloudApplication.AppState.STARTED, app.getState());
		assertEquals(uris, app.getUris());
		assertEquals("ruby simple.rb", app.getStaging().getCommand());
		connectedClient.stopApplication(appName);

		Staging newStaging = new Staging("ruby simple.rb test", app.getStaging().getBuildpackUrl());
		connectedClient.updateApplicationStaging(appName, newStaging);
		app = connectedClient.getApplication(appName);
		assertNotNull(app);
		assertEquals(uris, app.getUris());
		assertEquals("ruby simple.rb test", app.getStaging().getCommand());
	}

	@Test
	public void renameApplication() {
		String appName = createSpringTravelApp("5", null);
		CloudApplication app = connectedClient.getApplication(appName);
		assertNotNull(app);
		assertEquals(appName, app.getName());
		String newName = namespacedAppName("travel_test-6");
		connectedClient.rename(appName, newName);
		CloudApplication newApp = connectedClient.getApplication(newName);
		assertNotNull(newApp);
		assertEquals(newName, newApp.getName());
	}

	@Test
	public void createAndReCreateApplication() {
		String appName = createSpringTravelApp("A", null);
		assertEquals(1, connectedClient.getApplications().size());
		connectedClient.deleteApplication(appName);
		appName = createSpringTravelApp("A", null);
		assertEquals(1, connectedClient.getApplications().size());
		connectedClient.deleteApplication(appName);
	}

	@Test
	public void getApplicationsMatchGetApplication() {
		String appName = createSpringTravelApp("1", null);
		List<CloudApplication> apps = connectedClient.getApplications();
		assertEquals(1, apps.size());
		CloudApplication app = connectedClient.getApplication(appName);
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

		connectedClient.stopApplication(appName);
		InstancesInfo instInfo = connectedClient.getApplicationInstances(appName);
		assertEquals(0, instInfo.getInstances().size());
	}

	@Test
	@Ignore("Ignore until the Java buildpack detects app crashes upon OOM correctly")
	public void getCrashes() throws IOException, InterruptedException {
		String appName = namespacedAppName("crashes1");
		createAndUploadSimpleSpringApp(appName);
		connectedClient.updateApplicationEnv(appName, Collections.singletonMap("crash", "true"));
		connectedClient.startApplication(appName);

		boolean pass = getInstanceInfosWithTimeout(appName, 1, false);
		assertTrue("Couldn't get the right application state in 50 tries", pass);

		CrashesInfo crashes = connectedClient.getCrashes(appName);
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
		CloudApplication app = createAndUploadAndStartSimpleSpringApp(appName);
		connectedClient.startApplication(appName);
		assertEquals(1, app.getInstances());
		for (int i = 0; i < 100 && app.getRunningInstances() < 1; i++) {
			Thread.sleep(1000);
			app = connectedClient.getApplication(appName);
		}
		assertEquals(1, app.getRunningInstances());
		RestUtil restUtil = new RestUtil();
		RestTemplate rest = restUtil.createRestTemplate(null);
		String results = rest.getForObject("http://" + app.getUris().get(0), String.class);
		assertTrue(results.contains("Hello world!"));
	}

	@Test
	public void getApplicationStats() throws Exception {
		String appName = namespacedAppName("stats2");
		CloudApplication app = createAndUploadSimpleSpringApp(appName);
		connectedClient.updateApplicationInstances(appName, 3);
		connectedClient.startApplication(appName);
		app = connectedClient.getApplication(appName);
		
		assertEquals(CloudApplication.AppState.STARTED, app.getState());

		ApplicationStats stats = connectedClient.getApplicationStats(appName);

		assertNotNull(stats);
		assertNotNull(stats.getRecords());
		// TODO: Make this pattern reusable
		for (int i = 0; i < 10 && stats.getRecords().size() < 3; i++) {
			Thread.sleep(1000);
			stats = connectedClient.getApplicationStats(appName);
		}
		assertEquals(3, stats.getRecords().size());
		InstanceStats firstInstance = stats.getRecords().get(0);
		assertEquals("0", firstInstance.getId());
		for (int i = 0; i < 50 && firstInstance.getUsage() == null; i++) {
			Thread.sleep(1000);
			stats = connectedClient.getApplicationStats(appName);
			firstInstance = stats.getRecords().get(0);
		}
		assertNotNull(firstInstance.getUsage());

		// Allow more time deviations due to local clock being out of sync with cloud
		int timeTolerance = 300 * 1000; // 5 minutes
		assertTrue("Usage time should be very recent",
				Math.abs(System.currentTimeMillis() - firstInstance.getUsage().getTime().getTime()) < timeTolerance);
	}

	@Test
	public void getApplicationStatsStoppedApp() throws IOException {
		String appName = namespacedAppName("stats2");
		createAndUploadAndStartSimpleSpringApp(appName);
		connectedClient.stopApplication(appName);

		ApplicationStats stats = connectedClient.getApplicationStats(appName);
		assertEquals(Collections.emptyList(), stats.getRecords());
	}

	@Test
	public void uploadSinatraApp() throws IOException {
		String appName = namespacedAppName("env");
		ClassPathResource cpr = new ClassPathResource("apps/env/");
		File explodedDir = cpr.getFile();
		Staging staging = new Staging();
		createAndUploadExplodedTestApp(appName, explodedDir, staging);
		connectedClient.startApplication(appName);
		CloudApplication env = connectedClient.getApplication(appName);
		assertEquals(CloudApplication.AppState.STARTED, env.getState());
	}

	@Test
	public void uploadAppWithNonAsciiFileName() throws IOException {
		String appName = namespacedAppName("non-ascii-file-name");
		List<String> uris = new ArrayList<String>();
		uris.add(computeAppUrl(appName));

		File war = SampleProjects.nonAsciFileName();
		List<String> serviceNames = new ArrayList<String>();

		connectedClient.createApplication(appName, new Staging(),
				DEFAULT_MEMORY, uris, serviceNames);
		connectedClient.uploadApplication(appName, war.getCanonicalPath());

		CloudApplication app = connectedClient.getApplication(appName);
		assertNotNull(app);
		assertEquals(CloudApplication.AppState.STOPPED, app.getState());

		connectedClient.startApplication(appName);

		app = connectedClient.getApplication(appName);
		assertNotNull(app);
		assertEquals(CloudApplication.AppState.STARTED, app.getState());

		connectedClient.deleteApplication(appName);
	}

	@Test
	public void startExplodedApplication() throws IOException {
		String appName = namespacedAppName("exploded_app");
		createAndUploadExplodedSpringTestApp(appName);
		connectedClient.startApplication(appName);
		CloudApplication app = connectedClient.getApplication(appName);
		assertEquals(CloudApplication.AppState.STARTED, app.getState());
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
		Map<String, String> logs = connectedClient.getLogs(appName);
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
		connectedClient.updateApplicationEnv(appName, Collections.singletonMap("crash", "true"));
		connectedClient.startApplication(appName);

		boolean pass = getInstanceInfosWithTimeout(appName, 1, false);
		assertTrue("Couldn't get the right application state in 50 tries", pass);

		Map<String, String> logs = connectedClient.getCrashLogs(appName);
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
		doGetFile(connectedClient, appName);
	}

	
	//
	// Basic Services tests
	//

	@Test
	public void getServiceOfferings() {
		List<CloudServiceOffering> offerings = connectedClient.getServiceOfferings();

		assertNotNull(offerings);
		assertTrue(offerings.size() >= 2);

		CloudServiceOffering offering = null;
		for (CloudServiceOffering so : offerings) {
			if (so.getLabel().equals(getMysqlLabel())) {
				offering = so;
				break;
			}
		}
		assertNotNull(offering);
		assertEquals(getMysqlLabel(), offering.getLabel());
		assertNotNull(offering.getCloudServicePlans());
		assertTrue(offering.getCloudServicePlans().size() > 0);
	}

	@Test
	public void getCreateDeleteService() throws MalformedURLException {
		String serviceName = "mysql-test";
		createMySqlService(serviceName);

		CloudService service = connectedClient.getService(serviceName);
		assertNotNull(service);
		assertEquals(serviceName, service.getName());
		// Allow more time deviations due to local clock being out of sync with cloud
		int timeTolerance = 300 * 1000; // 5 minutes
		assertTrue("Creation time should be very recent",
				Math.abs(System.currentTimeMillis() - service.getMeta().getCreated().getTime()) < timeTolerance);
		
		connectedClient.deleteService(serviceName);
		List<CloudService> services = connectedClient.getServices();
		assertNotNull(services);
		assertEquals(0, services.size());
	}

	@Test
	public void getServices() {
		String serviceName = "mysql-test";
		createMySqlService(serviceName);
		createMySqlService("mysql-test2");

		List<CloudService> services = connectedClient.getServices();
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
		assertEquals(getMysqlLabel(), service.getLabel());
		assertEquals("cleardb", service.getProvider());
		assertEquals("n/a", service.getVersion());
		assertEquals(SERVICE_TEST_MYSQL_PLAN, service.getPlan());
	}



	//
	// Application and Services tests
	//

	@Test
	public void createApplicationWithService() throws IOException {
		String serviceName = "test_database";
		List<String> serviceNames= Collections.singletonList(serviceName);
		String appName = createSpringTravelApp("application-with-services", serviceNames);
		CloudApplication app = uploadSpringTravelApp(appName);
		app = connectedClient.getApplication(appName);
		assertNotNull(app);
		assertEquals(appName, app.getName());
		assertNotNull(app.getServices());
		assertEquals(1, app.getServices().size());
		assertEquals(serviceName, app.getServices().get(0));
	}

	@Test
	public void deleteServiceThatIsBoundToApp() throws MalformedURLException {
		String serviceName = "mysql-del-svc";
		List<String> serviceNames = new ArrayList<String>();
		serviceNames.add(serviceName);
		String appName = createSpringTravelApp("del-svc", serviceNames);

		CloudApplication app = connectedClient.getApplication(appName);
		assertNotNull(app.getServices());
		assertEquals(1, app.getServices().size());
		assertEquals(serviceName, app.getServices().get(0));

		connectedClient.deleteService(serviceName);
	}

	@Test
	public void updateApplicationService() throws IOException {
		String serviceName = "test_database";
		createMySqlService(serviceName);
		String appName = createSpringTravelApp("7", null);

		connectedClient.updateApplicationServices(appName, Collections.singletonList(serviceName));
		CloudApplication app = connectedClient.getApplication(appName);
		assertNotNull(app.getServices());
		assertTrue(app.getServices().size() > 0);
		assertEquals(serviceName, app.getServices().get(0));

		List<String> emptyList = Collections.emptyList();
		connectedClient.updateApplicationServices(appName, emptyList);
		app = connectedClient.getApplication(appName);
		assertNotNull(app.getServices());
		assertEquals(emptyList, app.getServices());
	}

	@Test
	public void bindAndUnbindService() throws IOException {
		String serviceName = "test_database";
		createMySqlService(serviceName);

		String appName = createSpringTravelApp("bind1", null);

		CloudApplication app = connectedClient.getApplication(appName);
		assertNotNull(app.getServices());
		assertTrue(app.getServices().isEmpty());

		connectedClient.bindService(appName, serviceName);

		app = connectedClient.getApplication(appName);
		assertNotNull(app.getServices());
		assertEquals(1, app.getServices().size());
		assertEquals(serviceName, app.getServices().get(0));

		connectedClient.unbindService(appName, serviceName);

		app = connectedClient.getApplication(appName);
		assertNotNull(app.getServices());
		assertTrue(app.getServices().isEmpty());
	}
	
	@Test
	public void manageDomainsAndRoutes() throws IOException {
		// Test that default domain is found
		List<CloudDomain> allDomains = connectedClient.getDomainsForOrg();
		CloudDomain defaultDomain = getDefaultDomain(allDomains);
		assertNotNull(getDefaultDomain(allDomains));

		// Test adding test domain - should be there for org and space
		connectedClient.addDomain(TEST_DOMAIN);
		allDomains = connectedClient.getDomainsForOrg();
		assertNotNull(getDefaultDomain(allDomains));
		assertNotNull(getDomainNamed(TEST_DOMAIN, allDomains));
		List<CloudDomain> spaceDomains = connectedClient.getDomains();
		assertNotNull(getDefaultDomain(spaceDomains));
		assertNotNull(getDomainNamed(TEST_DOMAIN, spaceDomains));

		// Test removing test domain from space
		connectedClient.removeDomain(TEST_DOMAIN);
		allDomains = connectedClient.getDomainsForOrg();
		assertNotNull(getDefaultDomain(allDomains));
		assertNotNull(getDomainNamed(TEST_DOMAIN, allDomains));
		spaceDomains = connectedClient.getDomains();
		assertNotNull(getDefaultDomain(spaceDomains));
		assertNull(getDomainNamed(TEST_DOMAIN, spaceDomains));

		// Test accessing/adding/deleting routes
		connectedClient.addDomain(TEST_DOMAIN);
		connectedClient.addRoute("my_route1", TEST_DOMAIN);
		connectedClient.addRoute("my_route2", TEST_DOMAIN);
		List<CloudRoute> routes = connectedClient.getRoutes(TEST_DOMAIN);
		assertNotNull(getRouteWithHost("my_route1", routes));
		assertNotNull(getRouteWithHost("my_route2", routes));
		connectedClient.deleteRoute("my_route2", TEST_DOMAIN);
		routes = connectedClient.getRoutes(TEST_DOMAIN);
		assertNotNull(getRouteWithHost("my_route1", routes));
		assertNull(getRouteWithHost("my_route2", routes));

		// Test that apps with route are counted
		String appName = namespacedAppName("my_route3");
		CloudApplication app = createAndUploadSimpleTestApp(appName);
		List<String> uris = app.getUris();
		uris.add("my_route3." + TEST_DOMAIN);
		connectedClient.updateApplicationUris(appName, uris);
		routes = connectedClient.getRoutes(TEST_DOMAIN);
		assertNotNull(getRouteWithHost("my_route1", routes));
		assertNotNull(getRouteWithHost("my_route3", routes));
		assertEquals(0, getRouteWithHost("my_route1", routes).getAppsUsingRoute());
		assertFalse(getRouteWithHost("my_route1", routes).inUse());
		assertEquals(1, getRouteWithHost("my_route3", routes).getAppsUsingRoute());
		assertTrue(getRouteWithHost("my_route3", routes).inUse());
		List<CloudRoute> defaultDomainRoutes = connectedClient.getRoutes(defaultDomain.getName());
		assertNotNull(getRouteWithHost(appName, defaultDomainRoutes));
		assertEquals(1, getRouteWithHost(appName, defaultDomainRoutes).getAppsUsingRoute());
		assertTrue(getRouteWithHost(appName, defaultDomainRoutes).inUse());

		// test that removing domain that has routes throws exception
		try {
			connectedClient.deleteDomain(TEST_DOMAIN);
			fail("should have thrown exception");
		}
		catch (IllegalStateException ex) {
			assertTrue(ex.getMessage().contains("in use"));
		}
	}



	//
	// Configuration/Metadata tests
	//

	@Test
	public void infoAvailableWithoutLoggingIn() throws Exception {
		CloudFoundryClient infoClient = new CloudFoundryClient(new URL(CCNG_API_URL));
		CloudInfo info = infoClient.getCloudInfo();
		assertNotNull(info.getName());
		assertNotNull(info.getSupport());
		assertNotNull(info.getBuild());
		assertTrue(info.getUser() == null);
	}

	@Test
	public void infoForUserAvailable() throws Exception {
		CloudInfo info = connectedClient.getCloudInfo();

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
	@Ignore("This test takes, by design, at least 10 minutes. Enable this only when dealing with authentication issues "
			+ "or a change in the client side OAuth implementation")
	public void dealingWithExpiredToken() throws Exception {
		// The current token expiration time is 10 minutes. If we can still make authenticated calls past that,
		// then the transparent token refresh scheme working as expected.
		for (int i = 0; i < 30; i++) {
			System.out.println("Elapsed time since the last login (at least) " + i/2 + " minutes");
			getServiceOfferings();
			Thread.sleep(30 * 1000);			
		}
	}
	
	@Test
	public void getRestLog() throws IOException {
		final List<RestLogEntry> log1 = new ArrayList<RestLogEntry>();
		final List<RestLogEntry> log2 = new ArrayList<RestLogEntry>();
		connectedClient.registerRestLogListener(new RestLogCallback() {
			public void onNewLogEntry(RestLogEntry logEntry) {
				log1.add(logEntry);
			}
		});
		RestLogCallback callback2 = new RestLogCallback() {
			public void onNewLogEntry(RestLogEntry logEntry) {
				log2.add(logEntry);
			}
		};
		connectedClient.registerRestLogListener(callback2);
		getApplications();
		connectedClient.deleteAllApplications();
		assertTrue(log1.size() > 0);
		assertEquals(log1, log2);
		connectedClient.unRegisterRestLogListener(callback2);
		getApplications();
		connectedClient.deleteAllApplications();
		assertTrue(log1.size() > log2.size());
	}
	
	@Test
	public void getStagingLogs() throws Exception {
		String appName = createSpringTravelApp("stagingLogs", null,
				"https://github.com/cloudfoundry/java-buildpack.git");

		File file = SampleProjects.springTravel();
		connectedClient.uploadApplication(appName, file.getCanonicalPath());

		StartingInfo startingInfo = null;
		String firstLine = null;
		int i = 0;
		do {
			startingInfo = connectedClient.startApplication(appName);

			if (startingInfo != null && startingInfo.getStagingFile() != null) {
				int offset = 0;
				firstLine = connectedClient
						.getStagingLogs(startingInfo, offset);
			}

			if (startingInfo != null && startingInfo.getStagingFile() != null
					&& firstLine != null) {
				break;
			} else {
				connectedClient.stopApplication(appName);
				Thread.sleep(10000);
			}
		} while (++i < 5);

		assertNotNull(startingInfo);
		assertNotNull(startingInfo.getStagingFile());
		assertNotNull(firstLine);
		assertTrue(firstLine.length() > 0);
	}

	//
	// Shared test methods
	//

	private boolean getInstanceInfosWithTimeout(String appName, int count, boolean shouldBeRunning) {
		if (count > 1) {
			connectedClient.updateApplicationInstances(appName, count);
			CloudApplication app = connectedClient.getApplication(appName);
			assertEquals(count, app.getInstances());
		}

		InstancesInfo instances = null;
		boolean pass = false;
		for (int i = 0; i < 50; i++) {
			try {
				instances = getInstancesWithTimeout(connectedClient, appName);
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

	private void doGetFile(CloudFoundryClient client, String appName) throws Exception {
		String appDir = "app";
		String fileName = appDir + "/webapps/ROOT/WEB-INF/web.xml";
		String emptyPropertiesfileName = appDir + "/webapps/ROOT/WEB-INF/classes/empty.properties";

		// File is often not available immediately after starting an app... so allow up to 60 seconds wait
		for (int i = 0; i < 60; i++) {
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
	// Helper methods
	//

	private CloudApplication createAndUploadSimpleTestApp(String name) throws IOException {
		createAndUploadSimpleSpringApp(name);
		return connectedClient.getApplication(name);
	}

	private String namespacedAppName(String basename) {
		return TEST_NAMESPACE + "-" + basename;
	}

	private static String defaultNamespace(String email) {
		return email.substring(0, email.indexOf('@')).replaceAll("\\.", "-").replaceAll("\\+", "-");
	}

	//
	// helper methods
	//

	private String createSpringTravelApp(String suffix, List<String> serviceNames) {
        return createSpringTravelApp(suffix, serviceNames, null);
	}
	private String createSpringTravelApp(String suffix, List<String> serviceNames, String buildpackUrl) {
		String appName = namespacedAppName("travel_test-" + suffix);
		createSpringApplication(appName, serviceNames, buildpackUrl);
		return appName;
	}

	private CloudApplication uploadSpringTravelApp(String appName) throws IOException {
		File file = SampleProjects.springTravel();
		connectedClient.uploadApplication(appName, file.getCanonicalPath());
		return connectedClient.getApplication(appName);
	}

	private CloudApplication createAndUploadExplodedSpringTestApp(String appName)
			throws IOException {
		File explodedDir = SampleProjects.springTravelUnpacked(temporaryFolder);
		assertTrue("Expected exploded test app at " + explodedDir.getCanonicalPath(), explodedDir.exists());
		createTestApp(appName, null, new Staging());
		connectedClient.uploadApplication(appName, explodedDir.getCanonicalPath());
		return connectedClient.getApplication(appName);
	}

	private CloudApplication createAndUploadAndStartSimpleSpringApp(String appName) throws IOException {
		createAndUploadSimpleSpringApp(appName);
		connectedClient.startApplication(appName);
		return connectedClient.getApplication(appName);
	}

	private CloudApplication createAndUploadSimpleSpringApp(String appName) throws IOException {
		createSpringApplication(appName, null);
		File war = SampleProjects.simpleSpringApp();
		connectedClient.uploadApplication(appName, war.getCanonicalPath());
		return connectedClient.getApplication(appName);
	}

	private CloudApplication createAndUploadExplodedTestApp(String appName, File explodedDir, Staging staging)
			throws IOException {
		assertTrue("Expected exploded test app at " + explodedDir.getCanonicalPath(), explodedDir.exists());
		createTestApp(appName, null, staging);
		connectedClient.uploadApplication(appName, explodedDir.getCanonicalPath());
		return connectedClient.getApplication(appName);
	}

	private void createStandaloneRubyTestApp(String appName, List<String> uris, List<String> services) throws IOException {
		Staging staging = new Staging("ruby simple.rb", null);
		File file = SampleProjects.standaloneRuby();
		connectedClient.createApplication(appName, staging, 128, uris, services);
		connectedClient.uploadApplication(appName, file.getCanonicalPath());
	}

	private void createSpringApplication(String appName, List<String> serviceNames) {
        createSpringApplication(appName, serviceNames, null);
	}

	private void createSpringApplication(String appName, List<String> serviceNames, String buildpackUrl) {
		createTestApp(appName, serviceNames, new Staging(null, buildpackUrl));
	}

    private void createTestApp(String appName, List<String> serviceNames, Staging staging) {
        List<String> uris = new ArrayList<String>();
        uris.add(computeAppUrl(appName));
        if (serviceNames != null) {
            for (String serviceName : serviceNames) {
                createMySqlService(serviceName);
            }
        }
        connectedClient.createApplication(appName, staging,
                DEFAULT_MEMORY,
                uris, serviceNames);
    }

	private void createMySqlService(String serviceName) {
		List<CloudServiceOffering> serviceOfferings = connectedClient.getServiceOfferings();
		CloudServiceOffering databaseServiceOffering = null;
		for (CloudServiceOffering so : serviceOfferings) {
			if (so.getLabel().equals(getMysqlLabel())) {
				databaseServiceOffering = so;
				break;
			}
		}
		if (databaseServiceOffering == null) {
			throw new IllegalStateException("No CloudServiceOffering found for MySQL.");
		}
		CloudService service = new CloudService(CloudEntity.Meta.defaultMeta(), serviceName);
		service.setProvider("core");
		service.setLabel(getMysqlLabel());
		service.setVersion(databaseServiceOffering.getVersion());
		service.setPlan(SERVICE_TEST_MYSQL_PLAN);
		connectedClient.createService(service);
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

	private void clearTestDomainAndRoutes() {
		List<CloudDomain> domains = connectedClient.getDomainsForOrg();
		for (CloudDomain domain : domains) {
			List<CloudRoute> routes = connectedClient.getRoutes(domain.getName());
			for (CloudRoute route : routes) {
				connectedClient.deleteRoute(route.getHost(), route.getDomain().getName());
			}
			if (!domain.getName().equals(defaultDomainName)) {
				connectedClient.deleteDomain(domain.getName());
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

	private String computeAppUrl(String appName) {
		return appName + "." + defaultDomainName;
	}

	private String computeAppUrlNoProtocol(String appName) {
		return computeAppUrl(appName);
	}
	
	private String getMysqlLabel() {
		return "cleardb";
	}
}
