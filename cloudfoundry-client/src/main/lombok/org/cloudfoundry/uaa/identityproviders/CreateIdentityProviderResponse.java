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
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The response from the create identity provider request
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class CreateIdentityProviderResponse extends AbstractIdentityProvider {

    @Builder
    CreateIdentityProviderResponse(@JsonProperty("active") Boolean active,
                                   @JsonProperty("config") String config,
                                   @JsonProperty("created") Long createdAt,
                                   @JsonProperty("id") String id,
                                   @JsonProperty("identityZoneId") String identityZoneId,
                                   @JsonProperty("name") String name,
                                   @JsonProperty("originKey") String originKey,
                                   @JsonProperty("type") String type,
                                   @JsonProperty("last_modified") Long updatedAt,
                                   @JsonProperty("version") Integer version) {
        super(active, config, createdAt, id, identityZoneId, name, originKey, type, updatedAt, version);
    }

}
