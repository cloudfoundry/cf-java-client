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
import org.cloudfoundry.uaa.identityzonemanagement.DeleteIdentityZoneRequest;
import org.cloudfoundry.uaa.identityzonemanagement.DeleteIdentityZoneResponse;
import org.cloudfoundry.uaa.identityzonemanagement.GetIdentityZoneRequest;
import org.cloudfoundry.uaa.identityzonemanagement.GetIdentityZoneResponse;
import org.cloudfoundry.uaa.identityzonemanagement.IdentityZone;
import org.cloudfoundry.uaa.identityzonemanagement.ListIdentityZoneRequest;
import org.cloudfoundry.uaa.identityzonemanagement.ListIdentityZoneResponse;
import org.cloudfoundry.uaa.identityzonemanagement.UpdateIdentityZoneRequest;
import org.cloudfoundry.uaa.identityzonemanagement.UpdateIdentityZoneResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
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
                .requestPayload("fixtures/uaa/identity-zones/POST_request.json")
                .status(CREATED)
                .responsePayload("fixtures/uaa/identity-zones/POST_response.json");
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

    public static final class Delete extends AbstractApiTest<DeleteIdentityZoneRequest, DeleteIdentityZoneResponse> {

        private final SpringIdentityZoneManagement identityZoneManagement = new SpringIdentityZoneManagement(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected DeleteIdentityZoneRequest getInvalidRequest() {
            return DeleteIdentityZoneRequest.builder().build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(DELETE).path("/identity-zones/identity-zone-id")
                .status(OK)
                .responsePayload("fixtures/uaa/identity-zones/DELETE_{id}_response.json");
        }

        @Override
        protected DeleteIdentityZoneResponse getResponse() {
            return DeleteIdentityZoneResponse.builder()
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
        protected DeleteIdentityZoneRequest getValidRequest() throws Exception {
            return DeleteIdentityZoneRequest.builder()
                .identityZoneId("identity-zone-id")
                .build();
        }

        @Override
        protected Mono<DeleteIdentityZoneResponse> invoke(DeleteIdentityZoneRequest request) {
            return this.identityZoneManagement.delete(request);
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
                .responsePayload("fixtures/uaa/identity-zones/GET_{id}_response.json");
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

    public static final class List extends AbstractApiTest<ListIdentityZoneRequest, ListIdentityZoneResponse> {

        private final SpringIdentityZoneManagement identityZoneManagement = new SpringIdentityZoneManagement(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListIdentityZoneRequest getInvalidRequest() {
            return null;
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/identity-zones")
                .status(OK)
                .responsePayload("fixtures/uaa/identity-zones/GET_response.json");
        }

        @Override
        protected ListIdentityZoneResponse getResponse() {
            return ListIdentityZoneResponse.builder()
                .identityZone(IdentityZone.builder()
                    .createdAt(946710000000L)
                    .description("The system zone for backwards compatibility")
                    .identityZoneId("uaa")
                    .name("uaa")
                    .subDomain("")
                    .updatedAt(946710000000L)
                    .version(0)
                    .build())
                .identityZone(IdentityZone.builder()
                    .createdAt(1426260091139L)
                    .description("Like the Twilight Zone but tastier[testzone1].")
                    .identityZoneId("testzone1")
                    .name("The Twiglet Zone[testzone1]")
                    .subDomain("testzone1")
                    .updatedAt(1426260091139L)
                    .version(0)
                    .build())
                .build();
        }

        @Override
        protected ListIdentityZoneRequest getValidRequest() throws Exception {
            return ListIdentityZoneRequest.builder().build();
        }

        @Override
        protected Mono<ListIdentityZoneResponse> invoke(ListIdentityZoneRequest request) {
            return this.identityZoneManagement.list(request);
        }
    }

    public static final class Update extends AbstractApiTest<UpdateIdentityZoneRequest, UpdateIdentityZoneResponse> {

        private final SpringIdentityZoneManagement identityZoneManagement = new SpringIdentityZoneManagement(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected UpdateIdentityZoneRequest getInvalidRequest() {
            return UpdateIdentityZoneRequest.builder().build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(PUT).path("/identity-zones/testzone1")
                .requestPayload("fixtures/uaa/identity-zones/PUT_request.json")
                .status(OK)
                .responsePayload("fixtures/uaa/identity-zones/PUT_response.json");
        }

        @Override
        protected UpdateIdentityZoneResponse getResponse() {
            return UpdateIdentityZoneResponse.builder()
                .createdAt(1426258488910L)
                .description("Like the Twilight Zone but tastier[testzone1].")
                .identityZoneId("testzone1")
                .name("The Twiglet Zone[testzone1]")
                .subDomain("testzone1")
                .version(0)
                .build();
        }

        @Override
        protected UpdateIdentityZoneRequest getValidRequest() throws Exception {
            return UpdateIdentityZoneRequest.builder()
                .description("Like the Twilight Zone but tastier[testzone1].")
                .identityZoneId("testzone1")
                .name("The Twiglet Zone[testzone1]")
                .subDomain("testzone1")
                .build();
        }

        @Override
        protected Mono<UpdateIdentityZoneResponse> invoke(UpdateIdentityZoneRequest request) {
            return this.identityZoneManagement.update(request);
        }
    }

}
