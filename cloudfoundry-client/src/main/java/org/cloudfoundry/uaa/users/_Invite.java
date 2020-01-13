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
 * An invite for a new user
 */
@JsonDeserialize
@Value.Immutable
abstract class _Invite {

    /**
     * The email address
     */
    @JsonProperty("email")
    abstract String getEmail();

    /**
     * The error code
     */
    @JsonProperty("errorCode")
    @Nullable
    abstract String getErrorCode();

    /**
     * The error message
     */
    @JsonProperty("errorMessage")
    @Nullable
    abstract String getErrorMessage();

    /**
     * The invite link
     */
    @JsonProperty("inviteLink")
    @Nullable
    abstract String getInviteLink();

    /**
     * The origin
     */
    @JsonProperty("origin")
    @Nullable
    abstract String getOrigin();

    /**
     * Whether the invite was sent successfully
     */
    @JsonProperty("success")
    abstract Boolean getSuccess();

    /**
     * The user id
     */
    @JsonProperty("userId")
    @Nullable
    abstract String getUserId();

}
