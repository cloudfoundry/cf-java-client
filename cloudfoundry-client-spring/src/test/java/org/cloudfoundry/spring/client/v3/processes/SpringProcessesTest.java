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
                .id("3e06d2c4-69da-4ded-b820-a0a8cf609535")
                .type("web")
                .instances(1)
                .memoryInMb(1_024)
                .diskInMb(1_024)
                .createdAt("2016-01-26T22:20:11Z")
                .updatedAt("2016-01-26T22:20:11Z")
                .link("self", Link.builder()
                    .href("/v3/processes/3e06d2c4-69da-4ded-b820-a0a8cf609535")
                    .build())
                .link("scale", Link.builder()
                    .href("/v3/processes/3e06d2c4-69da-4ded-b820-a0a8cf609535/scale")
                    .method("PUT")
                    .build())
                .link("app", Link.builder()
                    .href("/v3/apps/guid-a5b58fb9-7a41-4fcf-8eef-0cb9cc6fc0e3")
                    .build())
                .link("space", Link.builder()
                    .href("/v2/spaces/fe985301-7722-442c-ba2b-3a81545a74b4")
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
                .pagination(Pagination.builder()
                    .first(Link.builder().href("/v3/processes/8480ec7f-b06c-4df7-92c1-11afc823f967/stats")
                        .build())
                    .last(Link.builder().href("/v3/processes/8480ec7f-b06c-4df7-92c1-11afc823f967/stats")
                        .build())
                    .totalResults(1)
                    .build())
                .resource(GetProcessDetailedStatisticsResponse.Resource.builder()
                    .diskQuota(1073741824L)
                    .fdsQuota(16384)
                    .host("toast")
                    .index(0)
                    .memoryQuota(1073741824L)
                    .port(8080)
                    .state("RUNNING")
                    .type("web")
                    .uptime(1L)
                    .usage(ProcessUsage.builder()
                        .cpu(80.0)
                        .disk(1024L)
                        .memory(128L)
                        .time("2016-01-26 22:20:11 UTC")
                        .build())
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
                    .id("4de53195-876d-4b97-a4f6-79b0b65e9f30")
                    .type("web")
                    .instances(1)
                    .memoryInMb(1_024)
                    .diskInMb(1_024)
                    .createdAt("2016-01-26T22:20:11Z")
                    .updatedAt("2016-01-26T22:20:11Z")
                    .link("self", Link.builder()
                        .href("/v3/processes/4de53195-876d-4b97-a4f6-79b0b65e9f30")
                        .build())
                    .link("scale", Link.builder()
                        .href("/v3/processes/4de53195-876d-4b97-a4f6-79b0b65e9f30/scale")
                        .method("PUT")
                        .build())
                    .link("app", Link.builder()
                        .href("/v3/apps/guid-8fb1c462-d4ca-43b8-9b7b-a3e76158cb06")
                        .build())
                    .link("space", Link.builder()
                        .href("/v2/spaces/fba1181c-5acc-4c6b-96b5-13abaaba7381")
                        .build())
                    .build())
                .resource(Resource.builder()
                    .id("e6020111-0a8c-4515-97f0-120e50f8ab53")
                    .type("web")
                    .instances(1)
                    .memoryInMb(1_024)
                    .diskInMb(1_024)
                    .createdAt("2016-01-26T22:20:11Z")
                    .updatedAt("2016-01-26T22:20:11Z")
                    .link("self", Link.builder()
                        .href("/v3/processes/e6020111-0a8c-4515-97f0-120e50f8ab53")
                        .build())
                    .link("scale", Link.builder()
                        .href("/v3/processes/e6020111-0a8c-4515-97f0-120e50f8ab53/scale")
                        .method("PUT")
                        .build())
                    .link("app", Link.builder()
                        .href("/v3/apps/")
                        .build())
                    .link("space", Link.builder()
                        .href("/v2/spaces/fba1181c-5acc-4c6b-96b5-13abaaba7381")
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
                .id("df7e6396-6f23-47ef-95d5-fe899e1790cb")
                .type("web")
                .instances(3)
                .memoryInMb(100)
                .diskInMb(100)
                .createdAt("2016-01-26T22:20:12Z")
                .updatedAt("2016-01-26T22:20:12Z")
                .link("self", Link.builder()
                    .href("/v3/processes/df7e6396-6f23-47ef-95d5-fe899e1790cb")
                    .build())
                .link("scale", Link.builder()
                    .href("/v3/processes/df7e6396-6f23-47ef-95d5-fe899e1790cb/scale")
                    .method("PUT")
                    .build())
                .link("app", Link.builder()
                    .href("/v3/apps/guid-bdd03d9e-169d-476e-a9d9-50d54b0980b8")
                    .build())
                .link("space", Link.builder()
                    .href("/v2/spaces/fb5dd54a-f441-47bc-8f78-77a028a55614")
                    .build())
                .build();
        }

        @Override
        protected ScaleProcessRequest getValidRequest() {
            return ScaleProcessRequest.builder()
                .diskInMb(100)
                .processId("test-process-id")
                .instances(3)
                .memoryInMb(100)
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
                .id("d416ae86-16a7-4143-844a-6e707919b6ee")
                .type("web")
                .command("X")
                .instances(1)
                .memoryInMb(1_024)
                .diskInMb(1_024)
                .createdAt("2016-01-26T22:20:11Z")
                .updatedAt("2016-01-26T22:20:11Z")
                .link("self", Link.builder()
                    .href("/v3/processes/d416ae86-16a7-4143-844a-6e707919b6ee")
                    .build())
                .link("scale", Link.builder()
                    .href("/v3/processes/d416ae86-16a7-4143-844a-6e707919b6ee/scale")
                    .method("PUT")
                    .build())
                .link("app", Link.builder()
                    .href("/v3/apps/")
                    .build())
                .link("space", Link.builder()
                    .href("/v2/spaces/fc10c41e-ef1b-450d-818e-a117fd21a4c7")
                    .build())
                .build();
        }

        @Override
        protected UpdateProcessRequest getValidRequest() {
            return UpdateProcessRequest.builder()
                .processId("test-process-id")
                .command("test-command")
                .build();
        }

        @Override
        protected Mono<UpdateProcessResponse> invoke(UpdateProcessRequest request) {
            return this.processes.update(request);
        }
    }

}
