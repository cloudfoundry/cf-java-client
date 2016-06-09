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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * The payload for the identity provider configuration attribute mappings
 */
@JsonDeserialize
@Value.Immutable
abstract class _AttributeMappings {

    /**
     * Map email to the attribute for email in the provider assertion.
     */
    @JsonProperty("email")
    @Nullable
    abstract String getEmail();

    /**
     * Map external_groups to the attribute for groups in the provider assertion (can be a list or a string).
     */
    @JsonDeserialize(using = ExternalGroupsDeserializer.class)
    @JsonProperty("external_groups")
    @Nullable
    abstract List<String> getExternalGroups();

    /**
     * Map family_name to the attribute for family name in the provider assertion.
     */
    @JsonProperty("family_name")
    @Nullable
    abstract String getFamilyName();

    /**
     * Map first_name to the attribute for fist name in the provider assertion.
     */
    @JsonProperty("first_name")
    @Nullable
    abstract String getFirstName();

    /**
     * Map given_name to the attribute for given name in the provider assertion.
     */
    @JsonProperty("given_name")
    @Nullable
    abstract String getGivenName();

    /**
     * Map phone_number to the attribute for phone number in the provider assertion.
     */
    @JsonProperty("phone_number")
    @Nullable
    abstract String getPhoneNumber();

    static final class ExternalGroupsDeserializer extends StdDeserializer<List<String>> {

        private static final long serialVersionUID = -5615099418928747237L;

        protected ExternalGroupsDeserializer() {
            super(List.class);
        }

        @SuppressWarnings("deprecation")
        @Override
        public List<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            System.err.println(p.getCurrentToken());
            switch (p.getCurrentToken()) {
                case VALUE_STRING:
                    return Collections.singletonList(p.readValueAs(String.class));
                case START_ARRAY:
                    return p.readValueAs(new TypeReference<List<String>>() {

                    });
                default:
                    throw new JsonMappingException("Can not deserialize instance of java.util.ArrayList out of " + p.getCurrentToken() + " token", p.getCurrentLocation());
            }

        }

    }

}
