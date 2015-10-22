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

package org.cloudfoundry.client.v2.serviceinstances;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class ListServiceInstancesResponseTest {

    @Test
    public void test() {
        Map<String, Object> credentials = new HashMap<>();
        credentials.put("test-key-1", "test-value-1");
        credentials.put("test-key-2", "test-value-2");

        ListServiceInstancesResponse.ListServiceInstancesResponseEntity.LastOperation lastOperation =
                new ListServiceInstancesResponse.ListServiceInstancesResponseEntity.LastOperation()
                        .withCreatedAt("test-created-at")
                        .withDescription("test-description")
                        .withState("test-state")
                        .withType("test-type")
                        .withUpdatedAt("test-updated-at");

        ListServiceInstancesResponse.ListServiceInstancesResponseEntity entity =
                new ListServiceInstancesResponse.ListServiceInstancesResponseEntity()
                        .withCredential("test-key-1", "test-value-1")
                        .withCredentials(Collections.singletonMap("test-key-2", "test-value-2"))
                        .withDashboardUrl("test-dashboard-url")
                        .withLastOperation(lastOperation)
                        .withName("test-name")
                        .withRoutesUrl("test-routes-url")
                        .withServiceBindingsUrl("test-service-bindings-url")
                        .withServiceKeysUrl("test-service-keys-url")
                        .withServicePlanId("test-service-plan-id")
                        .withServicePlanUrl("test-service-plan-url")
                        .withSpaceId("test-space-id")
                        .withSpaceUrl("test-space-url")
                        .withTag("test-tag-1")
                        .withTags(Collections.singletonList("test-tag-2"))
                        .withType("test-type");

        assertEquals(credentials, entity.getCredentials());
        assertEquals("test-dashboard-url", entity.getDashboardUrl());

        ListServiceInstancesResponse.ListServiceInstancesResponseEntity.LastOperation actualLastOperation =
                entity.getLastOperation();
        assertEquals("test-created-at", actualLastOperation.getCreatedAt());
        assertEquals("test-description", actualLastOperation.getDescription());
        assertEquals("test-state", actualLastOperation.getState());
        assertEquals("test-type", actualLastOperation.getType());
        assertEquals("test-updated-at", actualLastOperation.getUpdatedAt());

        assertEquals("test-name", entity.getName());
        assertEquals("test-routes-url", entity.getRoutesUrl());
        assertEquals("test-service-bindings-url", entity.getServiceBindingsUrl());
        assertEquals("test-service-keys-url", entity.getServiceKeysUrl());
        assertEquals("test-service-plan-id", entity.getServicePlanId());
        assertEquals("test-service-plan-url", entity.getServicePlanUrl());
        assertEquals("test-space-id", entity.getSpaceId());
        assertEquals("test-space-url", entity.getSpaceUrl());
        assertEquals(Arrays.asList("test-tag-1", "test-tag-2"), entity.getTags());
        assertEquals("test-type", entity.getType());

        ListServiceInstancesResponse.ListServiceInstancesResponseResource resource
                = new ListServiceInstancesResponse.ListServiceInstancesResponseResource()
                .withEntity(entity);

        ListServiceInstancesResponse response = new ListServiceInstancesResponse()
                .withResource(resource);

        assertTrue(response.getResources() != null && response.getResources().size() == 1);
        assertEquals(entity, response.getResources().get(0).getEntity());
    }

}
