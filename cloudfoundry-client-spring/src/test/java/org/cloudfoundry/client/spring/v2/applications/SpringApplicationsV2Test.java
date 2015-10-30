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

package org.cloudfoundry.client.spring.v2.applications;

import org.cloudfoundry.client.RequestValidationException;
import org.cloudfoundry.client.spring.AbstractRestTest;
import org.cloudfoundry.client.v2.CloudFoundryException;
import org.cloudfoundry.client.v2.applications.SummaryApplicationRequest;
import org.cloudfoundry.client.v2.applications.SummaryApplicationResponse;
import org.cloudfoundry.client.v2.domains.Domain;
import org.cloudfoundry.client.v2.routes.Route;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstance;
import org.junit.Test;
import reactor.rx.Streams;

import static org.cloudfoundry.client.v2.serviceinstances.ServiceInstance.Plan.Service;
import static org.cloudfoundry.client.v2.serviceinstances.ServiceInstance.Plan.builder;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

public final class SpringApplicationsV2Test extends AbstractRestTest {

    private final SpringApplicationsV2 applications = new SpringApplicationsV2(this.restTemplate, this.root);

    @Test
    public void summary() {
        mockRequest(new RequestContext()
                .method(GET).path("/v2/apps/test-id/summary")
                .status(OK)
                .responsePayload("v2/apps/GET_{id}_summary_response.json"));

        SummaryApplicationRequest request = SummaryApplicationRequest.builder()
                .id("test-id")
                .build();

        SummaryApplicationResponse expected = SummaryApplicationResponse.builder()
                .id("f501634a-c6a8-44a7-aafc-88862ec727ba")
                .name("name-2124")
                .route(Route.builder()
                        .id("7b0b080a-c567-48a3-acee-28a2c252e959")
                        .host("host-18")
                        .domain(Domain.builder()
                                .id("011461ec-476b-444d-a048-b0e2a3ff0f30")
                                .name("domain-55.example.com")
                                .build())
                        .build())
                .runningInstances(0)
                .service(ServiceInstance.builder()
                        .id("b74ee576-9eb9-4e7e-8185-0c48db893d97")
                        .name("name-2126")
                        .boundApplicationCount(1)
                        .servicePlan(builder()
                                .id("192be526-c7c0-4899-aa2a-3aca7996ccb0")
                                .name("name-2127")
                                .service(Service.builder()
                                        .id("0e586250-a340-4924-8e2e-f13250f595ce")
                                        .label("label-75")
                                        .build())
                                .build())
                        .build())
                .availableDomain(Domain.builder()
                        .id("011461ec-476b-444d-a048-b0e2a3ff0f30")
                        .name("domain-55.example.com")
                        .owningOrganizationId("aadb707d-bf3b-4a0e-8b22-c06aabad53c4")
                        .build())
                .availableDomain(Domain.builder()
                        .id("7c4e943b-5e9b-44ac-85ce-fcff57134d3b")
                        .name("customer-app-domain1.com")
                        .build())
                .availableDomain(Domain.builder()
                        .id("1477a304-2af6-48d0-9e8f-6e2cdf06a9a2")
                        .name("customer-app-domain2.com")
                        .build())
                .production(false)
                .spaceId("3736ea8c-7ff0-44ba-8ed0-bd60971e8155")
                .stackId("21a9a6e8-127a-41ce-886a-0294d807c5e9")
                .memory(1024)
                .instances(1)
                .diskQuota(1024)
                .state("STOPPED")
                .version("53188475-8b6c-4e13-b321-2955431b27de")
                .console(false)
                .packageState("PENDING")
                .healthCheckType("port")
                .diego(false)
                .packageUpdatedAt("2015-07-27T22:43:29Z")
                .detectedStartCommand("")
                .enableSsh(true)
                .dockerCredentialsJson("redacted_message", "[PRIVATE DATA HIDDEN]")
                .build();

        SummaryApplicationResponse actual = Streams.wrap(this.applications.summary(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void summaryError() {
        mockRequest(new RequestContext()
                .method(GET).path("/v2/apps/test-id/summary")
                .errorResponse());

        SummaryApplicationRequest request = SummaryApplicationRequest.builder()
                .id("test-id")
                .build();

        Streams.wrap(this.applications.summary(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void summaryInvalidRequest() {
        SummaryApplicationRequest request = SummaryApplicationRequest.builder()
                .build();

        Streams.wrap(this.applications.summary(request)).next().poll();
    }

}
