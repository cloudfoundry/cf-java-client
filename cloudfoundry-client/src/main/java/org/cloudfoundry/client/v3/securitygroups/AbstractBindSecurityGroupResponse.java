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
import java.util.Map;
import org.cloudfoundry.AllowNulls;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.Relationship;

@JsonDeserialize
public abstract class AbstractBindSecurityGroupResponse {

    /**
     * A relationship to the spaces where the security_group is applied to applications during
     * runtime
     */
    @JsonProperty("data")
    abstract List<Relationship> getBoundSpaces();

    /**
     * The links
     */
    @AllowNulls
    @JsonProperty("links")
    public abstract Map<String, Link> getLinks();
}
