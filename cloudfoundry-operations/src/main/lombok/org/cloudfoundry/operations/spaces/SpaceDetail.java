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

package org.cloudfoundry.operations.spaces;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import org.cloudfoundry.operations.spacequotas.SpaceQuota;
import org.cloudfoundry.util.Optional;

import java.util.List;

/**
 * A Cloud Foundry Space Detail
 */
@Data
public final class SpaceDetail {

    /**
     * The applications
     *
     * @param applications the applications
     * @return the applications
     */
    private final List<String> applications;

    /**
     * The domains
     *
     * @param domains the domains
     * @return the domains
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
     * The organization
     *
     * @param organization the organization
     * @return the organization
     */
    private final String organization;

    /**
     * The security groups
     *
     * @param securityGroups the security groups
     * @return the security groups
     */
    private final List<String> securityGroups;

    /**
     * The services
     *
     * @param services the services
     * @return the services
     */
    private final List<String> services;

    /**
     * The space quota
     *
     * @param spaceQuota the space quota
     * @return the space quota
     */
    private final Optional<SpaceQuota> spaceQuota;

    @Builder
    SpaceDetail(@Singular List<String> applications,
                @Singular List<String> domains,
                String id,
                String name,
                String organization,
                @Singular List<String> securityGroups,
                @Singular List<String> services,
                Optional<SpaceQuota> spaceQuota) {

        this.applications = applications;
        this.domains = domains;
        this.id = id;
        this.name = name;
        this.organization = organization;
        this.securityGroups = securityGroups;
        this.services = services;
        this.spaceQuota = spaceQuota;
    }

}
