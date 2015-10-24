/*
 * Copyright 2013-2015 the original author or authors.
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
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;
import org.cloudfoundry.client.v2.Resource;

import java.util.Map;

/**
 * Base class for resources that contain events
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class EventResource extends Resource<EventResource.EventEntity> {

    protected EventResource(@JsonProperty("entity") EventEntity entity,
                            @JsonProperty("metadata") Metadata metadata) {
        super(entity, metadata);
    }

    /**
     * The entity response payload for the Event resource
     */
    @Data
    public static final class EventEntity {

        /**
         * The actee
         *
         * @param actee the actee
         * @retuen the actee
         */
        private final String actee;

        /**
         * The actee name
         *
         * @param acteeName the actee name
         * @return the actee name
         */
        private final String acteeName;

        /**
         * The actee type
         *
         * @param acteeType the actee type
         * @return the actee type
         */
        private final String acteeType;

        /**
         * The actor
         *
         * @param actor the actor
         * @return the actor
         */
        private final String actor;

        /**
         * The actor name
         *
         * @param actorName the actor name
         * @return the actor name
         */
        private final String actorName;

        /**
         * The actor type
         *
         * @param actorType the actor type
         * @return the actor type
         */
        private final String actorType;

        /**
         * The metadatas
         *
         * @param metadatas the metadatas
         * @return the metadatas
         */
        private final Map<String, Object> metadatas;

        /**
         * The organization id
         *
         * @param organizationId the organization id
         * @return the organization id
         */
        private final String organizationId;

        /**
         * The space id
         *
         * @param spaceId the space id
         * @return the space id
         */
        private final String spaceId;

        /**
         * The timestamp
         *
         * @param timestamp the timestamp
         * @return the timestamp
         */
        private final String timestamp;

        /**
         * The type
         *
         * @param type the type
         * @return the type
         */
        private final String type;

        @Builder
        EventEntity(@JsonProperty("actee") String actee,
                    @JsonProperty("actee_name") String acteeName,
                    @JsonProperty("actee_type") String acteeType,
                    @JsonProperty("actor") String actor,
                    @JsonProperty("actor_name") String actorName,
                    @JsonProperty("actor_type") String actorType,
                    @JsonProperty("metadata") @Singular Map<String, Object> metadatas,
                    @JsonProperty("organization_guid") String organizationId,
                    @JsonProperty("space_guid") String spaceId,
                    @JsonProperty("timestamp") String timestamp,
                    @JsonProperty("type") String type) {
            this.actee = actee;
            this.acteeName = acteeName;
            this.acteeType = acteeType;
            this.actor = actor;
            this.actorName = actorName;
            this.actorType = actorType;
            this.metadatas = metadatas;
            this.organizationId = organizationId;
            this.spaceId = spaceId;
            this.timestamp = timestamp;
            this.type = type;
        }

    }

}
