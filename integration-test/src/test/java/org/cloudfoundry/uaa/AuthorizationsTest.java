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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
            .subscribe(this.<String>testSubscriber()
                .expectThat(code -> {
                    assertNotNull(code);
                    assertTrue(code.length() == 6);
                }));
    }

    @Test
    public void authorizeByAuthorizationCodeGrantBrowser() {
        this.uaaClient.authorizations()
            .authorizationCodeGrantBrowser(AuthorizeByAuthorizationCodeGrantBrowserRequest.builder()
                .clientId(this.clientId)
                .redirectUri("http://redirect.to/app")
                .build())
            .subscribe(this.<String>testSubscriber()
                .expectThat(location -> assertTrue(location.startsWith("https://uaa."))));
    }

    @Test
    public void authorizeByImplicitGrantBrowser() {
        this.uaaClient.authorizations()
            .implicitGrantBrowser(AuthorizeByImplicitGrantBrowserRequest.builder()
                .clientId(this.clientId)
                .redirectUri("http://redirect.to/app")
                .build())
            .subscribe(this.<String>testSubscriber()
                .expectThat(location -> assertTrue(location.startsWith("https://uaa."))));
    }

    @Test
    public void authorizeByOpenIdWithAuthorizationCodeGrant() {
        this.uaaClient.authorizations()
            .openIdWithAuthorizationCodeGrant(AuthorizeByOpenIdWithAuthorizationCodeGrantRequest.builder()
                .clientId("app")
                .redirectUri("http://redirect.to/app")
                .scope("openid")
                .build())
            .subscribe(this.<String>testSubscriber()
                .expectThat(location -> assertTrue(location.startsWith("https://uaa."))));
    }

    @Test
    public void authorizeByOpenIdWithIdToken() {
        this.uaaClient.authorizations()
            .openIdWithIdToken(AuthorizeByOpenIdWithIdTokenRequest.builder()
                .clientId("app")
                .redirectUri("http://redirect.to/app")
                .scope("open-id")
                .build())
            .subscribe(this.<String>testSubscriber()
                .expectThat(location -> assertTrue(location.startsWith("https://uaa."))));
    }

    @Test
    public void authorizeByOpenIdWithImplicitGrant() {
        this.uaaClient.authorizations()
            .openIdWithImplicitGrant(AuthorizeByOpenIdWithImplicitGrantRequest.builder()
                .clientId("app")
                .redirectUri("http://redirect.to/app")
                .scope("openid")
                .build())
            .subscribe(this.<String>testSubscriber()
                .expectThat(location -> assertTrue(location.startsWith("https://uaa."))));
    }


}
