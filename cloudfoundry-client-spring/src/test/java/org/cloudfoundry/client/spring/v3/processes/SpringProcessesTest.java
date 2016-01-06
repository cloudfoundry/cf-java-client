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

package org.cloudfoundry.client.spring.v3.processes;

import org.cloudfoundry.client.spring.AbstractApiTest;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.processes.DeleteProcessInstanceRequest;
import org.cloudfoundry.client.v3.processes.GetProcessRequest;
import org.cloudfoundry.client.v3.processes.GetProcessResponse;
import org.cloudfoundry.client.v3.processes.ListProcessesRequest;
import org.cloudfoundry.client.v3.processes.ListProcessesResponse;
import org.cloudfoundry.client.v3.processes.ScaleProcessRequest;
import org.cloudfoundry.client.v3.processes.ScaleProcessResponse;
import org.cloudfoundry.client.v3.processes.UpdateProcessRequest;
import org.cloudfoundry.client.v3.processes.UpdateProcessResponse;
import org.reactivestreams.Publisher;

import static org.cloudfoundry.client.v3.PaginatedResponse.Pagination;
import static org.cloudfoundry.client.v3.processes.ListProcessesResponse.Resource;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

public final class SpringProcessesTest {

    public static final class DeleteInstance extends AbstractApiTest<DeleteProcessInstanceRequest, Void> {

        private final SpringProcesses processes = new SpringProcesses(this.restTemplate, this.root, this.processorGroup);


        @Override
        protected DeleteProcessInstanceRequest getInvalidRequest() {
            return DeleteProcessInstanceRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(DELETE).path("/v3/processes/test-id/instances/test-index")
                    .status(NO_CONTENT);
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected DeleteProcessInstanceRequest getValidRequest() {
            return DeleteProcessInstanceRequest.builder()
                    .id("test-id")
                    .index("test-index")
                    .build();
        }

        @Override
        protected Publisher<Void> invoke(DeleteProcessInstanceRequest request) {
            return this.processes.deleteInstance(request);
        }

    }

    public static final class Get extends AbstractApiTest<GetProcessRequest, GetProcessResponse> {

        private final SpringProcesses processes = new SpringProcesses(this.restTemplate, this.root, this.processorGroup);

        @Override
        protected GetProcessRequest getInvalidRequest() {
            return GetProcessRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(GET).path("/v3/processes/test-id")
                    .status(OK)
                    .responsePayload("v3/processes/GET_{id}_response.json");
        }

        @Override
        protected GetProcessResponse getResponse() {
            return GetProcessResponse.builder()
                    .id("07063514-b0ca-4e58-adbb-8e8bd7eebd64")
                    .type("web")
                    .instances(1)
                    .memoryInMb(1024)
                    .diskInMb(1024)
                    .createdAt("2015-07-27T22:43:31Z")
                    .updatedAt("2015-07-27T22:43:31Z")
                    .link("self", Link.builder()
                            .href("/v3/processes/07063514-b0ca-4e58-adbb-8e8bd7eebd64")
                            .build())
                    .link("scale", Link.builder()
                            .href("/v3/processes/07063514-b0ca-4e58-adbb-8e8bd7eebd64/scale")
                            .method("PUT")
                            .build())
                    .link("app", Link.builder()
                            .href("/v3/apps/")
                            .build())
                    .link("space", Link.builder()
                            .href("/v2/spaces/a99fa535-dfa7-4edf-9b18-5cadc80acb0f")
                            .build())
                    .build();
        }

        @Override
        protected GetProcessRequest getValidRequest() {
            return GetProcessRequest.builder()
                    .id("test-id")
                    .build();
        }

        @Override
        protected Publisher<GetProcessResponse> invoke(GetProcessRequest request) {
            return this.processes.get(request);
        }

    }

    public static final class List extends AbstractApiTest<ListProcessesRequest, ListProcessesResponse> {

