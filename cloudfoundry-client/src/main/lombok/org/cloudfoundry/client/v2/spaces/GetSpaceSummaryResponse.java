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

package org.cloudfoundry.client.v2.spaces;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstance;

import java.util.List;

/**
 * The response payload for the Get Space summary operation
 */
@Data
public final class GetSpaceSummaryResponse {

    private final List<SpaceApplicationSummary> applications;

    private final String id;

    private final String name;

    private final List<ServiceInstance> services;

    @Builder
    GetSpaceSummaryResponse(@JsonProperty("apps") @Singular List<SpaceApplicationSummary> applications,
                            @JsonProperty("guid") String id,
                            @JsonProperty("name") String name,
                            @JsonProperty("services") @Singular List<ServiceInstance> services) {
        this.applications = applications;
        this.id = id;
        this.name = name;
        this.services = services;
    }

}
