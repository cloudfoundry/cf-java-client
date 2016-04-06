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

package org.cloudfoundry.spring.client.v2.buildpacks;

import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.buildpacks.BuildpackEntity;
import org.cloudfoundry.client.v2.buildpacks.BuildpackResource;
import org.cloudfoundry.client.v2.buildpacks.CreateBuildpackRequest;
import org.cloudfoundry.client.v2.buildpacks.CreateBuildpackResponse;
import org.cloudfoundry.client.v2.buildpacks.DeleteBuildpackRequest;
import org.cloudfoundry.client.v2.buildpacks.DeleteBuildpackResponse;
import org.cloudfoundry.client.v2.buildpacks.GetBuildpackRequest;
import org.cloudfoundry.client.v2.buildpacks.GetBuildpackResponse;
import org.cloudfoundry.client.v2.buildpacks.ListBuildpacksRequest;
import org.cloudfoundry.client.v2.buildpacks.ListBuildpacksResponse;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.cloudfoundry.spring.AbstractApiTest;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

public final class SpringBuildpacksTest {

    public static final class Create extends AbstractApiTest<CreateBuildpackRequest, CreateBuildpackResponse> {

        private SpringBuildpacks buildpacks = new SpringBuildpacks(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected CreateBuildpackRequest getInvalidRequest() {
            return CreateBuildpackRequest.builder().build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(POST).path("/v2/buildpacks")
                .requestPayload("fixtures/client/v2/buildpacks/POST_request.json")
                .status(CREATED)
                .responsePayload("fixtures/client/v2/buildpacks/POST_response.json");
        }

        @Override
        protected CreateBuildpackResponse getResponse() {
            return CreateBuildpackResponse.builder()
                .metadata(Resource.Metadata.builder()
                    .createdAt("2016-03-17T21:41:28Z")
                    .id("9c38753c-960c-44aa-ac46-37ad61b87e35")
                    .url("/v2/buildpacks/9c38753c-960c-44aa-ac46-37ad61b87e35")
                    .build()
                )
                .entity(BuildpackEntity.builder()
                    .enabled(true)
                    .locked(false)
                    .name("Golang_buildpack")
                    .position(1)
                    .build())
                .build();
        }

        @Override
        protected CreateBuildpackRequest getValidRequest() throws Exception {
            return CreateBuildpackRequest.builder()
                .name("Golang_buildpack")
                .build();
        }

        @Override
        protected Mono<CreateBuildpackResponse> invoke(CreateBuildpackRequest request) {
            return this.buildpacks.create(request);
        }

    }

    public static final class Delete extends AbstractApiTest<DeleteBuildpackRequest, DeleteBuildpackResponse> {

        private SpringBuildpacks buildpacks = new SpringBuildpacks(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected DeleteBuildpackRequest getInvalidRequest() {
            return DeleteBuildpackRequest.builder().build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(DELETE).path("/v2/buildpacks/test-buildpack-id?async=true")
                .status(OK)
                .responsePayload("fixtures/client/v2/buildpacks/DELETE_{id}_response.json");
        }

        @Override
        protected DeleteBuildpackResponse getResponse() {
            return DeleteBuildpackResponse.builder()
                .metadata(Resource.Metadata.builder()
                    .createdAt("2015-07-27T22:43:34Z")
                    .id("c900719e-c70a-4c75-9e6a-9535f118acc3")
                    .url("/v2/jobs/c900719e-c70a-4c75-9e6a-9535f118acc3")
                    .build())
                .entity(JobEntity.builder()
                    .id("c900719e-c70a-4c75-9e6a-9535f118acc3")
                    .status("queued")
                    .build())
                .build();
        }

        @Override
        protected DeleteBuildpackRequest getValidRequest() throws Exception {
            return DeleteBuildpackRequest.builder()
                .async(true)
                .buildpackId("test-buildpack-id")
                .build();
        }

        @Override
        protected Mono<DeleteBuildpackResponse> invoke(DeleteBuildpackRequest request) {
            return this.buildpacks.delete(request);
        }

    }

