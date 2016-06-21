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

package org.cloudfoundry.reactor.uaa.authorizations;

import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.uaa.AbstractUaaApiTest;
import org.cloudfoundry.uaa.authorizations.AuthorizeByAuthorizationCodeGrantApiRequest;
import org.cloudfoundry.uaa.authorizations.AuthorizeByAuthorizationCodeGrantBrowserRequest;
import org.cloudfoundry.uaa.authorizations.AuthorizeByImplicitGrantBrowserRequest;
import reactor.core.publisher.Mono;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.FOUND;

public final class ReactorAuthorizationsTest {

    public static final class AuthorizeByAuthorizationCodeGrantApi extends AbstractUaaApiTest<AuthorizeByAuthorizationCodeGrantApiRequest, String> {

        private final ReactorAuthorizations authorizations = new ReactorAuthorizations(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/oauth/authorize?client_id=login&redirect_uri=https://uaa.cloudfoundry.com/redirect/cf&state=v4LpFF&response_type=code")
                    .build())
                .response(TestResponse.builder()
                    .status(FOUND)
                    .header("Location", "https://uaa.cloudfoundry.com/redirect/cf?code=O6A5eT&state=v4LpFF")
                    .build())
                .build();
        }

        @Override
        protected String getResponse() {
            return "O6A5eT";
        }

        @Override
        protected AuthorizeByAuthorizationCodeGrantApiRequest getValidRequest() throws Exception {
            return AuthorizeByAuthorizationCodeGrantApiRequest.builder()
                .clientId("login")
                .redirectUri("https://uaa.cloudfoundry.com/redirect/cf")
                .state("v4LpFF")
                .build();
        }

        @Override
        protected Mono<String> invoke(AuthorizeByAuthorizationCodeGrantApiRequest request) {
            return this.authorizations.authorizationCodeGrantApi(request);
        }
    }

    public static final class AuthorizeByAuthorizationCodeGrantBrowser extends AbstractUaaApiTest<AuthorizeByAuthorizationCodeGrantBrowserRequest, String> {

        private final ReactorAuthorizations authorizations = new ReactorAuthorizations(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/oauth/authorize?client_id=login&redirect_uri=https://uaa.cloudfoundry.com/redirect/cf&scope=openid%20oauth.approvals&response_type=code")
                    .build())
                .response(TestResponse.builder()
                    .status(FOUND)
                    .header("Location", "http://redirect.to/login")
                    .build())
                .build();
        }

        @Override
        protected String getResponse() {
            return "http://redirect.to/login";
        }

        @Override
        protected AuthorizeByAuthorizationCodeGrantBrowserRequest getValidRequest() throws Exception {
            return AuthorizeByAuthorizationCodeGrantBrowserRequest.builder()
                .clientId("login")
                .redirectUri("https://uaa.cloudfoundry.com/redirect/cf")
                .scope("openid")
                .scope("oauth.approvals")
                .build();
        }

        @Override
        protected Mono<String> invoke(AuthorizeByAuthorizationCodeGrantBrowserRequest request) {
            return this.authorizations.authorizationCodeGrantBrowser(request);
        }
    }

    public static final class AuthorizeByImplicitGrantBrowser extends AbstractUaaApiTest<AuthorizeByImplicitGrantBrowserRequest, String> {

        private final ReactorAuthorizations authorizations = new ReactorAuthorizations(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/oauth/authorize?client_id=app&redirect_uri=http://localhost:8080/app/&scope=openid&response_type=token")
                    .build())
                .response(TestResponse.builder()
                    .status(FOUND)
                    .header("Location", "http://localhost:8080/app/#token_type=bearer&" +
                        "access_token=eyJhbGciOiJIUzI1NiIsImtpZCI6ImxlZ2FjeS10b2tlbi1rZXkiLCJ0eXAiOiJKV1QifQ.eyJqdGkiOiJlNzI4Y2UxZjUyZjE0NTU2YjViNGNiOThkMmY1ZmRiZCIsInN1YiI6IjIzOTJhMzIwLTQzZWUtNDV" +
                        "expires_in=43199&" +
                        "jti=e728ce1f52f14556b5b4cb98d2f5fdbd")
                    .build())
                .build();
        }

        @Override
        protected String getResponse() {
            return "http://localhost:8080/app/#token_type=bearer&" +
                "access_token=eyJhbGciOiJIUzI1NiIsImtpZCI6ImxlZ2FjeS10b2tlbi1rZXkiLCJ0eXAiOiJKV1QifQ.eyJqdGkiOiJlNzI4Y2UxZjUyZjE0NTU2YjViNGNiOThkMmY1ZmRiZCIsInN1YiI6IjIzOTJhMzIwLTQzZWUtNDV" +
                "expires_in=43199&" +
                "jti=e728ce1f52f14556b5b4cb98d2f5fdbd";
        }

        @Override
        protected AuthorizeByImplicitGrantBrowserRequest getValidRequest() throws Exception {
            return AuthorizeByImplicitGrantBrowserRequest.builder()
                .clientId("app")
                .redirectUri("http://localhost:8080/app/")
                .scope("openid")
                .build();
        }

        @Override
        protected Mono<String> invoke(AuthorizeByImplicitGrantBrowserRequest request) {
            return this.authorizations.implicitGrantBrowser(request);
        }
    }

}
