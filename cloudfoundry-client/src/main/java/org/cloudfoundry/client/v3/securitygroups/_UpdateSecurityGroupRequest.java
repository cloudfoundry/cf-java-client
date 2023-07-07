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

import org.immutables.value.Value;

import java.util.List;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The request payload for the Create a Security Group operation
 */
@JsonSerialize
@Value.Immutable
abstract class _UpdateSecurityGroupRequest {
    /**
     * Name of the security group
     */
    @JsonProperty("name")
    abstract String getName();

    /**
     * Object that controls if the group is applied globally to the lifecycle of all
     * applications
     */
    @JsonProperty("globally_enabled")
    abstract GloballyEnabled getGloballyEnabled();

    /**
     * Rules that will be applied by this security group
     */
    @JsonProperty("rules")
    abstract List<Rule> getRules();
}
