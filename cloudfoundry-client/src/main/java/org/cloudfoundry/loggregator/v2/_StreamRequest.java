/*
 * Copyright 2013-2019 the original author or authors.
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

package org.cloudfoundry.loggregator.v2;

import org.cloudfoundry.Nullable;
import org.cloudfoundry.QueryParameter;
import org.immutables.value.Value;

import java.util.List;

/**
 * The request payload for the Read endpoint
 */
@Value.Immutable
abstract class _StreamRequest {

    /**
     * The source id
     */
    @QueryParameter("source_id")
    abstract List<String> getSourceIds();

    /**
     * The log filter
     */
    @QueryParameter("log")
    @Nullable
    abstract Boolean isLog();

    /**
     * The counter filter
     */
    @QueryParameter("counter")
    @Nullable
    abstract Boolean isCounter();

    /**
     * The gauge filter
     */
    @QueryParameter("gauge")
    @Nullable
    abstract Boolean isGauge();

}
