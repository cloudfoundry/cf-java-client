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
import org.immutables.value.Value;

/**
 * The approvals for a user
 */
@JsonDeserialize
@Value.Immutable
abstract class _Approval {

    /**
     * The client id
     */
    @JsonProperty("clientId")
    abstract String getClientId();

    /**
     * When the approval expires
     */
    @JsonProperty("expiresAt")
    abstract String getExpiresAt();

    /**
     * When the approval was last updated
     */
    @JsonProperty("lastUpdatedAt")
    abstract String getLastUpdatedAt();

    /**
     * The scope on the approval
     */
    @JsonProperty("scope")
    abstract String getScope();

    /**
     * The status of the approval
     */
    @JsonProperty("status")
    abstract ApprovalStatus getStatus();

    /**
     * The user id
     */
    @JsonProperty("userId")
    abstract String getUserId();

}
