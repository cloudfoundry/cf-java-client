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

package org.cloudfoundry.operations.serviceadmin;

import org.cloudfoundry.ValidationResult;
import org.junit.Test;

import static org.cloudfoundry.ValidationResult.Status.INVALID;
import static org.cloudfoundry.ValidationResult.Status.VALID;
import static org.junit.Assert.assertEquals;

public final class CreateServiceBrokerRequestTest {

    @Test
    public void isInValidNoName() {
        ValidationResult result = CreateServiceBrokerRequest.builder()
            .url("test-broker-url")
            .username("test-username")
            .password("test-password")
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("name must be specified", result.getMessages().get(0));
    }

    @Test
    public void isInValidNoUrl() {
        ValidationResult result = CreateServiceBrokerRequest.builder()
            .name("test-broker")
            .username("test-username")
            .password("test-password")
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("url must be specified", result.getMessages().get(0));
    }

    @Test
    public void isInValidNoUsername() {
        ValidationResult result = CreateServiceBrokerRequest.builder()
            .name("test-broker")
            .url("test-broker-url")
            .password("test-password")
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("username must be specified", result.getMessages().get(0));
    }

    @Test
    public void isInValidNoPassword() {
        ValidationResult result = CreateServiceBrokerRequest.builder()
            .name("test-broker")
            .url("test-broker-url")
            .username("test-username")
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("password must be specified", result.getMessages().get(0));
    }

    @Test
    public void isValid() {
        ValidationResult result = CreateServiceBrokerRequest.builder()
            .name("test-broker")
            .url("test-broker-url")
            .username("test-username")
            .password("test-password")
            .isSpaceScoped(true)
            .build()
            .isValid();

        assertEquals(VALID, result.getStatus());
    }

    @Test
    public void isValidNoSpaceScoped() {
        ValidationResult result = CreateServiceBrokerRequest.builder()
            .name("test-broker")
            .url("test-broker-url")
            .username("test-username")
            .password("test-password")
            .build()
            .isValid();

        assertEquals(VALID, result.getStatus());
    }
}
