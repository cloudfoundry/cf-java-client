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

package org.cloudfoundry.client.v2.organizations;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;
import org.cloudfoundry.client.v2.FilterParameter;
import org.cloudfoundry.client.v2.PaginatedRequest;

import java.util.List;

/**
 * The request payload for the List all Billing Managers for the Organization operation
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class ListOrganizationBillingManagersRequest extends PaginatedRequest implements Validatable {

    /**
     * The audited organization ids
     *
     * @param auditedOrganizationIds the audited organization ids
     * @return the audited organization ids
     */
    @Getter(onMethod = @__(@FilterParameter("audited_organization_guid")))
    private final List<String> auditedOrganizationIds;

    /**
     * The audited space ids
     *
     * @param auditedSpaceIds the audited space ids
     * @return the audited space ids
     */
    @Getter(onMethod = @__(@FilterParameter("audited_space_guid")))
    private final List<String> auditedSpaceIds;

    /**
     * The billing managed organization ids
     *
     * @param billingManagedOrganizationIds the billing managed organization ids
     * @return the billing managed organization ids
     */
    @Getter(onMethod = @__(@FilterParameter("billing_managed_organization_guid")))
    private final List<String> billingManagedOrganizationIds;

    /**
     * The id
     *
     * @param id the id
     * @return the id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String id;

    /**
     * The managed organization ids
     *
     * @param managedOrganizationIds the managed organization ids
     * @return the managed organization ids
     */
    @Getter(onMethod = @__(@FilterParameter("managed_organization_guid")))
    private final List<String> managedOrganizationIds;

    /**
     * The managed space ids
     *
     * @param managedSpaceIds the managed space ids
     * @return the managed space ids
     */
    @Getter(onMethod = @__(@FilterParameter("managed_space_guid")))
    private final List<String> managedSpaceIds;

    /**
     * The organization ids
     *
     * @param organizationIds the organization ids
     * @return the organization ids
     */
    @Getter(onMethod = @__(@FilterParameter("organization_guid")))
    private final List<String> organizationIds;

    /**
     * The space ids
     *
     * @param spaceIds the space ids
     * @return the space ids
     */
    @Getter(onMethod = @__(@FilterParameter("space_guid")))
    private final List<String> spaceIds;

    @Builder
    ListOrganizationBillingManagersRequest(OrderDirection orderDirection, Integer page, Integer resultsPerPage,
                                           @Singular List<String> auditedOrganizationIds,
                                           @Singular List<String> auditedSpaceIds,
                                           @Singular List<String> billingManagedOrganizationIds,
                                           String id,
                                           @Singular List<String> managedOrganizationIds,
                                           @Singular List<String> managedSpaceIds,
                                           @Singular List<String> organizationIds,
                                           @Singular List<String> spaceIds) {
        super(orderDirection, page, resultsPerPage);

        this.auditedOrganizationIds = auditedOrganizationIds;
        this.auditedSpaceIds = auditedSpaceIds;
        this.billingManagedOrganizationIds = billingManagedOrganizationIds;
        this.id = id;
        this.managedOrganizationIds = managedOrganizationIds;
        this.managedSpaceIds = managedSpaceIds;
        this.organizationIds = organizationIds;
        this.spaceIds = spaceIds;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.id == null) {
            builder.message("id must be specified");
        }

        return builder.build();
    }

}
