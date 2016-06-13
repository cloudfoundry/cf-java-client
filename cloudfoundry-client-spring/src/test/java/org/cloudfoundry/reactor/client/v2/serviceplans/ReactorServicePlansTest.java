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

package org.cloudfoundry.reactor.client.v2.serviceplans;

import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceEntity;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceResource;
import org.cloudfoundry.client.v2.serviceplans.DeleteServicePlanRequest;
import org.cloudfoundry.client.v2.serviceplans.DeleteServicePlanResponse;
import org.cloudfoundry.client.v2.serviceplans.GetServicePlanRequest;
import org.cloudfoundry.client.v2.serviceplans.GetServicePlanResponse;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlanServiceInstancesRequest;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlanServiceInstancesResponse;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansRequest;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansResponse;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanEntity;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanResource;
import org.cloudfoundry.client.v2.serviceplans.UpdateServicePlanRequest;
import org.cloudfoundry.client.v2.serviceplans.UpdateServicePlanResponse;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.PUT;
import static io.netty.handler.codec.http.HttpResponseStatus.ACCEPTED;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;


public final class ReactorServicePlansTest {

    public static final class Delete extends AbstractClientApiTest<DeleteServicePlanRequest, DeleteServicePlanResponse> {

        private final ReactorServicePlans servicePlans = new ReactorServicePlans(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/v2/service_plans/test-service-plan-id")
                    .build())
                .response(TestResponse.builder()
                    .status(NO_CONTENT)
                    .build())
                .build();
        }

        @Override
        protected DeleteServicePlanResponse getResponse() {
            return null;
        }

        @Override
        protected DeleteServicePlanRequest getValidRequest() throws Exception {
            return DeleteServicePlanRequest.builder()
                .servicePlanId("test-service-plan-id")
                .build();

        }

