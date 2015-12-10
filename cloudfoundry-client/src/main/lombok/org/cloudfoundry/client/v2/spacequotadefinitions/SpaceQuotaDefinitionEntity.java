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

package org.cloudfoundry.client.v2.spacequotadefinitions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * The entity response payload for the User resource
 */
@Data
public final class SpaceQuotaDefinitionEntity {

    /**
     * The instance memory limit
     *
     * @param instanceMemoryLimit the admin boolean
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
    private final boolean nonBasicServicesAllowed;

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
     * The spaces url
     *
     * @param spacesUrl the spaces url
     * @return the spaces url
     */
    private final String spacesUrl;

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

    @Builder
    SpaceQuotaDefinitionEntity(@JsonProperty("instance_memory_limit") Integer instanceMemoryLimit,
                               @JsonProperty("memory_limit") Integer memoryLimit,
                               @JsonProperty("name") String name,
                               @JsonProperty("non_basic_services_allowed") boolean nonBasicServicesAllowed,
                               @JsonProperty("organization_guid") String organizationId,
                               @JsonProperty("organization_url") String organizationUrl,
                               @JsonProperty("spaces_url") String spacesUrl,
                               @JsonProperty("total_routes") Integer totalRoutes,
                               @JsonProperty("total_services") Integer totalServices) {
        this.instanceMemoryLimit = instanceMemoryLimit;
        this.memoryLimit = memoryLimit;
        this.name = name;
        this.nonBasicServicesAllowed = nonBasicServicesAllowed;
        this.organizationId = organizationId;
        this.organizationUrl = organizationUrl;
        this.spacesUrl = spacesUrl;
        this.totalRoutes = totalRoutes;
        this.totalServices = totalServices;
    }

}
