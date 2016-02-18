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
import lombok.Getter;
import org.cloudfoundry.QueryParameter;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

/**
 * The request payload for the Creating a Route operation
 */
@Data
public final class CreateRouteRequest implements Validatable {

    /**
     * The domain id
     *
     * @param domainId the domain id
     * @return the domain id
     */
    @Getter(onMethod = @__(@JsonProperty("domain_guid")))
    private final String domainId;

    /**
     * The generate port
     *
     * @param generatePort the generate port
     * @return the generate port
     */
    @Getter(onMethod = @__(@QueryParameter("generate_port")))
    private final String generatePort;

    /**
     * The host
     *
     * @param host the host
     * @return the host
     */
    @Getter(onMethod = @__(@JsonProperty("host")))
    private final String host;

    /**
     * The path
     *
     * @param path the path
     * @return the path
     */
    @Getter(onMethod = @__(@JsonProperty("path")))
    private final String path;

    /**
     * The port
     *
     * @param port the port
     * @return the port
     */
    @Getter(onMethod = @__(@JsonProperty("port")))
    private final Integer port;

    /**
     * The space id
     *
     * @param spaceId the space id
     * @return the space id
     */
    @Getter(onMethod = @__(@JsonProperty("space_guid")))
    private final String spaceId;

    @Builder
    CreateRouteRequest(String domainId, String generatePort, String host, String path, Integer port, String spaceId) {
        this.domainId = domainId;
        this.generatePort = generatePort;
        this.host = host;
        this.path = path;
        this.port = port;
        this.spaceId = spaceId;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.domainId == null) {
            builder.message("domain id must be specified");
        }

        if (this.spaceId == null) {
            builder.message("space id must be specified");
        }

        return builder.build();
    }

}
