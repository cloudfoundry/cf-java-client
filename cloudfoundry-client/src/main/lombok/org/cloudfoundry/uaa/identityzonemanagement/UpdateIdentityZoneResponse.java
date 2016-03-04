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
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The response from the update identity zone request
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class UpdateIdentityZoneResponse extends AbstractIdentityZone {

    @Builder
    UpdateIdentityZoneResponse(@JsonProperty("created") Long createdAt,
                               @JsonProperty("description") String description,
                               @JsonProperty("id") String identityZoneId,
                               @JsonProperty("name") String name,
                               @JsonProperty("subdomain") String subdomain,
                               @JsonProperty("last_modified") Long updatedAt,
                               @JsonProperty("version") Integer version) {

        super(createdAt, description, identityZoneId, name, subdomain, updatedAt, version);
    }

}
