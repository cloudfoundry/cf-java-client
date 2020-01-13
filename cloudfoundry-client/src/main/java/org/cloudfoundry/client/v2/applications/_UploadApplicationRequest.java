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

package org.cloudfoundry.client.v2.applications;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.QueryParameter;
import org.immutables.value.Value;

import java.nio.file.Path;
import java.util.List;

/**
 * Request payload for the Upload Application operation.
 */
@Value.Immutable
abstract class _UploadApplicationRequest {

    /**
     * A binary zip file containing the application bits
     */
    @JsonIgnore
    abstract Path getApplication();

    /**
     * The application id
     */
    @JsonIgnore
    abstract String getApplicationId();

    /**
     * If true, a new asynchronous job is submitted to persist the bits and the job id is included in the response
     */
    @Nullable
    @QueryParameter("async")
    abstract Boolean getAsync();

    /**
     * Fingerprints of the application bits that have previously been pushed to Cloud Foundry
     */
    @JsonIgnore
    abstract List<Resource> getResources();

}
