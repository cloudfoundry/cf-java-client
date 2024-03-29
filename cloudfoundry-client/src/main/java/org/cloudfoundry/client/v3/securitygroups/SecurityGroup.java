/*
 * Copyright 2013-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.cloudfoundry.client.v3.securitygroups;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.cloudfoundry.client.v3.Resource;

/**
 * The entity response payload for the Security Group resource
 */
@JsonDeserialize
public abstract class SecurityGroup extends Resource {

    /**
     * The name
     */
    @JsonProperty("name")
    abstract String getName();

    /**
     * The globally enabled
     */
    @JsonProperty("globally_enabled")
    abstract GloballyEnabled getGloballyEnabled();

    /**
     * The rules
     */
    @JsonProperty("rules")
    abstract List<Rule> getRules();

    /**
     * The space relationships
     */
    @JsonProperty("relationships")
    abstract Relationships getRelationships();
}