    public static final class Get extends AbstractApiTest<GetBuildpackRequest, GetBuildpackResponse> {

        private SpringBuildpacks buildpacks = new SpringBuildpacks(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected GetBuildpackRequest getInvalidRequest() {
            return GetBuildpackRequest.builder().build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/buildpacks/test-buildpack-id")
                .status(OK)
                .responsePayload("fixtures/client/v2/buildpacks/GET_{id}_response.json");
        }

        @Override
        protected GetBuildpackResponse getResponse() {
            return GetBuildpackResponse.builder()
                .metadata(Resource.Metadata.builder()
                    .createdAt("2016-03-17T21:41:28Z")
                    .id("35d3fa06-08db-4b9e-b2a7-58724a179687")
                    .url("/v2/buildpacks/35d3fa06-08db-4b9e-b2a7-58724a179687")
                    .build()
                )
                .entity(BuildpackEntity.builder()
                    .enabled(true)
                    .filename("name-2302")
                    .locked(false)
                    .name("name_1")
                    .position(1)
                    .build())
                .build();
        }

        @Override
        protected GetBuildpackRequest getValidRequest() throws Exception {
            return GetBuildpackRequest.builder()
                .buildpackId("test-buildpack-id")
                .build();
        }

        @Override
        protected Mono<GetBuildpackResponse> invoke(GetBuildpackRequest request) {
            return this.buildpacks.get(request);
        }
    }

    public static final class List extends AbstractApiTest<ListBuildpacksRequest, ListBuildpacksResponse> {

        private SpringBuildpacks buildpacks = new SpringBuildpacks(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListBuildpacksRequest getInvalidRequest() {
            return null;
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/buildpacks?q=name%20IN%20test-name&page=-1")
                .status(OK)
                .responsePayload("fixtures/client/v2/buildpacks/GET_response.json");
        }

        @Override
        protected ListBuildpacksResponse getResponse() {
            return ListBuildpacksResponse.builder()
                .totalResults(3)
                .totalPages(1)
                .resource(BuildpackResource.builder()
                    .metadata(Resource.Metadata.builder()
                        .id("45203d32-475b-4d55-9d34-3ffc935edd49")
                        .url("/v2/buildpacks/45203d32-475b-4d55-9d34-3ffc935edd49")
                        .createdAt("2016-03-17T21:41:28Z")
                        .build())
                    .entity(BuildpackEntity.builder()
                        .enabled(true)
                        .filename("name-2308")
                        .locked(false)
                        .name("name_1")
                        .position(1)
                        .build())
                    .build())
                .resource(BuildpackResource.builder()
                    .metadata(Resource.Metadata.builder()
                        .id("1aeb95ef-7058-495c-b260-dea2e8efb976")
                        .url("/v2/buildpacks/1aeb95ef-7058-495c-b260-dea2e8efb976")
                        .createdAt("2016-03-17T21:41:28Z")
                        .build())
                    .entity(BuildpackEntity.builder()
                        .enabled(true)
                        .filename("name-2309")
                        .locked(false)
                        .name("name_2")
                        .position(2)
                        .build())
                    .build())
                .resource(BuildpackResource.builder()
                    .metadata(Resource.Metadata.builder()
                        .id("4dd0046a-7a54-4f57-a31f-06d7e57eb463")
                        .url("/v2/buildpacks/4dd0046a-7a54-4f57-a31f-06d7e57eb463")
                        .createdAt("2016-03-17T21:41:28Z")
                        .build())
                    .entity(BuildpackEntity.builder()
                        .enabled(true)
                        .filename("name-2310")
                        .locked(false)
                        .name("name_3")
                        .position(3)
                        .build())
                    .build())
                .build();
        }

        @Override
        protected ListBuildpacksRequest getValidRequest() throws Exception {
            return ListBuildpacksRequest.builder()
                .name("test-name")
                .page(-1)
                .build();
        }

        @Override
        protected Mono<ListBuildpacksResponse> invoke(ListBuildpacksRequest request) {
            return this.buildpacks.list(request);
        }

    }

}
