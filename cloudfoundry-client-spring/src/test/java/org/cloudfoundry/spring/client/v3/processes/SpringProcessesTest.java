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

package org.cloudfoundry.spring.client.v3.processes;

import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.processes.AbstractProcessDetailedStatistics.PortMapping;
import org.cloudfoundry.client.v3.processes.GetProcessDetailedStatisticsRequest;
import org.cloudfoundry.client.v3.processes.GetProcessDetailedStatisticsResponse;
import org.cloudfoundry.client.v3.processes.GetProcessRequest;
import org.cloudfoundry.client.v3.processes.GetProcessResponse;
import org.cloudfoundry.client.v3.processes.ListProcessesRequest;
import org.cloudfoundry.client.v3.processes.ListProcessesResponse;
import org.cloudfoundry.client.v3.processes.ProcessUsage;
import org.cloudfoundry.client.v3.processes.ScaleProcessRequest;
import org.cloudfoundry.client.v3.processes.ScaleProcessResponse;
import org.cloudfoundry.client.v3.processes.TerminateProcessInstanceRequest;
import org.cloudfoundry.client.v3.processes.UpdateProcessRequest;
import org.cloudfoundry.client.v3.processes.UpdateProcessResponse;
import org.cloudfoundry.spring.AbstractApiTest;
import reactor.core.publisher.Mono;

import static org.cloudfoundry.client.v3.PaginatedResponse.Pagination;
import static org.cloudfoundry.client.v3.processes.ListProcessesResponse.Resource;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

public final class SpringProcessesTest {

    public static final class DeleteInstance extends AbstractApiTest<TerminateProcessInstanceRequest, Void> {

        private final SpringProcesses processes = new SpringProcesses(this.restTemplate, this.root, PROCESSOR_GROUP);


        @Override
        protected TerminateProcessInstanceRequest getInvalidRequest() {
            return TerminateProcessInstanceRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(DELETE).path("/v3/processes/test-process-id/instances/test-index")
                .status(NO_CONTENT);
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected TerminateProcessInstanceRequest getValidRequest() {
            return TerminateProcessInstanceRequest.builder()
                .processId("test-process-id")
                .index("test-index")
                .build();
        }

        @Override
        protected Mono<Void> invoke(TerminateProcessInstanceRequest request) {
            return this.processes.terminateInstance(request);
        }

    }

    public static final class Get extends AbstractApiTest<GetProcessRequest, GetProcessResponse> {

        private final SpringProcesses processes = new SpringProcesses(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected GetProcessRequest getInvalidRequest() {
            return GetProcessRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v3/processes/test-process-id")
                .status(OK)
                .responsePayload("fixtures/client/v3/processes/GET_{id}_response.json");
        }

        @Override
        protected GetProcessResponse getResponse() {
            return GetProcessResponse.builder()
                .id("6a901b7c-9417-4dc1-8189-d3234aa0ab82")
                .type("web")
                .command("rackup")
                .instances(5)
                .memoryInMb(256)
                .diskInMb(1_024)
                .port(8080)
                .createdAt("2016-03-23T18:48:22Z")
                .updatedAt("2016-03-23T18:48:42Z")
                .link("self", Link.builder()
                    .href("/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82")
                    .build())
                .link("scale", Link.builder()
                    .href("/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82/scale")
                    .method("PUT")
                    .build())
                .link("app", Link.builder()
                    .href("/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5")
                    .build())
                .link("space", Link.builder()
                    .href("/v2/spaces/2f35885d-0c9d-4423-83ad-fd05066f8576")
                    .build())
                .build();
        }

        @Override
        protected GetProcessRequest getValidRequest() {
            return GetProcessRequest.builder()
                .processId("test-process-id")
                .build();
        }

        @Override
        protected Mono<GetProcessResponse> invoke(GetProcessRequest request) {
            return this.processes.get(request);
        }

    }

    public static final class GetDetailedProcessStatistics extends AbstractApiTest<GetProcessDetailedStatisticsRequest, GetProcessDetailedStatisticsResponse> {

