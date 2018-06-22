/*
 * Copyright 2013-2018 the original author or authors.
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

package org.cloudfoundry.reactor.client.v3.serviceInstances;

import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.Pagination;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.cloudfoundry.client.v3.serviceInstances.ListServiceInstancesRequest;
import org.cloudfoundry.client.v3.serviceInstances.ListServiceInstancesResponse;
import org.cloudfoundry.client.v3.serviceInstances.ListSharedSpacesRelationshipRequest;
import org.cloudfoundry.client.v3.serviceInstances.ListSharedSpacesRelationshipResponse;
import org.cloudfoundry.client.v3.serviceInstances.ServiceInstanceRelationships;
import org.cloudfoundry.client.v3.serviceInstances.ServiceInstanceResource;
import org.cloudfoundry.client.v3.serviceInstances.ShareServiceInstanceRequest;
import org.cloudfoundry.client.v3.serviceInstances.ShareServiceInstanceResponse;
import org.cloudfoundry.client.v3.serviceInstances.UnshareServiceInstanceRequest;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.cloudfoundry.reactor.client.v3.serviceinstances.ReactorServiceInstancesV3;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;


public final class ReactorServiceInstancesV3Test extends AbstractClientApiTest{

    private final ReactorServiceInstancesV3 serviceInstances = new ReactorServiceInstancesV3(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

    @Test
    public void share(){
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/service_instances/test-service-instance-id/relationships/shared_spaces")
                .payload("fixtures/client/v3/serviceinstances/POST_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v3/serviceinstances/POST_response.json")
                .build())
            .build());

        this.serviceInstances
            .share(ShareServiceInstanceRequest.builder()
                .serviceInstanceId("test-service-instance-id")
                .data(Relationship.builder()
                    .id("space-guid-1")
                .build())
                .data(Relationship.builder()
                    .id("space-guid-2")
                    .build())
                .build())
            .as(StepVerifier::create)
            .expectNext(ShareServiceInstanceResponse.builder()
                .data(Relationship.builder()
                    .id("68d54d31-9b3a-463b-ba94-e8e4c32edbac")
                    .build())
                .data(Relationship.builder()
                    .id("b19f6525-cbd3-4155-b156-dc0c2a431b4c")
                    .build())
                .link("self", Link.builder()
                    .href("/v3/service_instances/bdeg4371-cbd3-4155-b156-dc0c2a431b4c/relationships/shared_spaces")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list(){
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/service_instances?names=test-service-instance-name&space_guids=test-space-id&page=1")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/serviceinstances/GET_response.json")
                .build())
            .build());

        this.serviceInstances
            .list(ListServiceInstancesRequest.builder()
                .page(1)
                .serviceInstanceName("test-service-instance-name")
                .spaceId("test-space-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(ListServiceInstancesResponse.builder()
                .pagination(Pagination.builder()
                    .totalResults(3)
                    .first(Link.builder()
                        .href("/v3/service_instances?page=1&per_page=2")
                        .build())
                    .last(Link.builder()
                        .href("/v3/service_instances?page=2&per_page=2")
                        .build())
                    .next(Link.builder()
                        .href("/v3/service_instances?page=2&per_page=2")
                        .build())
                    .build())
                .resource(ServiceInstanceResource.builder()
                    .id("85ccdcad-d725-4109-bca4-fd6ba062b5c8")
                    .createdAt("2017-11-17T13:54:21Z")
                    .name("my_service_instance1")
                    .relationships(ServiceInstanceRelationships.builder()
                        .space(ToOneRelationship.builder()
                            .data(Relationship.builder()
                                .id("ae0031f9-dd49-461c-a945-df40e77c39cb")
                                .build())
                            .build())
                        .build())
                    .link("space", Link.builder()
                        .href("/v3/spaces/ae0031f9-dd49-461c-a945-df40e77c39cb")
                        .build())
                    .build())
                .resource(ServiceInstanceResource.builder()
                    .id("85ccdcad-d725-4109-bca4-fd6ba062b5c7")
                    .createdAt("2017-11-17T13:54:21Z")
                    .name("my_service_instance2")
                    .relationships(ServiceInstanceRelationships.builder()
                        .space(ToOneRelationship.builder()
                            .data(Relationship.builder()
                                .id("ae0031f9-dd49-461c-a945-df40e77c39ce")
                                .build())
                            .build())
                        .build())
                    .link("space", Link.builder()
                        .href("/v3/spaces/ae0031f9-dd49-461c-a945-df40e77c39ce")
                        .build())
                    .build())
                .resource(ServiceInstanceResource.builder()
                    .id("85ccdcad-d725-4109-bca4-fd6ba062b5c6")
                    .createdAt("2017-11-17T13:54:21Z")
                    .name("my_service_instance3")
                    .relationships(ServiceInstanceRelationships.builder()
                        .space(ToOneRelationship.builder()
                            .data(Relationship.builder()
                                .id("ae0031f9-dd49-461c-a945-df40e77c39cf")
                                .build())
                            .build())
                        .build())
                    .link("space", Link.builder()
                        .href("/v3/spaces/ae0031f9-dd49-461c-a945-df40e77c39cf")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listSharedSpaces(){
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/service_instances/test-service-instance-id/relationships/shared_spaces")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/serviceinstances/GET_{id}_relationships_spaces_shared_response.json")
                .build())
            .build());

        this.serviceInstances
            .listSharedSpacesRelationship(ListSharedSpacesRelationshipRequest.builder()
                .serviceInstanceId("test-service-instance-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(ListSharedSpacesRelationshipResponse.builder()
                .data(Relationship.builder()
                    .id("68d54d31-9b3a-463b-ba94-e8e4c32edbac")
                    .build())
                .data(Relationship.builder()
                    .id("b19f6525-cbd3-4155-b156-dc0c2a431b4c")
                    .build())
                .link("self", Link.builder()
                    .href("/v3/service_instances/bdeg4371-cbd3-4155-b156-dc0c2a431b4c/relationships/shared_spaces")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void unshare(){
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/service_instances/test-service-instance-id/relationships/shared_spaces/test-space-id")
                .build())
            .response(TestResponse.builder()
                .status(NO_CONTENT)
                .build())
            .build());

        this.serviceInstances
            .unshare(UnshareServiceInstanceRequest.builder()
                .serviceInstanceId("test-service-instance-id")
                .spaceId("test-space-id")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }
}
