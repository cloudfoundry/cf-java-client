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

import com.fasterxml.jackson.annotation.JsonIgnore;
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
 * The request payload for the List Application Droplets operation
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class ListApplicationDropletsRequest extends PaginatedAndSortedRequest implements Validatable {

    /**
     * The id
     *
     * @param id the id
     * @return the id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String id;

    /**
     * The states
     *
     * @param states the states
     * @return states
     */
    @Getter(onMethod = @__(@FilterParameter("state")))
    private final List<String> states;

    @Builder
    ListApplicationDropletsRequest(Integer page, Integer perPage, OrderBy orderBy, OrderDirection orderDirection,
                                   String id, @Singular List<String> states) {
        super(page, perPage, orderBy, orderDirection);
        this.id = id;
        this.states = states;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = isPaginatedAndSortedRequestValid();

        if (this.id == null) {
            builder.message("id must be specified");
        }

        return builder.build();
    }

}
