/*
 * Copyright 2013-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.cloudfoundry.client.v3.securitygroups;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * Controls if the group is applied globally to the lifecycle of all applications
 */
@JsonDeserialize
@Value.Immutable
abstract class _GloballyEnabled {

    /**
     * Specifies whether the group should be applied globally to all running applications
     */
    @JsonProperty("running")
    @Nullable
    abstract Boolean getRunning();

    /**
     * Specifies whether the group should be applied globally to all staging applications
     */
    @JsonProperty("staging")
    @Nullable
    abstract Boolean getStaging();

}
