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

package org.cloudfoundry.operations.applications;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

final class PushApplicationRequestTest {

    @Test
    void applicationAndDocker() {
        assertThrows(
                IllegalStateException.class,
                () -> {
                    PushApplicationRequest.builder()
                            .path(Paths.get("test-application"))
                            .dockerImage("test-docker")
                            .name("test-name")
                            .build();
                });
    }

    @Test
    void noApplicationOrDocker() {
        assertThrows(
                IllegalStateException.class,
                () -> {
                    PushApplicationRequest.builder().name("test-name").build();
                });
    }

    @Test
    void noName() {
        assertThrows(
                IllegalStateException.class,
                () -> {
                    PushApplicationRequest.builder().path(Paths.get("test-application")).build();
                });
    }

    @Test
    void validApplication() {
        PushApplicationRequest.builder()
                .path(Paths.get("test-application"))
                .name("test-name")
                .build();
    }

    @Test
    void validDocker() {
        PushApplicationRequest.builder().dockerImage("test-docker").name("test-name").build();
    }
}
