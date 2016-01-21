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
     * The visible flag
     *
     * @param public the visible flag
     * @return the visible flag
     */
    private final boolean active;

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
    private final boolean free;

    /**
     * The name
     *
     * @param name the name
     * @return the name
     */
    private final String name;

    /**
     * The service id
     *
     * @param serviceId the service id
     * @return the service id
     */
    private final String serviceId;

    /**
     * The service instance url
     *
     * @param serviceUrl the service instance url
     * @return the service instance url
     */
    private final String serviceInstanceUrl;

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
     * @param servicePlanUrl the service plan url
     * @return the service plan url
     */
    private final String uniqueId;

    /**
     * The visible flag
     *
     * @param public the visible flag
     * @return the visible flag
     */
    private final boolean visible;

    @Builder
    ServicePlanEntity(@JsonProperty("active") boolean active,
                      @JsonProperty("description") String description,
                      @JsonProperty("extra") String extra,
                      @JsonProperty("free") boolean free,
                      @JsonProperty("name") String name,
                      @JsonProperty("service_guid") String serviceId,
                      @JsonProperty("service_instances_url") String serviceInstanceUrl,
                      @JsonProperty("service_url") String serviceUrl,
                      @JsonProperty("unique_id") String uniqueId,
                      @JsonProperty("public") boolean visible) {
        this.active = active;
        this.description = description;
        this.extra = extra;
        this.free = free;
        this.name = name;
        this.serviceId = serviceId;
        this.serviceInstanceUrl = serviceInstanceUrl;
        this.serviceUrl = serviceUrl;
        this.uniqueId = uniqueId;
        this.visible = visible;
    }

}
