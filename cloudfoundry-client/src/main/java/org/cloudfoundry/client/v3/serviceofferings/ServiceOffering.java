/*
 * Copyright 2013-2021 the original author or authors.
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

package org.cloudfoundry.client.v3.serviceofferings;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.Metadata;
import org.cloudfoundry.client.v3.Resource;

import java.util.List;

/**
 * Base class for responses that are service offerings
 */
public abstract class ServiceOffering extends Resource {

    /**
     * Whether the service offering is available
     */
    @JsonProperty("available")
    public abstract Boolean getAvailable();

    /**
     * The broker catalog
     */
    @JsonProperty("broker_catalog")
    public abstract BrokerCatalog getBrokerCatalog();

    /**
     * The description
     */
    @JsonProperty("description")
    public abstract String getDescription();

    /**
     * The documentation url
     */
    @JsonProperty("documentation_url")
    public abstract String getDocumentationUrl();

    /**
     * The metadata
     */
    @JsonProperty("metadata")
    @Nullable
    public abstract Metadata getMetadata();

    /**
     * The name
     */
    @JsonProperty("name")
    public abstract String getName();

    /**
     * The relationships
     */
    @JsonProperty("relationships")
    @Nullable
    public abstract ServiceOfferingRelationships getRelationships();

    /**
     * A list of permissions that the user would have to give the service
     */
    @JsonProperty("requires")
    @Nullable
    public abstract List<String> getRequires();

    /**
     * Whether or not service instances of this service offering can be shared across organizations and spaces
     */
    @JsonProperty("shareable")
    public abstract Boolean getShareable();

    /**
     * A list of tags for the service offering
     */
    @JsonProperty("tags")
    @Nullable
    public abstract List<String> getTags();

}
