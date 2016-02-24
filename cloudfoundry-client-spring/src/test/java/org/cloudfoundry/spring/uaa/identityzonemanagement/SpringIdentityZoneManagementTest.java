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

package org.cloudfoundry.spring.uaa.identityzonemanagement;

import org.cloudfoundry.spring.AbstractApiTest;
import org.cloudfoundry.uaa.identityzonemanagement.CreateIdentityZoneRequest;
import org.cloudfoundry.uaa.identityzonemanagement.CreateIdentityZoneResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.CREATED;

public final class SpringIdentityZoneManagementTest {

    public static final class Create extends AbstractApiTest<CreateIdentityZoneRequest, CreateIdentityZoneResponse> {

        private final SpringIdentityZoneManagement identityZoneManagement = new SpringIdentityZoneManagement(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected CreateIdentityZoneRequest getInvalidRequest() {
            return CreateIdentityZoneRequest.builder().build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(POST).path("/identity-zones")
                .requestPayload("uaa/identity-zones/POST_request.json")
                .status(CREATED)
                .responsePayload("uaa/identity-zones/POST_response.json");
        }

        @Override
        protected CreateIdentityZoneResponse getResponse() {
            return CreateIdentityZoneResponse.builder()
                .createdAt("2015-07-27T22:43:20Z")
                .description("Like the Twilight Zone but tastier[testzone1].")
                .identityZoneId("testzone1")
                .name("The Twiglet Zone[testzone1]")
                .subDomain("testzone1")
                .version(0)
                .build();
        }

        @Override
        protected CreateIdentityZoneRequest getValidRequest() throws Exception {
            return CreateIdentityZoneRequest.builder()
                .description("Like the Twilight Zone but tastier[testzone1].")
                .identityZoneId("testzone1")
                .name("The Twiglet Zone[testzone1]")
                .subDomain("testzone1")
                .build();
        }

        @Override
        protected Mono<CreateIdentityZoneResponse> invoke(CreateIdentityZoneRequest request) {
            return this.identityZoneManagement.create(request);
        }
    }

}