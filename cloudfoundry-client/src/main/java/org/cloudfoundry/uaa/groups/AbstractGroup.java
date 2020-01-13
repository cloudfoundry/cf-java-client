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

package org.cloudfoundry.uaa.groups;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.uaa.Metadata;

import java.util.List;

/**
 * The entity response payload for Group
 */
abstract class AbstractGroup {

    /**
     * Human readable description of the group, displayed e.g. when approving scopes
     */
    @JsonProperty("description")
    @Nullable
    abstract String getDescription();

    /**
     * The identifier specified upon creation of the group, unique within the identity zone
     */
    @JsonProperty("displayName")
    abstract String getDisplayName();

    /**
     * The globally unique group ID
     */
    @JsonProperty("id")
    abstract String getId();

    /**
     * Array of group members
     */
    @JsonProperty("members")
    abstract List<MemberSummary> getMembers();

    /**
     * The group's metadata
     */
    @JsonProperty("meta")
    abstract Metadata getMetadata();

    /**
     * The group's schemas
     */
    @JsonProperty("schemas")
    abstract List<String> getSchemas();

    /**
     * Identifier for the identity zone to which the group belongs
     */
    @JsonProperty("zoneId")
    abstract String getZoneId();

}
