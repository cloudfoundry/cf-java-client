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

package org.cloudfoundry.client.v3.applications.packages;

import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.applications.packages.Package.Hash;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public final class CreatePackageResponseTest {

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

        CreatePackageResponse response = new CreatePackageResponse()
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

        assertEquals("test-created-at", response.getCreatedAt());
        assertEquals("test-error", response.getError());
        assertEquals(hash, response.getHash());
        assertEquals("test-id", response.getId());
        assertEquals(links, response.getLinks());
        assertEquals("test-state", response.getState());
        assertEquals("test-type", response.getType());
        assertEquals("test-updated-at", response.getUpdatedAt());
        assertEquals("test-url", response.getUrl());
    }

}
