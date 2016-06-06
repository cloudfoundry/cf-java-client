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

package org.cloudfoundry.uaa.identityzones;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

import java.io.IOException;
import java.util.List;

/**
 * The payload for the identity zone configuration
 */
@JsonDeserialize
@Value.Immutable
abstract class _IdentityZoneConfiguration {

    /**
     * IDP Discovery should be set to true if you have configured more than one identity provider for UAA. The discovery relies on email domain being set for each additional provider.
     */
    @JsonProperty("idpDiscoveryEnabled")
    @Nullable
    abstract Boolean getLdapDiscoveryEnabled();

    /**
     * Array The links
     */
    @JsonDeserialize(using = LinksDeserializer.class)
    @JsonProperty("links")
    @Nullable
    abstract Links getLinks();

    /**
     * The prompts
     */
    @JsonDeserialize(using = PromptsDeserializer.class)
    @JsonProperty("prompts")
    @Nullable
    abstract List<Prompt> getPrompts();

    /**
     * The saml configuration
     */
    @JsonDeserialize(using = SamlConfigurationDeserializer.class)
    @JsonProperty("samlConfig")
    @Nullable
    abstract SamlConfiguration getSamlConfiguration();

    /**
     * The token policy
     */
    @JsonDeserialize(using = TokenPolicyDeserializer.class)
    @JsonProperty("tokenPolicy")
    @Nullable
    abstract TokenPolicy getTokenPolicy();

    static final class LinksDeserializer extends StdDeserializer<Links> {


        LinksDeserializer() {
            super(Links.class);
        }

        @SuppressWarnings("deprecation")
        @Override
        public Links deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
            return p.readValueAs(Links.class);
        }
    }

    static final class PromptsDeserializer extends StdDeserializer<List<Prompt>> {


        PromptsDeserializer() {
            super(List.class);
        }

        @SuppressWarnings("deprecation")
        @Override
        public List<Prompt> deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
            return p.readValueAs(new TypeReference<List<Prompt>>() {

            });
        }
    }

    static final class SamlConfigurationDeserializer extends StdDeserializer<SamlConfiguration> {


        SamlConfigurationDeserializer() {
            super(SamlConfiguration.class);
        }

        @SuppressWarnings("deprecation")
        @Override
        public SamlConfiguration deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
            return p.readValueAs(SamlConfiguration.class);
        }
    }

    static final class TokenPolicyDeserializer extends StdDeserializer<TokenPolicy> {


        TokenPolicyDeserializer() {
            super(TokenPolicy.class);
        }

        @SuppressWarnings("deprecation")
        @Override
        public TokenPolicy deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
            return p.readValueAs(TokenPolicy.class);
        }
    }

}
