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

package org.cloudfoundry.client.v2.spaces;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;
import org.cloudfoundry.client.v2.InFilterParameter;
import org.cloudfoundry.client.v2.OrderDirection;
import org.cloudfoundry.client.v2.PaginatedRequest;

import java.util.List;

/**
 * The request payload for the List all Auditors for the Space operation
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class ListSpaceAuditorsRequest extends PaginatedRequest implements Validatable {

    /**
     * The audited organization ids
     *
     * @param auditedOrganizationIds the audited organization ids
     * @return the audited organization ids
     */
    @Getter(onMethod = @__(@InFilterParameter("audited_organization_guid")))
    private final List<String> auditedOrganizationIds;

    /**
     * The audited space ids
     *
     * @param auditedSpaceIds the audited space ids
     * @return the audited space ids
     */
    @Getter(onMethod = @__(@InFilterParameter("audited_space_guid")))
    private final List<String> auditedSpaceIds;

    /**
     * The billing managed organization ids
     *
     * @param billingManagedOrganizationIds the billing managed organization ids
     * @return the billing managed organization ids
     */
    @Getter(onMethod = @__(@InFilterParameter("billing_managed_organization_guid")))
    private final List<String> billingManagedOrganizationIds;

    /**
     * The managed organization ids
     *
     * @param managedOrganizationIds the managed organization ids
     * @return the managed organization ids
     */
    @Getter(onMethod = @__(@InFilterParameter("managed_organization_guid")))
    private final List<String> managedOrganizationIds;

    /**
     * The managed space ids
     *
     * @param managedSpaceIds the managed space ids
     * @return the managed space ids
     */
    @Getter(onMethod = @__(@InFilterParameter("managed_space_guid")))
    private final List<String> managedSpaceIds;

    /**
     * The organization ids
     *
     * @param organizationIds the organization ids
     * @return the organization ids
     */
    @Getter(onMethod = @__(@InFilterParameter("organization_guid")))
    private final List<String> organizationIds;

    /**
     * The space id
     *
     * @param spaceId the space id
     * @return the space id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String spaceId;

    @Builder
    ListSpaceAuditorsRequest(OrderDirection orderDirection, Integer page, Integer resultsPerPage,
                             @Singular List<String> auditedOrganizationIds,
                             @Singular List<String> auditedSpaceIds,
                             @Singular List<String> billingManagedOrganizationIds,
                             @Singular List<String> managedOrganizationIds,
                             @Singular List<String> managedSpaceIds,
                             @Singular List<String> organizationIds,
                             String spaceId) {
        super(orderDirection, page, resultsPerPage);
        this.auditedOrganizationIds = auditedOrganizationIds;
        this.auditedSpaceIds = auditedSpaceIds;
        this.billingManagedOrganizationIds = billingManagedOrganizationIds;
        this.managedOrganizationIds = managedOrganizationIds;
        this.managedSpaceIds = managedSpaceIds;
        this.organizationIds = organizationIds;
        this.spaceId = spaceId;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.spaceId == null) {
            builder.message("space id must be specified");
        }

        return builder.build();
    }
}
