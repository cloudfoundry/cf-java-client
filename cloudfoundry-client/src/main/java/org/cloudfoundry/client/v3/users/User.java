/*
 * Copyright 2013-2025 the original author or authors.
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

package org.cloudfoundry.client.v3.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.Metadata;
import org.cloudfoundry.client.v3.Resource;

/**
 * Base class for responses that are users
 */
public abstract class User extends Resource {

    /**
     * The name registered in UAA; will be null for UAA clients and non-UAA users
     */
    @JsonProperty("username")
    @Nullable
    public abstract String getUsername();

    /**
     * The name displayed for the user; for UAA users, this is the same as the username. For UAA clients, this is the UAA client ID
     */
    @JsonProperty("presentation_name")
    @Nullable
    public abstract String getPresentationName();

    /**
     * The identity provider for the UAA user; will be null for UAA clients
     */
    @JsonProperty("origin")
    @Nullable
    public abstract String getOrigin();

    /**
     * The metadata Labels and Annotations applied to the user
     */
    @JsonProperty("metadata")
    @Nullable
    public abstract Metadata getMetadata();
}
