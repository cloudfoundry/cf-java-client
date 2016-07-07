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
 * The event emitted when a client sends a request (or immediately when a server receives the request).
 */
@Value.Immutable
abstract class _HttpStart {

    public static HttpStart from(org.cloudfoundry.dropsonde.events.HttpStart dropsonde) {
        Objects.requireNonNull(dropsonde, "dropsonde");

        return HttpStart.builder()
            .applicationId(UuidUtils.from(dropsonde.applicationId))
            .instanceId(dropsonde.instanceId)
            .instanceIndex(dropsonde.instanceIndex)
            .method(Method.from(dropsonde.method))
            .parentRequestId(UuidUtils.from(dropsonde.parentRequestId))
            .peerType(PeerType.from(dropsonde.peerType))
            .remoteAddress(dropsonde.remoteAddress)
            .requestId(UuidUtils.from(dropsonde.requestId))
            .timestamp(dropsonde.timestamp)
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
     * The ID of the parent request of any request made to service an incoming request
     */
    @Nullable
    abstract UUID getParentRequestId();

    /**
     * The role of the emitting process in the request cycle
     */
    abstract PeerType getPeerType();

    /**
     * The remote address of the request. (For a server, this should be the origin of the request.)
     */
    abstract String getRemoteAddress();

    /**
     * The ID for tracking lifecycle of request. Should match requestId of a {@link HttpStop} event
     */
    abstract UUID getRequestId();

    /**
     * The UNIX timestamp (in nanoseconds) when the request was sent (by a client) or received (by a server)
     */
    abstract Long getTimestamp();

    /**
     * The uri of the request
     */
    abstract String getUri();

    /**
     * The contents of the UserAgent header on the request
     */
    abstract String getUserAgent();

}
