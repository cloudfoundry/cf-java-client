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

package org.cloudfoundry.spring.uaa.identityproviders;

import org.cloudfoundry.spring.AbstractApiTest;
import org.cloudfoundry.uaa.identityproviders.CreateIdentityProviderRequest;
import org.cloudfoundry.uaa.identityproviders.CreateIdentityProviderResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.CREATED;

public final class SpringIdentityProvidersTest {

    public static final class Create extends AbstractApiTest<CreateIdentityProviderRequest, CreateIdentityProviderResponse> {

        private final SpringIdentityProviders identityProviders = new SpringIdentityProviders(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected CreateIdentityProviderRequest getInvalidRequest() {
            return CreateIdentityProviderRequest.builder().build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(POST).path("/identity-providers")
                .requestHeader("X-Identity-Zone-Id", "testzone1")
                .requestPayload("fixtures/uaa/identity-providers/POST_request.json")
                .status(CREATED)
                .responsePayload("fixtures/uaa/identity-providers/POST_response.json");
        }

        @Override
        protected CreateIdentityProviderResponse getResponse() {
            return CreateIdentityProviderResponse.builder()
                .active(true)
                .config(null)
                .createdAt(1426260091149L)
                .id("50cf6125-4372-475e-94e8-c43f84111e75")
                .identityZoneId("testzone1")
                .name("internal")
                .originKey("uaa")
                .type("internal")
                .updatedAt(1426260091149L)
                .version(0)
                .build();
        }

        @Override
        protected CreateIdentityProviderRequest getValidRequest() throws Exception {
            return CreateIdentityProviderRequest.builder()
                .active(true)
                .identityZoneId("testzone1")
                .name("internal")
                .originKey("uaa")
                .type("internal")
                .version(0)
                .build();
        }

        @Override
        protected Mono<CreateIdentityProviderResponse> invoke(CreateIdentityProviderRequest request) {
            return this.identityProviders.create(request);
        }
    }

}