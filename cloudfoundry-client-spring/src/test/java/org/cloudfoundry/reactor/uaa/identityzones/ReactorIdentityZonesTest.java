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

package org.cloudfoundry.reactor.uaa.identityzones;

import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.uaa.AbstractUaaApiTest;
import org.cloudfoundry.uaa.identityzones.CreateIdentityZoneRequest;
import org.cloudfoundry.uaa.identityzones.CreateIdentityZoneResponse;
import org.cloudfoundry.uaa.identityzones.DeleteIdentityZoneRequest;
import org.cloudfoundry.uaa.identityzones.DeleteIdentityZoneResponse;
import org.cloudfoundry.uaa.identityzones.GetIdentityZoneRequest;
import org.cloudfoundry.uaa.identityzones.GetIdentityZoneResponse;
import org.cloudfoundry.uaa.identityzones.IdentityZone;
import org.cloudfoundry.uaa.identityzones.ListIdentityZonesRequest;
import org.cloudfoundry.uaa.identityzones.ListIdentityZonesResponse;
import org.cloudfoundry.uaa.identityzones.UpdateIdentityZoneRequest;
import org.cloudfoundry.uaa.identityzones.UpdateIdentityZoneResponse;
import reactor.core.publisher.Mono;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpMethod.PUT;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorIdentityZonesTest {

    public static final class Create extends AbstractUaaApiTest<CreateIdentityZoneRequest, CreateIdentityZoneResponse> {

        private final ReactorIdentityZones identityZoneManagement = new ReactorIdentityZones(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(POST).path("/identity-zones")
                    .payload("fixtures/uaa/identity-zones/POST_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(CREATED)
                    .payload("fixtures/uaa/identity-zones/POST_response.json")
                    .build())
                .build();
        }

        @Override
        protected CreateIdentityZoneResponse getResponse() {
            return CreateIdentityZoneResponse.builder()
                .createdAt(1426258488910L)
                .description("Like the Twilight Zone but tastier[testzone1].")
                .id("testzone1")
                .lastModified(1461972047048L)
                .name("The Twiglet Zone[testzone1]")
                .subdomain("testzone1")
                .version(0)
                .build();
        }

        @Override
        protected CreateIdentityZoneRequest getValidRequest() throws Exception {
            return CreateIdentityZoneRequest.builder()
                .description("Like the Twilight Zone but tastier[testzone1].")
                .identityZoneId("testzone1")
                .name("The Twiglet Zone[testzone1]")
                .subdomain("testzone1")
                .build();
        }

        @Override
        protected Mono<CreateIdentityZoneResponse> invoke(CreateIdentityZoneRequest request) {
            return this.identityZoneManagement.create(request);
        }
    }

    public static final class Delete extends AbstractUaaApiTest<DeleteIdentityZoneRequest, DeleteIdentityZoneResponse> {

        private final ReactorIdentityZones identityZoneManagement = new ReactorIdentityZones(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/identity-zones/identity-zone-id")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/uaa/identity-zones/DELETE_{id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected DeleteIdentityZoneResponse getResponse() {
            return DeleteIdentityZoneResponse.builder()
                .createdAt(946710000000L)
                .description("The test zone")
                .id("identity-zone-id")
                .lastModified(946710000000L)
                .name("test")
                .subdomain("test")
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

    public static final class Get extends AbstractUaaApiTest<GetIdentityZoneRequest, GetIdentityZoneResponse> {

        private final ReactorIdentityZones identityZoneManagement = new ReactorIdentityZones(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/identity-zones/identity-zone-id")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/uaa/identity-zones/GET_{id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected GetIdentityZoneResponse getResponse() {
            return GetIdentityZoneResponse.builder()
                .createdAt(946710000000L)
                .description("The test zone")
                .id("identity-zone-id")
                .lastModified(946710000000L)
                .name("test")
                .subdomain("test")
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

    public static final class List extends AbstractUaaApiTest<ListIdentityZonesRequest, ListIdentityZonesResponse> {

        private final ReactorIdentityZones identityZoneManagement = new ReactorIdentityZones(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/identity-zones")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/uaa/identity-zones/GET_response.json")
                    .build())
                .build();
        }

        @Override
        protected ListIdentityZonesResponse getResponse() {
            return ListIdentityZonesResponse.builder()
                .identityZone(IdentityZone.builder()
                    .createdAt(946710000000L)
                    .description("The system zone for backwards compatibility")
                    .id("uaa")
                    .lastModified(946710000000L)
                    .name("uaa")
                    .subdomain("")
                    .version(0)
                    .build())
                .identityZone(IdentityZone.builder()
                    .createdAt(1426260091139L)
                    .description("Like the Twilight Zone but tastier[testzone1].")
                    .id("testzone1")
                    .lastModified(1426260091139L)
                    .name("The Twiglet Zone[testzone1]")
                    .subdomain("testzone1")
                    .version(0)
                    .build())
                .build();
        }

        @Override
        protected ListIdentityZonesRequest getValidRequest() throws Exception {
            return ListIdentityZonesRequest.builder().build();
        }

        @Override
        protected Mono<ListIdentityZonesResponse> invoke(ListIdentityZonesRequest request) {
            return this.identityZoneManagement.list(request);
        }
    }

    public static final class Update extends AbstractUaaApiTest<UpdateIdentityZoneRequest, UpdateIdentityZoneResponse> {

        private final ReactorIdentityZones identityZoneManagement = new ReactorIdentityZones(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(PUT).path("/identity-zones/testzone1")
                    .payload("fixtures/uaa/identity-zones/PUT_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/uaa/identity-zones/PUT_response.json")
                    .build())
                .build();
        }

        @Override
        protected UpdateIdentityZoneResponse getResponse() {
            return UpdateIdentityZoneResponse.builder()
                .createdAt(1426258488910L)
                .description("Like the Twilight Zone but tastier[testzone1].")
                .id("testzone1")
                .lastModified(1461972046650L)
                .name("The Twiglet Zone[testzone1]")
                .subdomain("testzone1")
                .version(0)
                .build();
        }

        @Override
        protected UpdateIdentityZoneRequest getValidRequest() throws Exception {
            return UpdateIdentityZoneRequest.builder()
                .description("Like the Twilight Zone but tastier[testzone1].")
                .identityZoneId("testzone1")
                .name("The Twiglet Zone[testzone1]")
                .subdomain("testzone1")
                .build();
        }

        @Override
        protected Mono<UpdateIdentityZoneResponse> invoke(UpdateIdentityZoneRequest request) {
            return this.identityZoneManagement.update(request);
        }
    }

}
