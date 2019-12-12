/*
 * Copyright 2013-2019 the original author or authors.
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

package org.cloudfoundry.reactor.uaa.identityzones;

import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.uaa.AbstractUaaApiTest;
import org.cloudfoundry.uaa.identityproviders.Type;
import org.cloudfoundry.uaa.identityzones.Banner;
import org.cloudfoundry.uaa.identityzones.Branding;
import org.cloudfoundry.uaa.identityzones.ClientLockoutPolicy;
import org.cloudfoundry.uaa.identityzones.ClientSecretPolicy;
import org.cloudfoundry.uaa.identityzones.Consent;
import org.cloudfoundry.uaa.identityzones.CorsConfiguration;
import org.cloudfoundry.uaa.identityzones.CorsPolicy;
import org.cloudfoundry.uaa.identityzones.CreateIdentityZoneRequest;
import org.cloudfoundry.uaa.identityzones.CreateIdentityZoneResponse;
import org.cloudfoundry.uaa.identityzones.DeleteIdentityZoneRequest;
import org.cloudfoundry.uaa.identityzones.DeleteIdentityZoneResponse;
import org.cloudfoundry.uaa.identityzones.GetIdentityZoneRequest;
import org.cloudfoundry.uaa.identityzones.GetIdentityZoneResponse;
import org.cloudfoundry.uaa.identityzones.IdentityZone;
import org.cloudfoundry.uaa.identityzones.IdentityZoneConfiguration;
import org.cloudfoundry.uaa.identityzones.Key;
import org.cloudfoundry.uaa.identityzones.Links;
import org.cloudfoundry.uaa.identityzones.ListIdentityZonesRequest;
import org.cloudfoundry.uaa.identityzones.ListIdentityZonesResponse;
import org.cloudfoundry.uaa.identityzones.LogoutLink;
import org.cloudfoundry.uaa.identityzones.MfaConfig;
import org.cloudfoundry.uaa.identityzones.Prompt;
import org.cloudfoundry.uaa.identityzones.RefreshTokenFormat;
import org.cloudfoundry.uaa.identityzones.SamlConfiguration;
import org.cloudfoundry.uaa.identityzones.SelfServiceLink;
import org.cloudfoundry.uaa.identityzones.TokenPolicy;
import org.cloudfoundry.uaa.identityzones.UpdateIdentityZoneRequest;
import org.cloudfoundry.uaa.identityzones.UpdateIdentityZoneResponse;
import org.cloudfoundry.uaa.identityzones.UserConfig;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpMethod.PUT;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorIdentityZonesTest extends AbstractUaaApiTest {

    private final ReactorIdentityZones identityZones = new ReactorIdentityZones(CONNECTION_CONTEXT,
        this.root,
        TOKEN_PROVIDER,
        Collections.emptyMap());

    @Test
    public void create() {
        IdentityZoneConfiguration testConfiguration = IdentityZoneConfiguration.builder()
            .clientSecretPolicy(ClientSecretPolicy.builder()
                .minimumLength(-1)
                .maximumLength(-1)
                .requireUpperCaseCharacter(-1)
                .requireLowerCaseCharacter(-1)
                .requireDigit(-1)
                .requireSpecialCharacter(-1)
                .build())
            .tokenPolicy(TokenPolicy.builder()
                .accessTokenValidity(-1)
                .refreshTokenValidity(-1)
                .jwtRevocable(false)
                .refreshTokenUnique(false)
                .refreshTokenFormat(RefreshTokenFormat.JWT)
                .key("exampleKeyId",
                    Collections.singletonMap("signingKey",
                        "s1gNiNg.K3y/t3XT"))
                .build())
            .samlConfiguration(SamlConfiguration.builder()
                .assertionSigned(true)
                .requestSigned(true)
                .wantAssertionSigned(true)
                .wantPartnerAuthenticationRequestSigned(false)
                .assertionTimeToLive(600)
                .activeKeyId("legacy-saml-key")
                .key("legacy-saml-key",
                    Key.builder()
                        .key("-----BEGIN RSA PRIVATE KEY-----\nMIIBOwIBAAJBAJv8ZpB5hEK7qxP9K3v43hUS5fGT4waKe7ix4Z4mu5UBv+cw7WSF\nAt0Vaag0sAbsPzU8Hhsrj/qPABvfB8asUwcCAwEAAQJAG0r3ezH35WFG1tGGaUOr"
                            + "\nQA61cyaII53ZdgCR1IU8bx7AUevmkFtBf+aqMWusWVOWJvGu2r5VpHVAIl8nF6DS\nkQIhAMjEJ3zVYa2/Mo4ey+iU9J9Vd+WoyXDQD4EEtwmyG1PpAiEAxuZlvhDIbbce\n7o5BvOhnCZ2N7kYb1ZC57g3F"
                            + "+cbJyW8CIQCbsDGHBto2qJyFxbAO7uQ8Y0UVHa0J\nBO/g900SAcJbcQIgRtEljIShOB8pDjrsQPxmI1BLhnjD1EhRSubwhDw5AFUCIQCN\nA24pDtdOHydwtSB5+zFqFLfmVZplQM/g5kb4so70Yw==\n-----END RSA "
                            + "PRIVATE KEY-----\n")
                        .passphrase("password")
                        .certificate("-----BEGIN CERTIFICATE-----\nMIICEjCCAXsCAg36MA0GCSqGSIb3DQEBBQUAMIGbMQswCQYDVQQGEwJKUDEOMAwG\nA1UECBMFVG9reW8xEDAOBgNVBAcTB0NodW8ta3UxETAPBgNVBAoTCEZyYW5rNERE"
                            + "\nMRgwFgYDVQQLEw9XZWJDZXJ0IFN1cHBvcnQxGDAWBgNVBAMTD0ZyYW5rNEREIFdl\nYiBDQTEjMCEGCSqGSIb3DQEJARYUc3VwcG9ydEBmcmFuazRkZC5jb20wHhcNMTIw"
                            + "\nODIyMDUyNjU0WhcNMTcwODIxMDUyNjU0WjBKMQswCQYDVQQGEwJKUDEOMAwGA1UE\nCAwFVG9reW8xETAPBgNVBAoMCEZyYW5rNEREMRgwFgYDVQQDDA93d3cuZXhhbXBs"
                            + "\nZS5jb20wXDANBgkqhkiG9w0BAQEFAANLADBIAkEAm/xmkHmEQrurE/0re/jeFRLl\n8ZPjBop7uLHhnia7lQG/5zDtZIUC3RVpqDSwBuw/NTweGyuP+o8AG98HxqxTBwID"
                            + "\nAQABMA0GCSqGSIb3DQEBBQUAA4GBABS2TLuBeTPmcaTaUW/LCB2NYOy8GMdzR1mx\n8iBIu2H6/E2tiY3RIevV2OW61qY2/XRQg7YPxx3ffeUugX9F4J/iPnnu1zAxxyBy"
                            + "\n2VguKv4SWjRFoRkIfIlHX0qVviMhSlNy2ioFLy7JcPZb+v3ftDGywUqcBiVDoea0\nHn+GmxZA\n-----END CERTIFICATE-----\n")
                        .build())
                .entityId("cloudfoundry-saml-login")
                .disableInResponseToCheck(false)
                .privateKey("-----BEGIN RSA PRIVATE KEY-----\nMIIBOwIBAAJBAJv8ZpB5hEK7qxP9K3v43hUS5fGT4waKe7ix4Z4mu5UBv+cw7WSF\nAt0Vaag0sAbsPzU8Hhsrj/qPABvfB8asUwcCAwEAAQJAG0r3ezH35WFG1tGGaUOr"
                    + "\nQA61cyaII53ZdgCR1IU8bx7AUevmkFtBf+aqMWusWVOWJvGu2r5VpHVAIl8nF6DS\nkQIhAMjEJ3zVYa2/Mo4ey+iU9J9Vd+WoyXDQD4EEtwmyG1PpAiEAxuZlvhDIbbce\n7o5BvOhnCZ2N7kYb1ZC57g3F"
                    + "+cbJyW8CIQCbsDGHBto2qJyFxbAO7uQ8Y0UVHa0J\nBO/g900SAcJbcQIgRtEljIShOB8pDjrsQPxmI1BLhnjD1EhRSubwhDw5AFUCIQCN\nA24pDtdOHydwtSB5+zFqFLfmVZplQM/g5kb4so70Yw==\n-----END RSA PRIVATE "
                    + "KEY-----\n")
                .privateKeyPassword("password")
                .certificate("-----BEGIN CERTIFICATE-----\nMIICEjCCAXsCAg36MA0GCSqGSIb3DQEBBQUAMIGbMQswCQYDVQQGEwJKUDEOMAwG\nA1UECBMFVG9reW8xEDAOBgNVBAcTB0NodW8ta3UxETAPBgNVBAoTCEZyYW5rNERE"
                    + "\nMRgwFgYDVQQLEw9XZWJDZXJ0IFN1cHBvcnQxGDAWBgNVBAMTD0ZyYW5rNEREIFdl\nYiBDQTEjMCEGCSqGSIb3DQEJARYUc3VwcG9ydEBmcmFuazRkZC5jb20wHhcNMTIw"
                    + "\nODIyMDUyNjU0WhcNMTcwODIxMDUyNjU0WjBKMQswCQYDVQQGEwJKUDEOMAwGA1UE\nCAwFVG9reW8xETAPBgNVBAoMCEZyYW5rNEREMRgwFgYDVQQDDA93d3cuZXhhbXBs\nZS5jb20wXDANBgkqhkiG9w0BAQEFAANLADBIAkEAm"
                    + "/xmkHmEQrurE/0re/jeFRLl\n8ZPjBop7uLHhnia7lQG/5zDtZIUC3RVpqDSwBuw/NTweGyuP+o8AG98HxqxTBwID\nAQABMA0GCSqGSIb3DQEBBQUAA4GBABS2TLuBeTPmcaTaUW/LCB2NYOy8GMdzR1mx\n8iBIu2H6"
                    + "/E2tiY3RIevV2OW61qY2/XRQg7YPxx3ffeUugX9F4J/iPnnu1zAxxyBy\n2VguKv4SWjRFoRkIfIlHX0qVviMhSlNy2ioFLy7JcPZb+v3ftDGywUqcBiVDoea0\nHn+GmxZA\n-----END CERTIFICATE-----\n")
                .build())
            .corsPolicy(CorsPolicy.builder()
                .xhrConfiguration(CorsConfiguration.builder()
                    .allowedOrigin(".*")
                    .allowedUri(".*")
                    .allowedHeaders("Accept",
                        "Authorization",
                        "Content-Type")
                    .allowedMethod("GET")
                    .allowedCredentials(false)
                    .maxAge(1728000L)
                    .build())
                .defaultConfiguration(CorsConfiguration.builder()
                    .allowedOrigin(".*")
                    .allowedUri(".*")
                    .allowedHeaders("Accept",
                        "Authorization",
                        "Content-Type")
                    .allowedMethod("GET")
                    .allowedCredentials(false)
                    .maxAge(1728000L)
                    .build())
                .build())
            .links(Links.builder()
                .logout(LogoutLink.builder()
                    .redirectUrl("/login")
                    .redirectParameterName("redirect")
                    .disableRedirectParameter(false)
                    .build())
                .homeRedirect("http://my.hosted.homepage.com/")
                .selfService(SelfServiceLink.builder()
                    .selfServiceLinksEnabled(true)
                    .build())
                .build())
            .prompt(Prompt.builder()
                .fieldName("username")
                .fieldType("text")
                .text("Email")
                .build())
            .prompt(Prompt.builder()
                .fieldName("password")
                .fieldType("password")
                .text("Password")
                .build())
            .prompt(Prompt.builder()
                .fieldName("passcode")
                .fieldType("password")
                .text("One Time Code (Get on at /passcode)")
                .build())
            .ldapDiscoveryEnabled(false)
            .branding(Branding.builder()
                .companyName("Test Company")
                .productLogo("VGVzdFByb2R1Y3RMb2dv")
                .squareLogo("VGVzdFNxdWFyZUxvZ28=")
                .footerLegalText("Test footer legal text")
                .footerLink("Support",
                    "http://support.example.com")
                .banner(Banner.builder()
                    .logo("VGVzdFByb2R1Y3RMb2dv")
                    .text("Announcement")
                    .textColor("#000000")
                    .backgroundColor("#89cff0")
                    .link("http://announce.example.com")
                    .build())
                .build())
            .accountChooserEnabled(false)
            .userConfig(UserConfig.builder()
                .defaultGroups("openid",
                    "password.write",
                    "uaa.user",
                    "approvals.me",
                    "profile", "roles",
                    "user_attributes",
                    "uaa.offline_token")
                .build())
            .mfaConfig(MfaConfig.builder()
                .enabled(false)
                .build())
            .build();

        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST)
                .path("/identity-zones")
                .payload("fixtures/uaa/identity-zones/POST_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/uaa/identity-zones/POST_response.json")
                .build())
            .build());

        this.identityZones.create(CreateIdentityZoneRequest.builder()
            .identityZoneId("twiglet-create")
            .subdomain("twiglet-create")
            .configuration(testConfiguration)
            .name("The Twiglet Zone")
            .version(0)
            .description("Like the Twilight Zone but tastier.")
            .createdAt(1512452533738L)
            .lastModified(1512452533738L)
            .build())
            .as(StepVerifier::create)
            .expectNext(CreateIdentityZoneResponse.builder()
                .id("twiglet-create")
                .subdomain("twiglet-create")
                .configuration(testConfiguration)
                .name("The Twiglet Zone")
                .version(0)
                .description("Like the Twilight Zone but tastier.")
                .createdAt(1512452533738L)
                .lastModified(1512452533738L)
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void delete() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE)
                .path("/identity-zones/twiglet-delete")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/identity-zones/DELETE_{id}_response.json")
                .build())
            .build());

        this.identityZones.delete(DeleteIdentityZoneRequest.builder()
            .identityZoneId("twiglet-delete")
            .build())
            .as(StepVerifier::create)
            .expectNext(DeleteIdentityZoneResponse.builder()
                .id("twiglet-delete")
                .subdomain("twiglet-delete")
                .configuration(IdentityZoneConfiguration.builder()
                    .clientLockoutPolicy(ClientLockoutPolicy.builder()
                        .lockoutPeriodSeconds(-1)
                        .lockoutAfterFailures(-1)
                        .countFailuresWithin(-1)
                        .build())
                    .tokenPolicy(TokenPolicy.builder()
                        .accessTokenValidity(-1)
                        .refreshTokenValidity(-1)
                        .jwtRevocable(false)
                        .keys(Collections.emptyMap())
                        .build())
                    .samlConfiguration(SamlConfiguration.builder()
                        .assertionSigned(true)
                        .requestSigned(true)
                        .wantAssertionSigned(true)
                        .wantPartnerAuthenticationRequestSigned(false)
                        .assertionTimeToLive(600)
                        .build())
                    .corsPolicy(CorsPolicy.builder()
                        .xhrConfiguration(CorsConfiguration.builder()
                            .allowedOrigin(".*")
                            .allowedUri(".*")
                            .allowedHeader("Accept")
                            .allowedHeader("Authorization")
                            .allowedHeader("Content-Type")
                            .allowedMethod("GET")
                            .allowedCredentials(false)
                            .maxAge(1728000L)
                            .build())
                        .defaultConfiguration(CorsConfiguration.builder()
                            .allowedOrigin(".*")
                            .allowedUri(".*")
                            .allowedHeader("Accept")
                            .allowedHeader("Authorization")
                            .allowedHeader("Content-Type")
                            .allowedMethod("GET")
                            .allowedCredentials(false)
                            .maxAge(1728000L)
                            .build())
                        .build())
                    .links(Links.builder()
                        .logout(LogoutLink.builder()
                            .redirectUrl("/login")
                            .redirectParameterName("redirect")
                            .disableRedirectParameter(true)
                            .build())
                        .homeRedirect("http://my.hosted.homepage.com/")
                        .selfService(SelfServiceLink.builder()
                            .selfServiceLinksEnabled(true)
                            .signupLink("/create_account")
                            .resetPasswordLink("/forgot_password")
                            .build())
                        .build())
                    .prompt(Prompt.builder()
                        .fieldName("username")
                        .fieldType("text")
                        .text("Email")
                        .build())
                    .prompt(Prompt.builder()
                        .fieldName("password")
                        .fieldType("password")
                        .text("Password")
                        .build())
                    .prompt(Prompt.builder()
                        .fieldName("passcode")
                        .fieldType("password")
                        .text("One Time Code (Get on at /passcode)")
                        .build())
                    .ldapDiscoveryEnabled(false)
                    .branding(Branding.builder()
                        .companyName("Test Company")
                        .productLogo("VGVzdFByb2R1Y3RMb2dv")
                        .squareLogo("VGVzdFNxdWFyZUxvZ28=")
                        .footerLegalText("Test footer legal text")
                        .footerLink("Support",
                            "http://support.example.com")
                        .build())
                    .accountChooserEnabled(false)
                    .build())
                .name("The Twiglet Zone")
                .version(0)
                .createdAt(1481728057024L)
                .lastModified(1481728057024L)
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET)
                .path("/identity-zones/twiglet-get")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/identity-zones/GET_{id}_response.json")
                .build())
            .build());

        this.identityZones.get(GetIdentityZoneRequest.builder()
            .identityZoneId("twiglet-get")
            .build())
            .as(StepVerifier::create)
            .expectNext(GetIdentityZoneResponse.builder()
                .id("twiglet-get")
                .subdomain("twiglet-get")
                .configuration(IdentityZoneConfiguration.builder()
                    .clientSecretPolicy(ClientSecretPolicy.builder()
                        .minimumLength(-1)
                        .maximumLength(-1)
                        .requireDigit(-1)
                        .requireLowerCaseCharacter(-1)
                        .requireSpecialCharacter(-1)
                        .requireUpperCaseCharacter(-1)
                        .build())
                    .tokenPolicy(TokenPolicy.builder()
                        .accessTokenValidity(3600)
                        .activeKeyId("active-key-1")
                        .jwtRevocable(false)
                        .refreshTokenFormat(RefreshTokenFormat.JWT)
                        .refreshTokenUnique(false)
                        .refreshTokenValidity(7200)
                        .build())
                    .samlConfiguration(SamlConfiguration.builder()
                        .activeKeyId("legacy-saml-key")
                        .assertionSigned(true)
                        .assertionTimeToLive(600)
                        .certificate("-----BEGIN CERTIFICATE-----\nMIICEjCCAXsCAg36MA0GCSqGSIb3DQEBBQUAMIGbMQswCQYDVQQGEwJKUDEOMAwG\nA1UECBMFVG9reW8xEDAOBgNVBAcTB0NodW8ta3UxETAPBgNVBAoTCEZyYW5rNERE"
                            + "\nMRgwFgYDVQQLEw9XZWJDZXJ0IFN1cHBvcnQxGDAWBgNVBAMTD0ZyYW5rNEREIFdl\nYiBDQTEjMCEGCSqGSIb3DQEJARYUc3VwcG9ydEBmcmFuazRkZC5jb20wHhcNMTIw"
                            + "\nODIyMDUyNjU0WhcNMTcwODIxMDUyNjU0WjBKMQswCQYDVQQGEwJKUDEOMAwGA1UE\nCAwFVG9reW8xETAPBgNVBAoMCEZyYW5rNEREMRgwFgYDVQQDDA93d3cuZXhhbXBs"
                            + "\nZS5jb20wXDANBgkqhkiG9w0BAQEFAANLADBIAkEAm/xmkHmEQrurE/0re/jeFRLl\n8ZPjBop7uLHhnia7lQG/5zDtZIUC3RVpqDSwBuw/NTweGyuP+o8AG98HxqxTBwID"
                            + "\nAQABMA0GCSqGSIb3DQEBBQUAA4GBABS2TLuBeTPmcaTaUW/LCB2NYOy8GMdzR1mx\n8iBIu2H6/E2tiY3RIevV2OW61qY2/XRQg7YPxx3ffeUugX9F4J/iPnnu1zAxxyBy"
                            + "\n2VguKv4SWjRFoRkIfIlHX0qVviMhSlNy2ioFLy7JcPZb+v3ftDGywUqcBiVDoea0\nHn+GmxZA\n-----END CERTIFICATE-----\n")
                        .disableInResponseToCheck(false)
                        .entityId("cloudfoundry-saml-login")
                        .key("legacy-saml-key",
                            Key.builder()
                                .certificate("-----BEGIN CERTIFICATE-----\nMIICEjCCAXsCAg36MA0GCSqGSIb3DQEBBQUAMIGbMQswCQYDVQQGEwJKUDEOMAwG"
                                    + "\nA1UECBMFVG9reW8xEDAOBgNVBAcTB0NodW8ta3UxETAPBgNVBAoTCEZyYW5rNERE\nMRgwFgYDVQQLEw9XZWJDZXJ0IFN1cHBvcnQxGDAWBgNVBAMTD0ZyYW5rNEREIFdl"
                                    + "\nYiBDQTEjMCEGCSqGSIb3DQEJARYUc3VwcG9ydEBmcmFuazRkZC5jb20wHhcNMTIw\nODIyMDUyNjU0WhcNMTcwODIxMDUyNjU0WjBKMQswCQYDVQQGEwJKUDEOMAwGA1UE"
                                    + "\nCAwFVG9reW8xETAPBgNVBAoMCEZyYW5rNEREMRgwFgYDVQQDDA93d3cuZXhhbXBs\nZS5jb20wXDANBgkqhkiG9w0BAQEFAANLADBIAkEAm/xmkHmEQrurE/0re/jeFRLl\n8ZPjBop7uLHhnia7lQG"
                                    + "/5zDtZIUC3RVpqDSwBuw/NTweGyuP+o8AG98HxqxTBwID\nAQABMA0GCSqGSIb3DQEBBQUAA4GBABS2TLuBeTPmcaTaUW/LCB2NYOy8GMdzR1mx\n8iBIu2H6/E2tiY3RIevV2OW61qY2" +
                                    "/XRQg7YPxx3ffeUugX9F4J"
                                    + "/iPnnu1zAxxyBy\n2VguKv4SWjRFoRkIfIlHX0qVviMhSlNy2ioFLy7JcPZb+v3ftDGywUqcBiVDoea0\nHn+GmxZA\n-----END CERTIFICATE-----\n")
                                .build())
                        .requestSigned(true)
                        .wantAssertionSigned(true)
                        .wantPartnerAuthenticationRequestSigned(false)
                        .build())
                    .corsPolicy(CorsPolicy.builder()
                        .xhrConfiguration(CorsConfiguration.builder()
                            .allowedOrigin(".*")
                            .allowedUri(".*")
                            .allowedHeader("Accept")
                            .allowedHeader("Authorization")
                            .allowedHeader("Content-Type")
                            .allowedMethod("GET")
                            .allowedCredentials(false)
                            .maxAge(1728000L)
                            .build())
                        .defaultConfiguration(CorsConfiguration.builder()
                            .allowedOrigin(".*")
                            .allowedUri(".*")
                            .allowedHeader("Accept")
                            .allowedHeader("Authorization")
                            .allowedHeader("Content-Type")
                            .allowedMethod("GET")
                            .allowedCredentials(false)
                            .maxAge(1728000L)
                            .build())
                        .build())
                    .links(Links.builder()
                        .logout(LogoutLink.builder()
                            .redirectUrl("/login")
                            .redirectParameterName("redirect")
                            .disableRedirectParameter(false)
                            .build())
                        .homeRedirect("http://my.hosted.homepage.com/")
                        .selfService(SelfServiceLink.builder()
                            .selfServiceLinksEnabled(true)
                            .build())
                        .build())
                    .prompt(Prompt.builder()
                        .fieldName("username")
                        .fieldType("text")
                        .text("Email")
                        .build())
                    .prompt(Prompt.builder()
                        .fieldName("password")
                        .fieldType("password")
                        .text("Password")
                        .build())
                    .prompt(Prompt.builder()
                        .fieldName("passcode")
                        .fieldType("password")
                        .text("Temporary Authentication Code (Get on at /passcode)")
                        .build())
                    .ldapDiscoveryEnabled(false)
                    .branding(Branding.builder()
                        .banner(Banner.builder()
                            .backgroundColor("#89cff0")
                            .link("http://announce.example.com")
                            .logo("VGVzdFByb2R1Y3RMb2dv")
                            .text("Announcement")
                            .textColor("#000000")
                            .build())
                        .companyName("Test Company")
                        .consent(Consent.builder()
                            .link("http://policy.example.com")
                            .text("Some Policy")
                            .build())
                        .footerLegalText("Test footer legal text")
                        .footerLink("Support",
                            "http://support.example.com")
                        .productLogo("VGVzdFByb2R1Y3RMb2dv")
                        .squareLogo("VGVzdFNxdWFyZUxvZ28=")
                        .build())
                    .accountChooserEnabled(false)
                    .issuer("http://localhost:8080/uaa")
                    .mfaConfig(MfaConfig.builder()
                        .enabled(false)
                        .identityProvider(Type.INTERNAL)
                        .identityProvider(Type.LDAP)
                        .build())
                    .userConfig(UserConfig.builder()
                        .defaultGroups(Arrays.asList("openid",
                            "password.write",
                            "uaa.user",
                            "approvals.me",
                            "profile",
                            "roles",
                            "user_attributes",
                            "uaa.offline_token"))
                        .build())
                    .build())
                .name("The Twiglet Zone")
                .version(0)
                .createdAt(1529690486268L)
                .lastModified(1529690486268L)
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET)
                .path("/identity-zones")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/identity-zones/GET_response.json")
                .build())
            .build());

        this.identityZones.list(ListIdentityZonesRequest.builder()
            .build())
            .as(StepVerifier::create)
            .expectNext(ListIdentityZonesResponse.builder()
                .identityZone(IdentityZone.builder()
                    .id("15wtczzd")
                    .subdomain("15wtczzd")
                    .configuration(IdentityZoneConfiguration.builder()
                        .clientLockoutPolicy(ClientLockoutPolicy.builder()
                            .lockoutPeriodSeconds(-1)
                            .lockoutAfterFailures(-1)
                            .countFailuresWithin(-1)
                            .build())
                        .tokenPolicy(TokenPolicy.builder()
                            .accessTokenValidity(-1)
                            .refreshTokenValidity(-1)
                            .jwtRevocable(false)
                            .keys(Collections.emptyMap())
                            .build())
                        .samlConfiguration(SamlConfiguration.builder()
                            .assertionSigned(true)
                            .requestSigned(true)
                            .wantAssertionSigned(true)
                            .wantPartnerAuthenticationRequestSigned(false)
                            .assertionTimeToLive(600)
                            .build())
                        .corsPolicy(CorsPolicy.builder()
                            .xhrConfiguration(CorsConfiguration.builder()
                                .allowedOrigin(".*")
                                .allowedUri(".*")
                                .allowedHeader("Accept")
                                .allowedHeader("Authorization")
                                .allowedHeader("Content-Type")
                                .allowedMethod("GET")
                                .allowedCredentials(false)
                                .maxAge(1728000L)
                                .build())
                            .defaultConfiguration(CorsConfiguration.builder()
                                .allowedOrigin(".*")
                                .allowedUri(".*")
                                .allowedHeader("Accept")
                                .allowedHeader("Authorization")
                                .allowedHeader("Content-Type")
                                .allowedMethod("GET")
                                .allowedCredentials(false)
                                .maxAge(1728000L)
                                .build())
                            .build())
                        .links(Links.builder()
                            .logout(LogoutLink.builder()
                                .redirectUrl("/login")
                                .redirectParameterName("redirect")
                                .disableRedirectParameter(true)
                                .build())
                            .selfService(SelfServiceLink.builder()
                                .selfServiceLinksEnabled(true)
                                .signupLink("/create_account")
                                .resetPasswordLink("/forgot_password")
                                .build())
                            .build())
                        .prompt(Prompt.builder()
                            .fieldName("username")
                            .fieldType("text")
                            .text("Email")
                            .build())
                        .prompt(Prompt.builder()
                            .fieldName("password")
                            .fieldType("password")
                            .text("Password")
                            .build())
                        .prompt(Prompt.builder()
                            .fieldName("passcode")
                            .fieldType("password")
                            .text("One Time Code (Get on at /passcode)")
                            .build())
                        .ldapDiscoveryEnabled(false)
                        .accountChooserEnabled(false)
                        .build())
                    .name("The Twiglet Zone")
                    .version(0)
                    .description("Like the Twilight Zone but tastier.")
                    .createdAt(1481728053399L)
                    .lastModified(1481728053399L)
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void update() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT)
                .path("/identity-zones/twiglet-update")
                .payload("fixtures/uaa/identity-zones/PUT_{id}_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/identity-zones/PUT_{id}_response.json")
                .build())
            .build());

        this.identityZones.update(UpdateIdentityZoneRequest.builder()
            .identityZoneId("twiglet-update")
            .subdomain("twiglet-update")
            .configuration(IdentityZoneConfiguration.builder()
                .clientLockoutPolicy(ClientLockoutPolicy.builder()
                    .lockoutPeriodSeconds(-1)
                    .lockoutAfterFailures(-1)
                    .countFailuresWithin(-1)
                    .build())
                .tokenPolicy(TokenPolicy.builder()
                    .accessTokenValidity(-1)
                    .refreshTokenValidity(-1)
                    .jwtRevocable(false)
                    .key("updatedKeyId",
                        Collections.singletonMap("signingKey",
                            "upD4t3d.s1gNiNg.K3y/t3XT"))
                    .build())
                .samlConfiguration(SamlConfiguration.builder()
                    .assertionSigned(true)
                    .requestSigned(true)
                    .wantAssertionSigned(true)
                    .wantPartnerAuthenticationRequestSigned(false)
                    .assertionTimeToLive(600)
                    .build())
                .corsPolicy(CorsPolicy.builder()
                    .xhrConfiguration(CorsConfiguration.builder()
                        .allowedOrigin(".*")
                        .allowedUri(".*")
                        .allowedHeader("Accept")
                        .allowedHeader("Authorization")
                        .allowedHeader("Content-Type")
                        .allowedMethod("GET")
                        .allowedCredentials(false)
                        .maxAge(1728000L)
                        .build())
                    .defaultConfiguration(CorsConfiguration.builder()
                        .allowedOrigin(".*")
                        .allowedUri(".*")
                        .allowedHeader("Accept")
                        .allowedHeader("Authorization")
                        .allowedHeader("Content-Type")
                        .allowedMethod("GET")
                        .allowedCredentials(false)
                        .maxAge(1728000L)
                        .build())
                    .build())
                .links(Links.builder()
                    .logout(LogoutLink.builder()
                        .redirectUrl("/login")
                        .redirectParameterName("redirect")
                        .disableRedirectParameter(true)
                        .build())
                    .homeRedirect("http://my.hosted.homepage.com/")
                    .selfService(SelfServiceLink.builder()
                        .selfServiceLinksEnabled(true)
                        .signupLink("/create_account")
                        .resetPasswordLink("/forgot_password")
                        .build())
                    .build())
                .prompt(Prompt.builder()
                    .fieldName("username")
                    .fieldType("text")
                    .text("Email")
                    .build())
                .prompt(Prompt.builder()
                    .fieldName("password")
                    .fieldType("password")
                    .text("Password")
                    .build())
                .prompt(Prompt.builder()
                    .fieldName("passcode")
                    .fieldType("password")
                    .text("One Time Code (Get on at /passcode)")
                    .build())
                .ldapDiscoveryEnabled(false)
                .branding(Branding.builder()
                    .companyName("Test Company")
                    .productLogo("VGVzdFByb2R1Y3RMb2dv")
                    .squareLogo("VGVzdFNxdWFyZUxvZ28=")
                    .footerLegalText("Test footer legal text")
                    .footerLink("Support",
                        "http://support.example.com")
                    .build())
                .accountChooserEnabled(false)
                .build())
            .name("The Updated Twiglet Zone")
            .version(0)
            .description("Like the Twilight Zone but not tastier.")
            .createdAt(1481728057246L)
            .lastModified(1481728057246L)
            .build())
            .as(StepVerifier::create)
            .expectNext(UpdateIdentityZoneResponse.builder()
                .id("twiglet-update")
                .subdomain("twiglet-update")
                .configuration(IdentityZoneConfiguration.builder()
                    .clientLockoutPolicy(ClientLockoutPolicy.builder()
                        .lockoutPeriodSeconds(-1)
                        .lockoutAfterFailures(-1)
                        .countFailuresWithin(-1)
                        .build())
                    .tokenPolicy(TokenPolicy.builder()
                        .accessTokenValidity(-1)
                        .refreshTokenValidity(-1)
                        .jwtRevocable(false)
                        .key("updatedKeyId",
                            Collections.singletonMap("signingKey",
                                "upD4t3d.s1gNiNg.K3y/t3XT"))
                        .build())
                    .samlConfiguration(SamlConfiguration.builder()
                        .assertionSigned(true)
                        .requestSigned(true)
                        .wantAssertionSigned(true)
                        .wantPartnerAuthenticationRequestSigned(false)
                        .assertionTimeToLive(600)
                        .build())
                    .corsPolicy(CorsPolicy.builder()
                        .xhrConfiguration(CorsConfiguration.builder()
                            .allowedOrigin(".*")
                            .allowedUri(".*")
                            .allowedHeader("Accept")
                            .allowedHeader("Authorization")
                            .allowedHeader("Content-Type")
                            .allowedMethod("GET")
                            .allowedCredentials(false)
                            .maxAge(1728000L)
                            .build())
                        .defaultConfiguration(CorsConfiguration.builder()
                            .allowedOrigin(".*")
                            .allowedUri(".*")
                            .allowedHeader("Accept")
                            .allowedHeader("Authorization")
                            .allowedHeader("Content-Type")
                            .allowedMethod("GET")
                            .allowedCredentials(false)
                            .maxAge(1728000L)
                            .build())
                        .build())
                    .links(Links.builder()
                        .logout(LogoutLink.builder()
                            .redirectUrl("/login")
                            .redirectParameterName("redirect")
                            .disableRedirectParameter(true)
                            .build())
                        .homeRedirect("http://my.hosted.homepage.com/")
                        .selfService(SelfServiceLink.builder()
                            .selfServiceLinksEnabled(true)
                            .signupLink("/create_account")
                            .resetPasswordLink("/forgot_password")
                            .build())
                        .build())
                    .prompt(Prompt.builder()
                        .fieldName("username")
                        .fieldType("text")
                        .text("Email")
                        .build())
                    .prompt(Prompt.builder()
                        .fieldName("password")
                        .fieldType("password")
                        .text("Password")
                        .build())
                    .prompt(Prompt.builder()
                        .fieldName("passcode")
                        .fieldType("password")
                        .text("One Time Code (Get on at /passcode)")
                        .build())
                    .ldapDiscoveryEnabled(false)
                    .branding(Branding.builder()
                        .companyName("Test Company")
                        .productLogo("VGVzdFByb2R1Y3RMb2dv")
                        .squareLogo("VGVzdFNxdWFyZUxvZ28=")
                        .footerLegalText("Test footer legal text")
                        .footerLink("Support",
                            "http://support.example.com")
                        .build())
                    .accountChooserEnabled(false)
                    .build())
                .name("The Updated Twiglet Zone")
                .version(1)
                .description("Like the Twilight Zone but not tastier.")
                .createdAt(1481728057213L)
                .lastModified(1481728057259L)
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
