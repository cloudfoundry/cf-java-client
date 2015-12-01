package org.cloudfoundry.client.lib;

import org.apache.commons.io.IOUtils;
import org.cloudfoundry.client.lib.domain.ApplicationLog;
import org.cloudfoundry.client.lib.domain.ApplicationStats;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudDomain;
import org.cloudfoundry.client.lib.domain.CloudEntity;
import org.cloudfoundry.client.lib.domain.CloudEvent;
import org.cloudfoundry.client.lib.domain.CloudInfo;
import org.cloudfoundry.client.lib.domain.CloudOrganization;
import org.cloudfoundry.client.lib.domain.CloudQuota;
import org.cloudfoundry.client.lib.domain.CloudRoute;
import org.cloudfoundry.client.lib.domain.CloudSecurityGroup;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.CloudServiceBinding;
import org.cloudfoundry.client.lib.domain.CloudServiceBroker;
import org.cloudfoundry.client.lib.domain.CloudServiceInstance;
import org.cloudfoundry.client.lib.domain.CloudServiceOffering;
import org.cloudfoundry.client.lib.domain.CloudServicePlan;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.cloudfoundry.client.lib.domain.CloudStack;
import org.cloudfoundry.client.lib.domain.CloudUser;
import org.cloudfoundry.client.lib.domain.CrashInfo;
import org.cloudfoundry.client.lib.domain.CrashesInfo;
import org.cloudfoundry.client.lib.domain.InstanceInfo;
import org.cloudfoundry.client.lib.domain.InstanceState;
import org.cloudfoundry.client.lib.domain.InstanceStats;
import org.cloudfoundry.client.lib.domain.InstancesInfo;
import org.cloudfoundry.client.lib.domain.SecurityGroupRule;
import org.cloudfoundry.client.lib.domain.Staging;
import org.cloudfoundry.client.lib.oauth2.OauthClient;
import org.cloudfoundry.client.lib.rest.CloudControllerClient;
import org.cloudfoundry.client.lib.rest.CloudControllerClientFactory;
import org.cloudfoundry.client.lib.util.RestUtil;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ConnectHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.jboss.byteman.contrib.bmunit.BMScript;
import org.jboss.byteman.contrib.bmunit.BMUnitRunner;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
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
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

/**
 * Note that this integration tests rely on other methods working correctly, so these tests aren't independent unit
 * tests for all methods, they are intended to test the completeness of the functionality of each API version
 * implementation.
 *
 * @author Ramnivas Laddad
 * @author A.B.Srinivasan
 * @author Jennifer Hickey
 * @author Thomas Risberg
 */
@RunWith(BMUnitRunner.class)
@BMScript(value = "trace", dir = "target/test-classes")
public class CloudFoundryClientTest {

    public static final int STARTUP_TIMEOUT = Integer.getInteger("ccng.startup.timeout", 60000);

    private static final String CCNG_API_PROXY_HOST = System.getProperty("http.proxyHost", null);

    private static final String CCNG_API_PROXY_PASSWD = System.getProperty("http.proxyPassword", null);

    private static final int CCNG_API_PROXY_PORT = Integer.getInteger("http.proxyPort", 80);

    private static final String CCNG_API_PROXY_USER = System.getProperty("http.proxyUsername", null);

    private static final boolean CCNG_API_SSL = Boolean.getBoolean("ccng.ssl");

    // Pass -Dccng.target=http://api.cloudfoundry.com, vcap.me, or your own cloud -- must point to a v2 cloud controller
    private static final String CCNG_API_URL = System.getProperty("ccng.target", "http://api.run.pivotal.io");

    private static final String CCNG_QUOTA_NAME_TEST = System.getProperty("ccng.quota", "test_quota");

    private static final String CCNG_SECURITY_GROUP_NAME_TEST = System.getProperty("ccng.securityGroup",
            "test_security_group");

    private static final String CCNG_USER_EMAIL = System.getProperty("ccng.email",
            "java-authenticatedClient-test-user@vmware.com");

    private static final boolean CCNG_USER_IS_ADMIN = Boolean.getBoolean("ccng.admin");

    private static final String CCNG_USER_ORG = System.getProperty("ccng.org", "gopivotal.com");

    private static final String CCNG_USER_PASS = System.getProperty("ccng.passwd");

    private static final String CCNG_USER_SPACE = System.getProperty("ccng.space", "test");

    private static final int DEFAULT_DISK = 1024; // MB

    private static final int DEFAULT_MEMORY = 512; // MB

    private static final String DEFAULT_STACK_NAME = "lucid64";

    private static final int FIVE_MINUTES = 300 * 1000;

    private static final String MYSQL_SERVICE_LABEL = System.getProperty("vcap.mysql.label", "p-mysql");

    private static final String MYSQL_SERVICE_PLAN = System.getProperty("vcap.mysql.plan", "100mb-dev");

    private static final boolean SILENT_TEST_TIMINGS = Boolean.getBoolean("silent.testTimings");

    private static final boolean SKIP_INJVM_PROXY = Boolean.getBoolean("http.skipInJvmProxy");

    private static final String TEST_DOMAIN = System.getProperty("vcap.test.domain", defaultNamespace
            (CCNG_USER_EMAIL) + ".com");

    private static final String TEST_NAMESPACE = System.getProperty("vcap.test.namespace", defaultNamespace
            (CCNG_USER_EMAIL));

    private static String defaultDomainName = null;

    private static HttpProxyConfiguration httpProxyConfiguration;

    private static int inJvmProxyPort;

    private static Server inJvmProxyServer;

    private static AtomicInteger nbInJvmProxyRcvReqs;

    private static boolean tearDownComplete = false;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TestRule watcher = new TestWatcher() {
        private long startTime;

        @Override
        protected void finished(Description description) {
            if (!SILENT_TEST_TIMINGS) {
                System.out.println("Test " + description.getMethodName() + " took " + (System.currentTimeMillis() -
                        startTime) + " ms");
            }
        }

        @Override
        protected void starting(Description description) {
            if (!SILENT_TEST_TIMINGS) {
                System.out.println("Starting test " + description.getMethodName());
            }
            startTime = System.currentTimeMillis();
        }
    };

    private CloudFoundryOperations connectedClient;

    @AfterClass
    public static void afterClass() throws Exception {
        if (inJvmProxyServer != null) {
            inJvmProxyServer.stop();
            nbInJvmProxyRcvReqs.set(0);
        }
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        System.out.println("Running tests on " + CCNG_API_URL + " on behalf of " + CCNG_USER_EMAIL);
        System.out.println("Using space " + CCNG_USER_SPACE + " of organization " + CCNG_USER_ORG);
        if (CCNG_USER_PASS == null) {
            fail("System property ccng.passwd must be specified, supply -Dccng.passwd=<password>");
        }

        if (CCNG_API_PROXY_HOST != null) {
            if (CCNG_API_PROXY_USER != null) {
                httpProxyConfiguration = new HttpProxyConfiguration(CCNG_API_PROXY_HOST, CCNG_API_PROXY_PORT, true,
                        CCNG_API_PROXY_USER, CCNG_API_PROXY_PASSWD);
            } else {
                httpProxyConfiguration = new HttpProxyConfiguration(CCNG_API_PROXY_HOST, CCNG_API_PROXY_PORT);
            }
        }
        if (!SKIP_INJVM_PROXY) {
            startInJvmProxy();
            httpProxyConfiguration = new HttpProxyConfiguration("127.0.0.1", inJvmProxyPort);
        }
    }

    private static String defaultNamespace(String email) {
        String s;
        if (email.contains("@")) {
            s = email.substring(0, email.indexOf('@'));
        } else {
            s = email;
        }
        return s.replaceAll("\\.", "-").replaceAll("\\+", "-");
    }

    private static int getNextAvailablePort(int initial) {
        int current = initial;
        while (!PortAvailability.available(current)) {
            current++;
            if (current - initial > 100) {
                throw new RuntimeException("did not find an available port from " + initial + " up to:" + current);
            }
        }
        return current;
    }

    /**
     * To test that the CF client is able to go through a proxy, we point the CC client to a broken url that can only be
     * resolved by going through an inJVM proxy which rewrites the URI. This method starts this inJvm proxy.
     *
     * @throws Exception
     */
    private static void startInJvmProxy() throws Exception {
        inJvmProxyPort = getNextAvailablePort(8080);
        inJvmProxyServer = new Server(new InetSocketAddress("127.0.0.1", inJvmProxyPort)); //forcing use of loopback
        // that will be used both for Httpclient proxy and SocketDestHelper
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMinThreads(1);
        inJvmProxyServer.setThreadPool(threadPool);

        HandlerCollection handlers = new HandlerCollection();
        inJvmProxyServer.setHandler(handlers);

        ServletHandler servletHandler = new ServletHandler();
        handlers.addHandler(servletHandler);
        nbInJvmProxyRcvReqs = new AtomicInteger();
        ChainedProxyServlet chainedProxyServlet = new ChainedProxyServlet(httpProxyConfiguration, nbInJvmProxyRcvReqs);
        servletHandler.addServletWithMapping(new ServletHolder(chainedProxyServlet), "/*");

        // Setup proxy handler to handle CONNECT methods
        ConnectHandler proxyHandler;
        proxyHandler = new ChainedProxyConnectHandler(httpProxyConfiguration, nbInJvmProxyRcvReqs);
        handlers.addHandler(proxyHandler);

        inJvmProxyServer.start();
    }

    @Test
    public void CRUDQuota() throws Exception {
        assumeTrue(CCNG_USER_IS_ADMIN);

        // create quota
        CloudQuota cloudQuota = new CloudQuota(null, CCNG_QUOTA_NAME_TEST);
        connectedClient.createQuota(cloudQuota);

        CloudQuota afterCreate = connectedClient.getQuotaByName(CCNG_QUOTA_NAME_TEST, true);
        assertNotNull(afterCreate);

        // change quota mem to 10240
        afterCreate.setMemoryLimit(10240);
        connectedClient.updateQuota(afterCreate, CCNG_QUOTA_NAME_TEST);
        CloudQuota afterUpdate = connectedClient.getQuotaByName(CCNG_QUOTA_NAME_TEST, true);
        assertEquals(10240, afterUpdate.getMemoryLimit());

        // delete the quota
        connectedClient.deleteQuota(CCNG_QUOTA_NAME_TEST);
        CloudQuota afterDelete = connectedClient.getQuotaByName(CCNG_QUOTA_NAME_TEST, false);
        assertNull(afterDelete);
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
        RestTemplate rest = restUtil.createRestTemplate(httpProxyConfiguration, false);
        String results = rest.getForObject("http://" + app.getUris().get(0), String.class);
        assertTrue(results.contains("Hello world!"));
    }

