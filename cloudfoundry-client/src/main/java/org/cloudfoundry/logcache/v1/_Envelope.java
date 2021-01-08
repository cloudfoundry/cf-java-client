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

package org.cloudfoundry.logcache.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.AllowNulls;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

import java.util.Map;

@JsonDeserialize
@Value.Immutable
@JsonIgnoreProperties("deprecated_tags")
abstract class _Envelope {

    /**
     * The enclosed {@link Counter}
     */
    @JsonProperty("counter")
    @Nullable
    abstract Counter getCounter();

    /**
     * The enclosed {@link Event}
     */
    @JsonProperty("event")
    @Nullable
    abstract Event getEvent();

    /**
     * The enclosed {@link Gauge}
     */
    @JsonProperty("gauge")
    @Nullable
    abstract Gauge getGauge();

    /**
     * The instance id
     */
    @JsonProperty("instance_id")
    @Nullable
    abstract String getInstanceId();

    /**
     * The enclosed {@link Log}
     */
    @JsonProperty("log")
    @Nullable
    abstract Log getLog();

    /**
     * The source id
     */
    @JsonProperty("source_id")
    @Nullable
    abstract String getSourceId();

    /**
     * Key/value tags to include additional identifying information
     */
    @JsonProperty("tags")
    @AllowNulls
    abstract Map<String, String> getTags();

    /**
     * The enclosed {@link Timer}
     */
    @JsonProperty("timer")
    @Nullable
    abstract Timer getTimer();

    /**
     * UNIX timestamp (in nanoseconds) event was wrapped in this Envelope.
     */
    @JsonProperty("timestamp")
    @Nullable
    abstract Long getTimestamp();

}
