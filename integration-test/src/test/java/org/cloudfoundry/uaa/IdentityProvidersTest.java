/*
 * Copyright 2013-2017 the original author or authors.
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
import org.cloudfoundry.uaa.identityproviders.CreateIdentityProviderRequest;
import org.cloudfoundry.uaa.identityproviders.CreateIdentityProviderResponse;
import org.cloudfoundry.uaa.identityproviders.DeleteIdentityProviderRequest;
import org.cloudfoundry.uaa.identityproviders.ExternalGroupMappingMode;
import org.cloudfoundry.uaa.identityproviders.GetIdentityProviderRequest;
import org.cloudfoundry.uaa.identityproviders.LdapConfiguration;
import org.cloudfoundry.uaa.identityproviders.LdapGroupFile;
import org.cloudfoundry.uaa.identityproviders.LdapProfileFile;
import org.cloudfoundry.uaa.identityproviders.ListIdentityProvidersRequest;
import org.cloudfoundry.uaa.identityproviders.ListIdentityProvidersResponse;
import org.cloudfoundry.uaa.identityproviders.OAuth2Configuration;
import org.cloudfoundry.uaa.identityproviders.SamlConfiguration;
import org.cloudfoundry.uaa.identityproviders.TlsConfiguration;
import org.cloudfoundry.uaa.identityproviders.UpdateIdentityProviderRequest;
import org.cloudfoundry.uaa.identityzones.CreateIdentityZoneRequest;
import org.cloudfoundry.uaa.identityzones.CreateIdentityZoneResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.cloudfoundry.uaa.identityproviders.Type.LDAP;
import static org.cloudfoundry.uaa.identityproviders.Type.OAUTH2;
import static org.cloudfoundry.uaa.identityproviders.Type.SAML;

public final class IdentityProvidersTest extends AbstractIntegrationTest {

    @Autowired
    private UaaClient uaaClient;

    @Autowired
    private Mono<String> userId;

    @Test
    public void createLdapSimpleBind() {
        String identityZoneName = this.nameFactory.getIdentityZoneName();
        String name = this.nameFactory.getIdentityProviderName();
        String subdomainName = this.nameFactory.getDomainName();

        this.userId
            .flatMap(userId -> requestCreateIdentityZone(this.uaaClient, identityZoneName, subdomainName))
            .then(this.uaaClient.identityProviders()
                .create(CreateIdentityProviderRequest.builder()
                    .active(true)
                    .configuration(LdapConfiguration.builder()
                        .addShadowUserOnLogin(true)
                        .autoAddGroups(true)
                        .baseUrl(String.format("ldap://%s.url", name))
                        .groupSearchDepthLimit(10)
                        .groupSearchSubTree(true)
                        .ldapGroupFile(LdapGroupFile.NO_GROUP)
                        .ldapProfileFile(LdapProfileFile.SIMPLE_BIND)
                        .mailAttributeName("mail")
                        .mailSubstituteOverridesLdap(false)
                        .skipSSLVerification(false)
                        .tlsConfiguration(TlsConfiguration.NONE)
                        .userDistinguishedNamePattern("cn={0},ou=Users,dc=test,dc=com")
                        .userDistinguishedNamePatternDelimiter(";")
                        .build())
                    .identityZoneId(identityZoneName)
                    .name(name)
                    .originKey("ldap")
                    .type(LDAP)
                    .build()))
            .then(requestListIdentityProviders(this.uaaClient, identityZoneName))
            .flatMapIterable(ListIdentityProvidersResponse::getIdentityProviders)
            .filter(provider -> LDAP.equals(provider.getType()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void createOAuth() {
        String identityZoneName = this.nameFactory.getIdentityZoneName();
        String name = this.nameFactory.getIdentityProviderName();
        String subdomainName = this.nameFactory.getDomainName();

        this.userId
            .flatMap(userId -> requestCreateIdentityZone(this.uaaClient, identityZoneName, subdomainName))
            .then(this.uaaClient.identityProviders()
                .create(CreateIdentityProviderRequest.builder()
                    .active(true)
                    .configuration(OAuth2Configuration.builder()
                        .addShadowUserOnLogin(true)
                        .authUrl("http://auth.url")
                        .relyingPartyId("uaa")
                        .relyingPartySecret("secret")
                        .showLinkText(false)
                        .skipSslVerification(false)
                        .tokenKey("token-key")
                        .tokenUrl("http://token.url")
                        .build())
                    .identityZoneId(identityZoneName)
                    .name(name)
                    .originKey("oauth2.0")
                    .type(OAUTH2)
                    .build()))
            .then(requestListIdentityProviders(this.uaaClient, identityZoneName))
            .flatMapIterable(ListIdentityProvidersResponse::getIdentityProviders)
            .filter(provider -> OAUTH2.equals(provider.getType()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void createSaml() {
        String identityZoneName = this.nameFactory.getIdentityZoneName();
        String name = this.nameFactory.getIdentityProviderName();
        String subdomainName = this.nameFactory.getDomainName();

        this.userId
            .flatMap(userId -> requestCreateIdentityZone(this.uaaClient, identityZoneName, subdomainName))
            .then(this.uaaClient.identityProviders()
                .create(CreateIdentityProviderRequest.builder()
                    .active(true)
                    .configuration(SamlConfiguration.builder()
                        .addShadowUserOnLogin(true)
                        .metaDataLocation("<?xml version=\"1.0\" encoding=\"UTF-8\"?><md:EntityDescriptor xmlns:md=\"urn:oasis:names:tc:SAML:2.0:metadata\" entityID=\"http://www.okta" +
                            ".com/SAML\"><md:IDPSSODescriptor WantAuthnRequestsSigned=\"true\" protocolSupportEnumeration=\"urn:oasis:names:tc:SAML:2.0:protocol\"><md:KeyDescriptor " +
                            "use=\"signing\"><ds:KeyInfo xmlns:ds=\"http://www" +
                            ".w3.org/2000/09/xmldsig#\"><ds:X509Data><ds:X509Certificate>MIICmTCCAgKgAwIBAgIGAUPATqmEMA0GCSqGSIb3DQEBBQUAMIGPMQswCQYDVQQGEwJVUzETMBEG" +
                            "\nA1UECAwKQ2FsaWZvcm5pYTEWMBQGA1UEBwwNU2FuIEZyYW5jaXNjbzENMAsGA1UECgwET2t0YTEU\nMBIGA1UECwwLU1NPUHJvdmlkZXIxEDAOBgNVBAMMB1Bpdm90YWwxHDAaBgkqhkiG9w0BCQEWDWlu" +
                            "\nZm9Ab2t0YS5jb20wHhcNMTQwMTIzMTgxMjM3WhcNNDQwMTIzMTgxMzM3WjCBjzELMAkGA1UEBhMC\nVVMxEzARBgNVBAgMCkNhbGlmb3JuaWExFjAUBgNVBAcMDVNhbiBGcmFuY2lzY28xDTALBgNVBAoM" +
                            "\nBE9rdGExFDASBgNVBAsMC1NTT1Byb3ZpZGVyMRAwDgYDVQQDDAdQaXZvdGFsMRwwGgYJKoZIhvcN\nAQkBFg1pbmZvQG9rdGEuY29tMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCeil67/TLOiTZU" +
                            "\nWWgW2XEGgFZ94bVO90v5J1XmcHMwL8v5Z/8qjdZLpGdwI7Ph0CyXMMNklpaR/Ljb8fsls3amdT5O\nBw92Zo8ulcpjw2wuezTwL0eC0wY/GQDAZiXL59npE6U+fH1lbJIq92hx0HJSru/0O1q3+A/+jjZL\n3tL" +
                            "/SwIDAQABMA0GCSqGSIb3DQEBBQUAA4GBAI5BoWZoH6Mz9vhypZPOJCEKa/K+biZQsA4Zqsuk\nvvphhSERhqk/Nv76Vkl8uvJwwHbQrR9KJx4L3PRkGCG24rix71jEuXVGZUsDNM3CUKnARx4MEab6\nGFHNkZ6DmoT" +
                            "/PFagngecHu+EwmuDtaG0rEkFrARwe+d8Ru0BN558abFb</ds:X509Certificate></ds:X509Data></ds:KeyInfo></md:KeyDescriptor><md:NameIDFormat>urn:oasis:names:tc:SAML:1.1:nameid" +
                            "-format:emailAddress</md:NameIDFormat><md:NameIDFormat>urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified</md:NameIDFormat><md:SingleSignOnService " +
                            "Binding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST\" Location=\"https://pivotal.oktapreview" +
                            ".com/app/pivotal_pivotalcfstaging_1/k2lw4l5bPODCMIIDBRYZ/sso/saml\"/><md:SingleSignOnService Binding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect\" " +
                            "Location=\"https://pivotal.oktapreview.com/app/pivotal_pivotalcfstaging_1/k2lw4l5bPODCMIIDBRYZ/sso/saml\"/></md:IDPSSODescriptor></md:EntityDescriptor>\n")
                        .nameId("urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress")
                        .assertionConsumerIndex(0)
                        .metadataTrustCheck(false)
                        .showSamlLink(false)
                        .socketFactoryClassName("org.apache.commons.httpclient.protocol.DefaultProtocolSocketFactory")
                        .linkText("IDPEndpointsMockTests Saml Provider:SAML")
                        .groupMappingMode(ExternalGroupMappingMode.EXPLICITLY_MAPPED)
                        .build())
                    .identityZoneId(identityZoneName)
                    .name(name)
                    .originKey("SAML")
                    .type(SAML)
                    .build()))
            .then(requestListIdentityProviders(this.uaaClient, identityZoneName))
            .flatMapIterable(ListIdentityProvidersResponse::getIdentityProviders)
            .filter(provider -> SAML.equals(provider.getType()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() {
        String identityZoneName = this.nameFactory.getIdentityZoneName();
        String name = this.nameFactory.getIdentityProviderName();
        String subdomainName = this.nameFactory.getDomainName();

        this.userId
            .flatMap(userId -> requestCreateIdentityZone(this.uaaClient, identityZoneName, subdomainName))
            .then(requestCreateIdentityProvider(this.uaaClient, identityZoneName, name))
            .flatMap(response -> this.uaaClient.identityProviders()
                .delete(DeleteIdentityProviderRequest.builder()
                    .identityProviderId(response.getId())
                    .identityZoneId(response.getIdentityZoneId())
                    .build()))
            .then(requestListIdentityProviders(this.uaaClient, identityZoneName))
            .flatMapIterable(ListIdentityProvidersResponse::getIdentityProviders)
            .filter(provider -> name.equals(provider.getName()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() {
        String identityZoneName = this.nameFactory.getIdentityZoneName();
        String name = this.nameFactory.getIdentityProviderName();
        String subdomainName = this.nameFactory.getDomainName();

        this.userId
            .flatMap(userId -> requestCreateIdentityZone(this.uaaClient, identityZoneName, subdomainName))
            .then(requestCreateIdentityProvider(this.uaaClient, identityZoneName, name))
            .flatMap(response -> this.uaaClient.identityProviders()
                .get(GetIdentityProviderRequest.builder()
                    .identityProviderId(response.getId())
                    .identityZoneId(identityZoneName)
                    .build()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() {
        String identityZoneName = this.nameFactory.getIdentityZoneName();
        String name = this.nameFactory.getIdentityProviderName();
        String subdomainName = this.nameFactory.getDomainName();

        this.userId
            .flatMap(userId -> requestCreateIdentityZone(this.uaaClient, identityZoneName, subdomainName))
            .then(requestCreateIdentityProvider(this.uaaClient, identityZoneName, name))
            .then(this.uaaClient.identityProviders()
                .list(ListIdentityProvidersRequest.builder()
                    .identityZoneId(identityZoneName)
                    .build()))
            .flatMapIterable(ListIdentityProvidersResponse::getIdentityProviders)
            .filter(provider -> OAUTH2.equals(provider.getType()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void update() {
        String identityZoneName = this.nameFactory.getIdentityZoneName();
        String oldName = this.nameFactory.getIdentityProviderName();
        String newName = this.nameFactory.getIdentityProviderName();
        String subdomainName = this.nameFactory.getDomainName();

        this.userId
            .flatMap(userId -> requestCreateIdentityZone(this.uaaClient, identityZoneName, subdomainName))
            .then(requestCreateIdentityProvider(this.uaaClient, identityZoneName, oldName))
            .flatMap(response -> this.uaaClient.identityProviders()
                .update(UpdateIdentityProviderRequest.builder()
                    .configuration(OAuth2Configuration.builder()
                        .addShadowUserOnLogin(true)
                        .authUrl("http://auth.url")
                        .relyingPartyId("uaa")
                        .relyingPartySecret("secret")
                        .showLinkText(false)
                        .skipSslVerification(false)
                        .tokenKey("token-key")
                        .tokenUrl("http://token.url")
                        .build())
                    .identityProviderId(response.getId())
                    .identityZoneId(identityZoneName)
                    .name(newName)
                    .originKey("oauth2.0")
                    .type(OAUTH2)
                    .version(1)
                    .build()))
            .then(requestListIdentityProviders(this.uaaClient, identityZoneName))
            .flatMapIterable(ListIdentityProvidersResponse::getIdentityProviders)
            .filter(provider -> newName.equals(provider.getName()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<CreateIdentityProviderResponse> requestCreateIdentityProvider(UaaClient uaaClient, String identityZoneName, String name) {
        return uaaClient.identityProviders()
            .create(CreateIdentityProviderRequest.builder()
                .active(true)
                .configuration(OAuth2Configuration.builder()
                    .addShadowUserOnLogin(true)
                    .authUrl("http://auth.url")
                    .relyingPartyId("uaa")
                    .relyingPartySecret("secret")
                    .showLinkText(false)
                    .skipSslVerification(false)
                    .tokenKey("token-key")
                    .tokenUrl("http://token.url")
                    .build())
                .identityZoneId(identityZoneName)
                .name(name)
                .originKey("oauth2.0")
                .type(OAUTH2)
                .build());
    }

    private static Mono<CreateIdentityZoneResponse> requestCreateIdentityZone(UaaClient uaaClient, String identityZoneName, String subdomainName) {
        return uaaClient.identityZones()
            .create(CreateIdentityZoneRequest.builder()
                .identityZoneId(identityZoneName)
                .name(identityZoneName)
                .subdomain(subdomainName)
                .build());
    }

    private static Mono<ListIdentityProvidersResponse> requestListIdentityProviders(UaaClient uaaClient, String identityZoneName) {
        return uaaClient.identityProviders()
            .list(ListIdentityProvidersRequest.builder()
                .identityZoneId(identityZoneName)
                .build());
    }

}
