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
import org.cloudfoundry.uaa.identityproviders.CreateIdentityProviderRequest;
import org.cloudfoundry.uaa.identityproviders.CreateIdentityProviderResponse;
import org.cloudfoundry.uaa.identityproviders.DeleteIdentityProviderRequest;
import org.cloudfoundry.uaa.identityproviders.DeleteIdentityProviderResponse;
import org.cloudfoundry.uaa.identityproviders.GetIdentityProviderRequest;
import org.cloudfoundry.uaa.identityproviders.GetIdentityProviderResponse;
import org.cloudfoundry.uaa.identityproviders.IdentityProvider;
import org.cloudfoundry.uaa.identityproviders.ListIdentityProvidersRequest;
import org.cloudfoundry.uaa.identityproviders.ListIdentityProvidersResponse;
import org.cloudfoundry.uaa.identityproviders.Type;
import org.cloudfoundry.uaa.identityproviders.UpdateIdentityProviderRequest;
import org.cloudfoundry.uaa.identityproviders.UpdateIdentityProviderResponse;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpMethod.PUT;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorIdentityProvidersTest extends AbstractUaaApiTest {

    private final ReactorIdentityProviders identityProviders = new ReactorIdentityProviders(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

    @Test
    public void createLdap() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/identity-providers")
                .header("X-Identity-Zone-Id", "test-identity-zone-id")
                .payload("fixtures/uaa/identity-providers/POST_request_ldap.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/uaa/identity-providers/POST_response_ldap.json")
                .build())
            .build());

        this.identityProviders
            .create(CreateIdentityProviderRequest.builder()
                .active(true)
                .configuration("{\"addShadowUserOnLogin\":true,\"autoAddGroups\":true,\"baseUrl\":\"ldap://localhost:33389\",\"bindPassword\":null,\"bindUserDn\":null,\"groupRoleAttribute\":null," +
                    "\"groupSearchBase\":null,\"maxGroupSearchDepth\":10,\"groupSearchFilter\":null,\"groupSearchSubTree\":true,\"groupsIgnorePartialResults\":null," +
                    "\"ldapGroupFile\":\"ldap/ldap-groups-null.xml\",\"ldapProfileFile\":\"ldap/ldap-simple-bind.xml\",\"localPasswordCompare\":null,\"mailAttributeName\":\"mail\"," +
                    "\"mailSubstitute\":null,\"mailSubstituteOverridesLdap\":false,\"passwordAttributeName\":null,\"passwordEncoder\":null,\"referral\":null,\"skipSSLVerification\":false," +
                    "\"tlsConfiguration\":\"none\",\"userDNPattern\":\"cn={0},ou=Users,dc=test,dc=com\",\"userDNPatternDelimiter\":\";\",\"userSearchBase\":null,\"userSearchFilter\":null," +
                    "\"attributeMappings\":{\"email\":null,\"external_groups\":null,\"family_name\":null,\"first_name\":null,\"given_name\":null,\"phone_number\":null}," +
                    "\"externalGroupsWhitelist\":[],\"emailDomain\":null,\"providerDescription\":null}")
                .name("ldap name")
                .originKey("ldap")
                .type(Type.LDAP)
                .identityZoneId("test-identity-zone-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(CreateIdentityProviderResponse.builder()
                .active(true)
                .createdAt(1474923489869L)
                .configuration("{\"addShadowUserOnLogin\":true,\"autoAddGroups\":true,\"baseUrl\":\"ldap://localhost:33389\",\"bindPassword\":null,\"bindUserDn\":null,\"groupRoleAttribute\":null," +
                    "\"groupSearchBase\":null,\"maxGroupSearchDepth\":10,\"groupSearchFilter\":null,\"groupSearchSubTree\":true,\"groupsIgnorePartialResults\":null," +
                    "\"ldapGroupFile\":\"ldap/ldap-groups-null.xml\",\"ldapProfileFile\":\"ldap/ldap-simple-bind.xml\",\"localPasswordCompare\":null,\"mailAttributeName\":\"mail\"," +
                    "\"mailSubstitute\":null,\"mailSubstituteOverridesLdap\":false,\"passwordAttributeName\":null,\"passwordEncoder\":null,\"referral\":null,\"skipSSLVerification\":false," +
                    "\"tlsConfiguration\":\"none\",\"userDNPattern\":\"cn={0},ou=Users,dc=test,dc=com\",\"userDNPatternDelimiter\":\";\",\"userSearchBase\":null,\"userSearchFilter\":null," +
                    "\"attributeMappings\":{\"email\":null,\"external_groups\":null,\"family_name\":null,\"first_name\":null,\"given_name\":null,\"phone_number\":null}," +
                    "\"externalGroupsWhitelist\":[],\"emailDomain\":null,\"providerDescription\":null}")
                .id("45201375-13a3-4b62-8b88-25457a1f7d3a")
                .identityZoneId("yetxjqgb")
                .lastModified(1474923489869L)
                .name("ldap name")
                .originKey("ldap")
                .type(Type.LDAP)
                .version(0)
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void createOauth() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/identity-providers")
                .header("X-Identity-Zone-Id", "test-identity-zone-id")
                .payload("fixtures/uaa/identity-providers/POST_request_oauth.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/uaa/identity-providers/POST_response_oauth.json")
                .build())
            .build());

        this.identityProviders
            .create(CreateIdentityProviderRequest.builder()
                .active(true)
                .configuration("{\"checkTokenUrl\":null,\"addShadowUserOnLogin\":true,\"authUrl\":\"http://auth.url\",\"linkText\":null,\"relyingPartyId\":\"uaa\",\"relyingPartySecret\":\"secret\"," +
                    "\"scopes\":null,\"showLinkText\":false,\"skipSslValidation\":false,\"tokenKey\":\"token-key\",\"tokenKeyUrl\":null,\"tokenUrl\":\"http://token.url\"," +
                    "\"attributeMappings\":{\"email\":null,\"external_groups\":null,\"family_name\":null,\"first_name\":null,\"given_name\":null,\"phone_number\":null}," +
                    "\"externalGroupsWhitelist\":null,\"emailDomain\":null,\"providerDescription\":null}")
                .name("UAA Provider")
                .originKey("oauth2.0")
                .type(Type.OAUTH2)
                .identityZoneId("test-identity-zone-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(CreateIdentityProviderResponse.builder()
                .active(true)
                .createdAt(1465001966855L)
                .configuration("{\"checkTokenUrl\":null,\"addShadowUserOnLogin\":true,\"authUrl\":\"http://auth.url\",\"linkText\":null,\"relyingPartyId\":\"uaa\",\"relyingPartySecret\":\"secret\"," +
                    "\"scopes\":null,\"showLinkText\":false,\"skipSslValidation\":false,\"tokenKey\":\"token-key\",\"tokenKeyUrl\":null,\"tokenUrl\":\"http://token.url\"," +
                    "\"attributeMappings\":{\"email\":null,\"external_groups\":null,\"family_name\":null,\"first_name\":null,\"given_name\":null,\"phone_number\":null}," +
                    "\"externalGroupsWhitelist\":[],\"emailDomain\":null,\"providerDescription\":null}")
                .id("16506900-561d-411f-904b-15c3e2722cba")
                .identityZoneId("uaa")
                .lastModified(1465001966855L)
                .name("UAA Provider")
                .originKey("oauth2.0")
                .type(Type.OAUTH2)
                .version(0)
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void createSaml() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/identity-providers")
                .header("X-Identity-Zone-Id", "test-identity-zone-id")
                .payload("fixtures/uaa/identity-providers/POST_request_saml.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/uaa/identity-providers/POST_response_saml.json")
                .build())
            .build());

        this.identityProviders
            .create(CreateIdentityProviderRequest.builder()
                .active(true)
                .configuration("{\"addShadowUserOnLogin\":true,\"assertionConsumerIndex\":0,\"groupMappingMode\":\"EXPLICITLY_MAPPED\",\"iconUrl\":null,\"idpEntityAlias\":null," +
                    "\"linkText\":\"IDPEndpointsMockTests Saml Provider:SAML\",\"metaDataLocation\":\"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?><md:EntityDescriptor " +
                    "xmlns:md=\\\"urn:oasis:names:tc:SAML:2.0:metadata\\\" entityID=\\\"http://www.okta.com/SAML\\\"><md:IDPSSODescriptor WantAuthnRequestsSigned=\\\"true\\\" " +
                    "protocolSupportEnumeration=\\\"urn:oasis:names:tc:SAML:2.0:protocol\\\"><md:KeyDescriptor use=\\\"signing\\\"><ds:KeyInfo xmlns:ds=\\\"http://www" +
                    ".w3.org/2000/09/xmldsig#\\\"><ds:X509Data><ds:X509Certificate>MIICmTCCAgKgAwIBAgIGAUPATqmEMA0GCSqGSIb3DQEBBQUAMIGPMQswCQYDVQQGEwJVUzETMBEG" +
                    "\\nA1UECAwKQ2FsaWZvcm5pYTEWMBQGA1UEBwwNU2FuIEZyYW5jaXNjbzENMAsGA1UECgwET2t0YTEU\\nMBIGA1UECwwLU1NPUHJvdmlkZXIxEDAOBgNVBAMMB1Bpdm90YWwxHDAaBgkqhkiG9w0BCQEWDWlu" +
                    "\\nZm9Ab2t0YS5jb20wHhcNMTQwMTIzMTgxMjM3WhcNNDQwMTIzMTgxMzM3WjCBjzELMAkGA1UEBhMC\\nVVMxEzARBgNVBAgMCkNhbGlmb3JuaWExFjAUBgNVBAcMDVNhbiBGcmFuY2lzY28xDTALBgNVBAoM" +
                    "\\nBE9rdGExFDASBgNVBAsMC1NTT1Byb3ZpZGVyMRAwDgYDVQQDDAdQaXZvdGFsMRwwGgYJKoZIhvcN\\nAQkBFg1pbmZvQG9rdGEuY29tMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCeil67/TLOiTZU" +
                    "\\nWWgW2XEGgFZ94bVO90v5J1XmcHMwL8v5Z/8qjdZLpGdwI7Ph0CyXMMNklpaR/Ljb8fsls3amdT5O\\nBw92Zo8ulcpjw2wuezTwL0eC0wY/GQDAZiXL59npE6U+fH1lbJIq92hx0HJSru/0O1q3+A/+jjZL\\n3tL" +
                    "/SwIDAQABMA0GCSqGSIb3DQEBBQUAA4GBAI5BoWZoH6Mz9vhypZPOJCEKa/K+biZQsA4Zqsuk\\nvvphhSERhqk/Nv76Vkl8uvJwwHbQrR9KJx4L3PRkGCG24rix71jEuXVGZUsDNM3CUKnARx4MEab6\\nGFHNkZ6DmoT" +
                    "/PFagngecHu+EwmuDtaG0rEkFrARwe+d8Ru0BN558abFb</ds:X509Certificate></ds:X509Data></ds:KeyInfo></md:KeyDescriptor><md:NameIDFormat>urn:oasis:names:tc:SAML:1.1:nameid-format" +
                    ":emailAddress</md:NameIDFormat><md:NameIDFormat>urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified</md:NameIDFormat><md:SingleSignOnService " +
                    "Binding=\\\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST\\\" Location=\\\"https://pivotal.oktapreview" +
                    ".com/app/pivotal_pivotalcfstaging_1/k2lw4l5bPODCMIIDBRYZ/sso/saml\\\"/><md:SingleSignOnService Binding=\\\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect\\\" " +
                    "Location=\\\"https://pivotal.oktapreview.com/app/pivotal_pivotalcfstaging_1/k2lw4l5bPODCMIIDBRYZ/sso/saml\\\"/></md:IDPSSODescriptor></md:EntityDescriptor>\\n\"," +
                    "\"metadataTrustCheck\":false,\"nameID\":\"urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress\",\"showSamlLink\":false,\"socketFactoryClassName\":\"org.apache.commons" +
                    ".httpclient.protocol.DefaultProtocolSocketFactory\",\"zoneId\":null,\"attributeMappings\":{\"email\":null,\"external_groups\":null,\"family_name\":null,\"first_name\":null," +
                    "\"given_name\":null,\"phone_number\":null},\"externalGroupsWhitelist\":null,\"emailDomain\":null,\"providerDescription\":null}")
                .name("SAML name")
                .originKey("SAML")
                .type(Type.SAML)
                .identityZoneId("test-identity-zone-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(CreateIdentityProviderResponse.builder()
                .active(true)
                .createdAt(1465001965526L)
                .configuration("{\"addShadowUserOnLogin\":true,\"assertionConsumerIndex\":0,\"groupMappingMode\":\"EXPLICITLY_MAPPED\",\"iconUrl\":null,\"idpEntityAlias\":\"SAML\"," +
                    "\"linkText\":\"IDPEndpointsMockTests Saml Provider:SAML\",\"metaDataLocation\":\"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?><md:EntityDescriptor " +
                    "xmlns:md=\\\"urn:oasis:names:tc:SAML:2.0:metadata\\\" entityID=\\\"http://www.okta.com/SAML\\\"><md:IDPSSODescriptor WantAuthnRequestsSigned=\\\"true\\\" " +
                    "protocolSupportEnumeration=\\\"urn:oasis:names:tc:SAML:2.0:protocol\\\"><md:KeyDescriptor use=\\\"signing\\\"><ds:KeyInfo xmlns:ds=\\\"http://www" +
                    ".w3.org/2000/09/xmldsig#\\\"><ds:X509Data><ds:X509Certificate>MIICmTCCAgKgAwIBAgIGAUPATqmEMA0GCSqGSIb3DQEBBQUAMIGPMQswCQYDVQQGEwJVUzETMBEG" +
                    "\\nA1UECAwKQ2FsaWZvcm5pYTEWMBQGA1UEBwwNU2FuIEZyYW5jaXNjbzENMAsGA1UECgwET2t0YTEU\\nMBIGA1UECwwLU1NPUHJvdmlkZXIxEDAOBgNVBAMMB1Bpdm90YWwxHDAaBgkqhkiG9w0BCQEWDWlu" +
                    "\\nZm9Ab2t0YS5jb20wHhcNMTQwMTIzMTgxMjM3WhcNNDQwMTIzMTgxMzM3WjCBjzELMAkGA1UEBhMC\\nVVMxEzARBgNVBAgMCkNhbGlmb3JuaWExFjAUBgNVBAcMDVNhbiBGcmFuY2lzY28xDTALBgNVBAoM" +
                    "\\nBE9rdGExFDASBgNVBAsMC1NTT1Byb3ZpZGVyMRAwDgYDVQQDDAdQaXZvdGFsMRwwGgYJKoZIhvcN\\nAQkBFg1pbmZvQG9rdGEuY29tMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCeil67/TLOiTZU" +
                    "\\nWWgW2XEGgFZ94bVO90v5J1XmcHMwL8v5Z/8qjdZLpGdwI7Ph0CyXMMNklpaR/Ljb8fsls3amdT5O\\nBw92Zo8ulcpjw2wuezTwL0eC0wY/GQDAZiXL59npE6U+fH1lbJIq92hx0HJSru/0O1q3+A/+jjZL\\n3tL" +
                    "/SwIDAQABMA0GCSqGSIb3DQEBBQUAA4GBAI5BoWZoH6Mz9vhypZPOJCEKa/K+biZQsA4Zqsuk\\nvvphhSERhqk/Nv76Vkl8uvJwwHbQrR9KJx4L3PRkGCG24rix71jEuXVGZUsDNM3CUKnARx4MEab6\\nGFHNkZ6DmoT" +
                    "/PFagngecHu+EwmuDtaG0rEkFrARwe+d8Ru0BN558abFb</ds:X509Certificate></ds:X509Data></ds:KeyInfo></md:KeyDescriptor><md:NameIDFormat>urn:oasis:names:tc:SAML:1.1:nameid-format" +
                    ":emailAddress</md:NameIDFormat><md:NameIDFormat>urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified</md:NameIDFormat><md:SingleSignOnService " +
                    "Binding=\\\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST\\\" Location=\\\"https://pivotal.oktapreview" +
                    ".com/app/pivotal_pivotalcfstaging_1/k2lw4l5bPODCMIIDBRYZ/sso/saml\\\"/><md:SingleSignOnService Binding=\\\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect\\\" " +
                    "Location=\\\"https://pivotal.oktapreview.com/app/pivotal_pivotalcfstaging_1/k2lw4l5bPODCMIIDBRYZ/sso/saml\\\"/></md:IDPSSODescriptor></md:EntityDescriptor>\\n\"," +
                    "\"metadataTrustCheck\":false,\"nameID\":\"urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress\",\"showSamlLink\":false,\"socketFactoryClassName\":\"org.apache.commons" +
                    ".httpclient.protocol.DefaultProtocolSocketFactory\",\"zoneId\":\"uaa\",\"attributeMappings\":{\"email\":null,\"external_groups\":null,\"family_name\":null,\"first_name\":null," +
                    "\"given_name\":null,\"phone_number\":null},\"externalGroupsWhitelist\":[],\"emailDomain\":null,\"providerDescription\":null}")
                .id("a2e96056-c777-40b8-95b8-ff81b441fcf1")
                .identityZoneId("uaa")
                .lastModified(1465001965526L)
                .name("SAML name")
                .originKey("SAML")
                .type(Type.SAML)
                .version(0)
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void delete() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/identity-providers/test-identity-provider-id")
                .header("X-Identity-Zone-Id", "test-identity-zone-id")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/identity-providers/DELETE_{id}_response.json")
                .build())
            .build());

        this.identityProviders
            .delete(DeleteIdentityProviderRequest.builder()
                .identityProviderId("test-identity-provider-id")
                .identityZoneId("test-identity-zone-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(DeleteIdentityProviderResponse.builder()
                .active(true)
                .createdAt(1466035298319L)
                .configuration("{\"addShadowUserOnLogin\":true,\"assertionConsumerIndex\":0,\"groupMappingMode\":\"EXPLICITLY_MAPPED\",\"iconUrl\":null,\"idpEntityAlias\":\"saml-for-delete\"," +
                    "\"linkText\":\"IDPEndpointsMockTests Saml Provider:saml-for-delete\",\"metaDataLocation\":\"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?><md:EntityDescriptor " +
                    "xmlns:md=\\\"urn:oasis:names:tc:SAML:2.0:metadata\\\" entityID=\\\"http://www.okta.com/saml-for-delete\\\"><md:IDPSSODescriptor WantAuthnRequestsSigned=\\\"true\\\" " +
                    "protocolSupportEnumeration=\\\"urn:oasis:names:tc:SAML:2.0:protocol\\\"><md:KeyDescriptor use=\\\"signing\\\"><ds:KeyInfo xmlns:ds=\\\"http://www" +
                    ".w3.org/2000/09/xmldsig#\\\"><ds:X509Data><ds:X509Certificate>MIICmTCCAgKgAwIBAgIGAUPATqmEMA0GCSqGSIb3DQEBBQUAMIGPMQswCQYDVQQGEwJVUzETMBEG" +
                    "\\nA1UECAwKQ2FsaWZvcm5pYTEWMBQGA1UEBwwNU2FuIEZyYW5jaXNjbzENMAsGA1UECgwET2t0YTEU\\nMBIGA1UECwwLU1NPUHJvdmlkZXIxEDAOBgNVBAMMB1Bpdm90YWwxHDAaBgkqhkiG9w0BCQEWDWlu" +
                    "\\nZm9Ab2t0YS5jb20wHhcNMTQwMTIzMTgxMjM3WhcNNDQwMTIzMTgxMzM3WjCBjzELMAkGA1UEBhMC\\nVVMxEzARBgNVBAgMCkNhbGlmb3JuaWExFjAUBgNVBAcMDVNhbiBGcmFuY2lzY28xDTALBgNVBAoM" +
                    "\\nBE9rdGExFDASBgNVBAsMC1NTT1Byb3ZpZGVyMRAwDgYDVQQDDAdQaXZvdGFsMRwwGgYJKoZIhvcN\\nAQkBFg1pbmZvQG9rdGEuY29tMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCeil67/TLOiTZU" +
                    "\\nWWgW2XEGgFZ94bVO90v5J1XmcHMwL8v5Z/8qjdZLpGdwI7Ph0CyXMMNklpaR/Ljb8fsls3amdT5O\\nBw92Zo8ulcpjw2wuezTwL0eC0wY/GQDAZiXL59npE6U+fH1lbJIq92hx0HJSru/0O1q3+A/+jjZL\\n3tL" +
                    "/SwIDAQABMA0GCSqGSIb3DQEBBQUAA4GBAI5BoWZoH6Mz9vhypZPOJCEKa/K+biZQsA4Zqsuk\\nvvphhSERhqk/Nv76Vkl8uvJwwHbQrR9KJx4L3PRkGCG24rix71jEuXVGZUsDNM3CUKnARx4MEab6\\nGFHNkZ6DmoT" +
                    "/PFagngecHu+EwmuDtaG0rEkFrARwe+d8Ru0BN558abFb</ds:X509Certificate></ds:X509Data></ds:KeyInfo></md:KeyDescriptor><md:NameIDFormat>urn:oasis:names:tc:SAML:1.1:nameid-format" +
                    ":emailAddress</md:NameIDFormat><md:NameIDFormat>urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified</md:NameIDFormat><md:SingleSignOnService " +
                    "Binding=\\\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST\\\" Location=\\\"https://pivotal.oktapreview" +
                    ".com/app/pivotal_pivotalcfstaging_1/k2lw4l5bPODCMIIDBRYZ/sso/saml\\\"/><md:SingleSignOnService Binding=\\\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect\\\" " +
                    "Location=\\\"https://pivotal.oktapreview.com/app/pivotal_pivotalcfstaging_1/k2lw4l5bPODCMIIDBRYZ/sso/saml\\\"/></md:IDPSSODescriptor></md:EntityDescriptor>\\n\"," +
                    "\"metadataTrustCheck\":false,\"nameID\":\"urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress\",\"showSamlLink\":false,\"socketFactoryClassName\":\"org.apache.commons" +
                    ".httpclient.protocol.DefaultProtocolSocketFactory\",\"zoneId\":\"uaa\",\"attributeMappings\":{\"email\":null,\"external_groups\":null,\"family_name\":null,\"first_name\":null," +
                    "\"given_name\":null,\"phone_number\":null},\"externalGroupsWhitelist\":[],\"emailDomain\":null,\"providerDescription\":null}")
                .id("3ba5978b-8db1-4f27-bfbd-f24f6773b52f")
                .identityZoneId("uaa")
                .lastModified(1466035298319L)
                .name("saml-for-delete name")
                .originKey("saml-for-delete")
                .type(Type.SAML)
                .version(0)
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/identity-providers/test-identity-provider-id")
                .header("X-Identity-Zone-Id", "test-identity-zone-id")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/uaa/identity-providers/GET_{id}_response.json")
                .build())
            .build());

        this.identityProviders
            .get(GetIdentityProviderRequest.builder()
                .identityProviderId("test-identity-provider-id")
                .identityZoneId("test-identity-zone-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetIdentityProviderResponse.builder()
                .active(true)
                .createdAt(1465001966715L)
                .configuration("{\"addShadowUserOnLogin\":true,\"assertionConsumerIndex\":0,\"groupMappingMode\":\"EXPLICITLY_MAPPED\",\"iconUrl\":null,\"idpEntityAlias\":\"saml-for-get\"," +
                    "\"linkText\":\"IDPEndpointsMockTests Saml Provider:saml-for-get\",\"metaDataLocation\":\"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?><md:EntityDescriptor " +
                    "xmlns:md=\\\"urn:oasis:names:tc:SAML:2.0:metadata\\\" entityID=\\\"http://www.okta.com/saml-for-get\\\"><md:IDPSSODescriptor WantAuthnRequestsSigned=\\\"true\\\" " +
                    "protocolSupportEnumeration=\\\"urn:oasis:names:tc:SAML:2.0:protocol\\\"><md:KeyDescriptor use=\\\"signing\\\"><ds:KeyInfo xmlns:ds=\\\"http://www" +
                    ".w3.org/2000/09/xmldsig#\\\"><ds:X509Data><ds:X509Certificate>MIICmTCCAgKgAwIBAgIGAUPATqmEMA0GCSqGSIb3DQEBBQUAMIGPMQswCQYDVQQGEwJVUzETMBEG" +
                    "\\nA1UECAwKQ2FsaWZvcm5pYTEWMBQGA1UEBwwNU2FuIEZyYW5jaXNjbzENMAsGA1UECgwET2t0YTEU\\nMBIGA1UECwwLU1NPUHJvdmlkZXIxEDAOBgNVBAMMB1Bpdm90YWwxHDAaBgkqhkiG9w0BCQEWDWlu" +
                    "\\nZm9Ab2t0YS5jb20wHhcNMTQwMTIzMTgxMjM3WhcNNDQwMTIzMTgxMzM3WjCBjzELMAkGA1UEBhMC\\nVVMxEzARBgNVBAgMCkNhbGlmb3JuaWExFjAUBgNVBAcMDVNhbiBGcmFuY2lzY28xDTALBgNVBAoM" +
                    "\\nBE9rdGExFDASBgNVBAsMC1NTT1Byb3ZpZGVyMRAwDgYDVQQDDAdQaXZvdGFsMRwwGgYJKoZIhvcN\\nAQkBFg1pbmZvQG9rdGEuY29tMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCeil67/TLOiTZU" +
                    "\\nWWgW2XEGgFZ94bVO90v5J1XmcHMwL8v5Z/8qjdZLpGdwI7Ph0CyXMMNklpaR/Ljb8fsls3amdT5O\\nBw92Zo8ulcpjw2wuezTwL0eC0wY/GQDAZiXL59npE6U+fH1lbJIq92hx0HJSru/0O1q3+A/+jjZL\\n3tL" +
                    "/SwIDAQABMA0GCSqGSIb3DQEBBQUAA4GBAI5BoWZoH6Mz9vhypZPOJCEKa/K+biZQsA4Zqsuk\\nvvphhSERhqk/Nv76Vkl8uvJwwHbQrR9KJx4L3PRkGCG24rix71jEuXVGZUsDNM3CUKnARx4MEab6\\nGFHNkZ6DmoT" +
                    "/PFagngecHu+EwmuDtaG0rEkFrARwe+d8Ru0BN558abFb</ds:X509Certificate></ds:X509Data></ds:KeyInfo></md:KeyDescriptor><md:NameIDFormat>urn:oasis:names:tc:SAML:1.1:nameid-format" +
                    ":emailAddress</md:NameIDFormat><md:NameIDFormat>urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified</md:NameIDFormat><md:SingleSignOnService " +
                    "Binding=\\\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST\\\" Location=\\\"https://pivotal.oktapreview" +
                    ".com/app/pivotal_pivotalcfstaging_1/k2lw4l5bPODCMIIDBRYZ/sso/saml\\\"/><md:SingleSignOnService Binding=\\\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect\\\" " +
                    "Location=\\\"https://pivotal.oktapreview.com/app/pivotal_pivotalcfstaging_1/k2lw4l5bPODCMIIDBRYZ/sso/saml\\\"/></md:IDPSSODescriptor></md:EntityDescriptor>\\n\"," +
                    "\"metadataTrustCheck\":false,\"nameID\":\"urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress\",\"showSamlLink\":false,\"socketFactoryClassName\":\"org.apache.commons" +
                    ".httpclient.protocol.DefaultProtocolSocketFactory\",\"zoneId\":\"uaa\",\"attributeMappings\":{\"email\":null,\"external_groups\":null,\"family_name\":null,\"first_name\":null," +
                    "\"given_name\":null,\"phone_number\":null},\"externalGroupsWhitelist\":[],\"emailDomain\":null,\"providerDescription\":null}")
                .id("0077d56d-4e10-447a-9438-57d058e033ae")
                .identityZoneId("uaa")
                .lastModified(1465001966715L)
                .name("saml-for-get name")
                .originKey("saml-for-get")
                .type(Type.SAML)
                .version(0)
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/identity-providers")
                .header("X-Identity-Zone-Id", "test-identity-zone-id")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/identity-providers/GET_response.json")
                .build())
            .build());

        this.identityProviders
            .list(ListIdentityProvidersRequest.builder()
                .identityZoneId("test-identity-zone-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(ListIdentityProvidersResponse.builder()
                .identityProvider(IdentityProvider.builder()
                    .active(true)
                    .createdAt(1465001965526L)
                    .configuration("{\"addShadowUserOnLogin\":true,\"assertionConsumerIndex\":0,\"groupMappingMode\":\"EXPLICITLY_MAPPED\",\"iconUrl\":null,\"idpEntityAlias\":\"SAML\"," +
                        "\"linkText\":\"IDPEndpointsMockTests Saml Provider:SAML\",\"metaDataLocation\":\"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?><md:EntityDescriptor " +
                        "xmlns:md=\\\"urn:oasis:names:tc:SAML:2.0:metadata\\\" entityID=\\\"http://www.okta.com/SAML\\\"><md:IDPSSODescriptor WantAuthnRequestsSigned=\\\"true\\\" " +
                        "protocolSupportEnumeration=\\\"urn:oasis:names:tc:SAML:2.0:protocol\\\"><md:KeyDescriptor use=\\\"signing\\\"><ds:KeyInfo xmlns:ds=\\\"http://www" +
                        ".w3.org/2000/09/xmldsig#\\\"><ds:X509Data><ds:X509Certificate>MIICmTCCAgKgAwIBAgIGAUPATqmEMA0GCSqGSIb3DQEBBQUAMIGPMQswCQYDVQQGEwJVUzETMBEG" +
                        "\\nA1UECAwKQ2FsaWZvcm5pYTEWMBQGA1UEBwwNU2FuIEZyYW5jaXNjbzENMAsGA1UECgwET2t0YTEU\\nMBIGA1UECwwLU1NPUHJvdmlkZXIxEDAOBgNVBAMMB1Bpdm90YWwxHDAaBgkqhkiG9w0BCQEWDWlu" +
                        "\\nZm9Ab2t0YS5jb20wHhcNMTQwMTIzMTgxMjM3WhcNNDQwMTIzMTgxMzM3WjCBjzELMAkGA1UEBhMC\\nVVMxEzARBgNVBAgMCkNhbGlmb3JuaWExFjAUBgNVBAcMDVNhbiBGcmFuY2lzY28xDTALBgNVBAoM" +
                        "\\nBE9rdGExFDASBgNVBAsMC1NTT1Byb3ZpZGVyMRAwDgYDVQQDDAdQaXZvdGFsMRwwGgYJKoZIhvcN\\nAQkBFg1pbmZvQG9rdGEuY29tMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCeil67/TLOiTZU" +
                        "\\nWWgW2XEGgFZ94bVO90v5J1XmcHMwL8v5Z/8qjdZLpGdwI7Ph0CyXMMNklpaR/Ljb8fsls3amdT5O\\nBw92Zo8ulcpjw2wuezTwL0eC0wY/GQDAZiXL59npE6U+fH1lbJIq92hx0HJSru/0O1q3+A/+jjZL\\n3tL" +
                        "/SwIDAQABMA0GCSqGSIb3DQEBBQUAA4GBAI5BoWZoH6Mz9vhypZPOJCEKa/K+biZQsA4Zqsuk\\nvvphhSERhqk/Nv76Vkl8uvJwwHbQrR9KJx4L3PRkGCG24rix71jEuXVGZUsDNM3CUKnARx4MEab6\\nGFHNkZ6DmoT" +
                        "/PFagngecHu+EwmuDtaG0rEkFrARwe+d8Ru0BN558abFb</ds:X509Certificate></ds:X509Data></ds:KeyInfo></md:KeyDescriptor><md:NameIDFormat>urn:oasis:names:tc:SAML:1.1:nameid-format" +
                        ":emailAddress</md:NameIDFormat><md:NameIDFormat>urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified</md:NameIDFormat><md:SingleSignOnService " +
                        "Binding=\\\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST\\\" Location=\\\"https://pivotal.oktapreview" +
                        ".com/app/pivotal_pivotalcfstaging_1/k2lw4l5bPODCMIIDBRYZ/sso/saml\\\"/><md:SingleSignOnService Binding=\\\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect\\\" " +
                        "Location=\\\"https://pivotal.oktapreview.com/app/pivotal_pivotalcfstaging_1/k2lw4l5bPODCMIIDBRYZ/sso/saml\\\"/></md:IDPSSODescriptor></md:EntityDescriptor>\\n\"," +
                        "\"metadataTrustCheck\":false,\"nameID\":\"urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress\",\"showSamlLink\":false,\"socketFactoryClassName\":\"org.apache.commons" +
                        ".httpclient.protocol.DefaultProtocolSocketFactory\",\"zoneId\":\"uaa\",\"attributeMappings\":{\"email\":null,\"external_groups\":null,\"family_name\":null," +
                        "\"first_name\":null,\"given_name\":null,\"phone_number\":null},\"externalGroupsWhitelist\":[],\"emailDomain\":null,\"providerDescription\":null}")
                    .id("a2e96056-c777-40b8-95b8-ff81b441fcf1")
                    .identityZoneId("uaa")
                    .lastModified(1465001965526L)
                    .name("SAML name")
                    .originKey("SAML")
                    .type(Type.SAML)
                    .version(0)
                    .build())
                .identityProvider(IdentityProvider.builder()
                    .active(false)
                    .createdAt(946713600000L)
                    .id("e6f15c2c-e5fa-46f6-a301-66b802d0102f")
                    .identityZoneId("uaa")
                    .lastModified(1465001954764L)
                    .name("keystone")
                    .originKey("keystone")
                    .type(Type.KEYSTONE)
                    .version(1)
                    .build())
                .identityProvider(IdentityProvider.builder()
                    .active(false)
                    .createdAt(946713600000L)
                    .id("a3b9ef5d-e717-4ea9-91fa-371fa7a32f46")
                    .identityZoneId("uaa")
                    .lastModified(1465001955226L)
                    .name("ldap")
                    .originKey("ldap")
                    .type(Type.LDAP)
                    .version(1)
                    .build())
                .identityProvider(IdentityProvider.builder()
                    .active(true)
                    .createdAt(1465001966855L)
                    .configuration("{\"checkTokenUrl\":null,\"addShadowUserOnLogin\":true,\"authUrl\":\"http://auth.url\",\"linkText\":null,\"relyingPartyId\":\"uaa\"," +
                        "\"relyingPartySecret\":\"secret\",\"scopes\":null,\"showLinkText\":false,\"skipSslValidation\":false,\"tokenKey\":\"token-key\",\"tokenKeyUrl\":null," +
                        "\"tokenUrl\":\"http://token.url\",\"attributeMappings\":{\"email\":null,\"external_groups\":null,\"family_name\":null,\"first_name\":null,\"given_name\":null," +
                        "\"phone_number\":null},\"externalGroupsWhitelist\":[],\"emailDomain\":null,\"providerDescription\":null}")
                    .id("16506900-561d-411f-904b-15c3e2722cba")
                    .identityZoneId("uaa")
                    .lastModified(1465001966855L)
                    .name("UAA Provider")
                    .originKey("oauth2.0")
                    .type(Type.OAUTH2)
                    .version(0)
                    .build())
                .identityProvider(IdentityProvider.builder()
                    .active(true)
                    .createdAt(946713600000L)
                    .id("8d364146-ecb3-461e-b294-87580807a08f")
                    .identityZoneId("uaa")
                    .lastModified(1465001955249L)
                    .name("uaa")
                    .originKey("uaa")
                    .type(Type.INTERNAL)
                    .version(1)
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void update() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT).path("/identity-providers/test-identity-provider-id")
                .header("X-Identity-Zone-Id", "test-identity-zone-id")
                .payload("fixtures/uaa/identity-providers/PUT_{id}_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/identity-providers/PUT_{id}_response.json")
                .build())
            .build());

        this.identityProviders
            .update(UpdateIdentityProviderRequest.builder()
                .active(true)
                .configuration("{\"disableInternalUserManagement\":false,\"emailDomain\":null,\"lockoutPolicy\":{\"countFailuresWithin\":8,\"lockoutPeriodSeconds\":8,\"lockoutAfterFailures\":8}," +
                    "\"passwordPolicy\":null,\"providerDescription\":null}")
                .name("uaa")
                .originKey("uaa")
                .type(Type.INTERNAL)
                .version(1)
                .identityZoneId("test-identity-zone-id")
                .identityProviderId("test-identity-provider-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(UpdateIdentityProviderResponse.builder()
                .active(true)
                .createdAt(946713600000L)
                .configuration("{\"disableInternalUserManagement\":false,\"emailDomain\":null,\"lockoutPolicy\":{\"countFailuresWithin\":8,\"lockoutPeriodSeconds\":8,\"lockoutAfterFailures\":8}," +
                    "\"passwordPolicy\":null,\"providerDescription\":null}")
                .id("test-identity-provider-id")
                .identityZoneId("uaa")
                .lastModified(1465001967669L)
                .name("uaa")
                .originKey("uaa")
                .type(Type.INTERNAL)
                .version(2)
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
