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

package org.cloudfoundry.client.v3.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.LinkBased;
import org.cloudfoundry.client.v3.PaginatedResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * The response payload for the List Applications operation
 *
 * <p><b>This class is NOT threadsafe.</b>
 */
public final class ListApplicationsResponse
        extends PaginatedResponse<ListApplicationsResponse, ListApplicationsResponse.Resource> {

    /**
     * The Resource response payload for the List Applications operation
     *
     * <p><b>This class is NOT threadsafe.</b>
     */
    public static final class Resource implements LinkBased {

        private volatile String buildpack;

        private volatile String createdAt;

        private volatile String desiredState;

        private final Map<String, String> environmentVariables = new HashMap<>();

        private volatile String id;

        private final Map<String, Link> links = new HashMap<>();

        private volatile String name;

        private volatile Integer totalDesiredInstances;

        private volatile String updatedAt;

        /**
         * Returns the buildpack
         *
         * @return the buildpack
         */
        public String getBuildpack() {
            return this.buildpack;
        }

        /**
         * Configure the buildpack
         *
         * @param buildpack the buildpack
         * @return {@code this}
         */
        @JsonProperty("buildpack")
        public Resource withBuildpack(String buildpack) {
            this.buildpack = buildpack;
            return this;
        }

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
         * Returns the desired state
         *
         * @return the desired state
         */
        public String getDesiredState() {
            return this.desiredState;
        }

        /**
         * Returns the desired state
         *
         * @param desiredState the desired state
         * @return {@code this}
         */
        @JsonProperty("desired_state")
        public Resource withDesiredState(String desiredState) {
            this.desiredState = desiredState;
            return this;
        }

        /**
         * Returns the environment variables
         *
         * @return the environment variables
         */
        public Map<String, String> getEnvironmentVariables() {
            return this.environmentVariables;
        }

        /**
         * Add an environment variable
         *
         * @param key   the environment variable key
         * @param value the environment variable value
         * @return {@code this}
         */
        public Resource withEnvironmentVariable(String key, String value) {
            this.environmentVariables.put(key, value);
            return this;
        }

        /**
         * Add environment variables
         *
         * @param environmentVariables the environment variables
         * @return {@code this}
         */
        @JsonProperty("environment_variables")
        public Resource withEnvironmentVariables(Map<String, String> environmentVariables) {
            this.environmentVariables.putAll(environmentVariables);
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
        public final Resource withId(String id) {
            this.id = id;
            return this;
        }

        @Override
        public Link getLink(String rel) {
            return this.links.get(rel);
        }


        @Override
        public Map<String, Link> getLinks() {
            return this.links;
        }

        /**
         * Add a link
         *
         * @param rel  the rel
         * @param link the link
         * @return {@code this}
         */
        public Resource withLink(String rel, Link link) {
            this.links.put(rel, link);
            return this;
        }

        /**
         * Add links
         *
         * @param links the links
         * @return {@code this}
         */
        @JsonProperty("_links")
        public Resource withLinks(Map<String, Link> links) {
            this.links.putAll(links);
            return this;
        }

        /**
         * Returns the name
         *
         * @return the name
         */
        public String getName() {
            return this.name;
        }

        /**
         * Configure the name
         *
         * @param name the name
         * @return {@code this}
         */
        @JsonProperty("name")
        public Resource withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Returns the total desired instances
         *
         * @return the total desired instances
         */
        public Integer getTotalDesiredInstances() {
            return this.totalDesiredInstances;
        }

        /**
         * Configure the total desired instances
         *
         * @param totalDesiredInstances the total desired instances
         * @return {@code this}
         */
        @JsonProperty("total_desired_instances")
        public Resource withTotalDesiredInstances(Integer totalDesiredInstances) {
            this.totalDesiredInstances = totalDesiredInstances;
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

    }

}
