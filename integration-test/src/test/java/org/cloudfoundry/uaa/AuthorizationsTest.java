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
import org.cloudfoundry.uaa.authorizations.AuthorizeByAuthorizationCodeGrantApiRequest;
import org.cloudfoundry.uaa.authorizations.AuthorizeByAuthorizationCodeGrantBrowserRequest;
import org.cloudfoundry.uaa.authorizations.AuthorizeByAuthorizationCodeGrantHybridRequest;
import org.cloudfoundry.uaa.authorizations.AuthorizeByImplicitGrantBrowserRequest;
import org.cloudfoundry.uaa.authorizations.AuthorizeByOpenIdWithAuthorizationCodeGrantRequest;
import org.cloudfoundry.uaa.authorizations.AuthorizeByOpenIdWithIdTokenRequest;
import org.cloudfoundry.uaa.authorizations.AuthorizeByOpenIdWithImplicitGrantRequest;
import org.cloudfoundry.uaa.authorizations.GetOpenIdProviderConfigurationRequest;
import org.cloudfoundry.uaa.authorizations.GetOpenIdProviderConfigurationResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

public final class AuthorizationsTest extends AbstractIntegrationTest {

    @Autowired
    private String clientId;

    @Autowired
    private UaaClient uaaClient;

    @Test
    public void authorizeByAuthorizationCodeGrantApi() {
        this.uaaClient.authorizations()
            .authorizationCodeGrantApi(AuthorizeByAuthorizationCodeGrantApiRequest.builder()
                .clientId(this.clientId)
                .build())
            .as(StepVerifier::create)
            .consumeNextWith(actual -> assertThat(actual.length()).isGreaterThanOrEqualTo(6))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void authorizeByAuthorizationCodeGrantBrowser() {
        this.uaaClient.authorizations()
            .authorizationCodeGrantBrowser(AuthorizeByAuthorizationCodeGrantBrowserRequest.builder()
                .clientId(this.clientId)
                .redirectUri("http://redirect.to/app")
                .build())
            .as(StepVerifier::create)
            .consumeNextWith(startsWithExpectation("https://uaa."))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void authorizeByAuthorizationCodeGrantHybrid() {
        this.uaaClient.authorizations()
            .authorizationCodeGrantHybrid(AuthorizeByAuthorizationCodeGrantHybridRequest.builder()
                .clientId(this.clientId)
                .redirectUri("http://redirect.to/app")
                .build())
            .as(StepVerifier::create)
            .consumeNextWith(startsWithExpectation("https://uaa."))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void authorizeByImplicitGrantBrowser() {
        this.uaaClient.authorizations()
            .implicitGrantBrowser(AuthorizeByImplicitGrantBrowserRequest.builder()
                .clientId(this.clientId)
                .redirectUri("http://redirect.to/app")
                .build())
            .as(StepVerifier::create)
            .consumeNextWith(startsWithExpectation("https://uaa."))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void authorizeByOpenIdWithAuthorizationCodeGrant() {
        this.uaaClient.authorizations()
            .openIdWithAuthorizationCodeAndIdToken(AuthorizeByOpenIdWithAuthorizationCodeGrantRequest.builder()
                .clientId("app")
                .redirectUri("http://redirect.to/app")
                .scope("openid")
                .build())
            .as(StepVerifier::create)
            .consumeNextWith(startsWithExpectation("https://uaa."))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void authorizeByOpenIdWithIdToken() {
        this.uaaClient.authorizations()
            .openIdWithIdToken(AuthorizeByOpenIdWithIdTokenRequest.builder()
                .clientId("app")
                .redirectUri("http://redirect.to/app")
                .scope("open-id")
                .build())
            .as(StepVerifier::create)
            .consumeNextWith(startsWithExpectation("https://uaa."))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void authorizeByOpenIdWithImplicitGrant() {
        this.uaaClient.authorizations()
            .openIdWithTokenAndIdToken(AuthorizeByOpenIdWithImplicitGrantRequest.builder()
                .clientId("app")
                .redirectUri("http://redirect.to/app")
                .scope("openid")
                .build())
            .as(StepVerifier::create)
            .consumeNextWith(startsWithExpectation("https://uaa."))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void openIdProviderConfiguration() {
        this.uaaClient.authorizations()
            .getOpenIdProviderConfiguration(GetOpenIdProviderConfigurationRequest.builder()
                .build())
            .map(GetOpenIdProviderConfigurationResponse::getServiceDocumentation)
            .as(StepVerifier::create)
            .expectNext("http://docs.cloudfoundry.org/api/uaa/")
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Consumer<String> startsWithExpectation(String prefix) {
        return actual -> assertThat(actual).startsWith(prefix);
    }

}
