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

package org.cloudfoundry.reactor.uaa.tokens;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cloudfoundry.reactor.uaa.AbstractUaaOperations;
import org.cloudfoundry.reactor.util.AuthorizationProvider;
import org.cloudfoundry.uaa.tokens.CheckTokenRequest;
import org.cloudfoundry.uaa.tokens.CheckTokenResponse;
import org.cloudfoundry.uaa.tokens.GetTokenByAuthorizationCodeRequest;
import org.cloudfoundry.uaa.tokens.GetTokenByAuthorizationCodeResponse;
import org.cloudfoundry.uaa.tokens.GetTokenByClientCredentialsRequest;
import org.cloudfoundry.uaa.tokens.GetTokenByClientCredentialsResponse;
import org.cloudfoundry.uaa.tokens.GetTokenByPasswordRequest;
import org.cloudfoundry.uaa.tokens.GetTokenByPasswordResponse;
import org.cloudfoundry.uaa.tokens.GetTokenKeyRequest;
import org.cloudfoundry.uaa.tokens.GetTokenKeyResponse;
import org.cloudfoundry.uaa.tokens.ListTokenKeysRequest;
import org.cloudfoundry.uaa.tokens.ListTokenKeysResponse;
import org.cloudfoundry.uaa.tokens.RefreshTokenRequest;
import org.cloudfoundry.uaa.tokens.RefreshTokenResponse;
import org.cloudfoundry.uaa.tokens.Tokens;
import reactor.core.publisher.Mono;
import reactor.io.netty.http.HttpClient;

import static org.cloudfoundry.util.tuple.TupleUtils.function;


public final class ReactorTokens extends AbstractUaaOperations implements Tokens {

    private final String clientId;

    private final String clientSecret;

    /**
     * Creates an instance
     *
     * @param authorizationProvider the {@link AuthorizationProvider} to use when communicating with the server
     * @param clientId              the client id
     * @param clientSecret          the client secret
     * @param httpClient            the {@link HttpClient} to use when communicating with the server
     * @param objectMapper          the {@link ObjectMapper} to use when communicating with the server
     * @param root                  the root URI of the server.  Typically something like {@code https://uaa.run.pivotal.io}.
     */
    public ReactorTokens(AuthorizationProvider authorizationProvider, String clientId, String clientSecret, HttpClient httpClient, ObjectMapper objectMapper, Mono<String> root) {
        super(authorizationProvider, httpClient, objectMapper, root);
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public Mono<CheckTokenResponse> check(CheckTokenRequest request) {
        return post(request, CheckTokenResponse.class, function((builder, validRequest) -> builder.pathSegment("check_token")),
            function((outbound, validRequest) -> basicAuth(outbound, this.clientId, this.clientSecret)));
    }

    @Override
    public Mono<GetTokenByAuthorizationCodeResponse> getByAuthorizationCode(GetTokenByAuthorizationCodeRequest request) {
        return post(request, GetTokenByAuthorizationCodeResponse.class,
            function((builder, validRequest) -> builder.pathSegment("oauth", "token").queryParam("grant_type", "authorization_code").queryParam("response_type", "token")));
    }

    @Override
    public Mono<GetTokenByClientCredentialsResponse> getByClientCredentials(GetTokenByClientCredentialsRequest request) {
        return post(request, GetTokenByClientCredentialsResponse.class,
            function((builder, validRequest) -> builder.pathSegment("oauth", "token").queryParam("grant_type", "client_credentials").queryParam("response_type", "token")));
    }

    @Override
    public Mono<GetTokenByPasswordResponse> getByPassword(GetTokenByPasswordRequest request) {
        return post(request, GetTokenByPasswordResponse.class,
            function((builder, validRequest) -> builder.pathSegment("oauth", "token").queryParam("grant_type", "password").queryParam("response_type", "token")));
    }

    @Override
    public Mono<GetTokenKeyResponse> getKey(GetTokenKeyRequest request) {
        return get(request, GetTokenKeyResponse.class, function((builder, validRequest) -> builder.pathSegment("token_key")));
    }

    @Override
    public Mono<ListTokenKeysResponse> listKeys(ListTokenKeysRequest request) {
        return get(request, ListTokenKeysResponse.class, function((builder, validRequest) -> builder.pathSegment("token_keys")));
    }

    @Override
    public Mono<RefreshTokenResponse> refresh(RefreshTokenRequest request) {
        return post(request, RefreshTokenResponse.class,
            function((builder, validRequest) -> builder.pathSegment("oauth", "token").queryParam("grant_type", "refresh_token")));
    }

}
