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
import org.cloudfoundry.client.v2.PaginatedResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * The response payload for the List Events operation
 *
 * <p><b>This class is NOT threadsafe.</b>
 */
public final class ListEventsResponse extends PaginatedResponse<ListEventsResponse, ListEventsResponse.Entity> {

    /**
     * The Entity response payload for the List Events operation
     *
     * <p><b>This class is NOT threadsafe.</b>
     */
    public static final class Entity {

        private volatile String actee;

        private volatile String acteeName;

        private volatile String acteeType;

        private volatile String actor;

        private volatile String actorName;

        private volatile String actorType;

        private final Map<String, Object> metadatas = new HashMap<>();

        private volatile String organizationId;

        private volatile String spaceId;

        private volatile String timestamp;

        private volatile String type;

        /**
         * Returns the actee
         *
         * @return the actee
         */
        @JsonProperty("actee")
        public String getActee() {
            return this.actee;
        }

        /**
         * Configure the actee
         *
         * @param actee the actee
         * @return {@code this}
         */
        public Entity withActee(String actee) {
            this.actee = actee;
            return this;
        }

        /**
         * Returns the actee name
         *
         * @return the actee name
         */
        @JsonProperty("actee_name")
        public String getActeeName() {
            return this.acteeName;
        }

        /**
         * Configure the actee name
         *
         * @param acteeName the actee name
         * @return {@code this}
         */
        public Entity withActeeName(String acteeName) {
            this.acteeName = acteeName;
            return this;
        }

        /**
         * Returns the actee type
         *
         * @return the actee type
         */
        @JsonProperty("actee_type")
        public String getActeeType() {
            return this.acteeType;
        }

        /**
         * Configure the actee type
         *
         * @param acteeType the actee type
         * @return {@code this}
         */
        public Entity withActeeType(String acteeType) {
            this.acteeType = acteeType;
            return this;
        }

        /**
         * Returns the actor
         *
         * @return the actor
         */
        @JsonProperty("actor")
        public String getActor() {
            return this.actor;
        }

        /**
         * Configure the actor
         *
         * @param actor the actor
         * @return {@code this}
         */
        public Entity withActor(String actor) {
            this.actor = actor;
            return this;
        }

        /**
         * Returns the actor name
         *
         * @return the actor name
         */
        @JsonProperty("actor_name")
        public String getActorName() {
            return this.actorName;
        }

        /**
         * Configure the actor name
         *
         * @param actorName the actor name
         * @return {@code this}
         */
        public Entity withActorName(String actorName) {
            this.actorName = actorName;
            return this;
        }

        /**
         * Returns the actor type
         *
         * @return the actor type
         */
        @JsonProperty("actor_type")
        public String getActorType() {
            return this.actorType;
        }

        /**
         * Configure the actor type
         *
         * @param actorType the actor type
         * @return {@code this}
         */
        public Entity withActorType(String actorType) {
            this.actorType = actorType;
            return this;
        }

        /**
         * Returns the metadatas
         *
         * @return the metadatas
         */
        @JsonProperty("metadata")
        public Map<String, Object> getMetadatas() {
            return this.metadatas;
        }

        /**
         * Configure a metadata
         *
         * @param key   the key
         * @param value the value
         * @return {@code this}
         */
        public Entity withMetadata(String key, Object value) {
            this.metadatas.put(key, value);
            return this;
        }

        /**
         * Configure the metadatas
         *
         * @param metadatas the metadatas
         * @return {@code this}
         */
        public Entity withMetadatas(Map<String, Object> metadatas) {
            this.metadatas.putAll(metadatas);
            return this;
        }

        /**
         * Returns organization id
         *
         * @return organization id
         */
        @JsonProperty("organization_guid")
        public String getOrganizationId() {
            return this.organizationId;
        }

        /**
         * Configure the organization id
         *
         * @param organizationId organization id
         * @return {@code this}
         */
        public Entity withOrganizationId(String organizationId) {
            this.organizationId = organizationId;
            return this;
        }

        /**
         * Returns the space id
         *
         * @return the space id
         */
        @JsonProperty("space_guid")
        public String getSpaceId() {
            return this.spaceId;
        }

        /**
         * Configure the space id
         *
         * @param spaceId the space id
         * @return {@code this}
         */
        public Entity withSpaceId(String spaceId) {
            this.spaceId = spaceId;
            return this;
        }

        /**
         * Returns the timestamp
         *
         * @return the timestamp
         */
        @JsonProperty("timestamp")
        public String getTimestamp() {
            return this.timestamp;
        }

        /**
         * Configure the timestamp
         *
         * @param timestamp the timestamp
         * @return {@code this}
         */
        public Entity withTimestamp(String timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        /**
         * Returns the type
         *
         * @return the type
         */
        @JsonProperty("type")
        public String getType() {
            return this.type;
        }

        /**
         * Configure the type
         *
         * @param type the type
         * @return {@code this}
         */
        public Entity withType(String type) {
            this.type = type;
            return this;
        }

    }
}
