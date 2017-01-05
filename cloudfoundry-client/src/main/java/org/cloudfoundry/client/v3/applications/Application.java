/*
 * Copyright 2013-2017 the original author or authors.
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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.AllowNulls;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.Lifecycle;
import org.cloudfoundry.client.v3.Link;

import java.util.Map;

/**
 * Base class for responses that are tasks
 */
public abstract class Application {

    /**
     * When the application was created
     */
    @JsonProperty("created_at")
    @Nullable
    public abstract String getCreatedAt();

    /**
     * The desired state
     */
    @JsonProperty("desired_state")
    @Nullable
    public abstract String getDesiredState();

    /**
     * The environment variables\
     */
    @AllowNulls
    @JsonProperty("environment_variables")
    @Nullable
    public abstract Map<String, String> getEnvironmentVariables();

    /**
     * The id
     */
    @JsonProperty("guid")
    @Nullable
    public abstract String getId();

    /**
     * The lifecycle
     */
    @JsonProperty("lifecycle")
    @Nullable
    public abstract Lifecycle getLifecycle();

    /**
     * The links
     */
    @AllowNulls
    @JsonProperty("links")
    @Nullable
    public abstract Map<String, Link> getLinks();

    /**
     * The name
     */
    @JsonProperty("name")
    @Nullable
    public abstract String getName();

    /**
     * The total desired instances
     */
    @JsonProperty("total_desired_instances")
    @Nullable
    public abstract Integer getTotalDesiredInstances();

    /**
     * When the application was updated
     */
    @JsonProperty("updated_at")
    @Nullable
    public abstract String getUpdatedAt();

}
