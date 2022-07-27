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

package org.cloudfoundry.client.v3.servicebindings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.cloudfoundry.AllowNulls;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.Metadata;
import org.immutables.value.Value;

import java.util.Map;

/**
 * The request payload for the Create Service Binding operation.
 */
@JsonSerialize
@Value.Immutable
abstract class _CreateServiceBindingRequest {

    /**
     * The name
     */
    @JsonProperty("name")
    @Nullable
    abstract String getName();

    /**
     * The relationships
     */
    @JsonProperty("relationships")
    abstract ServiceBindingRelationships getRelationships();

    /**
     * The type
     */
    @JsonProperty("type")
    abstract ServiceBindingType getType();

    /**
     * The parameters
     */
    @JsonProperty("parameters")
    @Nullable
    @AllowNulls
    abstract Map<String, Object> getParameters();

    /**
     * The metadata
     */
    @JsonProperty("metadata")
    @Nullable
    abstract Metadata getMetadata();

    @Value.Check
    void validateParameters() {
        if (ServiceBindingType.KEY.equals(getType()) && getName() == null) {
            throw new IllegalStateException("A name is required for a service binding of type 'key'");
        }
    }
}
