/*
 * Copyright 2013-2015 the original author or authors.
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

package org.cloudfoundry.v3.client.application;

import org.cloudfoundry.v3.client.ValidationResult;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public final class CreateApplicationRequestTest {

    @Test
    public void test() {
        CreateApplicationRequest request = new CreateApplicationRequest()
                .withBuildpack("test-buildpack")
                .withEnvironmentVariable("test-key-1", "test-value-1")
                .withEnvironmentVariables(Collections.singletonMap("test-key-2", "test-value-2"))
                .withName("test-name")
                .withSpaceId("test-space-id");

        Map<String, String> environmentVariables = new HashMap<>();
        environmentVariables.put("test-key-1", "test-value-1");
        environmentVariables.put("test-key-2", "test-value-2");

        assertEquals("test-buildpack", request.getBuildpack());
        assertEquals(environmentVariables, request.getEnvironmentVariables());
        assertEquals("test-name", request.getName());
        assertEquals("test-space-id", request.getSpaceId());
    }

    @Test
    public void isValid() {
        ValidationResult result1 = new CreateApplicationRequest().isValid();
        assertEquals(ValidationResult.Status.INVALID, result1.getStatus());
        assertEquals(2, result1.getMessages().size());

        ValidationResult result2 = new CreateApplicationRequest()
                .withName("test-name")
                .withSpaceId("test-space-id").isValid();
        assertEquals(ValidationResult.Status.VALID, result2.getStatus());
    }
}
