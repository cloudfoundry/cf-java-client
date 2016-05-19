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

package org.cloudfoundry.client.v2.routes;

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
 * The request payload for the List all Routes operation
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class ListRoutesRequest extends PaginatedRequest implements Validatable {

    /**
     * The domain ids
     *
     * @param domainIds the domain ids
     * @return the domain ids
     */
    @Getter(onMethod = @__(@InFilterParameter("domain_guid")))
    private final List<String> domainIds;

    /**
     * The hosts
     *
     * @param hosts the hosts
     * @return the hosts
     */
    @Getter(onMethod = @__(@InFilterParameter("host")))
    private final List<String> hosts;

    /**
     * The organization ids
     *
     * @param organizationIds the organization ids
     * @return the organization ids
     */
    @Getter(onMethod = @__(@InFilterParameter("organization_guid")))
    private final String organizationId;

    /**
     * The paths
     *
     * @param paths the paths
     * @return the paths
     */
    @Getter(onMethod = @__(@InFilterParameter("path")))
    private final List<String> paths;

    @Builder
    ListRoutesRequest(OrderDirection orderDirection, Integer page, Integer resultsPerPage,
                      @Singular List<String> domainIds,
                      @Singular List<String> hosts,
                      String organizationId,
                      @Singular List<String> paths) {
        super(orderDirection, page, resultsPerPage);

        this.domainIds = domainIds;
        this.hosts = hosts;
        this.organizationId = organizationId;
        this.paths = paths;
    }

    @Override
    public ValidationResult isValid() {
        return ValidationResult.builder().build();
    }

}
