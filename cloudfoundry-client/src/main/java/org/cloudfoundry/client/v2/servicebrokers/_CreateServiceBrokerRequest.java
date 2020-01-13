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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * The request payload for the Create Service Broker
 */
@JsonSerialize
@Value.Immutable
abstract class _CreateServiceBrokerRequest {

    /**
     * The password with which to authenticate against the service broker.
     */
    @JsonProperty("auth_password")
    abstract String getAuthenticationPassword();

    /**
     * The username with which to authenticate against the service broker.
     */
    @JsonProperty("auth_username")
    abstract String getAuthenticationUsername();

    /**
     * The url of the service broker.
     */
    @JsonProperty("broker_url")
    abstract String getBrokerUrl();

    /**
     * The name of the service broker.
     */
    @JsonProperty("name")
    abstract String getName();

    /**
     * (experimental) Guid of a space the broker is scoped to. Space developers are able to create service brokers scoped to a space.
     */
    @JsonProperty("space_guid")
    @Nullable
    abstract String getSpaceId();

}
