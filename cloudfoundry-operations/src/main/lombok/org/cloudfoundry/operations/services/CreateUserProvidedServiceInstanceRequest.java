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
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

import java.util.Map;

/**
 * The request options for the create user provided service operation
 */
@Data
public final class CreateUserProvidedServiceInstanceRequest implements Validatable {

    /**
     * The credentials of the user provided service instance
     *
     * @param credentials the credentials
     * @return the credentials
     */
    private final Map<String, Object> credentials;

    /**
     * The name of the user provided service instance to create
     *
     * @param name the name of the user provided service instance
     * @return the name of the user provided service instance
     */
    private final String name;

    /**
     * URL to which requests for bound routes will be forwarded
     *
     * @param routeServiceUrl the route service url
     * @return the route service url
     */
    private final String routeServiceUrl;

    /**
     * The url for the Syslog Drain Url
     *
     * @param syslogDrainUrl the Syslog Drain Url
     * @return the Syslog Drain Url
     */
    private final String syslogDrainUrl;

    @Builder
    CreateUserProvidedServiceInstanceRequest(@Singular Map<String, Object> credentials,
                                             String name,
                                             String routeServiceUrl,
                                             String syslogDrainUrl) {
        this.credentials = credentials;
        this.name = name;
        this.routeServiceUrl = routeServiceUrl;
        this.syslogDrainUrl = syslogDrainUrl;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.name == null) {
            builder.message("name must be specified");
        }

        return builder.build();
    }

}
