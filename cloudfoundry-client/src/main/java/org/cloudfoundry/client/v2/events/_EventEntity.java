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

package org.cloudfoundry.client.v2.events;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.Map;
import java.util.Optional;

/**
 * The entity response payload for the Event resource
 */
@JsonDeserialize
@Value.Immutable
abstract class _EventEntity {

    /**
     * The actee
     */
    @JsonProperty("actee")
    abstract String getActee();

    /**
     * The actee name
     */
    @JsonProperty("actee_name")
    abstract String getActeeName();

    /**
     * The actee type
     */
    @JsonProperty("actee_type")
    abstract String getActeeType();

    /**
     * The actor
     */
    @JsonProperty("actor")
    abstract String getActor();

    /**
     * The actor name
     */
    @JsonProperty("actor_name")
    abstract String getActorName();

    /**
     * The actor type
     */
    @JsonProperty("actor_type")
    abstract String getActorType();

    /**
     * The metadatas
     */
    @JsonProperty("metadata")
    abstract Map<String, Optional<Object>> getMetadatas();

    /**
     * The organization id
     */
    @JsonProperty("organization_guid")
    abstract String getOrganizationId();

    /**
     * The space id
     */
    @JsonProperty("space_guid")
    abstract String getSpaceId();

    /**
     * The timestamp
     */
    @JsonProperty("timestamp")
    abstract String getTimestamp();

    /**
     * The type
     */
    @JsonProperty("type")
    abstract String getType();

}
