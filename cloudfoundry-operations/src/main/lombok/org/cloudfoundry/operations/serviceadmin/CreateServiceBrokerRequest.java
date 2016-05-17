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

package org.cloudfoundry.operations.serviceadmin;

import lombok.Builder;
import lombok.Data;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

/**
 * Request options for the create service broker operation
 */
@Data
public class CreateServiceBrokerRequest implements Validatable {

    /**
     * Whether the service broker should be space scoped
     *
     * @param isSpaceScoped Whether the service broker should be space scoped
     * @return isSpaceScoped
     */
    private final Boolean isSpaceScoped;

    /**
     * The name of the service broker
     *
     * @param name the name
     * @return the name
     */
    private final String name;


    /**
     * The password to authenticate with the broker
     *
     * @param password the password
     * @return the password
     */
    private final String password;

    /**
     * The url of the service broker
     *
     * @param url the url
     * @return the url
     */
    private final String url;

    /**
     * The username to authenticate with the broker
     *
     * @param username the username
     * @return the username
     */
    private final String username;

    @Builder
    CreateServiceBrokerRequest(Boolean isSpaceScoped,
                               String name,
                               String password,
                               String url,
                               String username) {
        this.isSpaceScoped = isSpaceScoped;
        this.name = name;
        this.password = password;
        this.url = url;
        this.username = username;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.name == null) {
            builder.message("name must be specified");
        }

        if (this.username == null) {
            builder.message("username must be specified");
        }


        if (this.password == null) {
            builder.message("password must be specified");
        }

        if (this.url == null) {
            builder.message("url must be specified");
        }

        return builder.build();
    }
}
