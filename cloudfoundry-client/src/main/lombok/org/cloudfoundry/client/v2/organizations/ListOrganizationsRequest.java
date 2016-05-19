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

package org.cloudfoundry.client.v2.organizations;

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
 * The request payload for the List Organizations operation
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class ListOrganizationsRequest extends PaginatedRequest implements Validatable {

    /**
     * The auditor ids
     *
     * @param auditorIds the auditor ids
     * @return the auditor ids
     */
    @Getter(onMethod = @__(@InFilterParameter("auditor_guid")))
    private final List<String> auditorIds;

    /**
     * The billing manager ids
     *
     * @param billingManagerIds the billing manager ids
     * @return the billing manager ids
     */
    @Getter(onMethod = @__(@InFilterParameter("billing_manager_guid")))
    private final List<String> billingManagerIds;

    /**
     * The manager ids
     *
     * @param managerIds the manager ids
     * @return the manager ids
     */
    @Getter(onMethod = @__(@InFilterParameter("manager_guid")))
    private final List<String> managerIds;

    /**
     * The names
     *
     * @param names the names
     * @return the names
     */
    @Getter(onMethod = @__(@InFilterParameter("name")))
    private final List<String> names;

    /**
     * The space ids
     *
     * @param spaceIds the space ids
     * @return the space ids
     */
    @Getter(onMethod = @__(@InFilterParameter("space_guid")))
    private final List<String> spaceIds;

    /**
     * The statuses
     *
     * @param statuses the statuses
     * @return the statuses
     */
    @Getter(onMethod = @__(@InFilterParameter("status")))
    private final List<String> statuses;

    /**
     * The user ids
     *
     * @param userIds the user ids
     * @return the user ids
     */
    @Getter(onMethod = @__(@InFilterParameter("user_guid")))
    private final List<String> userIds;

    @Builder
    ListOrganizationsRequest(OrderDirection orderDirection, Integer page, Integer resultsPerPage,
                             @Singular List<String> auditorIds,
                             @Singular List<String> billingManagerIds,
                             @Singular List<String> managerIds,
                             @Singular List<String> names,
                             @Singular List<String> spaceIds,
                             @Singular List<String> statuses,
                             @Singular List<String> userIds) {
        super(orderDirection, page, resultsPerPage);
        this.auditorIds = auditorIds;
        this.billingManagerIds = billingManagerIds;
        this.managerIds = managerIds;
        this.names = names;
        this.spaceIds = spaceIds;
        this.statuses = statuses;
        this.userIds = userIds;
    }

    @Override
    public ValidationResult isValid() {
        return ValidationResult.builder().build();
    }

}
