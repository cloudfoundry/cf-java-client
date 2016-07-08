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
import org.cloudfoundry.uaa.clients.GetClientRequest;
import org.cloudfoundry.uaa.clients.GetClientResponse;
import reactor.core.publisher.Mono;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorClientsTest {

    public static final class Get extends AbstractUaaApiTest<GetClientRequest, GetClientResponse> {

        private final ReactorClients clients = new ReactorClients(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

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
                .authorityScope("clients.read")
                .authorityScope("clients.write")
                .authorizedGrantType("client_credentials")
                .autoApprove("true")
                .clientId("RoXWdB")
                .lastModified(1467059560412L)
                .name("My Client Name")
                .redirectUriPattern("http*://ant.path.wildcard/**/passback/*")
                .redirectUriPattern("http://test1.example.com")
                .resourceId("none")
                .scope("clients.read")
                .scope("clients.write")
                .tokenSalt("nNUNmH")
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

}