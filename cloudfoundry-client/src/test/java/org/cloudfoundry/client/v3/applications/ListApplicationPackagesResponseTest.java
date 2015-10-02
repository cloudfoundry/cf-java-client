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
import org.cloudfoundry.client.v3.applications.ListApplicationPackagesResponse.Resource;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public final class ListApplicationPackagesResponseTest {

    @Test
    public void test() {
        Hash hash = new Hash()
                .withType("test-type")
                .withValue("test-value");

        assertEquals("test-type", hash.getType());
        assertEquals("test-value", hash.getValue());

        Map<String, Link> links = new HashMap<>();
        links.put("test-link-1", new Link());
        links.put("test-link-2", new Link());

        Resource resource = new Resource()
                .withCreatedAt("test-created-at")
                .withError("test-error")
                .withHash(hash)
                .withId("test-id")
                .withLink("test-link-1", links.get("test-link-1"))
                .withLinks(Collections.singletonMap("test-link-2", links.get("test-link-2")))
                .withState("test-state")
                .withType("test-type")
                .withUpdatedAt("test-updated-at")
                .withUrl("test-url");

        assertEquals("test-created-at", resource.getCreatedAt());
        assertEquals("test-error", resource.getError());
        assertEquals(hash, resource.getHash());
        assertEquals("test-id", resource.getId());
        assertEquals(links, resource.getLinks());
        assertEquals("test-state", resource.getState());
        assertEquals("test-type", resource.getType());
        assertEquals("test-updated-at", resource.getUpdatedAt());
        assertEquals("test-url", resource.getUrl());

    }

}
