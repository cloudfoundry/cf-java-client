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

package org.cloudfoundry.client.v2.routes;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * The entity response payload for the Route resource
 */
@Data
public final class RouteEntity {

    /**
     * The applications url
     *
     * @param applicationURL the application url
     * @return the application url
     */
    private final String applicationUrl;

    /**
     * The domain id
     *
     * @param domainId
     * @return domain Id
     */
    private final String domainId;

    /**
     * The domain url
     *
     * @param domainUrl the domain url
     * @return the domain url
     */
    private final String domainUrl;

    /**
     * The host
     *
     * @param host the host
     * @return the host
     */
    private final String host;

    /**
     * The path
     *
     * @param path the path
     * @return the path
     */
    private final String path;

    /**
     * The space id
     *
     * @param spaceId the space id
     * @return the space id
     */
    private final String spaceId;

    /**
     * The space url
     *
     * @param spaceURL the space url
     * @return the space url
     */
    private final String spaceUrl;

    @Builder
    RouteEntity(@JsonProperty("apps_url") String applicationUrl,
                @JsonProperty("domain_guid") String domainId,
                @JsonProperty("domain_url") String domainUrl,
                @JsonProperty("host") String host,
                @JsonProperty("path") String path,
                @JsonProperty("space_guid") String spaceId,
                @JsonProperty("space_url") String spaceUrl) {

        this.applicationUrl = applicationUrl;
        this.domainId = domainId;
        this.domainUrl = domainUrl;
        this.host = host;
        this.path = path;
        this.spaceId = spaceId;
        this.spaceUrl = spaceUrl;
    }
}