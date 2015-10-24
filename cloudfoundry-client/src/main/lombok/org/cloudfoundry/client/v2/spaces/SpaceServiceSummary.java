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
import lombok.Builder;
import lombok.Data;

/**
 * A service summary for the Get Space summary response payload
 */
@Data
public final class SpaceServiceSummary {

    /**
     * The bound application count
     *
     * @param boundApplicationCount the bound application count
     * @return the bound application count
     */
    private final Integer boundApplicationCount;

    /**
     * The dashboard url
     *
     * @param dashboardUrl the dashboard url
     * @return the dashboard url
     */
    private final String dashboardUrl;

    /**
     * The id
     *
     * @param id the id
     * @return the id
     */
    private final String id;

    /**
     * The last operation
     *
     * @param lastOperation the last operation
     * @return the last operation
     */
    private final String lastOperation;

    /**
     * The name
     *
     * @param name the name
     * @return the name
     */
    private final String name;

    /**
     * The service plan
     *
     * @param servicePlan the service plan
     * @return the service plan
     */
    private final Plan servicePlan;

    @Builder
    SpaceServiceSummary(@JsonProperty("bound_app_count") Integer boundApplicationCount,
                        @JsonProperty("dashboard_url") String dashboardUrl,
                        @JsonProperty("guid") String id,
                        @JsonProperty("last_operation") String lastOperation,
                        @JsonProperty("name") String name,
                        @JsonProperty("service_plan") Plan servicePlan) {
        this.boundApplicationCount = boundApplicationCount;
        this.dashboardUrl = dashboardUrl;
        this.id = id;
        this.lastOperation = lastOperation;
        this.name = name;
        this.servicePlan = servicePlan;
    }

    @Data
    public final static class Plan {

        /**
         * The id
         *
         * @param id the id
         * @return the id
         */
        private final String id;

        /**
         * The name
         *
         * @param name the name
         * @return the name
         */
        private final String name;

        /**
         * The service
         *
         * @param service the service
         * @return the service
         */
        private final Plan.Service service;

        @Builder
        Plan(@JsonProperty("guid") String id,
             @JsonProperty("name") String name,
             @JsonProperty("service") Service service) {
            this.id = id;
            this.name = name;
            this.service = service;
        }

        @Data
        public final static class Service {

            /**
             * The id
             *
             * @param id the id
             * @return the id
             */
            private final String id;

            /**
             * The label
             *
             * @param label the label
             * @return the label
             */
            private final String label;

            /**
             * The provider
             *
             * @param provider the provider
             * @return the provider
             */
            private final String provider;

            /**
             * The version
             *
             * @param version the version
             * @return the version
             */
            private final String version;

            @Builder
            Service(@JsonProperty("guid") String id,
                    @JsonProperty("label") String label,
                    @JsonProperty("provider") String provider,
                    @JsonProperty("version") String version) {
                this.id = id;
                this.label = label;
                this.provider = provider;
                this.version = version;
            }

        }

    }

}
