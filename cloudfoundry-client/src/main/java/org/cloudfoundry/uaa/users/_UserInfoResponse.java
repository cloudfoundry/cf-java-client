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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * The info for a user
 */
@JsonDeserialize
@Value.Immutable
abstract class _UserInfoResponse {

    /**
     * The user's email address
     */
    @JsonProperty("email")
    abstract String getEmail();

    /**
     * Whether the user has verified their email address
     */
    @JsonProperty("email_verified")
    @Nullable
    abstract Boolean getEmailVerified();

    /**
     * The user's family name
     */
    @JsonProperty("family_name")
    abstract String getFamilyName();

    /**
     * The user's given name
     */
    @JsonProperty("given_name")
    abstract String getGivenName();

    /**
     * The name of the user
     */
    @JsonProperty("name")
    abstract String getName();

    /**
     * The user's phone number
     */
    @JsonProperty("phone_number")
    @Nullable
    abstract String getPhoneNumber();

    /**
     * When the user last logged on
     */
    @JsonProperty("previous_logon_time")
    @Nullable
    abstract Long getPreviousLogonTime();

    /**
     * The user subject identifier
     */
    @JsonProperty("sub")
    @Nullable
    abstract String getSub();

    /**
     * The user id
     */
    @JsonProperty("user_id")
    abstract String getUserId();

    /**
     * The user name
     */
    @JsonProperty("user_name")
    abstract String getUserName();

}
