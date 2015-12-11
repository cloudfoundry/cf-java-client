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

package org.cloudfoundry.client.v2.securitygroups;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

/**
 * The entity response payload for the Route resource
 */
@Data
public final class SecurityGroupEntity {

    /**
     * The name
     *
     * @param name the name
     * @return the name
     */
    private final String name;

    /**
     * The rules
     *
     * @param rules
     * @return rules
     */
    private final List<RuleEntity> rules;

    /**
     * The running default
     *
     * @param runningDefault the running default
     * @return the running default
     */
    private final Boolean runningDefault;

    /**
     * The spaces url
     *
     * @param spacesUrl the spaces url
     * @return the spaces url
     */
    private final String spacesUrl;

    /**
     * The staging default
     *
     * @param stagingDefault the staging default
     * @return the staging default
     */
    private final Boolean stagingDefault;

    @Builder
    SecurityGroupEntity(@JsonProperty("name") String name,
                        @JsonProperty("rules") @Singular List<RuleEntity> rules,
                        @JsonProperty("running_default") Boolean runningDefault,
                        @JsonProperty("spaces_url") String spacesUrl,
                        @JsonProperty("staging_default") Boolean stagingDefault) {

        this.name = name;
        this.rules = rules;
        this.runningDefault = runningDefault;
        this.spacesUrl = spacesUrl;
        this.stagingDefault = stagingDefault;
    }

    @Data
    public static final class RuleEntity {

        /**
         * The destination
         *
         * @param destination the destination
         * @return the destination
         */
        private final String destination;

        /**
         * The ports
         *
         * @param ports the ports
         * @return the ports
         */
        private final String ports;

        /**
         * The protocol
         *
         * @param protocol the protocol
         * @return the protocol
         */
        private final String protocol;

        @Builder
        RuleEntity(@JsonProperty("destination") String destination,
                   @JsonProperty("ports") String ports,
                   @JsonProperty("protocol") String protocol) {

            this.destination = destination;
            this.ports = ports;
            this.protocol = protocol;
        }

    }

}