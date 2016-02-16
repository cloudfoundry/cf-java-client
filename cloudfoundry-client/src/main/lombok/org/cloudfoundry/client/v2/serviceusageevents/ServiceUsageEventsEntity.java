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

package org.cloudfoundry.client.v2.serviceusageevents;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * The entity response payload for Service Usage Events
 */
@Data
public class ServiceUsageEventsEntity {

    /**
     * The organization id
     *
     * @param organizationId the organization id
     * @return the organization id
     */
    private final String organizationId;

    /**
     * The service id
     *
     * @param serviceId the service id
     * @return the service id
     */
    private final String serviceId;

    /**
     * The service instance id
     *
     * @param serviceInstanceId the service instance id
     * @return the service instance id
     */
    private final String serviceInstanceId;

    /**
     * The service instance name
     *
     * @param serviceInstanceName the service instance name
     * @return the service instance name
     */
    private final String serviceInstanceName;

    /**
     * The service instance type
     *
     * @param serviceInstanceType the service instance type
     * @return the service instance type
     */
    private final String serviceInstanceType;

    /**
     * The service label
     *
     * @param serviceLabel the service label
     * @return the service label
     */
    private final String serviceLabel;

    /**
     * The service plan id
     *
     * @param servicePlanId the service plan id
     * @return the service plan id
     */
    private final String servicePlanId;

    /**
     * The service plan name
     *
     * @param servicePlanName the service plan name
     * @return the service plan name
     */
    private final String servicePlanName;

    /**
     * The space id
     *
     * @param spaceId the space id
     * @return the space id
     */
    private final String spaceId;

    /**
     * The space name
     *
     * @param spaceName the space name
     * @return the space name
     */
    private final String spaceName;

    /**
     * The state
     *
     * @param state the state
     * @return the state
     */
    private final String state;

    @Builder
    ServiceUsageEventsEntity(@JsonProperty("org_guid") String organizationId,
                             @JsonProperty("service_guid") String serviceId,
                             @JsonProperty("service_instance_guid") String serviceInstanceId,
                             @JsonProperty("service_instance_name") String serviceInstanceName,
                             @JsonProperty("service_instance_type") String serviceInstanceType,
                             @JsonProperty("service_label") String serviceLabel,
                             @JsonProperty("service_plan_guid") String servicePlanId,
                             @JsonProperty("service_plan_name") String servicePlanName,
                             @JsonProperty("space_guid") String spaceId,
                             @JsonProperty("space_name") String spaceName,
                             @JsonProperty("state") String state) {
        this.organizationId = organizationId;
        this.serviceId = serviceId;
        this.serviceInstanceId = serviceInstanceId;
        this.serviceInstanceName = serviceInstanceName;
        this.serviceInstanceType = serviceInstanceType;
        this.serviceLabel = serviceLabel;
        this.servicePlanId = servicePlanId;
        this.servicePlanName = servicePlanName;
        this.spaceId = spaceId;
        this.spaceName = spaceName;
        this.state = state;
    }

}