        private final SpringProcesses processes = new SpringProcesses(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected GetProcessDetailedStatisticsRequest getInvalidRequest() {
            return GetProcessDetailedStatisticsRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v3/processes/test-id/stats")
                .status(OK)
                .responsePayload("fixtures/client/v3/processes/GET_{id}_stats_response.json");
        }

        @Override
        protected GetProcessDetailedStatisticsResponse getResponse() {
            return GetProcessDetailedStatisticsResponse.builder()
                .resource(GetProcessDetailedStatisticsResponse.Resource.builder()
                    .type("web")
                    .index(0)
                    .state("RUNNING")
                    .usage(ProcessUsage.builder()
                        .time("2016-03-23T23:17:30.476314154Z")
                        .cpu(0.00038711029163348665)
                        .memory(19177472L)
                        .disk(69705728L)
                        .build())
                    .host("10.244.16.10")
                    .instancePort(PortMapping.builder()
                        .external(64546)
                        .internal(8080)
                        .build())
                    .uptime(9042L)
                    .memoryQuota(268435456L)
                    .diskQuota(1073741824L)
                    .fdsQuota(16384)
                    .build())
                .build();
        }

        @Override
        protected GetProcessDetailedStatisticsRequest getValidRequest() throws Exception {
            return GetProcessDetailedStatisticsRequest.builder()
                .processId("test-id")
                .build();
        }

        @Override
        protected Mono<GetProcessDetailedStatisticsResponse> invoke(GetProcessDetailedStatisticsRequest request) {
            return this.processes.getDetailedStatistics(request);
        }

    }

    public static final class List extends AbstractApiTest<ListProcessesRequest, ListProcessesResponse> {

        private final SpringProcesses processes = new SpringProcesses(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListProcessesRequest getInvalidRequest() {
            return ListProcessesRequest.builder()
                .page(-1)
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v3/processes?page=1&per_page=2")
                .status(OK)
                .responsePayload("fixtures/client/v3/processes/GET_response.json");
        }

        @Override
        protected ListProcessesResponse getResponse() {
            return ListProcessesResponse.builder()
                .pagination(Pagination.builder()
                    .totalResults(3)
                    .first(Link.builder()
                        .href("/v3/processes?page=1&per_page=2")
                        .build())
                    .last(Link.builder()
                        .href("/v3/processes?page=2&per_page=2")
                        .build())
                    .next(Link.builder()
                        .href("/v3/processes?page=2&per_page=2")
                        .build())
                    .build())
                .resource(Resource.builder()
                    .id("6a901b7c-9417-4dc1-8189-d3234aa0ab82")
                    .type("web")
                    .command("rackup")
                    .instances(5)
                    .memoryInMb(256)
                    .diskInMb(1_024)
                    .port(8080)
                    .createdAt("2016-03-23T18:48:22Z")
                    .updatedAt("2016-03-23T18:48:42Z")
                    .link("self", Link.builder()
                        .href("/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82")
                        .build())
                    .link("scale", Link.builder()
                        .href("/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82/scale")
                        .method("PUT")
                        .build())
                    .link("app", Link.builder()
                        .href("/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5")
                        .build())
                    .link("space", Link.builder()
                        .href("/v2/spaces/2f35885d-0c9d-4423-83ad-fd05066f8576")
                        .build())
                    .build())
                .resource(Resource.builder()
                    .id("3fccacd9-4b02-4b96-8d02-8e865865e9eb")
                    .type("worker")
                    .command("bundle exec rake worker")
                    .instances(1)
                    .memoryInMb(256)
                    .diskInMb(1_024)
                    .createdAt("2016-03-23T18:48:22Z")
                    .updatedAt("2016-03-23T18:48:42Z")
                    .link("self", Link.builder()
                        .href("/v3/processes/3fccacd9-4b02-4b96-8d02-8e865865e9eb")
                        .build())
                    .link("scale", Link.builder()
                        .href("/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82/scale")
                        .method("PUT")
                        .build())
                    .link("app", Link.builder()
                        .href("/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5")
                        .build())
                    .link("space", Link.builder()
                        .href("/v2/spaces/2f35885d-0c9d-4423-83ad-fd05066f8576")
                        .build())
                    .build())
                .build();
        }

        @Override
        protected ListProcessesRequest getValidRequest() {
            return ListProcessesRequest.builder()
                .page(1)
                .perPage(2)
                .build();
        }

        @Override
        protected Mono<ListProcessesResponse> invoke(ListProcessesRequest request) {
            return this.processes.list(request);
        }

    }

