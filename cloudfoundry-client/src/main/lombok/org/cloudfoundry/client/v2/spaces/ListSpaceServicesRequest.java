/*
 * Copyright 2015 the original author or authors.
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
    @Getter(onMethod = @__(@FilterParameter("active")))
    private volatile List<String> actives;

    /**
     * The id
     *
     * @param id the id
     * @return the id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private volatile String id;

    /**
     * The labels
     *
     * @param labels the labels
     * @return the labels
     */
    @Getter(onMethod = @__(@FilterParameter("label")))
    private volatile List<String> labels;

    /**
     * The providers
     *
     * @param providers the providers
     * @return the providers
     */
    @Getter(onMethod = @__(@FilterParameter("provider")))
    private volatile List<String> providers;

    /**
     * The service broker ids
     *
     * @param serviceBrokerIds the service broker ids
     * @return the service broker ids
     */
    @Getter(onMethod = @__(@FilterParameter("service_broker_guid")))
    private volatile List<String> serviceBrokerIds;

    @Builder
    ListSpaceServicesRequest(OrderDirection orderDirection, Integer page, Integer resultsPerPage,
                             @Singular List<String> actives,
                             String id,
                             @Singular List<String> labels,
                             @Singular List<String> providers,
                             @Singular List<String> serviceBrokerIds) {
        super(orderDirection, page, resultsPerPage);

        this.actives = actives;
        this.id = id;
        this.labels = labels;
        this.providers = providers;
        this.serviceBrokerIds = serviceBrokerIds;
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
