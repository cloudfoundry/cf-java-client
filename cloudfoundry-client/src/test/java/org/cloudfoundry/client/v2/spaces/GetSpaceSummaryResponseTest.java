/*
 * Copyright 2013-2015 the original author or authors.
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

package org.cloudfoundry.client.v2.spaces;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GetSpaceSummaryResponseTest {

    @Test
    public void test() {

        GetSpaceSummaryResponse response = new GetSpaceSummaryResponse()
                .withApplications(Collections.singletonList(getTestSpaceApplicationSummary()))
                .withId("test-space-id")
                .withName("test-space-name")
                .withServices(Collections.singletonList(getTestSpaceServiceSummary()));

        assertEquivalentApplications(setOf(getTestSpaceApplicationSummary()), setOf(response
                .getApplications()));

        assertEquals("test-space-id", response.getId());

        assertEquals("test-space-name", response.getName());

        assertEquivalentServices(setOf(getTestSpaceServiceSummary()), response.getServices());
    }

    private static void assertEquivalentApplications(Set<SpaceApplicationSummary> eSpaceApplicationSummaries,
                                              Set<SpaceApplicationSummary> spaceApplicationSummaries) {
        assertTrue((eSpaceApplicationSummaries == null) == (spaceApplicationSummaries == null));
        if (eSpaceApplicationSummaries == null) {
            return;
        }
        assertEquals(eSpaceApplicationSummaries.size(), spaceApplicationSummaries.size());

        for (SpaceApplicationSummary as : eSpaceApplicationSummaries) {
            assertSpaceApplicationSummaryIsIn(as, spaceApplicationSummaries);
        }
    }

    private static void assertEquivalentServices(Set<SpaceServiceSummary> eSpaceServiceSummaries,
                                          List<SpaceServiceSummary> spaceServiceSummaries) {
        assertTrue((eSpaceServiceSummaries == null) == (spaceServiceSummaries == null));
        if (eSpaceServiceSummaries == null) {
            return;
        }
        assertEquals(eSpaceServiceSummaries.size(), spaceServiceSummaries.size());

        for (SpaceServiceSummary es : eSpaceServiceSummaries) {
            assertSpaceServiceSummaryIsIn(es, spaceServiceSummaries);
        }
    }

    private static void assertSpaceApplicationSummaryIsIn(SpaceApplicationSummary as,
                                                   Set<SpaceApplicationSummary> spaceApplicationSummaries) {
        for (SpaceApplicationSummary a : spaceApplicationSummaries) {
            if (equivalentApplicationSummary(as, a)) return;
        }
        fail("an application summary is not in the response");
    }

    private static void assertSpaceServiceSummaryIsIn(SpaceServiceSummary es,
                                               List<SpaceServiceSummary> spaceServiceSummaries) {
        for (SpaceServiceSummary s : spaceServiceSummaries) {
            if (equivalentServiceSummary(es, s)) return;
        }
        fail("a service summary is not in the response");
    }

    private static boolean equalStringSets(Set<String> ss1, Set<String> ss2) {
        return ss1.containsAll(ss2) && ss2.containsAll(ss1);
    }

    private static boolean equivalentApplicationSummary(SpaceApplicationSummary as1, SpaceApplicationSummary as2) {
        return as1.getId().equals(as2.getId())
                && (as1.getRunningInstances().equals(as2.getRunningInstances()))
                && equivalentRoutes(setOf(as1.getRoutes()), setOf(as2.getRoutes()))
                && (as1.getServiceCount().equals(as2.getServiceCount()))
                && (equalStringSets(setOf(as1.getServiceNames()), setOf(as2.getServiceNames())))
                && (equalStringSets(setOf(as1.getUrls()), setOf(as2.getUrls())))

                && as1.getBuildpack().equals(as2.getBuildpack())
                && as1.getCommand().equals(as2.getCommand())
                && as1.getConsole().equals(as2.getConsole())
                && as1.getDebug().equals(as2.getDebug())
                && as1.getDetectedBuildpack().equals(as2.getDetectedBuildpack())
                && as1.getDetectedStartCommand().equals(as2.getDetectedStartCommand())
                && as1.getDiego().equals(as2.getDiego())
                && as1.getDiskQuota().equals(as2.getDiskQuota())
                && as1.getDockerCredentialsJson().equals(as2.getDockerCredentialsJson())
                && as1.getDockerImage().equals(as2.getDockerImage())
                && as1.getEnableSsh().equals(as2.getEnableSsh())
                && as1.getEnvironmentJson().equals(as2.getEnvironmentJson())
                && as1.getHealthCheckTimeout().equals(as2.getHealthCheckTimeout())
                && as1.getHealthCheckType().equals(as2.getHealthCheckType())
                && as1.getInstances().equals(as2.getInstances())
                && as1.getMemory().equals(as2.getMemory())
                && as1.getName().equals(as2.getName())
                && as1.getPackageState().equals(as2.getPackageState())
                && as1.getPackageUpdatedAt().equals(as2.getPackageUpdatedAt())
                && as1.getProduction().equals(as2.getProduction())
                && as1.getSpaceId().equals(as2.getSpaceId())
                && as1.getStackId().equals(as2.getStackId())
                && as1.getStagingFailedDescription().equals(as2.getStagingFailedDescription())
                && as1.getStagingFailedReason().equals(as2.getStagingFailedReason())
                && as1.getStagingTaskId().equals(as2.getStagingTaskId())
                && as1.getState().equals(as2.getState())
                && as1.getVersion().equals(as2.getVersion())
                ;
    }

    private static boolean equivalentDomain(SpaceApplicationSummary.Route.Domain d1, SpaceApplicationSummary.Route.Domain d2) {
        return d1.getId().equals(d2.getId())
                && d1.getName().equals(d2.getName());
    }

    private static boolean equivalentPlans(SpaceServiceSummary.Plan sp1, SpaceServiceSummary.Plan sp2) {
        return sp1.getId().equals(sp2.getId())
                && sp1.getName().equals(sp2.getName())
                && equivalentServicePlanService(sp1.getService(), sp2.getService());
    }

    private static boolean equivalentRoute(SpaceApplicationSummary.Route r1, SpaceApplicationSummary.Route r2) {
        return equivalentDomain(r1.getDomain(), r2.getDomain())
                && r1.getHost().equals(r2.getHost())
                && r1.getId().equals(r2.getId());
    }

    private static boolean equivalentRoutes(Set<SpaceApplicationSummary.Route> rs1, Set<SpaceApplicationSummary.Route> rs2) {
        return routesContainsAll(rs1, rs2) && routesContainsAll(rs2, rs1);
    }

    private static boolean equivalentServiceSummary(SpaceServiceSummary ss1, SpaceServiceSummary ss2) {
        return ss1.getBoundAppCount().equals(ss2.getBoundAppCount())
                && ss1.getDashboardUrl().equals(ss2.getDashboardUrl())
                && ss1.getId().equals(ss2.getId())
                && ss1.getLastOperation().equals(ss2.getLastOperation())
                && ss1.getName().equals(ss2.getName())
                && equivalentPlans(ss1.getServicePlan(), ss2.getServicePlan());
    }

    private static boolean equivalentServicePlanService(SpaceServiceSummary.Plan.Service s1,
                                                 SpaceServiceSummary.Plan.Service s2) {
        return s1.getId().equals(s2.getId())
                && s1.getLabel().equals(s2.getLabel())
                && s1.getProvider().equals(s2.getProvider())
                && s1.getVersion().equals(s2.getVersion());
    }

    private static SpaceApplicationSummary getTestSpaceApplicationSummary() {
        SpaceApplicationSummary testAppSummary = new SpaceApplicationSummary()
                // SpaceApplicationSummary
                .withId("test-app-id")
                .withRoutes(Collections.singletonList(getTestRoute()))
                .withRunningInstances(1234)
                .withServiceCount(2345)
                .withServiceNames(Arrays.asList("test-service-name-1", "test-service-name-2"))
                .withUrls(Arrays.asList("test-url-1", "test-url-2"));

        testAppSummary
                // AbstractApplicationEntity
                .withBuildpack("test-buildpack")
                .withCommand("test-command")
                .console(true)
                .debug(true)
                .withDetectedBuildpack("test-detected-buildpack")
                .withDetectedStartCommand("test-detected-start-command")
                .diego(true)
                .withDiskQuota(3456)
                .withDockerCredentialsJson(Collections.singletonMap("test-docker-var", "test-docker-value"))
                .withDockerImage("test-docker-image")
                .enableSsh(true)
                .withEnvironmentJson(Collections.singletonMap("test-env-var", "test-env-value"))
                .withHealthCheckTimeout(4567)
                .withHealthCheckType("test-health-check-type")
                .withInstances(5678)
                .withMemory(6789)
                .withName("test-name")
                .withPackageState("test-package-state")
                .withPackageUpdatedAt("test-package-updated-at")
                .production(true)
                .withSpaceId("test-space-id")
                .withStackId("test-stack-id")
                .withStagingFailedDescription("test-staging-failed-description")
                .withStagingFailedReason("test-staging-failed-reason")
                .withStagingTaskId("test-staging-task-id")
                .withState("test-state")
                .withVersion("test-version");
        return testAppSummary;
    }

    private static SpaceServiceSummary getTestSpaceServiceSummary() {
        SpaceServiceSummary.Plan.Service testService = new SpaceServiceSummary.Plan.Service()
                .withId("test-service-id-1")
                .withLabel("test-service-label")
                .withProvider("test-service-provider")
                .withVersion("test-service-version");

        SpaceServiceSummary.Plan testPlan = new SpaceServiceSummary.Plan()
                .withId("test-plan-id")
                .withName("test-plan-name")
                .withService(testService);

        return new SpaceServiceSummary()
                .withBoundAppCount(1234)
                .withDashboardUrl("test-dashboard-url")
                .withId("test-service-id-2")
                .withLastOperation("test-last-operation")
                .withName("test-service-name")
                .withServicePlan(testPlan);
    }

    private static SpaceApplicationSummary.Route getTestRoute() {
        SpaceApplicationSummary.Route.Domain testDomain = new SpaceApplicationSummary.Route.Domain()
                .withId("test-domain-id")
                .withName("test-domain-name");

        return new SpaceApplicationSummary.Route()
                .withDomain(testDomain)
                .withHost("test-host")
                .withId("test-route-id");
    }

    private static boolean routeIsElementOf(SpaceApplicationSummary.Route r, Set<SpaceApplicationSummary.Route> rset) {
        for (SpaceApplicationSummary.Route rel : rset) {
            if (equivalentRoute(r, rel)) return true;
        }
        return false;
    }

    private static boolean routesContainsAll(Set<SpaceApplicationSummary.Route> rset,
                                      Set<SpaceApplicationSummary.Route> rsubset) {
        for (SpaceApplicationSummary.Route r : rsubset) {
            if (!routeIsElementOf(r, rset))
                return false;
        }
        return true;
    }

    private static <E> Set<E> setOf(E e) {
        return (e == null) ? Collections.emptySet() : setOf(Collections.singletonList(e));
    }

    private static <E> Set<E> setOf(Collection<E> c) {
        return (c == null) ? Collections.emptySet() : new HashSet<>(c);
    }
}
