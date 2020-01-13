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

package org.cloudfoundry.client.v2.routes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.QueryParameter;
import org.immutables.value.Value;

/**
 * The request payload for the Check a Route exists operation
 */
@Value.Immutable
abstract class _RouteExistsRequest {

    /**
     * The domain id
     */
    @JsonIgnore
    abstract String getDomainId();

    /**
     * The host
     */
    @Nullable
    @JsonIgnore
    abstract String getHost();

    /**
     * The path
     */
    @Nullable
    @QueryParameter("path")
    abstract String getPath();

}
