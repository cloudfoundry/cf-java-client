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

package org.cloudfoundry.spring.client.v2.shareddomains;

import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsRequest;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsResponse;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainEntity;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainResource;
import org.cloudfoundry.client.v2.shareddomains.CreateSharedDomainRequest;
import org.cloudfoundry.client.v2.shareddomains.CreateSharedDomainResponse;
import org.cloudfoundry.spring.AbstractApiTest;
import reactor.core.publisher.Mono;

import static org.cloudfoundry.client.v2.Resource.Metadata;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;

public final class SpringSharedDomainsTest {

    public static final class ListSharedDomains extends AbstractApiTest<ListSharedDomainsRequest, ListSharedDomainsResponse> {

        private final SpringSharedDomains sharedDomains = new SpringSharedDomains(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListSharedDomainsRequest getInvalidRequest() {
            return null;
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/shared_domains?page=-1")
                .status(OK)
                .responsePayload("fixtures/client/v2/shared_domains/GET_response.json");
        }

        @Override
        protected ListSharedDomainsResponse getResponse() {
            return ListSharedDomainsResponse.builder()
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
                .build();
        }

        @Override
        protected ListSharedDomainsRequest getValidRequest() throws Exception {
            return ListSharedDomainsRequest.builder()
                .page(-1)
                .build();
        }

        @Override
        protected Mono<ListSharedDomainsResponse> invoke(ListSharedDomainsRequest request) {
            return this.sharedDomains.list(request);
        }

    }

    public static final class Create extends AbstractApiTest<CreateSharedDomainRequest, CreateSharedDomainResponse> {

        private final SpringSharedDomains sharedDomains = new SpringSharedDomains(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected CreateSharedDomainRequest getInvalidRequest() {
            return CreateSharedDomainRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(POST).path("/v2/shared_domains")
                    .requestPayload("fixtures/client/v2/shared_domains/POST_request.json")
                    .status(OK)
                    .responsePayload("fixtures/client/v2/shared_domains/POST_response.json");
        }

        @Override
        protected CreateSharedDomainResponse getResponse() {
            return CreateSharedDomainResponse.builder()
                    .metadata(Resource.Metadata.builder()
                            .id("5e9d838e-6d4f-42cd-ba13-789bfae2277c")
                            .url("/v2/shared_domains/5e9d838e-6d4f-42cd-ba13-789bfae2277c")
                            .createdAt("2015-12-22T18:28:19Z")
                            .build())
                    .entity(SharedDomainEntity.builder()
                            .name("shared-domain.com")
                            .routerGroupId("random-guid")
                            .build())
                    .build();
        }

        @Override
        protected CreateSharedDomainRequest getValidRequest() throws Exception {
            return CreateSharedDomainRequest.builder()
                    .name("shared-domain.com")
                    .routerGroupId("random-guid")
                    .build();
        }

        @Override
        protected Mono<CreateSharedDomainResponse> invoke(CreateSharedDomainRequest request) {
            return this.sharedDomains.create(request);
        }
    }

}
