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

package org.cloudfoundry.client.spring.v2.serviceinstances;

import org.cloudfoundry.client.spring.AbstractApiTest;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingEntity;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingResource;
import org.cloudfoundry.client.v2.serviceinstances.CreateServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.CreateServiceInstanceResponse;
import org.cloudfoundry.client.v2.serviceinstances.DeleteServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.GetServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.GetServiceInstanceResponse;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstanceServiceBindingsRequest;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstanceServiceBindingsResponse;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstancesRequest;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstancesResponse;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceEntity;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceResource;
import org.cloudfoundry.client.v2.serviceinstances.UpdateServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.UpdateServiceInstanceResponse;
import reactor.Mono;

import static org.cloudfoundry.client.v2.Resource.Metadata;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

public final class SpringServiceInstancesTest {

    public static final class Create extends AbstractApiTest<CreateServiceInstanceRequest, CreateServiceInstanceResponse> {

        private final SpringServiceInstances serviceInstances = new SpringServiceInstances(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected CreateServiceInstanceRequest getInvalidRequest() {
            return CreateServiceInstanceRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(POST).path("v2/service_instances?accepts_incomplete=true")
                    .requestPayload("v2/service_instances/POST_{id}_create_service_instance_request.json")
                    .status(CREATED)
                    .responsePayload("v2/service_instances/POST_{id}_create_service_binding_response.json");
        }

        @Override
        protected CreateServiceInstanceResponse getResponse() {
            return CreateServiceInstanceResponse.builder()
                    .metadata(Resource.Metadata.builder()
                            .createdAt("2015-07-27T22:43:08Z")
                            .id("8b2b3c5e-c1ba-41d0-ac87-08c776cfc25a")
                            .url("/v2/service_instances/8b2b3c5e-c1ba-41d0-ac87-08c776cfc25a")
                            .build())
                    .entity(ServiceInstanceEntity.builder()
                            .name("my-service-instance")
                            .credential("creds-key-356", "creds-val-356")
                            .servicePlanId("2048a369-d2d3-48cf-bcfd-eaf9032fa0ab")
                            .spaceId("86b29f7e-721d-4eb8-b34f-3b1d1eccdf23")
                            .type("managed_service_instance")
                            .lastOperation(ServiceInstanceEntity.LastOperation.builder()
                                    .createdAt("2015-07-27T22:43:08Z")
                                    .updatedAt("2015-07-27T22:43:08Z")
                                    .description("")
                                    .state("in progress")
                                    .type("create")
                                    .build())
                            .tag("accounting")
                            .tag("mongodb")
                            .spaceUrl("/v2/spaces/86b29f7e-721d-4eb8-b34f-3b1d1eccdf23")
                            .servicePlanUrl("/v2/service_plans/2048a369-d2d3-48cf-bcfd-eaf9032fa0ab")
                            .serviceBindingsUrl("/v2/service_instances/8b2b3c5e-c1ba-41d0-ac87-08c776cfc25a/service_bindings")
                            .serviceKeysUrl("/v2/service_instances/8b2b3c5e-c1ba-41d0-ac87-08c776cfc25a/service_keys")
                            .build())
                    .build();
        }

        @Override
        protected CreateServiceInstanceRequest getValidRequest() throws Exception {
            return CreateServiceInstanceRequest.builder()
                    .acceptsIncomplete(true)
                    .name("my-service-instance")
                    .servicePlanId("2048a369-d2d3-48cf-bcfd-eaf9032fa0ab")
                    .spaceId("86b29f7e-721d-4eb8-b34f-3b1d1eccdf23")
                    .parameter("the_service_broker", "wants this object")
                    .tag("accounting")
                    .tag("mongodb")
                    .build();
        }

        @Override
        protected Mono<CreateServiceInstanceResponse> invoke(CreateServiceInstanceRequest request) {
            return this.serviceInstances.create(request);
        }

    }

    public static final class Delete extends AbstractApiTest<DeleteServiceInstanceRequest, Void> {

