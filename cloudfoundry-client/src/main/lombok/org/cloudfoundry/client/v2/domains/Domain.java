/*
 * Copyright 2013-2015 the original author or authors.
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

    @Builder
    Domain(@JsonProperty("guid") String id,
           @JsonProperty("name") String name,
           @JsonProperty("owning_organization_guid") String owningOrganizationId) {
        this.id = id;
        this.name = name;
        this.owningOrganizationId = owningOrganizationId;
    }

}
