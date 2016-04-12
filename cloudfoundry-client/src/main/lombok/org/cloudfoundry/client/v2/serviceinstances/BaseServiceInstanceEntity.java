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

package org.cloudfoundry.client.v2.serviceinstances;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Singular;

import java.util.Map;

/**
 * The entity response payload for both types of Service Instances
 */
@Data
public abstract class BaseServiceInstanceEntity {

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
     * The type
     *
     * @param type the type
     * @return the type
     */
    private final String type;

    public BaseServiceInstanceEntity(@JsonProperty("credentials") @Singular Map<String, Object> credentials,
                                     @JsonProperty("name") String name,
                                     @JsonProperty("routes_url") String routesUrl,
                                     @JsonProperty("service_bindings_url") String serviceBindingsUrl,
                                     @JsonProperty("space_guid") String spaceId,
                                     @JsonProperty("space_url") String spaceUrl,
                                     @JsonProperty("type") String type) {
        this.credentials = credentials;
        this.name = name;
        this.routesUrl = routesUrl;
        this.serviceBindingsUrl = serviceBindingsUrl;
        this.spaceId = spaceId;
        this.spaceUrl = spaceUrl;
        this.type = type;
    }

}
