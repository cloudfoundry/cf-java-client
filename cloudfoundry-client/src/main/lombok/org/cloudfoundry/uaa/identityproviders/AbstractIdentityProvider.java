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

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * The entity response payload for Identity Provider
 */
@Data
public abstract class AbstractIdentityProvider {

    /**
     * The active flag
     *
     * @param active the active flag
     * @return the active flag
     */
    private final Boolean active;

    /**
     * The config of the identity provider in a JSON format.
     *
     * @param config the config
     * @return the config
     */
    private final String config;

    /**
     * The creation date of the identity zone provider.
     *
     * @param createdAt the creation date
     * @return the creation date
     */
    private final Long createdAt;

    /**
     * The id of the identity provider.
     *
     * @param id the id
     * @return the id
     */
    private final String id;

    /**
     * The id of the zone for this identity provider.
     *
     * @param identityZoneId the identity zone id
     * @return the identity zone id
     */
    private final String identityZoneId;

    /**
     * The name of the identity provider.
     *
     * @param name the name
     * @return the name
     */
    private final String name;

    /**
     * The origin key of the identity provider.
     *
     * @param originKey the origin key
     * @return the origin key
     */
    private final String originKey;

    /**
     * The type of the identity provider. Must be either "saml", "ldap" or "internal".
     *
     * @param type the type
     * @return the type
     */
    private final String type;

    /**
     * The last modification date of the identity zone provider.
     *
     * @param updatedAt the last modification date
     * @return the last modification date
     */
    private final Long updatedAt;

    /**
     * The version of the identity zone provider.
     *
     * @param version the version
     * @return the version
     */
    private final Integer version;

    AbstractIdentityProvider(@JsonProperty("active") Boolean active,
                             @JsonProperty("config") String config,
                             @JsonProperty("created") Long createdAt,
                             @JsonProperty("id") String id,
                             @JsonProperty("identityZoneId") String identityZoneId,
                             @JsonProperty("name") String name,
                             @JsonProperty("originKey") String originKey,
                             @JsonProperty("type") String type,
                             @JsonProperty("last_modified") Long updatedAt,
                             @JsonProperty("version") Integer version) {
        this.active = active;
        this.config = config;
        this.createdAt = createdAt;
        this.id = id;
        this.identityZoneId = identityZoneId;
        this.name = name;
        this.originKey = originKey;
        this.type = type;
        this.updatedAt = updatedAt;
        this.version = version;
    }

}
