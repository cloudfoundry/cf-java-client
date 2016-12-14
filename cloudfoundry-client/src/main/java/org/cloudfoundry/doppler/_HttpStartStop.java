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

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * This event represents the whole lifecycle of an HTTP request.
 */
@Value.Immutable
abstract class _HttpStartStop {

    public static HttpStartStop from(org.cloudfoundry.dropsonde.events.HttpStartStop dropsonde) {
        Objects.requireNonNull(dropsonde, "dropsonde");

        return HttpStartStop.builder()
            .applicationId(UuidUtils.from(dropsonde.applicationId))
            .contentLength(dropsonde.contentLength)
            .forwarded(dropsonde.forwarded)
            .instanceId(dropsonde.instanceId)
            .instanceIndex(dropsonde.instanceIndex)
            .method(Method.from(dropsonde.method))
            .peerType(PeerType.from(dropsonde.peerType))
            .remoteAddress(dropsonde.remoteAddress)
            .requestId(UuidUtils.from(dropsonde.requestId))
            .startTimestamp(dropsonde.startTimestamp)
            .statusCode(dropsonde.statusCode)
            .stopTimestamp(dropsonde.stopTimestamp)
            .uri(dropsonde.uri)
            .userAgent(dropsonde.userAgent)
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
     * The http forwarded-for [x-forwarded-for] header from the request
     */
    abstract List<String> getForwarded();

    /**
     * The ID of the application instance
     */
    @Nullable
    abstract String getInstanceId();

    /**
     * The index of the application instance
     */
    @Nullable
    abstract Integer getInstanceIndex();

    /**
     * The method of the request
     */
    abstract Method getMethod();

    /**
     * The role of the emitting process in the request cycle
     */
    abstract PeerType getPeerType();

    /**
     * The remote address of the request. (For a server, this should be the origin of the request.)
     */
    abstract String getRemoteAddress();

    /**
     * The ID for tracking lifecycle of request.
     */
    abstract UUID getRequestId();

    /**
     * The UNIX timestamp (in nanoseconds) when the request was sent (by a client) or received (by a server)
     */
    abstract Long getStartTimestamp();

    /**
     * The status code returned with the response to the request
     */
    abstract Integer getStatusCode();

    /**
     * The UNIX timestamp (in nanoseconds) when the request was received
     */
    abstract Long getStopTimestamp();

    /**
     * The uri of the request
     */
    abstract String getUri();

    /**
     * The contents of the UserAgent header on the request
     */
    abstract String getUserAgent();

}
