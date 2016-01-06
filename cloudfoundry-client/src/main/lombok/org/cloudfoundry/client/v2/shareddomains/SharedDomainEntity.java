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

package org.cloudfoundry.client.v2.shareddomains;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * The entity response payload for the Domain resource
 */
@Data
public final class SharedDomainEntity {

    /**
     * The name
     *
     * @param name the name
     * @return the name
     */
    private final String name;

    /**
     * The router group id
     *
     * @param routerGroupId the router group id
     * @return the router group id
     */
    private final String routerGroupId;

    @Builder
    SharedDomainEntity(@JsonProperty("name") String name,
                       @JsonProperty("router_group_guid") String routerGroupId) {
        this.name = name;
        this.routerGroupId = routerGroupId;
    }
}
