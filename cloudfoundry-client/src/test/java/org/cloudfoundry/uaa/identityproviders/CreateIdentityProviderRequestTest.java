/*
 * Copyright 2013-2020 the original author or authors.
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

public final class CreateIdentityProviderRequestTest {

    @Test(expected = IllegalStateException.class)
    public void noName() {
        CreateIdentityProviderRequest.builder()
            .configuration(SamlConfiguration.builder()
                .metaDataLocation("test-metadata-location")
                .build())
            .identityZoneId("test-identity-zone-id")
            .type(Type.SAML)
            .originKey("test-origin-key")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noOriginKey() {
        CreateIdentityProviderRequest.builder()
            .configuration(SamlConfiguration.builder()
                .metaDataLocation("test-metadata-location")
                .build())
            .identityZoneId("test-identity-zone-id")
            .name("test-name")
            .type(Type.SAML)
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noType() {
        CreateIdentityProviderRequest.builder()
            .configuration(SamlConfiguration.builder()
                .metaDataLocation("test-metadata-location")
                .build())
            .identityZoneId("test-identity-zone-id")
            .name("test-name")
            .originKey("test-origin-key")
            .build();
    }

    @Test
    public void valid() {
        CreateIdentityProviderRequest.builder()
            .configuration(SamlConfiguration.builder()
                .metaDataLocation("test-metadata-location")
                .build())
            .identityZoneId("test-identity-zone-id")
            .name("test-name")
            .originKey("test-origin-key")
            .type(Type.SAML)
            .build();
    }

}
