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

package org.cloudfoundry.uaa;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.uaa.serverinformation.AutoLoginRequest;
import org.cloudfoundry.uaa.serverinformation.GetAutoLoginAuthenticationCodeRequest;
import org.cloudfoundry.uaa.serverinformation.GetAutoLoginAuthenticationCodeResponse;
import org.cloudfoundry.uaa.serverinformation.GetInfoRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

public final class ServerInformationTest extends AbstractIntegrationTest {

    @Autowired
    private String clientId;

    @Autowired
    private String clientSecret;

    @Autowired
    private String password;

    @Autowired
    private UaaClient uaaClient;

    @Autowired
    private String username;

    @Test
    public void autoLogin() {
        getAuthenticationCode(this.uaaClient, this.clientId, this.clientSecret, this.password, this.username)
            .flatMap(code -> this.uaaClient.serverInformation()
                .autoLogin(AutoLoginRequest.builder()
                    .clientId(this.clientId)
                    .code(code)
                    .build()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void getAutoLoginAuthenticationCode() {
        this.uaaClient.serverInformation()
            .getAuthenticationCode(GetAutoLoginAuthenticationCodeRequest.builder()
                .clientId(this.clientId)
                .clientSecret(this.clientSecret)
                .password(this.password)
                .username(this.username)
                .build())
            .map(GetAutoLoginAuthenticationCodeResponse::getPath)
            .as(StepVerifier::create)
            .expectNext("/oauth/authorize")
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void getInfo() {
        this.uaaClient.serverInformation()
            .getInfo(GetInfoRequest.builder()
                .build())
            .map(response -> response.getLinks().getPassword())
            .as(StepVerifier::create)
            .consumeNextWith(endsWithExpectation("password"))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Consumer<String> endsWithExpectation(String suffix) {
        return actual -> assertThat(actual).endsWith(suffix);
    }

    private static Mono<String> getAuthenticationCode(UaaClient uaaClient, String clientId, String clientSecret, String password, String username) {
        return requestAuthenticationCode(uaaClient, clientId, clientSecret, password, username)
            .map(GetAutoLoginAuthenticationCodeResponse::getCode);
    }

    private static Mono<GetAutoLoginAuthenticationCodeResponse> requestAuthenticationCode(UaaClient uaaClient, String clientId, String clientSecret, String password, String username) {
        return uaaClient.serverInformation()
            .getAuthenticationCode(GetAutoLoginAuthenticationCodeRequest.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .password(password)
                .username(username)
                .build());
    }

}