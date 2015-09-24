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

package org.cloudfoundry.client.v2;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The resource payload for a paginated response
 *
 * <p><b>This class is NOT threadsafe.</b>
 *
 * @param <V> the "self" type.  Used to ensure the appropriate type is returned from builder APIs.
 * @param <W> the type of entity contained within the resource
 */
public abstract class Resource<V extends Resource<V, W>, W> {

    private volatile W entity;

    private volatile Metadata metadata;

    /**
     * Returns the entity
     *
     * @return the entity
     */
    public final W getEntity() {
        return this.entity;
    }

    /**
     * Configure the entity
     *
     * @param entity the entity
     * @return {@code this}
     */
    @JsonProperty("entity")
    @SuppressWarnings("unchecked")
    public final V withEntity(W entity) {
        this.entity = entity;
        return (V) this;
    }

    /**
     * Returns the metadata
     *
     * @return the metadata
     */
    public final Metadata getMetadata() {
        return this.metadata;
    }

    /**
     * Configure the metadata
     *
     * @param metadata the metadata
     * @return {@code this}
     */
    @JsonProperty("metadata")
    @SuppressWarnings("unchecked")
    public final V withMetadata(Metadata metadata) {
        this.metadata = metadata;
        return (V) this;
    }

    /**
     * The metadata payload for a resource
     *
     * <p><b>This class is NOT threadsafe.</b>
     */
    public static final class Metadata {

        private volatile String createdAt;

        private volatile String id;

        private volatile String updatedAt;

        private volatile String url;

        /**
         * Returns the created at
         *
         * @return the created at
         */
        public String getCreatedAt() {
            return this.createdAt;
        }

        /**
         * Configure the created at
         *
         * @param createdAt the created at
         * @return {@code this}
         */
        @JsonProperty("created_at")
        public Metadata withCreatedAt(String createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        /**
         * Returns the id
         *
         * @return the id
         */
        public String getId() {
            return this.id;
        }

        /**
         * Configure the id
         *
         * @param id the id
         * @return {@code this}
         */
        @JsonProperty("guid")
        public Metadata withId(String id) {
            this.id = id;
            return this;
        }

        /**
         * Returns the updated at
         *
         * @return the updated at
         */
        public String getUpdatedAt() {
            return this.updatedAt;
        }

        /**
         * Configure the updated at
         *
         * @param updatedAt the updated at
         * @return {@code this}
         */
        @JsonProperty("updated_at")
        public Metadata withUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        /**
         * Returns the url
         *
         * @return the url
         */
        public String getUrl() {
            return this.url;
        }

        /**
         * Configure the url
         *
         * @param url the url
         * @return {@code this}
         */
        @JsonProperty("url")
        public Metadata withUrl(String url) {
            this.url = url;
            return this;
        }

    }

}
