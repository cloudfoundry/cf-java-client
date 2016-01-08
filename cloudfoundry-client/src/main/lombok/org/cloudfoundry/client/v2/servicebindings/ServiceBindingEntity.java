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
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Map;

/**
 * The entity response payload for the Service Binding resource
 */
@Data
public final class ServiceBindingEntity {

    private final String applicationId;

    private final String applicationUrl;

    private final Map<String, Object> bindingOptions;

    private final Map<String, Object> credentials;

    private final Map<String, Object> gatewayData;

    private final String gatewayName;

    private final String serviceInstanceId;

    private final String serviceInstanceUrl;

    private final String syslogDrainUrl;

    @Builder
    ServiceBindingEntity(@JsonProperty("app_guid") String applicationId,
                         @JsonProperty("app_url") String applicationUrl,
                         @JsonProperty("binding_options") @Singular Map<String, Object> bindingOptions,
                         @JsonProperty("credentials") @Singular Map<String, Object> credentials,
                         @JsonProperty("gateway_data") Map<String, Object> gatewayData,
                         @JsonProperty("gateway_name") String gatewayName,
                         @JsonProperty("service_instance_guid") String serviceInstanceId,
                         @JsonProperty("service_instance_url") String serviceInstanceUrl,
                         @JsonProperty("syslog_drain_url") String syslogDrainUrl) {

        this.applicationId = applicationId;
        this.applicationUrl = applicationUrl;
        this.bindingOptions = bindingOptions;
        this.credentials = credentials;
        this.gatewayData = gatewayData;
        this.gatewayName = gatewayName;
        this.serviceInstanceId = serviceInstanceId;
        this.serviceInstanceUrl = serviceInstanceUrl;
        this.syslogDrainUrl = syslogDrainUrl;
    }
}
