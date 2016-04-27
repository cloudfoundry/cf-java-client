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

package org.cloudfoundry.operations.buildpacks;

import org.cloudfoundry.ValidationResult;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static org.cloudfoundry.ValidationResult.Status.INVALID;
import static org.cloudfoundry.ValidationResult.Status.VALID;
import static org.junit.Assert.assertEquals;

public class CreateBuildpackRequestTest {

    @Test
    public void isNotValidNoBuildpack() {
        ValidationResult result = CreateBuildpackRequest.builder()
            .fileName("test-file-name")
            .name("test-name")
            .position(0)
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("buildpack must be specified", result.getMessages().get(0));
    }

    @Test
    public void isNotValidNoFilename() {
        ValidationResult result = CreateBuildpackRequest.builder()
            .buildpack(new ByteArrayInputStream(new byte[0]))
            .name("test-name")
            .position(0)
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("file name must be specified", result.getMessages().get(0));
    }

    @Test
    public void isNotValidNoName() {
        ValidationResult result = CreateBuildpackRequest.builder()
            .buildpack(new ByteArrayInputStream(new byte[0]))
            .fileName("test-file-name")
            .position(0)
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("name must be specified", result.getMessages().get(0));
    }

    @Test
    public void isNotValidNoPosition() {
        ValidationResult result = CreateBuildpackRequest.builder()
            .buildpack(new ByteArrayInputStream(new byte[0]))
            .fileName("test-file-name")
            .name("test-name")
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("position must be specified", result.getMessages().get(0));
    }

    @Test
    public void isValid() {
        ValidationResult result = CreateBuildpackRequest.builder()
            .buildpack(new ByteArrayInputStream(new byte[0]))
            .fileName("test-file-name")
            .name("test-name")
            .position(0)
            .build()
            .isValid();

        assertEquals(VALID, result.getStatus());
    }

}