        private final SpringServiceInstances serviceInstances = new SpringServiceInstances(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected DeleteServiceInstanceRequest getInvalidRequest() {
            return DeleteServiceInstanceRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(DELETE).path("/v2/service_instances/test-id?accepts_incomplete=true&purge=true")
                    .status(NO_CONTENT);
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected DeleteServiceInstanceRequest getValidRequest() {
            return DeleteServiceInstanceRequest.builder()
                    .id("test-id")
                    .acceptsIncomplete(true)
                    .purge(true)
                    .build();
        }

        @Override
        protected Mono<Void> invoke(DeleteServiceInstanceRequest request) {
            return this.serviceInstances.delete(request);
        }

    }

    public static final class Get extends AbstractApiTest<GetServiceInstanceRequest, GetServiceInstanceResponse> {

        private final SpringServiceInstances serviceInstances = new SpringServiceInstances(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected GetServiceInstanceRequest getInvalidRequest() {
            return GetServiceInstanceRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(GET).path("/v2/service_instances/test-id")
                    .status(OK)
                    .responsePayload("v2/service_instances/GET_{id}_response.json");
        }

        @Override
        protected GetServiceInstanceResponse getResponse() {
            return GetServiceInstanceResponse.builder()
                    .metadata(Metadata.builder()
                            .id("24ec15f9-f6c7-434a-8893-51baab8408d8")
                            .url("/v2/service_instances/24ec15f9-f6c7-434a-8893-51baab8408d8")
                            .createdAt("2015-07-27T22:43:08Z")
                            .build())
                    .entity(ServiceInstanceEntity.builder()
                            .name("name-133")
                            .credential("creds-key-72", "creds-val-72")
                            .servicePlanId("2b53255a-8b40-4671-803d-21d3f5d4183a")
                            .spaceId("83b3e705-49fd-4c40-8adf-f5e34f622a19")
                            .type("managed_service_instance")
                            .lastOperation(ServiceInstanceEntity.LastOperation.builder()
                                    .type("create")
                                    .state("succeeded")
                                    .description("service broker-provided description")
                                    .updatedAt("2015-07-27T22:43:08Z")
                                    .createdAt("2015-07-27T22:43:08Z")
                                    .build())
                            .tag("accounting")
                            .tag("mongodb")
                            .spaceUrl("/v2/spaces/83b3e705-49fd-4c40-8adf-f5e34f622a19")
                            .servicePlanUrl("/v2/service_plans/2b53255a-8b40-4671-803d-21d3f5d4183a")
                            .serviceBindingsUrl
                                    ("/v2/service_instances/24ec15f9-f6c7-434a-8893-51baab8408d8/service_bindings")
                            .serviceKeysUrl
                                    ("/v2/service_instances/24ec15f9-f6c7-434a-8893-51baab8408d8/service_keys")
                            .build())
                    .build();
        }

        @Override
        protected GetServiceInstanceRequest getValidRequest() throws Exception {
            return GetServiceInstanceRequest.builder()
                    .id("test-id")
                    .build();
        }

        @Override
        protected Mono<GetServiceInstanceResponse> invoke(GetServiceInstanceRequest request) {
            return this.serviceInstances.get(request);
        }

    }

    public static final class List extends AbstractApiTest<ListServiceInstancesRequest, ListServiceInstancesResponse> {

        private final SpringServiceInstances serviceInstances = new SpringServiceInstances(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListServiceInstancesRequest getInvalidRequest() {
            return null;
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(GET).path("/v2/service_instances?q=name%20IN%20test-name&page=-1")
                    .status(OK)
                    .responsePayload("v2/service_instances/GET_response.json");
        }

        @Override
        protected ListServiceInstancesResponse getResponse() {
            return ListServiceInstancesResponse.builder()
                    .totalResults(1)
                    .totalPages(1)
                    .resource(ServiceInstanceResource.builder()
                            .metadata(Metadata.builder()
                                    .id("24ec15f9-f6c7-434a-8893-51baab8408d8")
                                    .url("/v2/service_instances/24ec15f9-f6c7-434a-8893-51baab8408d8")
                                    .createdAt("2015-07-27T22:43:08Z")
                                    .build())
                            .entity(ServiceInstanceEntity.builder()
                                    .name("name-133")
                                    .credential("creds-key-72", "creds-val-72")
                                    .servicePlanId("2b53255a-8b40-4671-803d-21d3f5d4183a")
                                    .spaceId("83b3e705-49fd-4c40-8adf-f5e34f622a19")
                                    .type("managed_service_instance")
                                    .lastOperation(ServiceInstanceEntity.LastOperation.builder()
                                            .type("create")
                                            .state("succeeded")
                                            .description("service broker-provided description")
                                            .updatedAt("2015-07-27T22:43:08Z")
                                            .createdAt("2015-07-27T22:43:08Z")
                                            .build())
                                    .tag("accounting")
                                    .tag("mongodb")
                                    .spaceUrl("/v2/spaces/83b3e705-49fd-4c40-8adf-f5e34f622a19")
                                    .servicePlanUrl("/v2/service_plans/2b53255a-8b40-4671-803d-21d3f5d4183a")
                                    .serviceBindingsUrl
                                            ("/v2/service_instances/24ec15f9-f6c7-434a-8893-51baab8408d8/service_bindings")
                                    .serviceKeysUrl
                                            ("/v2/service_instances/24ec15f9-f6c7-434a-8893-51baab8408d8/service_keys")
                                    .build())
                            .build())
                    .build();
        }

        @Override
        protected ListServiceInstancesRequest getValidRequest() {
            return ListServiceInstancesRequest.builder()
                    .name("test-name")
                    .page(-1)
                    .build();
        }

        @Override
        protected Mono<ListServiceInstancesResponse> invoke(ListServiceInstancesRequest request) {
            return this.serviceInstances.list(request);
        }

    }

    public static final class ListServiceBindings
            extends AbstractApiTest<ListServiceInstanceServiceBindingsRequest, ListServiceInstanceServiceBindingsResponse> {

        private final SpringServiceInstances serviceInstances = new SpringServiceInstances(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListServiceInstanceServiceBindingsRequest getInvalidRequest() {
            return ListServiceInstanceServiceBindingsRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(GET)
                    .path("v2/service_instances/test-id/service_bindings?q=app_guid%20IN%20test-application-id&page=-1")
                    .status(OK)
                    .responsePayload("v2/service_instances/GET_{id}_service_bindings_response.json");
        }

        @Override
        protected ListServiceInstanceServiceBindingsResponse getResponse() {
            return ListServiceInstanceServiceBindingsResponse.builder()
                    .totalResults(1)
                    .totalPages(1)
                    .resource(ServiceBindingResource.builder()
                            .metadata(Resource.Metadata.builder()
                                    .createdAt("2015-07-27T22:43:09Z")
                                    .id("05f3ec3c-8d97-4bd8-bf86-e44cc835a154")
                                    .url("/v2/service_bindings/05f3ec3c-8d97-4bd8-bf86-e44cc835a154")
                                    .build())
                            .entity(ServiceBindingEntity.builder()
                                    .applicationId("8a50163b-a39d-4f44-aece-dc5a956da848")
                                    .serviceInstanceId("a5a0567e-edbf-4da9-ae90-dce24af308a1")
                                    .credential("creds-key-85", "creds-val-85")
                                    .gatewayName("")
                                    .applicationUrl("/v2/apps/8a50163b-a39d-4f44-aece-dc5a956da848")
                                    .serviceInstanceUrl("/v2/service_instances/a5a0567e-edbf-4da9-ae90-dce24af308a1")
                                    .build())
                            .build())
                    .build();
        }

        @Override
        protected ListServiceInstanceServiceBindingsRequest getValidRequest() throws Exception {
            return ListServiceInstanceServiceBindingsRequest.builder()
                    .id("test-id")
                    .applicationId("test-application-id")
                    .page(-1)
                    .build();
        }

        @Override
        protected Mono<ListServiceInstanceServiceBindingsResponse> invoke(ListServiceInstanceServiceBindingsRequest request) {
            return this.serviceInstances.listServiceBindings(request);
        }

    }

    public static final class Update extends AbstractApiTest<UpdateServiceInstanceRequest, UpdateServiceInstanceResponse> {

        private final SpringServiceInstances serviceInstances = new SpringServiceInstances(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected UpdateServiceInstanceRequest getInvalidRequest() {
            return UpdateServiceInstanceRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(PUT).path("v2/service_instances/2a80a0f7-cb9c-414a-8a6b-7cc3f811ad41?accepts_incomplete=true")
                    .requestPayload("v2/service_instances/PUT_{id}_update_service_instance_request.json")
                    .status(CREATED)
                    .responsePayload("v2/service_instances/PUT_{id}_update_service_instance_response.json");
        }

        @Override
        protected UpdateServiceInstanceResponse getResponse() {
            return UpdateServiceInstanceResponse.builder()
                    .metadata(Resource.Metadata.builder()
                            .createdAt("2015-07-27T22:43:08Z")
                            .id("2a80a0f7-cb9c-414a-8a6b-7cc3f811ad41")
                            .url("/v2/service_instances/2a80a0f7-cb9c-414a-8a6b-7cc3f811ad41")
                            .build())
                    .entity(ServiceInstanceEntity.builder()
                            .name("name-139")
                            .credential("creds-key-75", "creds-val-75")
                            .servicePlanId("b07ff29a-78b8-486f-87a8-3f695368b83d")
                            .spaceId("04219ffa-a817-459f-bbd7-c161bdca541b")
                            .type("managed_service_instance")
                            .lastOperation(ServiceInstanceEntity.LastOperation.builder()
                                    .createdAt("2015-07-27T22:43:08Z")
                                    .updatedAt("2015-07-27T22:43:08Z")
                                    .description("")
                                    .state("in progress")
                                    .type("update")
                                    .build())
                            .spaceUrl("/v2/spaces/04219ffa-a817-459f-bbd7-c161bdca541b")
                            .servicePlanUrl("/v2/service_plans/b07ff29a-78b8-486f-87a8-3f695368b83d")
                            .serviceBindingsUrl("/v2/service_instances/2a80a0f7-cb9c-414a-8a6b-7cc3f811ad41/service_bindings")
                            .serviceKeysUrl("/v2/service_instances/2a80a0f7-cb9c-414a-8a6b-7cc3f811ad41/service_keys")
                            .build())
                    .build();
        }

        @Override
        protected UpdateServiceInstanceRequest getValidRequest() throws Exception {
            return UpdateServiceInstanceRequest.builder()
                    .acceptsIncomplete(true)
                    .id("2a80a0f7-cb9c-414a-8a6b-7cc3f811ad41")
                    .servicePlanId("5b5e984f-bbf6-477b-9d3a-b6d5df941b50")
                    .parameter("the_service_broker", "wants this object")
                    .build();
        }

        @Override
        protected Mono<UpdateServiceInstanceResponse> invoke(UpdateServiceInstanceRequest request) {
            return this.serviceInstances.update(request);
        }

    }

}
