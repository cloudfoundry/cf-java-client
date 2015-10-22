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

package org.cloudfoundry.client.v2.spaces;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A service summary for the Get Space summary response payload
 *
 * <p><b>This class is NOT threadsafe.</b>
 */
public final class SpaceServiceSummary {

    private volatile Integer boundAppCount;

    private volatile String dashboardUrl;

    private volatile String id; //guid

    private volatile String lastOperation;

    private volatile String name;

    private volatile Plan servicePlan;

    /**
     * Returns bound application count
     *
     * @return bound application count
     */
    public final Integer getBoundAppCount() {
        return boundAppCount;
    }

    /**
     * Configure bound application count
     *
     * @param boundAppCount bound application count
     * @return {@code this}
     */
    @JsonProperty("bound_app_count")
    public final SpaceServiceSummary withBoundAppCount(Integer boundAppCount) {
        this.boundAppCount = boundAppCount;
        return this;
    }

    /**
     * Returns dashboard URL
     *
     * @return dashboard URL
     */
    public final String getDashboardUrl() {
        return dashboardUrl;
    }

    /**
     * Configure dashboard URL
     *
     * @param dashboardUrl dashboard URL
     * @return {@code this}
     */
    @JsonProperty("dashboard_url")
    public final SpaceServiceSummary withDashboardUrl(String dashboardUrl) {
        this.dashboardUrl = dashboardUrl;
        return this;
    }

    /**
     * Returns id
     *
     * @return id
     */
    public final String getId() {
        return id;
    }

    /**
     * Configure id
     *
     * @param id id
     * @return {@code this}
     */
    @JsonProperty("guid")
    public final SpaceServiceSummary withId(String id) {
        this.id = id;
        return this;
    }

    /**
     * Returns last operation
     *
     * @return last operation
     */
    public final String getLastOperation() {
        return lastOperation;
    }

    /**
     * Configure last operation
     *
     * @param lastOperation last operation
     * @return {@code this}
     */
    @JsonProperty("last_operation")
    public final SpaceServiceSummary withLastOperation(String lastOperation) {
        this.lastOperation = lastOperation;
        return this;
    }

    /**
     * Returns name
     *
     * @return name
     */
    public final String getName() {
        return name;
    }

    /**
     * Configure name
     *
     * @param name name
     * @return {@code this}
     */
    @JsonProperty("name")
    public final SpaceServiceSummary withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Returns service plan
     *
     * @return service plan
     */
    public final Plan getServicePlan() {
        return servicePlan;
    }

    /**
     * Configure service plan
     *
     * @param servicePlan service plan
     * @return {@code this}
     */
    @JsonProperty("service_plan")
    public final SpaceServiceSummary withServicePlan(Plan servicePlan) {
        this.servicePlan = servicePlan;
        return this;
    }

    public final static class Plan {

        private volatile String id; //guid

        private volatile String name;

        private volatile Service service;

        /**
         * Returns id
         *
         * @return id
         */
        public final String getId() {
            return id;
        }

        /**
         * Configure id
         *
         * @param id id
         * @return {@code this}
         */
        @JsonProperty("guid")
        public final Plan withId(String id) {
            this.id = id;
            return this;
        }

        /**
         * Returns name
         *
         * @return name
         */
        public final String getName() {
            return name;
        }

        /**
         * Configure name
         *
         * @param name name
         * @return {@code this}
         */
        @JsonProperty("name")
        public final Plan withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Returns service
         *
         * @return service
         */
        public final Service getService() {
            return service;
        }

        /**
         * Configure service
         *
         * @param service service
         * @return {@code this}
         */
        @JsonProperty("service")
        public final Plan withService(Service service) {
            this.service = service;
            return this;
        }

        public final static class Service {

            private volatile String id; //guid

            private volatile String label;

            private volatile String provider;

            private volatile String version;

            /**
             * Returns id
             *
             * @return id
             */
            public final String getId() {
                return id;
            }

            /**
             * Configure id
             *
             * @param id id
             * @return {@code this}
             */
            @JsonProperty("guid")
            public final Service withId(String id) {
                this.id = id;
                return this;
            }

            /**
             * Returns label
             *
             * @return label
             */
            public final String getLabel() {
                return label;
            }

            /**
             * Configure label
             *
             * @param label label
             * @return {@code this}
             */
            @JsonProperty("label")
            public final Service withLabel(String label) {
                this.label = label;
                return this;
            }

            /**
             * Returns provider
             *
             * @return provider
             */
            public final String getProvider() {
                return provider;
            }

            /**
             * Configure provider
             *
             * @param provider provider
             * @return {@code this}
             */
            @JsonProperty("provider")
            public final Service withProvider(String provider) {
                this.provider = provider;
                return this;
            }

            /**
             * Returns version
             *
             * @return version
             */
            public final String getVersion() {
                return version;
            }

            /**
             * Configure version
             *
             * @param version version
             * @return {@code this}
             */
            @JsonProperty("version")
            public final Service withVersion(String version) {
                this.version = version;
                return this;
            }
        }
    }
}
