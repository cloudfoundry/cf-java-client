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

package org.cloudfoundry.doppler;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.immutables.value.Value;

/**
 * The request payload for the Container Metrics endpoint
 */
@Value.Immutable
abstract class _ContainerMetricsRequest {

    /**
     * The application id
     */
    @JsonIgnore
    abstract String getApplicationId();

}
