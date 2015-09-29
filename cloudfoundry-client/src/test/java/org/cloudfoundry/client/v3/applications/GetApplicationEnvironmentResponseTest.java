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

package org.cloudfoundry.client.v3.applications;

import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public final class GetApplicationEnvironmentResponseTest {

    @Test
    public void test() throws Exception {
        Map<String, String> applicationEnvironmentVariables = new HashMap<>();
        applicationEnvironmentVariables.put("application-test-key-1", "application-test-value-1");
        applicationEnvironmentVariables.put("application-test-key-2", "application-test-value-2");

        Map<String, String> environmentVariables = new HashMap<>();
        environmentVariables.put("test-key-1", "test-value-1");
        environmentVariables.put("test-key-2", "test-value-2");

        Map<String, String> runningEnvironmentVariables = new HashMap<>();
        runningEnvironmentVariables.put("running-test-key-1", "running-test-value-1");
        runningEnvironmentVariables.put("running-test-key-2", "running-test-value-2");

        Map<String, String> stagingEnvironmentVariables = new HashMap<>();
        stagingEnvironmentVariables.put("staging-test-key-1", "staging-test-value-1");
        stagingEnvironmentVariables.put("staging-test-key-2", "staging-test-value-2");

        GetApplicationEnvironmentResponse response = new GetApplicationEnvironmentResponse()
                .withApplicationEnvironmentVariable("application-test-key-1", "application-test-value-1")
                .withApplicationEnvironmentVariables(Collections.singletonMap("application-test-key-2", "application-test-value-2"))
                .withEnvironmentVariable("test-key-1", "test-value-1")
                .withEnvironmentVariables(Collections.singletonMap("test-key-2", "test-value-2"))
                .withRunningEnvironmentVariable("running-test-key-1", "running-test-value-1")
                .withRunningEnvironmentVariables(Collections.singletonMap("running-test-key-2", "running-test-value-2"))
                .withStagingEnvironmentVariable("staging-test-key-1", "staging-test-value-1")
                .withStagingEnvironmentVariables(Collections.singletonMap("staging-test-key-2", "staging-test-value-2"));

        assertEquals(applicationEnvironmentVariables, response.getApplicationEnvironmentVariables());
        assertEquals(environmentVariables, response.getEnvironmentVariables());
        assertEquals(runningEnvironmentVariables, response.getRunningEnvironmentVariables());
        assertEquals(stagingEnvironmentVariables, response.getStagingEnvironmentVariables());
    }

}