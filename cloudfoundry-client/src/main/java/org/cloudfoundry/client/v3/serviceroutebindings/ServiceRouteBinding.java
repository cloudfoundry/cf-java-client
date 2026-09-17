/*
 * Copyright 2013-2026 the original author or authors.
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

package org.cloudfoundry.client.v3.serviceroutebindings;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.LastOperation;
import org.cloudfoundry.client.v3.Metadata;
import org.cloudfoundry.client.v3.Resource;

/**
 * Base class for responses that are service route bindings
 *
 * see https://v3-apidocs.cloudfoundry.org/index.html#service-route-binding
 */
public abstract class ServiceRouteBinding extends Resource {

    /**
     * The last operation
     */
    @JsonProperty("last_operation")
    @Nullable
    public abstract LastOperation getLastOperation();

    /**
     * The metadata
     */
    @JsonProperty("metadata")
    @Nullable
    public abstract Metadata getMetadata();

    /**
     * The relationships
     */
    @JsonProperty("relationships")
    public abstract ServiceRouteBindingRelationships getRelationships();

    /**
     * The URL of the service that will intercept traffic to the route
     */
    @JsonProperty("route_service_url")
    @Nullable
    public abstract String getRouteServiceUrl();
}