        @Override
        protected Mono<DeleteServicePlanResponse> invoke(DeleteServicePlanRequest request) {
            return this.servicePlans.delete(request);
        }
    }

    public static final class DeleteAsync extends AbstractClientApiTest<DeleteServicePlanRequest, DeleteServicePlanResponse> {

        private final ReactorServicePlans servicePlans = new ReactorServicePlans(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/v2/service_plans/test-service-plan-id?async=true")
                    .build())
                .response(TestResponse.builder()
                    .status(ACCEPTED)
                    .payload("fixtures/client/v2/service_plans/DELETE_{id}_async_response.json")
                    .build())
                .build();
        }

        @Override
        protected DeleteServicePlanResponse getResponse() {
            return DeleteServicePlanResponse.builder()
                .metadata(Metadata.builder()
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
        protected DeleteServicePlanRequest getValidRequest() throws Exception {
            return DeleteServicePlanRequest.builder()
                .async(true)
                .servicePlanId("test-service-plan-id")
                .build();

        }

        @Override
        protected Mono<DeleteServicePlanResponse> invoke(DeleteServicePlanRequest request) {
            return this.servicePlans.delete(request);
        }
    }

    public static final class Get extends AbstractClientApiTest<GetServicePlanRequest, GetServicePlanResponse> {

        private final ReactorServicePlans servicePlans = new ReactorServicePlans(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/service_plans/test-service-plan-id")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/service_plans/GET_{id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected GetServicePlanResponse getResponse() {
            return GetServicePlanResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2015-07-27T22:43:16Z")
                    .id("f6ceb8a2-e6fc-43d5-a11b-7ced9e1b47c7")
                    .url("/v2/service_plans/f6ceb8a2-e6fc-43d5-a11b-7ced9e1b47c7")
                    .build())
                .entity(ServicePlanEntity.builder()
                    .name("name-462")
                    .free(false)
                    .description("desc-52")
                    .serviceId("8ac39757-0f9d-4295-9b6f-e626f7ee3cd4")
                    .uniqueId("2aa0162c-9c88-4084-ad1d-566a09e8d316")
                    .publiclyVisible(true)
                    .active(true)
                    .serviceUrl("/v2/services/8ac39757-0f9d-4295-9b6f-e626f7ee3cd4")
                    .serviceInstancesUrl("/v2/service_plans/f6ceb8a2-e6fc-43d5-a11b-7ced9e1b47c7/service_instances")
                    .build())
                .build();
        }

        @Override
        protected GetServicePlanRequest getValidRequest() throws Exception {
            return GetServicePlanRequest.builder()
                .servicePlanId("test-service-plan-id")
                .build();
        }

        @Override
        protected Mono<GetServicePlanResponse> invoke(GetServicePlanRequest request) {
            return this.servicePlans.get(request);
        }
    }

    public static final class List extends AbstractClientApiTest<ListServicePlansRequest, ListServicePlansResponse> {

        private final ReactorServicePlans servicePlans = new ReactorServicePlans(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/service_plans?q=service_guid%20IN%20test-service-id&page=-1")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/service_plans/GET_response.json")
                    .build())
                .build();
        }

        @Override
        protected ListServicePlansResponse getResponse() {
            return ListServicePlansResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(ServicePlanResource.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2015-07-27T22:43:16Z")
                        .id("956cb355-3acc-4ced-8161-a57b9b5c7943")
                        .url("/v2/service_plans/956cb355-3acc-4ced-8161-a57b9b5c7943")
                        .build())
                    .entity(ServicePlanEntity.builder()
                        .name("name-464")
                        .free(false)
                        .description("desc-54")
                        .serviceId("83dc64ef-eb0a-454c-b3d9-c554921f3bd2")
                        .uniqueId("49aee95b-2108-4bbb-9769-c6197f308acf")
                        .publiclyVisible(true)
                        .active(true)
                        .serviceUrl("/v2/services/83dc64ef-eb0a-454c-b3d9-c554921f3bd2")
                        .serviceInstancesUrl("/v2/service_plans/956cb355-3acc-4ced-8161-a57b9b5c7943/service_instances")
                        .build())
                    .build())
                .build();
        }

        @Override
        protected ListServicePlansRequest getValidRequest() throws Exception {
            return ListServicePlansRequest.builder()
                .serviceId("test-service-id")
                .page(-1)
                .build();
        }

        @Override
        protected Mono<ListServicePlansResponse> invoke(ListServicePlansRequest request) {
            return this.servicePlans.list(request);
        }
    }

    public static final class ListServiceInstances extends AbstractClientApiTest<ListServicePlanServiceInstancesRequest, ListServicePlanServiceInstancesResponse> {

        private final ReactorServicePlans servicePlans = new ReactorServicePlans(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/service_plans/test-service-plan-id/service_instances?q=space_guid%20IN%20test-space-id&page=-1")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/service_plans/GET_{id}_service_instances_response.json")
                    .build())
                .build();
        }

        @Override
        protected ListServicePlanServiceInstancesResponse getResponse() {
            return ListServicePlanServiceInstancesResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(ServiceInstanceResource.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2015-07-27T22:43:16Z")
                        .id("b95c56b9-f81b-4d34-9a00-a1a1ddba5f2f")
                        .url("/v2/service_instances/b95c56b9-f81b-4d34-9a00-a1a1ddba5f2f")
                        .build())
                    .entity(ServiceInstanceEntity.builder()
                        .name("name-457")
                        .credential("creds-key-268", "creds-val-268")
                        .servicePlanId("bb29926c-7482-4ae5-803c-ec99e95aa278")
                        .spaceId("cf5812f5-bf43-40cc-88d4-d50b76d7797d")
                        .type("managed_service_instance")
                        .tags(Collections.emptyList())
                        .spaceUrl("/v2/spaces/cf5812f5-bf43-40cc-88d4-d50b76d7797d")
                        .servicePlanUrl("/v2/service_plans/bb29926c-7482-4ae5-803c-ec99e95aa278")
                        .serviceBindingsUrl("/v2/service_instances/b95c56b9-f81b-4d34-9a00-a1a1ddba5f2f/service_bindings")
                        .serviceKeysUrl("/v2/service_instances/b95c56b9-f81b-4d34-9a00-a1a1ddba5f2f/service_keys")
                        .build())
                    .build())
                .build();
        }

        @Override
        protected ListServicePlanServiceInstancesRequest getValidRequest() throws Exception {
            return ListServicePlanServiceInstancesRequest.builder()
                .servicePlanId("test-service-plan-id")
                .spaceId("test-space-id")
                .page(-1)
                .build();
        }

        @Override
        protected Mono<ListServicePlanServiceInstancesResponse> invoke(ListServicePlanServiceInstancesRequest request) {
            return this.servicePlans.listServiceInstances(request);
        }

    }

    public static final class Update extends AbstractClientApiTest<UpdateServicePlanRequest, UpdateServicePlanResponse> {

        private final ReactorServicePlans servicePlans = new ReactorServicePlans(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(PUT).path("/v2/service_plans/test-service-plan-id")
                    .payload("fixtures/client/v2/service_plans/PUT_{id}_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(CREATED)
                    .payload("fixtures/client/v2/service_plans/PUT_{id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected UpdateServicePlanResponse getResponse() {
            return UpdateServicePlanResponse.builder()
                .metadata(Metadata.builder()
                    .id("195f6bd5-0aa4-4a97-9c8d-5410e5e6d4b6")
                    .url("/v2/service_plans/195f6bd5-0aa4-4a97-9c8d-5410e5e6d4b6")
                    .createdAt("2016-02-19T02:04:09Z")
                    .updatedAt("2016-02-19T02:04:09Z")
                    .build())
                .entity(ServicePlanEntity.builder()
                    .name("name-2674")
                    .free(false)
                    .description("desc-225")
                    .serviceId("42bea093-8fe5-491a-8a34-b1943dc3709a")
                    .uniqueId("7c4f2f8a-aa82-49e9-9f0c-76248aa1036d")
                    .publiclyVisible(false)
                    .active(true)
                    .serviceUrl("/v2/services/42bea093-8fe5-491a-8a34-b1943dc3709a")
                    .serviceInstancesUrl("/v2/service_plans/195f6bd5-0aa4-4a97-9c8d-5410e5e6d4b6/service_instances")
                    .build())
                .build();
        }

        @Override
        protected UpdateServicePlanRequest getValidRequest() throws Exception {
            return UpdateServicePlanRequest.builder()
                .servicePlanId("test-service-plan-id")
                .publiclyVisible(false)
                .build();
        }

        @Override
        protected Mono<UpdateServicePlanResponse> invoke(UpdateServicePlanRequest request) {
            return this.servicePlans.update(request);
        }

    }
}
