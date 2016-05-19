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

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;
import org.cloudfoundry.client.v2.InFilterParameter;
import org.cloudfoundry.client.v2.IsFilterParameter;
import org.cloudfoundry.client.v2.OrderDirection;
import org.cloudfoundry.client.v2.PaginatedRequest;

import java.util.List;

/**
 * The request payload for the List all Applications for the Route operation
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class ListRouteApplicationsRequest extends PaginatedRequest implements Validatable {

    /**
     * The diego flag
     *
     * @param diegos the diego flag
     * @return the diego flag
     */
    @Getter(onMethod = @__(@IsFilterParameter("diego")))
    private final Boolean diego;

    /**
     * The names
     *
     * @param names the names
     * @return the names
     */
    @Getter(onMethod = @__(@InFilterParameter("name")))
    private final List<String> names;

    /**
     * The organization ids
     *
     * @param organizationIds the organization ids
     * @return the organization ids
     */
    @Getter(onMethod = @__(@InFilterParameter("organization_guid")))
    private final List<String> organizationIds;

    /**
     * The route id
     *
     * @param routeId the route id
     * @return the route id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String routeId;

    /**
     * The space ids
     *
     * @param spaceIds the space ids
     * @return the space ids
     */
    @Getter(onMethod = @__(@InFilterParameter("space_guid")))
    private final List<String> spaceIds;

    /**
     * The stack ids
     *
     * @param stackIds the stack ids
     * @return the stack ids
     */
    @Getter(onMethod = @__(@InFilterParameter("stack_guid")))
    private final List<String> stackIds;

    @Builder
    ListRouteApplicationsRequest(OrderDirection orderDirection, Integer page, Integer resultsPerPage,
                                 Boolean diego,
                                 @Singular List<String> names,
                                 @Singular List<String> organizationIds,
                                 String routeId,
                                 @Singular List<String> spaceIds,
                                 @Singular List<String> stackIds) {
        super(orderDirection, page, resultsPerPage);

        this.diego = diego;
        this.names = names;
        this.organizationIds = organizationIds;
        this.routeId = routeId;
        this.spaceIds = spaceIds;
        this.stackIds = stackIds;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.routeId == null) {
            builder.message("route id must be specified");
        }

        return builder.build();
    }

}
