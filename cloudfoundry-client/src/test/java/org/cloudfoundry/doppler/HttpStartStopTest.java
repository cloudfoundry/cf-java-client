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

package org.cloudfoundry.doppler;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

final class HttpStartStopTest {

    @Test
    void dropsonde() {
        HttpStartStop.from(new org.cloudfoundry.dropsonde.events.HttpStartStop.Builder()
            .contentLength(0L)
            .method(org.cloudfoundry.dropsonde.events.Method.GET)
            .peerType(org.cloudfoundry.dropsonde.events.PeerType.Client)
            .remoteAddress("test-remote-address")
            .requestId(new org.cloudfoundry.dropsonde.events.UUID.Builder()
                .high(0L)
                .low(0L)
                .build())
            .startTimestamp(0L)
            .statusCode(0)
            .stopTimestamp(0L)
            .uri("test-uri")
            .userAgent("test-user-agent")
            .build());
    }

    @Test
    void noContentLength() {
        assertThrows(IllegalStateException.class, () -> {
            HttpStartStop.builder()
                .peerType(PeerType.CLIENT)
                .remoteAddress("test-remote-address")
                .requestId(UUID.randomUUID())
                .startTimestamp(0L)
                .statusCode(0)
                .stopTimestamp(0L)
                .uri("test-uri")
                .userAgent("test-user-agent")
                .build();
        });
    }

    @Test
    void noPeerType() {
        assertThrows(IllegalStateException.class, () -> {
            HttpStartStop.builder()
                .contentLength(0L)
                .remoteAddress("test-remote-address")
                .requestId(UUID.randomUUID())
                .startTimestamp(0L)
                .statusCode(0)
                .stopTimestamp(0L)
                .uri("test-uri")
                .userAgent("test-user-agent")
                .build();
        });
    }

    @Test
    void noRemoteAddress() {
        assertThrows(IllegalStateException.class, () -> {
            HttpStartStop.builder()
                .contentLength(0L)
                .peerType(PeerType.CLIENT)
                .requestId(UUID.randomUUID())
                .startTimestamp(0L)
                .statusCode(0)
                .stopTimestamp(0L)
                .uri("test-uri")
                .userAgent("test-user-agent")
                .build();
        });
    }

    @Test
    void noRequestId() {
        assertThrows(IllegalStateException.class, () -> {
            HttpStartStop.builder()
                .contentLength(0L)
                .peerType(PeerType.CLIENT)
                .remoteAddress("test-remote-address")
                .startTimestamp(0L)
                .statusCode(0)
                .stopTimestamp(0L)
                .uri("test-uri")
                .userAgent("test-user-agent")
                .build();
        });
    }

    @Test
    void noStartTimestamp() {
        assertThrows(IllegalStateException.class, () -> {
            HttpStartStop.builder()
                .contentLength(0L)
                .peerType(PeerType.CLIENT)
                .remoteAddress("test-remote-address")
                .requestId(UUID.randomUUID())
                .statusCode(0)
                .stopTimestamp(0L)
                .uri("test-uri")
                .userAgent("test-user-agent")
                .build();
        });
    }

    @Test
    void noStatusCode() {
        assertThrows(IllegalStateException.class, () -> {
            HttpStartStop.builder()
                .contentLength(0L)
                .peerType(PeerType.CLIENT)
                .remoteAddress("test-remote-address")
                .requestId(UUID.randomUUID())
                .startTimestamp(0L)
                .stopTimestamp(0L)
                .uri("test-uri")
                .userAgent("test-user-agent")
                .build();
        });
    }

    @Test
    void noStopTimestamp() {
        assertThrows(IllegalStateException.class, () -> {
            HttpStartStop.builder()
                .contentLength(0L)
                .peerType(PeerType.CLIENT)
                .remoteAddress("test-remote-address")
                .requestId(UUID.randomUUID())
                .startTimestamp(0L)
                .statusCode(0)
                .uri("test-uri")
                .userAgent("test-user-agent")
                .build();
        });
    }

    @Test
    void noUri() {
        assertThrows(IllegalStateException.class, () -> {
            HttpStartStop.builder()
                .contentLength(0L)
                .peerType(PeerType.CLIENT)
                .remoteAddress("test-remote-address")
                .requestId(UUID.randomUUID())
                .startTimestamp(0L)
                .statusCode(0)
                .stopTimestamp(0L)
                .userAgent("test-user-agent")
                .build();
        });
    }

    @Test
    void noUserAgent() {
        assertThrows(IllegalStateException.class, () -> {
            HttpStartStop.builder()
                .contentLength(0L)
                .peerType(PeerType.CLIENT)
                .remoteAddress("test-remote-address")
                .requestId(UUID.randomUUID())
                .startTimestamp(0L)
                .statusCode(0)
                .stopTimestamp(0L)
                .uri("test-uri")
                .build();
        });
    }

    @Test
    void valid() {
        HttpStartStop.builder()
                .contentLength(0L)
                .peerType(PeerType.CLIENT)
                .remoteAddress("test-remote-address")
                .requestId(UUID.randomUUID())
                .startTimestamp(0L)
                .statusCode(0)
                .stopTimestamp(0L)
                .uri("test-uri")
                .userAgent("test-user-agent")
                .build();
    }
}
