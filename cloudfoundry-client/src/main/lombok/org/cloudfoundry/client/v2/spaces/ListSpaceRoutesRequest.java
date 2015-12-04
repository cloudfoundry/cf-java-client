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
 * The request payload for the List all Routes for the Space operation
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class ListSpaceRoutesRequest extends PaginatedRequest implements Validatable {

    /**
     * The domain ids
     *
     * @param domainIds the domain ids
     * @return the domain ids
     */
    @Getter(onMethod = @__(@FilterParameter("domain_guid")))
    private volatile List<String> domainIds;

    /**
     * The hosts
     *
     * @param hosts the hosts
     * @return the hosts
     */
    @Getter(onMethod = @__(@FilterParameter("host")))
    private volatile List<String> hosts;

    /**
     * The id
     *
     * @param id the id
     * @return the id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private volatile String id;

    /**
     * The organization ids
     *
     * @param organizationIds the organization ids
     * @return the organization ids
     */
    @Getter(onMethod = @__(@FilterParameter("organization_guid")))
    private volatile List<String> organizationIds;

    /**
     * The paths
     *
     * @param paths the paths
     * @return the paths
     */
    @Getter(onMethod = @__(@FilterParameter("path")))
    private volatile List<String> paths;

    @Builder
    ListSpaceRoutesRequest(OrderDirection orderDirection, Integer page, Integer resultsPerPage,
                           @Singular List<String> domainIds,
                           String id,
                           @Singular List<String> hosts,
                           @Singular List<String> organizationIds,
                           @Singular List<String> paths) {
        super(orderDirection, page, resultsPerPage);

        this.domainIds = domainIds;
        this.id = id;
        this.hosts = hosts;
        this.organizationIds = organizationIds;
        this.paths = paths;
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
