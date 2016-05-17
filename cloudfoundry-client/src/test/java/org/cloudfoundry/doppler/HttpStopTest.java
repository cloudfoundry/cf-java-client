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

import org.junit.Test;

import java.util.UUID;

public final class HttpStopTest {

    @Test
    public void dropsonde() {
        HttpStop.from(new org.cloudfoundry.dropsonde.events.HttpStop.Builder()
            .contentLength(0L)
            .peerType(org.cloudfoundry.dropsonde.events.PeerType.Client)
            .requestId(new org.cloudfoundry.dropsonde.events.UUID.Builder()
                .high(0L)
                .low(0L)
                .build())
            .statusCode(0)
            .timestamp(0L)
            .uri("test-uri")
            .build());
    }

    @Test(expected = IllegalStateException.class)
    public void noContentLength() {
        HttpStop.builder()
            .peerType(PeerType.CLIENT)
            .requestId(UUID.randomUUID())
            .statusCode(0)
            .timestamp(0L)
            .uri("test-uri")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noPeerType() {
        HttpStop.builder()
            .contentLength(0L)
            .requestId(UUID.randomUUID())
            .statusCode(0)
            .timestamp(0L)
            .uri("test-uri")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noRequestId() {
        HttpStop.builder()
            .contentLength(0L)
            .peerType(PeerType.CLIENT)
            .statusCode(0)
            .timestamp(0L)
            .uri("test-uri")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noStatusCode() {
        HttpStop.builder()
            .contentLength(0L)
            .peerType(PeerType.CLIENT)
            .requestId(UUID.randomUUID())
            .timestamp(0L)
            .uri("test-uri")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noTimestamp() {
        HttpStop.builder()
            .contentLength(0L)
            .peerType(PeerType.CLIENT)
            .requestId(UUID.randomUUID())
            .statusCode(0)
            .uri("test-uri")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noUri() {
        HttpStop.builder()
            .contentLength(0L)
            .peerType(PeerType.CLIENT)
            .requestId(UUID.randomUUID())
            .statusCode(0)
            .timestamp(0L)
            .build();
    }

    @Test
    public void valid() {
        HttpStop.builder()
            .contentLength(0L)
            .peerType(PeerType.CLIENT)
            .requestId(UUID.randomUUID())
            .statusCode(0)
            .timestamp(0L)
            .uri("test-uri")
            .build();
    }

}
