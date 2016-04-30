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

package org.cloudfoundry.operations.services;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Map;

/**
 * A service key
 */
@Data
public final class ServiceKey {

    /**
     * The credentials
     *
     * @param credentials the credentials
     * @return the credentials
     */
    private final Map<String, Object> credentials;


    /**
     * The service key id
     *
     * @param serviceKeyId the service key id
     * @return the service key id
     */
    private final String serviceKeyId;

    @Builder
    ServiceKey(@Singular Map<String, Object> credentials,
               String serviceKeyId) {
        this.credentials = credentials;
        this.serviceKeyId = serviceKeyId;
    }

}
