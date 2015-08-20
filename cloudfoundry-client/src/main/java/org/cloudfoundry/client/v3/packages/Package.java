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

package org.cloudfoundry.client.v3.packages;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.LinkBased;

import java.util.HashMap;
import java.util.Map;

abstract class Package<T extends Package<T>> implements LinkBased {

    private volatile String createdAt;

    private volatile String error;

    private volatile Hash hash;

    private volatile String id;

    private final Map<String, Link> links = new HashMap<>();

    private volatile String state;

    private volatile String type;

    private volatile String updatedAt;

    private volatile String url;

    /**
     * Returns the created at
     *
     * @return the created at
     */
    public final String getCreatedAt() {
        return this.createdAt;
    }

    /**
     * Configure the created at
     *
     * @param createdAt the created at
     * @return {@code this}
     */
    @JsonProperty("created_at")
    @SuppressWarnings("unchecked")
    public final T withCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        return (T) this;
    }

    /**
     * Returns the error
     *
     * @return the error
     */
    public final String getError() {
        return this.error;
    }

    /**
     * Configure the error
     *
     * @param error the error
     * @return {@code this}
     */
    @JsonProperty("error")
    @SuppressWarnings("unchecked")
    public final T withError(String error) {
        this.error = error;
        return (T) this;
    }

    /**
     * Returns the hash
     *
     * @return the hash
     */
    public final Hash getHash() {
        return this.hash;
    }

    /**
     * Configure the hash
     *
     * @param hash the hash
     * @return {@code this}
     */
    @JsonProperty("hash")
    @SuppressWarnings("unchecked")
    public final T withHash(Hash hash) {
        this.hash = hash;
        return (T) this;
    }

    /**
     * Returns the id
     *
     * @return the id
     */
    public final String getId() {
        return this.id;
    }

    /**
     * Configure the id
     *
     * @param id the id
     * @return {@code this}
     */
    @JsonProperty("guid")
    @SuppressWarnings("unchecked")
    public final T withId(String id) {
        this.id = id;
        return (T) this;
    }

    @Override
    public final Link getLink(String rel) {
        return this.links.get(rel);
    }

    /**
     * Returns the links
     *
     * @return the links
     */
    @Override
    public final Map<String, Link> getLinks() {
        return this.links;
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

    /**
     * Returns the state
     *
     * @return the state
     */
    public final String getState() {
        return this.state;
    }

    /**
     * Configure the state
     *
     * @param state the state
     * @return {@code this}
     */
    @JsonProperty("state")
    @SuppressWarnings("unchecked")
    public final T withState(String state) {
        this.state = state;
        return (T) this;
    }

    /**
     * Returns the type
     *
     * @return the type
     */
    public final String getType() {
        return this.type;
    }

    @JsonProperty("type")
    @SuppressWarnings("unchecked")
    public final T withType(String type) {
        this.type = type;
        return (T) this;
    }

    /**
     * Returns the updated at
     *
     * @return the updated at
     */
    public final String getUpdatedAt() {
        return this.updatedAt;
    }

    /**
     * Configure the updated at
     *
     * @param updatedAt the updated at
     * @return {@code this}
     */
    @JsonProperty("updated_at")
    @SuppressWarnings("unchecked")
    public final T withUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
        return (T) this;
    }

    /**
     * Returns the url
     *
     * @return the url
     */
    public final String getUrl() {
        return this.url;
    }

    /**
     * Configure the url
     *
     * @param url the url
     * @return {@code this}
     */
    @JsonProperty("url")
    @SuppressWarnings("unchecked")
    public final T withUrl(String url) {
        this.url = url;
        return (T) this;
    }

    public static final class Hash {

        private volatile String type;

        private volatile String value;

        /**
         * Returns the type
         *
         * @return the type
         */
        public String getType() {
            return this.type;
        }

        /**
         * Configure the type
         *
         * @param type the type
         * @return {@code this}
         */
        public Hash withType(String type) {
            this.type = type;
            return this;
        }

        /**
         * Returns the value
         *
         * @return the value
         */
        public String getValue() {
            return this.value;
        }

        /**
         * Configure the value
         *
         * @param value the value
         * @return {@code this}
         */
        public Hash withValue(String value) {
            this.value = value;
            return this;
        }

    }
}
