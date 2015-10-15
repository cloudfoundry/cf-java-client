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

package org.cloudfoundry.client.v2.applications;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The entity response payload for the Application resource
 *
 * <p><b>This class is NOT threadsafe.</b>
 */
public final class ApplicationEntity extends AbstractApplicationEntity<ApplicationEntity> {

    private volatile String eventsUrl;

    private volatile String routesUrl;

    private volatile String serviceBindingsUrl;

    private volatile String spaceUrl;

    private volatile String stackUrl;

    /**
     * Returns the events URL
     *
     * @return the events URL
     */
    public final String getEventsUrl() {
        return eventsUrl;
    }

    /**
     * Configure the events URL
     *
     * @param eventsUrl the events URL
     * @return {@code this}
     */
    @JsonProperty("events_url")
    public final ApplicationEntity withEventsUrl(String eventsUrl) {
        this.eventsUrl = eventsUrl;
        return this;
    }

    /**
     * Returns routes URL
     *
     * @return routes URL
     */
    public final String getRoutesUrl() {
        return routesUrl;
    }

    /**
     * Configure routes URL
     *
     * @param routesUrl routes URL
     * @return {@code this}
     */
    @JsonProperty("routes_url")
    public final ApplicationEntity withRoutesUrl(String routesUrl) {
        this.routesUrl = routesUrl;
        return this;
    }

    /**
     * Returns service bindings URL
     *
     * @return service bindings URL
     */
    public final String getServiceBindingsUrl() {
        return serviceBindingsUrl;
    }

    /**
     * Configure service bindings URL
     *
     * @param serviceBindingsUrl service bindings URL
     * @return {@code this}
     */
    @JsonProperty("service_bindings_url")
    public final ApplicationEntity withServiceBindingsUrl(String serviceBindingsUrl) {
        this.serviceBindingsUrl = serviceBindingsUrl;
        return this;
    }

    /**
     * Returns space URL
     *
     * @return space URL
     */
    public final String getSpaceUrl() {
        return spaceUrl;
    }

    /**
     * Configure space URL
     *
     * @param spaceUrl space URL
     * @return {@code this}
     */
    @JsonProperty("space_url")
    public final ApplicationEntity withSpaceUrl(String spaceUrl) {
        this.spaceUrl = spaceUrl;
        return this;
    }

    /**
     * Returns stack URL
     *
     * @return stack URL
     */
    public final String getStackUrl() {
        return stackUrl;
    }

    /**
     * Configure stack URL
     *
     * @param stackUrl stack URL
     * @return {@code this}
     */
    @JsonProperty("stack_url")
    public final ApplicationEntity withStackUrl(String stackUrl) {
        this.stackUrl = stackUrl;
        return this;
    }

}
