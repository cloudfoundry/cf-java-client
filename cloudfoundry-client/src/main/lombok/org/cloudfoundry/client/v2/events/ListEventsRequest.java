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

package org.cloudfoundry.client.v2.events;

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

import static org.cloudfoundry.client.v2.FilterParameter.Operation.GREATER_THAN_OR_EQUAL_TO;

/**
 * The request payload for the List Events operation
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class ListEventsRequest extends PaginatedRequest implements Validatable {

    /**
     * The actees
     *
     * @param actees the actees
     * @return the actees
     */
    @Getter(onMethod = @__(@FilterParameter("actee")))
    private final List<String> actees;

    /**
     * The timestamps
     *
     * @param timestamps the timestamps
     * @return the timestamps
     */
    private final List<String> timestamps;

    /**
     * The types
     *
     * @param types the types
     * @return the types
     */
    @Getter(onMethod = @__(@FilterParameter("type")))
    private final List<String> types;

    @Builder
    ListEventsRequest(OrderDirection orderDirection, Integer page, Integer resultsPerPage,
                      @Singular List<String> actees, @Singular List<String> timestamps, @Singular List<String> types) {
        super(orderDirection, page, resultsPerPage);
        this.actees = actees;
        this.timestamps = timestamps;
        this.types = types;
    }

    /**
     * The timestamps
     *
     * @return the timestamps
     */
    @FilterParameter(name = "timestamp", operation = GREATER_THAN_OR_EQUAL_TO)
    public List<String> getTimestamps() {
        // This method exists because annotations with arguments break Lombok.
        // https://github.com/rzwitserloot/lombok/issues/735
        return this.timestamps;
    }

    @Override
    public ValidationResult isValid() {
        return ValidationResult.builder().build();
    }

}
