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

import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;
import org.cloudfoundry.client.v2.FilterParameter;
import org.cloudfoundry.client.v2.PaginatedRequest;

import java.util.ArrayList;
import java.util.List;

import static org.cloudfoundry.client.v2.FilterParameter.Operation.GREATER_THAN_OR_EQUAL_TO;

/**
 * The request payload for the List Events operation
 *
 * <p><b>This class is NOT threadsafe.</b>
 */
public final class ListEventsRequest extends PaginatedRequest<ListEventsRequest> implements Validatable {

    private final List<String> actees = new ArrayList<>();

    private final List<String> timestamps = new ArrayList<>();

    private final List<String> types = new ArrayList<>();

    /**
     * Returns the actees
     *
     * @return the actees
     */
    @FilterParameter("actee")
    public List<String> getActees() {
        return this.actees;
    }

    /**
     * Configure the actee
     *
     * @param actee the actee
     * @return {@code this}
     */
    public ListEventsRequest withActee(String actee) {
        this.actees.add(actee);
        return this;
    }

    /**
     * Configure the actees
     *
     * @param actees the actees
     * @return {@code this}
     */
    public ListEventsRequest withActees(List<String> actees) {
        this.actees.addAll(actees);
        return this;
    }

    /**
     * Returns the timestamps
     *
     * @return the timestamps
     */
    @FilterParameter(name = "timestamp", operation = GREATER_THAN_OR_EQUAL_TO)
    public List<String> getTimestamps() {
        return this.timestamps;
    }

    /**
     * Configure the timestamp
     *
     * @param timestamp the timestamp
     * @return {@code this}
     */
    public ListEventsRequest withTimestamp(String timestamp) {
        this.timestamps.add(timestamp);
        return this;
    }

    /**
     * Configure the timestamps
     *
     * @param timestamps the timestamps
     * @return {@code this}
     */
    public ListEventsRequest withTimestamps(List<String> timestamps) {
        this.timestamps.addAll(timestamps);
        return this;
    }

    /**
     * Returns the types
     *
     * @return the types
     */
    @FilterParameter("type")
    public List<String> getTypes() {
        return this.types;
    }

    /**
     * Configure the type
     *
     * @param type the type
     * @return {@code this}
     */
    public ListEventsRequest withType(String type) {
        this.types.add(type);
        return this;
    }

    /**
     * Configure the types
     *
     * @param types the types
     * @return {@code this}
     */
    public ListEventsRequest withTypes(List<String> types) {
        this.types.addAll(types);
        return this;
    }

    @Override
    public ValidationResult isValid() {
        return new ValidationResult();
    }

}
