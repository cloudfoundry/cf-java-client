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

package org.cloudfoundry.client.v2.spaces;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * The response payload for the Get Space summary operation
 *
 * <p><b>This class is NOT threadsafe.</b>
 */
public final class GetSpaceSummaryResponse {

    private final List<SpaceApplicationSummary> applications = new ArrayList<>();

    private volatile String id; //guid

    private volatile String name;

    private final List<SpaceServiceSummary> services = new ArrayList<>();

    /**
     * Returns list of application summaries for this space
     *
     * @return list of application summaries for this space
     */
    public final List<SpaceApplicationSummary> getApplications() {
        return applications;
    }

    /**
     * Configure with an application summary for this space
     *
     * @param application an application summary for this space
     * @return {@code this}
     */
    public final GetSpaceSummaryResponse withApplication(SpaceApplicationSummary application) {
        this.applications.add(application);
        return this;
    }

    /**
     * Configure with list of application summaries for this space
     *
     * @param applications list of application summaries for this space
     * @return {@code this}
     */
    @JsonProperty("apps")
    public final GetSpaceSummaryResponse withApplications(List<SpaceApplicationSummary> applications) {
        this.applications.addAll(applications);
        return this;
    }

    /**
     * Returns id
     *
     * @return id
     */
    public final String getId() {
        return id;
    }

    /**
     * Configure id
     *
     * @param id id
     * @return {@code this}
     */
    @JsonProperty("guid")
    public final GetSpaceSummaryResponse withId(String id) {
        this.id = id;
        return this;
    }

    /**
     * Returns name
     *
     * @return name
     */
    public final String getName() {
        return name;
    }

    /**
     * Configure name
     *
     * @param name name
     * @return {@code this}
     */
    @JsonProperty("name")
    public final GetSpaceSummaryResponse withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Returns list of service summaries for this space
     *
     * @return list of service summaries for this space
     */
    public final List<SpaceServiceSummary> getServices() {
        return services;
    }

    /**
     * Configure with a service summary for this space
     *
     * @param service a service summary for this space
     * @return {@code this}
     */
    public final GetSpaceSummaryResponse withService(SpaceServiceSummary service) {
        this.services.add(service);
        return this;
    }

    /**
     * Configure with list of service summaries for this space
     *
     * @param services list of service summaries for this space
     * @return {@code this}
     */
    @JsonProperty("services")
    public final GetSpaceSummaryResponse withServices(List<SpaceServiceSummary> services) {
        this.services.addAll(services);
        return this;
    }
}
