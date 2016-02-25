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

package org.cloudfoundry.client.v2.userprovidedserviceinstances;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Map;

/**
 * The entity response payload for User Provided Service Instances
 */
@Data
public final class UserProvidedServiceInstanceEntity {

    /**
     * The credentials
     *
     * @param credentials the credentials
     * @return the credentials
     */
    private final Map<String, Object> credentials;

    /**
     * The name
     *
     * @param name the name
     * @return the name
     */
    private final String name;

    /**
     * URL to which requests for bound routes will be forwarded
     *
     * @param routeServiceUrl the route service url
     * @return the route service url
     */
    private final String routeServiceUrl;

    /**
     * The routes url
     *
     * @param routesUrl the routes url
     * @return the routes url
     */
    private final String routesUrl;

    /**
     * The service bindings url
     *
     * @param serviceBindingsUrl the service bindings url
     * @return the service bindings url
     */
    private final String serviceBindingsUrl;

    /**
     * The space id
     *
     * @param spaceId the space id
     * @return the space id
     */
    private final String spaceId;

    /**
     * The space url
     *
     * @param spaceUrl the space url
     * @return the space url
     */
    private final String spaceUrl;

    /**
     * The url for the syslog_drain to direct to
     *
     * @param syslogDrainUrl the syslog drain url
     * @return the syslog drain url
     */
    private final String syslogDrainUrl;

    /**
     * The type
     *
     * @param type the type
     * @return the type
     */
    private final String type;

    @Builder
    UserProvidedServiceInstanceEntity(@JsonProperty("credentials") @Singular Map<String, Object> credentials,
                                      @JsonProperty("name") String name,
                                      @JsonProperty("route_service_url") String routeServiceUrl,
                                      @JsonProperty("routes_url") String routesUrl,
                                      @JsonProperty("service_bindings_url") String serviceBindingsUrl,
                                      @JsonProperty("space_guid") String spaceId,
                                      @JsonProperty("space_url") String spaceUrl,
                                      @JsonProperty("syslog_drain_url") String syslogDrainUrl,
                                      @JsonProperty("type") String type) {
        this.credentials = credentials;
        this.name = name;
        this.routeServiceUrl = routeServiceUrl;
        this.routesUrl = routesUrl;
        this.serviceBindingsUrl = serviceBindingsUrl;
        this.spaceId = spaceId;
        this.spaceUrl = spaceUrl;
        this.syslogDrainUrl = syslogDrainUrl;
        this.type = type;
    }

}
