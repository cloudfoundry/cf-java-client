/*
 * Copyright 2013-2021 the original author or authors.
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

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.function.Consumer;
import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.uaa.serverinformation.AutoLoginRequest;
import org.cloudfoundry.uaa.serverinformation.GetAutoLoginAuthenticationCodeRequest;
import org.cloudfoundry.uaa.serverinformation.GetAutoLoginAuthenticationCodeResponse;
import org.cloudfoundry.uaa.serverinformation.GetInfoRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public final class ServerInformationTest extends AbstractIntegrationTest {

    @Autowired private String clientId;

    @Autowired private String clientSecret;

    @Autowired private String password;

    @Autowired private UaaClient uaaClient;

    @Autowired private String username;

    @Autowired private ConnectionContext context;

    @Test
    public void autoLogin() {
        getAuthenticationCode(
                        this.uaaClient,
                        this.clientId,
                        this.clientSecret,
                        this.password,
                        this.username)
                .flatMap(
                        code ->
                                this.uaaClient
                                        .serverInformation()
                                        .autoLogin(
                                                AutoLoginRequest.builder()
                                                        .clientId(this.clientId)
                                                        .code(code)
                                                        .build()))
                .as(StepVerifier::create)
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    @Test
    public void getAutoLoginAuthenticationCode() {
        this.uaaClient
                .serverInformation()
                .getAuthenticationCode(
                        GetAutoLoginAuthenticationCodeRequest.builder()
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
    @DisabledIf(value = "tasSpecificUaaVersion")
    public void getInfo() {
        this.uaaClient
                .serverInformation()
                .getInfo(GetInfoRequest.builder().build())
                .map(response -> response.getLinks().getPassword())
                .as(StepVerifier::create)
                .consumeNextWith(endsWithExpectation("password"))
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    private static Consumer<String> endsWithExpectation(String suffix) {
        return actual -> assertThat(actual).endsWith(suffix);
    }

    private static Mono<String> getAuthenticationCode(
            UaaClient uaaClient,
            String clientId,
            String clientSecret,
            String password,
            String username) {
        return requestAuthenticationCode(uaaClient, clientId, clientSecret, password, username)
                .map(GetAutoLoginAuthenticationCodeResponse::getCode);
    }

    private static Mono<GetAutoLoginAuthenticationCodeResponse> requestAuthenticationCode(
            UaaClient uaaClient,
            String clientId,
            String clientSecret,
            String password,
            String username) {
        return uaaClient
                .serverInformation()
                .getAuthenticationCode(
                        GetAutoLoginAuthenticationCodeRequest.builder()
                                .clientId(clientId)
                                .clientSecret(clientSecret)
                                .password(password)
                                .username(username)
                                .build());
    }

    /**
     * TAS has a specific line of UAA releases 77.20.x, where x >= 8.
     * The latest OSS release of that line is <a href="https://github.com/cloudfoundry/uaa/releases/v77.20.7">v77.20.7</a>.
     * In those proprietary releases, the UAA info response has extra properties and crashes some tests.
     * We do not want to include those extra fields into the OSS releases of CF-java-client. To have our
     * integration tests succeed, we exclude these specific UAA releases.
     */
    private boolean tasSpecificUaaVersion() {
        ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();

        Boolean hasTasSpecificUaaVersion =
                context.getRootProvider()
                        .getRoot("uaa", context)
                        .map(url -> context.getHttpClient().baseUrl(url))
                        .flatMap(
                                client ->
                                        client.get()
                                                .uri("/info")
                                                .responseContent()
                                                .aggregate()
                                                .asString())
                        .map(
                                r -> {
                                    try {
                                        return mapper.readTree(r).at("/app/version").asText();
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException("Can't parse");
                                    }
                                })
                        .map(
                                version -> {
                                    String[] versionParts = version.split("\\.");
                                    int major = Integer.parseInt(versionParts[0]);
                                    int minor = Integer.parseInt(versionParts[1]);
                                    int patch = Integer.parseInt(versionParts[2]);
                                    return major == 77 && minor == 20 && patch > 8;
                                })
                        .onErrorReturn(false)
                        .block();
        return Boolean.TRUE.equals(hasTasSpecificUaaVersion);
    }
}
