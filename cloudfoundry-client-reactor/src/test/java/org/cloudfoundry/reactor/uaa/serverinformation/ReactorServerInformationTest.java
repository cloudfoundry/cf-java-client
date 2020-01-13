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

package org.cloudfoundry.reactor.uaa.serverinformation;

import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.uaa.AbstractUaaApiTest;
import org.cloudfoundry.uaa.serverinformation.ApplicationInfo;
import org.cloudfoundry.uaa.serverinformation.AutoLoginRequest;
import org.cloudfoundry.uaa.serverinformation.GetAutoLoginAuthenticationCodeRequest;
import org.cloudfoundry.uaa.serverinformation.GetAutoLoginAuthenticationCodeResponse;
import org.cloudfoundry.uaa.serverinformation.GetInfoRequest;
import org.cloudfoundry.uaa.serverinformation.GetInfoResponse;
import org.cloudfoundry.uaa.serverinformation.Links;
import org.cloudfoundry.uaa.serverinformation.Prompts;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorServerInformationTest extends AbstractUaaApiTest {

    private final ReactorServerInformation info = new ReactorServerInformation(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void autoLogin() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/autologin?client_id=admin&code=NaOjAprtCK")
                .build())
            .response(TestResponse.builder()
                .status(FOUND)
                .build())
            .build());

        this.info
            .autoLogin(AutoLoginRequest.builder()
                .clientId("admin")
                .code("NaOjAprtCK")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getAutoLoginAuthenticationCode() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/autologin")
                .payload("fixtures/uaa/info/POST_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/info/POST_response.json")
                .build())
            .build());

        this.info
            .getAuthenticationCode(GetAutoLoginAuthenticationCodeRequest.builder()
                .clientId("admin")
                .clientSecret("adminsecret")
                .password("koala")
                .username("marissa")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetAutoLoginAuthenticationCodeResponse.builder()
                .code("m0R24i7t2s")
                .path("/oauth/authorize")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getInfo() {
        Map<String, String> ipDefinitions = new HashMap<>();
        ipDefinitions.put("SAMLMetadataUrl", "http://localhost:8080/uaa/saml/discovery?returnIDParam=idp&entityID=cloudfoundry-saml-login&idp=SAMLMetadataUrl&isPassive=true");
        ipDefinitions.put("SAML", "http://localhost:8080/uaa/saml/discovery?returnIDParam=idp&entityID=cloudfoundry-saml-login&idp=SAML&isPassive=true");

        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/info")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/info/GET_response.json")
                .build())
            .build());

        this.info
            .getInfo(GetInfoRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNext(GetInfoResponse.builder()
                .app(ApplicationInfo.builder()
                    .version("4.7.0-SNAPSHOT")
                    .build())
                .commitId("4bba13c")
                .entityId("cloudfoundry-saml-login")
                .idpDefinitions(ipDefinitions)
                .links(Links.builder()
                    .login("http://localhost:8080/uaa")
                    .password("/forgot_password")
                    .register("/create_account")
                    .uaa("http://localhost:8080/uaa")
                    .build())
                .prompts(Prompts.builder()
                    .passcode(Arrays.asList("password", "One Time Code ( Get one at http://localhost:8080/uaa/passcode )"))
                    .password(Arrays.asList("password", "Password"))
                    .username(Arrays.asList("text", "Email"))
                    .build())
                .showLoginLinks(true)
                .timestamp("2017-09-08T23:11:58+0000")
                .zoneName("uaa")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
