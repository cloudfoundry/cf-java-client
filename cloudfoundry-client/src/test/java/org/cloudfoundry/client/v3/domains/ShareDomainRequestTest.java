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

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.cloudfoundry.client.v3.Relationship;
import org.junit.jupiter.api.Test;

final class ShareDomainRequestTest {

    @Test
    void emptyRelationship() {
        ShareDomainRequest.builder()
                .domainId("test-domain-id")
                .data(Relationship.builder().build())
                .build();
    }

    @Test
    void noDomainId() {
        assertThrows(
                IllegalStateException.class,
                () -> {
                    ShareDomainRequest.builder()
                            .data(Relationship.builder().id("shared-organization-id").build())
                            .build();
                });
    }

    @Test
    void valid() {
        ShareDomainRequest.builder()
                .domainId("test-domain-id")
                .data(Relationship.builder().id("shared-organization-id").build())
                .build();
    }
}
