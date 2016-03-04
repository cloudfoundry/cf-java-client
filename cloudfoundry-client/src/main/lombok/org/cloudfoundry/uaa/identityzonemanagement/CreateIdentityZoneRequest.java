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

package org.cloudfoundry.uaa.identityzonemanagement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

/**
 * The request payload for the create identity zone operation
 */
@Data
public final class CreateIdentityZoneRequest implements Validatable {

    /**
     * The description of the identity zone.
     *
     * @param description the description
     * @return the description
     */
    @Getter(onMethod = @__(@JsonProperty("description")))
    private final String description;

    /**
     * The id of the identity zone. When not provided, an identifier will be generated
     *
     * @param identityZoneId the identity zone id
     * @return the identity zone id
     */
    @Getter(onMethod = @__(@JsonProperty("id")))
    private final String identityZoneId;

    /**
     * The name of the identity zone.
     *
     * @param name the name
     * @return the name
     */
    @Getter(onMethod = @__(@JsonProperty("name")))
    private final String name;

    /**
     * The unique subdomain. It will be converted into lowercase upon creation.
     *
     * @param subdomain the subdomain
     * @return the subdomain
     */
    @Getter(onMethod = @__(@JsonProperty("subdomain")))
    private final String subdomain;

    /**
     * The version of the identity zone.
     *
     * @param version the version
     * @return the version
     */
    @Getter(onMethod = @__(@JsonProperty("version")))
    private final Integer version;

    @Builder
    CreateIdentityZoneRequest(String description,
                              String identityZoneId,
                              String name,
                              String subdomain,
                              Integer version) {
        this.description = description;
        this.identityZoneId = identityZoneId;
        this.name = name;
        this.subdomain = subdomain;
        this.version = version;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.name == null) {
            builder.message("name must be specified");
        }

        if (this.subdomain == null) {
            builder.message("sub domain must be specified");
        }

        return builder.build();
    }

}
