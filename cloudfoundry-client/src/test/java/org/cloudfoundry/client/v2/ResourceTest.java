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

package org.cloudfoundry.client.v2;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class ResourceTest {

    @Test
    public void test() {
        Object entity = new Object();

        Resource.Metadata metadata = new Resource.Metadata()
                .withCreatedAt("test-created-at")
                .withId("test-id")
                .withUpdatedAt("test-updated-at")
                .withUrl("test-url");

        assertEquals("test-created-at", metadata.getCreatedAt());
        assertEquals("test-id", metadata.getId());
        assertEquals("test-updated-at", metadata.getUpdatedAt());
        assertEquals("test-url", metadata.getUrl());

        StubResource resource = new StubResource()
                .withEntity(entity)
                .withMetadata(metadata);

        assertEquals(entity, resource.getEntity());
        assertEquals(metadata, resource.getMetadata());
    }

    private static final class StubResource extends Resource<StubResource, Object> {
    }

}
