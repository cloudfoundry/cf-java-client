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

package org.cloudfoundry.client.v2.services;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

import java.util.List;

/**
 * The entity response payload for Services
 */
@JsonDeserialize
@Value.Immutable
abstract class _ServiceEntity {

    /**
     * The active status
     */
    @JsonProperty("active")
    @Nullable
    abstract Boolean getActive();

    /**
     * Whether to allow context updates
     */
    @JsonProperty("allow_context_updates")
    @Nullable
    abstract Boolean getAllowContextUpdates();

    /**
     * The bindable status
     */
    @JsonProperty("bindable")
    @Nullable
    abstract Boolean getBindable();

    /**
     * The bindings retrievable status
     */
    @JsonProperty("bindings_retrievable")
    @Nullable
    abstract Boolean getBindingsRetrievable();

    /**
     * The description
     */
    @JsonProperty("description")
    @Nullable
    abstract String getDescription();

    /**
     * The documentation url
     */
    @Deprecated
    @JsonProperty("documentation_url")
    @Nullable
    abstract String getDocumentationUrl();

    /**
     * Extra service-related data
     */
    @JsonProperty("extra")
    @Nullable
    abstract String getExtra();

    /**
     * The info url
     */
    @Deprecated
    @JsonProperty("info_url")
    @Nullable
    abstract String getInfoUrl();

    /**
     * The instances retrievable status
     */
    @JsonProperty("instances_retrievable")
    @Nullable
    abstract Boolean getInstancesRetrievable();

    /**
     * The name of the service
     */
    @JsonProperty("label")
    @Nullable
    abstract String getLabel();

    /**
     * The long description
     */
    @Deprecated
    @JsonProperty("long_description")
    @Nullable
    abstract String getLongDescription();

    /**
     * Whether the service can be updated to a different plan
     */
    @JsonProperty("plan_updateable")
    @Nullable
    abstract Boolean getPlanUpdateable();

    /**
     * The name of the service provider
     */
    @Deprecated
    @JsonProperty("provider")
    @Nullable
    abstract String getProvider();

    /**
     * Required dependencies
     */
    @JsonProperty("requires")
    @Nullable
    abstract List<String> getRequires();

    /**
     * The service broker id
     */
    @JsonProperty("service_broker_guid")
    @Nullable
    abstract String getServiceBrokerId();

    /**
     * The service broker name
     */
    @JsonProperty("service_broker_name")
    @Nullable
    abstract String getServiceBrokerName();

    /**
     * The service plans url
     */
    @JsonProperty("service_plans_url")
    @Nullable
    abstract String getServicePlansUrl();

    /**
     * The tags
     */
    @JsonProperty("tags")
    @Nullable
    abstract List<String> getTags();

    /**
     * The unique id to identify the service with the broker
     */
    @JsonProperty("unique_id")
    @Nullable
    abstract String getUniqueId();

    /**
     * The url
     */
    @Deprecated
    @JsonProperty("url")
    @Nullable
    abstract String getUrl();

    /**
     * The version
     */
    @Deprecated
    @JsonProperty("version")
    @Nullable
    abstract String getVersion();

}
