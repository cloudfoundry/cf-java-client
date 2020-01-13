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

package org.cloudfoundry.client.v3;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.AllowNulls;
import org.cloudfoundry.Nullable;

import java.util.Map;

/**
 * A base class for all response types that are relationships
 */
public abstract class RelationshipResponse {

    /**
     * The relationship
     */
    @JsonProperty("data")
    @Nullable
    public abstract Relationship getData();

    /**
     * The links
     */
    @AllowNulls
    @JsonProperty("links")
    public abstract Map<String, Link> getLinks();

}
