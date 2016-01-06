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

package org.cloudfoundry.client.v3.applications;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;
import org.cloudfoundry.client.v3.FilterParameter;
import org.cloudfoundry.client.v3.PaginatedAndSortedRequest;

import java.util.List;

/**
 * The request payload for the List Applications operation
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class ListApplicationsRequest extends PaginatedAndSortedRequest implements Validatable {

    /**
     * The ids
     *
     * @param ids the ids
     * @return the ids
     */
    @Getter(onMethod = @__(@FilterParameter("guids")))
    private final List<String> ids;

    /**
     * The names
     *
     * @param names the names
     * @return the names
     */
    @Getter(onMethod = @__(@FilterParameter("names")))
    private final List<String> names;

    /**
     * The organization ids
     *
     * @param organizationIds the organization ids
     * @return the organization ids
     */
    @Getter(onMethod = @__(@FilterParameter("organization_guids")))
    private final List<String> organizationIds;

    /**
     * The space ids
     *
     * @param spaceIds the space ids
     * @return the space ids
     */
    @Getter(onMethod = @__(@FilterParameter("space_guids")))
    private final List<String> spaceIds;

    @Builder
    ListApplicationsRequest(Integer page, Integer perPage, OrderBy orderBy, OrderDirection orderDirection,
                            @Singular List<String> ids,
                            @Singular List<String> names,
                            @Singular List<String> organizationIds,
                            @Singular List<String> spaceIds) {
        super(page, perPage, orderBy, orderDirection);
        this.ids = ids;
        this.names = names;
        this.organizationIds = organizationIds;
        this.spaceIds = spaceIds;
    }

    @Override
    public ValidationResult isValid() {
        return isPaginatedAndSortedRequestValid().build();
    }

}
