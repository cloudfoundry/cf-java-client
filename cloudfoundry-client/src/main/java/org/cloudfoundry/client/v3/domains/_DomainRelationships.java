/*
 * Copyright 2013-2020 the original author or authors.
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

package org.cloudfoundry.client.v3.domains;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.ToManyRelationship;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.immutables.value.Value;

@JsonDeserialize
@Value.Immutable
abstract class _DomainRelationships {

    /**
     * The organization the domain is scoped to. If set, the domain will only be available in that organization.
     * Otherwise, the domain will be globally available.
     */
    @JsonProperty("organization")
    abstract ToOneRelationship getOrganization();

    /**
     * Organizations the domain is shared with. If set, the domain will be available in these organizations in addition
     * to the organization the domain is scoped to.
     */
    @JsonProperty("shared_organizations")
    @Nullable
    abstract ToManyRelationship getSharedOrganizations();

}
