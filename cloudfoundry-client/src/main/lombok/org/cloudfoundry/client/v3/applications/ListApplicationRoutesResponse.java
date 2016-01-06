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

package org.cloudfoundry.client.v3.applications;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.PaginatedResponse;

import java.util.List;
import java.util.Map;

/**
 * The response payload for the List Application Routes operation
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class ListApplicationRoutesResponse extends PaginatedResponse<ListApplicationRoutesResponse.Resource> {

    @Builder
    ListApplicationRoutesResponse(@JsonProperty("pagination") Pagination pagination,
                                  @JsonProperty("resources") @Singular List<ListApplicationRoutesResponse.Resource> resources) {
        super(pagination, resources);
    }

    /**
     * The Resource response payload for the List Application Routes operation
     */
    @Data
    public static final class Resource {

        /**
         * The created at
         *
         * @param createdAt the created at
         * @return the created at
         */
        private final String createdAt;

        /**
         * The host
         *
         * @param the host
         * @return the host
         */
        private final String host;

        /**
         * The id
         *
         * @param id the id
         * @return the id
         */
        private final String id;

        /**
         * The links
         *
         * @return links the links
         * @return the links
         */
        private final Map<String, Link> links;

        /**
         * The path
         *
         * @param path the path
         * @return the path
         */
        private final String path;

        /**
         * The updated at
         *
         * @param updatedAt the updated at
         * @return the updated at
         */
        private final String updatedAt;

        @Builder
        Resource(@JsonProperty("created_at") String createdAt,
                 @JsonProperty("host") String host,
                 @JsonProperty("guid") String id,
                 @JsonProperty("_links") @Singular Map<String, Link> links,
                 @JsonProperty("path") String path,
                 @JsonProperty("updated_at") String updatedAt) {
            this.createdAt = createdAt;
            this.host = host;
            this.id = id;
            this.links = links;
            this.path = path;
            this.updatedAt = updatedAt;
        }

    }

}
