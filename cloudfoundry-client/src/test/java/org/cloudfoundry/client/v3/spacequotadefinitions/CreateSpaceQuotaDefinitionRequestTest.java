/*
 * Copyright 2013-2025 the original author or authors.
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

package org.cloudfoundry.client.v3.spacequotadefinitions;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.junit.jupiter.api.Test;

final class CreateSpaceQuotaDefinitionRequestTest {

    @Test
    void noName() {
        assertThrows(
                IllegalStateException.class,
                () -> CreateSpaceQuotaDefinitionRequest.builder().build());
    }

    @Test
    void noOrganizationsRelationship() {
        assertThrows(
                IllegalStateException.class,
                () -> CreateSpaceQuotaDefinitionRequest.builder().name("test-quota").build());
    }

    @Test
    void valid() {

        String organizationGuid = UUID.randomUUID().toString();
        ToOneRelationship organizationsRelationship =
                ToOneRelationship.builder()
                        .data(Relationship.builder().id(organizationGuid).build())
                        .build();
        SpaceQuotaDefinitionRelationships relationships =
                SpaceQuotaDefinitionRelationships.builder()
                        .organization(organizationsRelationship)
                        .build();
        CreateSpaceQuotaDefinitionRequest.builder()
                .name("test-quota")
                .relationships(relationships)
                .build();
    }
}
