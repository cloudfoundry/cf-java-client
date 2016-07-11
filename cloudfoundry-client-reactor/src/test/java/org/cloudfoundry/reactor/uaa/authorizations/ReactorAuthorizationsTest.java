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
import org.cloudfoundry.uaa.authorizations.AuthorizeByOpenIdWithAuthorizationCodeGrantRequest;
import org.cloudfoundry.uaa.authorizations.AuthorizeByOpenIdWithIdTokenRequest;
import org.cloudfoundry.uaa.authorizations.AuthorizeByOpenIdWithImplicitGrantRequest;
import reactor.core.publisher.Mono;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.FOUND;

public final class ReactorAuthorizationsTest {

    public static final class AuthorizeByAuthorizationCodeGrantApi extends AbstractUaaApiTest<AuthorizeByAuthorizationCodeGrantApiRequest, String> {

        private final ReactorAuthorizations authorizations = new ReactorAuthorizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

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

        private final ReactorAuthorizations authorizations = new ReactorAuthorizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

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

        private final ReactorAuthorizations authorizations = new ReactorAuthorizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

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

    public static final class AuthorizeByOpenIdWithAuthorizationCodeGrant extends AbstractUaaApiTest<AuthorizeByOpenIdWithAuthorizationCodeGrantRequest, String> {

        private final ReactorAuthorizations authorizations = new ReactorAuthorizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/oauth/authorize?client_id=app&redirect_uri=http://localhost:8080/app/&scope=openid&response_type=code%20id_token")
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
        protected AuthorizeByOpenIdWithAuthorizationCodeGrantRequest getValidRequest() throws Exception {
            return AuthorizeByOpenIdWithAuthorizationCodeGrantRequest.builder()
                .clientId("app")
                .redirectUri("http://localhost:8080/app/")
                .scope("openid")
                .build();
        }

        @Override
        protected Mono<String> invoke(AuthorizeByOpenIdWithAuthorizationCodeGrantRequest request) {
            return this.authorizations.openIdWithAuthorizationCodeGrant(request);
        }
    }

    public static final class AuthorizeByOpenIdWithToken extends AbstractUaaApiTest<AuthorizeByOpenIdWithIdTokenRequest, String> {

        private final ReactorAuthorizations authorizations = new ReactorAuthorizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/oauth/authorize?client_id=app&redirect_uri=http://localhost:8080/app/&scope=openid&response_type=id_token")
                    .build())
                .response(TestResponse.builder()
                    .status(FOUND)
                    .header("Location", "http://localhost:8080/app/#token_type=bearer" +
                        "&id_token=eyJhbGciOiJIUzI1NiIsImtpZCI6ImxlZ2FjeS10b2tlbi1rZXkiLCJ0eXAiOiJKV1QifQ.eyJzdWIiOiIyMzkyYTMyMC00M2VlLTQ1ZTgtODdhNC1iYTkzYTIwMTZmODciLCJ1c2VyX25hbWUiOiJtYXJpc3NhIiw" +
                        "ib3JpZ2luIjoidWFhIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3VhYS9vYXV0aC90b2tlbiIsImNsaWVudF9pZCI6ImFwcCIsImF1ZCI6WyJhcHAiXSwiemlkIjoidWFhIiwidXNlcl9pZCI6IjIzOTJhMzIwLTQzZWU" +
                        "tNDVlOC04N2E0LWJhOTNhMjAxNmY4NyIsImF6cCI6ImFwcCIsInNjb3BlIjpbIm9wZW5pZCJdLCJleHAiOjE0NjYwNzg0OTAsImlhdCI6MTQ2NjAzNTI5MCwianRpIjoiM2NjNDg2NmYzMWRjNGIyMThkMTdiZDNhMzE4MjhmNWU" +
                        "iLCJlbWFpbCI6Im1hcmlzc2FAdGVzdC5vcmciLCJyZXZfc2lnIjoiMzYyNzRiZmMiLCJjaWQiOiJhcHAifQ.zR0b0TVFY8VrxAXLve2VRZvwb9HWMtbD79KSHwgr1wo" +
                        "&expires_in=43199" +
                        "&jti=3cc4866f31dc4b218d17bd3a31828f5e")
                    .build())
                .build();
        }

        @Override
        protected String getResponse() {
            return "http://localhost:8080/app/#token_type=bearer" +
                "&id_token=eyJhbGciOiJIUzI1NiIsImtpZCI6ImxlZ2FjeS10b2tlbi1rZXkiLCJ0eXAiOiJKV1QifQ.eyJzdWIiOiIyMzkyYTMyMC00M2VlLTQ1ZTgtODdhNC1iYTkzYTIwMTZmODciLCJ1c2VyX25hbWUiOiJtYXJpc3NhIiwib3JpZ2l" +
                "uIjoidWFhIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3VhYS9vYXV0aC90b2tlbiIsImNsaWVudF9pZCI6ImFwcCIsImF1ZCI6WyJhcHAiXSwiemlkIjoidWFhIiwidXNlcl9pZCI6IjIzOTJhMzIwLTQzZWUtNDVlOC04N2E0LWJ" +
                "hOTNhMjAxNmY4NyIsImF6cCI6ImFwcCIsInNjb3BlIjpbIm9wZW5pZCJdLCJleHAiOjE0NjYwNzg0OTAsImlhdCI6MTQ2NjAzNTI5MCwianRpIjoiM2NjNDg2NmYzMWRjNGIyMThkMTdiZDNhMzE4MjhmNWUiLCJlbWFpbCI6Im1hcmlzc2F" +
                "AdGVzdC5vcmciLCJyZXZfc2lnIjoiMzYyNzRiZmMiLCJjaWQiOiJhcHAifQ.zR0b0TVFY8VrxAXLve2VRZvwb9HWMtbD79KSHwgr1wo" +
                "&expires_in=43199" +
                "&jti=3cc4866f31dc4b218d17bd3a31828f5e";
        }

        @Override
        protected AuthorizeByOpenIdWithIdTokenRequest getValidRequest() throws Exception {
            return AuthorizeByOpenIdWithIdTokenRequest.builder()
                .clientId("app")
                .redirectUri("http://localhost:8080/app/")
                .scope("openid")
                .build();
        }

        @Override
        protected Mono<String> invoke(AuthorizeByOpenIdWithIdTokenRequest request) {
            return this.authorizations.openIdWithIdToken(request);
        }
    }

    public static final class AuthorizeByOpenIdWithimplicitGrant extends AbstractUaaApiTest<AuthorizeByOpenIdWithImplicitGrantRequest, String> {

        private final ReactorAuthorizations authorizations = new ReactorAuthorizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/oauth/authorize?client_id=app&redirect_uri=http://localhost:8080/app/&scope=openid&response_type=token%20id_token")
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
        protected AuthorizeByOpenIdWithImplicitGrantRequest getValidRequest() throws Exception {
            return AuthorizeByOpenIdWithImplicitGrantRequest.builder()
                .clientId("app")
                .redirectUri("http://localhost:8080/app/")
                .scope("openid")
                .build();
        }

        @Override
        protected Mono<String> invoke(AuthorizeByOpenIdWithImplicitGrantRequest request) {
            return this.authorizations.openIdWithImplicitGrant(request);
        }
    }

}
