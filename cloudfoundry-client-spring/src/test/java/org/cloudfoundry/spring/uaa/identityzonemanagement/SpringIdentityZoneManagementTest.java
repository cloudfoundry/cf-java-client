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
import org.cloudfoundry.uaa.identityzonemanagement.GetIdentityZoneRequest;
import org.cloudfoundry.uaa.identityzonemanagement.GetIdentityZoneResponse;
import reactor.core.publisher.Mono;

import java.util.Date;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

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
                .createdAt(1426258488910L)
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

    public static final class Get extends AbstractApiTest<GetIdentityZoneRequest, GetIdentityZoneResponse> {

        private final SpringIdentityZoneManagement identityZoneManagement = new SpringIdentityZoneManagement(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected GetIdentityZoneRequest getInvalidRequest() {
            return GetIdentityZoneRequest.builder().build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/identity-zones/identity-zone-id")
                .status(OK)
                .responsePayload("uaa/identity-zones/GET_{id}_response.json");
        }

        @Override
        protected GetIdentityZoneResponse getResponse() {
            return GetIdentityZoneResponse.builder()
                .createdAt(946710000000L)
                .description("The test zone")
                .identityZoneId("identity-zone-id")
                .name("test")
                .subDomain("test")
                .updatedAt(946710000000L)
                .version(0)
                .build();
        }

        @Override
        protected GetIdentityZoneRequest getValidRequest() throws Exception {
            return GetIdentityZoneRequest.builder()
                .identityZoneId("identity-zone-id")
                .build();
        }

        @Override
        protected Mono<GetIdentityZoneResponse> invoke(GetIdentityZoneRequest request) {
            return this.identityZoneManagement.get(request);
        }
    }

}