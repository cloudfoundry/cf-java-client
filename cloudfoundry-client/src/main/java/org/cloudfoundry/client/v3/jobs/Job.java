/*
 * Copyright 2013-2021 the original author or authors.
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

package org.cloudfoundry.client.v3.jobs;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.client.v3.Resource;

import java.util.List;

/**
 * Base class for responses that are jobs
 */
public abstract class Job extends Resource {

    /**
     * Collection of errors that occurred while processing the job.
     */
    @JsonProperty("errors")
    public abstract List<org.cloudfoundry.client.v3.Error> getErrors();

    /**
     * Current desired operation of the job
     */
    @JsonProperty("operation")
    public abstract String getOperation();

    /**
     * State of the job
     */
    @JsonProperty("state")
    public abstract JobState getState();

    /**
     * Collection of warnings that occurred while processing the job.
     */
    @JsonProperty("warnings")
    public abstract List<Warning> getWarnings();

}
