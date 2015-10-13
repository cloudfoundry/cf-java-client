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
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;
import org.cloudfoundry.client.v3.PaginatedAndSortedRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The request payload for the List Application Droplets operation
 *
 * <p><b>This class is NOT threadsafe.</b>
 */
public final class ListApplicationDropletsRequest extends PaginatedAndSortedRequest<ListApplicationDropletsRequest> implements Validatable {

    private volatile String id;

    private volatile List<String> state = new ArrayList<>();

    /**
     * Returns the id
     *
     * @return the id
     */
    @JsonIgnore
    public String getId() {
        return this.id;
    }

    /**
     * Configure the id
     *
     * @param id the id
     * @return {@code this}
     */
    public ListApplicationDropletsRequest withId(String id) {
        this.id = id;
        return this;
    }

    /**
     * Returns the state
     *
     * @return the state
     */
    @JsonIgnore
    public String[] getState() {
        return this.state.toArray(new String[this.state.size()]);
    }

    /**
     * Configure the id
     *
     * @param state the id
     * @return {@code this}
     */
    public ListApplicationDropletsRequest withState(String state) {
        this.state.add(state);
        return this;
    }

    /**
     * Configure the id
     *
     * @param state the id
     * @return {@code this}
     */
    public ListApplicationDropletsRequest withStates(String[] state) {
        this.state.addAll(Arrays.asList(state));
        return this;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult result = isPaginatedAndSortedRequestValid();

        if (this.id == null) {
            result.invalid("id must be specified");
        }

        return result;
    }

}
