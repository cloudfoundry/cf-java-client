/*
 * Copyright 2013-2021 the original author or authors.
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

package org.cloudfoundry.client.v3.roles;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.organizations.OrganizationResource;
import org.cloudfoundry.client.v3.spaces.SpaceResource;
import org.cloudfoundry.client.v3.users.UserResource;
import org.immutables.value.Value;

import java.util.List;


@JsonDeserialize
@Value.Immutable
public abstract class _RoleIncluded {
    @JsonProperty("users")
    @Nullable
    public abstract List<UserResource> getUsers();

    @JsonProperty("spaces")
    @Nullable
    public abstract List<SpaceResource> getSpaces();

    @JsonProperty("organizations")
    @Nullable
    public abstract List<OrganizationResource> getOrganizations();
}
