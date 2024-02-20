/*
 * Copyright 2013-2021 the original author or authors.
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

package org.cloudfoundry.client.v3.securitygroups;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

import java.util.List;

/**
 * The request payload for the Create a Security Group operation
 */
@JsonSerialize
@Value.Immutable
abstract class _CreateSecurityGroupRequest {

    /**
     * The security group name
     */
    @JsonProperty("name")
    abstract String getName();

    /**
     * the security group glbally enabled field
     */
    @JsonProperty("globally_enabled")
    @Nullable
    abstract GloballyEnabled getGloballyEnabled();

    /**
     * The security group rules
     */
    @JsonProperty("rules")
    @Nullable
    abstract List<Rule> getRules();

    /**
     * The security group relationships
     */
    @JsonProperty("relationships")
    @Nullable
    abstract Relationships getRelationships();

}
