/*
 * Copyright 2013-2022 the original author or authors.
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

package org.cloudfoundry.client.v3.serviceinstances;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.junit.jupiter.api.Test;

class CreateServiceInstanceRequestTest {

    @Test
    void validManagedServiceInstance() {
        CreateServiceInstanceRequest.builder()
                .type(ServiceInstanceType.MANAGED)
                .name("test-service-instance-name")
                .relationships(
                        ServiceInstanceRelationships.builder()
                                .servicePlan(
                                        ToOneRelationship.builder()
                                                .data(
                                                        Relationship.builder()
                                                                .id("test-service-plan-id")
                                                                .build())
                                                .build())
                                .space(
                                        ToOneRelationship.builder()
                                                .data(
                                                        Relationship.builder()
                                                                .id("test-space-id")
                                                                .build())
                                                .build())
                                .build())
                .tags("foo", "bar")
                .parameter("key", "value")
                .build();
    }

    @Test
    void validUserProvidedServiceInstance() {
        CreateServiceInstanceRequest.builder()
                .type(ServiceInstanceType.USER_PROVIDED)
                .syslogDrainUrl("https://syslog.com")
                .routeServiceUrl("https://route.com")
                .credential("key", "value")
                .name("test-user-provided-name")
                .build();
    }

    @Test
    void withMissingName() {
        assertThrows(
                IllegalStateException.class,
                () -> {
                    CreateServiceInstanceRequest.builder()
                            .type(ServiceInstanceType.MANAGED)
                            .relationships(
                                    ServiceInstanceRelationships.builder()
                                            .servicePlan(
                                                    ToOneRelationship.builder()
                                                            .data(
                                                                    Relationship.builder()
                                                                            .id(
                                                                                    "test-service-plan-id")
                                                                            .build())
                                                            .build())
                                            .space(
                                                    ToOneRelationship.builder()
                                                            .data(
                                                                    Relationship.builder()
                                                                            .id("test-space-id")
                                                                            .build())
                                                            .build())
                                            .build())
                            .tags("foo", "bar")
                            .parameter("key", "value")
                            .build();
                });
    }
}
