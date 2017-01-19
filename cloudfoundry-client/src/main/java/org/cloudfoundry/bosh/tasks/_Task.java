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

package org.cloudfoundry.bosh.tasks;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * The details of a task
 */
@JsonDeserialize
@Value.Immutable
abstract class _Task {

    /**
     * The description of the task's purpose
     */
    @JsonProperty("description")
    abstract String getDescription();

    /**
     * The id of the task
     */
    @JsonProperty("id")
    abstract Integer getId();

    /**
     * The task's result
     */
    @JsonProperty("result")
    @Nullable
    abstract String getResult();

    /**
     * The state of the task
     */
    @JsonProperty("state")
    abstract State getState();

    /**
     * The timestamp of the task
     */
    @JsonProperty("timestamp")
    abstract Integer getTimestamp();

    /**
     * The user which started the task
     */
    @JsonProperty("user")
    abstract String getUser();

}


