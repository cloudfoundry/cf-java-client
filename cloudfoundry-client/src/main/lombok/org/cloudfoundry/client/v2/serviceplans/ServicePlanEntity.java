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

package org.cloudfoundry.client.v2.serviceplans;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * The entity response payload for Service Plans
 */
@Data
public final class ServicePlanEntity {

    /**
     * The active flag
     *
     * @param active the active flag
     * @return the active flag
     */
    private final Boolean active;

    /**
     * The description
     *
     * @param description the description
     * @return the description
     */
    private final String description;

    /**
     * The extra (A JSON string with additional data about the plan)
     *
     * @param extra the extra
     * @return the extra
     */
    private final String extra;

    /**
     * The free flag
     *
     * @param free the free flag
     * @return the free flag
     */
    private final Boolean free;

    /**
     * The name
     *
     * @param name the name
     * @return the name
     */
    private final String name;

    /**
     * The public flag
     *
     * @param publiclyVisible the public flag
     * @return the public flag
     */
    private final Boolean publiclyVisible;

    /**
     * The service id
     *
     * @param serviceId the service id
     * @return the service id
     */
    private final String serviceId;

    /**
     * The service instances url
     *
     * @param serviceInstancesUrl the service instances url
     * @return the service instances url
     */
    private final String serviceInstancesUrl;

    /**
     * The service url
     *
     * @param serviceUrl the service url
     * @return the service url
     */
    private final String serviceUrl;

    /**
     * The unique id in the service broker
     *
     * @param uniqueId the unique id
     * @return the unique id
     */
    private final String uniqueId;

    @Builder
    ServicePlanEntity(@JsonProperty("active") Boolean active,
                      @JsonProperty("description") String description,
                      @JsonProperty("extra") String extra,
                      @JsonProperty("free") Boolean free,
                      @JsonProperty("name") String name,
                      @JsonProperty("public") Boolean publiclyVisible,
                      @JsonProperty("service_guid") String serviceId,
                      @JsonProperty("service_instances_url") String serviceInstancesUrl,
                      @JsonProperty("service_url") String serviceUrl,
                      @JsonProperty("unique_id") String uniqueId) {
        this.active = active;
        this.description = description;
        this.extra = extra;
        this.free = free;
        this.name = name;
        this.publiclyVisible = publiclyVisible;
        this.serviceId = serviceId;
        this.serviceInstancesUrl = serviceInstancesUrl;
        this.serviceUrl = serviceUrl;
        this.uniqueId = uniqueId;
    }

}
