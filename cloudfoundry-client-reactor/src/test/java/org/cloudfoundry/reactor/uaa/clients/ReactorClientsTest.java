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

package org.cloudfoundry.reactor.uaa.clients;

import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.uaa.AbstractUaaApiTest;
import org.cloudfoundry.uaa.clients.CreateClientRequest;
import org.cloudfoundry.uaa.clients.CreateClientResponse;
import org.cloudfoundry.uaa.clients.GetClientRequest;
import org.cloudfoundry.uaa.clients.GetClientResponse;
import org.cloudfoundry.uaa.clients.UpdateClientRequest;
import org.cloudfoundry.uaa.clients.UpdateClientResponse;
import reactor.core.publisher.Mono;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorClientsTest {

    public static final class Create extends AbstractUaaApiTest<CreateClientRequest, CreateClientResponse> {

        private final ReactorClients clients = new ReactorClients(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(POST).path("/oauth/clients")
                    .payload("fixtures/uaa/clients/POST_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(CREATED)
                    .payload("fixtures/uaa/clients/POST_response.json")
                    .build())
                .build();
        }

        @Override
        protected CreateClientResponse getResponse() {
            return CreateClientResponse.builder()
                .allowedProvider("uaa")
                .allowedProvider("ldap")
                .allowedProvider("my-saml-provider")
                .authority("clients.read")
                .authority("clients.write")
                .authorizedGrantType("client_credentials")
                .autoApprove("true")
                .clientId("aPq3I1")
                .lastModified(1468364445109L)
                .name("My Client Name")
                .redirectUriPattern("http*://ant.path.wildcard/**/passback/*")
                .redirectUriPattern("http://test1.com")
                .resourceId("none")
                .scope("clients.read")
                .scope("clients.write")
                .tokenSalt("hRZ21X")
                .build();
        }

        @Override
        protected CreateClientRequest getValidRequest() throws Exception {
            return CreateClientRequest.builder()
                .allowedProvider("uaa")
                .allowedProvider("ldap")
                .allowedProvider("my-saml-provider")
                .authority("clients.read")
                .authority("clients.write")
                .authorizedGrantType("client_credentials")
                .autoApprove("true")
                .clientId("aPq3I1")
                .clientSecret("secret")
                .name("My Client Name")
                .redirectUriPattern("http://test1.com")
                .redirectUriPattern("http*://ant.path.wildcard/**/passback/*")
                .scope("clients.read")
                .scope("clients.write")
                .tokenSalt("hRZ21X")
                .build();
        }

        @Override
        protected Mono<CreateClientResponse> invoke(CreateClientRequest request) {
            return this.clients.create(request);
        }
    }

    public static final class Get extends AbstractUaaApiTest<GetClientRequest, GetClientResponse> {

        private final ReactorClients clients = new ReactorClients(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/oauth/clients/test-client-id")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/uaa/clients/GET_{id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected GetClientResponse getResponse() {
            return GetClientResponse.builder()
                .allowedProvider("uaa")
                .allowedProvider("ldap")
                .allowedProvider("my-saml-provider")
                .authority("clients.read")
                .authority("clients.write")
                .authorizedGrantType("client_credentials")
                .autoApprove("true")
                .clientId("4Z3t1r")
                .lastModified(1468364445592L)
                .name("My Client Name")
                .redirectUriPattern("http*://ant.path.wildcard/**/passback/*")
                .redirectUriPattern("http://test1.com")
                .resourceId("none")
                .scope("clients.read")
                .scope("clients.write")
                .tokenSalt("mr80UZ")
                .build();
        }

        @Override
        protected GetClientRequest getValidRequest() throws Exception {
            return GetClientRequest.builder()
                .clientId("test-client-id")
                .build();
        }

        @Override
        protected Mono<GetClientResponse> invoke(GetClientRequest request) {
            return this.clients.get(request);
        }
    }

    public static final class Update extends AbstractUaaApiTest<UpdateClientRequest, UpdateClientResponse> {

        private final ReactorClients clients = new ReactorClients(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(PUT).path("/oauth/clients/55pTMX")
                    .payload("fixtures/uaa/clients/PUT_{id}_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/uaa/clients/PUT_{id}_response.json")
                    .build())
                .build();
}
        @Override
        protected UpdateClientResponse getResponse() {
            return UpdateClientResponse.builder()
                .allowedProvider("uaa", "ldap", "my-saml-provider")
                .authority("clients.read", "clients.write")
                .authorizedGrantType("client_credentials")
                .autoApprove("clients.autoapprove")
                .clientId("55pTMX")
                .lastModified(1468364443857L)
                .name("My Client Name")
                .redirectUriPattern("http*://ant.path.wildcard/**/passback/*", "http://test1.com")
                .resourceId("none")
                .scope("clients.new", "clients.autoapprove")
                .tokenSalt("8mwCEy")
                .build();
        }

        @Override
        protected UpdateClientRequest getValidRequest() throws Exception {
            return UpdateClientRequest.builder()
                .authorizedGrantType("client_credentials")
                .autoApprove("clients.autoapprove")
                .clientId("55pTMX")
                .scope("clients.new", "clients.autoapprove")
                .build();
        }

        @Override
        protected Mono<UpdateClientResponse> invoke(UpdateClientRequest request) {
            return this.clients.update(request);
        }
    }

}
