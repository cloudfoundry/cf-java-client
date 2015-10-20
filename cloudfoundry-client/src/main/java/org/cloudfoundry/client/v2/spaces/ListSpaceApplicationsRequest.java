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

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;
import org.cloudfoundry.client.v2.FilterParameter;
import org.cloudfoundry.client.v2.PaginatedRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * The request payload for the List all Apps for the Space operation
 *
 * <p><b>This class is NOT threadsafe.</b>
 */
public final class ListSpaceApplicationsRequest extends PaginatedRequest<ListSpaceApplicationsRequest> implements
        Validatable {

    private final List<Boolean> diegos = new ArrayList<>();

    private volatile String id;

    private final List<String> names = new ArrayList<>();

    private final List<String> organizationIds = new ArrayList<>();

    private final List<String> spaceIds = new ArrayList<>();

    private final List<String> stackIds = new ArrayList<>();

    /**
     * Returns the diego flags
     *
     * @return the diego flags
     */
    @FilterParameter("diego")
    public List<Boolean> getDiegos() {
        return diegos;
    }

    /**
     * Configure the diego flag
     *
     * @param diego the diego flag
     * @return {@code this}
     */
    public ListSpaceApplicationsRequest withDiego(boolean diego) {
        this.diegos.add(diego);
        return this;
    }

    /**
     * Configure the diego flags
     *
     * @param diegos the diego flags
     * @return {@code this}
     */
    public ListSpaceApplicationsRequest withDiegos(List<Boolean> diegos) {
        this.diegos.addAll(diegos);
        return this;
    }

    /**
     * Returns the id
     *
     * @return the id
     */
    @JsonIgnore
    public String getId() {
        return this.id;
    }

    /**
     * Configure the id
     *
     * @param id the id
     * @return {@code this}
     */
    public ListSpaceApplicationsRequest withId(String id) {
        this.id = id;
        return this;
    }


    /**
     * Returns the names
     *
     * @return the names
     */
    @FilterParameter("name")
    public List<String> getNames() {
        return names;
    }

    /**
     * Configure the name
     *
     * @param name the name
     * @return {@code this}
     */
    public ListSpaceApplicationsRequest withName(String name) {
        this.names.add(name);
        return this;
    }

    /**
     * Configure the names
     *
     * @param names the names
     * @return {@code this}
     */
    public ListSpaceApplicationsRequest withNames(List<String> names) {
        this.names.addAll(names);
        return this;
    }

    /**
     * Returns the organization ids
     *
     * @return the organization ids
     */
    @FilterParameter("organization_guid")
    public List<String> getOrganizationIds() {
        return organizationIds;
    }

    /**
     * Configure the organization id
     *
     * @param organizationId the organization id
     * @return {@code this}
     */
    public ListSpaceApplicationsRequest withOrganizationId(String organizationId) {
        this.organizationIds.add(organizationId);
        return this;
    }

    /**
     * Configure the organization ids
     *
     * @param organizationIds the organization ids
     * @return {@code this}
     */
    public ListSpaceApplicationsRequest withOrganizationIds(List<String> organizationIds) {
        this.organizationIds.addAll(organizationIds);
        return this;
    }

    /**
     * Returns the space ids
     *
     * @return the space ids
     */
    @FilterParameter("space_guid")
    public List<String> getSpaceIds() {
        return spaceIds;
    }

    /**
     * Configure the space id
     *
     * @param spaceId the space id
     * @return {@code this}
     */
    public ListSpaceApplicationsRequest withSpaceId(String spaceId) {
        this.spaceIds.add(spaceId);
        return this;
    }

    /**
     * Configure the space ids
     *
     * @param spaceIds the space ids
     * @return {@code this}
     */
    public ListSpaceApplicationsRequest withSpaceIds(List<String> spaceIds) {
        this.spaceIds.addAll(spaceIds);
        return this;
    }

    /**
     * Returns the stack ids
     *
     * @return the stack ids
     */
    @FilterParameter("stack_guid")
    public List<String> getStackIds() {
        return stackIds;
    }

    /**
     * Configure the stack id
     *
     * @param stackId the stack id
     * @return {@code this}
     */
    public ListSpaceApplicationsRequest withStackId(String stackId) {
        this.stackIds.add(stackId);
        return this;
    }

    /**
     * Configure the stack ids
     *
     * @param stackIds the stack ids
     * @return {@code this}
     */
    public ListSpaceApplicationsRequest withStackIds(List<String> stackIds) {
        this.stackIds.addAll(stackIds);
        return this;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult result = new ValidationResult();

        if (this.id == null) {
            result.invalid("id must be specified");
        }

        return result;
    }

}
