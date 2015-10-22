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

import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ListSpaceApplicationsResponseTest {

    @Test
    public void test() {
        ApplicationEntity entityIn =
                new ApplicationEntity()
                        .diego(true)
                        .enableSsh(true)
                        .withBuildpack("test-buildpack")
                        .withCommand("test-command")
                        .withDetectedBuildpack("test-detected-buildpack")
                        .withDetectedStartCommand("test-detected-start-command")
                        .withDiskQuota(1234)
                        .withDockerCredentialsJson(Collections.singletonMap("test-docker-credentials",
                                "test-docker-credentials-value"))
                        .withDockerImage("test-docker-image")
                        .withEnvironmentJson(Collections.singletonMap("test-environment-var", "test-environment-value"))
                        .withEventsUrl("test-events-url")
                        .withHealthCheckTimeout(2345)
                        .withHealthCheckType("test-health-check-type")
                        .withInstances(3456)
                        .withMemory(4567)
                        .withName("test-name")
                        .withPackageState("test-package-state")
                        .withPackageUpdatedAt("test-package-updated-at")
                        .withRoutesUrl("test-routes-url")
                        .withServiceBindingsUrl("test-service-bindings-url")
                        .withSpaceId("test-space-id")
                        .withSpaceUrl("test-space-url")
                        .withStackId("test-stack-id")
                        .withStackUrl("test-stack-url")
                        .withStagingFailedDescription("test-staging-failed-description")
                        .withStagingFailedReason("test-staging-failed-reason")
                        .withStagingTaskId("test-staging-task-id")
                        .withState("test-state")
                        .withVersion("test-version");

        ListSpaceApplicationsResponse.Resource resource = new ListSpaceApplicationsResponse.Resource()
                .withEntity(entityIn);

        ListSpaceApplicationsResponse response = new ListSpaceApplicationsResponse()
                .withResource(resource);

        assertEquals(Collections.singletonList(resource), response.getResources());

        ApplicationEntity entityOut = response.getResources().get(0).getEntity();

        assertTrue(entityOut.getDiego());
        assertTrue(entityOut.getEnableSsh());
        assertEquals("test-buildpack", entityOut.getBuildpack());
        assertEquals("test-command", entityOut.getCommand());
        assertEquals("test-detected-start-command", entityOut.getDetectedStartCommand());
        assertEquals(Integer.valueOf(1234), entityOut.getDiskQuota());
        assertEquals(Collections.singletonMap("test-docker-credentials",
                "test-docker-credentials-value"), entityOut.getDockerCredentialsJson());
        assertEquals("test-docker-image", entityOut.getDockerImage());
        assertEquals(Collections.singletonMap("test-environment-var",
                "test-environment-value"), entityOut.getEnvironmentJson());
        assertEquals("test-events-url", entityOut.getEventsUrl());
        assertEquals(Integer.valueOf(2345), entityOut.getHealthCheckTimeout());
        assertEquals("test-health-check-type", entityOut.getHealthCheckType());
        assertEquals(Integer.valueOf(3456), entityOut.getInstances());
        assertEquals(Integer.valueOf(4567), entityOut.getMemory());
        assertEquals("test-name", entityOut.getName());
        assertEquals("test-package-state", entityOut.getPackageState());
        assertEquals("test-package-updated-at", entityOut.getPackageUpdatedAt());
        assertEquals("test-routes-url", entityOut.getRoutesUrl());
        assertEquals("test-service-bindings-url", entityOut.getServiceBindingsUrl());
        assertEquals("test-space-id", entityOut.getSpaceId());
        assertEquals("test-space-url", entityOut.getSpaceUrl());
        assertEquals("test-stack-id", entityOut.getStackId());
        assertEquals("test-stack-url", entityOut.getStackUrl());
        assertEquals("test-staging-failed-description", entityOut.getStagingFailedDescription());
        assertEquals("test-staging-failed-reason", entityOut.getStagingFailedReason());
        assertEquals("test-state", entityOut.getState());
        assertEquals("test-state", entityOut.getState());
    }

}
