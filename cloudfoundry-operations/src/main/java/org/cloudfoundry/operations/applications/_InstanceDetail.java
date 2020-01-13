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

package org.cloudfoundry.operations.applications;

import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

import java.util.Date;

/**
 * Information about an instance of an application
 */
@Value.Immutable
abstract class _InstanceDetail {

    /**
     * The CPU consumption of this instance
     */
    @Nullable
    abstract Double getCpu();

    /**
     * The diskUsage quota, in bytes, of this instance
     */
    @Nullable
    abstract Long getDiskQuota();

    /**
     * The disk usage, in bytes, of this instance
     */
    @Nullable
    abstract Long getDiskUsage();

    /**
     * The index of this instance
     */
    abstract String getIndex();

    /**
     * The memoryUsage quota, in bytes, of this instance
     */
    @Nullable
    abstract Long getMemoryQuota();

    /**
     * The memory usage, in bytes, of this instance
     */
    @Nullable
    abstract Long getMemoryUsage();

    /**
     * The time this instance was created
     */
    @Nullable
    abstract Date getSince();

    /**
     * The state of the instance
     */
    @Nullable
    abstract String getState();

}
