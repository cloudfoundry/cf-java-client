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
import lombok.Data;

/**
 * The entity response payload for Identity Zone
 */
@Data
public abstract class AbstractIdentityZone {

    /**
     * The creation date of the identity zone.
     *
     * @param createdAt the creation date
     * @return the creation date
     */
    private final Long createdAt;

    /**
     * The description of the identity zone.
     *
     * @param description the description
     * @return the description
     */
    private final String description;

    /**
     * The id of the identity zone.
     *
     * @param identityZoneId the identity zone id
     * @return the identity zone id
     */
    private final String identityZoneId;

    /**
     * The name of the identity zone.
     *
     * @param name the name
     * @return the name
     */
    private final String name;

    /**
     * The unique sub domain. It will be converted into lowercase upon creation.
     *
     * @param subDomain the sub domain
     * @return the sub domain
     */
    private final String subDomain;

    /**
     * The last modification date of the identity zone.
     *
     * @param updatedAt the last modification date
     * @return the last modification date
     */
    private final Long updatedAt;

    /**
     * The version of the identity zone.
     *
     * @param version the version
     * @return the version
     */
    private final Integer version;

    AbstractIdentityZone(@JsonProperty("created") Long createdAt,
                         @JsonProperty("description") String description,
                         @JsonProperty("id") String identityZoneId,
                         @JsonProperty("name") String name,
                         @JsonProperty("subdomain") String subDomain,
                         @JsonProperty("last_modified") Long updatedAt,
                         @JsonProperty("version") Integer version) {
        this.createdAt = createdAt;
        this.description = description;
        this.identityZoneId = identityZoneId;
        this.name = name;
        this.subDomain = subDomain;
        this.updatedAt = updatedAt;
        this.version = version;
    }

}
