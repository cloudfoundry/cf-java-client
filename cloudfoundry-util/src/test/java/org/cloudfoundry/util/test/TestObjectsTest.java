/*
 * Copyright 2013-2016 the original author or authors.
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

package org.cloudfoundry.util.test;

import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationsResponse;
import org.junit.Test;

import static org.cloudfoundry.util.test.TestObjects.fill;
import static org.cloudfoundry.util.test.TestObjects.fillPage;
import static org.junit.Assert.assertEquals;

public final class TestObjectsTest {

    @Test
    public void fillCreate() {

        CreateApplicationRequest actual = fill(CreateApplicationRequest.builder()).build();
        CreateApplicationRequest expected = CreateApplicationRequest.builder()
            .buildpack("test-buildpack")
            .command("test-command")
            .console(true)
            .debug(true)
            .diego(true)
            .diskQuota(1)
            .dockerImage("test-dockerImage")
            .healthCheckTimeout(1)
            .healthCheckType("test-healthCheckType")
            .instances(1)
            .memory(1)
            .name("test-name")
            .production(true)
            .spaceId("test-spaceId")
            .stackId("test-stackId")
            .state("test-state")
            .build();

        assertEquals(expected, actual);
    }

    @Test
    public void fillEntityWithModifier() {

        ApplicationEntity actual = fill(ApplicationEntity.builder(), "1").build();
        ApplicationEntity expected = ApplicationEntity.builder()
            .buildpack("test-1buildpack")
            .command("test-1command")
            .console(true)
            .debug(true)
            .detectedStartCommand("test-1detectedStartCommand")
            .diego(true)
            .diskQuota(1)
            .dockerImage("test-1dockerImage")
            .healthCheckTimeout(1)
            .healthCheckType("test-1healthCheckType")
            .instances(1)
            .memory(1)
            .name("test-1name")
            .production(true)
            .spaceId("test-1spaceId")
            .stackId("test-1stackId")
            .spaceUrl("test-1spaceUrl")
            .stagingFailedDescription("test-1stagingFailedDescription")
            .stagingFailedReason("test-1stagingFailedReason")
            .state("test-1state")
            .detectedBuildpack("test-1detectedBuildpack")
            .enableSsh(true)
            .eventsUrl("test-1eventsUrl")
            .packageState("test-1packageState")
            .packageUpdatedAt("test-1packageUpdatedAt")
            .routesUrl("test-1routesUrl")
            .serviceBindingsUrl("test-1serviceBindingsUrl")
            .spaceUrl("test-1spaceUrl")
            .stackUrl("test-1stackUrl")
            .stagingTaskId("test-1stagingTaskId")
            .version("test-1version")
            .build();

        assertEquals(expected, actual);
    }

    @Test(expected = AssertionError.class)
    public void fillOfPaginated() {
        fill(ListApplicationsResponse.builder());
    }

    @Test(expected = AssertionError.class)
    public void fillPageOfNonPaginated() {
        fillPage(ApplicationEntity.builder());
    }

    @Test
    public void fillRequestPage() {

        ListApplicationsRequest actual = fillPage(ListApplicationsRequest.builder()).build();
        ListApplicationsRequest expected = ListApplicationsRequest.builder()
            .page(1)
            .diego(true)
            .build();

        assertEquals(expected, actual);
    }

    @Test
    public void fillResource() {

        ApplicationResource actual = fill(ApplicationResource.builder()).build();
        ApplicationResource expected = ApplicationResource.builder()
            .entity(fill(ApplicationEntity.builder()).build())
            .metadata(fill(Resource.Metadata.builder()).build())
            .build();

        assertEquals(expected, actual);
    }

    @Test
    public void fillResponsePage() {

        ListApplicationsResponse actual = fillPage(ListApplicationsResponse.builder()).build();
        ListApplicationsResponse expected = ListApplicationsResponse.builder()
            .nextUrl("test-nextUrl")
            .previousUrl("test-previousUrl")
            .totalPages(1)
            .totalResults(1)
            .build();

        assertEquals(expected, actual);
    }

}