    @Test
    public void addAndDeleteDomain() {
        connectedClient.addDomain(TEST_DOMAIN);

        assertDomainInList(connectedClient.getPrivateDomains());
        assertDomainInList(connectedClient.getDomainsForOrg());

        assertDomainNotInList(connectedClient.getSharedDomains());

        connectedClient.deleteDomain(TEST_DOMAIN);

        assertDomainNotInList(connectedClient.getPrivateDomains());
        assertDomainNotInList(connectedClient.getDomainsForOrg());
    }

    @Test
    public void addAndDeleteRoute() {
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

        // test that removing domain that has routes throws exception
        try {
            connectedClient.deleteDomain(TEST_DOMAIN);
            fail("should have thrown exception");
        } catch (IllegalStateException ex) {
            assertTrue(ex.getMessage().contains("in use"));
        }
    }

    @Test
    public void appEventsAvailable() throws Exception {
        String appName = createSpringTravelApp("appEvents");
        List<CloudEvent> events = connectedClient.getApplicationEvents(appName);
        assertEvents(events);
        assertEventTimestamps(events);
    }

    @Test
    public void appsWithRoutesAreCounted() throws IOException {
        String appName = namespacedAppName("my_route3");
        CloudApplication app = createAndUploadSimpleTestApp(appName);
        List<String> uris = app.getUris();
        uris.add("my_route3." + TEST_DOMAIN);
        connectedClient.addDomain(TEST_DOMAIN);
        connectedClient.updateApplicationUris(appName, uris);

        List<CloudRoute> routes = connectedClient.getRoutes(TEST_DOMAIN);
        assertNotNull(getRouteWithHost("my_route3", routes));
        assertEquals(1, getRouteWithHost("my_route3", routes).getAppsUsingRoute());
        assertTrue(getRouteWithHost("my_route3", routes).inUse());

        List<CloudRoute> defaultDomainRoutes = connectedClient.getRoutes(defaultDomainName);
        assertNotNull(getRouteWithHost(appName, defaultDomainRoutes));
        assertEquals(1, getRouteWithHost(appName, defaultDomainRoutes).getAppsUsingRoute());
        assertTrue(getRouteWithHost(appName, defaultDomainRoutes).inUse());
    }

    //
    // Basic Event Tests
    //

    @Test
    public void assignAllUserRolesInSpaceWithOrgToCurrentUser() {
        String orgName = CCNG_USER_ORG;
        String spaceName = "assignAllUserRolesInSpaceWithOrgToCurrentUser";
        connectedClient.createSpace(spaceName);

        Map<String, CloudUser> orgUsers = connectedClient.getOrganizationUsers(orgName);
        String username = CCNG_USER_EMAIL;
        CloudUser user = orgUsers.get(username);
        assertNotNull("Retrieved user should not be null", user);
        String userGuid = user.getMeta().getGuid().toString();

        List<UUID> spaceManagers = connectedClient.getSpaceManagers(orgName, spaceName);
        assertEquals("Space should have no manager when created", 0, spaceManagers.size());
        connectedClient.associateManagerWithSpace(orgName, spaceName, userGuid);
        spaceManagers = connectedClient.getSpaceManagers(orgName, spaceName);
        assertEquals("Space should have one manager", 1, spaceManagers.size());

        List<UUID> spaceDevelopers = connectedClient.getSpaceDevelopers(orgName, spaceName);
        assertEquals("Space should have no developer when created", 0, spaceDevelopers.size());
        connectedClient.associateDeveloperWithSpace(orgName, spaceName, userGuid);
        spaceDevelopers = connectedClient.getSpaceDevelopers(orgName, spaceName);
        assertEquals("Space should have one developer", 1, spaceDevelopers.size());

        List<UUID> spaceAuditors = connectedClient.getSpaceAuditors(orgName, spaceName);
        assertEquals("Space should have no auditor when created", 0, spaceAuditors.size());
        connectedClient.associateAuditorWithSpace(orgName, spaceName, userGuid);
        spaceAuditors = connectedClient.getSpaceAuditors(orgName, spaceName);
        assertEquals("Space should have one auditor ", 1, spaceAuditors.size());

        connectedClient.deleteSpace(spaceName);
        CloudSpace deletedSpace = connectedClient.getSpace(spaceName);
        assertNull("Space '" + spaceName + "' should not exist after deletion", deletedSpace);
    }

    @Test
    public void assignDefaultUserRolesInSpace() {
        String spaceName = "assignDefaultUserRolesInSpace";
        connectedClient.createSpace(spaceName);

        List<UUID> spaceManagers = connectedClient.getSpaceManagers(spaceName);
        assertEquals("Space should have no manager when created", 0, spaceManagers.size());
        connectedClient.associateManagerWithSpace(spaceName);
        spaceManagers = connectedClient.getSpaceManagers(spaceName);
        assertEquals("Space should have one manager", 1, spaceManagers.size());

        List<UUID> spaceDevelopers = connectedClient.getSpaceDevelopers(spaceName);
        assertEquals("Space should have no developer when created", 0, spaceDevelopers.size());
        connectedClient.associateDeveloperWithSpace(spaceName);
        spaceDevelopers = connectedClient.getSpaceDevelopers(spaceName);
        assertEquals("Space should have one developer", 1, spaceDevelopers.size());

        List<UUID> spaceAuditors = connectedClient.getSpaceAuditors(spaceName);
        assertEquals("Space should have no auditor when created", 0, spaceAuditors.size());
        connectedClient.associateAuditorWithSpace(spaceName);
        spaceAuditors = connectedClient.getSpaceAuditors(spaceName);
        assertEquals("Space should have one auditor ", 1, spaceAuditors.size());

        connectedClient.deleteSpace(spaceName);
        CloudSpace deletedSpace = connectedClient.getSpace(spaceName);
        assertNull("Space '" + spaceName + "' should not exist after deletion", deletedSpace);
    }

