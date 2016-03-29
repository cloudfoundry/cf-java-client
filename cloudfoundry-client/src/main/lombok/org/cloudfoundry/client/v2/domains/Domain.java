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

package org.cloudfoundry.client.v2.domains;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

/**
 * The domain
 */
@Data
public final class Domain {

    /**
     * The id
     *
     * @param id the id
     * @return the id
     */
    private final String id;

    /**
     * The name
     *
     * @param name the name
     * @return the name
     */
    private final String name;

    /**
     * The owning organization id
     *
     * @param owningOrganizationId the owning organization id
     * @return the owning organization id
     */
    private final String owningOrganizationId;

    /**
     * The router group id
     *
     * @param routerGroupId the router group id
     * @return the router group id
     */
    private final String routerGroupId;

    /**
     * The router group types
     *
     * @param routerGroupTypes the router group types
     * @return the router group types
     */
    private final List<String> routerGroupTypes;

    @Builder
    Domain(@JsonProperty("guid") String id,
           @JsonProperty("name") String name,
           @JsonProperty("owning_organization_guid") String owningOrganizationId,
           @JsonProperty("router_group_guid") String routerGroupId,
           @JsonProperty("router_group_types") @Singular List<String> routerGroupTypes) {
        this.id = id;
        this.name = name;
        this.owningOrganizationId = owningOrganizationId;
        this.routerGroupId = routerGroupId;
        this.routerGroupTypes = routerGroupTypes;
    }

}
