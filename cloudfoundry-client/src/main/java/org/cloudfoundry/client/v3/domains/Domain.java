/*
 * Copyright 2013-2019 the original author or authors.
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

package org.cloudfoundry.client.v3.domains;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.Metadata;
import org.cloudfoundry.client.v3.Resource;

public abstract class Domain extends Resource {

    /**
     * Metadata applied to the domain.
     */
    @JsonProperty("metadata")
    @Nullable
    public abstract Metadata getMetadata();

    /**
     * The name of the domain.
     * Must be between 3 ~ 253 characters and follow <a href="https://tools.ietf.org/html/rfc1035">RFC 1035</a>.
     */
    @JsonProperty("name")
    public abstract String getName();

    /**
     * Relationships of the domain.
     */
    @JsonProperty("relationships")
    public abstract DomainRelationships getRelationships();

    /**
     * Whether the domain is used for internal (container-to-container) traffic.
     */
    @JsonProperty("internal")
    public abstract boolean isInternal();

}
