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

import org.cloudfoundry.client.v3.Link;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public final class ListApplicationProcessesResponseTest {

    @Test
    public void test() {
        Map<String, Link> links = new HashMap<>();
        links.put("test-link-1", new Link());
        links.put("test-link-2", new Link());

        ListApplicationProcessesResponse.Resource resource = new ListApplicationProcessesResponse.Resource()
                .withCreatedAt("test-created-at")
                .withCommand(null)
                .withDiskInMb(400)
                .withId("test-id")
                .withLink("test-link-1", links.get("test-link-1"))
                .withLinks(Collections.singletonMap("test-link-2", links.get("test-link-2")))
                .withInstances(3)
                .withMemoryInMb(1024)
                .withType("web")
                .withUpdatedAt("test-updated-at");

        assertEquals("test-created-at", resource.getCreatedAt());
        assertEquals(null, resource.getCommand());
        assertEquals(Integer.valueOf(400), resource.getDiskInMb());
        assertEquals("test-id", resource.getId());
        assertEquals(links, resource.getLinks());
        assertEquals(Integer.valueOf(3), resource.getInstances());
        assertEquals(Integer.valueOf(1024), resource.getMemoryInMb());
        assertEquals("web", resource.getType());
        assertEquals("test-updated-at", resource.getUpdatedAt());
    }

}
