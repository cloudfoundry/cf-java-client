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
     * Returns the application ids to filter by
     *
     * @return the application ids to filter by
     */
    @FilterParameter("app_guid")
    public List<String> getApplicationIds() {
        return this.applicationIds;
    }

    /**
     * Add an application id to filter by
     *
     * @param applicationId the application id to filter by
     * @return {@code this}
     */
    public ListSpacesRequest filterByApplicationId(String applicationId) {
        this.applicationIds.add(applicationId);
        return this;
    }

    /**
     * Returns the developer ids to filter by
     *
     * @return the developer ids to filter by
     */
    @FilterParameter("developer_guid")
    public List<String> getDeveloperIds() {
        return this.developerIds;
    }

    /**
     * Add a developer id to filter by
     *
     * @param developerId the developer id to filter by
     * @return {@code this}
     */
    public ListSpacesRequest filterByDeveloperId(String developerId) {
        this.developerIds.add(developerId);
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
    public ListSpacesRequest filterByName(String name) {
        this.names.add(name);
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
    public ListSpacesRequest filterByOrganizationId(String organizationId) {
        this.organizationIds.add(organizationId);
        return this;
    }

    @Override
    public ValidationResult isValid() {
        return new ValidationResult();
    }

}
