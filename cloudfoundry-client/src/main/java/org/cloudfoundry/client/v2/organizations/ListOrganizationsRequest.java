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
     * Returns the auditor ids
     *
     * @return the auditor ids
     */
    @FilterParameter("auditor_guid")
    public List<String> getAuditorIds() {
        return this.auditorIds;
    }

    /**
     * Configure the auditor id
     *
     * @param auditorId the auditor id
     * @return {@code this}
     */
    public ListOrganizationsRequest withAuditorId(String auditorId) {
        this.auditorIds.add(auditorId);
        return this;
    }

    /**
     * Configure the auditor ids
     *
     * @param auditorIds the auditor ids
     * @return {@code this}
     */
    public ListOrganizationsRequest withAuditorIds(List<String> auditorIds) {
        this.auditorIds.addAll(auditorIds);
        return this;
    }

    /**
     * Returns the billing manager ids
     *
     * @return the billing manager ids
     */
    @FilterParameter("billing_manager_guid")
    public List<String> getBillingManagerIds() {
        return this.billingManagerIds;
    }

    /**
     * Configure the billing manager id
     *
     * @param billingManagerId the billing manager id
     * @return {@code this}
     */
    public ListOrganizationsRequest withBillingManagerId(String billingManagerId) {
        this.billingManagerIds.add(billingManagerId);
        return this;
    }

    /**
     * Configure the billing manager ids
     *
     * @param billingManagerIds the billing manager ids
     * @return {@code this}
     */
    public ListOrganizationsRequest withBillingManagerIds(List<String> billingManagerIds) {
        this.billingManagerIds.addAll(billingManagerIds);
        return this;
    }

    /**
     * Returns the manager ids
     *
     * @return the manager ids
     */
    @FilterParameter("manager_guid")
    public List<String> getManagerIds() {
        return this.managerIds;
    }

    /**
     * Configure the manager id
     *
     * @param managerId the manager id
     * @return {@code this}
     */
    public ListOrganizationsRequest withManagerId(String managerId) {
        this.managerIds.add(managerId);
        return this;
    }

    /**
     * Configure the manager ids
     *
     * @param managerIds the manager ids
     * @return {@code this}
     */
    public ListOrganizationsRequest withManagerIds(List<String> managerIds) {
        this.managerIds.addAll(managerIds);
        return this;
    }

    /**
     * Returns the names
     *
     * @return the names
     */
    @FilterParameter("name")
    public List<String> getNames() {
        return this.names;
    }

    /**
     * Configure the name
     *
     * @param name the name
     * @return {@code this}
     */
    public ListOrganizationsRequest withName(String name) {
        this.names.add(name);
        return this;
    }

    /**
     * Configure the names
     *
     * @param names the names
     * @return {@code this}
     */
    public ListOrganizationsRequest withNames(List<String> names) {
        this.names.addAll(names);
        return this;
    }

    /**
     * Returns the space ids
     *
     * @return the space ids
     */
    @FilterParameter("space_guid")
    public List<String> getSpaceIds() {
        return this.spaceIds;
    }

    /**
     * Configure the space id
     *
     * @param spaceId the space id
     * @return {@code this}
     */
    public ListOrganizationsRequest withSpaceId(String spaceId) {
        this.spaceIds.add(spaceId);
        return this;
    }

    /**
     * Configure the space ids
     *
     * @param spaceIds the space ids
     * @return {@code this}
     */
    public ListOrganizationsRequest withSpaceIds(List<String> spaceIds) {
        this.spaceIds.addAll(spaceIds);
        return this;
    }

    /**
     * Returns the statuses
     *
     * @return the statuses
     */
    @FilterParameter("status")
    public List<String> getStatuses() {
        return this.statuses;
    }

    /**
     * Configure the status
     *
     * @param status the status
     * @return {@code this}
     */
    public ListOrganizationsRequest withStatus(String status) {
        this.statuses.add(status);
        return this;
    }

    /**
     * Configure the statuses
     *
     * @param statuses the statuses
     * @return {@code this}
     */
    public ListOrganizationsRequest withStatuses(List<String> statuses) {
        this.statuses.addAll(statuses);
        return this;
    }

    /**
     * Returns the user ids
     *
     * @return the user ids
     */
    @FilterParameter("user_guid")
    public List<String> getUserIds() {
        return this.userIds;
    }

    /**
     * Configure the user id
     *
     * @param userId the user id
     * @return {@code this}
     */
    public ListOrganizationsRequest withUserId(String userId) {
        this.userIds.add(userId);
        return this;
    }

    /**
     * Configure the user ids
     *
     * @param userIds the user ids
     * @return {@code this}
     */
    public ListOrganizationsRequest withUserIds(List<String> userIds) {
        this.userIds.addAll(userIds);
        return this;
    }

    @Override
    public ValidationResult isValid() {
        return new ValidationResult();
    }

}
