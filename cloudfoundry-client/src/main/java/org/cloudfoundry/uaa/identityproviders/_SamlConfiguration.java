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

package org.cloudfoundry.uaa.identityproviders;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

import java.util.List;

/**
 * The payload for the saml identity provider configuration
 */
@JsonDeserialize
@Value.Immutable
abstract class _SamlConfiguration extends AbstractExternalIdentityProviderConfiguration {

    /**
     * Determines whether or not shadow users must be created before login by an administrator.
     */
    @JsonProperty("addShadowUserOnLogin")
    @Nullable
    abstract Boolean getAddShadowUserOnLogin();

    /**
     * SAML assertion consumer index, default is 0
     */
    @JsonProperty("assertionConsumerIndex")
    @Nullable
    abstract Integer getAssertionConsumerIndex();

    /**
     * A list of AuthnContextClassRef to include in the SAML request
     */
    @JsonProperty("authnContext")
    @Nullable
    abstract List<String> getAuthnContext();

    /**
     * Either EXPLICITLY_MAPPED in order to map external groups to OAuth scopes using the group mappings, or AS_SCOPES to use SAML group names as scopes.
     */
    @JsonProperty("groupMappingMode")
    @Nullable
    abstract ExternalGroupMappingMode getGroupMappingMode();

    /**
     * Reserved for future use
     */
    @JsonProperty("iconUrl")
    @Nullable
    abstract String getIconUrl();

    /**
     * This will be set to origin by system
     */
    @JsonProperty("idpEntityAlias")
    @Nullable
    abstract String getIdpEntityAlias();

    /**
     * The link text for the SAML IDP on the login page
     */
    @JsonProperty("linkText")
    @Nullable
    abstract String getLinkText();

    /**
     * SAML Metadata - either an XML string or a URL that will deliver XML content
     */
    @JsonProperty("metaDataLocation")
    abstract String getMetaDataLocation();

    /**
     * Should metadata be validated, defaults to false
     */
    @JsonProperty("metadataTrustCheck")
    @Nullable
    abstract Boolean getMetadataTrustCheck();

    /**
     * The name ID to use for the username, default is “urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified”.
     */
    @JsonProperty("nameID")
    @Nullable
    abstract String getNameId();

    /**
     * Should the SAML login link be displayed on the login page, defaults to false
     */
    @JsonProperty("showSamlLink")
    @Nullable
    abstract Boolean getShowSamlLink();

    /**
     * Whether to skip SSL validation
     */
    @JsonProperty("skipSslValidation")
    @Nullable
    abstract Boolean getSkipSslValidation();

    /**
     * Either "org.apache.commons.httpclient.protocol.DefaultProtocolSocketFactory" or"org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory" depending on if the metaDataLocation of
     * type URL is HTTP or HTTPS, respectively
     */
    @JsonProperty("socketFactoryClassName")
    @Nullable
    abstract String getSocketFactoryClassName();

    /**
     * This will be set to the ID of the zone where the provider is being created by system
     */
    @JsonProperty("zoneId")
    @Nullable
    abstract String getZoneId();

}
