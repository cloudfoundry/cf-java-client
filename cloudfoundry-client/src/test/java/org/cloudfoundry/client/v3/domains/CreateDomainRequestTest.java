/*
 * Copyright 2013-2021 the original author or authors.
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

package org.cloudfoundry.client.v3.domains;

import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.ToManyRelationship;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

final class CreateDomainRequestTest {

    @Test
    void noName() {
        assertThrows(IllegalStateException.class, () -> {
            CreateDomainRequest.builder()
                .build();
        });
    }

    @Test
    void valid() {
        CreateDomainRequest.builder()
                .name("test-domain-name")
                .internal(true)
                .relationships(
                        DomainRelationships.builder()
                                .organization(
                                        ToOneRelationship.builder()
                                                .data(
                                                        Relationship.builder()
                                                                .id("test-org-id")
                                                                .build())
                                                .build())
                                .sharedOrganizations(
                                        ToManyRelationship.builder()
                                                .data(
                                                        Relationship.builder()
                                                                .id("shared-org-id-1")
                                                                .build())
                                                .data(
                                                        Relationship.builder()
                                                                .id("shared-org-id-2")
                                                                .build())
                                                .build())
                                .build())
                .build();
    }
}
