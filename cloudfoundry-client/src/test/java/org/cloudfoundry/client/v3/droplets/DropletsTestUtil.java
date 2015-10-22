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

package org.cloudfoundry.client.v3.droplets;

import org.cloudfoundry.client.v3.Hash;
import org.cloudfoundry.client.v3.Link;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

final class DropletsTestUtil {

    static <T extends Droplet<T>> void test(Droplet<T> response) {
        Map<String, String> environmentVariables = new HashMap<>();
        environmentVariables.put("test-key-1", "test-value-1");
        environmentVariables.put("test-key-2", "test-value-2");

        Hash hash = new Hash();

        Map<String, Link> links = new HashMap<>();
        links.put("test-link-1", new Link());
        links.put("test-link-2", new Link());

        response.withBuildpack("test-buildpack")
                .withCreatedAt("test-created-at")
                .withEnvironmentVariable("test-key-1", environmentVariables.get("test-key-1"))
                .withEnvironmentVariables(Collections.singletonMap("test-key-2",
                        environmentVariables.get("test-key-2")))
                .withError("test-error")
                .withHash(hash)
                .withId("test-id")
                .withLink("test-link-1", links.get("test-link-1"))
                .withLinks(Collections.singletonMap("test-link-2", links.get("test-link-2")))
                .withProcfile("test-procfile")
                .withState("test-state")
                .withUpdatedAt("test-updated-at");

        assertEquals("test-buildpack", response.getBuildpack());
        assertEquals("test-created-at", response.getCreatedAt());
        assertEquals(environmentVariables, response.getEnvironmentVariables());
        assertEquals("test-error", response.getError());
        assertEquals(hash, response.getHash());
        assertEquals("test-id", response.getId());
        assertEquals(links, response.getLinks());
        assertEquals("test-procfile", response.getProcfile());
        assertEquals("test-state", response.getState());
        assertEquals("test-updated-at", response.getUpdatedAt());
    }
}
