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

package org.cloudfoundry.uaa.identityzones;

import org.cloudfoundry.ValidationResult;
import org.junit.Test;

import static org.cloudfoundry.ValidationResult.Status.INVALID;
import static org.cloudfoundry.ValidationResult.Status.VALID;
import static org.junit.Assert.assertEquals;

public class UpdateIdentityZoneRequestTest {

    @Test
    public void isNotValidNoDescription() {
        ValidationResult result = UpdateIdentityZoneRequest.builder()
            .description("test-description")
            .identityZoneId("test-id")
            .name("test-name")
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("sub domain must be specified", result.getMessages().get(0));
    }

    @Test
    public void isNotValidNoId() {
        ValidationResult result = UpdateIdentityZoneRequest.builder()
            .description("test-description")
            .name("test-name")
            .subdomain("test-sub-domain")
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("identity zone id must be specified", result.getMessages().get(0));
    }

    @Test
    public void isNotValidNoName() {
        ValidationResult result = UpdateIdentityZoneRequest.builder()
            .description("test-description")
            .identityZoneId("test-id")
            .subdomain("test-sub-domain")
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("name must be specified", result.getMessages().get(0));
    }

    @Test
    public void isNotValidNoSubdomain() {
        ValidationResult result = UpdateIdentityZoneRequest.builder()
            .description("test-description")
            .identityZoneId("test-id")
            .name("test-name")
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("sub domain must be specified", result.getMessages().get(0));
    }

    @Test
    public void isValid() {
        ValidationResult result = UpdateIdentityZoneRequest.builder()
            .description("test-description")
            .identityZoneId("test-id")
            .name("test-name")
            .subdomain("test-sub-domain")
            .build()
            .isValid();

        assertEquals(VALID, result.getStatus());
    }

}
