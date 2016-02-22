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

package org.cloudfoundry.client.v2.routemappings;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * The entity response payload for the Route Mapping resource
 */
@Data
public final class RouteMappingEntity {

    /**
     * The application id
     *
     * @param applicationId the application id
     * @return the application id
     */
    private final String applicationId;

    /**
     * The application port
     *
     * @param applicationPort the application port
     * @return the application port
     */
    private final Integer applicationPort;

    /**
     * The application url
     *
     * @param applicationUrl the application url
     * @return the application url
     */
    private final String applicationUrl;

    /**
     * The route id
     *
     * @param routeId the route id
     * @return route id
     */
    private final String routeId;

    /**
     * The route url
     *
     * @param routeUrl the route url
     * @return the route url
     */
    private final String routeUrl;

    @Builder
    RouteMappingEntity(@JsonProperty("app_guid") String applicationId,
                       @JsonProperty("app_port") Integer applicationPort,
                       @JsonProperty("app_url") String applicationUrl,
                       @JsonProperty("route_guid") String routeId,
                       @JsonProperty("route_url") String routeUrl) {
        this.applicationId = applicationId;
        this.applicationPort = applicationPort;
        this.applicationUrl = applicationUrl;
        this.routeId = routeId;
        this.routeUrl = routeUrl;
    }

}
