/*
 * Copyright 2013-2017 the original author or authors.
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

package org.cloudfoundry.bosh.deployments;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.List;

/**
 * The deployment
 */
@JsonDeserialize
@Value.Immutable
abstract class _Deployment {

    /**
     * The cloud config
     */
    @JsonProperty("cloud_config")
    abstract CloudConfig getCloudConfig();

    /**
     * The name
     */
    @JsonProperty("name")
    abstract String getName();

    /**
     * The releases
     */
    @JsonProperty("releases")
    abstract List<Release> getReleases();

    /**
     * The stemcells
     */
    @JsonProperty("stemcells")
    abstract List<Stemcell> getStemcells();

}
