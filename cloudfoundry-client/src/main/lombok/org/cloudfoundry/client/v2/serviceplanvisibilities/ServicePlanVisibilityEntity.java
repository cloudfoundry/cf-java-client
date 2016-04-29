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

package org.cloudfoundry.client.v2.serviceplanvisibilities;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * The entity response payload for Service Plan Visibility
 */
@Data
public final class ServicePlanVisibilityEntity {

    /**
     * The organization id
     *
     * @param organizationId the organization id
     * @return the organization id
     */
    private final String organizationId;

    /**
     * The organization url
     *
     * @param organizationUrl the organization url
     * @return the organization url
     */
    private final String organizationUrl;

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

    @Builder
    ServicePlanVisibilityEntity(@JsonProperty("organization_guid") String organizationId,
                                @JsonProperty("organization_url") String organizationUrl,
                                @JsonProperty("service_plan_guid") String servicePlanId,
                                @JsonProperty("service_plan_url") String servicePlanUrl) {
        this.organizationId = organizationId;
        this.organizationUrl = organizationUrl;
        this.servicePlanId = servicePlanId;
        this.servicePlanUrl = servicePlanUrl;
    }

}
