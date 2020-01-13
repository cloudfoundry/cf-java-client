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

package org.cloudfoundry.uaa.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.uaa.IdentityZoned;
import org.immutables.value.Value;

import java.util.List;

/**
 * The request payload for the create user operation
 */
@JsonSerialize
@Value.Immutable
abstract class _CreateUserRequest implements IdentityZoned {

    @Value.Check
    void check() {
        if (getName() != null && (getName().getFamilyName() == null || getName().getFamilyName().isEmpty())) {
            throw new IllegalStateException("Cannot build CreateUserRequest, required attribute familyName is not set, or is empty");
        }

        if (getName() != null && (getName().getGivenName() == null || getName().getGivenName().isEmpty())) {
            throw new IllegalStateException("Cannot build CreateUserRequest, required attribute givenName is not set, or is empty");
        }

        if (getEmail().isEmpty()) {
            throw new IllegalStateException("Cannot build CreateUserRequest, at least one email address is required");
        }
    }

    /**
     * Whether the user is active
     */
    @JsonProperty("active")
    @Nullable
    abstract Boolean getActive();

    /**
     * The emails for the user
     */
    @JsonProperty("emails")
    abstract List<Email> getEmail();

    /**
     * The external id
     */
    @JsonProperty("externalId")
    @Nullable
    abstract String getExternalId();

    /**
     * The user's name
     */
    @JsonProperty("name")
    @Nullable
    abstract Name getName();

    /**
     * The identity provider that authenticated this user
     */
    @JsonProperty("origin")
    @Nullable
    abstract String getOrigin();

    /**
     * The password
     */
    @JsonProperty("password")
    @Nullable
    abstract String getPassword();

    /**
     * The phone numbers for the user
     */
    @JsonProperty("phoneNumbers")
    @Nullable
    abstract List<PhoneNumber> getPhoneNumbers();

    /**
     * The user name
     */
    @JsonProperty("userName")
    abstract String getUserName();

    /**
     * Whether the user's email is verified
     */
    @JsonProperty("verified")
    @Nullable
    abstract Boolean getVerified();

}
