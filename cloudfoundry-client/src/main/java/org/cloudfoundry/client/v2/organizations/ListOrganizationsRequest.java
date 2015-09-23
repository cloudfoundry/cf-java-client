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

import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;
import org.cloudfoundry.client.v2.FilterParameter;
import org.cloudfoundry.client.v2.PaginatedRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * The request payload for the List Organizations operation
 *
 * <p><b>This class is NOT threadsafe.</b>
 */
public final class ListOrganizationsRequest extends PaginatedRequest<ListOrganizationsRequest> implements Validatable {

    private final List<String> auditorIds = new ArrayList<>();

    private final List<String> billingManagerIds = new ArrayList<>();

    private final List<String> managerIds = new ArrayList<>();

    private final List<String> names = new ArrayList<>();

    private final List<String> spaceIds = new ArrayList<>();

    private final List<String> statuses = new ArrayList<>();

    private final List<String> userIds = new ArrayList<>();


    /**
     * Returns the auditor ids to filter by
     *
     * @return the auditor ids to filter by
     */
    @FilterParameter("auditor_guid")
    public List<String> getAuditorIds() {
        return this.auditorIds;
    }

    /**
     * Add an auditor id to filter by
     *
     * @param auditorId the auditor id to filter by
     * @return {@code this}
     */
    public ListOrganizationsRequest filterByAuditorId(String auditorId) {
        this.auditorIds.add(auditorId);
        return this;
    }

    /**
     * Returns the billing manager ids to filter by
     *
     * @return the billing manager ids to filter by
     */
    @FilterParameter("billing_manager_guid")
    public List<String> getBillingManagerIds() {
        return this.billingManagerIds;
    }

    /**
     * Add a billing manager id to filter by
     *
     * @param billingManagerId the billing manager id to filter by
     * @return {@code this}
     */
    public ListOrganizationsRequest filterByBillingManagerId(String billingManagerId) {
        this.billingManagerIds.add(billingManagerId);
        return this;
    }

    /**
     * Returns the manager ids to filter by
     *
     * @return the manager ids to filter by
     */
    @FilterParameter("manager_guid")
    public List<String> getManagerIds() {
        return this.managerIds;
    }

    /**
     * Add a manager id to filter by
     *
     * @param managerId the manager id to filter by
     * @return {@code this}
     */
    public ListOrganizationsRequest filterByManagerId(String managerId) {
        this.managerIds.add(managerId);
        return this;
    }

    /**
     * Returns the names to filter by
     *
     * @return the names to filter by
     */
    @FilterParameter("name")
    public List<String> getNames() {
        return this.names;
    }

    /**
     * Add a name to filter by
     *
     * @param name the name to filter by
     * @return {@code this}
     */
    public ListOrganizationsRequest filterByName(String name) {
        this.names.add(name);
        return this;
    }

    /**
     * Returns the space ids to filter by
     *
     * @return the space ids to filter by
     */
    @FilterParameter("space_guid")
    public List<String> getSpaceIds() {
        return this.spaceIds;
    }

    /**
     * Add a space id to filter by
     *
     * @param spaceId the space id to filter by
     * @return {@code this}
     */
    public ListOrganizationsRequest filterBySpaceId(String spaceId) {
        this.spaceIds.add(spaceId);
        return this;
    }

    /**
     * Returns the statuses to filter by
     *
     * @return the status to filter by
     */
    @FilterParameter("status")
    public List<String> getStatuses() {
        return this.statuses;
    }

    /**
     * Add a status to filter by
     *
     * @param status the status to filter by
     * @return {@code this}
     */
    public ListOrganizationsRequest filterByStatus(String status) {
        this.statuses.add(status);
        return this;
    }

    /**
     * Returns the user ids to filter by
     *
     * @return the user ids to filter by
     */
    @FilterParameter("user_guid")
    public List<String> getUserIds() {
        return this.userIds;
    }

    /**
     * Add a user id to filter by
     *
     * @param userId the user id to filter by
     * @return {@code this}
     */
    public ListOrganizationsRequest filterByUserId(String userId) {
        this.userIds.add(userId);
        return this;
    }

    @Override
    public ValidationResult isValid() {
        return new ValidationResult();
    }

}
