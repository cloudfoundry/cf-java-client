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

package org.cloudfoundry.client.v2.servicebindings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

import java.util.Map;

/**
 * The entity response payload for the Service Binding resource
 */
@JsonDeserialize
@Value.Immutable
abstract class _ServiceBindingEntity {

    /**
     * The application id
     */
    @JsonProperty("app_guid")
    abstract String getApplicationId();

    /**
     * The application url
     */
    @JsonProperty("app_url")
    @Nullable
    abstract String getApplicationUrl();

    /**
     * The binding options
     */
    @JsonProperty("binding_options")
    abstract Map<String, Object> getBindingOptions();

    /**
     * The credentials
     */
    @JsonProperty("credentials")
    abstract Map<String, Object> getCredentials();

    /**
     * The gateway data
     */
    @JsonProperty("gateway_data")
    abstract Map<String, Object> getGatewayDatas();

    /**
     * The gateway name
     */
    @JsonProperty("gateway_name")
    @Nullable
    abstract String getGatewayName();

    /**
     * The service instance id
     */
    @JsonProperty("service_instance_guid")
    @Nullable
    abstract String getServiceInstanceId();

    /**
     * The service instance url
     */
    @JsonProperty("service_instance_url")
    @Nullable
    abstract String getServiceInstanceUrl();

    /**
     * The syslog drain url
     */
    @JsonProperty("syslog_drain_url")
    @Nullable
    abstract String getSyslogDrainUrl();

}
