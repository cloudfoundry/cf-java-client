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

package org.cloudfoundry.uaa;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.uaa.authorizations.AuthorizeByAuthorizationCodeGrantApiRequest;
import org.cloudfoundry.uaa.authorizations.AuthorizeByAuthorizationCodeGrantBrowserRequest;
import org.cloudfoundry.uaa.authorizations.AuthorizeByImplicitGrantBrowserRequest;
import org.cloudfoundry.uaa.authorizations.AuthorizeByOpenIdWithAuthorizationCodeGrantRequest;
import org.cloudfoundry.uaa.authorizations.AuthorizeByOpenIdWithIdTokenRequest;
import org.cloudfoundry.uaa.authorizations.AuthorizeByOpenIdWithImplicitGrantRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.subscriber.ScriptedSubscriber;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

public final class AuthorizationsTest extends AbstractIntegrationTest {

    @Autowired
    private String clientId;

    @Autowired
    private UaaClient uaaClient;

    @Test
    public void authorizeByAuthorizationCodeGrantApi() throws TimeoutException, InterruptedException {
        ScriptedSubscriber<String> subscriber = ScriptedSubscriber.<String>create()
            .expectValueWith(actual -> actual.length() == 6, actual -> String.format("expected length: %d; actual length: %d", 6, actual.length()))
            .expectComplete();

        this.uaaClient.authorizations()
            .authorizationCodeGrantApi(AuthorizeByAuthorizationCodeGrantApiRequest.builder()
                .clientId(this.clientId)
                .build())
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void authorizeByAuthorizationCodeGrantBrowser() throws TimeoutException, InterruptedException {
        ScriptedSubscriber<String> subscriber = startsWithExpectation("https://uaa.");

        this.uaaClient.authorizations()
            .authorizationCodeGrantBrowser(AuthorizeByAuthorizationCodeGrantBrowserRequest.builder()
                .clientId(this.clientId)
                .redirectUri("http://redirect.to/app")
                .build())
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void authorizeByImplicitGrantBrowser() throws TimeoutException, InterruptedException {
        ScriptedSubscriber<String> subscriber = startsWithExpectation("https://uaa.");

        this.uaaClient.authorizations()
            .implicitGrantBrowser(AuthorizeByImplicitGrantBrowserRequest.builder()
                .clientId(this.clientId)
                .redirectUri("http://redirect.to/app")
                .build())
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void authorizeByOpenIdWithAuthorizationCodeGrant() throws TimeoutException, InterruptedException {
        ScriptedSubscriber<String> subscriber = startsWithExpectation("https://uaa.");

        this.uaaClient.authorizations()
            .openIdWithAuthorizationCodeGrant(AuthorizeByOpenIdWithAuthorizationCodeGrantRequest.builder()
                .clientId("app")
                .redirectUri("http://redirect.to/app")
                .scope("openid")
                .build())
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void authorizeByOpenIdWithIdToken() throws TimeoutException, InterruptedException {
        ScriptedSubscriber<String> subscriber = startsWithExpectation("https://uaa.");

        this.uaaClient.authorizations()
            .openIdWithIdToken(AuthorizeByOpenIdWithIdTokenRequest.builder()
                .clientId("app")
                .redirectUri("http://redirect.to/app")
                .scope("open-id")
                .build())
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void authorizeByOpenIdWithImplicitGrant() throws TimeoutException, InterruptedException {
        ScriptedSubscriber<String> subscriber = startsWithExpectation("https://uaa.");

        this.uaaClient.authorizations()
            .openIdWithImplicitGrant(AuthorizeByOpenIdWithImplicitGrantRequest.builder()
                .clientId("app")
                .redirectUri("http://redirect.to/app")
                .scope("openid")
                .build())
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    private static ScriptedSubscriber<String> startsWithExpectation(String prefix) {
        Function<String, Optional<String>> assertion = actual -> actual.startsWith(prefix) ? Optional.empty() : Optional.of("expected to start with: %s; actual: %s, actual");

        return ScriptedSubscriber.<String>create()
            .expectValueWith(actual -> !assertion.apply(actual).isPresent(),
                actual -> assertion.apply(actual).orElseThrow(() -> new IllegalArgumentException("Cannot generate assertion message for matching value")))
            .expectComplete();
    }


}
