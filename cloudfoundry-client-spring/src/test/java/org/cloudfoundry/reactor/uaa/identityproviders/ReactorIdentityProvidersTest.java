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

package org.cloudfoundry.reactor.uaa.identityproviders;


import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.uaa.AbstractUaaApiTest;
import org.cloudfoundry.uaa.identityproviders.AttributeMappings;
import org.cloudfoundry.uaa.identityproviders.CreateIdentityProviderRequest;
import org.cloudfoundry.uaa.identityproviders.CreateIdentityProviderResponse;
import org.cloudfoundry.uaa.identityproviders.ExternalGroupMappingMode;
import org.cloudfoundry.uaa.identityproviders.LdapConfiguration;
import org.cloudfoundry.uaa.identityproviders.Oauth2Configuration;
import org.cloudfoundry.uaa.identityproviders.SamlConfiguration;
import org.cloudfoundry.uaa.identityproviders.Type;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;

public final class ReactorIdentityProvidersTest {

    public static final class LdapCreate extends AbstractUaaApiTest<CreateIdentityProviderRequest, CreateIdentityProviderResponse> {

        private final ReactorIdentityProviders identityProviderManagement = new ReactorIdentityProviders(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(POST).path("/identity-providers")
                    .header("X-Identity-Zone-Id", "test-identity-zone-id")
                    .payload("fixtures/uaa/identity-providers/POST_request_ldap.json")
                    .build())
                .response(TestResponse.builder()
                    .status(CREATED)
                    .payload("fixtures/uaa/identity-providers/POST_response_ldap.json")
                    .build())
                .build();
        }

        @Override
        protected CreateIdentityProviderResponse getResponse() {
            return CreateIdentityProviderResponse.builder()
                .active(true)
                .createdAt(1465001967988L)
                .configuration(LdapConfiguration.builder()
                    .attributeMappings(AttributeMappings.builder()
                        .build())
                    .autoAddGroups(true)
                    .baseUrl("ldap://localhost:33389")
                    .externalGroupsWhitelist(Collections.emptyList())
                    .groupSearchDepthLimit(10)
                    .groupSearchSubTree(true)
                    .ldapProfileFile("ldap/ldap-simple-bind.xml")
                    .ldapGroupFile("ldap/ldap-groups-null.xml")
                    .mailAttributeName("mail")
                    .mailSubstituteOverridesLdap(false)
                    .skipSSLVerification(false)
                    .userDistinguishedNamePattern("cn={0},ou=Users,dc=test,dc=com")
                    .userDistinguishedNamePatternDelimiter(";")
                    .build())
                .id("aaccbccb-1c85-4e8b-86ed-4ce66f91c856")
                .identityZoneId("uaa")
                .lastModified(1465001967988L)
                .name("ldap name")
                .originKey("ldap")
                .type(Type.LDAP)
                .version(0)
                .build();
        }

        @Override
        protected CreateIdentityProviderRequest getValidRequest() throws Exception {
            return CreateIdentityProviderRequest.builder()
                .active(true)
                .configuration(LdapConfiguration.builder()
                    .attributeMappings(AttributeMappings.builder()
                        .build())
                    .ldapProfileFile("ldap/ldap-simple-bind.xml")
                    .ldapGroupFile("ldap/ldap-groups-null.xml")
                    .baseUrl("ldap://localhost:33389")
                    .skipSSLVerification(false)
                    .mailAttributeName("mail")
                    .mailSubstituteOverridesLdap(false)
                    .build())
                .name("ldap name")
                .originKey("ldap")
                .type(Type.LDAP)
                .identityZoneId("test-identity-zone-id")
                .build();
        }

