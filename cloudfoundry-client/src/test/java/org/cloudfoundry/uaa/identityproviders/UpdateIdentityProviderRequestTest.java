/*
 * Copyright 2013-2016 the original author or authors.
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

import org.junit.Test;

public final class UpdateIdentityProviderRequestTest {

    @Test(expected = IllegalStateException.class)
    public void noIdentityProviderId() {
        UpdateIdentityProviderRequest.builder()
            .configuration(InternalConfiguration.builder()
                .build())
            .identityZoneId("test-identity-zone-id")
            .name("test-name")
            .originKey("test-origin-key")
            .type(Type.INTERNAL)
            .version(0)
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noIdentityZoneId() {
        UpdateIdentityProviderRequest.builder()
            .configuration(InternalConfiguration.builder()
                .build())
            .identityProviderId("test-identity-provider-id")
            .name("test-name")
            .originKey("test-origin-key")
            .type(Type.INTERNAL)
            .version(0)
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noName() {
        UpdateIdentityProviderRequest.builder()
            .configuration(InternalConfiguration.builder()
                .build())
            .identityZoneId("test-identity-zone-id")
            .identityProviderId("test-identity-provider-id")
            .originKey("test-origin-key")
            .type(Type.INTERNAL)
            .version(0)
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noOriginKey() {
        UpdateIdentityProviderRequest.builder()
            .configuration(InternalConfiguration.builder()
                .build())
            .identityZoneId("test-identity-zone-id")
            .identityProviderId("test-identity-provider-id")
            .name("test-name")
            .type(Type.INTERNAL)
            .version(0)
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noType() {
        UpdateIdentityProviderRequest.builder()
            .configuration(InternalConfiguration.builder()
                .build())
            .identityZoneId("test-identity-zone-id")
            .identityProviderId("test-identity-provider-id")
            .name("test-name")
            .originKey("test-origin-key")
            .version(0)
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noVersion() {
        UpdateIdentityProviderRequest.builder()
            .configuration(InternalConfiguration.builder()
                .build())
            .identityZoneId("test-identity-zone-id")
            .identityProviderId("test-identity-provider-id")
            .name("test-name")
            .originKey("test-origin-key")
            .type(Type.INTERNAL)
            .build();
    }

    @Test
    public void valid() {
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