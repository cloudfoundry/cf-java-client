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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

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
    @JsonProperty("external_groups")
    @JsonFormat(with = Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
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

}
