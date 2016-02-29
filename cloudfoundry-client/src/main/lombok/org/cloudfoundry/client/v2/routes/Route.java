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

package org.cloudfoundry.client.v2.routes;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.cloudfoundry.client.v2.domains.Domain;

/**
 * A route bound to an application
 */
@Data
public final class Route {

    /**
     * The domain
     *
     * @param domain the domain
     * @return the domain
     */
    private final Domain domain;

    /**
     * The host
     *
     * @param host the host
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
     * The path
     *
     * @param path the path
     * @return the path
     */
    private final String path;

    @Builder
    Route(@JsonProperty("domain") Domain domain,
          @JsonProperty("host") String host,
          @JsonProperty("guid") String id,
          @JsonProperty("path") String path) {
        this.domain = domain;
        this.host = host;
        this.id = id;
        this.path = path;
    }

}
