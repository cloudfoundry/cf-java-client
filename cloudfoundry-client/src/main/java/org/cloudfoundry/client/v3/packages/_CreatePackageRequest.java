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

package org.cloudfoundry.client.v3.packages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.Data;
import org.immutables.value.Value;

/**
 * The request payload for the Create Package operation
 */
@Value.Immutable
abstract class _CreatePackageRequest {

    /**
     * The application id
     */
    @JsonIgnore
    abstract String getApplicationId();

    /**
     * The datas
     */
    @JsonProperty("data")
    @Nullable
    abstract Data getData();

    /**
     * The type
     */
    @JsonProperty("type")
    abstract PackageType getType();

}
