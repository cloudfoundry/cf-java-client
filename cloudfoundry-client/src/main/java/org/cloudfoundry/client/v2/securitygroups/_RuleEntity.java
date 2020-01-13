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

package org.cloudfoundry.client.v2.securitygroups;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * A security group rule
 */
@JsonDeserialize
@Value.Immutable
abstract class _RuleEntity {

    /**
     * The code control signal for icmp
     */
    @JsonProperty("code")
    @Nullable
    abstract Integer getCode();

    /**
     * The description of the rule
     */
    @JsonProperty("description")
    @Nullable
    abstract String getDescription();

    /**
     * The destination
     */
    @JsonProperty("destination")
    @Nullable
    abstract String getDestination();

    /**
     * Enables logging for the egress rule
     */
    @JsonProperty("log")
    @Nullable
    abstract Boolean getLog();

    /**
     * The ports
     */
    @JsonProperty("ports")
    @Nullable
    abstract String getPorts();

    /**
     * The protocol
     */
    @JsonProperty("protocol")
    @Nullable
    abstract Protocol getProtocol();

    /**
     * The type control signal for icmp
     */
    @JsonProperty("type")
    @Nullable
    abstract Integer getType();

}
