/*
 * Copyright 2013-2015 the original author or authors.
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

package org.cloudfoundry.client.v3.packages;

import org.cloudfoundry.client.ValidationResult;
import org.junit.Test;

import static org.cloudfoundry.client.ValidationResult.Status.INVALID;
import static org.cloudfoundry.client.ValidationResult.Status.VALID;
import static org.cloudfoundry.client.v3.packages.CreatePackageRequest.PackageType.BITS;
import static org.cloudfoundry.client.v3.packages.CreatePackageRequest.PackageType.DOCKER;
import static org.junit.Assert.assertEquals;

public final class CreatePackageRequestTest {

    @Test
    public void isValid() {
        ValidationResult result = CreatePackageRequest.builder()
                .applicationId("test-application-id")
                .type(BITS)
                .build()
                .isValid();

        assertEquals(VALID, result.getStatus());
    }

    @Test
    public void isValidBitsAndUrl() {
        ValidationResult result = CreatePackageRequest.builder()
                .applicationId("test-application-id")
                .type(BITS)
                .url("test-url")
                .build()
                .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("url must only be specified if type is DOCKER", result.getMessages().get(0));
    }

    @Test
    public void isValidDockerNoUrl() {
        ValidationResult result = CreatePackageRequest.builder()
                .applicationId("test-application-id")
                .type(DOCKER)
                .build()
                .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("url must be specified if type is DOCKER", result.getMessages().get(0));
    }

    @Test
    public void isValidNoId() {
        ValidationResult result = CreatePackageRequest.builder()
                .type(BITS)
                .build()
                .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("applicationId must be specified", result.getMessages().get(0));
    }

    @Test
    public void isValidNoType() {
        ValidationResult result = CreatePackageRequest.builder()
                .applicationId("test-application-id")
                .build()
                .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("type must be specified", result.getMessages().get(0));
    }

}
