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

package org.cloudfoundry.uaa.identityzones;

import org.junit.Test;

public class UpdateIdentityZoneRequestTest {

    @Test(expected = IllegalStateException.class)
    public void noIdentityZoneId() {
        UpdateIdentityZoneRequest.builder()
            .configuration(IdentityZoneConfiguration.builder().build())
            .description("test-new-description")
            .name("test-name")
            .subdomain("test-sub-domain")
            .version(1)
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noName() {
        UpdateIdentityZoneRequest.builder()
            .configuration(IdentityZoneConfiguration.builder().build())
            .description("test-new-description")
            .identityZoneId("test-id")
            .subdomain("test-sub-domain")
            .version(1)
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noSubdomain() {
        UpdateIdentityZoneRequest.builder()
            .configuration(IdentityZoneConfiguration.builder().build())
            .description("test-new-description")
            .identityZoneId("test-id")
            .name("test-name")
            .version(1)
            .build();
    }

    @Test
    public void validMax() {
        UpdateIdentityZoneRequest.builder()
            .configuration(IdentityZoneConfiguration.builder().build())
            .description("test-new-description")
            .identityZoneId("test-id")
            .name("test-name")
            .subdomain("test-sub-domain")
            .version(1)
            .build();
    }

    @Test
    public void validMin() {
        UpdateIdentityZoneRequest.builder()
            .identityZoneId("test-id")
            .name("test-name")
            .subdomain("test-sub-domain")
            .build();
    }

}
