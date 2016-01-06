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

package org.cloudfoundry.client.v2.organizations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

/**
 * The response payload for the Organization summary operation
 */
@Data
public final class SummaryOrganizationResponse {

    /**
     * The organization id
     *
     * @param id the organization id
     * @return the organization id
     */
    private final String id;

    /**
     * The organization name
     *
     * @param name the organization name
     * @return the organization name
     */
    private final String name;

    /**
     * List of spaces that are in the organization
     *
     * @param spaces the list of spaces that are in the organization
     * @return the list of spaces that are in the organization
     */
    private final List<OrganizationSpaceSummary> spaces;

    /**
     * The organization status
     *
     * @param status the organization status
     * @return the organization status
     */
    private final String status;


    @Builder
    SummaryOrganizationResponse(@JsonProperty("guid") String id,
                                @JsonProperty("name") String name,
                                @JsonProperty("spaces") @Singular List<OrganizationSpaceSummary> spaces,
                                @JsonProperty("status") String status) {
        this.id = id;
        this.name = name;
        this.spaces = spaces;
        this.status = status;
    }

}
