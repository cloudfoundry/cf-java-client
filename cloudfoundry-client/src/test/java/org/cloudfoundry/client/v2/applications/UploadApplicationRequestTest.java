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

package org.cloudfoundry.client.v2.applications;

import org.cloudfoundry.client.ValidationResult;
import org.junit.Test;

import java.io.File;

import static org.cloudfoundry.client.ValidationResult.Status.INVALID;
import static org.cloudfoundry.client.ValidationResult.Status.VALID;
import static org.junit.Assert.assertEquals;

public class UploadApplicationRequestTest {

    @Test
    public void isValid() {
        ValidationResult result = UploadApplicationRequest.builder()
                .application(new File("test-file"))
                .id("test-id")
                .resource(ResourceFingerprint.builder()
                        .hash("test-hash")
                        .path("test-path")
                        .size(-1)
                        .build())
                .build()
                .isValid();

        assertEquals(VALID, result.getStatus());
    }

    @Test
    public void isValidNoApplication() {
        ValidationResult result = UploadApplicationRequest.builder()
                .id("test-id")
                .resource(ResourceFingerprint.builder()
                        .hash("test-hash")
                        .path("test-path")
                        .size(-1)
                        .build())
                .build()
                .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("application must be specified", result.getMessages().get(0));
    }

    @Test
    public void isValidNoId() {
        ValidationResult result = UploadApplicationRequest.builder()
                .application(new File("test-file"))
                .resource(ResourceFingerprint.builder()
                        .hash("test-hash")
                        .path("test-path")
                        .size(-1)
                        .build())
                .build()
                .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("id must be specified", result.getMessages().get(0));
    }

    @Test
    public void isValidNoResourceHash() {
        ValidationResult result = UploadApplicationRequest.builder()
                .application(new File("test-file"))
                .id("test-id")
                .resource(ResourceFingerprint.builder()
                        .path("test-path")
                        .size(-1)
                        .build())
                .build()
                .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("resource hash must be specified", result.getMessages().get(0));
    }

    @Test
    public void isValidNoResourcePath() {
        ValidationResult result = UploadApplicationRequest.builder()
                .application(new File("test-file"))
                .id("test-id")
                .resource(ResourceFingerprint.builder()
                        .hash("test-hash")
                        .size(-1)
                        .build())
                .build()
                .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("resource path must be specified", result.getMessages().get(0));
    }

    @Test
    public void isValidNoResourceSize() {
        ValidationResult result = UploadApplicationRequest.builder()
                .application(new File("test-file"))
                .id("test-id")
                .resource(ResourceFingerprint.builder()
                        .hash("test-hash")
                        .path("test-path")
                        .build())
                .build()
                .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("resource size must be specified", result.getMessages().get(0));
    }

    @Test
    public void isValidNoResources() {
        ValidationResult result = UploadApplicationRequest.builder()
                .application(new File("test-file"))
                .id("test-id")
                .build()
                .isValid();

        assertEquals(VALID, result.getStatus());
    }

}
