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

package org.cloudfoundry.client.v2.servicekeys;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.AllowNulls;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

import java.util.Map;

/**
 * The entity response payload for Service Keys
 */
@JsonDeserialize
@Value.Immutable
abstract class _ServiceKeyEntity {

    /**
     * The credentials
     */
    @AllowNulls
    @JsonProperty("credentials")
    @Nullable
    abstract Map<String, Object> getCredentials();

    /**
     * The name
     */
    @JsonProperty("name")
    @Nullable
    abstract String getName();

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
     * The service key parameters url
     */
    @JsonProperty("service_key_parameters_url")
    @Nullable
    abstract String getServiceKeyParametersUrl();

}
