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

package org.cloudfoundry.client.v3.serviceofferings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

/**
 * Broker Catalog information for a Service Offering
 */
@JsonDeserialize
@Value.Immutable
abstract class _Features {

    /**
     * Whether service instance updates relating only to context are propagated to the service broker
     */
    @JsonProperty("allow_context_updates")
    abstract Boolean getAllowContextUpdates();

    /**
     * Whether service instances of the service can be bound to applications
     */
    @JsonProperty("bindable")
    abstract Boolean getBindable();

    /**
     * Whether the Fetching a service binding endpoint is supported for all service plans
     */
    @JsonProperty("bindings_retrievable")
    abstract Boolean getBindingsRetrievable();

    /**
     * Whether the Fetching a service instance endpoint is supported for all service plans
     */
    @JsonProperty("instances_retrievable")
    abstract Boolean getInstancesRetrievable();

    /**
     * Whether the service offering supports upgrade/downgrade for service plans by default
     */
    @JsonProperty("plan_updateable")
    abstract Boolean getPlanUpdateable();
}
