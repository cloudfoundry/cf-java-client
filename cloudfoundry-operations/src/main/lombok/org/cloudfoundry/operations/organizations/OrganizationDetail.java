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

package org.cloudfoundry.operations.organizations;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import org.cloudfoundry.operations.spacequotas.SpaceQuota;

import java.util.List;

/**
 * A Cloud Foundry Organization
 */
@Data
public final class OrganizationDetail {

    /**
     * The domains bound to this organization.
     *
     * @param domains the organizations domains
     * @return the domain names
     */
    private final List<String> domains;

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
     * The organization quota
     *
     * @param quota the space quota
     * @return the space quota
     */
    private final OrganizationQuota quota;

    /**
     * The spaces in this organization.
     *
     * @param spaces the organizations spaces
     * @return the spaces names
     */
    private final List<String> spaces;

    /**
     * The space quota of the organizations quota
     *
     * @param spaceQuota the space quota
     * @return the space quota
     */
    private final List<SpaceQuota> spacesQuotas;

    @Builder
    private OrganizationDetail(@Singular List<String> domains,
                               String id,
                               String name,
                               OrganizationQuota quota,
                               @Singular List<SpaceQuota> spacesQuotas,
                               @Singular List<String> spaces) {
        this.domains = domains;
        this.id = id;
        this.name = name;
        this.quota = quota;
        this.spacesQuotas = spacesQuotas;
        this.spaces = spaces;
    }

}
