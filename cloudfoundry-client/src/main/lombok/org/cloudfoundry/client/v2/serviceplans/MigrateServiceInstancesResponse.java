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

package org.cloudfoundry.client.v2.serviceplans;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The resource response payload for the Migrate Service Instances Response
 */
@Data
@EqualsAndHashCode
@ToString(callSuper = true)
public final class MigrateServiceInstancesResponse {

    /**
     * The number of resources changed
     *
     * @param resourcesChanged the number of resources changed
     * @return the number of resources changed
     */
    private final Integer resourcesChanged;

    @Builder
    MigrateServiceInstancesResponse(@JsonProperty("changed_count") Integer resourcesChanged) {
        this.resourcesChanged = resourcesChanged;
    }

}
