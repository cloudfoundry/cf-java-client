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

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

final class SpacesTestUtil {

    static SpaceResource.SpaceEntity entity() {
        return new SpaceResource.SpaceEntity()
                .allowSsh(true)
                .withApplicationEventsUrl("test-application-events-url")
                .withApplicationsUrl("test-applications-url")
                .withAuditorsUrl("test-auditors-url")
                .withDevelopersUrl("test-developers-url")
                .withDomainsUrl("test-domains-url")
                .withEventsUrl("test-events-url")
                .withManagersUrl("test-managers-url")
                .withName("test-name")
                .withOrganizationId("test-organization-id")
                .withOrganizationUrl("test-organization-url")
                .withRoutesUrl("test-routes-url")
                .withSecurityGroupsUrl("test-security-groups-url")
                .withServiceInstancesUrl("test-service-instances-url")
                .withSpaceQuotaDefinitionId("test-space-quota-definition-id");
    }

    static void verify(SpaceResource.SpaceEntity entity) {
        assertTrue(entity.getAllowSsh());
        assertEquals("test-application-events-url", entity.getApplicationEventsUrl());
        assertEquals("test-applications-url", entity.getApplicationsUrl());
        assertEquals("test-auditors-url", entity.getAuditorsUrl());
        assertEquals("test-developers-url", entity.getDevelopersUrl());
        assertEquals("test-domains-url", entity.getDomainsUrl());
        assertEquals("test-events-url", entity.getEventsUrl());
        assertEquals("test-managers-url", entity.getManagersUrl());
        assertEquals("test-name", entity.getName());
        assertEquals("test-organization-id", entity.getOrganizationId());
        assertEquals("test-organization-url", entity.getOrganizationUrl());
        assertEquals("test-routes-url", entity.getRoutesUrl());
        assertEquals("test-security-groups-url", entity.getSecurityGroupsUrl());
        assertEquals("test-service-instances-url", entity.getServiceInstancesUrl());
        assertEquals("test-space-quota-definition-id", entity.getSpaceQuotaDefinitionId());
    }

    static SpaceServiceSummary serviceSummary() {
        return new SpaceServiceSummary()
                .withBoundAppCount(1234)
                .withDashboardUrl("test-dashboard-url")
                .withId("test-service-id-2")
                .withLastOperation("test-last-operation")
                .withName("test-service-name")
                .withServicePlan(new SpaceServiceSummary.Plan()
                        .withId("test-plan-id")
                        .withName("test-plan-name")
                        .withService(new SpaceServiceSummary.Plan.Service()
                                .withId("test-service-id-1")
                                .withLabel("test-service-label")
                                .withProvider("test-service-provider")
                                .withVersion("test-service-version")));

    }

    static void verifyServiceSummary(SpaceServiceSummary serviceSummary) {
        assertEquals(Integer.valueOf(1234), serviceSummary.getBoundAppCount());
        assertEquals("test-dashboard-url", serviceSummary.getDashboardUrl());
        assertEquals("test-service-id-2", serviceSummary.getId());
        assertEquals("test-last-operation", serviceSummary.getLastOperation());
        assertEquals("test-service-name", serviceSummary.getName());

        SpaceServiceSummary.Plan plan = serviceSummary.getServicePlan();
        assertEquals("test-plan-id", plan.getId());
        assertEquals("test-plan-name", plan.getName());

        SpaceServiceSummary.Plan.Service service = plan.getService();
        assertEquals("test-service-id-1", service.getId());
        assertEquals("test-service-label", service.getLabel());
        assertEquals("test-service-provider", service.getProvider());
        assertEquals("test-service-version", service.getVersion());
    }

    static SpaceApplicationSummary applicationSummary() {
        return new SpaceApplicationSummary()
                .withId("test-app-id")
                .withRoute(new SpaceApplicationSummary.Route()
                        .withDomain(new SpaceApplicationSummary.Route.Domain()
                                .withId("test-domain-id")
                                .withName("test-domain-name"))
                        .withHost("test-host")
                        .withId("test-route-id"))
                .withRunningInstances(1234)
                .withServiceCount(2345)
                .withServiceNames(Arrays.asList("test-service-name-1", "test-service-name-2"))
                .withUrls(Arrays.asList("test-url-1", "test-url-2"))

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
    }

    static void verifyApplicationSummary(SpaceApplicationSummary applicationSummary) {
        assertEquals("test-app-id", applicationSummary.getId());

        assertTrue(applicationSummary.getRoutes().size() == 1);
        SpaceApplicationSummary.Route route = applicationSummary.getRoutes().get(0);
        assertEquals("test-domain-id", route.getDomain().getId());
        assertEquals("test-domain-name", route.getDomain().getName());
        assertEquals("test-host", route.getHost());
        assertEquals("test-route-id", route.getId());

        assertEquals(Integer.valueOf(1234), applicationSummary.getRunningInstances());
        assertEquals(Integer.valueOf(2345), applicationSummary.getServiceCount());
        assertEquals(Arrays.asList("test-service-name-1", "test-service-name-2"), applicationSummary.getServiceNames());
        assertEquals(Arrays.asList("test-url-1", "test-url-2"), applicationSummary.getUrls());
        assertEquals("test-buildpack", applicationSummary.getBuildpack());
        assertEquals("test-command", applicationSummary.getCommand());
        assertTrue(applicationSummary.getConsole());
        assertTrue(applicationSummary.getDebug());
        assertEquals("test-detected-buildpack", applicationSummary.getDetectedBuildpack());
        assertEquals("test-detected-start-command", applicationSummary.getDetectedStartCommand());
        assertTrue(applicationSummary.getDiego());
        assertEquals(Integer.valueOf(3456), applicationSummary.getDiskQuota());
        assertEquals(Collections.singletonMap("test-docker-var", "test-docker-value"), applicationSummary
                .getDockerCredentialsJson());
        assertEquals("test-docker-image", applicationSummary.getDockerImage());
        assertTrue(applicationSummary.getEnableSsh());
        assertEquals(Collections.singletonMap("test-env-var", "test-env-value"), applicationSummary
                .getEnvironmentJson());
        assertEquals(Integer.valueOf(4567), applicationSummary.getHealthCheckTimeout());
        assertEquals("test-health-check-type", applicationSummary.getHealthCheckType());
        assertEquals(Integer.valueOf(5678), applicationSummary.getInstances());
        assertEquals(Integer.valueOf(6789), applicationSummary.getMemory());
        assertEquals("test-name", applicationSummary.getName());
        assertEquals("test-package-state", applicationSummary.getPackageState());
        assertEquals("test-package-updated-at", applicationSummary.getPackageUpdatedAt());
        assertTrue(applicationSummary.getProduction());
        assertEquals("test-space-id", applicationSummary.getSpaceId());
        assertEquals("test-stack-id", applicationSummary.getStackId());
        assertEquals("test-staging-failed-description", applicationSummary.getStagingFailedDescription());
        assertEquals("test-staging-failed-reason", applicationSummary.getStagingFailedReason());
        assertEquals("test-staging-task-id", applicationSummary.getStagingTaskId());
        assertEquals("test-state", applicationSummary.getState());
        assertEquals("test-version", applicationSummary.getVersion());
    }
}