        @Override
        protected Mono<CreateIdentityProviderResponse> invoke(CreateIdentityProviderRequest request) {
            return this.identityProviderManagement.create(request);
        }
    }

    public static final class OauthCreate extends AbstractUaaApiTest<CreateIdentityProviderRequest, CreateIdentityProviderResponse> {

        private final ReactorIdentityProviders identityProviderManagement = new ReactorIdentityProviders(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(POST).path("/identity-providers")
                    .header("X-Identity-Zone-Id", "test-identity-zone-id")
                    .payload("fixtures/uaa/identity-providers/POST_request_oauth.json")
                    .build())
                .response(TestResponse.builder()
                    .status(CREATED)
                    .payload("fixtures/uaa/identity-providers/POST_response_oauth.json")
                    .build())
                .build();
        }

        @Override
        protected CreateIdentityProviderResponse getResponse() {
            return CreateIdentityProviderResponse.builder()
                .active(true)
                .createdAt(1465001966855L)
                .configuration(Oauth2Configuration.builder()
                    .attributeMappings(AttributeMappings.builder()
                        .build())
                    .authUrl("http://auth.url")
                    .externalGroupsWhitelist(Collections.emptyList())
                    .tokenUrl("http://token.url")
                    .tokenKey("token-key")
                    .showLinkText(false)
                    .skipSslVerification(false)
                    .relyingPartyId("uaa")
                    .relyingPartySecret("secret")
                    .addShadowUserOnLogin(true)
                    .build())
                .id("16506900-561d-411f-904b-15c3e2722cba")
                .identityZoneId("uaa")
                .lastModified(1465001966855L)
                .name("UAA Provider")
                .originKey("oauth2.0")
                .type(Type.OAUTH2)
                .version(0)
                .build();
        }

        @Override
        protected CreateIdentityProviderRequest getValidRequest() throws Exception {
            return CreateIdentityProviderRequest.builder()
                .active(true)
                .configuration(Oauth2Configuration.builder()
                    .attributeMappings(AttributeMappings.builder()
                        .build())
                    .authUrl("http://auth.url")
                    .tokenUrl("http://token.url")
                    .tokenKey("token-key")
                    .showLinkText(false)
                    .skipSslVerification(false)
                    .relyingPartyId("uaa")
                    .relyingPartySecret("secret")
                    .addShadowUserOnLogin(true)
                    .build())
                .name("UAA Provider")
                .originKey("oauth2.0")
                .type(Type.OAUTH2)
                .identityZoneId("test-identity-zone-id")
                .build();
        }

        @Override
        protected Mono<CreateIdentityProviderResponse> invoke(CreateIdentityProviderRequest request) {
            return this.identityProviderManagement.create(request);
        }
    }

    public static final class SamlCreate extends AbstractUaaApiTest<CreateIdentityProviderRequest, CreateIdentityProviderResponse> {

        private final ReactorIdentityProviders identityProviderManagement = new ReactorIdentityProviders(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(POST).path("/identity-providers")
                    .header("X-Identity-Zone-Id", "test-identity-zone-id")
                    .payload("fixtures/uaa/identity-providers/POST_request_saml.json")
                    .build())
                .response(TestResponse.builder()
                    .status(CREATED)
                    .payload("fixtures/uaa/identity-providers/POST_response_saml.json")
                    .build())
                .build();
        }

        @Override
        protected CreateIdentityProviderResponse getResponse() {
            return CreateIdentityProviderResponse.builder()
                .active(true)
                .createdAt(1465001965526L)
                .configuration(SamlConfiguration.builder()
                    .addShadowUserOnLogin(true)
                    .assertionConsumerIndex(0)
                    .attributeMappings(AttributeMappings.builder()
                        .build())
                    .externalGroupsWhitelist(Collections.emptyList())
                    .groupMappingMode(ExternalGroupMappingMode.EXPLICITLY_MAPPED)
                    .idpEntityAlias("SAML")
                    .linkText("IDPEndpointsMockTests Saml Provider:SAML")
                    .metaDataLocation("<?xml version=\"1.0\" encoding=\"UTF-8\"?><md:EntityDescriptor xmlns:md=\"urn:oasis:names:tc:SAML:2.0:metadata\" entityID=\"http://www.okta" +
                        ".com/SAML\"><md:IDPSSODescriptor WantAuthnRequestsSigned=\"true\" protocolSupportEnumeration=\"urn:oasis:names:tc:SAML:2.0:protocol\"><md:KeyDescriptor " +
                        "use=\"signing\"><ds:KeyInfo xmlns:ds=\"http://www.w3" +
                        ".org/2000/09/xmldsig#\"><ds:X509Data><ds:X509Certificate>MIICmTCCAgKgAwIBAgIGAUPATqmEMA0GCSqGSIb3DQEBBQUAMIGPMQswCQYDVQQGEwJVUzETMBEG" +
                        "\nA1UECAwKQ2FsaWZvcm5pYTEWMBQGA1UEBwwNU2FuIEZyYW5jaXNjbzENMAsGA1UECgwET2t0YTEU\nMBIGA1UECwwLU1NPUHJvdmlkZXIxEDAOBgNVBAMMB1Bpdm90YWwxHDAaBgkqhkiG9w0BCQEWDWlu" +
                        "\nZm9Ab2t0YS5jb20wHhcNMTQwMTIzMTgxMjM3WhcNNDQwMTIzMTgxMzM3WjCBjzELMAkGA1UEBhMC\nVVMxEzARBgNVBAgMCkNhbGlmb3JuaWExFjAUBgNVBAcMDVNhbiBGcmFuY2lzY28xDTALBgNVBAoM" +
                        "\nBE9rdGExFDASBgNVBAsMC1NTT1Byb3ZpZGVyMRAwDgYDVQQDDAdQaXZvdGFsMRwwGgYJKoZIhvcN\nAQkBFg1pbmZvQG9rdGEuY29tMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCeil67/TLOiTZU" +
                        "\nWWgW2XEGgFZ94bVO90v5J1XmcHMwL8v5Z/8qjdZLpGdwI7Ph0CyXMMNklpaR/Ljb8fsls3amdT5O\nBw92Zo8ulcpjw2wuezTwL0eC0wY/GQDAZiXL59npE6U+fH1lbJIq92hx0HJSru/0O1q3+A/+jjZL\n3tL" +
                        "/SwIDAQABMA0GCSqGSIb3DQEBBQUAA4GBAI5BoWZoH6Mz9vhypZPOJCEKa/K+biZQsA4Zqsuk\nvvphhSERhqk/Nv76Vkl8uvJwwHbQrR9KJx4L3PRkGCG24rix71jEuXVGZUsDNM3CUKnARx4MEab6\nGFHNkZ6DmoT" +
                        "/PFagngecHu+EwmuDtaG0rEkFrARwe+d8Ru0BN558abFb</ds:X509Certificate></ds:X509Data></ds:KeyInfo></md:KeyDescriptor><md:NameIDFormat>urn:oasis:names:tc:SAML:1" +
                        ".1:nameid-format:emailAddress</md:NameIDFormat><md:NameIDFormat>urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified</md:NameIDFormat><md:SingleSignOnService " +
                        "Binding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST\" Location=\"https://pivotal.oktapreview" +
                        ".com/app/pivotal_pivotalcfstaging_1/k2lw4l5bPODCMIIDBRYZ/sso/saml\"/><md:SingleSignOnService Binding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect\" " +
                        "Location=\"https://pivotal.oktapreview.com/app/pivotal_pivotalcfstaging_1/k2lw4l5bPODCMIIDBRYZ/sso/saml\"/></md:IDPSSODescriptor></md:EntityDescriptor>\n")
                    .metadataTrustCheck(false)
                    .nameId("urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress")
                    .showSamlLink(false)
                    .socketFactoryClassName("org.apache.commons.httpclient.protocol.DefaultProtocolSocketFactory")
                    .zoneId("uaa")
                    .build())
                .id("a2e96056-c777-40b8-95b8-ff81b441fcf1")
                .identityZoneId("uaa")
                .lastModified(1465001965526L)
                .name("SAML name")
                .originKey("SAML")
                .type(Type.SAML)
                .version(0)

                .build();
        }

        @Override
        protected CreateIdentityProviderRequest getValidRequest() throws Exception {
            return CreateIdentityProviderRequest.builder()
                .active(true)
                .configuration(SamlConfiguration.builder()
                    .addShadowUserOnLogin(true)
                    .assertionConsumerIndex(0)
                    .attributeMappings(AttributeMappings.builder()
                        .build())
                    .groupMappingMode(ExternalGroupMappingMode.EXPLICITLY_MAPPED)
                    .linkText("IDPEndpointsMockTests Saml Provider:SAML")
                    .metaDataLocation("<?xml version=\"1.0\" encoding=\"UTF-8\"?><md:EntityDescriptor xmlns:md=\"urn:oasis:names:tc:SAML:2.0:metadata\" entityID=\"http://www.okta" +
                        ".com/SAML\"><md:IDPSSODescriptor WantAuthnRequestsSigned=\"true\" protocolSupportEnumeration=\"urn:oasis:names:tc:SAML:2.0:protocol\"><md:KeyDescriptor " +
                        "use=\"signing\"><ds:KeyInfo xmlns:ds=\"http://www.w3" +
                        ".org/2000/09/xmldsig#\"><ds:X509Data><ds:X509Certificate>MIICmTCCAgKgAwIBAgIGAUPATqmEMA0GCSqGSIb3DQEBBQUAMIGPMQswCQYDVQQGEwJVUzETMBEG" +
                        "\nA1UECAwKQ2FsaWZvcm5pYTEWMBQGA1UEBwwNU2FuIEZyYW5jaXNjbzENMAsGA1UECgwET2t0YTEU\nMBIGA1UECwwLU1NPUHJvdmlkZXIxEDAOBgNVBAMMB1Bpdm90YWwxHDAaBgkqhkiG9w0BCQEWDWlu" +
                        "\nZm9Ab2t0YS5jb20wHhcNMTQwMTIzMTgxMjM3WhcNNDQwMTIzMTgxMzM3WjCBjzELMAkGA1UEBhMC\nVVMxEzARBgNVBAgMCkNhbGlmb3JuaWExFjAUBgNVBAcMDVNhbiBGcmFuY2lzY28xDTALBgNVBAoM" +
                        "\nBE9rdGExFDASBgNVBAsMC1NTT1Byb3ZpZGVyMRAwDgYDVQQDDAdQaXZvdGFsMRwwGgYJKoZIhvcN\nAQkBFg1pbmZvQG9rdGEuY29tMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCeil67/TLOiTZU" +
                        "\nWWgW2XEGgFZ94bVO90v5J1XmcHMwL8v5Z/8qjdZLpGdwI7Ph0CyXMMNklpaR/Ljb8fsls3amdT5O\nBw92Zo8ulcpjw2wuezTwL0eC0wY/GQDAZiXL59npE6U+fH1lbJIq92hx0HJSru/0O1q3+A/+jjZL\n3tL" +
                        "/SwIDAQABMA0GCSqGSIb3DQEBBQUAA4GBAI5BoWZoH6Mz9vhypZPOJCEKa/K+biZQsA4Zqsuk\nvvphhSERhqk/Nv76Vkl8uvJwwHbQrR9KJx4L3PRkGCG24rix71jEuXVGZUsDNM3CUKnARx4MEab6\nGFHNkZ6DmoT" +
                        "/PFagngecHu+EwmuDtaG0rEkFrARwe+d8Ru0BN558abFb</ds:X509Certificate></ds:X509Data></ds:KeyInfo></md:KeyDescriptor><md:NameIDFormat>urn:oasis:names:tc:SAML:1" +
                        ".1:nameid-format:emailAddress</md:NameIDFormat><md:NameIDFormat>urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified</md:NameIDFormat><md:SingleSignOnService " +
                        "Binding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST\" Location=\"https://pivotal.oktapreview" +
                        ".com/app/pivotal_pivotalcfstaging_1/k2lw4l5bPODCMIIDBRYZ/sso/saml\"/><md:SingleSignOnService Binding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect\" " +
                        "Location=\"https://pivotal.oktapreview.com/app/pivotal_pivotalcfstaging_1/k2lw4l5bPODCMIIDBRYZ/sso/saml\"/></md:IDPSSODescriptor></md:EntityDescriptor>\n")
                    .metadataTrustCheck(false)
                    .nameId("urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress")
                    .showSamlLink(false)
                    .socketFactoryClassName("org.apache.commons.httpclient.protocol.DefaultProtocolSocketFactory")
                    .build())
                .name("SAML name")
                .originKey("SAML")
                .type(Type.SAML)
                .identityZoneId("test-identity-zone-id")
                .build();
        }

        @Override
        protected Mono<CreateIdentityProviderResponse> invoke(CreateIdentityProviderRequest request) {
            return this.identityProviderManagement.create(request);
        }
    }

}