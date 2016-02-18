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

package org.cloudfoundry.client.v2.servicebrokers;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

/**
 * The request payload for the Create Service Broker
 */
public final class CreateServiceBrokerRequest implements Validatable {

    /**
     * The password with which to authenticate against the service broker.
     *
     * @param authPassword the password for authentication
     * @return the password for authentication
     */
    @Getter(onMethod = @__(@JsonProperty("auth_password")))
    private final String authenticationPassword;

    /**
     * The username with which to authenticate against the service broker.
     *
     * @param authUsername the username for authentication
     * @return the username for authentication
     */
    @Getter(onMethod = @__(@JsonProperty("auth_username")))
    private final String authenticationUsername;

    /**
     * The url of the service broker.
     *
     * @param brokerUrl the broker url
     * @return the broker url
     */
    @Getter(onMethod = @__(@JsonProperty("broker_url")))
    private final String brokerUrl;

    /**
     * The name of the service broker.
     *
     * @param name the name
     * @return the name
     */
    @Getter(onMethod = @__(@JsonProperty("name")))
    private final String name;

    /**
     * (experimental) Guid of a space the broker is scoped to. Space developers are able to create service brokers scoped to a space.
     *
     * @param spaceId the space id
     * @return the space id
     */
    @Getter(onMethod = @__(@JsonProperty("space_guid")))
    private final String spaceId;

    @Builder
    CreateServiceBrokerRequest(String authenticationPassword,
                               String authenticationUsername,
                               String brokerUrl,
                               String name,
                               String spaceId) {
        this.authenticationPassword = authenticationPassword;
        this.authenticationUsername = authenticationUsername;
        this.brokerUrl = brokerUrl;
        this.name = name;
        this.spaceId = spaceId;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.name == null) {
            builder.message("name must be specified");
        }

        if (this.authenticationUsername == null) {
            builder.message("authentication username must be specified");
        }

        if (this.authenticationPassword == null) {
            builder.message("authentication password must be specified");
        }

        if (this.brokerUrl == null) {
            builder.message("broker url must be specified");
        }

        return builder.build();
    }

}
