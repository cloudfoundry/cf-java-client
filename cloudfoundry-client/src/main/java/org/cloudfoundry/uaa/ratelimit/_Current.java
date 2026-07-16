/*
 * Copyright 2013-2025 the original author or authors.
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

package org.cloudfoundry.uaa.ratelimit;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Date;

import org.immutables.value.Value;

/**
 * The payload for the uaa ratelimiting
 */
@JsonDeserialize
@Value.Immutable
abstract class _Current {

    /**
     * The number of configured limiter mappings
     */
    @JsonProperty("limiterMappings")
    abstract Integer getLimiterMappings();

    /**
     * Is ratelimit "ACTIVE" or not? Possible values are DISABLED, PENDING, ACTIVE
     */
    @JsonProperty("status")
    abstract String getStatus();

    /**
     * Timestamp, when this Current was created.
     */
    @JsonProperty("asOf")
    abstract Date getTimeOfCurrent();

    /**
     * The credentialIdExtractor
     */
    @JsonProperty("credentialIdExtractor")
    abstract String getCredentialIdExtractor();

    /**
     * The loggingLevel. Valid values include: "OnlyLimited", "AllCalls" and "AllCallsWithDetails"
     */
    @JsonProperty("loggingLevel")
    abstract String getLoggingLevel();
}
