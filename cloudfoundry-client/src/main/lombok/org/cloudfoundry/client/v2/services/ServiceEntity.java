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

package org.cloudfoundry.client.v2.services;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Singular;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * The entity response payload for Services
 */
@Data
public final class ServiceEntity {

    /**
     * The active status
     *
     * @param active service can be provisioned
     * @return active status
     */
    private final Boolean active;

    /**
     * The bindable status
     *
     * @param bindable the service can be bound
     * @return bindable status
     */
    private final Boolean bindable;

    /**
     * The description
     *
     * @param description the description
     * @return the description
     */
    private final String description;

    /**
     * The documentation url
     *
     * @param documentationUrl the documentation url
     * @return the documentation url
     */
    @Getter(onMethod = @__(@Deprecated))
    private final String documentationUrl;

    /**
     * Extra service-related data
     *
     * @param extra the extra data
     * @return the extra data
     */
    private final String extra;

    /**
     * The info url
     *
     * @param infoUrl the info url
     * @return the info url
     */
    @Getter(onMethod = @__(@Deprecated))
    private final String infoUrl;

    /**
     * The name of the service
     *
     * @param label the label
     * @return the label
     */
    private final String label;

    /**
     * The long description
     *
     * @param longDescription the long description
     * @return the long description
     */
    @Getter(onMethod = @__(@Deprecated))
    private final String longDescription;

    /**
     * Whether the service can be updated to a different plan
     *
     * @param planUpdateable the plan updateable status
     * @return the plan updateable status
     */
    private final Boolean planUpdateable;

    /**
     * The name of the service provider
     *
     * @param provider the provider
     * @return the provider
     */
    @Getter(onMethod = @__(@Deprecated))
    private final String provider;

    /**
     * Required dependencies
     *
     * @param requires the required dependencies
     * @return the required dependencies
     */
    private final List<String> requires;

    /**
     * The service broker id
     *
     * @param serviceBrokerId the service broker id
     * @return the service broker id
     */
    private final String serviceBrokerId;

    /**
     * The service plans url
     *
     * @param servicePlansUrl the service plans url
     * @return the service plans url
     */
    private final String servicePlansUrl;

    /**
     * The tags
     *
     * @param tags the tags
     * @return the tags
     */
    private final List<String> tags;

    /**
     * The unique id to identify the service with the broker
     *
     * @param uniqueId the unique id
     * @return the unique id
     */
    private final String uniqueId;

    /**
     * The url
     *
     * @param url the url
     * @return the url
     */
    @Getter(onMethod = @__(@Deprecated))
    private final String url;

    /**
     * The version
     *
     * @param version the version
     * @return the version
     */
    @Getter(onMethod = @__(@Deprecated))
    private final String version;

    @Builder
    ServiceEntity(@JsonProperty("active") Boolean active,
                  @JsonProperty("bindable") Boolean bindable,
                  @JsonProperty("description") String description,
                  @JsonProperty("documentation_url") @Deprecated String documentationUrl,
                  @JsonProperty("extra") String extra,
                  @JsonProperty("info_url") @Deprecated String infoUrl,
                  @JsonProperty("label") String label,
                  @JsonProperty("long_description") @Deprecated String longDescription,
                  @JsonProperty("plan_updateable") Boolean planUpdateable,
                  @JsonProperty("provider") @Deprecated String provider,
                  @JsonProperty("requires") @Singular List<String> requires,
                  @JsonProperty("service_broker_guid") String serviceBrokerId,
                  @JsonProperty("service_plans_url") String servicePlansUrl,
                  @JsonProperty("tags") @Singular List<String> tags,
                  @JsonProperty("unique_id") String uniqueId,
                  @JsonProperty("url") @Deprecated String url,
                  @JsonProperty("version") @Deprecated String version) {
        this.active = active;
        this.bindable = bindable;
        this.description = description;
        this.documentationUrl = documentationUrl;
        this.extra = extra;
        this.infoUrl = infoUrl;
        this.label = label;
        this.longDescription = longDescription;
        this.planUpdateable = planUpdateable;
        this.provider = provider;
        this.requires = Optional.ofNullable(requires).orElse(Collections.emptyList());
        this.serviceBrokerId = serviceBrokerId;
        this.servicePlansUrl = servicePlansUrl;
        this.tags = Optional.ofNullable(tags).orElse(Collections.emptyList());
        this.uniqueId = uniqueId;
        this.url = url;
        this.version = version;
    }

}
