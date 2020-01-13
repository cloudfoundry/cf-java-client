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

package org.cloudfoundry.reactor.client.v2.shareddomains;

import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.cloudfoundry.client.v2.shareddomains.CreateSharedDomainRequest;
import org.cloudfoundry.client.v2.shareddomains.CreateSharedDomainResponse;
import org.cloudfoundry.client.v2.shareddomains.DeleteSharedDomainRequest;
import org.cloudfoundry.client.v2.shareddomains.DeleteSharedDomainResponse;
import org.cloudfoundry.client.v2.shareddomains.GetSharedDomainRequest;
import org.cloudfoundry.client.v2.shareddomains.GetSharedDomainResponse;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsRequest;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsResponse;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainEntity;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainResource;
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
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorSharedDomainsTest extends AbstractClientApiTest {

    private final ReactorSharedDomains sharedDomains = new ReactorSharedDomains(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void create() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/shared_domains")
                .payload("fixtures/client/v2/shared_domains/POST_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/shared_domains/POST_response.json")
                .build())
            .build());

        this.sharedDomains
            .create(CreateSharedDomainRequest.builder()
                .name("shared-domain.com")
                .routerGroupId("random-guid")
                .build())
            .as(StepVerifier::create)
            .expectNext(CreateSharedDomainResponse.builder()
                .metadata(Metadata.builder()
                    .id("d6c7d452-70bb-4edd-bbf1-a925dd51732c")
                    .url("/v2/shared_domains/d6c7d452-70bb-4edd-bbf1-a925dd51732c")
                    .createdAt("2016-04-22T19:33:17Z")
                    .build())
                .entity(SharedDomainEntity.builder()
                    .name("example.com")
                    .routerGroupId("my-random-guid")
                    .routerGroupType("tcp")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void delete() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/shared_domains/fa1385de-55ba-41d3-beb2-f83919c634d6")
                .build())
            .response(TestResponse.builder()
                .status(NO_CONTENT)
                .build())
            .build());

        this.sharedDomains
            .delete(DeleteSharedDomainRequest.builder()
                .sharedDomainId("fa1385de-55ba-41d3-beb2-f83919c634d6")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deleteAsync() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/shared_domains/fa1385de-55ba-41d3-beb2-f83919c634d6?async=true")
                .build())
            .response(TestResponse.builder()
                .status(ACCEPTED)
                .payload("fixtures/client/v2/shared_domains/DELETE_{id}_async_response.json")
                .build())
            .build());

        this.sharedDomains
            .delete(DeleteSharedDomainRequest.builder()
                .async(true)
                .sharedDomainId("fa1385de-55ba-41d3-beb2-f83919c634d6")
                .build())
            .as(StepVerifier::create)
            .expectNext(DeleteSharedDomainResponse.builder()
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
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listSharedDomains() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/shared_domains?page=-1")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/shared_domains/GET_response.json")
                .build())
            .build());

        this.sharedDomains
            .list(ListSharedDomainsRequest.builder()
                .page(-1)
                .build())
            .as(StepVerifier::create)
            .expectNext(ListSharedDomainsResponse.builder()
                .totalResults(5)
                .totalPages(1)
                .resource(SharedDomainResource.builder()
                    .metadata(Metadata.builder()
                        .id("f01b174d-c750-46b0-9ddf-3aeb2064d796")
                        .url("/v2/shared_domains/f01b174d-c750-46b0-9ddf-3aeb2064d796")
                        .createdAt("2015-11-30T23:38:35Z")
                        .build())
                    .entity(SharedDomainEntity.builder()
                        .name("customer-app-domain1.com")
                        .build())
                    .build())
                .resource(SharedDomainResource.builder()
                    .metadata(Metadata.builder()
                        .id("3595f6cb-81cf-424e-a546-533877ccccfd")
                        .url("/v2/shared_domains/3595f6cb-81cf-424e-a546-533877ccccfd")
                        .createdAt("2015-11-30T23:38:35Z")
                        .build())
                    .entity(SharedDomainEntity.builder()
                        .name("customer-app-domain2.com")
                        .build())
                    .build())
                .resource(SharedDomainResource.builder()
                    .metadata(Metadata.builder()
                        .id("d0d28c59-86ee-4415-9269-500976f18e72")
                        .url("/v2/shared_domains/d0d28c59-86ee-4415-9269-500976f18e72")
                        .createdAt("2015-11-30T23:38:35Z")
                        .build())
                    .entity(SharedDomainEntity.builder()
                        .name("domain-19.example.com")
                        .build())
                    .build())
                .resource(SharedDomainResource.builder()
                    .metadata(Metadata.builder()
                        .id("b7242cdb-f81a-4469-b897-d5a218470fdf")
                        .url("/v2/shared_domains/b7242cdb-f81a-4469-b897-d5a218470fdf")
                        .createdAt("2015-11-30T23:38:35Z")
                        .build())
                    .entity(SharedDomainEntity.builder()
                        .name("domain-20.example.com")
                        .build())
                    .build())
                .resource(SharedDomainResource.builder()
                    .metadata(Metadata.builder()
                        .id("130c193c-c1c6-41c9-98c2-4a0e16a948bf")
                        .url("/v2/shared_domains/130c193c-c1c6-41c9-98c2-4a0e16a948bf")
                        .createdAt("2015-11-30T23:38:35Z")
                        .build())
                    .entity(SharedDomainEntity.builder()
                        .name("domain-21.example.com")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    public static final class Get extends AbstractClientApiTest {

        private final ReactorSharedDomains sharedDomains = new ReactorSharedDomains(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

        @Test
        public void get() {
            mockRequest(interactionContext());

            this.sharedDomains
                .get(GetSharedDomainRequest.builder()
                    .sharedDomainId("fa1385de-55ba-41d3-beb2-f83919c634d6")
                    .build())
                .as(StepVerifier::create)
                .expectNext(GetSharedDomainResponse.builder()
                    .metadata(Metadata.builder()
                        .id("fa1385de-55ba-41d3-beb2-f83919c634d6")
                        .url("/v2/shared_domains/fa1385de-55ba-41d3-beb2-f83919c634d6")
                        .createdAt("2016-06-08T16:41:33Z")
                        .build())
                    .entity(SharedDomainEntity.builder()
                        .name("customer-app-domain1.com")
                        .build())
                    .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
        }

        public InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/shared_domains/fa1385de-55ba-41d3-beb2-f83919c634d6")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/shared_domains/GET_{id}_response.json")
                    .build())
                .build();
        }

    }

}
