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

package org.cloudfoundry.client.v3.droplets;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;
import org.cloudfoundry.client.v3.Lifecycle;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.PaginatedResponse;

import java.util.List;
import java.util.Map;

/**
 * The response payload for the List Applications operation
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class ListDropletsResponse extends PaginatedResponse<ListDropletsResponse.Resource> {

    @Builder
    ListDropletsResponse(@JsonProperty("pagination") Pagination pagination,
                         @JsonProperty("resources") @Singular List<ListDropletsResponse.Resource> resources) {
        super(pagination, resources);
    }

    /**
     * The Resource response payload for the List Applications operation
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static final class Resource extends Droplet {

        @Builder
        Resource(@JsonProperty("created_at") String createdAt,
                 @JsonProperty("disk_limit") Integer diskLimit,
                 @JsonProperty("environment_variables") @Singular Map<String, Object> environmentVariables,
                 @JsonProperty("error") String error,
                 @JsonProperty("lifecycle") Lifecycle lifecycle,
                 @JsonProperty("guid") String id,
                 @JsonProperty("links") @Singular Map<String, Link> links,
                 @JsonProperty("memory_limit") Integer memoryLimit,
                 @JsonProperty("result") @Singular Map<String, Object> results,
                 @JsonProperty("state") String state,
                 @JsonProperty("updated_at") String updatedAt) {
            super(createdAt, diskLimit, environmentVariables, error, lifecycle, id, links, memoryLimit, results, state, updatedAt);
        }

    }

}
