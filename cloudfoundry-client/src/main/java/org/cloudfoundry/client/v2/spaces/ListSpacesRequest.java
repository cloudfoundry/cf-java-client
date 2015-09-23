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

package org.cloudfoundry.client.v2.spaces;

import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;
import org.cloudfoundry.client.v2.FilterParameter;
import org.cloudfoundry.client.v2.PaginatedRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * The request payload for the List Spaces operation
 *
 * <p><b>This class is NOT threadsafe.</b>
 */
public final class ListSpacesRequest extends PaginatedRequest<ListSpacesRequest> implements Validatable {

    private final List<String> applicationIds = new ArrayList<>();

    private final List<String> developerIds = new ArrayList<>();

    private final List<String> names = new ArrayList<>();

    private final List<String> organizationIds = new ArrayList<>();

    /**
     * Returns the application ids
     *
     * @return the application ids
     */
    @FilterParameter("app_guid")
    public List<String> getApplicationIds() {
        return this.applicationIds;
    }

    /**
     * Configure the application id
     *
     * @param applicationId the application id
     * @return {@code this}
     */
    public ListSpacesRequest withApplicationId(String applicationId) {
        this.applicationIds.add(applicationId);
        return this;
    }

    /**
     * Configure the application ids
     *
     * @param applicationIds the application ids
     * @return {@code this}
     */
    public ListSpacesRequest withApplicationIds(List<String> applicationIds) {
        this.applicationIds.addAll(applicationIds);
        return this;
    }

    /**
     * Returns the developer ids
     *
     * @return the developer ids
     */
    @FilterParameter("developer_guid")
    public List<String> getDeveloperIds() {
        return this.developerIds;
    }

    /**
     * Configure the developer id
     *
     * @param developerId the developer id
     * @return {@code this}
     */
    public ListSpacesRequest withDeveloperId(String developerId) {
        this.developerIds.add(developerId);
        return this;
    }

    /**
     * Configure the developer ids
     *
     * @param developerIds the developer ids
     * @return {@code this}
     */
    public ListSpacesRequest withDeveloperIds(List<String> developerIds) {
        this.developerIds.addAll(developerIds);
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
    public ListSpacesRequest withName(String name) {
        this.names.add(name);
        return this;
    }

    /**
     * Configure the names
     *
     * @param names the names
     * @return {@code this}
     */
    public final ListSpacesRequest withNames(List<String> names) {
        this.names.addAll(names);
        return this;
    }

    /**
     * Returns the organization ids to filter by
     *
     * @return the organization ids to filter by
     */
    @FilterParameter("organization_guid")
    public List<String> getOrganizationIds() {
        return this.organizationIds;
    }

    /**
     * Add an organization id to filter by
     *
     * @param organizationId the organization id to filter by
     * @return {@code this}
     */
    public ListSpacesRequest withOrganizationId(String organizationId) {
        this.organizationIds.add(organizationId);
        return this;
    }

    /**
     * Configure the organization ids
     *
     * @param organizationIds the organization ids
     * @return {@code this}
     */
    public final ListSpacesRequest withOrganizationIds(List<String> organizationIds) {
        this.organizationIds.addAll(organizationIds);
        return this;
    }

    @Override
    public ValidationResult isValid() {
        return new ValidationResult();
    }

}
