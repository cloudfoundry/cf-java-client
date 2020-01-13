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

import java.util.Map;

/**
 * The payload for the identity zone saml configuration
 */
@JsonDeserialize
@Value.Immutable
abstract class _SamlConfiguration {

    /**
     * The active key id
     */
    @JsonProperty("activeKeyId")
    @Nullable
    abstract String getActiveKeyId();

    /**
     * If true, the SAML provider will sign all assertions.
     */
    @JsonProperty("assertionSigned")
    @Nullable
    abstract Boolean getAssertionSigned();

    /**
     * The lifetime of a SAML assertion in seconds.
     */
    @Nullable
    @JsonProperty("assertionTimeToLiveSeconds")
    abstract Integer getAssertionTimeToLive();

    /**
     * Exposed SAML metadata property. The certificate used to sign all communications.
     */
    @JsonProperty("certificate")
    @Nullable
    abstract String getCertificate();

    /**
     * If true, this zone will not validate the InResponseToField part of an incoming IDP assertion.
     */
    @JsonProperty("disableInResponseToCheck")
    @Nullable
    abstract Boolean getDisableInResponseToCheck();

    /**
     * Unique ID of the SAML2 entity.
     */
    @JsonProperty("entityID")
    @Nullable
    abstract String getEntityId();

    /**
     * The keys
     */
    @Nullable
    @JsonProperty("keys")
    abstract Map<String, Key> getKeys();

    /**
     * Exposed SAML metadata property. The SAML provider’s private key.
     */
    @JsonProperty("privateKey")
    @Nullable
    abstract String getPrivateKey();

    /**
     * Exposed SAML metadata property. The SAML provider’s private key password. Reserved for future use.
     */
    @JsonProperty("privateKeyPassword")
    @Nullable
    abstract String getPrivateKeyPassword();

    /**
     * Exposed SAML metadata property. If true, the service provider will sign all outgoing authentication requests.
     */
    @JsonProperty("requestSigned")
    @Nullable
    abstract Boolean getRequestSigned();

    /**
     * Exposed SAML metadata property. If true, all assertions received by the SAML provider must be signed.
     */
    @JsonProperty("wantAssertionSigned")
    @Nullable
    abstract Boolean getWantAssertionSigned();

    /**
     * If true, the authentication request from the partner service provider must be signed.
     */
    @JsonProperty("wantAuthnRequestSigned")
    @Nullable
    abstract Boolean getWantPartnerAuthenticationRequestSigned();

}
