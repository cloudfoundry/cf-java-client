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

package org.cloudfoundry.client.v2.servicekeys;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Map;

/**
 * The entity response payload for Service Keys
 */
@Data
public final class ServiceKeyEntity {

    /**
     * The credentials
     *
     * @param credentials the credentials
     * @return the credentials
     */
    private final Map<String, Object> credentials;

    /**
     * The name
     *
     * @param name the name
     * @return the name
     */
    private final String name;

    /**
     * The service instance id
     *
     * @param serviceInstanceId the service instance id
     * @return the service instance id
     */
    private final String serviceInstanceId;

    /**
     * The service instance url
     *
     * @param serviceInstanceId the service instance url
     * @return the service instance url
     */
    private final String serviceInstanceUrl;

    @Builder
    ServiceKeyEntity(@JsonProperty("credentials") @Singular Map<String, Object> credentials,
                     @JsonProperty("name") String name,
                     @JsonProperty("service_instance_guid") String serviceInstanceId,
                     @JsonProperty("service_instance_url") String serviceInstanceUrl) {
        this.credentials = credentials;
        this.name = name;
        this.serviceInstanceId = serviceInstanceId;
        this.serviceInstanceUrl = serviceInstanceUrl;
    }

}
