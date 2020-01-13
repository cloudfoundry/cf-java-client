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

package org.cloudfoundry.operations.routes;

import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * The request options for the unmap route operation
 */
@Value.Immutable
abstract class _UnmapRouteRequest {

    @Value.Check
    void checkSetup() {
        if (getPort() != null && hostOrPathSet()) {
            throw new IllegalStateException("Cannot specify port together with hostname and/or path");
        }
    }

    /**
     * The name of the application to remove a route from
     */
    abstract String getApplicationName();

    /**
     * The domain of the route
     */
    abstract String getDomain();

    /**
     * The host of the route
     */
    @Nullable
    abstract String getHost();

    /**
     * The path of the route
     */
    @Nullable
    abstract String getPath();

    /**
     * The port of the route
     */
    @Nullable
    abstract Integer getPort();

    private boolean hostOrPathSet() {
        return getHost() != null || getPath() != null;
    }

}
