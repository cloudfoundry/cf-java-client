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

package org.cloudfoundry.client.v2.organizationquotadefinitions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

/**
 * The request payload for the Updating an Organization Quota Definition operation
 */
@Data
public final class UpdateOrganizationQuotaDefinitionRequest implements Validatable {

    /**
     * The application instance limit
     *
     * @param applicationInstanceLimit the application instance limit
     * @return applicationInstanceLimit
     */
    @Getter(onMethod = @__(@JsonProperty("app_instance_limit")))
    private final Integer applicationInstanceLimit;

    /**
     * The application task limit
     *
     * @param appTaskLimit the application task limit
     * @return then application task limit
     */
    @Getter(onMethod = @__(@JsonProperty("app_task_limit")))
    private final Integer applicationTaskLimit;

    /**
     * The instance memory limit
     *
     * @param instanceMemoryLimit the instance memory limit
     * @return instanceMemoryLimit
     */
    @Getter(onMethod = @__(@JsonProperty("instance_memory_limit")))
    private final Integer instanceMemoryLimit;

    /**
     * The memory limit
     *
     * @param memoryLimit the memory limit
     * @return memory limit
     */
    @Getter(onMethod = @__(@JsonProperty("memory_limit")))
    private final Integer memoryLimit;

    /**
     * The name
     *
     * @param name the name
     * @return name
     */
    @Getter(onMethod = @__(@JsonProperty("name")))
    private final String name;

    /**
     * The non basic services allowed
     *
     * @param nonBasicServicesAllowed the non basic services allowed boolean
     * @return the nonBasicServicesAllowed
     */
    @Getter(onMethod = @__(@JsonProperty("non_basic_services_allowed")))
    private final Boolean nonBasicServicesAllowed;

    /**
     * The quota definition id
     *
     * @param organizationQuotaDefinitionId the quota definition id
     * @return the quota definition id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String organizationQuotaDefinitionId;

    /**
     * The total private domains
     *
     * @param totalPrivateDomains the total private domains
     * @return the total private domains
     */
    @Getter(onMethod = @__(@JsonProperty("total_private_domains")))
    private final Integer totalPrivateDomains;

    /**
     * The total routes
     *
     * @param totalRoutes the total routes
     * @return the total routes
     */
    @Getter(onMethod = @__(@JsonProperty("total_routes")))
    private final Integer totalRoutes;

    /**
     * The total service keys
     *
     * @param totalServiceKeys the total service keys
     * @return the total service keys
     */
    @Getter(onMethod = @__(@JsonProperty("total_service_keys")))
    private final Integer totalServiceKeys;

    /**
     * The total services
     *
     * @param totalServices the total services
     * @return the total services
     */
    @Getter(onMethod = @__(@JsonProperty("total_services")))
    private final Integer totalServices;

    /**
     * The trial db allowed
     *
     * @param trialDatabaseAllowed the trial db allowed
     * @return the trial db allowed
     */
    @Getter(onMethod = @__({@JsonProperty("trial_db_allowed"), @Deprecated}))
    private final Boolean trialDatabaseAllowed;

    @Builder
    UpdateOrganizationQuotaDefinitionRequest(Integer applicationInstanceLimit,
                                             Integer applicationTaskLimit,
                                             Integer instanceMemoryLimit,
                                             Integer memoryLimit,
                                             String name,
                                             Boolean nonBasicServicesAllowed,
                                             String organizationQuotaDefinitionId,
                                             Integer totalPrivateDomains,
                                             Integer totalRoutes,
                                             Integer totalServiceKeys,
                                             Integer totalServices,
                                             Boolean trialDatabaseAllowed) {

        this.applicationInstanceLimit = applicationInstanceLimit;
        this.applicationTaskLimit = applicationTaskLimit;
        this.instanceMemoryLimit = instanceMemoryLimit;
        this.memoryLimit = memoryLimit;
        this.name = name;
        this.nonBasicServicesAllowed = nonBasicServicesAllowed;
        this.organizationQuotaDefinitionId = organizationQuotaDefinitionId;
        this.totalPrivateDomains = totalPrivateDomains;
        this.totalRoutes = totalRoutes;
        this.totalServiceKeys = totalServiceKeys;
        this.totalServices = totalServices;
        this.trialDatabaseAllowed = trialDatabaseAllowed;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.organizationQuotaDefinitionId == null) {
            builder.message("organization quota definition id must be specified");
        }

        return builder.build();
    }

}
