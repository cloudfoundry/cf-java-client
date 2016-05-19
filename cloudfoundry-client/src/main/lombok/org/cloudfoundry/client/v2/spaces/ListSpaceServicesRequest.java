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
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;
import org.cloudfoundry.client.v2.InFilterParameter;
import org.cloudfoundry.client.v2.OrderDirection;
import org.cloudfoundry.client.v2.PaginatedRequest;

import java.util.List;

/**
 * The request payload for the List all Services for the Space operation
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class ListSpaceServicesRequest extends PaginatedRequest implements Validatable {

    /**
     * The actives
     *
     * @param actives the actives
     * @return the actives
     */
    @Getter(onMethod = @__(@InFilterParameter("active")))
    private final List<String> actives;

    /**
     * The labels
     *
     * @param labels the labels
     * @return the labels
     */
    @Getter(onMethod = @__(@InFilterParameter("label")))
    private final List<String> labels;

    /**
     * The providers
     *
     * @param providers the providers
     * @return the providers
     */
    @Getter(onMethod = @__(@InFilterParameter("provider")))
    private final List<String> providers;

    /**
     * The service broker ids
     *
     * @param serviceBrokerIds the service broker ids
     * @return the service broker ids
     */
    @Getter(onMethod = @__(@InFilterParameter("service_broker_guid")))
    private final List<String> serviceBrokerIds;

    /**
     * The space id
     *
     * @param spaceId the space id
     * @return the space id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String spaceId;

    @Builder
    ListSpaceServicesRequest(OrderDirection orderDirection, Integer page, Integer resultsPerPage,
                             @Singular List<String> actives,
                             @Singular List<String> labels,
                             @Singular List<String> providers,
                             @Singular List<String> serviceBrokerIds,
                             String spaceId) {
        super(orderDirection, page, resultsPerPage);
        this.actives = actives;
        this.labels = labels;
        this.providers = providers;
        this.serviceBrokerIds = serviceBrokerIds;
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
