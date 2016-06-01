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

package org.cloudfoundry.uaa.identityproviders;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

/**
 * The entity response payload for Identity Provider
 */
abstract class AbstractIdentityProvider {

    /**
     * Whether the identity provider is active
     */
    @JsonProperty("active")
    abstract Boolean getActive();

    /**
     * The configuration of this identity provider
     */
    @JsonProperty("config")
    abstract IdentityProviderConfiguration getConfiguration();

    /**
     * The creation date of the identity provider
     */
    @JsonProperty("created")
    abstract Long getCreatedAt();

    /**
     * The id
     */
    @JsonProperty("id")
    abstract String getId();

    /**
     * Set to the zone that this provider will be active in. Determined either by the Host header or the zone switch header.
     */
    @JsonProperty("identityZoneId")
    abstract String getIdentityZoneId();

    /**
     * The last modification date of the identity provider
     */
    @JsonProperty("last_modified")
    abstract Long getLastModified();

    /**
     * Human-readable name for this provider
     */
    @JsonProperty("name")
    abstract String getName();

    /**
     * A unique alias for the SAML provider
     */
    @JsonProperty("originKey")
    abstract String getOriginKey();

    /**
     * The type of identity provider
     */
    @JsonProperty("type")
    abstract Type getType();

    /**
     * Version of the identity provider data. Clients can use this to protect against conflicting updates
     */
    @JsonProperty("version")
    abstract Integer getVersion();


    static abstract class AbstractConfigurationDeserializer extends StdDeserializer<IdentityProviderConfiguration> {

        private static final long serialVersionUID = 7944719509270679039L;

        AbstractConfigurationDeserializer() {
            super(IdentityProviderConfiguration.class);
        }

        @Override
        public IdentityProviderConfiguration deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            Type type = getType(p);
            switch (type) {
                case SAML:
                    return p.readValueAs(SamlConfiguration.class);
                case LDAP:
                    return p.readValueAs(LdapConfiguration.class);
                case OAUTH2:
                    return p.readValueAs(Oauth2Configuration.class);
                case OIDC:
                    return p.readValueAs(OpenIdConnectConfiguration.class);
                default:
                    throw new IllegalArgumentException(String.format("Unknown identity provider type: %s", type));
            }
        }

        abstract Type getType(JsonParser p);

    }

}
