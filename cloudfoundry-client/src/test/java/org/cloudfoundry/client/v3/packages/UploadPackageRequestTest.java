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

package org.cloudfoundry.client.v3.packages;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

final class UploadPackageRequestTest {

    private static final Path TEST_PACKAGE = Paths.get("/");

    @Test
    void neitherBitsNorResources() {
        assertThrows(
                IllegalStateException.class,
                () -> {
                    UploadPackageRequest.builder().packageId("test-package-id").build();
                });
    }

    @Test
    void noPackageId() {
        assertThrows(
                IllegalStateException.class,
                () -> {
                    UploadPackageRequest.builder().bits(TEST_PACKAGE).build();
                });
    }

    @Test
    void valid() {
        UploadPackageRequest.builder().bits(TEST_PACKAGE).packageId("test-package-id").build();
    }
}
