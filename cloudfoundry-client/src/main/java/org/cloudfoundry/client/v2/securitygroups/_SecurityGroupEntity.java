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

package org.cloudfoundry.client.v2.securitygroups;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

import java.util.List;

/**
 * The entity response payload for the Route resource
 */
@JsonDeserialize
@Value.Immutable
abstract class _SecurityGroupEntity {

    /**
     * The name
     */
    @JsonProperty("name")
    abstract String getName();

    /**
     * The rules
     */
    @JsonProperty("rules")
    abstract List<RuleEntity> getRules();

    /**
     * The running default
     */
    @JsonProperty("running_default")
    abstract Boolean getRunningDefault();

    /**
     * The spaces url
     */
    @JsonProperty("spaces_url")
    @Nullable
    abstract String getSpacesUrl();

    /**
     * The staging default
     */
    @JsonProperty("staging_default")
    abstract Boolean getStagingDefault();

}
