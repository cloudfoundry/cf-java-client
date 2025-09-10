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

package org.cloudfoundry.client.v3.spacequotadefinitions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * Quotas that affect services
 */
@JsonDeserialize
@Value.Immutable
abstract class _Services {

    /**
     * Specifies whether instances of paid service plans can be created
     * @return true if instances of paid service plans can be created, false otherwise
     */
    @JsonProperty("paid_services_allowed")
    abstract boolean isPaidServicesAllowed();

    /**
     * Total number of service instances allowed in a space
     * @return the total number of service instances allowed in a space
     */
    @JsonProperty("total_service_instances")
    @Nullable
    abstract Integer getTotalServiceInstances();

    /**
     * Total number of service keys allowed in a space
     * @return the total number of service keys allowed in a space
     */
    @JsonProperty("total_service_keys")
    @Nullable
    abstract Integer getTotalServiceKeys();
}
