/*
 * Copyright 2013-2020 the original author or authors.
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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

@JsonDeserialize
@Value.Immutable
abstract class _Buildpack {

    /**
     * The name reported by buildpack
     */
    @JsonProperty("buildpack_name")
    @Nullable
    abstract String getBuildpackName();

    /**
     * The output during buildpack detect process
     */
    @JsonProperty("detect_output")
    @Nullable
    abstract String getDetectOutput();

    /**
     * The system buildpack name
     */
    @JsonProperty("name")
    abstract String getName();

    /**
     * The version of the buildpack
     */
    @JsonProperty("version")
    @Nullable
    abstract String getVersion();

}
