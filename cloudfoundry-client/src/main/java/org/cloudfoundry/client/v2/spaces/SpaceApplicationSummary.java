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
import org.cloudfoundry.client.v2.applications.AbstractApplicationEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * The Application part of a Space Summary in a response payload.
 *
 * <p><b>This class is NOT threadsafe.</b>
 */
public final class SpaceApplicationSummary extends AbstractApplicationEntity<SpaceApplicationSummary> {

    private volatile String id; //guid

    private final List<Route> routes = new ArrayList<>();

    private volatile Integer runningInstances;

    private volatile Integer serviceCount;

    private final List<String> serviceNames = new ArrayList<>();

    private final List<String> urls = new ArrayList<>();

    /**
     * Returns the id of the application
     *
     * @return the id of the application
     */
    public final String getId() {
        return id;
    }

    /**
     * Configure the id of the application
     *
     * @param id the id of the application
     * @return {@code this}
     */
    @JsonProperty("guid")
    public final SpaceApplicationSummary withId(String id) {
        this.id = id;
        return this;
    }

    /**
     * Returns the list of route summaries
     *
     * @return the list of route summaries
     */
    public final List<Route> getRoutes() {
        return routes;
    }

    /**
     * Configure a route summary
     *
     * @param route a route summary
     * @return {@code this}
     */
    public final SpaceApplicationSummary withRoute(Route route) {
        this.routes.add(route);
        return this;
    }

    /**
     * Configure a list of route summaries
     *
     * @param routes a list of route summaries
     * @return {@code this}
     */
    @JsonProperty("routes")
    public final SpaceApplicationSummary withRoutes(List<Route> routes) {
        this.routes.addAll(routes);
        return this;
    }

    /**
     * Returns the number of running instances
     *
     * @return the number of running instances
     */
    public final Integer getRunningInstances() {
        return runningInstances;
    }

    /**
     * Configure the number of running instances
     *
     * @param runningInstances the number of running instances
     * @return {@code this}
     */
    @JsonProperty("running_instances")
    public final SpaceApplicationSummary withRunningInstances(Integer runningInstances) {
        this.runningInstances = runningInstances;
        return this;
    }

    /**
     * Returns the number of services
     *
     * @return the number of services
     */
    public final Integer getServiceCount() {
        return serviceCount;
    }

    /**
     * Configure the number of services
     *
     * @param serviceCount the number of services
     * @return {@code this}
     */
    @JsonProperty("service_count")
    public final SpaceApplicationSummary withServiceCount(Integer serviceCount) {
        this.serviceCount = serviceCount;
        return this;
    }

    /**
     * Returns the list of service names
     *
     * @return the list of service names
     */
    public final List<String> getServiceNames() {
        return serviceNames;
    }

    /**
     * Configure a service name
     *
     * @param serviceName a service name
     * @return {@code this}
     */
    public final SpaceApplicationSummary withServiceName(String serviceName) {
        this.serviceNames.add(serviceName);
        return this;
    }

    /**
     * Configure a list of service names
     *
     * @param serviceNames a list of service names
     * @return {@code this}
     */
    @JsonProperty("service_names")
    public final SpaceApplicationSummary withServiceNames(List<String> serviceNames) {
        this.serviceNames.addAll(serviceNames);
        return this;
    }

    /**
     * Returns the list of URLs
     *
     * @return the list of URLs
     */
    public final List<String> getUrls() {
        return urls;
    }

    /**
     * Configure a URL
     *
     * @param url a URL
     * @return {@code this}
     */
    public final SpaceApplicationSummary withUrl(String url) {
        this.urls.add(url);
        return this;
    }

    /**
     * Configure a list of URLs
     *
     * @param urls a list of URLs
     * @return {@code this}
     */
    @JsonProperty("urls")
    public final SpaceApplicationSummary withUrls(List<String> urls) {
        this.urls.addAll(urls);
        return this;
    }

    public final static class Route {

        private volatile Domain domain;

        private volatile String host;

        private volatile String id; //guid

        /**
         * Returns domain summary
         *
         * @return domain summary
         */
        public final Domain getDomain() {
            return domain;
        }

        /**
         * Configure domain summary
         *
         * @param domain domain summary
         * @return {@code this}
         */
        @JsonProperty("domain")
        public final Route withDomain(Domain domain) {
            this.domain = domain;
            return this;
        }

        /**
         * Returns host
         *
         * @return host
         */
        public final String getHost() {
            return host;
        }

        /**
         * Configure host
         *
         * @param host host
         * @return {@code this}
         */
        @JsonProperty("host")
        public final Route withHost(String host) {
            this.host = host;
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
        public final Route withId(String id) {
            this.id = id;
            return this;
        }

        public final static class Domain {

            private volatile String id; //guid

            private volatile String name;

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
            public final Domain withId(String id) {
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
            public final Domain withName(String name) {
                this.name = name;
                return this;
            }
        }
    }
}