        private final SpringProcesses processes = new SpringProcesses(this.restTemplate, this.root, this.processorGroup);

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
                    .responsePayload("v3/processes/GET_response.json");
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
                            .id("fdfa71c4-5e0e-4f68-adb6-82fc250cd233")
                            .type("web")
                            .instances(1)
                            .memoryInMb(1024)
                            .diskInMb(1024)
                            .createdAt("2015-07-27T22:43:31Z")
                            .updatedAt("2015-07-27T22:43:31Z")
                            .link("self", Link.builder()
                                    .href("/v3/processes/fdfa71c4-5e0e-4f68-adb6-82fc250cd233")
                                    .build())
                            .link("scale", Link.builder()
                                    .href("/v3/processes/fdfa71c4-5e0e-4f68-adb6-82fc250cd233/scale")
                                    .method("PUT")
                                    .build())
                            .link("app", Link.builder()
                                    .href("/v3/apps/guid-e7b136ef-0e53-4421-a34e-f7d5bcc3508e")
                                    .build())
                            .link("space", Link.builder()
                                    .href("/v2/spaces/176b0be5-f742-4db3-a0a2-6c178351052e")
                                    .build())
                            .build())
                    .resource(Resource.builder()
                            .id("491ef052-92a1-4c82-9cb9-7cf840a79eed")
                            .type("web")
                            .instances(1)
                            .memoryInMb(1024)
                            .diskInMb(1024)
                            .createdAt("2015-07-27T22:43:31Z")
                            .updatedAt("2015-07-27T22:43:31Z")
                            .link("self", Link.builder()
                                    .href("/v3/processes/491ef052-92a1-4c82-9cb9-7cf840a79eed")
                                    .build())
                            .link("scale", Link.builder()
                                    .href("/v3/processes/491ef052-92a1-4c82-9cb9-7cf840a79eed/scale")
                                    .method("PUT")
                                    .build())
                            .link("app", Link.builder()
                                    .href("/v3/apps/")
                                    .build())
                            .link("space", Link.builder()
                                    .href("/v2/spaces/176b0be5-f742-4db3-a0a2-6c178351052e")
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
        protected Publisher<ListProcessesResponse> invoke(ListProcessesRequest request) {
            return this.processes.list(request);
        }

    }

    public static final class Scale extends AbstractApiTest<ScaleProcessRequest, ScaleProcessResponse> {

        private final SpringProcesses processes = new SpringProcesses(this.restTemplate, this.root, this.processorGroup);

        @Override
        protected ScaleProcessRequest getInvalidRequest() {
            return ScaleProcessRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(PUT).path("/v3/processes/test-id/scale")
                    .requestPayload("v3/processes/PUT_{id}_scale_request.json")
                    .status(OK)
                    .responsePayload("v3/processes/PUT_{id}_scale_response.json");
        }

        @Override
        protected ScaleProcessResponse getResponse() {
            return ScaleProcessResponse.builder()
                    .id("1dbdf1dc-ec61-4ade-96bf-4e148092b2e8")
                    .type("web")
                    .instances(3)
                    .memoryInMb(100)
                    .diskInMb(100)
                    .createdAt("2015-07-27T22:43:31Z")
                    .updatedAt("2015-07-27T22:43:32Z")
                    .link("self", Link.builder()
                            .href("/v3/processes/1dbdf1dc-ec61-4ade-96bf-4e148092b2e8")
                            .build())
                    .link("scale", Link.builder()
                            .href("/v3/processes/1dbdf1dc-ec61-4ade-96bf-4e148092b2e8/scale")
                            .method("PUT")
                            .build())
                    .link("app", Link.builder()
                            .href("/v3/apps/guid-1ee4b2f6-a4b6-4125-97b6-0e7997e04643")
                            .build())
                    .link("space", Link.builder()
                            .href("/v2/spaces/bb0d102f-1218-48ee-bb04-377f2743d433")
                            .build())
                    .build();
        }

        @Override
        protected ScaleProcessRequest getValidRequest() {
            return ScaleProcessRequest.builder()
                    .diskInMb(100)
                    .id("test-id")
                    .instances(3)
                    .memoryInMb(100)
                    .build();
        }

        @Override
        protected Publisher<ScaleProcessResponse> invoke(ScaleProcessRequest request) {
            return this.processes.scale(request);
        }
    }

    public static final class Update extends AbstractApiTest<UpdateProcessRequest, UpdateProcessResponse> {

        private final SpringProcesses processes = new SpringProcesses(this.restTemplate, this.root, this.processorGroup);

        @Override
        protected UpdateProcessRequest getInvalidRequest() {
            return UpdateProcessRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(PATCH).path("/v3/processes/test-id")
                    .requestPayload("v3/processes/PATCH_{id}_request.json")
                    .status(OK)
                    .responsePayload("v3/processes/PATCH_{id}_response.json");
        }

        @Override
        protected UpdateProcessResponse getResponse() {
            return UpdateProcessResponse.builder()
                    .id("92d5b770-32ed-4f5c-8def-b1f2348447fb")
                    .type("web")
                    .command("X")
                    .instances(1)
                    .memoryInMb(1024)
                    .diskInMb(1024)
                    .createdAt("2015-07-27T22:43:32Z")
                    .updatedAt("2015-07-27T22:43:32Z")
                    .link("self", Link.builder()
                            .href("/v3/processes/92d5b770-32ed-4f5c-8def-b1f2348447fb")
                            .build())
                    .link("scale", Link.builder()
                            .href("/v3/processes/92d5b770-32ed-4f5c-8def-b1f2348447fb/scale")
                            .method("PUT")
                            .build())
                    .link("app", Link.builder()
                            .href("/v3/apps/")
                            .build())
                    .link("space", Link.builder()
                            .href("/v2/spaces/b7f8b3b1-3210-4151-a356-c26f31289064")
                            .build())
                    .build();
        }

        @Override
        protected UpdateProcessRequest getValidRequest() {
            return UpdateProcessRequest.builder()
                    .id("test-id")
                    .command("test-command")
                    .build();
        }

        @Override
        protected Publisher<UpdateProcessResponse> invoke(UpdateProcessRequest request) {
            return this.processes.update(request);
        }
    }

}
