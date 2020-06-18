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

    @Nullable
    @JsonProperty("timestamp")
    abstract Long getTimestamp();

    @Nullable
    @JsonProperty("source_id")
    abstract String  getSourceId();

    @Nullable
    @JsonProperty("instance_id")
    abstract String getInstanceId();

    @AllowNulls
    @JsonProperty("tags")
    abstract Map<String, String> getTags();

    @Nullable
    @JsonProperty("log")
    abstract Log getLog();

    @Nullable
    @JsonProperty("counter")
    abstract Counter getCounter();

    @Nullable
    @JsonProperty("gauge")
    abstract Gauge getGauge();

    @Nullable
    @JsonProperty("timer")
    abstract Timer getTimer();

    @Nullable
    @JsonProperty("event")
    abstract Event getEvent();

}
