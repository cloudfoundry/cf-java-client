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

package org.cloudfoundry.client.v2.servicebrokers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * The entity response payload for Service Broker
 */
@JsonDeserialize
@Value.Immutable
abstract class _ServiceBrokerEntity {

    /**
     * The authentication username
     */
    @JsonProperty("auth_username")
    @Nullable
    abstract String getAuthenticationUsername();

    /**
     * The url of the service broker.
     */
    @JsonProperty("broker_url")
    @Nullable
    abstract String getBrokerUrl();

    /**
     * The name
     */
    @JsonProperty("name")
    @Nullable
    abstract String getName();

    /**
     * The space id
     */
    @JsonProperty("space_guid")
    @Nullable
    abstract String getSpaceId();

}
