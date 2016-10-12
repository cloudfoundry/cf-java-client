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

package org.cloudfoundry.reactor.client.v2.privatedomains;

import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.cloudfoundry.client.v2.privatedomains.CreatePrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.CreatePrivateDomainResponse;
import org.cloudfoundry.client.v2.privatedomains.DeletePrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.DeletePrivateDomainResponse;
import org.cloudfoundry.client.v2.privatedomains.GetPrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.GetPrivateDomainResponse;
import org.cloudfoundry.client.v2.privatedomains.ListPrivateDomainsRequest;
import org.cloudfoundry.client.v2.privatedomains.ListPrivateDomainsResponse;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainEntity;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainResource;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import reactor.core.publisher.Mono;
import reactor.test.subscriber.ScriptedSubscriber;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.ACCEPTED;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorPrivateDomainsTest {

    public static final class Create extends AbstractClientApiTest<CreatePrivateDomainRequest, CreatePrivateDomainResponse> {

        private final ReactorPrivateDomains privateDomains = new ReactorPrivateDomains(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<CreatePrivateDomainResponse> expectations() {
            return ScriptedSubscriber.<CreatePrivateDomainResponse>create()
                .expectValue(CreatePrivateDomainResponse.builder()
                    .metadata(Metadata.builder()
                        .id("4af3234e-813d-453f-b3ae-fcdecfd87a47")
                        .url("/v2/private_domains/4af3234e-813d-453f-b3ae-fcdecfd87a47")
                        .createdAt("2016-01-19T19:41:12Z")
                        .build())
                    .entity(PrivateDomainEntity.builder()
                        .name("exmaple.com")
                        .owningOrganizationId("22bb8ae1-6324-40eb-b077-bd1bfad773f8")
                        .owningOrganizationUrl("/v2/organizations/22bb8ae1-6324-40eb-b077-bd1bfad773f8")
                        .sharedOrganizationsUrl("/v2/private_domains/4af3234e-813d-453f-b3ae-fcdecfd87a47/shared_organizations")
                        .build())
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(POST).path("/v2/private_domains")
                    .payload("fixtures/client/v2/private_domains/POST_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/private_domains/POST_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<CreatePrivateDomainResponse> invoke(CreatePrivateDomainRequest request) {
            return this.privateDomains.create(request);
        }

        @Override
        protected CreatePrivateDomainRequest validRequest() {
            return CreatePrivateDomainRequest.builder()
                .name("exmaple.com")
                .owningOrganizationId("22bb8ae1-6324-40eb-b077-bd1bfad773f8")
                .build();
        }
    }

    public static final class Delete extends AbstractClientApiTest<DeletePrivateDomainRequest, DeletePrivateDomainResponse> {

        private final ReactorPrivateDomains privateDomains = new ReactorPrivateDomains(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<DeletePrivateDomainResponse> expectations() {
            return ScriptedSubscriber.<DeletePrivateDomainResponse>create()
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/v2/private_domains/test-private-domain-id")
                    .build())
                .response(TestResponse.builder()
                    .status(NO_CONTENT)
                    .build())
                .build();
        }

        @Override
        protected Mono<DeletePrivateDomainResponse> invoke(DeletePrivateDomainRequest request) {
            return this.privateDomains.delete(request);
        }

        @Override
        protected DeletePrivateDomainRequest validRequest() {
            return DeletePrivateDomainRequest.builder()
                .privateDomainId("test-private-domain-id")
                .build();
        }

    }

    public static final class DeleteAsync extends AbstractClientApiTest<DeletePrivateDomainRequest, DeletePrivateDomainResponse> {

        private final ReactorPrivateDomains privateDomains = new ReactorPrivateDomains(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<DeletePrivateDomainResponse> expectations() {
            return ScriptedSubscriber.<DeletePrivateDomainResponse>create()
                .expectValue(DeletePrivateDomainResponse.builder()
                    .metadata(Metadata.builder()
                        .id("2d9707ba-6f0b-4aef-a3de-fe9bdcf0c9d1")
                        .createdAt("2016-02-02T17:16:31Z")
                        .url("/v2/jobs/2d9707ba-6f0b-4aef-a3de-fe9bdcf0c9d1")
                        .build())
                    .entity(JobEntity.builder()
                        .id("2d9707ba-6f0b-4aef-a3de-fe9bdcf0c9d1")
                        .status("queued")
                        .build())
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/v2/private_domains/test-private-domain-id?async=true")
                    .build())
                .response(TestResponse.builder()
                    .status(ACCEPTED)
                    .payload("fixtures/client/v2/private_domains/DELETE_{id}_async_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<DeletePrivateDomainResponse> invoke(DeletePrivateDomainRequest request) {
            return this.privateDomains.delete(request);
        }

        @Override
        protected DeletePrivateDomainRequest validRequest() {
            return DeletePrivateDomainRequest.builder()
                .async(true)
                .privateDomainId("test-private-domain-id")
                .build();
        }

    }

    public static final class Get extends AbstractClientApiTest<GetPrivateDomainRequest, GetPrivateDomainResponse> {

        private final ReactorPrivateDomains privateDomains = new ReactorPrivateDomains(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<GetPrivateDomainResponse> expectations() {
            return ScriptedSubscriber.<GetPrivateDomainResponse>create()
                .expectValue(GetPrivateDomainResponse.builder()
                    .metadata(Metadata.builder()
                        .id("3de9db5f-8e3b-4d10-a8c9-8137caafe43d")
                        .url("/v2/private_domains/3de9db5f-8e3b-4d10-a8c9-8137caafe43d")
                        .createdAt("2016-02-19T02:04:00Z")
                        .build())
                    .entity(PrivateDomainEntity.builder()
                        .name("my-domain.com")
                        .owningOrganizationId("2f70efed-abb2-4b7a-9f31-d4fe4d849932")
                        .owningOrganizationUrl("/v2/organizations/2f70efed-abb2-4b7a-9f31-d4fe4d849932")
                        .sharedOrganizationsUrl("/v2/private_domains/3de9db5f-8e3b-4d10-a8c9-8137caafe43d/shared_organizations")
                        .build())
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/private_domains/test-private-domain-id")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/private_domains/GET_{id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<GetPrivateDomainResponse> invoke(GetPrivateDomainRequest request) {
            return this.privateDomains.get(request);
        }

        @Override
        protected GetPrivateDomainRequest validRequest() {
            return GetPrivateDomainRequest.builder()
                .privateDomainId("test-private-domain-id")
                .build();
        }

    }

    public static final class List extends AbstractClientApiTest<ListPrivateDomainsRequest, ListPrivateDomainsResponse> {

        private final ReactorPrivateDomains privateDomains = new ReactorPrivateDomains(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<ListPrivateDomainsResponse> expectations() {
            return ScriptedSubscriber.<ListPrivateDomainsResponse>create()
                .expectValue(ListPrivateDomainsResponse.builder()
                    .totalResults(1)
                    .totalPages(1)
                    .resource(PrivateDomainResource.builder()
                        .metadata(Metadata.builder()
                            .id("3de9db5f-8e3b-4d10-a8c9-8137caafe43d")
                            .url("/v2/private_domains/3de9db5f-8e3b-4d10-a8c9-8137caafe43d")
                            .createdAt("2016-02-19T02:04:00Z")
                            .build())
                        .entity(PrivateDomainEntity.builder()
                            .name("my-domain.com")
                            .owningOrganizationId("2f70efed-abb2-4b7a-9f31-d4fe4d849932")
                            .owningOrganizationUrl("/v2/organizations/2f70efed-abb2-4b7a-9f31-d4fe4d849932")
                            .sharedOrganizationsUrl("/v2/private_domains/3de9db5f-8e3b-4d10-a8c9-8137caafe43d/shared_organizations")
                            .build())
                        .build())
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/private_domains?q=name%20IN%20test-name.com&page=-1")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/private_domains/GET_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<ListPrivateDomainsResponse> invoke(ListPrivateDomainsRequest request) {
            return this.privateDomains.list(request);
        }

        @Override
        protected ListPrivateDomainsRequest validRequest() {
            return ListPrivateDomainsRequest.builder()
                .name("test-name.com")
                .page(-1)
                .build();
        }

    }
}
