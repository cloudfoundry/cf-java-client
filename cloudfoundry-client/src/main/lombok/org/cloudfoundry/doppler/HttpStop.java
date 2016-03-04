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

import lombok.Builder;
import lombok.Data;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

import java.util.Optional;
import java.util.UUID;

/**
 * The event emitted when a client receives a response to its request (or when a server completes its handling and returns a response)
 */
@Data
public final class HttpStop implements Event, Validatable {

    /**
     * The application id
     *
     * @return applicationId the application id
     * @return the application id
     */
    private final UUID applicationId;

    /**
     * The length of the response in bytes
     *
     * @param contentLength the length of the response in bytes
     * @return the length of the response in bytes
     */
    private final Long contentLength;

    /**
     * The role of the emitting process in the request cycle
     *
     * @param peerType the role of the emitting process in the request cycle
     * @return the role of the emitting process in the request cycle
     */
    private final PeerType peerType;

    /**
     * The ID for tracking lifecycle of request. Should match requestId of a {@link HttpStart} event
     *
     * @param requestId the ID for tracking lifecycle of request
     * @return the ID for tracking lifecycle of request
     */
    private final UUID requestId;

    /**
     * The status code returned with the response to the request
     *
     * @param statusCode the status code returned with the response to the request
     * @return the status code returned with the response to the request
     */
    private final Integer statusCode;

    /**
     * The UNIX timestamp (in nanoseconds) when the request was received
     *
     * @param timestamp the UNIX timestamp
     * @return the UNIX timestamp
     */
    private final Long timestamp;

    /**
     * The uri of the request
     *
     * @param uri the uri of the request
     * @return the uri of the request
     */
    private final String uri;

    @Builder
    HttpStop(org.cloudfoundry.dropsonde.events.HttpStop dropsonde, UUID applicationId, Long contentLength, PeerType peerType, UUID requestId, Integer statusCode, Long timestamp, String uri) {
        Optional<org.cloudfoundry.dropsonde.events.HttpStop> o = Optional.ofNullable(dropsonde);

        this.applicationId = o.map(d -> d.applicationId).map(DropsondeUtils::uuid).orElse(applicationId);
        this.contentLength = o.map(d -> d.contentLength).orElse(contentLength);
        this.peerType = o.map(d -> d.peerType).map(PeerType::dropsonde).orElse(peerType);
        this.requestId = o.map(d -> d.requestId).map(DropsondeUtils::uuid).orElse(requestId);
        this.statusCode = o.map(d -> d.statusCode).orElse(statusCode);
        this.timestamp = o.map(d -> d.timestamp).orElse(timestamp);
        this.uri = o.map(d -> d.uri).orElse(uri);
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.contentLength == null) {
            builder.message("content length must be specified");
        }

        if (this.peerType == null) {
            builder.message("peer type must be specified");
        }

        if (this.requestId == null) {
            builder.message("request id must be specified");
        }

        if (this.statusCode == null) {
            builder.message("status code must be specified");
        }

        if (this.timestamp == null) {
            builder.message("timestamp must be specified");
        }

        if (this.uri == null) {
            builder.message("uri must be specified");
        }

        return builder.build();
    }

}
