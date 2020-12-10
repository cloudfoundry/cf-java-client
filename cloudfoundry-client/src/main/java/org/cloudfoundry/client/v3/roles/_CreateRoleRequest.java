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

package org.cloudfoundry.client.v3.roles;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * The request payload for the Create Role operation
 */
@JsonSerialize
@Value.Immutable
abstract class _CreateRoleRequest {

    /**
     * The type
     */
    @JsonProperty("type")
    abstract RoleType getType();

    /**
     * The relationships
     */
    @JsonProperty("relationships")
    abstract RoleRelationships getRelationships();

    @Value.Check
    protected void validateRelationships() {
        if (RoleType.ORGANIZATION_ROLE_TYPES.contains(getType()) && getRelationships().getOrganization() == null) {
            throw new IllegalStateException("An organization relationship is required for an " + getType() + " role");
        }
        if (RoleType.SPACE_ROLE_TYPES.contains(getType()) && getRelationships().getSpace() == null) {
            throw new IllegalStateException("A space relationship is required for a " + getType() + " role");
        }
    }

}
