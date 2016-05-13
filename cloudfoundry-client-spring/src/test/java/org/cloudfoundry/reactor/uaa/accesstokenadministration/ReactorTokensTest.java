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

package org.cloudfoundry.reactor.uaa.accesstokenadministration;

import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.uaa.AbstractUaaApiTest;
import org.cloudfoundry.uaa.tokens.AbstractTokenKey.KeyType;
import org.cloudfoundry.uaa.tokens.GetTokenByAuthorizationCodeRequest;
import org.cloudfoundry.uaa.tokens.GetTokenByAuthorizationCodeResponse;
import org.cloudfoundry.uaa.tokens.GetTokenKeyRequest;
import org.cloudfoundry.uaa.tokens.GetTokenKeyResponse;
import org.cloudfoundry.uaa.tokens.ListTokenKeysRequest;
import org.cloudfoundry.uaa.tokens.ListTokenKeysResponse;
import org.cloudfoundry.uaa.tokens.ListTokenKeysResponse.TokenKey;
import org.cloudfoundry.uaa.tokens.TokenFormat;
import reactor.core.publisher.Mono;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorTokensTest {

    public static final class GetKey extends AbstractUaaApiTest<GetTokenKeyRequest, GetTokenKeyResponse> {

        private final ReactorTokens tokens = new ReactorTokens(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/token_key")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/uaa/token_key/GET_response.json")
                    .build())
                .build();
        }

        @Override
        protected GetTokenKeyRequest getInvalidRequest() {
            return null;
        }

        @Override
        protected GetTokenKeyResponse getResponse() {
            return GetTokenKeyResponse.builder()
                .id("testKey")
                .algorithm("SHA256withRSA")
                .value("-----BEGIN PUBLIC KEY-----\n" +
                    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0m59l2u9iDnMbrXHfqkO\n" +
                    "rn2dVQ3vfBJqcDuFUK03d+1PZGbVlNCqnkpIJ8syFppW8ljnWweP7+LiWpRoz0I7\n" +
                    "fYb3d8TjhV86Y997Fl4DBrxgM6KTJOuE/uxnoDhZQ14LgOU2ckXjOzOdTsnGMKQB\n" +
                    "LCl0vpcXBtFLMaSbpv1ozi8h7DJyVZ6EnFQZUWGdgTMhDrmqevfx95U/16c5WBDO\n" +
                    "kqwIn7Glry9n9Suxygbf8g5AzpWcusZgDLIIZ7JTUldBb8qU2a0Dl4mvLZOn4wPo\n" +
                    "jfj9Cw2QICsc5+Pwf21fP+hzf+1WSRHbnYv8uanRO0gZ8ekGaghM/2H6gqJbo2nI\n" +
                    "JwIDAQAB\n" +
                    "-----END PUBLIC KEY-----")
                .keyType(KeyType.RSA)
                .use("sig")
                .n("ANJufZdrvYg5zG61x36pDq59nVUN73wSanA7hVCtN3ftT2Rm1ZTQqp5KSCfLMhaaVvJY51sHj" +
                    "+/i4lqUaM9CO32G93fE44VfOmPfexZeAwa8YDOikyTrhP7sZ6A4WUNeC4DlNnJF4zsznU7JxjCkASwpdL6XFwbRSzGkm6b9aM4vIewyclWehJxUGVFhnYEzIQ65qnr38feVP9enOVgQzpKsCJ+xpa8vZ/UrscoG3" +
                    "/IOQM6VnLrGYAyyCGeyU1JXQW/KlNmtA5eJry2Tp+MD6I34/QsNkCArHOfj8H9tXz/oc3/tVkkR252L/Lmp0TtIGfHpBmoITP9h+oKiW6NpyCc=")
                .e("AQAB")
                .build();
        }

        @Override
        protected GetTokenKeyRequest getValidRequest() {
            return GetTokenKeyRequest.builder()
                .build();
        }

        @Override
        protected Mono<GetTokenKeyResponse> invoke(GetTokenKeyRequest request) {
            return this.tokens.getKey(request);
        }

    }

    public static final class GetTokenByAuthorizationCode extends AbstractUaaApiTest<GetTokenByAuthorizationCodeRequest, GetTokenByAuthorizationCodeResponse> {

        private final ReactorTokens tokens = new ReactorTokens(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(POST).path("/oauth/token?code=zI6Z1X&client_id=login&client_secret=loginsecret&redirect_uri=https://uaa.cloudfoundry.com/redirect/cf" +
                        "&token_format=opaque&grant_type=authorization_code&response_type=token")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/uaa/tokens/GET_response_AC.json")
                    .build())
                .build();
        }

        @Override
        protected GetTokenByAuthorizationCodeRequest getInvalidRequest() {
            return GetTokenByAuthorizationCodeRequest.builder().build();
        }

        @Override
        protected GetTokenByAuthorizationCodeResponse getResponse() {
            return GetTokenByAuthorizationCodeResponse.builder()
                .accessToken("555e2047bbc849628ff8cbfa7b342274")
                .tokenType("bearer")
                .refreshToken("555e2047bbc849628ff8cbfa7b342274-r")
                .expiresInSeconds(43199)
                .scopes("openid oauth.approvals")
                .tokenId("555e2047bbc849628ff8cbfa7b342274")
                .build();
        }

        @Override
        protected GetTokenByAuthorizationCodeRequest getValidRequest() {
            return GetTokenByAuthorizationCodeRequest.builder()
                .clientId("login")
                .clientSecret("loginsecret")
                .authorizationCode("zI6Z1X")
                .redirectUri("https://uaa.cloudfoundry.com/redirect/cf")
                .tokenFormat(TokenFormat.OPAQUE)
                .build();
        }

        @Override
        protected Mono<GetTokenByAuthorizationCodeResponse> invoke(GetTokenByAuthorizationCodeRequest request) {
            return this.tokens.getByAuthorizationCode(request);
        }

    }

    public static final class ListKeys extends AbstractUaaApiTest<ListTokenKeysRequest, ListTokenKeysResponse> {

        private final ReactorTokens tokens = new ReactorTokens(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/token_keys")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/uaa/token_keys/GET_response.json")
                    .build())
                .build();
        }

        @Override
        protected ListTokenKeysRequest getInvalidRequest() {
            return null;
        }

        @Override
        protected ListTokenKeysResponse getResponse() {
            return ListTokenKeysResponse.builder()
                .key(TokenKey.builder()
                    .id("testKey")
                    .algorithm("SHA256withRSA")
                    .value("-----BEGIN PUBLIC KEY-----\n" +
                        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0m59l2u9iDnMbrXHfqkO\n" +
                        "rn2dVQ3vfBJqcDuFUK03d+1PZGbVlNCqnkpIJ8syFppW8ljnWweP7+LiWpRoz0I7\n" +
                        "fYb3d8TjhV86Y997Fl4DBrxgM6KTJOuE/uxnoDhZQ14LgOU2ckXjOzOdTsnGMKQB\n" +
                        "LCl0vpcXBtFLMaSbpv1ozi8h7DJyVZ6EnFQZUWGdgTMhDrmqevfx95U/16c5WBDO\n" +
                        "kqwIn7Glry9n9Suxygbf8g5AzpWcusZgDLIIZ7JTUldBb8qU2a0Dl4mvLZOn4wPo\n" +
                        "jfj9Cw2QICsc5+Pwf21fP+hzf+1WSRHbnYv8uanRO0gZ8ekGaghM/2H6gqJbo2nI\n" +
                        "JwIDAQAB\n" +
                        "-----END PUBLIC KEY-----")
                    .keyType(KeyType.RSA)
                    .use("sig")
                    .n("ANJufZdrvYg5zG61x36pDq59nVUN73wSanA7hVCtN3ftT2Rm1ZTQqp5KSCfLMhaaVvJY51sHj" +
                        "+/i4lqUaM9CO32G93fE44VfOmPfexZeAwa8YDOikyTrhP7sZ6A4WUNeC4DlNnJF4zsznU7JxjCkASwpdL6XFwbRSzGkm6b9aM4vIewyclWehJxUGVFhnYEzIQ65qnr38feVP9enOVgQzpKsCJ+xpa8vZ/UrscoG3" +
                        "/IOQM6VnLrGYAyyCGeyU1JXQW/KlNmtA5eJry2Tp+MD6I34/QsNkCArHOfj8H9tXz/oc3/tVkkR252L/Lmp0TtIGfHpBmoITP9h+oKiW6NpyCc=")
                    .e("AQAB")
                    .build())
                .build();
        }

        @Override
        protected ListTokenKeysRequest getValidRequest() throws Exception {
            return ListTokenKeysRequest.builder()
                .build();
        }

        @Override
        protected Mono<ListTokenKeysResponse> invoke(ListTokenKeysRequest request) {
            return this.tokens.listKeys(request);
        }
    }

}
