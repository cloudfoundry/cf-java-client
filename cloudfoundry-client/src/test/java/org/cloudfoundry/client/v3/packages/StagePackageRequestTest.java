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

package org.cloudfoundry.client.v3.packages;

import org.cloudfoundry.client.ValidationResult;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.cloudfoundry.client.ValidationResult.Status.INVALID;
import static org.cloudfoundry.client.ValidationResult.Status.VALID;
import static org.junit.Assert.assertEquals;

public final class StagePackageRequestTest {

    @Test
    public void test() {
        Map<String, String> environmentVariables = new HashMap<>();
        environmentVariables.put("test-key-1", "test-value-1");
        environmentVariables.put("test-key-2", "test-value-2");

        StagePackageRequest request = new StagePackageRequest()
                .withBuildpack("test-buildpack")
                .withDiskLimit(-1)
                .withEnvironmentVariable("test-key-1", environmentVariables.get("test-key-1"))
                .withEnvironmentVariables(Collections.singletonMap("test-key-2",
                        environmentVariables.get("test-key-2")))
                .withId("test-id")
                .withMemoryLimit(-2)
                .withStack("test-stack");

        assertEquals("test-buildpack", request.getBuildpack());
        assertEquals(Integer.valueOf(-1), request.getDiskLimit());
        assertEquals(environmentVariables, request.getEnvironmentVariables());
        assertEquals("test-id", request.getId());
        assertEquals(Integer.valueOf(-2), request.getMemoryLimit());
        assertEquals("test-stack", request.getStack());
    }

    @Test
    public void isValid() {
        ValidationResult result = new StagePackageRequest()
                .withId("test-application-id")
                .isValid();

        assertEquals(VALID, result.getStatus());
    }

    @Test
    public void isValidNoId() {
        ValidationResult result = new StagePackageRequest()
                .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("id must be specified", result.getMessages().get(0));
    }

}
