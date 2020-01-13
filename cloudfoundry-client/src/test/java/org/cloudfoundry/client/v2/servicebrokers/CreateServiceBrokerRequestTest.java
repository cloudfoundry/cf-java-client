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

package org.cloudfoundry.client.v2.servicebrokers;

import org.junit.Test;

public final class CreateServiceBrokerRequestTest {

    @Test(expected = IllegalStateException.class)
    public void noAuthenticationPassword() {
        CreateServiceBrokerRequest.builder()
            .name("name")
            .authenticationUsername("username")
            .brokerUrl("http://somewhere-over-the-rainbow.org")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noAuthenticationUsername() {
        CreateServiceBrokerRequest.builder()
            .name("name")
            .authenticationPassword("password")
            .brokerUrl("http://somewhere-over-the-rainbow.org")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noBrokerUrl() {
        CreateServiceBrokerRequest.builder()
            .name("name")
            .authenticationPassword("password")
            .authenticationUsername("username")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noName() {
        CreateServiceBrokerRequest.builder()
            .authenticationPassword("password")
            .authenticationUsername("username")
            .brokerUrl("http://somewhere-over-the-rainbow.org")
            .build();
    }

    @Test
    public void valid() {
        CreateServiceBrokerRequest.builder()
            .name("name")
            .authenticationPassword("password")
            .authenticationUsername("username")
            .brokerUrl("http://somewhere-over-the-rainbow.org")
            .build();
    }

}
