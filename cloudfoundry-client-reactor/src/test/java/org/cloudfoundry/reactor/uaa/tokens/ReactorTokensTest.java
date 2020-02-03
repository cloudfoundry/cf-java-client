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

import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.uaa.AbstractUaaApiTest;
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
import org.cloudfoundry.uaa.tokens.KeyType;
import org.cloudfoundry.uaa.tokens.ListTokenKeysRequest;
import org.cloudfoundry.uaa.tokens.ListTokenKeysResponse;
import org.cloudfoundry.uaa.tokens.RefreshTokenRequest;
import org.cloudfoundry.uaa.tokens.RefreshTokenResponse;
import org.cloudfoundry.uaa.tokens.TokenFormat;
import org.cloudfoundry.uaa.tokens.TokenKey;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorTokensTest extends AbstractUaaApiTest {

    private final ReactorTokens tokens = new ReactorTokens(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void check() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/check_token?scopes=password.write%2Cscim.userids&token=f9f2f98d88e04ff7bb1f69041d3c0346")
                .header("Authorization", "Basic YXBwOmFwcGNsaWVudHNlY3JldA==")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/check/POST_response.json")
                .build())
            .build());

        this.tokens
            .check(CheckTokenRequest.builder()
                .token("f9f2f98d88e04ff7bb1f69041d3c0346")
                .scope("password.write")
                .scope("scim.userids")
                .clientId("app")
                .clientSecret("appclientsecret")
                .build())
            .as(StepVerifier::create)
            .expectNext(CheckTokenResponse.builder()
                .userId("ae77988e-1b25-4e02-87f2-81f98293a356")
                .userName("marissa")
                .email("marissa@test.org")
                .clientId("app")
                .expirationTime(1462015244L)
                .scopes(Arrays.asList("scim.userids", "openid", "cloud_controller.read", "password.write", "cloud_controller.write"))
                .jwtId("f9f2f98d88e04ff7bb1f69041d3c0346")
                .audiences(Arrays.asList("app", "scim", "openid", "cloud_controller", "password"))
                .subject("ae77988e-1b25-4e02-87f2-81f98293a356")
                .issuer("http://localhost:8080/uaa/oauth/token")
                .issuedAt(1461972044L)
                .cid("app")
                .grantType("password")
                .authorizedParty("app")
                .authorizationTime(1461972044L)
                .zoneId("uaa")
                .revocationSignature("4e89e4da")
                .origin("uaa")
                .revocable(true)
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getKey() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/token_key")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/token_key/GET_response.json")
                .build())
            .build());

        this.tokens
            .getKey(GetTokenKeyRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNext(GetTokenKeyResponse.builder()
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
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getTokenByAuthorizationCode() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/oauth/token?code=zI6Z1X&client_id=login&client_secret=loginsecret&redirect_uri=https%3A%2F%2Fuaa.cloudfoundry.com%2Fredirect%2Fcf" +
                    "&token_format=opaque&grant_type=authorization_code&response_type=token")
                .header("Authorization", null)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/tokens/GET_response_AC.json")
                .build())
            .build());

        this.tokens
            .getByAuthorizationCode(GetTokenByAuthorizationCodeRequest.builder()
                .clientId("login")
                .clientSecret("loginsecret")
                .authorizationCode("zI6Z1X")
                .redirectUri("https://uaa.cloudfoundry.com/redirect/cf")
                .tokenFormat(TokenFormat.OPAQUE)
                .build())
            .as(StepVerifier::create)
            .expectNext(GetTokenByAuthorizationCodeResponse.builder()
                .accessToken("555e2047bbc849628ff8cbfa7b342274")
                .tokenType("bearer")
                .refreshToken("555e2047bbc849628ff8cbfa7b342274-r")
                .expiresInSeconds(43199)
                .scopes("openid oauth.approvals")
                .tokenId("555e2047bbc849628ff8cbfa7b342274")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getTokenByClientCredentials() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/oauth/token?client_id=login&client_secret=loginsecret&token_format=opaque&grant_type=client_credentials&response_type=token")
                .header("Authorization", null)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/tokens/GET_response_CC.json")
                .build())
            .build());

        this.tokens
            .getByClientCredentials(GetTokenByClientCredentialsRequest.builder()
                .clientId("login")
                .clientSecret("loginsecret")
                .tokenFormat(TokenFormat.OPAQUE)
                .build())
            .as(StepVerifier::create)
            .expectNext(GetTokenByClientCredentialsResponse.builder()
                .accessToken("f87f93a2666d4e6eaa54e34df86d160c")
                .tokenType("bearer")
                .expiresInSeconds(43199)
                .scopes("clients.read emails.write scim.userids password.write idps.write notifications.write oauth.login scim.write critical_notifications.write")
                .tokenId("f87f93a2666d4e6eaa54e34df86d160c")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getTokenByOneTimePasscode() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/oauth/token?client_id=app&client_secret=appclientsecret&passcode=qcZNkd&token_format=opaque&grant_type=password&response_type=token")
                .header("Authorization", null)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/tokens/GET_response_OT.json")
                .build())
            .build());

        this.tokens
            .getByOneTimePasscode(GetTokenByOneTimePasscodeRequest.builder()
                .clientId("app")
                .clientSecret("appclientsecret")
                .passcode("qcZNkd")
                .tokenFormat(TokenFormat.OPAQUE)
                .build())
            .as(StepVerifier::create)
            .expectNext(GetTokenByOneTimePasscodeResponse.builder()
                .accessToken("0ddcada64ef742a28badaf4750ef435f")
                .tokenType("bearer")
                .refreshToken("0ddcada64ef742a28badaf4750ef435f-r")
                .expiresInSeconds(43199)
                .scopes("scim.userids openid cloud_controller.read password.write cloud_controller.write")
                .tokenId("0ddcada64ef742a28badaf4750ef435f")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getTokenByOpenId() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/oauth/token?code=NAlA1d&client_id=app&client_secret=appclientsecret&redirect_uri=https%3A%2F%2Fuaa.cloudfoundry.com%2Fredirect%2Fcf&token_format=opaque" +
                    "&grant_type=authorization_code&response_type=id_token")
                .header("Authorization", null)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/tokens/GET_response_OI.json")
                .build())
            .build());

        this.tokens
            .getByOpenId(GetTokenByOpenIdRequest.builder()
                .clientId("app")
                .clientSecret("appclientsecret")
                .authorizationCode("NAlA1d")
                .redirectUri("https://uaa.cloudfoundry.com/redirect/cf")
                .tokenFormat(TokenFormat.OPAQUE)
                .build())
            .as(StepVerifier::create)
            .expectNext(GetTokenByOpenIdResponse.builder()
                .accessToken("53a58e6581ee49d08f9e572f673bc8db")
                .tokenType("bearer")
                .openIdToken("eyJhbGciOiJIUzI1NiIsImtpZCI6ImxlZ2FjeS10b2tlbi1rZXkiLC")
                .refreshToken("53a58e6581ee49d08f9e572f673bc8db-r")
                .expiresInSeconds(43199)
                .scopes("openid oauth.approvals")
                .tokenId("53a58e6581ee49d08f9e572f673bc8db")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getTokenByPassword() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/oauth/token?client_id=app&client_secret=appclientsecret&password=secr3T&token_format=opaque&" +
                    "username=jENeJj%40test.org&grant_type=password&response_type=token")
                .header("Authorization", null)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/tokens/GET_response_PW.json")
                .build())
            .build());

        this.tokens
            .getByPassword(GetTokenByPasswordRequest.builder()
                .clientId("app")
                .clientSecret("appclientsecret")
                .password("secr3T")
                .tokenFormat(TokenFormat.OPAQUE)
                .username("jENeJj@test.org")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetTokenByPasswordResponse.builder()
                .accessToken("cd37a35114084fafb83d21c6f2af0e84")
                .tokenType("bearer")
                .refreshToken("cd37a35114084fafb83d21c6f2af0e84-r")
                .expiresInSeconds(43199)
                .scopes("scim.userids openid cloud_controller.read password.write cloud_controller.write")
                .tokenId("cd37a35114084fafb83d21c6f2af0e84")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listKeys() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/token_keys")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/token_keys/GET_response.json")
                .build())
            .build());

        this.tokens
            .listKeys(ListTokenKeysRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNext(ListTokenKeysResponse.builder()
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
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void refreshToken() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/oauth/token?client_id=app&client_secret=appclientsecret&refresh_token=6af5fc07a8b74c2eafb0079ff477bb11-r&token_format=opaque&grant_type=refresh_token")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/tokens/GET_refresh_response.json")
                .build())
            .build());

        this.tokens
            .refresh(RefreshTokenRequest.builder()
                .clientId("app")
                .clientSecret("appclientsecret")
                .refreshToken("6af5fc07a8b74c2eafb0079ff477bb11-r")
                .tokenFormat(TokenFormat.OPAQUE)
                .build())
            .as(StepVerifier::create)
            .expectNext(RefreshTokenResponse.builder()
                .accessToken("eyJhbGciOiJIUzI1NiIsImtpZCI6Imx")
                .tokenType("bearer")
                .refreshToken("eyJhbGciOiJIUzI1NiIsImtpZCI6Imx_E")
                .expiresInSeconds(43199)
                .scopes("scim.userids cloud_controller.read password.write cloud_controller.write openid")
                .tokenId("6af5fc07a8b74c2eafb0079ff477bb11")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
