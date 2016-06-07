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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.uaa.Versioned;
import org.immutables.value.Value;

import java.util.List;

/**
 * The request payload for the update user operation
 */
@Value.Immutable
abstract class _UpdateUserRequest implements Versioned {

    /**
     * The version
     */
    @JsonIgnore
    @Override
    public abstract String getVersion();

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
    abstract List<Email> getEmails();

    /**
     * The external id
     */
    @JsonProperty("externalId")
    @Nullable
    abstract String getExternalId();

    /**
     * The id
     */
    @JsonProperty("id")
    @JsonIgnore
    abstract String getId();

    /**
     * The user's name
     */
    @JsonProperty("name")
    abstract Name getName();

    /**
     * The identity provider that authenticated this user
     */
    @JsonProperty("origin")
    @Nullable
    abstract String getOrigin();

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
