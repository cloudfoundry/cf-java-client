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

/**
 * Represents a task
 */
@Value.Immutable
abstract class _Task {

    /**
     * The command for the task
     */
    @Nullable
    abstract String getCommand();

    /**
     * The name of the task
     */
    abstract String getName();

    /**
     * The sequence id of the task
     */
    abstract Integer getSequenceId();

    /**
     * The start time of the task
     */
    abstract String getStartTime();

    /**
     * The state of the task
     */
    abstract TaskState getState();

}
