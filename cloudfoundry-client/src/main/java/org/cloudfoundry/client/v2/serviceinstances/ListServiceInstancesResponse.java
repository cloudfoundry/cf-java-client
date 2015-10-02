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

package org.cloudfoundry.client.v2.serviceinstances;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.client.v2.PaginatedResponse;
import org.cloudfoundry.client.v2.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The response payload for the List Service Instances operation
 *
 * <p><b>This class is NOT threadsafe.</b>
 */
public final class ListServiceInstancesResponse extends PaginatedResponse<ListServiceInstancesResponse,
        ListServiceInstancesResponse.ListServiceInstancesResponseResource> {

    /**
     * The resource response payload for the List Service Instances operation
     *
     * <p><b>This class is NOT threadsafe.</b>
     */
    public static final class ListServiceInstancesResponseResource
            extends Resource<ListServiceInstancesResponseResource, ListServiceInstancesResponseEntity> {
    }

    /**
     * The entity response payload for the List Service Instances operation
     *
     * <p><b>This class is NOT threadsafe.</b>
     */
    public static final class ListServiceInstancesResponseEntity {

        private final Map<String, Object> credentials = new HashMap<>();

        private volatile String dashboardUrl;

        private volatile String gatewayData;

        private volatile LastOperation lastOperation;

        private volatile String name;

        private volatile String routesUrl;

        private volatile String serviceBindingsUrl;

        private volatile String serviceKeysUrl;

        private volatile String servicePlanId;

        private volatile String servicePlanUrl;

        private volatile String spaceId;

        private volatile String spaceUrl;

        private final List<String> tags = new ArrayList<>();

        private volatile String type;

        /**
         * Returns the credentials
         *
         * @return the credentials
         */
        public Map<String, Object> getCredentials() {
            return this.credentials;
        }

        /**
         * Configure the credential
         *
         * @param key   the key
         * @param value the value
         * @return {@code this}
         */
        public ListServiceInstancesResponseEntity withCredential(String key, Object value) {
            this.credentials.put(key, value);
            return this;
        }

        /**
         * Configure the credentials
         *
         * @param credentials the credentials
         * @return {@code this}
         */
        @JsonProperty("credentials")
        public ListServiceInstancesResponseEntity withCredentials(Map<String, Object> credentials) {
            this.credentials.putAll(credentials);
            return this;
        }

        /**
         * Returns the dashboard url
         *
         * @return the dashboard url
         */
        public String getDashboardUrl() {
            return this.dashboardUrl;
        }

        /**
         * Configure the dashboard url
         *
         * @param dashboardUrl the dashboard url
         * @return {@code this}
         */
        @JsonProperty("dashboard_url")
        public ListServiceInstancesResponseEntity withDashboardUrl(String dashboardUrl) {
            this.dashboardUrl = dashboardUrl;
            return this;
        }

        /**
         * Returns the gateway data
         *
         * @return the gateway data
         */
        @Deprecated
        public String getGatewayData() {
            return this.gatewayData;
        }

        /**
         * Configure the gateway data
         *
         * @param gatewayData the gateway data
         * @return {@code this}
         */
        @Deprecated
        @JsonProperty("gateway_data")
        public ListServiceInstancesResponseEntity withGatewayData(String gatewayData) {
            this.gatewayData = gatewayData;
            return this;
        }

        /**
         * Returns the last operation
         *
         * @return the last operation
         */
        public LastOperation getLastOperation() {
            return this.lastOperation;
        }

        /**
         * Configure the last operation
         *
         * @param lastOperation the last operation
         * @return {@code this}
         */
        @JsonProperty("last_operation")
        public ListServiceInstancesResponseEntity withLastOperation(LastOperation lastOperation) {
            this.lastOperation = lastOperation;
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
        public ListServiceInstancesResponseEntity withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Returns the routes url
         *
         * @return the routes url
         */
        public String getRoutesUrl() {
            return this.routesUrl;
        }

        /**
         * Configure the routes url
         *
         * @param routesUrl the routes url
         * @return {@code this}
         */
        @JsonProperty("routes_url")
        public ListServiceInstancesResponseEntity withRoutesUrl(String routesUrl) {
            this.routesUrl = routesUrl;
            return this;
        }

        /**
         * Returns the service bindings url
         *
         * @return the service bindings url
         */
        public String getServiceBindingsUrl() {
            return this.serviceBindingsUrl;
        }

        /**
         * Configure the service bindings url
         *
         * @param serviceBindingsUrl the service bindings url
         * @return {@code this}
         */
        @JsonProperty("service_bindings_url")
        public ListServiceInstancesResponseEntity withServiceBindingsUrl(String serviceBindingsUrl) {
            this.serviceBindingsUrl = serviceBindingsUrl;
            return this;
        }

        /**
         * Returns the service keys url
         *
         * @return the service keys url
         */
        public String getServiceKeysUrl() {
            return this.serviceKeysUrl;
        }

        /**
         * Configure the service keys url
         *
         * @param serviceKeysUrl the service keys url
         * @return {@code this}
         */
        @JsonProperty("service_keys_url")
        public ListServiceInstancesResponseEntity withServiceKeysUrl(String serviceKeysUrl) {
            this.serviceKeysUrl = serviceKeysUrl;
            return this;
        }

        /**
         * Returns the service plan id
         *
         * @return the service plan id
         */
        public String getServicePlanId() {
            return this.servicePlanId;
        }

        /**
         * Configure the service plan id
         *
         * @param servicePlanId the service plan id
         * @return {@code this}
         */
        @JsonProperty("service_plan_guid")
        public ListServiceInstancesResponseEntity withServicePlanId(String servicePlanId) {
            this.servicePlanId = servicePlanId;
            return this;
        }

        /**
         * Returns the service plan url
         *
         * @return the service plan url
         */
        public String getServicePlanUrl() {
            return this.servicePlanUrl;
        }

        /**
         * Configure the service plan url
         *
         * @param servicePlanUrl the service plan url
         * @return {@code this}
         */
        @JsonProperty("service_plan_url")
        public ListServiceInstancesResponseEntity withServicePlanUrl(String servicePlanUrl) {
            this.servicePlanUrl = servicePlanUrl;
            return this;
        }

        /**
         * Returns the space id
         *
         * @return the space id
         */
        public String getSpaceId() {
            return this.spaceId;
        }

        /**
         * Configure the space id
         *
         * @param spaceId the space id
         * @return {@code this}
         */
        @JsonProperty("space_guid")
        public ListServiceInstancesResponseEntity withSpaceId(String spaceId) {
            this.spaceId = spaceId;
            return this;
        }

        /**
         * Returns the space url
         *
         * @return the space url
         */
        public String getSpaceUrl() {
            return this.spaceUrl;
        }

        /**
         * Configure the space url
         *
         * @param spaceUrl the space url
         * @return {@code this}
         */
        @JsonProperty("space_url")
        public ListServiceInstancesResponseEntity withSpaceUrl(String spaceUrl) {
            this.spaceUrl = spaceUrl;
            return this;
        }

        /**
         * Returns the tags
         *
         * @return the tags
         */
        public List<String> getTags() {
            return this.tags;
        }

        /**
         * Configure the tag
         *
         * @param tag the tag
         * @return {@code this}
         */
        public ListServiceInstancesResponseEntity withTag(String tag) {
            this.tags.add(tag);
            return this;
        }

        /**
         * Configure the tags
         *
         * @param tags the tags
         * @return {@code this}
         */
        @JsonProperty("tags")
        public ListServiceInstancesResponseEntity withTags(List<String> tags) {
            this.tags.addAll(tags);
            return this;
        }

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
        @JsonProperty("type")
        public ListServiceInstancesResponseEntity withType(String type) {
            this.type = type;
            return this;
        }

        /**
         * The last operation payload for the List Service Instances operation
         *
         * <p><b>This class is NOT threadsafe.</b>
         */
        public static final class LastOperation {

            private volatile String createdAt;

            private volatile String description;

            private volatile String state;

            private volatile String type;

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
            public LastOperation withCreatedAt(String createdAt) {
                this.createdAt = createdAt;
                return this;
            }

            /**
             * Returns the description
             *
             * @return the description
             */
            public String getDescription() {
                return this.description;
            }

            /**
             * Configure the description
             *
             * @param description the description
             * @return {@code this}
             */
            @JsonProperty("description")
            public LastOperation withDescription(String description) {
                this.description = description;
                return this;
            }

            /**
             * Returns the state
             *
             * @return the state
             */
            public String getState() {
                return this.state;
            }

            /**
             * Configure the state
             *
             * @param state the state
             * @return {@code this}
             */
            @JsonProperty("state")
            public LastOperation withState(String state) {
                this.state = state;
                return this;
            }

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
            @JsonProperty("type")
            public LastOperation withType(String type) {
                this.type = type;
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
            public LastOperation withUpdatedAt(String updatedAt) {
                this.updatedAt = updatedAt;
                return this;
            }

        }

    }
}
