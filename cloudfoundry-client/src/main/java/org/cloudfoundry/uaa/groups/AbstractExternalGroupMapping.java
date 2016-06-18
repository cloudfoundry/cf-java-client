/*
 * Copyright 2013-2016 the original author or authors.
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
import org.cloudfoundry.uaa.Metadata;

import java.util.List;

/**
 * The entity response payload for External Group
 */
abstract class AbstractExternalGroupMapping {

    /**
     * The identifier for the group in external identity provider that needs to be mapped to internal UAA groups
     */
    @JsonProperty("externalGroup")
    abstract String getExternalGroup();

    /**
     * The group's displayed name
     */
    @JsonProperty("displayName")
    abstract String getGroupDisplayName();

    /**
     * The group unique ID
     */
    @JsonProperty("groupId")
    abstract String getGroupId();

    /**
     * The group's metadata
     */
    @JsonProperty("meta")
    abstract Metadata getMetadata();

    /**
     * Unique alias of the identity provider
     */
    @JsonProperty("origin")
    abstract String getOriginKey();

    /**
     * The group's schemas:  "urn:scim:schemas:core:1.0" ]
     */
    @JsonProperty("schemas")
    abstract List<String> getSchemas();

}
