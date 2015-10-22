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

import org.cloudfoundry.client.v3.Hash;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.processes.Process;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

final class ApplicationsTestUtil {

    static Map<String, String> getEnvironment() {
        Map<String, String> environmentVariables = new HashMap<>();
        environmentVariables.put("test-key-1", "test-value-1");
        environmentVariables.put("test-key-2", "test-value-2");
        return environmentVariables;
    }


    static Hash getHash() {
        Hash hash = new Hash()
                .withType("test-type")
                .withValue("test-value");

        assertEquals("test-type", hash.getType());
        assertEquals("test-value", hash.getValue());
        return hash;
    }

    static Map<String, Link> getLinks() {
        Map<String, Link> links = new HashMap<>();
        links.put("test-link-1", new Link());
        links.put("test-link-2", new Link());
        return links;
    }

    static <T extends Application<T>> void testForApplication(Application<T> response) {
        Map<String, String> environmentVariables = getEnvironment();
        Map<String, Link> links = getLinks();

        response.withBuildpack("test-buildpack")
                .withCreatedAt("test-created-at")
                .withDesiredState("test-desired-state")
                .withEnvironmentVariable("test-key-1", environmentVariables.get("test-key-1"))
                .withEnvironmentVariables(
                        Collections.singletonMap("test-key-2", environmentVariables.get("test-key-2")))
                .withId("test-id")
                .withLink("test-link-1", links.get("test-link-1"))
                .withLinks(Collections.singletonMap("test-link-2", links.get("test-link-2")))
                .withName("test-name")
                .withTotalDesiredInstances(-1)
                .withUpdatedAt("test-updated-at");

        assertEquals("test-buildpack", response.getBuildpack());
        assertEquals("test-created-at", response.getCreatedAt());
        assertEquals("test-desired-state", response.getDesiredState());
        assertEquals(environmentVariables, response.getEnvironmentVariables());
        assertEquals("test-id", response.getId());
        assertEquals(links, response.getLinks());
        assertEquals("test-name", response.getName());
        assertEquals(Integer.valueOf(-1), response.getTotalDesiredInstances());
        assertEquals("test-updated-at", response.getUpdatedAt());
    }

    static <T extends Process<T>> void testForProcess(Process<T> response) {
        Map<String, Link> links = getLinks();

        response.withCreatedAt("test-created-at")
                .withCommand("test-command")
                .withDiskInMb(400)
                .withId("test-id")
                .withLink("test-link-1", links.get("test-link-1"))
                .withLinks(Collections.singletonMap("test-link-2", links.get("test-link-2")))
                .withInstances(3)
                .withMemoryInMb(1024)
                .withType("web")
                .withUpdatedAt("test-updated-at");

        assertEquals("test-created-at", response.getCreatedAt());
        assertEquals("test-command", response.getCommand());
        assertEquals(Integer.valueOf(400), response.getDiskInMb());
        assertEquals("test-id", response.getId());
        assertEquals(links, response.getLinks());
        assertEquals(Integer.valueOf(3), response.getInstances());
        assertEquals(Integer.valueOf(1024), response.getMemoryInMb());
        assertEquals("web", response.getType());
        assertEquals("test-updated-at", response.getUpdatedAt());
    }

}
