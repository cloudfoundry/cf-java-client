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
import static org.junit.Assert.assertTrue;

public class CreateBuildpackRequestTest {

    @Test
    public void isValid() {

        ValidationResult result = CreateBuildpackRequest.builder()
            .name("go-buildpack")
            .fileName("buildpack.zip")
            .buildpack(new ByteArrayInputStream(new byte[0]))
            .position(1)
            .enable(true)
            .build()
            .isValid();

        assertEquals(VALID, result.getStatus());
    }

    @Test
    public void isValidDefaultEnable() {

        CreateBuildpackRequest buildpackRequest = CreateBuildpackRequest.builder()
            .name("go-buildpack")
            .fileName("buildpack.zip")
            .buildpack(new ByteArrayInputStream(new byte[0]))
            .position(1)
            .build();
        ValidationResult result = buildpackRequest.isValid();

        assertTrue(buildpackRequest.getEnable() == true);
        assertEquals(VALID, result.getStatus());
    }

    @Test
    public void isValidDisabledBuildpack() {

        CreateBuildpackRequest buildpackRequest = CreateBuildpackRequest.builder()
            .name("go-buildpack")
            .fileName("buildpack.zip")
            .buildpack(new ByteArrayInputStream(new byte[0]))
            .position(1)
            .enable(false)
            .build();
        ValidationResult result = buildpackRequest.isValid();

        assertTrue(buildpackRequest.getEnable() == false);
        assertEquals(VALID, result.getStatus());
    }

    @Test
    public void isInValid() {
        ValidationResult result = CreateBuildpackRequest.builder()
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertTrue(result.getMessages().size() == 4);
        assertEquals("name must be specified", result.getMessages().get(0));
        assertEquals("file name must be specified", result.getMessages().get(1));
        assertEquals("buildpack must be specified", result.getMessages().get(2));
        assertEquals("position must be specified", result.getMessages().get(3));
    }

    @Test
    public void isInValidNoBuildpack() {
        ValidationResult result = CreateBuildpackRequest.builder()
            .name("go-buildpack")
            .fileName("buildpack.zip")
            .position(1)
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertTrue(result.getMessages().size() == 1);
        assertEquals("buildpack must be specified", result.getMessages().get(0));
    }

    @Test
    public void isInValidNoFileName() {
        ValidationResult result = CreateBuildpackRequest.builder()
            .name("go-buildpack")
            .buildpack(new ByteArrayInputStream(new byte[0]))
            .position(1)
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertTrue(result.getMessages().size() == 1);
        assertEquals("file name must be specified", result.getMessages().get(0));
    }

    @Test
    public void isInValidNoPosition() {
        ValidationResult result = CreateBuildpackRequest.builder()
            .name("go-buildpack")
            .fileName("buildpack.zip")
            .buildpack(new ByteArrayInputStream(new byte[0]))
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertTrue(result.getMessages().size() == 1);
        assertEquals("position must be specified", result.getMessages().get(0));
    }


}
