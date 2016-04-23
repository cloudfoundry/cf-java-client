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

package org.cloudfoundry.uaa.identityproviders;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.cloudfoundry.RequestHeader;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

/**
 * The request payload for the create identity provider operation
 */
@Data
public final class CreateIdentityProviderRequest implements Validatable {

    /**
     * The active flag
     *
     * @param active the active flag
     * @return the active flag
     */
    @Getter(onMethod = @__(@JsonProperty("active")))
    private final Boolean active;

    /**
     * The config of the identity provider in a JSON format.
     *
     * @param config the config
     * @return the config
     */
    @Getter(onMethod = @__(@JsonProperty("config")))
    private final String config;

    /**
     * The identity zone id
     *
     * @param identityZoneId the identity zone id
     * @return the identity zone id
     */
    @Getter(onMethod = @__(@RequestHeader("X-Identity-Zone-Id")))
    private final String identityZoneId;

    /**
     * The name of the identity provider.
     *
     * @param name the name
     * @return the name
     */
    @Getter(onMethod = @__(@JsonProperty("name")))
    private final String name;

    /**
     * The origin key of the identity provider.
     *
     * @param originKey the origin key
     * @return the origin key
     */
    @Getter(onMethod = @__(@JsonProperty("originKey")))
    private final String originKey;

    /**
     * The type of the identity provider. Must be either "saml", "ldap" or "internal".
     *
     * @param type the type
     * @return the type
     */
    @Getter(onMethod = @__(@JsonProperty("type")))
    private final String type;

    /**
     * The version of the identity zone provider.
     *
     * @param version the version
     * @return the version
     */
    @Getter(onMethod = @__(@JsonProperty("version")))
    private final Integer version;

    @Builder
    CreateIdentityProviderRequest(Boolean active,
                                  String config,
                                  String identityZoneId,
                                  String name,
                                  String originKey,
                                  String type,
                                  Integer version) {
        this.active = active;
        this.config = config;
        this.identityZoneId = identityZoneId;
        this.name = name;
        this.originKey = originKey;
        this.type = type;
        this.version = version;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.identityZoneId == null) {
            builder.message("identity zone id must be specified");
        }

        if (this.name == null) {
            builder.message("name must be specified");
        }

        if (this.originKey == null) {
            builder.message("origin key must be specified");
        }

        if (this.type == null) {
            builder.message("type must be specified");
        }

        return builder.build();
    }

}
