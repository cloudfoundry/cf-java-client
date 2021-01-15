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

package org.cloudfoundry.logcache.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.doppler.HttpStartStop;
import org.immutables.value.Value;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@JsonDeserialize
@Value.Immutable
abstract class _Log {

    public String getPayloadAsText() {
        final byte[] decodedPayload = Base64.getDecoder().decode(getPayload());
        return StandardCharsets.UTF_8.decode(ByteBuffer.wrap(decodedPayload)).toString();
    }

    /**
     * The log payload
     */
    @JsonProperty("payload")
    @Nullable
    abstract String getPayload();

    /**
     * The log type
     */
    @JsonProperty("type")
    @Nullable
    abstract LogType getType();

}
