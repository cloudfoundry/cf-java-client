/*
 * Copyright 2013-2020 the original author or authors.
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

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.AsciiString;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.uaa.AbstractUaaOperations;
import org.cloudfoundry.uaa.ResponseType;
import org.cloudfoundry.uaa.tokens.CheckTokenRequest;
import org.cloudfoundry.uaa.tokens.CheckTokenResponse;
import org.cloudfoundry.uaa.tokens.GetTokenByAuthorizationCodeRequest;
import org.cloudfoundry.uaa.tokens.GetTokenByAuthorizationCodeResponse;
import org.cloudfoundry.uaa.tokens.GetTokenByClientCredentialsRequest;
import org.cloudfoundry.uaa.tokens.GetTokenByClientCredentialsResponse;
import org.cloudfoundry.uaa.tokens.GetTokenByOneTimePasscodeRequest;
import org.cloudfoundry.uaa.tokens.GetTokenByOneTimePasscodeResponse;
import org.cloudfoundry.uaa.tokens.GetTokenByOpenIdRequest;
import org.cloudfoundry.uaa.tokens.GetTokenByOpenIdResponse;
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

import java.util.Base64;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.AUTHORIZATION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED;
import static org.cloudfoundry.uaa.tokens.GrantType.AUTHORIZATION_CODE;
import static org.cloudfoundry.uaa.tokens.GrantType.CLIENT_CREDENTIALS;
import static org.cloudfoundry.uaa.tokens.GrantType.PASSWORD;
import static org.cloudfoundry.uaa.tokens.GrantType.REFRESH_TOKEN;

public final class ReactorTokens extends AbstractUaaOperations implements Tokens {

    private static final AsciiString BASIC_PREAMBLE = new AsciiString("Basic ");

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://uaa.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorTokens(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<CheckTokenResponse> check(CheckTokenRequest request) {
        return post(request, CheckTokenResponse.class, builder -> builder.pathSegment("check_token"),
            outbound -> {
            },
            outbound -> {
                String encoded = Base64.getEncoder().encodeToString(new AsciiString(request.getClientId()).concat(":").concat(request.getClientSecret()).toByteArray());
                return Mono.just(outbound.set(AUTHORIZATION, BASIC_PREAMBLE + encoded));
            })
            .checkpoint();
    }

    @Override
    public Mono<GetTokenByAuthorizationCodeResponse> getByAuthorizationCode(GetTokenByAuthorizationCodeRequest request) {
        return post(request, GetTokenByAuthorizationCodeResponse.class, builder -> builder.pathSegment("oauth", "token")
                .queryParam("grant_type", AUTHORIZATION_CODE)
                .queryParam("response_type", ResponseType.TOKEN),
            ReactorTokens::setUrlEncoded,
            ReactorTokens::removeAuthorization)
            .checkpoint();
    }

    @Override
    public Mono<GetTokenByClientCredentialsResponse> getByClientCredentials(GetTokenByClientCredentialsRequest request) {
        return post(request, GetTokenByClientCredentialsResponse.class, builder -> builder.pathSegment("oauth", "token")
                .queryParam("grant_type", CLIENT_CREDENTIALS)
                .queryParam("response_type", ResponseType.TOKEN),
            ReactorTokens::setUrlEncoded,
            ReactorTokens::removeAuthorization)
            .checkpoint();
    }

    @Override
    public Mono<GetTokenByOneTimePasscodeResponse> getByOneTimePasscode(GetTokenByOneTimePasscodeRequest request) {
        return post(request, GetTokenByOneTimePasscodeResponse.class, builder -> builder.pathSegment("oauth", "token")
                .queryParam("grant_type", PASSWORD)
                .queryParam("response_type", ResponseType.TOKEN),
            ReactorTokens::setUrlEncoded,
            ReactorTokens::removeAuthorization)
            .checkpoint();
    }

    @Override
    public Mono<GetTokenByOpenIdResponse> getByOpenId(GetTokenByOpenIdRequest request) {
        return post(request, GetTokenByOpenIdResponse.class, builder -> builder.pathSegment("oauth", "token")
                .queryParam("grant_type", AUTHORIZATION_CODE)
                .queryParam("response_type", ResponseType.ID_TOKEN),
            ReactorTokens::setUrlEncoded,
            ReactorTokens::removeAuthorization)
            .checkpoint();
    }

    @Override
    public Mono<GetTokenByPasswordResponse> getByPassword(GetTokenByPasswordRequest request) {
        return post(request, GetTokenByPasswordResponse.class, builder -> builder.pathSegment("oauth", "token")
                .queryParam("grant_type", PASSWORD)
                .queryParam("response_type", ResponseType.TOKEN),
            ReactorTokens::setUrlEncoded,
            ReactorTokens::removeAuthorization)
            .checkpoint();
    }

    @Override
    public Mono<GetTokenKeyResponse> getKey(GetTokenKeyRequest request) {
        return get(request, GetTokenKeyResponse.class, builder -> builder.pathSegment("token_key"))
            .checkpoint();
    }

    @Override
    public Mono<ListTokenKeysResponse> listKeys(ListTokenKeysRequest request) {
        return get(request, ListTokenKeysResponse.class, builder -> builder.pathSegment("token_keys"))
            .checkpoint();
    }

    @Override
    public Mono<RefreshTokenResponse> refresh(RefreshTokenRequest request) {
        return post(request, RefreshTokenResponse.class, builder -> builder.pathSegment("oauth", "token")
                .queryParam("grant_type", REFRESH_TOKEN),
            ReactorTokens::setUrlEncoded,
            ReactorTokens::removeAuthorization)
            .checkpoint();
    }

    private static Mono<HttpHeaders> removeAuthorization(HttpHeaders request) {
        return Mono.just(request.remove(AUTHORIZATION));
    }

    private static void setUrlEncoded(HttpHeaders request) {
        request.set(CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);
    }

}
