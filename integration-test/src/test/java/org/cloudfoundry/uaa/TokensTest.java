/*
 * Copyright 2013-2017 the original author or authors.
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

package org.cloudfoundry.uaa;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.uaa.tokens.CheckTokenRequest;
import org.cloudfoundry.uaa.tokens.GetTokenByAuthorizationCodeRequest;
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
import org.cloudfoundry.uaa.tokens.RefreshTokenRequest;
import org.cloudfoundry.uaa.tokens.TokenFormat;
import org.cloudfoundry.uaa.tokens.TokenKey;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

public final class TokensTest extends AbstractIntegrationTest {

    @Autowired
    private String clientId;

    @Autowired
    private String clientSecret;

    @Autowired
    private ConnectionContext connectionContext;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private UaaClient uaaClient;

    @Test
    public void checkTokenNotAuthorized() throws TimeoutException, InterruptedException {
        this.tokenProvider.getToken(this.connectionContext)
            .flatMap(token -> this.uaaClient.tokens()
                .check(CheckTokenRequest.builder()
                    .token(token)
                    .clientId(this.clientId)
                    .clientSecret(this.clientSecret)
                    .scope("password.write")
                    .scope("scim.userids")
                    .build()))
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(UaaException.class).hasMessage("access_denied: Access is denied"))
            .verify(Duration.ofMinutes(5));
    }

    //TODO: Ready to Implement - use test authorizationCode
    @Ignore("Ready to Implement - use test authorizationCode")
    @Test
    public void getTokenByAuthorizationCode() throws TimeoutException, InterruptedException {
        this.uaaClient.tokens()
            .getByAuthorizationCode(GetTokenByAuthorizationCodeRequest.builder()
                .authorizationCode("some auth code")
                .clientId(this.clientId)
                .clientSecret(this.clientSecret)
                .build())
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void getTokenByClientCredentials() throws TimeoutException, InterruptedException {
        this.uaaClient.tokens()
            .getByClientCredentials(GetTokenByClientCredentialsRequest.builder()
                .clientId(this.clientId)
                .clientSecret(this.clientSecret)
                .tokenFormat(TokenFormat.OPAQUE)
                .build())
            .map(GetTokenByClientCredentialsResponse::getTokenType)
            .as(StepVerifier::create)
            .expectNext("bearer")
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    //TODO: Ready to Implement - use test one-time passcode
    @Ignore("Ready to Implement - use test one-time passcode")
    @Test
    public void getTokenByOneTimePasscode() throws TimeoutException, InterruptedException {
        this.uaaClient.tokens()
            .getByOneTimePasscode(GetTokenByOneTimePasscodeRequest.builder()
                .passcode("Some passcode")
                .clientId(this.clientId)
                .clientSecret(this.clientSecret)
                .tokenFormat(TokenFormat.OPAQUE)
                .build())
            .map(GetTokenByOneTimePasscodeResponse::getTokenType)
            .as(StepVerifier::create)
            .expectNext("bearer")
            .expectComplete();
    }

    //TODO: Ready to Implement - use test openid authorizationCode
    @Ignore("Ready to Implement - use test openid authorizationCode")
    @Test
    public void getTokenByOpenId() throws TimeoutException, InterruptedException {
        this.uaaClient.tokens()
            .getByOpenId(GetTokenByOpenIdRequest.builder()
                .authorizationCode("Some authorization code")
                .clientId(this.clientId)
                .clientSecret(this.clientSecret)
                .tokenFormat(TokenFormat.OPAQUE)
                .build())
            .map(GetTokenByOpenIdResponse::getTokenType)
            .as(StepVerifier::create)
            .expectNext("bearer")
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    //TODO: Ready to Implement - use test username and password
    @Ignore("Ready to Implement - use test username and password")
    @Test
    public void getTokenByPassword() throws TimeoutException, InterruptedException {
        this.uaaClient.tokens()
            .getByPassword(GetTokenByPasswordRequest.builder()
                .password("a-password")
                .username("a-username")
                .clientId(this.clientId)
                .clientSecret(this.clientSecret)
                .tokenFormat(TokenFormat.OPAQUE)
                .build())
            .map(GetTokenByPasswordResponse::getTokenType)
            .as(StepVerifier::create)
            .expectNext("bearer")
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void getTokenKey() throws TimeoutException, InterruptedException {
        this.uaaClient.tokens()
            .getKey(GetTokenKeyRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listTokenKeys() throws TimeoutException, InterruptedException {
        this.uaaClient.tokens()
            .getKey(GetTokenKeyRequest.builder()
                .build())
            .flatMap(getKey -> Mono
                .when(
                    this.uaaClient.tokens()
                        .listKeys(ListTokenKeysRequest.builder()
                            .build())
                        .flatMapMany(response -> Flux.fromIterable(response.getKeys()))
                        .filter(tokenKey -> getKey.getValue().equals(tokenKey.getValue()))
                        .single()
                        .map(TokenKey::getId),
                    Mono.just(getKey)
                        .map(GetTokenKeyResponse::getId)
                ))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    //TODO: Ready to Implement - use test refresh token
    @Ignore("Ready to Implement - use test refresh token")
    @Test
    public void refreshToken() throws TimeoutException, InterruptedException {
        this.uaaClient.tokens()
            .refresh(RefreshTokenRequest.builder()
                .tokenFormat(TokenFormat.OPAQUE)
                .clientId(this.clientId)
                .clientSecret(this.clientSecret)
                .refreshToken("a-refresh-token")
                .build())
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

}
