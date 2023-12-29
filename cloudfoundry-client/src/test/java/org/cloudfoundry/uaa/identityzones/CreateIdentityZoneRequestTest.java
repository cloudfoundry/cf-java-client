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

package org.cloudfoundry.uaa.identityzones;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

final class CreateIdentityZoneRequestTest {

    @Test
    void noName() {
        assertThrows(IllegalStateException.class, () -> {
            CreateIdentityZoneRequest.builder()
                .configuration(IdentityZoneConfiguration.builder().build())
                .description("test-description")
                .identityZoneId("test-identity-zone-id")
                .subdomain("test-sub-domain")
                .version(1)
                .build();
        });
    }

    @Test
    void noSubdomain() {
        assertThrows(IllegalStateException.class, () -> {
            CreateIdentityZoneRequest.builder()
                .configuration(IdentityZoneConfiguration.builder().build())
                .description("test-description")
                .identityZoneId("test-identity-zone-id")
                .name("test-name")
                .version(1)
                .build();
        });
    }

    @Test
    void validMax() {
        CreateIdentityZoneRequest.builder()
                .configuration(IdentityZoneConfiguration.builder().build())
                .description("test-description")
                .identityZoneId("test-identity-zone-id")
                .name("test-name")
                .subdomain("test-sub-domain")
                .version(1)
                .build();
    }

    @Test
    void validMin() {
        CreateIdentityZoneRequest.builder()
            .name("test-name")
            .subdomain("test-sub-domain")
            .build();
    }
}
