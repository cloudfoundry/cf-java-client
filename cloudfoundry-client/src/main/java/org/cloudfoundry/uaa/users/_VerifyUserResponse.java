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

package org.cloudfoundry.uaa.users;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.List;

/**
 * The response from the create user request
 */
@JsonDeserialize
@Value.Immutable
abstract class _VerifyUserResponse {

    /**
     * Whether the user is active
     */
    @JsonProperty("active")
    abstract Boolean getActive();

    /**
     * The emails for the user
     */
    @JsonProperty("emails")
    abstract List<Email> getEmail();

    /**
     * The user id
     */
    @JsonProperty("id")
    abstract String getId();

    /**
     * Metadata for the result
     */
    @JsonProperty("meta")
    abstract Meta getMeta();

    /**
     * The user's name
     */
    @JsonProperty("name")
    abstract Name getName();

    /**
     * The identity provider that authenticated this user
     */
    @JsonProperty("origin")
    abstract String getOrigin();

    /**
     * The timestamp when the user's password was last modified
     */
    @JsonProperty("passwordLastModified")
    abstract String getPasswordLastModified();

    /**
     * The schemas
     */
    @JsonProperty("schemas")
    abstract List<String> getSchemas();

    /**
     * The user name
     */
    @JsonProperty("userName")
    public abstract String getUserName();

    /**
     * Whether the user's email is verified
     */
    @JsonProperty("verified")
    abstract Boolean getVerified();

    /**
     * The zone id the user belongs to
     */
    @JsonProperty("zoneId")
    abstract String getZoneId();

}
