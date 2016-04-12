/*
 * Copyright 2013-2016 the original author or authors.
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

import java.util.List;
import java.util.Map;

/**
 * The entity response payload for any type of Service Instances
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class UnionServiceInstanceEntity extends BaseServiceInstanceEntity {

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
     * URL to which requests for bound routes will be forwarded
     *
     * @param routeServiceUrl the route service url
     * @return the route service url
     */
    private final String routeServiceUrl;

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
     * The url for the syslog_drain to direct to
     *
     * @param syslogDrainUrl the syslog drain url
     * @return the syslog drain url
     */
    private final String syslogDrainUrl;

    /**
     * The tags
     *
     * @param tags the tags
     * @return the tags
     */
    private final List<String> tags;

    @Builder
    UnionServiceInstanceEntity(@JsonProperty("credentials") @Singular Map<String, Object> credentials,
                               @JsonProperty("dashboard_url") String dashboardUrl,
                               @JsonProperty("gateway_data") @Deprecated String gatewayData,
                               @JsonProperty("last_operation") LastOperation lastOperation,
                               @JsonProperty("name") String name,
                               @JsonProperty("route_service_url") String routeServiceUrl,
                               @JsonProperty("routes_url") String routesUrl,
                               @JsonProperty("service_bindings_url") String serviceBindingsUrl,
                               @JsonProperty("service_keys_url") String serviceKeysUrl,
                               @JsonProperty("service_plan_guid") String servicePlanId,
                               @JsonProperty("service_plan_url") String servicePlanUrl,
                               @JsonProperty("space_guid") String spaceId,
                               @JsonProperty("space_url") String spaceUrl,
                               @JsonProperty("syslog_drain_url") String syslogDrainUrl,
                               @JsonProperty("tags") @Singular List<String> tags,
                               @JsonProperty("type") String type) {

        super(credentials, name, routesUrl, serviceBindingsUrl, spaceId, spaceUrl, type);

        this.dashboardUrl = dashboardUrl;
        this.gatewayData = gatewayData;
        this.lastOperation = lastOperation;
        this.routeServiceUrl = routeServiceUrl;
        this.serviceKeysUrl = serviceKeysUrl;
        this.servicePlanId = servicePlanId;
        this.servicePlanUrl = servicePlanUrl;
        this.syslogDrainUrl = syslogDrainUrl;
        this.tags = tags;
    }

}
