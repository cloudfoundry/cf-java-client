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

import java.util.List;

/**
 * A route and the applications that are bound to the route.
 */
@Value.Immutable
abstract class _Route {

    /**
     * The applications bound to this route.
     */
    abstract List<String> getApplications();

    /**
     * The domain of this route
     */
    abstract String getDomain();

    /**
     * The host of this route
     */
    abstract String getHost();

    /**
     * The id
     */
    abstract String getId();

    /**
     * The path of this route
     */
    @Nullable
    abstract String getPath();

    /**
     * The port of this route
     */
    @Nullable
    abstract String getPort();

    /**
     * The service of this route
     */
    @Nullable
    abstract String getService();

    /**
     * The name of the space of this route
     */
    abstract String getSpace();

    /**
     * The type of this route
     */
    @Nullable
    abstract String getType();

}
