/*
 * Copyright 2013-2025 the original author or authors.
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

package org.cloudfoundry.client.v3.spacequotas;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.ToManyRelationship;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.immutables.value.Value;

/**
 * The relationships for the Space Quota entity
 */

@Value.Immutable
@JsonDeserialize
abstract class _SpaceQuotaRelationships {

    /**
     * A relationship to the organization where the quota belongs
     */
    @JsonProperty("organization")
    abstract ToOneRelationship getOrganization();

    /**
     * A relationship to the spaces where the quota is applied
     */
    @JsonProperty("spaces")
    @Nullable
    abstract ToManyRelationship getSpaces();

}
