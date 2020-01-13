/*
 * Copyright 2013-2020 the original author or authors.
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

package org.cloudfoundry.reactor.client.v3.processes;

import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.Metadata;
import org.cloudfoundry.client.v3.Pagination;
import org.cloudfoundry.client.v3.processes.Data;
import org.cloudfoundry.client.v3.processes.GetProcessRequest;
import org.cloudfoundry.client.v3.processes.GetProcessResponse;
import org.cloudfoundry.client.v3.processes.GetProcessStatisticsRequest;
import org.cloudfoundry.client.v3.processes.GetProcessStatisticsResponse;
import org.cloudfoundry.client.v3.processes.HealthCheck;
import org.cloudfoundry.client.v3.processes.HealthCheckType;
import org.cloudfoundry.client.v3.processes.ListProcessesRequest;
import org.cloudfoundry.client.v3.processes.ListProcessesResponse;
import org.cloudfoundry.client.v3.processes.PortMapping;
import org.cloudfoundry.client.v3.processes.ProcessRelationships;
import org.cloudfoundry.client.v3.processes.ProcessResource;
import org.cloudfoundry.client.v3.processes.ProcessState;
import org.cloudfoundry.client.v3.processes.ProcessStatisticsResource;
import org.cloudfoundry.client.v3.processes.ProcessUsage;
import org.cloudfoundry.client.v3.processes.ScaleProcessRequest;
import org.cloudfoundry.client.v3.processes.ScaleProcessResponse;
import org.cloudfoundry.client.v3.processes.TerminateProcessInstanceRequest;
import org.cloudfoundry.client.v3.processes.UpdateProcessRequest;
import org.cloudfoundry.client.v3.processes.UpdateProcessResponse;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.PATCH;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorProcessesTest extends AbstractClientApiTest {

    private final ReactorProcesses processes = new ReactorProcesses(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void deleteInstance() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/processes/test-process-id/instances/test-index")
                .build())
            .response(TestResponse.builder()
                .status(NO_CONTENT)
                .build())
            .build());

        this.processes
            .terminateInstance(TerminateProcessInstanceRequest.builder()
                .processId("test-process-id")
                .index("test-index")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/processes/test-process-id")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/processes/GET_{id}_response.json")
                .build())
            .build());

        this.processes
            .get(GetProcessRequest.builder()
                .processId("test-process-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetProcessResponse.builder()
                .id("6a901b7c-9417-4dc1-8189-d3234aa0ab82")
                .type("web")
                .command("rackup")
                .instances(5)
                .memoryInMb(256)
                .diskInMb(1_024)
                .healthCheck(HealthCheck.builder()
                    .type(HealthCheckType.PORT)
                    .data(Data.builder()
                        .timeout(null)
                        .endpoint(null)
                        .build())
                    .build())
                .metadata(Metadata.builder()
                    .annotations(Collections.emptyMap())
                    .labels(Collections.emptyMap())
                    .build())
                .relationships(ProcessRelationships.builder()
                    .build())
                .createdAt("2016-03-23T18:48:22Z")
                .updatedAt("2016-03-23T18:48:42Z")
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82")
                    .build())
                .link("scale", Link.builder()
                    .href("https://api.example.org/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82/actions/scale")
                    .method("POST")
                    .build())
                .link("app", Link.builder()
                    .href("https://api.example.org/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5")
                    .build())
                .link("space", Link.builder()
                    .href("https://api.example.org/v3/spaces/2f35885d-0c9d-4423-83ad-fd05066f8576")
                    .build())
                .link("stats", Link.builder()
                    .href("https://api.example.org/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82/stats")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getProcessStatistics() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/processes/test-id/stats")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/processes/GET_{id}_stats_response.json")
                .build())
            .build());

        this.processes
            .getStatistics(GetProcessStatisticsRequest.builder()
                .processId("test-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetProcessStatisticsResponse.builder()
                .resource(ProcessStatisticsResource.builder()
                    .type("web")
                    .index(0)
                    .state(ProcessState.RUNNING)
                    .usage(ProcessUsage.builder()
                        .time("2016-03-23T23:17:30.476314154Z")
                        .cpu(0.00038711029163348665)
                        .memory(19177472)
                        .disk(69705728)
                        .build())
                    .host("10.244.16.10")
                    .instancePort(PortMapping.builder()
                        .external(64546)
                        .externalTlsProxyPort(1234)
                        .internal(8080)
                        .internalTlsProxyPort(5678)
                        .build())
                    .uptime(9042)
                    .memoryQuota(268435456)
                    .diskQuota(1073741824)
                    .fileDescriptorQuota(16384)
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/processes")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/processes/GET_response.json")
                .build())
            .build());

        this.processes
            .list(ListProcessesRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNext(ListProcessesResponse.builder()
                .pagination(Pagination.builder()
                    .totalResults(3)
                    .totalPages(2)
                    .first(Link.builder()
                        .href("https://api.example.org/v3/processes?page=1&per_page=2")
                        .build())
                    .last(Link.builder()
                        .href("https://api.example.org/v3/processes?page=2&per_page=2")
                        .build())
                    .next(Link.builder()
                        .href("https://api.example.org/v3/processes?page=2&per_page=2")
                        .build())
                    .build())
                .resource(ProcessResource.builder()
                    .id("6a901b7c-9417-4dc1-8189-d3234aa0ab82")
                    .type("web")
                    .command("[PRIVATE DATA HIDDEN IN LISTS]")
                    .instances(5)
                    .memoryInMb(256)
                    .diskInMb(1_024)
                    .healthCheck(HealthCheck.builder()
                        .type(HealthCheckType.PORT)
                        .data(Data.builder()
                            .timeout(null)
                            .endpoint(null)
                            .build())
                        .build())
                    .metadata(Metadata.builder()
                        .annotations(Collections.emptyMap())
                        .labels(Collections.emptyMap())
                        .build())
                    .relationships(ProcessRelationships.builder()
                        .build())
                    .createdAt("2016-03-23T18:48:22Z")
                    .updatedAt("2016-03-23T18:48:42Z")
                    .link("self", Link.builder()
                        .href("https://api.example.org/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82")
                        .build())
                    .link("scale", Link.builder()
                        .href("https://api.example.org/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82/actions/scale")
                        .method("POST")
                        .build())
                    .link("app", Link.builder()
                        .href("https://api.example.org/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5")
                        .build())
                    .link("space", Link.builder()
                        .href("https://api.example.org/v3/spaces/2f35885d-0c9d-4423-83ad-fd05066f8576")
                        .build())
                    .link("stats", Link.builder()
                        .href("https://api.example.org/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82/stats")
                        .build())
                    .build())
                .resource(ProcessResource.builder()
                    .id("3fccacd9-4b02-4b96-8d02-8e865865e9eb")
                    .type("worker")
                    .command("[PRIVATE DATA HIDDEN IN LISTS]")
                    .instances(1)
                    .memoryInMb(256)
                    .diskInMb(1_024)
                    .healthCheck(HealthCheck.builder()
                        .type(HealthCheckType.PROCESS)
                        .data(Data.builder()
                            .timeout(null)
                            .endpoint(null)
                            .build())
                        .build())
                    .metadata(Metadata.builder()
                        .annotations(Collections.emptyMap())
                        .labels(Collections.emptyMap())
                        .build())
                    .relationships(ProcessRelationships.builder()
                        .build())
                    .createdAt("2016-03-23T18:48:22Z")
                    .updatedAt("2016-03-23T18:48:42Z")
                    .link("self", Link.builder()
                        .href("https://api.example.org/v3/processes/3fccacd9-4b02-4b96-8d02-8e865865e9eb")
                        .build())
                    .link("scale", Link.builder()
                        .href("https://api.example.org/v3/processes/3fccacd9-4b02-4b96-8d02-8e865865e9eb/actions/scale")
                        .method("POST")
                        .build())
                    .link("app", Link.builder()
                        .href("https://api.example.org/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5")
                        .build())
                    .link("space", Link.builder()
                        .href("https://api.example.org/v3/spaces/2f35885d-0c9d-4423-83ad-fd05066f8576")
                        .build())
                    .link("stats", Link.builder()
                        .href("https://api.example.org/v3/processes/3fccacd9-4b02-4b96-8d02-8e865865e9eb/stats")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void scale() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/processes/test-process-id/actions/scale")
                .payload("fixtures/client/v3/processes/POST_{id}_actions_scale_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/processes/POST_{id}_actions_scale_response.json")
                .build())
            .build());

        this.processes
            .scale(ScaleProcessRequest.builder()
                .processId("test-process-id")
                .instances(5)
                .memoryInMb(256)
                .diskInMb(1_024)
                .build())
            .as(StepVerifier::create)
            .expectNext(ScaleProcessResponse.builder()
                .id("6a901b7c-9417-4dc1-8189-d3234aa0ab82")
                .type("web")
                .command("rackup")
                .instances(5)
                .memoryInMb(256)
                .diskInMb(1_024)
                .healthCheck(HealthCheck.builder()
                    .type(HealthCheckType.PORT)
                    .data(Data.builder()
                        .timeout(null)
                        .endpoint(null)
                        .build())
                    .build())
                .metadata(Metadata.builder()
                    .annotations(Collections.emptyMap())
                    .labels(Collections.emptyMap())
                    .build())
                .relationships(ProcessRelationships.builder()
                    .build())
                .createdAt("2016-03-23T18:48:22Z")
                .updatedAt("2016-03-23T18:48:42Z")
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82")
                    .build())
                .link("scale", Link.builder()
                    .href("https://api.example.org/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82/actions/scale")
                    .method("POST")
                    .build())
                .link("app", Link.builder()
                    .href("https://api.example.org/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5")
                    .build())
                .link("space", Link.builder()
                    .href("https://api.example.org/v3/spaces/2f35885d-0c9d-4423-83ad-fd05066f8576")
                    .build())
                .link("stats", Link.builder()
                    .href("https://api.example.org/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82/stats")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void update() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PATCH).path("/processes/test-process-id")
                .payload("fixtures/client/v3/processes/PATCH_{id}_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/processes/PATCH_{id}_response.json")
                .build())
            .build());

        this.processes
            .update(UpdateProcessRequest.builder()
                .processId("test-process-id")
                .command("rackup")
                .build())
            .as(StepVerifier::create)
            .expectNext(UpdateProcessResponse.builder()
                .id("6a901b7c-9417-4dc1-8189-d3234aa0ab82")
                .type("web")
                .command("rackup")
                .instances(5)
                .memoryInMb(256)
                .diskInMb(1_024)
                .healthCheck(HealthCheck.builder()
                    .type(HealthCheckType.PORT)
                    .data(Data.builder()
                        .timeout(null)
                        .endpoint(null)
                        .build())
                    .build())
                .metadata(Metadata.builder()
                    .annotations(Collections.emptyMap())
                    .labels(Collections.emptyMap())
                    .build())
                .relationships(ProcessRelationships.builder()
                    .build())
                .createdAt("2016-03-23T18:48:22Z")
                .updatedAt("2016-03-23T18:48:42Z")
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82")
                    .build())
                .link("scale", Link.builder()
                    .href("https://api.example.org/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82/actions/scale")
                    .method("POST")
                    .build())
                .link("app", Link.builder()
                    .href("https://api.example.org/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5")
                    .build())
                .link("space", Link.builder()
                    .href("https://api.example.org/v3/spaces/2f35885d-0c9d-4423-83ad-fd05066f8576")
                    .build())
                .link("stats", Link.builder()
                    .href("https://api.example.org/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82/stats")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
