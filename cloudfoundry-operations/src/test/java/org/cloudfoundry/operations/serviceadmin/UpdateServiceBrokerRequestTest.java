/*
 * Copyright 2013-2018 the original author or authors.
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

package org.cloudfoundry.operations.serviceadmin;

import org.junit.Test;

public class UpdateServiceBrokerRequestTest {

    @Test(expected = IllegalStateException.class)
    public void noName() {
        UpdateServiceBrokerRequest.builder()
                .url("test-broker-url")
                .username("test-username")
                .password("test-password")
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noPassword() {
        UpdateServiceBrokerRequest.builder()
                .name("test-broker")
                .url("test-broker-url")
                .username("test-username")
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noUrl() {
        UpdateServiceBrokerRequest.builder()
                .name("test-broker")
                .username("test-username")
                .password("test-password")
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noUsername() {
        UpdateServiceBrokerRequest.builder()
                .name("test-broker")
                .url("test-broker-url")
                .password("test-password")
                .build();
    }

    @Test
    public void valid() {
        UpdateServiceBrokerRequest.builder()
                .name("test-broker")
                .url("test-broker-url")
                .username("test-username")
                .password("test-password")
                .build();
    }
}
