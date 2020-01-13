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

package org.cloudfoundry.operations.domains;

import org.immutables.value.Value;

/**
 * A Router Group
 */
@Value.Immutable
abstract class _RouterGroup {

    /**
     * The id
     */
    abstract String getId();

    /**
     * The name of the router group
     */
    abstract String getName();

    /**
     * Type of the router group, e.g. tcp
     */
    abstract String getType();

}
