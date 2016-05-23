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
import java.util.Map;

/**
 * The payload for the identity zone token policy
 */
@JsonDeserialize
@Value.Immutable
abstract class _TokenPolicy {

    /**
     * Time in seconds between when a access token is issued and when it expires
     */
    @JsonProperty("accessTokenValidity")
    @Nullable
    abstract Integer getAccessTokenValidity();

    /**
     * The ID of the key that is used to sign tokens
     */
    @JsonProperty("activeKeyId")
    @Nullable
    abstract String getActiveKeyId();

    /**
     *
     */
    @JsonProperty("jwtRevocable")
    @Nullable
    abstract Boolean getJwtRevokable();

    /**
     * The keys of the token policy
     */
    @JsonDeserialize(using = KeysDeserializer.class)
    @JsonProperty("keys")
    @Nullable
    abstract Map<String, KeyInformation> getKeys();

    /**
     * Time in seconds between when a refresh token is issued and when it expires
     */
    @JsonProperty("refreshTokenValidity")
    @Nullable
    abstract Integer getRefreshTokenValidity();

    static final class KeysDeserializer extends StdDeserializer<Map<String, KeyInformation>> {


        KeysDeserializer() {
            super(Map.class);
        }

        @SuppressWarnings("deprecation")
        @Override
        public Map<String, KeyInformation> deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
            return p.readValueAs(new TypeReference<Map<String, KeyInformation>>() {

            });
        }
    }

}