    public static final class Scale extends AbstractApiTest<ScaleProcessRequest, ScaleProcessResponse> {

        private final SpringProcesses processes = new SpringProcesses(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ScaleProcessRequest getInvalidRequest() {
            return ScaleProcessRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(PUT).path("/v3/processes/test-process-id/scale")
                .requestPayload("fixtures/client/v3/processes/PUT_{id}_scale_request.json")
                .status(OK)
                .responsePayload("fixtures/client/v3/processes/PUT_{id}_scale_response.json");
        }

        @Override
        protected ScaleProcessResponse getResponse() {
            return ScaleProcessResponse.builder()
                .id("6a901b7c-9417-4dc1-8189-d3234aa0ab82")
                .type("web")
                .command("rackup")
                .instances(5)
                .memoryInMb(256)
                .diskInMb(1_024)
                .port(8080)
                .createdAt("2016-03-23T18:48:22Z")
                .updatedAt("2016-03-23T18:48:42Z")
                .link("self", Link.builder()
                    .href("/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82")
                    .build())
                .link("scale", Link.builder()
                    .href("/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82/scale")
                    .method("PUT")
                    .build())
                .link("app", Link.builder()
                    .href("/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5")
                    .build())
                .link("space", Link.builder()
                    .href("/v2/spaces/2f35885d-0c9d-4423-83ad-fd05066f8576")
                    .build())
                .build();
        }

        @Override
        protected ScaleProcessRequest getValidRequest() {
            return ScaleProcessRequest.builder()
                .processId("test-process-id")
                .instances(5)
                .memoryInMb(256)
                .diskInMb(1_024)
                .build();
        }

        @Override
        protected Mono<ScaleProcessResponse> invoke(ScaleProcessRequest request) {
            return this.processes.scale(request);
        }
    }

    public static final class Update extends AbstractApiTest<UpdateProcessRequest, UpdateProcessResponse> {

        private final SpringProcesses processes = new SpringProcesses(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected UpdateProcessRequest getInvalidRequest() {
            return UpdateProcessRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(PATCH).path("/v3/processes/test-process-id")
                .requestPayload("fixtures/client/v3/processes/PATCH_{id}_request.json")
                .status(OK)
                .responsePayload("fixtures/client/v3/processes/PATCH_{id}_response.json");
        }

        @Override
        protected UpdateProcessResponse getResponse() {
            return UpdateProcessResponse.builder()
                .id("6a901b7c-9417-4dc1-8189-d3234aa0ab82")
                .type("web")
                .command("rackup")
                .instances(5)
                .memoryInMb(256)
                .diskInMb(1_024)
                .port(8080)
                .createdAt("2016-03-23T18:48:22Z")
                .updatedAt("2016-03-23T18:48:42Z")
                .link("self", Link.builder()
                    .href("/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82")
                    .build())
                .link("scale", Link.builder()
                    .href("/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82/scale")
                    .method("PUT")
                    .build())
                .link("app", Link.builder()
                    .href("/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5")
                    .build())
                .link("space", Link.builder()
                    .href("/v2/spaces/2f35885d-0c9d-4423-83ad-fd05066f8576")
                    .build())
                .build();
        }

        @Override
        protected UpdateProcessRequest getValidRequest() {
            return UpdateProcessRequest.builder()
                .processId("test-process-id")
                .command("rackup")
                .build();
        }

        @Override
        protected Mono<UpdateProcessResponse> invoke(UpdateProcessRequest request) {
            return this.processes.update(request);
        }
    }

}
