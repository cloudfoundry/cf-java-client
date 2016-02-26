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

package org.cloudfoundry.spring.client.v2.privatedomains;

import org.cloudfoundry.spring.AbstractApiTest;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.privatedomains.CreatePrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.CreatePrivateDomainResponse;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainEntity;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;

public final class SpringPrivateDomainsTest {

    public static final class Create extends AbstractApiTest<CreatePrivateDomainRequest, CreatePrivateDomainResponse> {

        private final SpringPrivateDomains privateDomains = new SpringPrivateDomains(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected CreatePrivateDomainRequest getInvalidRequest() {
            return CreatePrivateDomainRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(POST).path("/v2/private_domains")
                .requestPayload("fixtures/client/v2/private_domains/POST_request.json")
                .status(OK)
                .responsePayload("fixtures/client/v2/private_domains/POST_response.json");
        }

        @Override
        protected CreatePrivateDomainResponse getResponse() {
            return CreatePrivateDomainResponse.builder()
                .metadata(Resource.Metadata.builder()
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
                .build();
        }

        @Override
        protected CreatePrivateDomainRequest getValidRequest() throws Exception {
            return CreatePrivateDomainRequest.builder()
                .name("exmaple.com")
                .owningOrganizationId("22bb8ae1-6324-40eb-b077-bd1bfad773f8")
                .build();
        }

        @Override
        protected Mono<CreatePrivateDomainResponse> invoke(CreatePrivateDomainRequest request) {
            return this.privateDomains.create(request);
        }
    }

}
