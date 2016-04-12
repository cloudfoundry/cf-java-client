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

package org.cloudfoundry.spring.client.v2.routemappings;

import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.cloudfoundry.client.v2.routemappings.CreateRouteMappingRequest;
import org.cloudfoundry.client.v2.routemappings.CreateRouteMappingResponse;
import org.cloudfoundry.client.v2.routemappings.DeleteRouteMappingRequest;
import org.cloudfoundry.client.v2.routemappings.DeleteRouteMappingResponse;
import org.cloudfoundry.client.v2.routemappings.ListRouteMappingsRequest;
import org.cloudfoundry.client.v2.routemappings.ListRouteMappingsResponse;
import org.cloudfoundry.client.v2.routemappings.RouteMappingEntity;
import org.cloudfoundry.client.v2.routemappings.RouteMappingResource;
import org.cloudfoundry.spring.AbstractApiTest;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

public final class SpringRouteMappingsTest {

    public static final class Create extends AbstractApiTest<CreateRouteMappingRequest, CreateRouteMappingResponse> {

        private final SpringRouteMappings routeMappings = new SpringRouteMappings(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected CreateRouteMappingRequest getInvalidRequest() {
            return CreateRouteMappingRequest.builder().build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(POST).path("/v2/route_mappings")
                .requestPayload("fixtures/client/v2/route_mappings/POST_request.json")
                .status(CREATED)
                .responsePayload("fixtures/client/v2/route_mappings/POST_response.json");
        }

        @Override
        protected CreateRouteMappingResponse getResponse() {
            return CreateRouteMappingResponse.builder()
                .metadata(Resource.Metadata.builder()
                    .createdAt("2016-01-19T19:40:59Z")
                    .id("ca9cdd28-53c4-4b8e-a7e0-1838f69b8f91")
                    .url("/v2/route_mappings/ca9cdd28-53c4-4b8e-a7e0-1838f69b8f91")
                    .build())
                .entity(RouteMappingEntity.builder()
                    .applicationId("d232b485-b035-4d65-9f77-6b867d859de5")
                    .applicationPort(8888)
                    .routeId("c041e8a3-64d0-4beb-bac8-1900e3aedd07")
                    .applicationUrl("/v2/apps/d232b485-b035-4d65-9f77-6b867d859de5")
                    .routeUrl("/v2/routes/c041e8a3-64d0-4beb-bac8-1900e3aedd07")
                    .build())
                .build();
        }

        @Override
        protected CreateRouteMappingRequest getValidRequest() throws Exception {
            return CreateRouteMappingRequest.builder()
                .applicationId("d232b485-b035-4d65-9f77-6b867d859de5")
                .routeId("c041e8a3-64d0-4beb-bac8-1900e3aedd07")
                .applicationPort(8888)
                .build();
        }

        @Override
        protected Mono<CreateRouteMappingResponse> invoke(CreateRouteMappingRequest request) {
            return this.routeMappings.create(request);
        }
    }

    public static final class Delete extends AbstractApiTest<DeleteRouteMappingRequest, DeleteRouteMappingResponse> {

        private final SpringRouteMappings routeMappings = new SpringRouteMappings(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected DeleteRouteMappingRequest getInvalidRequest() {
            return DeleteRouteMappingRequest.builder().build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(DELETE).path("/v2/route_mappings/random-route-mapping-id")
                .status(NO_CONTENT);
        }

        @Override
        protected DeleteRouteMappingResponse getResponse() {
            return null;
        }

        @Override
        protected DeleteRouteMappingRequest getValidRequest() throws Exception {
            return DeleteRouteMappingRequest.builder()
                .routeMappingId("random-route-mapping-id")
                .build();
        }

        @Override
        protected Publisher<DeleteRouteMappingResponse> invoke(DeleteRouteMappingRequest request) {
            return this.routeMappings.delete(request);
        }
    }

    public static final class DeleteAsync extends AbstractApiTest<DeleteRouteMappingRequest, DeleteRouteMappingResponse> {

        private final SpringRouteMappings routeMappings = new SpringRouteMappings(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected DeleteRouteMappingRequest getInvalidRequest() {
            return DeleteRouteMappingRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(DELETE).path("/v2/route_mappings/random-route-mapping-id?async=true")
                .status(ACCEPTED)
                .responsePayload("fixtures/client/v2/route_mappings/DELETE_{id}_async_response.json");
        }

        @Override
        protected DeleteRouteMappingResponse getResponse() {
            return DeleteRouteMappingResponse.builder()
                .metadata(Resource.Metadata.builder()
                    .id("2d9707ba-6f0b-4aef-a3de-fe9bdcf0c9d1")
                    .createdAt("2016-02-02T17:16:31Z")
                    .url("/v2/jobs/2d9707ba-6f0b-4aef-a3de-fe9bdcf0c9d1")
                    .build())
                .entity(JobEntity.builder()
                    .id("2d9707ba-6f0b-4aef-a3de-fe9bdcf0c9d1")
                    .status("queued")
                    .build())
                .build();
        }

        @Override
        protected DeleteRouteMappingRequest getValidRequest() throws Exception {
            return DeleteRouteMappingRequest.builder()
                .async(true)
                .routeMappingId("random-route-mapping-id")
                .build();
        }

        @Override
        protected Mono<DeleteRouteMappingResponse> invoke(DeleteRouteMappingRequest request) {
            return this.routeMappings.delete(request);
        }

    }

    public static final class List extends AbstractApiTest<ListRouteMappingsRequest, ListRouteMappingsResponse> {

        private final SpringRouteMappings routeMappings = new SpringRouteMappings(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListRouteMappingsRequest getInvalidRequest() {
            return null;
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/route_mappings?page=-1")
                .status(OK)
                .responsePayload("fixtures/client/v2/route_mappings/GET_response.json");
        }

        @Override
        protected ListRouteMappingsResponse getResponse() {
            return ListRouteMappingsResponse.builder()
                .totalPages(1)
                .totalResults(1)
                .resource(RouteMappingResource.builder()
                    .metadata(Resource.Metadata.builder()
                        .createdAt("2016-04-06T00:17:40Z")
                        .id("50dedf28-08db-4cdd-9903-0d74f3b8708d")
                        .url("/v2/route_mappings/50dedf28-08db-4cdd-9903-0d74f3b8708d")
                        .build())
                    .entity(RouteMappingEntity.builder()
                        .applicationId("fbfe5df8-5391-4e75-966b-69fe34b7ee5d")
                        .applicationPort(8888)
                        .routeId("b683ae9e-0a54-4445-a2ea-5d78d9f89266")
                        .applicationUrl("/v2/apps/fbfe5df8-5391-4e75-966b-69fe34b7ee5d")
                        .routeUrl("/v2/routes/b683ae9e-0a54-4445-a2ea-5d78d9f89266")
                        .build())
                    .build())
                .build();
        }

        @Override
        protected ListRouteMappingsRequest getValidRequest() throws Exception {
            return ListRouteMappingsRequest.builder()
                .page(-1)
                .build();
        }

        @Override
        protected Mono<ListRouteMappingsResponse> invoke(ListRouteMappingsRequest request) {
            return this.routeMappings.list(request);
        }

    }

}
