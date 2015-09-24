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

import java.util.HashMap;
import java.util.Map;

abstract class Event<T extends Event<T>> {

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
    public final String getActee() {
        return this.actee;
    }

    /**
     * Configure the actee
     *
     * @param actee the actee
     * @return {@code this}
     */
    @SuppressWarnings("unchecked")
    public final T withActee(String actee) {
        this.actee = actee;
        return (T) this;
    }

    /**
     * Returns the actee name
     *
     * @return the actee name
     */
    @JsonProperty("actee_name")
    public final String getActeeName() {
        return this.acteeName;
    }

    /**
     * Configure the actee name
     *
     * @param acteeName the actee name
     * @return {@code this}
     */
    @SuppressWarnings("unchecked")
    public final T withActeeName(String acteeName) {
        this.acteeName = acteeName;
        return (T) this;
    }

    /**
     * Returns the actee type
     *
     * @return the actee type
     */
    @JsonProperty("actee_type")
    public final String getActeeType() {
        return this.acteeType;
    }

    /**
     * Configure the actee type
     *
     * @param acteeType the actee type
     * @return {@code this}
     */
    @SuppressWarnings("unchecked")
    public final T withActeeType(String acteeType) {
        this.acteeType = acteeType;
        return (T) this;
    }

    /**
     * Returns the actor
     *
     * @return the actor
     */
    @JsonProperty("actor")
    public final String getActor() {
        return this.actor;
    }

    /**
     * Configure the actor
     *
     * @param actor the actor
     * @return {@code this}
     */
    @SuppressWarnings("unchecked")
    public final T withActor(String actor) {
        this.actor = actor;
        return (T) this;
    }

    /**
     * Returns the actor name
     *
     * @return the actor name
     */
    @JsonProperty("actor_name")
    public final String getActorName() {
        return this.actorName;
    }

    /**
     * Configure the actor name
     *
     * @param actorName the actor name
     * @return {@code this}
     */
    @SuppressWarnings("unchecked")
    public final T withActorName(String actorName) {
        this.actorName = actorName;
        return (T) this;
    }

    /**
     * Returns the actor type
     *
     * @return the actor type
     */
    @JsonProperty("actor_type")
    public final String getActorType() {
        return this.actorType;
    }

    /**
     * Configure the actor type
     *
     * @param actorType the actor type
     * @return {@code this}
     */
    @SuppressWarnings("unchecked")
    public final T withActorType(String actorType) {
        this.actorType = actorType;
        return (T) this;
    }

    /**
     * Returns the metadatas
     *
     * @return the metadatas
     */
    @JsonProperty("metadata")
    public final Map<String, Object> getMetadatas() {
        return this.metadatas;
    }

    /**
     * Configure a metadata
     *
     * @param key   the key
     * @param value the value
     * @return {@code this}
     */
    @SuppressWarnings("unchecked")
    public final T withMetadata(String key, Object value) {
        this.metadatas.put(key, value);
        return (T) this;
    }

    /**
     * Configure the metadatas
     *
     * @param metadatas the metadatas
     * @return {@code this}
     */
    @SuppressWarnings("unchecked")
    public final T withMetadatas(Map<String, Object> metadatas) {
        this.metadatas.putAll(metadatas);
        return (T) this;
    }

    /**
     * Returns organization id
     *
     * @return organization id
     */
    @JsonProperty("organization_guid")
    public final String getOrganizationId() {
        return this.organizationId;
    }

    /**
     * Configure the organization id
     *
     * @param organizationId organization id
     * @return {@code this}
     */
    @SuppressWarnings("unchecked")
    public final T withOrganizationId(String organizationId) {
        this.organizationId = organizationId;
        return (T) this;
    }

    /**
     * Returns the space id
     *
     * @return the space id
     */
    @JsonProperty("space_guid")
    public final String getSpaceId() {
        return this.spaceId;
    }

    /**
     * Configure the space id
     *
     * @param spaceId the space id
     * @return {@code this}
     */
    @SuppressWarnings("unchecked")
    public final T withSpaceId(String spaceId) {
        this.spaceId = spaceId;
        return (T) this;
    }

    /**
     * Returns the timestamp
     *
     * @return the timestamp
     */
    @JsonProperty("timestamp")
    public final String getTimestamp() {
        return this.timestamp;
    }

    /**
     * Configure the timestamp
     *
     * @param timestamp the timestamp
     * @return {@code this}
     */
    @SuppressWarnings("unchecked")
    public final T withTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return (T) this;
    }

    /**
     * Returns the type
     *
     * @return the type
     */
    @JsonProperty("type")
    public final String getType() {
        return this.type;
    }

    /**
     * Configure the type
     *
     * @param type the type
     * @return {@code this}
     */
    @SuppressWarnings("unchecked")
    public final T  withType(String type) {
        this.type = type;
        return (T) this;
    }
}
