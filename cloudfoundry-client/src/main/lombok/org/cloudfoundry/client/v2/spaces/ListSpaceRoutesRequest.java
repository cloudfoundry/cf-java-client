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
import org.cloudfoundry.client.v2.InFilterParameter;
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
     * The paths
     *
     * @param paths the paths
     * @return the paths
     */
    @Getter(onMethod = @__(@InFilterParameter("path")))
    private final List<String> paths;

    /**
     * The space id
     *
     * @param spaceId the space id
     * @return the space id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String spaceId;

    @Builder
    ListSpaceRoutesRequest(OrderDirection orderDirection, Integer page, Integer resultsPerPage,
                           @Singular List<String> domainIds,
                           @Singular List<String> hosts,
                           @Singular List<String> paths,
                           String spaceId) {
        super(orderDirection, page, resultsPerPage);
        this.domainIds = domainIds;
        this.hosts = hosts;
        this.paths = paths;
        this.spaceId = spaceId;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.spaceId == null) {
            builder.message("space id must be specified");
        }

        return builder.build();
    }

}
