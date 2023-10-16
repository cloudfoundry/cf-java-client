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

package org.cloudfoundry.uaa.identityproviders;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

final class UpdateIdentityProviderRequestTest {

    @Test
    void noIdentityProviderId() {
        assertThrows(IllegalStateException.class, () -> {
            UpdateIdentityProviderRequest.builder()
                .configuration(InternalConfiguration.builder()
                    .build())
                .identityZoneId("test-identity-zone-id")
                .name("test-name")
                .originKey("test-origin-key")
                .type(Type.INTERNAL)
                .version(0)
                .build();
        });
    }

    @Test
    void noName() {
        assertThrows(IllegalStateException.class, () -> {
            UpdateIdentityProviderRequest.builder()
                .configuration(InternalConfiguration.builder()
                    .build())
                .identityZoneId("test-identity-zone-id")
                .identityProviderId("test-identity-provider-id")
                .originKey("test-origin-key")
                .type(Type.INTERNAL)
                .version(0)
                .build();
        });
    }

    @Test
    void noOriginKey() {
        assertThrows(IllegalStateException.class, () -> {
            UpdateIdentityProviderRequest.builder()
                .configuration(InternalConfiguration.builder()
                    .build())
                .identityZoneId("test-identity-zone-id")
                .identityProviderId("test-identity-provider-id")
                .name("test-name")
                .type(Type.INTERNAL)
                .version(0)
                .build();
        });
    }

    @Test
    void noType() {
        assertThrows(IllegalStateException.class, () -> {
            UpdateIdentityProviderRequest.builder()
                .configuration(InternalConfiguration.builder()
                    .build())
                .identityZoneId("test-identity-zone-id")
                .identityProviderId("test-identity-provider-id")
                .name("test-name")
                .originKey("test-origin-key")
                .version(0)
                .build();
        });
    }

    @Test
    void noVersion() {
        assertThrows(IllegalStateException.class, () -> {
            UpdateIdentityProviderRequest.builder()
                .configuration(InternalConfiguration.builder()
                    .build())
                .identityZoneId("test-identity-zone-id")
                .identityProviderId("test-identity-provider-id")
                .name("test-name")
                .originKey("test-origin-key")
                .type(Type.INTERNAL)
                .build();
        });
    }

    @Test
    void valid() {
        UpdateIdentityProviderRequest.builder()
            .configuration(InternalConfiguration.builder()
                .build())
            .identityZoneId("test-identity-zone-id")
            .identityProviderId("test-identity-provider-id")
            .name("test-name")
            .originKey("test-origin-key")
            .type(Type.INTERNAL)
            .version(0)
            .build();
    }

}
