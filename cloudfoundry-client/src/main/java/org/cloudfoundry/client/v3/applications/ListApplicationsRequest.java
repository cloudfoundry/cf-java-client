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

package org.cloudfoundry.client.v3.applications;

import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;
import org.cloudfoundry.client.v3.FilterParameter;
import org.cloudfoundry.client.v3.PaginatedAndSortedRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * The request payload for the List Applications operation
 *
 * <p><b>This class is NOT threadsafe.</b>
 */
public final class ListApplicationsRequest extends PaginatedAndSortedRequest<ListApplicationsRequest>
        implements Validatable {

    private final List<String> ids = new ArrayList<>();

    private final List<String> names = new ArrayList<>();

    private final List<String> organizationIds = new ArrayList<>();

    private final List<String> spaceIds = new ArrayList<>();

    /**
     * Returns the ids
     *
     * @return the ids
     */
    @FilterParameter("guids")
    public List<String> getIds() {
        return this.ids;
    }

    /**
     * Configure an id
     *
     * @param id the id
     * @return {@code this}
     */
    public final ListApplicationsRequest withId(String id) {
        this.ids.add(id);
        return this;
    }

    /**
     * Configure the ids
     *
     * @param ids the ids
     * @return {@code this}
     */
    public final ListApplicationsRequest withIds(List<String> ids) {
        this.ids.addAll(ids);
        return this;
    }

    /**
     * Returns the names
     *
     * @return the names
     */
    @FilterParameter("names")
    public List<String> getNames() {
        return this.names;
    }

    /**
     * Configure the name
     *
     * @param name the name
     * @return {@code this}
     */
    public final ListApplicationsRequest withName(String name) {
        this.names.add(name);
        return this;
    }

    /**
     * Configure the names
     *
     * @param names the names
     * @return {@code this}
     */
    public final ListApplicationsRequest withNames(List<String> names) {
        this.names.addAll(names);
        return this;
    }

    /**
     * Returns the organization ids
     *
     * @return the organization ids
     */
    @FilterParameter("organization_guids")
    public List<String> getOrganizationIds() {
        return this.organizationIds;
    }

    /**
     * Configure an organization id
     *
     * @param organizationId the organization id
     * @return {@code this}
     */
    public final ListApplicationsRequest withOrganizationId(String organizationId) {
        this.organizationIds.add(organizationId);
        return this;
    }

    /**
     * Configure the organization ids
     *
     * @param organizationIds the organization ids
     * @return {@code this}
     */
    public final ListApplicationsRequest withOrganizationIds(List<String> organizationIds) {
        this.organizationIds.addAll(organizationIds);
        return this;
    }

    /**
     * Returns the space ids
     *
     * @return the space ids
     */
    @FilterParameter("space_guids")
    public List<String> getSpaceIds() {
        return this.spaceIds;
    }

    /**
     * Configure a space id
     *
     * @param spaceId the space id
     * @return {@code this}
     */
    public final ListApplicationsRequest withSpaceId(String spaceId) {
        this.spaceIds.add(spaceId);
        return this;
    }

    /**
     * Configure the space ids
     *
     * @param spaceIds the space ids
     * @return {@code this}
     */
    public final ListApplicationsRequest withSpaceIds(List<String> spaceIds) {
        this.spaceIds.addAll(spaceIds);
        return this;
    }

    @Override
    public ValidationResult isValid() {
        return isPaginatedAndSortedRequestValid();
    }

}
