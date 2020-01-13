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

package org.cloudfoundry.servicebroker.catalog;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.servicebroker.Nullable;
import org.immutables.value.Value;

@Value.Immutable
abstract class _Plan {

    @JsonProperty("description")
    abstract String getDescription();

    @JsonProperty("id")
    abstract String getId();

    @JsonProperty("max_storage_tb")
    @Nullable
    abstract Integer getMaxStorageTb();

    @JsonProperty("metadata")
    abstract PlanMetadata getMetadata();

    @JsonProperty("name")
    abstract String getName();

}
