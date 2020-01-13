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

package org.cloudfoundry.uaa.identityzones;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

import java.util.List;

/**
 * The payload for the identity zone configuration
 */
@JsonDeserialize
@Value.Immutable
abstract class _IdentityZoneConfiguration {

    /**
     * This flag is required to enable account choosing functionality for IDP discovery page.
     */
    @JsonProperty("accountChooserEnabled")
    @Nullable
    abstract Boolean getAccountChooserEnabled();

    /**
     * The branding
     */
    @JsonProperty("branding")
    @Nullable
    abstract Branding getBranding();

    /**
     * The client lockout policy
     */
    @JsonProperty("clientLockoutPolicy")
    @Nullable
    abstract ClientLockoutPolicy getClientLockoutPolicy();

    /**
     * The client secret policy
     */
    @JsonProperty("clientSecretPolicy")
    @Nullable
    abstract ClientSecretPolicy getClientSecretPolicy();

    /**
     * The CORS policy
     */
    @JsonProperty("corsPolicy")
    @Nullable
    abstract CorsPolicy getCorsPolicy();

    /**
     * The issuer of this zone
     */
    @JsonProperty("issuer")
    @Nullable
    abstract String getIssuer();

    /**
     * IDP Discovery should be set to true if you have configured more than one identity provider for UAA. The discovery relies on email domain being set for each additional provider.
     */
    @JsonProperty("idpDiscoveryEnabled")
    @Nullable
    abstract Boolean getLdapDiscoveryEnabled();

    /**
     * The links
     */
    @JsonProperty("links")
    @Nullable
    abstract Links getLinks();

    /**
     * The Multi-factor Authentication configuration
     */
    @JsonProperty("mfaConfig")
    @Nullable
    abstract MfaConfig getMfaConfig();

    /**
     * The prompts
     */
    @JsonProperty("prompts")
    @Nullable
    abstract List<Prompt> getPrompts();

    /**
     * The saml configuration
     */
    @JsonProperty("samlConfig")
    @Nullable
    abstract SamlConfiguration getSamlConfiguration();

    /**
     * The token policy
     */
    @JsonProperty("tokenPolicy")
    @Nullable
    abstract TokenPolicy getTokenPolicy();

    /**
     * The user configuration
     */
    @JsonProperty("userConfig")
    @Nullable
    abstract UserConfig getUserConfig();

}
