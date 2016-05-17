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

public final class HttpStartTest {

    @Test
    public void dropsonde() {
        HttpStart.from(new org.cloudfoundry.dropsonde.events.HttpStart.Builder()
            .method(org.cloudfoundry.dropsonde.events.Method.GET)
            .peerType(org.cloudfoundry.dropsonde.events.PeerType.Client)
            .remoteAddress("test-remote-address")
            .requestId(new org.cloudfoundry.dropsonde.events.UUID.Builder()
                .high(0L)
                .low(0L)
                .build())
            .timestamp(0L)
            .uri("test-uri")
            .userAgent("test-user-agent")
            .build());
    }

    @Test(expected = IllegalStateException.class)
    public void noMethod() {
        HttpStart.builder()
            .peerType(PeerType.CLIENT)
            .remoteAddress("test-remote-address")
            .requestId(UUID.randomUUID())
            .timestamp(0L)
            .uri("test-uri")
            .userAgent("test-user-agent")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noPeerType() {
        HttpStart.builder()
            .method(Method.GET)
            .remoteAddress("test-remote-address")
            .requestId(UUID.randomUUID())
            .timestamp(0L)
            .uri("test-uri")
            .userAgent("test-user-agent")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noRemoteAddress() {
        HttpStart.builder()
            .method(Method.GET)
            .peerType(PeerType.CLIENT)
            .requestId(UUID.randomUUID())
            .timestamp(0L)
            .uri("test-uri")
            .userAgent("test-user-agent")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noRequestId() {
        HttpStart.builder()
            .method(Method.GET)
            .peerType(PeerType.CLIENT)
            .remoteAddress("test-remote-address")
            .timestamp(0L)
            .uri("test-uri")
            .userAgent("test-user-agent")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noTimestamp() {
        HttpStart.builder()
            .method(Method.GET)
            .peerType(PeerType.CLIENT)
            .remoteAddress("test-remote-address")
            .requestId(UUID.randomUUID())
            .uri("test-uri")
            .userAgent("test-user-agent")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noUri() {
        HttpStart.builder()
            .method(Method.GET)
            .peerType(PeerType.CLIENT)
            .remoteAddress("test-remote-address")
            .requestId(UUID.randomUUID())
            .timestamp(0L)
            .userAgent("test-user-agent")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noUserAgent() {
        HttpStart.builder()
            .method(Method.GET)
            .peerType(PeerType.CLIENT)
            .remoteAddress("test-remote-address")
            .requestId(UUID.randomUUID())
            .timestamp(0L)
            .uri("test-uri")
            .build();
    }

    @Test
    public void valid() {
        HttpStart.builder()
            .method(Method.GET)
            .peerType(PeerType.CLIENT)
            .remoteAddress("test-remote-address")
            .requestId(UUID.randomUUID())
            .timestamp(0L)
            .uri("test-uri")
            .userAgent("test-user-agent")
            .build();
    }

}
