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

import org.junit.Test;

import java.util.UUID;

public final class HttpStartStopTest {

    @Test
    public void dropsonde() {
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

    @Test(expected = IllegalStateException.class)
    public void noContentLength() {
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
    }

    @Test(expected = IllegalStateException.class)
    public void noPeerType() {
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
    }

    @Test(expected = IllegalStateException.class)
    public void noRemoteAddress() {
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
    }

    @Test(expected = IllegalStateException.class)
    public void noRequestId() {
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
    }

    @Test(expected = IllegalStateException.class)
    public void noStartTimestamp() {
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
    }

    @Test(expected = IllegalStateException.class)
    public void noStatusCode() {
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
    }

    @Test(expected = IllegalStateException.class)
    public void noStopTimestamp() {
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
    }

    @Test(expected = IllegalStateException.class)
    public void noUri() {
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
    }

    @Test(expected = IllegalStateException.class)
    public void noUserAgent() {
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
    }

    @Test
    public void valid() {
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