    @Test(expected = IllegalArgumentException.class)
    public void attemptingToDeleteANonExistentSecurityGroupThrowsAnIllegalArgumentException() {
        assumeTrue(CCNG_USER_IS_ADMIN);

        connectedClient.deleteSecurityGroup(randomSecurityGroupName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void attemptingToUpdateANonExistentSecurityGroupThrowsAnIllegalArgumentException() throws
            FileNotFoundException {
        assumeTrue(CCNG_USER_IS_ADMIN);

        connectedClient.updateSecurityGroup(randomSecurityGroupName(), new FileInputStream(new File
                ("src/test/resources/security-groups/test-rules-2.json")));
    }

    //
    // Basic Application tests
    //

    @Test
    public void bindAndUnbindService() throws IOException {
        String serviceName = "test_database";
        createMySqlService(serviceName);

        String appName = createSpringTravelApp("bind1");

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
    public void bindingAndUnbindingSecurityGroupToDefaultRunningSet() throws FileNotFoundException {
        assumeTrue(CCNG_USER_IS_ADMIN);

        // Given
        assertFalse(containsSecurityGroupNamed(connectedClient.getRunningSecurityGroups(),
                CCNG_SECURITY_GROUP_NAME_TEST));
        connectedClient.createSecurityGroup(CCNG_SECURITY_GROUP_NAME_TEST, new FileInputStream(new File
                ("src/test/resources/security-groups/test-rules-2.json")));

        // When
        connectedClient.bindRunningSecurityGroup(CCNG_SECURITY_GROUP_NAME_TEST);

        // Then
        assertTrue(containsSecurityGroupNamed(connectedClient.getRunningSecurityGroups(),
                CCNG_SECURITY_GROUP_NAME_TEST));

        // When
        connectedClient.unbindRunningSecurityGroup(CCNG_SECURITY_GROUP_NAME_TEST);

        // Then
        assertFalse(containsSecurityGroupNamed(connectedClient.getRunningSecurityGroups(),
                CCNG_SECURITY_GROUP_NAME_TEST));

        // Cleanup
        connectedClient.deleteSecurityGroup(CCNG_SECURITY_GROUP_NAME_TEST);
    }

    @Test
    public void bindingAndUnbindingSecurityGroupToDefaultStagingSet() throws FileNotFoundException {
        assumeTrue(CCNG_USER_IS_ADMIN);

        // Given
        assertFalse(containsSecurityGroupNamed(connectedClient.getStagingSecurityGroups(),
                CCNG_SECURITY_GROUP_NAME_TEST));
        connectedClient.createSecurityGroup(CCNG_SECURITY_GROUP_NAME_TEST, new FileInputStream(new File
                ("src/test/resources/security-groups/test-rules-2.json")));

        // When
        connectedClient.bindStagingSecurityGroup(CCNG_SECURITY_GROUP_NAME_TEST);

        // Then
        assertTrue(containsSecurityGroupNamed(connectedClient.getStagingSecurityGroups(),
                CCNG_SECURITY_GROUP_NAME_TEST));

        // When
        connectedClient.unbindStagingSecurityGroup(CCNG_SECURITY_GROUP_NAME_TEST);

        // Then
        assertFalse(containsSecurityGroupNamed(connectedClient.getStagingSecurityGroups(),
                CCNG_SECURITY_GROUP_NAME_TEST));

        // Cleanup
        connectedClient.deleteSecurityGroup(CCNG_SECURITY_GROUP_NAME_TEST);
    }

    @Test
    public void bindingAndUnbindingSecurityGroupToSpaces() throws FileNotFoundException {
        assumeTrue(CCNG_USER_IS_ADMIN);

        // Given
        connectedClient.createSecurityGroup(CCNG_SECURITY_GROUP_NAME_TEST, new FileInputStream(new File
                ("src/test/resources/security-groups/test-rules-2.json")));

        // When
        connectedClient.bindSecurityGroup(CCNG_USER_ORG, CCNG_USER_SPACE, CCNG_SECURITY_GROUP_NAME_TEST);
        // Then
        assertTrue(isSpaceBoundToSecurityGroup(CCNG_USER_SPACE, CCNG_SECURITY_GROUP_NAME_TEST));

        // When
        connectedClient.unbindSecurityGroup(CCNG_USER_ORG, CCNG_USER_SPACE, CCNG_SECURITY_GROUP_NAME_TEST);
        //Then
        assertFalse(isSpaceBoundToSecurityGroup(CCNG_USER_SPACE, CCNG_SECURITY_GROUP_NAME_TEST));

        // Cleanup
        connectedClient.deleteSecurityGroup(CCNG_SECURITY_GROUP_NAME_TEST);
    }

    /**
     * Self tests that the assert mechanisms with jetty and byteman are properly working. If debugging is needed
     * consider enabling one or more of the following system properties -Dorg.jboss.byteman.verbose=true
     * -Dorg.jboss.byteman.debug=true -Dorg.jboss.byteman.rule.debug=true -Dorg.eclipse.jetty.util.log.class=org
     * .eclipse.jetty.util.log.StdErrLog -Dorg.eclipse.jetty.LEVEL=INFO -Dorg.eclipse.jetty.server.LEVEL=INFO
     * -Dorg.eclipse.jetty.server.handler .ConnectHandler=DEBUG Documentation on byteman at
     * http://downloads.jboss.org/byteman/2.1.3/ProgrammersGuideSinglePage.2.1.3.1.html
     */
    @Test
    public void checkByteManrulesAndInJvmProxyAssertMechanisms() {
        if (SKIP_INJVM_PROXY) {
            return; //inJvm Proxy test skipped.
        }
        assertTrue(SocketDestHelper.isSocketRestrictionFlagActive());

        RestUtil restUtil = new RestUtil();
        RestTemplate restTemplateNoProxy = restUtil.createRestTemplate(null, CCNG_API_SSL);

        // When called directly without a proxy, expect an exception to be thrown due to byteman rules
        assertNetworkCallFails(restTemplateNoProxy, new HttpComponentsClientHttpRequestFactory());
        // Repeat that with different request factory used in the code as this exercises different byteman rules
        assertNetworkCallFails(restTemplateNoProxy, new SimpleClientHttpRequestFactory());
        // And with the actual one used by RestUtil, without a proxy configured
        assertNetworkCallFails(restTemplateNoProxy, restUtil.createRequestFactory(null, CCNG_API_SSL));

        // Test with the in-JVM proxy configured
        HttpProxyConfiguration localProxy = new HttpProxyConfiguration("127.0.0.1", inJvmProxyPort);
        RestTemplate restTemplate = restUtil.createRestTemplate(localProxy, CCNG_API_SSL);

        restTemplate.execute(CCNG_API_URL + "/info", HttpMethod.GET, null, null);

        // then executes fine, and the jetty proxy indeed received one request
        assertEquals("expected network calls to make it through the inJvmProxy.", 1, nbInJvmProxyRcvReqs.get());
        nbInJvmProxyRcvReqs.set(0); //reset for next test

        assertTrue(SocketDestHelper.isActivated());
        assertFalse("expected some installed rules, got:" + SocketDestHelper.getInstalledRules(), SocketDestHelper
                .getInstalledRules().isEmpty());
    }

    @Test
    public void createAndReCreateApplication() {
        String appName = createSpringTravelApp("A");
        assertEquals(1, connectedClient.getApplications().size());
        connectedClient.deleteApplication(appName);
        appName = createSpringTravelApp("A");
        assertEquals(1, connectedClient.getApplications().size());
        connectedClient.deleteApplication(appName);
    }

    @Test
    public void createApplication() {
        String appName = namespacedAppName("travel_test-0");
        List<String> uris = Collections.singletonList(computeAppUrl(appName));
        Staging staging = new Staging();
        connectedClient.createApplication(appName, staging, DEFAULT_MEMORY, uris, null);
        CloudApplication app = connectedClient.getApplication(appName);
        assertNotNull(app);
        assertEquals(appName, app.getName());

        assertNotNull(app.getMeta().getGuid());

        final Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        final Calendar createdDate = Calendar.getInstance();
        createdDate.setTime(app.getMeta().getCreated());

        assertEquals(now.get(Calendar.DATE), createdDate.get(Calendar.DATE));
    }

    @Test
    public void createApplicationWithBuildPack() throws IOException {
        String buildpackUrl = "https://github.com/cloudfoundry/java-buildpack.git";
        String appName = namespacedAppName("buildpack");
        createSpringApplication(appName, buildpackUrl);

        CloudApplication app = connectedClient.getApplication(appName);
        assertNotNull(app);
        assertEquals(CloudApplication.AppState.STOPPED, app.getState());

        assertEquals(buildpackUrl, app.getStaging().getBuildpackUrl());
        assertNull(app.getStaging().getDetectedBuildpack());
    }

    @Test
    public void createApplicationWithDetectedBuildpack() throws Exception {
        String appName = createSpringTravelApp("detectedBuildpack");

        File file = SampleProjects.springTravel();
        connectedClient.uploadApplication(appName, file.getCanonicalPath());
        connectedClient.startApplication(appName);

        ensureApplicationRunning(appName);

        CloudApplication app = connectedClient.getApplication(appName);
        Staging staging = app.getStaging();
        assertNotNull(staging.getDetectedBuildpack());
    }

    @Test
    public void createApplicationWithDomainOnly() {
        String appName = namespacedAppName("travel_test-tld");

        connectedClient.addDomain(TEST_DOMAIN);
        List<String> uris = Collections.singletonList(TEST_DOMAIN);

        Staging staging = new Staging();
        connectedClient.createApplication(appName, staging, DEFAULT_MEMORY, uris, null);
        CloudApplication app = connectedClient.getApplication(appName);
        assertNotNull(app);
        assertEquals(appName, app.getName());

        List<String> actualUris = app.getUris();
        assertTrue(actualUris.size() == 1);
        assertEquals(TEST_DOMAIN, actualUris.get(0));
    }

    @Test
    public void createApplicationWithHealthCheckTimeout() throws IOException {
        String appName = namespacedAppName("health_check");
        createSpringApplication(appName, null, 2);

        CloudApplication app = connectedClient.getApplication(appName);
        assertNotNull(app);
        assertEquals(CloudApplication.AppState.STOPPED, app.getState());

        assertEquals(2, app.getStaging().getHealthCheckTimeout().intValue());
    }

    @Test
    public void createApplicationWithService() throws IOException {
        String serviceName = "test_database";
        String appName = createSpringTravelApp("application-with-services", Collections.singletonList(serviceName));
        uploadSpringTravelApp(appName);
        CloudApplication app = connectedClient.getApplication(appName);
        assertNotNull(app);
        assertEquals(appName, app.getName());
        assertNotNull(app.getServices());
        assertEquals(1, app.getServices().size());
        assertEquals(serviceName, app.getServices().get(0));
    }

    @Test
    public void createApplicationWithStack() throws IOException {
        String appName = namespacedAppName("stack");
        createSpringApplication(appName, DEFAULT_STACK_NAME, null);

        CloudApplication app = connectedClient.getApplication(appName);
        assertNotNull(app);
        assertEquals(CloudApplication.AppState.STOPPED, app.getState());

        assertEquals(DEFAULT_STACK_NAME, app.getStaging().getStack());
    }

    @Test
    public void createGetAndDeleteSpaceOnCurrentOrg() throws Exception {
        String spaceName = "dummy space";
        CloudSpace newSpace = connectedClient.getSpace(spaceName);
        assertNull("Space '" + spaceName + "' should not exist before creation", newSpace);
        connectedClient.createSpace(spaceName);
        newSpace = connectedClient.getSpace(spaceName);
        assertNotNull("newSpace should not be null", newSpace);
        assertEquals(spaceName, newSpace.getName());
        boolean foundSpaceInCurrentOrg = false;
        for (CloudSpace aSpace : connectedClient.getSpaces()) {
            if (spaceName.equals(aSpace.getName())) {
                foundSpaceInCurrentOrg = true;
            }
        }
        assertTrue(foundSpaceInCurrentOrg);
        connectedClient.deleteSpace(spaceName);
        CloudSpace deletedSpace = connectedClient.getSpace(spaceName);
        assertNull("Space '" + spaceName + "' should not exist after deletion", deletedSpace);

    }

    @Test
    public void crudSecurityGroups() throws Exception {
        assumeTrue(CCNG_USER_IS_ADMIN);

        List<SecurityGroupRule> rules = new ArrayList<SecurityGroupRule>();
        SecurityGroupRule rule = new SecurityGroupRule("tcp", "80, 443", "205.158.11.29");
        rules.add(rule);
        rule = new SecurityGroupRule("all", null, "0.0.0.0-255.255.255.255");
        rules.add(rule);
        rule = new SecurityGroupRule("icmp", null, "0.0.0.0/0", true, 0, 1);
        rules.add(rule);
        CloudSecurityGroup securityGroup = new CloudSecurityGroup(CCNG_SECURITY_GROUP_NAME_TEST, rules);

        // Create
        connectedClient.createSecurityGroup(securityGroup);

        // Verify created
        securityGroup = connectedClient.getSecurityGroup(CCNG_SECURITY_GROUP_NAME_TEST);
        assertNotNull(securityGroup);
        assertThat(securityGroup.getRules().size(), is(3));
        assertRulesMatchTestData(securityGroup);

        // Update group
        rules = new ArrayList<SecurityGroupRule>();
        rule = new SecurityGroupRule("all", null, "0.0.0.0-255.255.255.255");
        rules.add(rule);
        securityGroup = new CloudSecurityGroup(CCNG_SECURITY_GROUP_NAME_TEST, rules);
        connectedClient.updateSecurityGroup(securityGroup);

        // Verify update
        securityGroup = connectedClient.getSecurityGroup(CCNG_SECURITY_GROUP_NAME_TEST);
        assertThat(securityGroup.getRules().size(), is(1));

        // Delete group
        connectedClient.deleteSecurityGroup(CCNG_SECURITY_GROUP_NAME_TEST);
        // Verify deleted
        securityGroup = connectedClient.getSecurityGroup(CCNG_SECURITY_GROUP_NAME_TEST);
        assertNull(securityGroup);
    }

    @Test
    public void defaultDomainFound() throws Exception {
        assertNotNull(connectedClient.getDefaultDomain());
    }

    @Test
    public void deleteApplication() {
        String appName = createSpringTravelApp("4");
        assertEquals(1, connectedClient.getApplications().size());
        connectedClient.deleteApplication(appName);
        assertEquals(0, connectedClient.getApplications().size());
    }

    //
    // App configuration tests
    //

    @Test
    public void deleteOrphanedRoutes() {
        connectedClient.addDomain(TEST_DOMAIN);
        connectedClient.addRoute("unbound_route", TEST_DOMAIN);

        List<CloudRoute> routes = connectedClient.getRoutes(TEST_DOMAIN);
        CloudRoute unboundRoute = getRouteWithHost("unbound_route", routes);
        assertNotNull(unboundRoute);
        assertEquals(0, unboundRoute.getAppsUsingRoute());

        List<CloudRoute> deletedRoutes = connectedClient.deleteOrphanedRoutes();
        assertNull(getRouteWithHost("unbound_route", connectedClient.getRoutes(TEST_DOMAIN)));

        assertTrue(deletedRoutes.size() > 0);
        boolean found = false;
        for (CloudRoute route : deletedRoutes) {
            if (route.getHost().equals("unbound_route")) {
                found = true;
            }
        }
        assertTrue(found);
    }

    @Test
    public void deleteServiceThatIsBoundToApp() throws MalformedURLException {
        String serviceName = "mysql-del-svc";
        String appName = createSpringTravelApp("del-svc", Collections.singletonList(serviceName));

        CloudApplication app = connectedClient.getApplication(appName);
        assertNotNull(app.getServices());
        assertEquals(1, app.getServices().size());
        assertEquals(serviceName, app.getServices().get(0));

        connectedClient.deleteService(serviceName);
    }

    @Test
    public void eventsAvailable() throws Exception {
        List<CloudEvent> events = connectedClient.getEvents();
        assertEvents(events);
    }

    @Test
    public void getApplicationByGuid() {
        String appName = createSpringTravelApp("3");
        CloudApplication app = connectedClient.getApplication(appName);
        CloudApplication guidApp = connectedClient.getApplication(app.getMeta().getGuid());
        assertEquals(app.getName(), guidApp.getName());
    }

    @Test
    public void getApplicationByName() {
        final String serviceName = "test_database";
        String appName = createSpringTravelApp("1", Collections.singletonList(serviceName));
        CloudApplication app = connectedClient.getApplication(appName);
        assertNotNull(app);
        assertEquals(appName, app.getName());

        assertEquals(1, app.getServices().size());
        assertEquals(serviceName, app.getServices().get(0));
        assertEquals(CCNG_USER_SPACE, app.getSpace().getName());

        assertEquals(1, app.getInstances());
        assertEquals(DEFAULT_MEMORY, app.getMemory());
        assertEquals(DEFAULT_DISK, app.getDiskQuota());

        assertNull(app.getStaging().getCommand());
        assertNull(app.getStaging().getBuildpackUrl());
        assertNull(app.getStaging().getHealthCheckTimeout());
    }

    @Test
    public void getApplicationEnvironmentByGuid() {
        String appName = namespacedAppName("simple-app");
        List<String> uris = Collections.singletonList(computeAppUrl(appName));
        Staging staging = new Staging();
        connectedClient.createApplication(appName, staging, DEFAULT_MEMORY, uris, null);
        connectedClient.updateApplicationEnv(appName, Collections.singletonMap("testKey", "testValue"));
        CloudApplication app = connectedClient.getApplication(appName);
        Map<String, Object> env = connectedClient.getApplicationEnvironment(app.getMeta().getGuid());
        assertAppEnvironment(env);
    }

    @Test
    public void getApplicationEnvironmentByName() {
        String appName = namespacedAppName("simple-app");
        List<String> uris = Collections.singletonList(computeAppUrl(appName));
        Staging staging = new Staging();
        connectedClient.createApplication(appName, staging, DEFAULT_MEMORY, uris, null);
        connectedClient.updateApplicationEnv(appName, Collections.singletonMap("testKey", "testValue"));
        Map<String, Object> env = connectedClient.getApplicationEnvironment(appName);
        assertAppEnvironment(env);
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
        assertNull(instInfo);
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
    public void getApplicationStats() throws Exception {
        final int instanceCount = 3;
        String appName = namespacedAppName("stats2");
        createAndUploadSimpleSpringApp(appName);
        connectedClient.updateApplicationInstances(appName, instanceCount);
        connectedClient.startApplication(appName);
        CloudApplication app = connectedClient.getApplication(appName);

        assertEquals(CloudApplication.AppState.STARTED, app.getState());

        waitForStatsAvailable(appName, instanceCount);

        ApplicationStats stats = connectedClient.getApplicationStats(appName);
        assertNotNull(stats);
        assertNotNull(stats.getRecords());
        assertEquals(instanceCount, stats.getRecords().size());

        for (InstanceStats instanceStats : stats.getRecords()) {
            assertNotNull(instanceStats.getUris());
            assertNotNull(instanceStats.getHost());
            assertTrue(instanceStats.getPort() > 0);
            assertTrue(instanceStats.getDiskQuota() > 0);
            assertTrue(instanceStats.getMemQuota() > 0);
            assertTrue(instanceStats.getFdsQuota() > 0);
            assertTrue(instanceStats.getUptime() > 0);

            InstanceStats.Usage usage = instanceStats.getUsage();
            assertNotNull(usage);
            assertTrue(usage.getDisk() > 0);
            assertTrue(usage.getMem() > 0);

            assertTimeWithinRange("Usage time should be very recent", usage.getTime().getTime(), FIVE_MINUTES);
        }
    }


    //
    // Advanced Application tests
    //

    @Test
    public void getApplicationStatsStoppedApp() throws IOException {
        String appName = namespacedAppName("stats2");
        createAndUploadAndStartSimpleSpringApp(appName);
        connectedClient.stopApplication(appName);

        ApplicationStats stats = connectedClient.getApplicationStats(appName);
        assertTrue(stats.getRecords().isEmpty());
    }

    @Test
    public void getApplications() {
        final String serviceName = "test_database";
        String appName = createSpringTravelApp("2", Collections.singletonList(serviceName));
        List<CloudApplication> apps = connectedClient.getApplications();
        assertEquals(1, apps.size());

        CloudApplication app = apps.get(0);
        assertEquals(appName, app.getName());
        assertNotNull(app.getMeta());
        assertNotNull(app.getMeta().getGuid());

        assertEquals(1, app.getServices().size());
        assertEquals(serviceName, app.getServices().get(0));

        createSpringTravelApp("3");
        apps = connectedClient.getApplications();
        assertEquals(2, apps.size());
    }

    @Test
    public void getApplicationsMatchGetApplication() {
        String appName = createSpringTravelApp("1");
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
    public void getCreateDeleteService() throws MalformedURLException {
        String serviceName = "mysql-test";
        createMySqlService(serviceName);

        CloudService service = connectedClient.getService(serviceName);
        assertNotNull(service);
        assertEquals(serviceName, service.getName());
        assertTimeWithinRange("Creation time should be very recent",
                service.getMeta().getCreated().getTime(), FIVE_MINUTES);

        connectedClient.deleteService(serviceName);

        List<CloudService> services = connectedClient.getServices();
        assertNotNull(services);
        assertEquals(0, services.size());
    }

    @Test
    public void getCurrentOrganizationUsersAndEnsureCurrentUserIsAMember() {
        String orgName = CCNG_USER_ORG;
        Map<String, CloudUser> orgUsers = connectedClient.getOrganizationUsers(orgName);
        assertNotNull(orgUsers);
        assertTrue("Org " + orgName + " should at least contain 1 user", orgUsers.size() > 0);
        String username = CCNG_USER_EMAIL;
        assertTrue("Organization user list should contain current user", orgUsers.containsKey(username));
    }

    @Test
    public void getDomains() {
        connectedClient.addDomain(TEST_DOMAIN);

        List<CloudDomain> allDomains = connectedClient.getDomains();

        assertNotNull(getDomainNamed(defaultDomainName, allDomains));
        assertNotNull(getDomainNamed(TEST_DOMAIN, allDomains));
    }

    @Test
    public void getFile() throws Exception {
        String appName = namespacedAppName("simple_getFile");
        createAndUploadAndStartSimpleSpringApp(appName);
        boolean running = getInstanceInfosWithTimeout(appName, 1, true);
        assertTrue("App failed to start", running);
        doGetFile(connectedClient, appName);
    }

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
        connectedClient.deleteAllServices();
        assertTrue(log1.size() > 0);
        assertEquals(log1, log2);
        connectedClient.unRegisterRestLogListener(callback2);
        getApplications();
        connectedClient.deleteAllApplications();
        assertTrue(log1.size() > log2.size());
    }

    @Test
    public void getService() {
        String serviceName = "mysql-test";

        CloudService expectedService = createMySqlService(serviceName);
        CloudService service = connectedClient.getService(serviceName);

        assertNotNull(service);
        assertServicesEqual(expectedService, service);
    }

    @Test
    public void getServiceBrokers() {
        assumeTrue(CCNG_USER_IS_ADMIN);

        List<CloudServiceBroker> brokers = connectedClient.getServiceBrokers();
        assertNotNull(brokers);
        assertTrue(brokers.size() >= 1);
        CloudServiceBroker broker0 = brokers.get(0);
        assertNotNull(broker0.getMeta());
        assertNotNull(broker0.getName());
        assertNotNull(broker0.getUrl());
        assertNotNull(broker0.getUsername());
    }

    @Test
    public void getServiceInstance() {
        String serviceName = "mysql-instance-test";
        String appName = createSpringTravelApp("service-instance-app", Collections.singletonList(serviceName));

        CloudApplication application = connectedClient.getApplication(appName);

        CloudServiceInstance serviceInstance = connectedClient.getServiceInstance(serviceName);
        assertNotNull(serviceInstance);
        assertEquals(serviceName, serviceInstance.getName());
        assertNotNull(serviceInstance.getDashboardUrl());
        assertNotNull(serviceInstance.getType());
        assertNotNull(serviceInstance.getCredentials());

        CloudService service = serviceInstance.getService();
        assertNotNull(service);
        assertEquals(MYSQL_SERVICE_LABEL, service.getLabel());
        assertEquals(MYSQL_SERVICE_PLAN, service.getPlan());

        CloudServicePlan servicePlan = serviceInstance.getServicePlan();
        assertNotNull(servicePlan);
        assertEquals(MYSQL_SERVICE_PLAN, servicePlan.getName());

        List<CloudServiceBinding> bindings = serviceInstance.getBindings();
        assertNotNull(bindings);
        assertEquals(1, bindings.size());
        CloudServiceBinding binding = bindings.get(0);
        assertEquals(application.getMeta().getGuid(), binding.getAppGuid());
        assertNotNull(binding.getCredentials());
        assertTrue(binding.getCredentials().size() > 0);
        assertNotNull(binding.getBindingOptions());
        assertEquals(0, binding.getBindingOptions().size());
        assertNull(binding.getSyslogDrainUrl());
    }

    @Test
    public void getServiceOfferings() {
        List<CloudServiceOffering> offerings = connectedClient.getServiceOfferings();

        assertNotNull(offerings);
        assertTrue(offerings.size() >= 2);

        CloudServiceOffering offering = null;
        for (CloudServiceOffering so : offerings) {
            if (so.getLabel().equals(MYSQL_SERVICE_LABEL)) {
                offering = so;
                break;
            }
        }
        assertNotNull(offering);
        assertEquals(MYSQL_SERVICE_LABEL, offering.getLabel());
        assertNotNull(offering.getCloudServicePlans());
        assertTrue(offering.getCloudServicePlans().size() > 0);
        assertNotNull(offering.getName());
        assertNotNull(offering.getDescription());
        assertNotNull(offering.getLabel());
        assertNotNull(offering.getUniqueId());
        assertNotNull(offering.getExtra());

        CloudServicePlan plan = offering.getCloudServicePlans().get(0);
        assertNotNull(plan.getName());
        assertNotNull(plan.getUniqueId());
        assertNotNull(plan.getDescription());
        assertSame(offering, plan.getServiceOffering());
    }

    @Test
    public void getServiceWithVersionAndProvider() {
        String serviceName = "mysql-test-version-provider";

        CloudService expectedService = createMySqlServiceWithVersionAndProvider(serviceName);
        CloudService service = connectedClient.getService(serviceName);

        assertNotNull(service);
        assertServicesEqual(expectedService, service);
        assertEquals(expectedService.getProvider(), service.getProvider());
        assertEquals(expectedService.getVersion(), service.getVersion());
    }

    @Test
    public void getServices() {
        List<CloudService> expectedServices = Arrays.asList(
                createMySqlService("mysql-test"),
                createUserProvidedService("user-provided-test"),
                createMySqlService("mysql-test2")
        );

        List<CloudService> services = connectedClient.getServices();
        assertNotNull(services);
        assertEquals(3, services.size());
        for (CloudService expectedService : expectedServices) {
            assertServiceMatching(expectedService, services);
        }
    }

    @Test
    public void getStack() throws Exception {
        CloudStack stack = connectedClient.getStack(DEFAULT_STACK_NAME);
        assertNotNull(stack);
        assertNotNull(stack.getMeta().getGuid());
        assertEquals(DEFAULT_STACK_NAME, stack.getName());
        assertNotNull(stack.getDescription());
    }

    @Test
    public void getStacks() throws Exception {
        List<CloudStack> stacks = connectedClient.getStacks();
        assert (stacks.size() >= 1);

        CloudStack stack = null;
        for (CloudStack s : stacks) {
            if (DEFAULT_STACK_NAME.equals(s.getName())) {
                stack = s;
            }
        }
        assertNotNull(stack);
        assertNotNull(stack.getMeta().getGuid());
        assertEquals(DEFAULT_STACK_NAME, stack.getName());
        assertNotNull(stack.getDescription());
    }

    @Test
    public void getStagingLogs() throws Exception {
        String appName = createSpringTravelApp("stagingLogs");

        File file = SampleProjects.springTravel();
        connectedClient.uploadApplication(appName, file.getCanonicalPath());

        StartingInfo startingInfo;
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
    // Files and Log tests
    //

    @Test
    public void getUserProvidedService() {
        String serviceName = "user-provided-test-service";

        CloudService expectedService = createUserProvidedService(serviceName);
        CloudService service = connectedClient.getService(serviceName);

        assertNotNull(service);
        assertServicesEqual(expectedService, service);
    }

    @Test
    public void infoAvailable() throws Exception {
        CloudInfo info = connectedClient.getCloudInfo();
        assertNotNull(info.getName());
        assertNotNull(info.getSupport());
        assertNotNull(info.getBuild());
    }

    @Test
    public void infoAvailableWithoutLoggingIn() throws Exception {
        CloudFoundryClient infoClient = new CloudFoundryClient(new URL(CCNG_API_URL), httpProxyConfiguration,
                CCNG_API_SSL);
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
    public void openFile() throws Exception {
        String appName = namespacedAppName("simple_openFile");
        createAndUploadAndStartSimpleSpringApp(appName);
        boolean running = getInstanceInfosWithTimeout(appName, 1, true);
        assertTrue("App failed to start", running);
        doOpenFile(connectedClient, appName);
    }

    @Test
    public void orgsAvailable() throws Exception {
        List<CloudOrganization> orgs = connectedClient.getOrganizations();
        assertNotNull(orgs);
        assertTrue(orgs.size() > 0);
    }

    //
    // Basic Services tests
    //

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

    @Test
    public void quotasAvailable() throws Exception {
        List<CloudQuota> quotas = connectedClient.getQuotas();
        assertNotNull(quotas);
        assertTrue(quotas.size() > 0);
    }

    @Test
    public void refreshTokenOnExpiration() throws Exception {
        URL cloudControllerUrl = new URL(CCNG_API_URL);
        CloudCredentials credentials = new CloudCredentials(CCNG_USER_EMAIL, CCNG_USER_PASS);

        CloudControllerClientFactory factory =
                new CloudControllerClientFactory(httpProxyConfiguration, CCNG_API_SSL);
        CloudControllerClient client = factory.newCloudController(cloudControllerUrl, credentials, CCNG_USER_ORG,
                CCNG_USER_SPACE);

        client.login();

        validateClientAccess(client);

        OauthClient oauthClient = factory.getOauthClient();
        OAuth2AccessToken token = oauthClient.getToken();
        if (token instanceof DefaultOAuth2AccessToken) {
            // set the token expiration to "now", forcing the access token to be refreshed
            ((DefaultOAuth2AccessToken) token).setExpiration(new Date());
            validateClientAccess(client);
        } else {
            fail("Error forcing expiration of access token");
        }
    }

    @Test
    public void renameApplication() {
        String appName = createSpringTravelApp("5");
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
    public void securityGroupsCanBeCreatedAndUpdatedFromJsonFiles() throws FileNotFoundException {
        assumeTrue(CCNG_USER_IS_ADMIN);

        // Create
        connectedClient.createSecurityGroup(CCNG_SECURITY_GROUP_NAME_TEST, new FileInputStream(new File
                ("src/test/resources/security-groups/test-rules-1.json")));

        // Verify created
        CloudSecurityGroup securityGroup = connectedClient.getSecurityGroup(CCNG_SECURITY_GROUP_NAME_TEST);
        assertNotNull(securityGroup);
        assertThat(securityGroup.getRules().size(), is(4));
        assertRulesMatchThoseInJsonFile1(securityGroup);

        // Update group
        connectedClient.updateSecurityGroup(CCNG_SECURITY_GROUP_NAME_TEST, new FileInputStream(new File
                ("src/test/resources/security-groups/test-rules-2.json")));

        // Verify update
        securityGroup = connectedClient.getSecurityGroup(CCNG_SECURITY_GROUP_NAME_TEST);
        assertThat(securityGroup.getRules().size(), is(1));

        // Clean up after ourselves
        connectedClient.deleteSecurityGroup(CCNG_SECURITY_GROUP_NAME_TEST);
    }

    @Test
    public void serviceBrokerLifecycle() throws IOException {
        assumeTrue(CCNG_USER_IS_ADMIN);

        createAndUploadAndStartSampleServiceBrokerApp("haash-broker");

        boolean pass = ensureApplicationRunning("haash-broker");
        assertTrue("haash-broker failed to start", pass);

        CloudServiceBroker newBroker = new CloudServiceBroker(CloudEntity.Meta.defaultMeta(), "haash-broker",
                "http://haash-broker.cf.deepsouthcloud.com", "warreng", "snoopdogg");
        connectedClient.createServiceBroker(newBroker);

        CloudServiceBroker broker = connectedClient.getServiceBroker("haash-broker");
        assertNotNull(broker);
        assertNotNull(broker.getMeta());
        assertEquals("haash-broker", broker.getName());
        assertEquals("http://haash-broker.cf.deepsouthcloud.com", broker.getUrl());
        assertEquals("warreng", broker.getUsername());
        assertNull(broker.getPassword());

        newBroker = new CloudServiceBroker(CloudEntity.Meta.defaultMeta(), "haash-broker", "http://haash-broker.cf" +
                ".deepsouthcloud.com", "warreng", "snoopdogg");
        connectedClient.updateServiceBroker(newBroker);

        connectedClient.updateServicePlanVisibilityForBroker("haash-broker", true);
        connectedClient.updateServicePlanVisibilityForBroker("haash-broker", false);

        connectedClient.deleteServiceBroker("haash-broker");
    }

	/*@Test
    public void getServiceBroker() {
		assumeTrue(CCNG_USER_IS_ADMIN);

		CloudServiceBroker broker = connectedClient.getServiceBroker("haash-broker");
		assertNotNull(broker);
		assertNotNull(broker.getMeta());
		assertEquals("haash-broker", broker.getName());
		assertEquals("http://haash-broker.cf.deepsouthcloud.com", broker.getUrl());
		assertEquals("warreng", broker.getUsername());
		assertNull(broker.getPassword());
	}

	@Test
	public void createServiceBroker() {
		assumeTrue(CCNG_USER_IS_ADMIN);

		CloudServiceBroker newBroker = new CloudServiceBroker(CloudEntity.Meta.defaultMeta(), "haash-broker",
		"http://haash-broker.cf.deepsouthcloud.com", "warreng", "natedogg");
		connectedClient.createServiceBroker(newBroker);
	}

	@Test
	public void updateServiceBroker() {
		assumeTrue(CCNG_USER_IS_ADMIN);

		CloudServiceBroker newBroker = new CloudServiceBroker(CloudEntity.Meta.defaultMeta(), "haash-broker",
		"http://haash-broker.cf.deepsouthcloud.com", "warreng", "snoopdogg");
		connectedClient.updateServiceBroker(newBroker);
	}*/

    @Test
    public void setEnvironmentThroughList() throws IOException {
        String appName = createSpringTravelApp("env1");
        CloudApplication app = connectedClient.getApplication(appName);
        assertTrue(app.getEnv().isEmpty());

        connectedClient.updateApplicationEnv(appName, asList("foo=bar", "bar=baz"));
        app = connectedClient.getApplication(app.getName());
        assertEquals(arrayToHashSet("foo=bar", "bar=baz"), listToHashSet(app.getEnv()));

        connectedClient.updateApplicationEnv(appName, asList("foo=baz", "baz=bong"));
        app = connectedClient.getApplication(app.getName());
        assertEquals(arrayToHashSet("foo=baz", "baz=bong"), listToHashSet(app.getEnv()));

        connectedClient.updateApplicationEnv(appName, new ArrayList<String>());
        app = connectedClient.getApplication(app.getName());
        assertTrue(app.getEnv().isEmpty());
    }

    @Test
    public void setEnvironmentThroughMap() throws IOException {
        String appName = createSpringTravelApp("env3");
        CloudApplication app = connectedClient.getApplication(appName);
        assertTrue(app.getEnv().isEmpty());

        Map<String, String> env1 = new HashMap<String, String>();
        env1.put("foo", "bar");
        env1.put("bar", "baz");
        connectedClient.updateApplicationEnv(appName, env1);
        app = connectedClient.getApplication(app.getName());
        assertEquals(env1, app.getEnvAsMap());
        assertEquals(arrayToHashSet("foo=bar", "bar=baz"), listToHashSet(app.getEnv()));

        Map<String, String> env2 = new HashMap<String, String>();
        env2.put("foo", "baz");
        env2.put("baz", "bong");
        connectedClient.updateApplicationEnv(appName, env2);
        app = connectedClient.getApplication(app.getName());

        // Test the unparsed list first
        assertEquals(arrayToHashSet("foo=baz", "baz=bong"), listToHashSet(app.getEnv()));

        assertEquals(env2, app.getEnvAsMap());

        connectedClient.updateApplicationEnv(appName, new HashMap<String, String>());
        app = connectedClient.getApplication(app.getName());
        assertTrue(app.getEnv().isEmpty());
        assertTrue(app.getEnvAsMap().isEmpty());
    }

    @Test
    public void setEnvironmentThroughMapEqualsInValue() throws IOException {
        String appName = createSpringTravelApp("env4");
        CloudApplication app = connectedClient.getApplication(appName);
        assertTrue(app.getEnv().isEmpty());

        Map<String, String> env1 = new HashMap<String, String>();
        env1.put("key", "foo=bar,fu=baz");
        connectedClient.updateApplicationEnv(appName, env1);
        app = connectedClient.getApplication(app.getName());

        // Test the unparsed list first
        assertEquals(arrayToHashSet("key=foo=bar,fu=baz"), listToHashSet(app.getEnv()));

        assertEquals(env1, app.getEnvAsMap());

        connectedClient.updateApplicationEnv(appName, new HashMap<String, String>());
        app = connectedClient.getApplication(app.getName());
        assertTrue(app.getEnv().isEmpty());
        assertTrue(app.getEnvAsMap().isEmpty());
    }

    @Test
    public void setEnvironmentWithoutEquals() throws IOException {
        thrown.expect(IllegalArgumentException.class);
        String appName = createSpringTravelApp("env2");
        CloudApplication app = connectedClient.getApplication(appName);
        assertTrue(app.getEnv().isEmpty());
        connectedClient.updateApplicationEnv(appName, asList("foo:bar", "bar=baz"));
    }

    @Test
    public void setQuotaToOrg() throws Exception {
        assumeTrue(CCNG_USER_IS_ADMIN);

        // get old quota to restore after test
        CloudOrganization org = connectedClient.getOrgByName(CCNG_USER_ORG, true);
        CloudQuota oldQuota = org.getQuota();

        // create and set test_quota to org
        CloudQuota cloudQuota = new CloudQuota(null, CCNG_QUOTA_NAME_TEST);
        connectedClient.createQuota(cloudQuota);
        connectedClient.setQuotaToOrg(CCNG_USER_ORG, CCNG_QUOTA_NAME_TEST);

        // get the bound quota of org
        org = connectedClient.getOrgByName(CCNG_USER_ORG, true);
        CloudQuota newQuota = org.getQuota();

        // bound quota should be equals to test_quota
        assertEquals(CCNG_QUOTA_NAME_TEST, newQuota.getName());

        // restore org to default quota
        connectedClient.setQuotaToOrg(CCNG_USER_ORG, oldQuota.getName());
        connectedClient.deleteQuota(CCNG_QUOTA_NAME_TEST);
    }

    @Before
    public void setUp() throws Exception {
        URL cloudControllerUrl;

        cloudControllerUrl = new URL(CCNG_API_URL);
        connectedClient = new CloudFoundryClient(new CloudCredentials(CCNG_USER_EMAIL, CCNG_USER_PASS),
                cloudControllerUrl, CCNG_USER_ORG, CCNG_USER_SPACE, httpProxyConfiguration, CCNG_API_SSL);
        connectedClient.login();
        defaultDomainName = connectedClient.getDefaultDomain().getName();

        // Optimization to avoid redoing the work already done is tearDown()
        if (!tearDownComplete) {
            tearDown();
        }
        tearDownComplete = false;
        connectedClient.addDomain(TEST_DOMAIN);

        // connectedClient.registerRestLogListener(new RestLogger("CF_REST"));
        if (nbInJvmProxyRcvReqs != null) {
            nbInJvmProxyRcvReqs.set(0); //reset calls made in setup to leave a clean state for tests to assert
        }

        if (!SKIP_INJVM_PROXY) {
            new SocketDestHelper().setForbiddenOnCurrentThread();
        }
    }

    //
    // Application and Services tests
    //

    @Test
    public void spacesAvailable() throws Exception {
        List<CloudSpace> spaces = connectedClient.getSpaces();
        assertNotNull(spaces);
        assertTrue(spaces.size() > 0);
    }

    @Test
    public void startExplodedApplication() throws IOException {
        String appName = namespacedAppName("exploded_app");
        createAndUploadExplodedSpringTestApp(appName);
        connectedClient.startApplication(appName);
        CloudApplication app = connectedClient.getApplication(appName);
        assertEquals(CloudApplication.AppState.STARTED, app.getState());
    }

    @Test
    public void startStopApplication() throws IOException {
        String appName = createSpringTravelApp("upload-start-stop");
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
    public void streamLogs() throws Exception {
        // disable proxy validation for this test, since Loggregator websockets
        // connectivity does not currently support proxies
        new SocketDestHelper().setAllowedOnCurrentThread();

        String appName = namespacedAppName("simple_logs");
        CloudApplication app = createAndUploadAndStartSimpleSpringApp(appName);
        boolean pass = getInstanceInfosWithTimeout(appName, 1, true);
        assertTrue("Couldn't get the right application state", pass);

        List<ApplicationLog> logs = doGetRecentLogs(appName);

        for (int index = 0; index < logs.size() - 1; index++) {
            int comparison = logs.get(index).getTimestamp().compareTo(logs.get(index + 1).getTimestamp());
            assertTrue("Logs are not properly sorted", comparison <= 0);
        }

        AccumulatingApplicationLogListener testListener = new AccumulatingApplicationLogListener();
        connectedClient.streamLogs(appName, testListener);
        String appUri = "http://" + app.getUris().get(0);
        RestTemplate appTemplate = new RestTemplate();
        int attempt = 0;
        do {
            // no need to sleep, visiting the app uri should be sufficient
            try {
                appTemplate.getForObject(appUri, String.class);
            } catch (HttpClientErrorException ex) {
                // ignore
            }
            if (testListener.logs.size() > 0) {
                break;
            }
            Thread.sleep(1000);
        } while (attempt++ < 30);
        assertTrue("Failed to stream normal log", testListener.logs.size() > 0);
    }

    @After
    public void tearDown() throws Exception {
        // Clean after ourselves so that there are no leftover apps, services, domains, and routes
        if (connectedClient != null) { //may happen if setUp() fails
            connectedClient.deleteAllApplications();
            connectedClient.deleteAllServices();
            clearTestDomainAndRoutes();
            deleteAnyOrphanedTestSecurityGroups();
        }
        tearDownComplete = true;
    }

    @Test
    public void updateApplicationDisk() throws IOException {
        String appName = createSpringTravelApp("updateDisk");
        connectedClient.updateApplicationDiskQuota(appName, 2048);
        CloudApplication app = connectedClient.getApplication(appName);
        assertEquals(2048, app.getDiskQuota());
    }

    @Test
    public void updateApplicationInstances() throws Exception {
        String appName = createSpringTravelApp("updateInstances");
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
    public void updateApplicationMemory() throws IOException {
        String appName = createSpringTravelApp("updateMemory");
        connectedClient.updateApplicationMemory(appName, 256);
        CloudApplication app = connectedClient.getApplication(appName);
        assertEquals(256, app.getMemory());
    }

    @Test
    public void updateApplicationService() throws IOException {
        String serviceName = "test_database";
        createMySqlService(serviceName);
        String appName = createSpringTravelApp("7");

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
    public void updateApplicationUris() throws IOException {
        String appName = namespacedAppName("updateUris");
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

    @Test
    public void updatePassword() throws MalformedURLException {
        // Not working currently
        assumeTrue(false);

        String newPassword = "newPass123";
        connectedClient.updatePassword(newPassword);
        CloudFoundryClient clientWithChangedPassword =
                new CloudFoundryClient(new CloudCredentials(CCNG_USER_EMAIL, newPassword), new URL(CCNG_API_URL),
                        httpProxyConfiguration);
        clientWithChangedPassword.login();

        // Revert
        connectedClient.updatePassword(CCNG_USER_PASS);
        connectedClient.login();
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

        Staging newStaging = new Staging("ruby simple.rb test", "https://github" +
                ".com/cloudfoundry/heroku-buildpack-ruby");
        connectedClient.updateApplicationStaging(appName, newStaging);
        app = connectedClient.getApplication(appName);
        assertNotNull(app);
        assertEquals(uris, app.getUris());
        assertEquals("ruby simple.rb test", app.getStaging().getCommand());
        assertEquals("https://github.com/cloudfoundry/heroku-buildpack-ruby", app.getStaging().getBuildpackUrl());
    }

    @Test
    public void uploadAppFromInputStream() throws IOException {
        String appName = namespacedAppName("upload-from-input-stream");
        createSpringApplication(appName);
        File file = SampleProjects.springTravel();
        FileInputStream inputStream = new FileInputStream(file);
        connectedClient.uploadApplication(appName, appName, inputStream);
        connectedClient.startApplication(appName);
        CloudApplication env = connectedClient.getApplication(appName);
        assertEquals(CloudApplication.AppState.STARTED, env.getState());
    }

    @Test
    public void uploadAppWithNonAsciiFileName() throws IOException {
        String appName = namespacedAppName("non-ascii");
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
    public void uploadAppWithNonUnsubscribingCallback() throws IOException {
        String appName = namespacedAppName("upload-non-unsubscribing-callback");
        createSpringApplication(appName);
        File file = SampleProjects.springTravel();
        NonUnsubscribingUploadStatusCallback callback = new NonUnsubscribingUploadStatusCallback();
        connectedClient.uploadApplication(appName, file, callback);
        CloudApplication env = connectedClient.getApplication(appName);
        assertEquals(CloudApplication.AppState.STOPPED, env.getState());
        assertTrue(callback.progressCount >= 1); // must have taken at least 10 seconds
    }

    @Test
    public void uploadAppWithUnsubscribingCallback() throws IOException {
        String appName = namespacedAppName("upload-unsubscribing-callback");
        createSpringApplication(appName);
        File file = SampleProjects.springTravel();
        UnsubscribingUploadStatusCallback callback = new UnsubscribingUploadStatusCallback();
        connectedClient.uploadApplication(appName, file, callback);
        CloudApplication env = connectedClient.getApplication(appName);
        assertEquals(CloudApplication.AppState.STOPPED, env.getState());
        assertTrue(callback.progressCount == 1);
    }

    //
    // Configuration/Metadata tests
    //

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

    private HashSet<String> arrayToHashSet(String... array) {
        return listToHashSet(asList(array));
    }

    private void assertAppEnvironment(Map<String, Object> env) {
        assertMapInEnv(env, "staging_env_json", true);
        assertMapInEnv(env, "running_env_json", true);
        assertMapInEnv(env, "environment_json", true, "testKey");
        assertMapInEnv(env, "system_env_json", true, "VCAP_SERVICES");
        // this value is not present in Pivotal CF < 1.4
        assertMapInEnv(env, "application_env_json", false, "VCAP_APPLICATION");
    }

    private void assertDomainInList(List<CloudDomain> domains) {
        assertTrue(domains.size() >= 1);
        assertNotNull(getDomainNamed(TEST_DOMAIN, domains));
    }

    private void assertDomainNotInList(List<CloudDomain> domains) {
        assertNull(getDomainNamed(TEST_DOMAIN, domains));
    }

    private void assertEventTimestamps(List<CloudEvent> events) {
        for (CloudEvent event : events) {
            if (event.getTimestamp() != null) {
                assertTimeWithinRange("Event time should be very recent", event.getTimestamp().getTime(), FIVE_MINUTES);
            }
        }
    }

    private void assertEvents(List<CloudEvent> events) {
        assertNotNull(events);
        assertTrue(events.size() > 0);

        for (CloudEvent event : events) {
            assertNotNull(event.getActee());
            assertNotNull(event.getActeeType());
            assertNotNull(event.getActeeName());
            assertNotNull(event.getActor());
            assertNotNull(event.getActorType());
            assertNotNull(event.getActorName());
        }
    }

    private void assertMapInEnv(Map<String, Object> env, String key, boolean alwaysPresent, String... expectedKeys) {
        Object value = env.get(key);

        if (value == null) {
            if (alwaysPresent) {
                fail("Expected key " + key + " was not found");
            } else {
                return;
            }
        }

        assertTrue(value.getClass().getName(), value instanceof Map);
        Map map = (Map) value;
        assertTrue(map.size() >= expectedKeys.length);

        for (String expectedKey : expectedKeys) {
            assertTrue(map.containsKey(expectedKey));
        }
    }

    private void assertNetworkCallFails(RestTemplate restTemplate, ClientHttpRequestFactory requestFactory) {
        restTemplate.setRequestFactory(requestFactory);
        try {
            HttpStatus status = restTemplate.execute(CCNG_API_URL + "/info", HttpMethod.GET, null, new
                    ResponseExtractor<HttpStatus>() {
                        public HttpStatus extractData(ClientHttpResponse response) throws IOException {
                            return response.getStatusCode();
                        }
                    });
            Assert.fail("Expected byteman rules to detect direct socket connections, status is:" + status);
        } catch (Exception e) {
            //good, byteman rejected it as expected
            //e.printStackTrace();
        }
        assertEquals("Not expecting Jetty to receive requests since we asked direct connections", 0,
                nbInJvmProxyRcvReqs.get());
    }

    private void assertRulesMatchTestData(CloudSecurityGroup securityGroup) {
        // This asserts against the test data defined in the crudSecurityGroups method
        // Rule ordering is preserved so we can depend on it here
        SecurityGroupRule rule = securityGroup.getRules().get(0);
        assertThat(rule.getProtocol(), is("tcp"));
        assertThat(rule.getPorts(), is("80, 443"));
        assertThat(rule.getDestination(), is("205.158.11.29"));
        assertNull(rule.getLog());
        assertNull(rule.getType());
        assertNull(rule.getCode());

        rule = securityGroup.getRules().get(1);
        assertThat(rule.getProtocol(), is("all"));
        assertNull(rule.getPorts());
        assertThat(rule.getDestination(), is("0.0.0.0-255.255.255.255"));
        assertNull(rule.getLog());
        assertNull(rule.getType());
        assertNull(rule.getCode());

        rule = securityGroup.getRules().get(2);
        assertThat(rule.getProtocol(), is("icmp"));
        assertNull(rule.getPorts());
        assertThat(rule.getDestination(), is("0.0.0.0/0"));
        assertTrue(rule.getLog());
        assertThat(rule.getType(), is(0));
        assertThat(rule.getCode(), is(1));
    }

    private void assertRulesMatchThoseInJsonFile1(CloudSecurityGroup securityGroup) {
        // Rule ordering is preserved so we can depend on it here

        SecurityGroupRule rule = securityGroup.getRules().get(0);
        assertThat(rule.getProtocol(), is("icmp"));
        assertNull(rule.getPorts());
        assertThat(rule.getDestination(), is("0.0.0.0/0"));
        assertNull(rule.getLog());
        assertThat(rule.getType(), is(0));
        assertThat(rule.getCode(), is(1));

        rule = securityGroup.getRules().get(1);
        assertThat(rule.getProtocol(), is("tcp"));
        assertThat(rule.getPorts(), is("2048-3000"));
        assertThat(rule.getDestination(), is("1.0.0.0/0"));
        assertTrue(rule.getLog());
        assertNull(rule.getType());
        assertNull(rule.getCode());

        rule = securityGroup.getRules().get(2);
        assertThat(rule.getProtocol(), is("udp"));
        assertThat(rule.getPorts(), is("53, 5353"));
        assertThat(rule.getDestination(), is("2.0.0.0/0"));
        assertNull(rule.getLog());
        assertNull(rule.getType());
        assertNull(rule.getCode());

        rule = securityGroup.getRules().get(3);
        assertThat(rule.getProtocol(), is("all"));
        assertNull(rule.getPorts());
        assertThat(rule.getDestination(), is("3.0.0.0/0"));
        assertNull(rule.getLog());
        assertNull(rule.getType());
        assertNull(rule.getCode());
    }

    private void assertServiceMatching(CloudService expectedService, List<CloudService> services) {
        for (CloudService service : services) {
            if (service.getName().equals(expectedService.getName())) {
                assertServicesEqual(expectedService, service);
                return;
            }
        }
        fail("No service found matching " + expectedService.getName());
    }

    private void assertServicesEqual(CloudService expectedService, CloudService service) {
        assertEquals(expectedService.getName(), service.getName());
        assertEquals(expectedService.getLabel(), service.getLabel());
        assertEquals(expectedService.getPlan(), service.getPlan());
        assertEquals(expectedService.isUserProvided(), service.isUserProvided());
    }

    private void assertTimeWithinRange(String message, long actual, int timeTolerance) {
        // Allow more time deviations due to local clock being out of sync with cloud
        assertTrue(message,
                Math.abs(System.currentTimeMillis() - actual) < timeTolerance);
    }

    private void clearTestDomainAndRoutes() {
        CloudDomain domain = getDomainNamed(TEST_DOMAIN, connectedClient.getDomains());
        if (domain != null) {
            List<CloudRoute> routes = connectedClient.getRoutes(domain.getName());
            for (CloudRoute route : routes) {
                connectedClient.deleteRoute(route.getHost(), route.getDomain().getName());
            }
            connectedClient.deleteDomain(domain.getName());
        }
    }

    private String computeAppUrl(String appName) {
        return appName + "." + defaultDomainName;
    }

    private String computeAppUrlNoProtocol(String appName) {
        return computeAppUrl(appName);
    }

    private boolean containsSecurityGroupNamed(List<CloudSecurityGroup> groups, String groupName) {
        for (CloudSecurityGroup group : groups) {
            if (groupName.equalsIgnoreCase(group.getName())) {
                return true;
            }
        }
        return false;
    }

    private CloudApplication createAndUploadAndStartSampleServiceBrokerApp(String appName) throws IOException {
        createSpringApplication(appName);
        File jar = SampleProjects.sampleServiceBrokerApp();
        connectedClient.uploadApplication(appName, jar.getCanonicalPath());
        connectedClient.startApplication(appName);
        return connectedClient.getApplication(appName);
    }

    private CloudApplication createAndUploadAndStartSimpleSpringApp(String appName) throws IOException {
        createAndUploadSimpleSpringApp(appName);
        connectedClient.startApplication(appName);
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

    //
    // Shared test methods
    //

    private CloudApplication createAndUploadExplodedTestApp(String appName, File explodedDir, Staging staging)
            throws IOException {
        assertTrue("Expected exploded test app at " + explodedDir.getCanonicalPath(), explodedDir.exists());
        createTestApp(appName, null, staging);
        connectedClient.uploadApplication(appName, explodedDir.getCanonicalPath());
        return connectedClient.getApplication(appName);
    }

    private CloudApplication createAndUploadSimpleSpringApp(String appName) throws IOException {
        createSpringApplication(appName);
        File war = SampleProjects.simpleSpringApp();
        connectedClient.uploadApplication(appName, war.getCanonicalPath());
        return connectedClient.getApplication(appName);
    }

    private CloudApplication createAndUploadSimpleTestApp(String name) throws IOException {
        createAndUploadSimpleSpringApp(name);
        return connectedClient.getApplication(name);
    }

    //
    // Helper methods
    //

    private CloudService createMySqlService(String serviceName) {
        CloudService service = new CloudService(CloudEntity.Meta.defaultMeta(), serviceName);
        service.setLabel(MYSQL_SERVICE_LABEL);
        service.setPlan(MYSQL_SERVICE_PLAN);

        connectedClient.createService(service);

        return service;
    }

    private CloudService createMySqlServiceWithVersionAndProvider(String serviceName) {
        CloudServiceOffering databaseServiceOffering = getCloudServiceOffering(MYSQL_SERVICE_LABEL);

        CloudService service = new CloudService(CloudEntity.Meta.defaultMeta(), serviceName);
        service.setProvider(databaseServiceOffering.getProvider());
        service.setLabel(databaseServiceOffering.getLabel());
        service.setVersion(databaseServiceOffering.getVersion());
        service.setPlan(MYSQL_SERVICE_PLAN);

        connectedClient.createService(service);

        return service;
    }

    private void createSpringApplication(String appName) {
        createTestApp(appName, null, new Staging());
    }

    //
    // helper methods
    //

    private void createSpringApplication(String appName, List<String> serviceNames) {
        createTestApp(appName, serviceNames, new Staging());
    }

    private void createSpringApplication(String appName, String buildpackUrl) {
        createTestApp(appName, null, new Staging(null, buildpackUrl));
    }

    private void createSpringApplication(String appName, String stack, Integer healthCheckTimeout) {
        createTestApp(appName, null, new Staging(null, null, stack, healthCheckTimeout));
    }

    private String createSpringTravelApp(String suffix) {
        return createSpringTravelApp(suffix, null);
    }

    private String createSpringTravelApp(String suffix, List<String> serviceNames) {
        String appName = namespacedAppName("travel_test-" + suffix);
        createSpringApplication(appName, serviceNames);
        return appName;
    }

    private void createStandaloneRubyTestApp(String appName, List<String> uris, List<String> services) throws
            IOException {
        Staging staging = new Staging("ruby simple.rb", null);
        File file = SampleProjects.standaloneRuby();
        connectedClient.createApplication(appName, staging, 128, uris, services);
        connectedClient.uploadApplication(appName, file.getCanonicalPath());
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

    private CloudService createUserProvidedService(String serviceName) {
        CloudService service = new CloudService(CloudEntity.Meta.defaultMeta(), serviceName);

        Map<String, Object> credentials = new HashMap<String, Object>();
        credentials.put("host", "example.com");
        credentials.put("port", 1234);
        credentials.put("user", "me");

        connectedClient.createUserProvidedService(service, credentials);

        return service;
    }

    /**
     * Try to clean up any security group test data left behind in the case of assertions failing and test security
     * groups not being deleted as part of test logic.
     */
    private void deleteAnyOrphanedTestSecurityGroups() {
        try {
            CloudSecurityGroup securityGroup = connectedClient.getSecurityGroup(CCNG_SECURITY_GROUP_NAME_TEST);
            if (securityGroup != null) {
                connectedClient.deleteSecurityGroup(CCNG_SECURITY_GROUP_NAME_TEST);
            }
        } catch (Exception e) {
            // Nothing we can do at this point except protect other teardown logic from not running
        }
    }

    private void doGetFile(CloudFoundryOperations client, String appName) throws Exception {
        String appDir = "app";
        String fileName = appDir + "/WEB-INF/web.xml";
        String emptyPropertiesFileName = appDir + "/WEB-INF/classes/empty.properties";

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
        int start = end / 2;
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
        String fileContent7 = client.getFile(appName, 0, emptyPropertiesFileName);
        assertNull(fileContent7);

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

    private List<ApplicationLog> doGetRecentLogs(String appName) throws InterruptedException {
        int attempt = 0;
        do {
            List<ApplicationLog> logs = connectedClient.getRecentLogs(appName);

            if (logs.size() > 0) {
                return logs;
            }
            Thread.sleep(1000);
        } while (attempt++ < 20);
        fail("Failed to see recent logs");
        return null;
    }

    private void doOpenFile(CloudFoundryOperations client, String appName) throws Exception {
        String appDir = "app";
        String fileName = appDir + "/WEB-INF/web.xml";
        String emptyPropertiesFileName = appDir + "/WEB-INF/classes/empty.properties";

        // File is often not available immediately after starting an app... so
        // allow up to 60 seconds wait
        for (int i = 0; i < 60; i++) {
            try {
                client.getFile(appName, 0, fileName);
                break;
            } catch (HttpServerErrorException ex) {
                Thread.sleep(1000);
            }
        }
        // Test open file

        client.openFile(appName, 0, fileName, new ClientHttpResponseCallback() {

            public void onClientHttpResponse(ClientHttpResponse clientHttpResponse) throws IOException {
                InputStream in = clientHttpResponse.getBody();
                assertNotNull(in);
                byte[] fileContents = IOUtils.toByteArray(in);
                assertTrue(fileContents.length > 5);
            }
        });

        client.openFile(appName, 0, emptyPropertiesFileName, new ClientHttpResponseCallback() {

            public void onClientHttpResponse(ClientHttpResponse clientHttpResponse) throws IOException {
                InputStream in = clientHttpResponse.getBody();
                assertNotNull(in);
                byte[] fileContents = IOUtils.toByteArray(in);
                assertTrue(fileContents.length == 0);
            }
        });

    }

    private boolean ensureApplicationRunning(String appName) {
        InstancesInfo instances;
        boolean pass = false;
        for (int i = 0; i < 50; i++) {
            try {
                instances = getInstancesWithTimeout(connectedClient, appName);
                assertNotNull(instances);

                List<InstanceInfo> infos = instances.getInstances();
                assertEquals(1, infos.size());

                int passCount = 0;
                for (InstanceInfo info : infos) {
                    if (InstanceState.RUNNING.equals(info.getState())) {
                        passCount++;
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

    private CloudServiceOffering getCloudServiceOffering(String label) {
        List<CloudServiceOffering> serviceOfferings = connectedClient.getServiceOfferings();
        for (CloudServiceOffering so : serviceOfferings) {
            if (so.getLabel().equals(label)) {
                return so;
            }
        }
        throw new IllegalStateException("No CloudServiceOffering found with label " + label + ".");
    }

    private CloudDomain getDomainNamed(String domainName, List<CloudDomain> domains) {
        for (CloudDomain domain : domains) {
            if (domain.getName().equals(domainName)) {
                return domain;
            }
        }
        return null;
    }

    private boolean getInstanceInfosWithTimeout(String appName, int count, boolean shouldBeRunning) {
        if (count > 1) {
            connectedClient.updateApplicationInstances(appName, count);
            CloudApplication app = connectedClient.getApplication(appName);
            assertEquals(count, app.getInstances());
        }

        InstancesInfo instances;
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

    private InstancesInfo getInstancesWithTimeout(CloudFoundryOperations client, String appName) {
        long start = System.currentTimeMillis();
        while (true) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e1) {
                // ignore
            }

            final InstancesInfo applicationInstances = client.getApplicationInstances(appName);
            if (applicationInstances != null) {
                return applicationInstances;
            }

            if (System.currentTimeMillis() - start > STARTUP_TIMEOUT) {
                fail("Timed out waiting for startup");
                break; // for the compiler
            }
        }

        return null; // for the compiler
    }

    private CloudRoute getRouteWithHost(String hostName, List<CloudRoute> routes) {
        for (CloudRoute route : routes) {
            if (route.getHost().equals(hostName)) {
                return route;
            }
        }
        return null;
    }

    private boolean isSpaceBoundToSecurityGroup(String spaceName, String securityGroupName) {
        List<CloudSpace> boundSpaces = connectedClient.getSpacesBoundToSecurityGroup(securityGroupName);
        for (CloudSpace space : boundSpaces) {
            if (spaceName.equals(space.getName())) {
                return true;
            }
        }
        return false;
    }

    private HashSet<String> listToHashSet(List<String> list) {
        return new HashSet<String>(list);
    }

    private String namespacedAppName(String basename) {
        return TEST_NAMESPACE + "-" + basename;
    }

    private String randomSecurityGroupName() {
        return UUID.randomUUID().toString();
    }

    private CloudApplication uploadSpringTravelApp(String appName) throws IOException {
        File file = SampleProjects.springTravel();
        connectedClient.uploadApplication(appName, file.getCanonicalPath());
        return connectedClient.getApplication(appName);
    }

    private void validateClientAccess(CloudControllerClient client) {
        List<CloudServiceOffering> offerings = client.getServiceOfferings();
        assertNotNull(offerings);
        assertTrue(offerings.size() >= 2);
    }

    private void waitForStatsAvailable(String appName, int instanceCount) throws InterruptedException {
        // TODO: Make this pattern reusable
        ApplicationStats stats = connectedClient.getApplicationStats(appName);
        for (int retries = 0; retries < 10 && stats.getRecords().size() < instanceCount; retries++) {
            Thread.sleep(1000);
            stats = connectedClient.getApplicationStats(appName);
        }

        InstanceStats firstInstance = stats.getRecords().get(0);
        assertEquals("0", firstInstance.getId());
        for (int retries = 0; retries < 50 && firstInstance.getUsage() == null; retries++) {
            Thread.sleep(1000);
            stats = connectedClient.getApplicationStats(appName);
            firstInstance = stats.getRecords().get(0);
        }
    }

    private static abstract class NoOpUploadStatusCallback implements UploadStatusCallback {

        public void onCheckResources() {
        }

        public void onMatchedFileNames(Set<String> matchedFileNames) {
        }

        public void onProcessMatchedResources(int length) {
        }
    }

    private static class NonUnsubscribingUploadStatusCallback extends NoOpUploadStatusCallback {

        public int progressCount = 0;

        public boolean onProgress(String status) {
            progressCount++;
            return false;
        }
    }

    private static class UnsubscribingUploadStatusCallback extends NoOpUploadStatusCallback {

        public int progressCount = 0;

        public boolean onProgress(String status) {
            progressCount++;
            // unsubscribe after the first report
            return progressCount == 1;
        }
    }

    private class AccumulatingApplicationLogListener implements ApplicationLogListener {

        private List<ApplicationLog> logs = new ArrayList<ApplicationLog>();

        public void onComplete() {
        }

        public void onError(Throwable exception) {
            fail(exception.getMessage());
        }

        public void onMessage(ApplicationLog log) {
            logs.add(log);
        }

    }
}
