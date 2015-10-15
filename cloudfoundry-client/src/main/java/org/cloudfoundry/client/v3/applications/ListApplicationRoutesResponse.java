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

package org.cloudfoundry.client.v3.applications;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.LinkBased;
import org.cloudfoundry.client.v3.PaginatedResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * The response payload for the List Application Routes operation
 *
 * <p><b>This class is NOT threadsafe.</b>
 */
public final class ListApplicationRoutesResponse extends PaginatedResponse<ListApplicationRoutesResponse,
        ListApplicationRoutesResponse.Resource> {

    /**
     * The Resource response payload for the List Application Routes operation
     *
     * <p><b>This class is NOT threadsafe.</b>
     */
    public static final class Resource<T extends Resource<T>> implements LinkBased {

        private volatile String createdAt;

        private volatile String host;

        private volatile String id;

        private final Map<String, Link> links = new HashMap<>();

        private volatile String path;

        private volatile String updatedAt;

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
        public Resource withCreatedAt(String createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        /**
         * Returns the host
         *
         * @return the host
         */
        public String getHost() {
            return this.host;
        }

        /**
         * Configure the host
         *
         * @param host the host
         * @return {@code this}
         */
        @JsonProperty("host")
        public Resource withHost(String host) {
            this.host = host;
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
        public Resource withId(String id) {
            this.id = id;
            return this;
        }

        /**
         * Returns the path
         *
         * @return the path
         */
        public String getPath() {
            return this.path;
        }

        /**
         * Configure the path
         *
         * @param path the path
         * @return {@code this}
         */
        @JsonProperty("path")
        public Resource withPath(String path) {
            this.path = path;
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
        public Resource withUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        /**
         * Add a link
         *
         * @param rel  the rel
         * @param link the link
         * @return {@code this}
         */
        @SuppressWarnings("unchecked")
        public final T withLink(String rel, Link link) {
            this.links.put(rel, link);
            return (T) this;
        }

        /**
         * Add links
         *
         * @param links the links
         * @return {@code this}
         */
        @JsonProperty("_links")
        @SuppressWarnings("unchecked")
        public final T withLinks(Map<String, Link> links) {
            this.links.putAll(links);
            return (T) this;
        }

        @Override
        public Link getLink(String rel) {
            return this.links.get(rel);
        }

        @Override
        public Map<String, Link> getLinks() {
            return this.links;
        }
    }

}
