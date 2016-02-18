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

package org.cloudfoundry.client.v3.droplets;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;
import org.cloudfoundry.client.v3.FilterParameter;
import org.cloudfoundry.client.v3.PaginatedAndSortedRequest;

import java.util.List;

/**
 * The request payload for the List Applications operation
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class ListDropletsRequest extends PaginatedAndSortedRequest implements Validatable {

    /**
     * The application ids
     *
     * @param applicationIds the application ids
     * @return the application ids
     */
    @Getter(onMethod = @__(@FilterParameter("app_guids")))
    private final List<String> applicationIds;

    /**
     * The states
     *
     * @param states the states
     * @return the states
     */
    @Getter(onMethod = @__(@FilterParameter("states")))
    private final List<String> states;


    @Builder
    ListDropletsRequest(Integer page, Integer perPage, String orderBy,
                        @Singular List<String> applicationIds,
                        @Singular List<String> states) {
        super(page, perPage, orderBy);
        this.applicationIds = applicationIds;
        this.states = states;
    }

    @Override
    public ValidationResult isValid() {
        return isPaginatedAndSortedRequestValid().build();
    }

}
