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
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import org.cloudfoundry.client.v2.PaginatedResponse;
import org.cloudfoundry.client.v2.Resource;

import java.util.List;
import java.util.Map;

/**
 * The response payload for the List Service Instances operation
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class ListServiceInstancesResponse
        extends PaginatedResponse<ListServiceInstancesResponse.ListServiceInstancesResponseResource> {

    @Builder
    ListServiceInstancesResponse(@JsonProperty("next_url") String nextUrl,
                                 @JsonProperty("prev_url") String previousUrl,
                                 @JsonProperty("resources") @Singular List<ListServiceInstancesResponseResource>
                                         resources,
                                 @JsonProperty("total_pages") Integer totalPages,
                                 @JsonProperty("total_results") Integer totalResults) {
        super(nextUrl, previousUrl, resources, totalPages, totalResults);
    }

    /**
     * The resource response payload for the List Service Instances operation
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static final class ListServiceInstancesResponseResource
            extends Resource<ListServiceInstancesResponseEntity> {

        @Builder
        ListServiceInstancesResponseResource(@JsonProperty("entity") ListServiceInstancesResponseEntity entity,
                                             @JsonProperty("metadata") Metadata metadata) {
            super(entity, metadata);
        }
    }

    /**
     * The entity response payload for the List Service Instances operation
     */
    @Data
    public static final class ListServiceInstancesResponseEntity {

        /**
         * The credentials
         *
         * @param credentials the credentials
         * @return the credentials
         */
        private final Map<String, Object> credentials;

        /**
         * The dashboard url
         *
         * @param dashboardUrl the dashboard url
         * @return the dashboard url
         */
        private final String dashboardUrl;

        /**
         * The gateway data
         *
         * @param gatewayData the gateway data
         * @return the gateway data
         */
        @Getter(onMethod = @__(@Deprecated))
        private final String gatewayData;

        /**
         * The last operation
         *
         * @param lastOperation the last operation
         * @return the last operation
         */
        private final LastOperation lastOperation;

        /**
         * The name
         *
         * @param name the name
         * @return the name
         */
        private final String name;

        /**
         * The routes url
         *
         * @param routesUrl the routes url
         * @return the routes url
         */
        private final String routesUrl;

        /**
         * The service bindings url
         *
         * @param serviceBindingsUrl the service bindings url
         * @return the service bindings url
         */
        private final String serviceBindingsUrl;

        /**
         * The service keys url
         *
         * @param serviceKeysUrl the service keys url
         * @return the service keys url
         */
        private final String serviceKeysUrl;

        /**
         * The service plan id
         *
         * @param servicePlanId the service plan id
         * @return the service plan id
         */
        private final String servicePlanId;

        /**
         * The service plan url
         *
         * @param servicePlanUrl the service plan url
         * @return the service plan url
         */
        private final String servicePlanUrl;

        /**
         * The space id
         *
         * @param spaceId the space id
         * @return the space id
         */
        private final String spaceId;

        /**
         * The space url
         *
         * @param spaceUrl the space url
         * @return the space url
         */
        private final String spaceUrl;

        /**
         * The tags
         *
         * @param tags the tags
         * @return the tags
         */
        private final List<String> tags;

        /**
         * The type
         *
         * @param type the type
         * @return the type
         */
        private final String type;

        @Builder
        ListServiceInstancesResponseEntity(@JsonProperty("credentials") @Singular Map<String, Object> credentials,
                                           @JsonProperty("dashboard_url") String dashboardUrl,
                                           @JsonProperty("gateway_data") @Deprecated String gatewayData,
                                           @JsonProperty("last_operation") LastOperation lastOperation,
                                           @JsonProperty("name") String name,
                                           @JsonProperty("routes_url") String routesUrl,
                                           @JsonProperty("service_bindings_url") String serviceBindingsUrl,
                                           @JsonProperty("service_keys_url") String serviceKeysUrl,
                                           @JsonProperty("service_plan_guid") String servicePlanId,
                                           @JsonProperty("service_plan_url") String servicePlanUrl,
                                           @JsonProperty("space_guid") String spaceId,
                                           @JsonProperty("space_url") String spaceUrl,
                                           @JsonProperty("tags") @Singular List<String> tags,
                                           @JsonProperty("type") String type) {
            this.credentials = credentials;
            this.dashboardUrl = dashboardUrl;
            this.gatewayData = gatewayData;
            this.lastOperation = lastOperation;
            this.name = name;
            this.routesUrl = routesUrl;
            this.serviceBindingsUrl = serviceBindingsUrl;
            this.serviceKeysUrl = serviceKeysUrl;
            this.servicePlanId = servicePlanId;
            this.servicePlanUrl = servicePlanUrl;
            this.spaceId = spaceId;
            this.spaceUrl = spaceUrl;
            this.tags = tags;
            this.type = type;
        }

        /**
         * The last operation payload for the List Service Instances operation
         */
        @Data
        public static final class LastOperation {

            private final String createdAt;

            private final String description;

            private final String state;

            private final String type;

            private final String updatedAt;

            @Builder
            LastOperation(@JsonProperty("created_at") String createdAt,
                          @JsonProperty("description") String description,
                          @JsonProperty("state") String state,
                          @JsonProperty("type") String type,
                          @JsonProperty("updated_at") String updatedAt) {
                this.createdAt = createdAt;
                this.description = description;
                this.state = state;
                this.type = type;
                this.updatedAt = updatedAt;
            }

        }

    }
}
