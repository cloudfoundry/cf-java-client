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

package org.cloudfoundry.client.v2.quotadefinitions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * The entity response payload for the Quota Definition resource
 */
@Data
public final class OrganizationQuotaDefinitionEntity {

    /**
     * The application instance limit
     *
     * @param applicationInstanceLimit the application instance limit
     * @return applicationInstanceLimit
     */
    private final Integer applicationInstanceLimit;

    /**
     * The instance memory limit
     *
     * @param instanceMemoryLimit the instance memory limit
     * @return instanceMemoryLimit
     */
    private final Integer instanceMemoryLimit;

    /**
     * The memory limit
     *
     * @param memoryLimit the memory limit
     * @return memory limit
     */
    private final Integer memoryLimit;

    /**
     * The name
     *
     * @param name the name
     * @return name
     */
    private final String name;

    /**
     * The non basic services allowed
     *
     * @param nonBasicServicesAllowed the non basic services allowed boolean
     * @return the nonBasicServicesAllowed
     */
    private final Boolean nonBasicServicesAllowed;

    /**
     * The total private domains
     *
     * @param totalPrivateDomains the total private domains
     * @return the total private domains
     */
    private final Integer totalPrivateDomains;

    /**
     * The total routes
     *
     * @param totalRoutes the total routes
     * @return the total routes
     */
    private final Integer totalRoutes;

    /**
     * The total services
     *
     * @param totalServices the total services
     * @return the total services
     */
    private final Integer totalServices;

    /**
     * The trial db allowed
     *
     * @param trialDbAllowed the trial db allowed
     * @return the trial db allowed
     */
    private final Boolean trialDbAllowed;

    @Builder
    OrganizationQuotaDefinitionEntity(@JsonProperty("app_instance_limit") Integer applicationInstanceLimit,
                                      @JsonProperty("instance_memory_limit") Integer instanceMemoryLimit,
                                      @JsonProperty("memory_limit") Integer memoryLimit,
                                      @JsonProperty("name") String name,
                                      @JsonProperty("non_basic_services_allowed") Boolean nonBasicServicesAllowed,
                                      @JsonProperty("total_private_domains") Integer totalPrivateDomains,
                                      @JsonProperty("trial_db_allowed") Boolean trialDbAllowed,
                                      @JsonProperty("total_routes") Integer totalRoutes,
                                      @JsonProperty("total_services") Integer totalServices) {
        this.applicationInstanceLimit = applicationInstanceLimit;
        this.instanceMemoryLimit = instanceMemoryLimit;
        this.memoryLimit = memoryLimit;
        this.name = name;
        this.nonBasicServicesAllowed = nonBasicServicesAllowed;
        this.totalPrivateDomains = totalPrivateDomains;
        this.trialDbAllowed = trialDbAllowed;
        this.totalRoutes = totalRoutes;
        this.totalServices = totalServices;
    }

}
