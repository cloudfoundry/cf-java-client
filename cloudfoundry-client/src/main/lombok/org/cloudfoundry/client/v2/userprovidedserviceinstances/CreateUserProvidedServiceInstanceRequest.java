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

package org.cloudfoundry.client.v2.userprovidedserviceinstances;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

/**
 * The request payload for the Create User Provided Service Instance
 */
public final class CreateUserProvidedServiceInstanceRequest implements Validatable {


    /**
     * Key/value pairs that can be stored to store credentials
     *
     * @return the credentials
     */
    @Getter(onMethod = @__({@JsonProperty("credentials"), @JsonInclude(NON_EMPTY)}))
    private final Map<String, Object> credentials;

    /**
     * The name
     *
     * @param name the name
     * @return the name
     */
    @Getter(onMethod = @__(@JsonProperty("name")))
    private final String name;

    /**
     * The space id
     *
     * @param spaceId the space id
     * @return the space id
     */
    @Getter(onMethod = @__(@JsonProperty("space_guid")))
    private final String spaceId;

    /**
     * The url for the syslog_drain to direct to
     *
     * @param syslogDrainUrl the syslog drain url
     * @return the syslog drain url
     */
    @Getter(onMethod = @__(@JsonProperty("syslog_drain_url")))
    private final String syslogDrainUrl;

    @Builder
    CreateUserProvidedServiceInstanceRequest(@Singular Map<String, Object> credentials,
                                             String name,
                                             String spaceId,
                                             String syslogDrainUrl) {
        this.credentials = credentials;
        this.name = name;
        this.spaceId = spaceId;
        this.syslogDrainUrl = syslogDrainUrl;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.name == null) {
            builder.message("name must be specified");
        }

        if (this.spaceId == null) {
            builder.message("space id must be specified");
        }

        return builder.build();
    }

}
