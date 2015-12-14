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

package org.cloudfoundry.client.spring.v2.routes;

import org.cloudfoundry.client.RequestValidationException;
import org.cloudfoundry.client.spring.AbstractRestTest;
import org.cloudfoundry.client.v2.CloudFoundryException;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.routes.ListRouteApplicationsRequest;
import org.cloudfoundry.client.v2.routes.ListRouteApplicationsResponse;
import org.junit.Test;
import reactor.rx.Streams;

import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

public final class SpringRoutesTest extends AbstractRestTest {

    private final SpringRoutes routes = new SpringRoutes(this.restTemplate, this.root);

    @Test
    public void listApplications() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/routes/test-id/apps?app_guid=test-app-id&page=-1")
                .status(OK)
                .responsePayload("v2/routes/GET_{id}_apps_response.json"));

        ListRouteApplicationsRequest request = ListRouteApplicationsRequest.builder()
                .appId("test-app-id")
                .id("test-id")
                .page(-1)
                .build();

        ListRouteApplicationsResponse expected = ListRouteApplicationsResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(ApplicationResource.builder()
                        .metadata(Resource.Metadata.builder()
                                .id("f1243da8-e613-490a-8a0e-21ef1bcce952")
                                .url("/v2/apps/f1243da8-e613-490a-8a0e-21ef1bcce952")
                                .createdAt("2015-11-30T23:38:56Z")
                                .updatedAt("2015-11-30T23:38:56Z")
                                .build())
                        .entity(ApplicationEntity.builder()
                                .name("name-2404")
                                .production(false)
                                .spaceId("55f1c5ea-12a5-4128-8f20-606af2a3bce1")
                                .stackId("0ef84d2a-4fdd-43ba-afbc-074a5e19ea66")
                                .memory(1024)
                                .instances(1)
                                .diskQuota(1024)
                                .state("STOPPED")
                                .version("5c7c81b2-941b-48a6-b718-c57c02a5f802")
                                .console(false)
                                .packageState("PENDING")
                                .healthCheckType("port")
                                .diego(false)
                                .packageUpdatedAt("2015-11-30T23:38:56Z")
                                .detectedStartCommand("")
                                .enableSsh(true)
                                .dockerCredentialsJson("redacted_message", "[PRIVATE DATA HIDDEN]")
                                .spaceUrl("/v2/spaces/55f1c5ea-12a5-4128-8f20-606af2a3bce1")
                                .stackUrl("/v2/stacks/0ef84d2a-4fdd-43ba-afbc-074a5e19ea66")
                                .eventsUrl("/v2/apps/f1243da8-e613-490a-8a0e-21ef1bcce952/events")
                                .serviceBindingsUrl("/v2/apps/f1243da8-e613-490a-8a0e-21ef1bcce952/service_bindings")
                                .routesUrl("/v2/apps/f1243da8-e613-490a-8a0e-21ef1bcce952/routes")
                                .build())
                        .build())
                .build();

        ListRouteApplicationsResponse actual = Streams.wrap(this.routes.listApplications(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void listApplicationsError() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/routes/test-id/apps?app_guid=test-app-id&page=-1")
                .errorResponse());

        ListRouteApplicationsRequest request = ListRouteApplicationsRequest.builder()
                .appId("test-app-id")
                .id("test-id")
                .page(-1)
                .build();

        Streams.wrap(this.routes.listApplications(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void listApplicationsInvalidRequest() {
        ListRouteApplicationsRequest request = ListRouteApplicationsRequest.builder()
                .build();

        Streams.wrap(this.routes.listApplications(request)).next().get();
    }


}