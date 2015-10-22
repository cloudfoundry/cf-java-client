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

package org.cloudfoundry.client.v2.events;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

final class EventsTestUtil {

    static EventResource.EventEntity entity() {
        return new EventResource.EventEntity()
                .withActee("test-actee")
                .withActeeName("test-actee-name")
                .withActeeType("test-actee-type")
                .withActor("test-actor")
                .withActorName("test-actor-name")
                .withActorType("test-actor-type")
                .withMetadata("test-key-1", "test-value-1")
                .withMetadatas(Collections.singletonMap("test-key-2", "test-value-2"))
                .withOrganizationId("test-organization-id")
                .withSpaceId("test-space-id")
                .withTimestamp("test-timestamp")
                .withType("test-type");
    }

    static void verify(EventResource.EventEntity entity) {
        Map<String, String> metadatas = new HashMap<>();
        metadatas.put("test-key-1", "test-value-1");
        metadatas.put("test-key-2", "test-value-2");

        assertEquals("test-actee", entity.getActee());
        assertEquals("test-actee-name", entity.getActeeName());
        assertEquals("test-actee-type", entity.getActeeType());
        assertEquals("test-actor", entity.getActor());
        assertEquals("test-actor-name", entity.getActorName());
        assertEquals("test-actor-type", entity.getActorType());
        assertEquals(metadatas, entity.getMetadatas());
        assertEquals("test-organization-id", entity.getOrganizationId());
        assertEquals("test-space-id", entity.getSpaceId());
        assertEquals("test-timestamp", entity.getTimestamp());
        assertEquals("test-type", entity.getType());
    }

}
