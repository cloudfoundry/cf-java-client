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

package org.cloudfoundry.operations.networkpolicies;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

final class RemoveNetworkPolicyRequestTest {

    @Test
    void noDestination() {
        assertThrows(IllegalStateException.class, () -> {
            RemoveNetworkPolicyRequest.builder()
                .protocol("test-protocol")
                .startPort(1234)
                .source("test-source")
                .build();
        });
    }

    @Test
    void noPort() {
        assertThrows(IllegalStateException.class, () -> {
            RemoveNetworkPolicyRequest.builder()
                .destination("test-destination")
                .protocol("test-protocol")
                .source("test-source")
                .build();
        });
    }

    @Test
    void noProtocol() {
        assertThrows(IllegalStateException.class, () -> {
            RemoveNetworkPolicyRequest.builder()
                .destination("test-destination")
                .startPort(1234)
                .source("test-source")
                .build();
        });
    }

    @Test
    void noSource() {
        assertThrows(IllegalStateException.class, () -> {
            RemoveNetworkPolicyRequest.builder()
                .destination("test-destination")
                .protocol("test-protocol")
                .startPort(1234)
                .build();
        });
    }

    @Test
    void valid() {
        RemoveNetworkPolicyRequest.builder()
            .destination("test-destination")
            .protocol("test-protocol")
            .startPort(1234)
            .source("test-source")
            .build();
    }

}
