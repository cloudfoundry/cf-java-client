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

package org.cloudfoundry.reactor.client.v2.servicebindings;

import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingResponse;
import org.cloudfoundry.client.v2.servicebindings.DeleteServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.DeleteServiceBindingResponse;
import org.cloudfoundry.client.v2.servicebindings.GetServiceBindingParametersRequest;
import org.cloudfoundry.client.v2.servicebindings.GetServiceBindingParametersResponse;
import org.cloudfoundry.client.v2.servicebindings.GetServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.GetServiceBindingResponse;
import org.cloudfoundry.client.v2.servicebindings.LastOperation;
import org.cloudfoundry.client.v2.servicebindings.ListServiceBindingsRequest;
import org.cloudfoundry.client.v2.servicebindings.ListServiceBindingsResponse;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingEntity;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingResource;
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
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.ACCEPTED;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorServiceBindingsV2Test extends AbstractClientApiTest {

    private final ReactorServiceBindingsV2 serviceBindings = new ReactorServiceBindingsV2(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void create() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST)
                .path("/service_bindings")
                .payload("fixtures/client/v2/service_bindings/POST_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v2/service_bindings/POST_response.json")
                .build())
            .build());

        this.serviceBindings
            .create(CreateServiceBindingRequest.builder()
                .applicationId("26ddc1de-3eeb-424b-82f3-f7f30a38b610")
                .serviceInstanceId("650d0eb7-3b83-414a-82a0-d503d1c8eb5f")
                .parameters(Collections.singletonMap("the_service_broker",
                    (Object) "wants this object"))
                .build())
            .as(StepVerifier::create)
            .expectNext(CreateServiceBindingResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2015-07-27T22:43:20Z")
                    .id("42eda707-fe4d-4eed-9b39-7cb5e665c226")
                    .url("/v2/service_bindings/42eda707-fe4d-4eed-9b39-7cb5e665c226")
                    .build())
                .entity(ServiceBindingEntity.builder()
                    .applicationId("26ddc1de-3eeb-424b-82f3-f7f30a38b610")
                    .serviceInstanceId("650d0eb7-3b83-414a-82a0-d503d1c8eb5f")
                    .bindingOptions(Collections.emptyMap())
                    .credential("creds-key-356", "creds-val-356")
                    .gatewayName("")
                    .applicationUrl("/v2/apps/26ddc1de-3eeb-424b-82f3-f7f30a38b610")
                    .serviceInstanceUrl("/v2/service_instances/650d0eb7-3b83-414a-82a0-d503d1c8eb5f")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void delete() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE)
                .path("/service_bindings/test-service-binding-id")
                .build())
            .response(TestResponse.builder()
                .status(NO_CONTENT)
                .build())
            .build());

        this.serviceBindings
            .delete(DeleteServiceBindingRequest.builder()
                .serviceBindingId("test-service-binding-id")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deleteAsync() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE)
                .path("/service_bindings/test-service-binding-id?async=true")
                .build())
            .response(TestResponse.builder()
                .status(ACCEPTED)
                .payload("fixtures/client/v2/service_bindings/DELETE_{id}_async_response.json")
                .build())
            .build());

        this.serviceBindings
            .delete(DeleteServiceBindingRequest.builder()
                .async(true)
                .serviceBindingId("test-service-binding-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(DeleteServiceBindingResponse.builder()
                .metadata(Metadata.builder()
                    .id("c4faac01-5bbd-494f-8849-256a3bab06b8")
                    .createdAt("2016-03-14T22:30:51Z")
                    .url("/v2/jobs/c4faac01-5bbd-494f-8849-256a3bab06b8")
                    .build())
                .entity(JobEntity.builder()
                    .id("c4faac01-5bbd-494f-8849-256a3bab06b8")
                    .status("queued")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET)
                .path("/service_bindings/test-service-binding-id")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/service_bindings/GET_{id}_response.json")
                .build())
            .build());

        this.serviceBindings
            .get(GetServiceBindingRequest.builder()
                .serviceBindingId("test-service-binding-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetServiceBindingResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2016-06-08T16:41:43Z")
                    .id("ddd7fb26-c42d-4acf-a035-60fdd094a167")
                    .updatedAt("2016-06-08T16:41:26Z")
                    .url("/v2/service_bindings/ddd7fb26-c42d-4acf-a035-60fdd094a167")
                    .build())
                .entity(ServiceBindingEntity.builder()
                    .applicationId("784bca1b-c4d9-4d99-9961-9f413620031a")
                    .applicationUrl("/v2/apps/784bca1b-c4d9-4d99-9961-9f413620031a")
                    .bindingOptions(Collections.emptyMap())
                    .credential("creds-key-64", "creds-val-64")
                    .gatewayName("")
                    .lastOperation(LastOperation.builder()
                        .createdAt("2018-02-28T16:25:19Z")
                        .description("")
                        .state("succeeded")
                        .type("create")
                        .updatedAt("2018-02-28T16:25:19Z")
                        .build())
                    .name("prod-db")
                    .serviceBindingParametersUrl("/v2/service_bindings/ddd7fb26-c42d-4acf-a035-60fdd094a167/parameters")
                    .serviceInstanceId("ada8700c-dd02-467c-937b-32ce498302f6")
                    .serviceInstanceUrl("/v2/service_instances/ada8700c-dd02-467c-937b-32ce498302f6")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getParameters() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET)
                .path("/service_bindings/test-service-binding-id/parameters")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/service_bindings/GET_{id}_parameters_response.json")
                .build())
            .build());

        this.serviceBindings
            .getParameters(GetServiceBindingParametersRequest.builder()
                .serviceBindingId("test-service-binding-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetServiceBindingParametersResponse.builder()
                .parameter("test-param-key-1", "test-param-value-1")
                .parameter("test-param-key-2", 12345)
                .parameter("test-param-key-3", false)
                .parameter("test-param-key-4", 3.141)
                .parameter("test-param-key-5", null)
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET)
                .path("/service_bindings?q=app_guid%3Add44fd4f-5e20-4c52-b66d-7af6e201f01e&page=-1")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/service_bindings/GET_response.json")
                .build())
            .build());

        this.serviceBindings
            .list(ListServiceBindingsRequest.builder()
                .applicationId("dd44fd4f-5e20-4c52-b66d-7af6e201f01e")
                .page(-1)
                .build())
            .as(StepVerifier::create)
            .expectNext(ListServiceBindingsResponse.builder()
                .totalResults(3)
                .totalPages(1)
                .resource(ServiceBindingResource.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2015-07-27T22:43:06Z")
                        .id("d6d87c3d-a38f-4b31-9bbe-2432d2faaa1d")
                        .url("/v2/service_bindings/d6d87c3d-a38f-4b31-9bbe-2432d2faaa1d")
                        .build())
                    .entity(ServiceBindingEntity.builder()
                        .applicationId("dd44fd4f-5e20-4c52-b66d-7af6e201f01e")
                        .serviceInstanceId("bbd1f170-bb1f-481d-bcf7-def2bbe6a3a2")
                        .bindingOptions(Collections.emptyMap())
                        .credential("creds-key-3", "creds-val-3")
                        .gatewayName("")
                        .applicationUrl("/v2/apps/dd44fd4f-5e20-4c52-b66d-7af6e201f01e")
                        .serviceInstanceUrl("/v2/service_instances/bbd1f170-bb1f-481d-bcf7-def2bbe6a3a2")
                        .build())
                    .build())
                .resource(ServiceBindingResource.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2015-11-03T00:53:50Z")
                        .id("925d8848-4808-47cf-a3e8-049aa0163328")
                        .updatedAt("2015-11-04T12:54:50Z")
                        .url("/v2/service_bindings/925d8848-4808-47cf-a3e8-049aa0163328")
                        .build())
                    .entity(ServiceBindingEntity.builder()
                        .applicationId("dd44fd4f-5e20-4c52-b66d-7af6e201f01e")
                        .serviceInstanceId("f99b3d23-55f9-48b5-add3-d7ab08b2ff0c")
                        .bindingOptions(Collections.emptyMap())
                        .credential("creds-key-108", "creds-val-108")
                        .gatewayName("")
                        .applicationUrl("/v2/apps/dd44fd4f-5e20-4c52-b66d-7af6e201f01e")
                        .serviceInstanceUrl("/v2/service_instances/f99b3d23-55f9-48b5-add3-d7ab08b2ff0c")
                        .build())
                    .build())
                .resource(ServiceBindingResource.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2015-07-27T22:43:20Z")
                        .id("42eda707-fe4d-4eed-9b39-7cb5e665c226")
                        .url("/v2/service_bindings/42eda707-fe4d-4eed-9b39-7cb5e665c226")
                        .build())
                    .entity(ServiceBindingEntity.builder()
                        .applicationId("dd44fd4f-5e20-4c52-b66d-7af6e201f01e")
                        .serviceInstanceId("650d0eb7-3b83-414a-82a0-d503d1c8eb5f")
                        .bindingOptions(Collections.emptyMap())
                        .credential("creds-key-356", "creds-val-356")
                        .gatewayName("")
                        .applicationUrl("/v2/apps/dd44fd4f-5e20-4c52-b66d-7af6e201f01e")
                        .serviceInstanceUrl("/v2/service_instances/650d0eb7-3b83-414a-82a0-d503d1c8eb5f")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
