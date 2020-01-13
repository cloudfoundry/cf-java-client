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

package org.cloudfoundry.client.v2.servicebindings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.AllowNulls;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v2.serviceinstances.GatewayData;
import org.immutables.value.Value;

import java.util.List;
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
    @Nullable
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
    @AllowNulls
    @JsonProperty("binding_options")
    @Nullable
    abstract Map<String, Object> getBindingOptions();

    /**
     * The credentials
     */
    @AllowNulls
    @JsonProperty("credentials")
    @Nullable
    abstract Map<String, Object> getCredentials();

    /**
     * The gateway data
     */
    @Deprecated
    @JsonProperty("gateway_data")
    @Nullable
    abstract GatewayData getGatewayData();

    /**
     * The gateway name
     */
    @JsonProperty("gateway_name")
    @Nullable
    abstract String getGatewayName();

    /**
     * The last operation
     */
    @JsonProperty("last_operation")
    @Nullable
    abstract LastOperation getLastOperation();

    /**
     * The service binding name
     */
    @JsonProperty("name")
    @Nullable
    abstract String getName();

    /**
     * The service binding parameters url
     */
    @JsonProperty("service_binding_parameters_url")
    @Nullable
    abstract String getServiceBindingParametersUrl();

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

    /**
     * The volume mounts
     */
    @JsonProperty("volume_mounts")
    @Nullable
    abstract List<VolumeMounts> getVolumeMounts();

}
