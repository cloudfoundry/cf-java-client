/*
 * Copyright 2013-2016 the original author or authors.
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

import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

import java.util.Objects;
import java.util.UUID;

/**
 * The event emitted when a client receives a response to its request (or when a server completes its handling and returns a response)
 */
@Value.Immutable
abstract class _HttpStop {

    public static HttpStop from(org.cloudfoundry.dropsonde.events.HttpStop dropsonde) {
        Objects.requireNonNull(dropsonde, "dropsonde");

        return HttpStop.builder()
            .applicationId(UuidUtils.from(dropsonde.applicationId))
            .contentLength(dropsonde.contentLength)
            .peerType(PeerType.from(dropsonde.peerType))
            .requestId(UuidUtils.from(dropsonde.requestId))
            .statusCode(dropsonde.statusCode)
            .timestamp(dropsonde.timestamp)
            .uri(dropsonde.uri)
            .build();
    }

    /**
     * The application id
     */
    @Nullable
    abstract UUID getApplicationId();

    /**
     * The length of the response in bytes
     */
    abstract Long getContentLength();

    /**
     * The role of the emitting process in the request cycle
     */
    abstract PeerType getPeerType();

    /**
     * The ID for tracking lifecycle of request. Should match requestId of a {@link HttpStop} event
     */
    abstract UUID getRequestId();

    /**
     * The status code returned with the response to the request
     */
    abstract Integer getStatusCode();

    /**
     * The UNIX timestamp (in nanoseconds) when the request was received
     */
    abstract Long getTimestamp();

    /**
     * The uri of the request
     */
    abstract String getUri();


}
