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
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;

/**
 * The request payload for the Map Application Route operation
 *
 * <p><b>This class is NOT threadsafe.</b>
 */
public final class MapApplicationRouteRequest implements Validatable {

    private volatile String id;

    private volatile String routeId;

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
    public MapApplicationRouteRequest withId(String id) {
        this.id = id;
        return this;
    }

    /**
     * Returns the route id
     *
     * @return the route id
     */
    @JsonProperty("route_guid")
    public String getRouteId() {
        return this.routeId;
    }

    /**
     * Configure the route id
     *
     * @param routeId the route id
     * @return {@code this}
     */
    public MapApplicationRouteRequest withRouteId(String routeId) {
        this.routeId = routeId;
        return this;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult result = new ValidationResult();

        if (this.id == null) {
            result.invalid("id must be specified");
        }

        if (this.routeId == null) {
            result.invalid("route id must be specified");
        }

        return result;
    }

}
