/*
 * Copyright 2013-2020 the original author or authors.
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
import org.cloudfoundry.AllowNulls;
import org.cloudfoundry.Nullable;

import java.util.List;
import java.util.Map;

/**
 * The entity response payload for both types of Service Instances
 */
public abstract class BaseServiceInstanceEntity {

    /**
     * The credentials
     */
    @AllowNulls
    @JsonProperty("credentials")
    @Nullable
    public abstract Map<String, Object> getCredentials();

    /**
     * The name
     */
    @JsonProperty("name")
    @Nullable
    public abstract String getName();

    /**
     * The routes url
     */
    @JsonProperty("routes_url")
    @Nullable
    public abstract String getRoutesUrl();

    /**
     * The service bindings url
     */
    @JsonProperty("service_bindings_url")
    @Nullable
    public abstract String getServiceBindingsUrl();

    /**
     * The shared from url
     */
    @JsonProperty("shared_from_url")
    @Nullable
    public abstract String getSharedFromUrl();

    /**
     * The shared to url
     */
    @JsonProperty("shared_to_url")
    @Nullable
    public abstract String getSharedToUrl();

    /**
     * The space id
     */
    @JsonProperty("space_guid")
    @Nullable
    public abstract String getSpaceId();

    /**
     * The space url
     */
    @JsonProperty("space_url")
    @Nullable
    public abstract String getSpaceUrl();

    /**
     * A list of tags for the service instance
     */
    @JsonProperty("tags")
    @Nullable
    public abstract List<String> getTags();

    /**
     * The type
     */
    @JsonProperty("type")
    @Nullable
    public abstract String getType();

}
