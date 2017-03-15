/*
 * Copyright 2013-2017 the original author or authors.
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

package org.cloudfoundry.client.v3.isolationsegments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.client.v3.Relationship;
import org.immutables.value.Value;

import java.util.List;

/**
 * The request payload for the Entitle one or more Organizations for an Isolation Segment operation
 */
@Value.Immutable
abstract class _AddIsolationSegmentOrganizationEntitlementRequest {

    @Value.Check
    void checkData() {
        if (getData().isEmpty()) {
            throw new IllegalStateException("Cannot build AddIsolationSegmentOrganizationEntitlementRequest, required attribute data is not set");
        }
    }

    /**
     * The organizations to add entitlement to
     */
    @JsonProperty("data")
    abstract List<Relationship> getData();

    /**
     * The isolation segment id
     */
    @JsonIgnore
    abstract String getIsolationSegmentId();

}
