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

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertThrows;

final class ApplicationManifestTest {

    @Test
    void dockerAndBuildpack() {
        assertThrows(IllegalStateException.class, () -> {
            ApplicationManifest.builder()
                .name("test-name")
                .buildpack("test-buildpack")
                .docker(Docker.builder()
                    .image("test-docker-image")
                    .build())
                .build();
        });
    }

    @Test
    void dockerAndPath() {
        assertThrows(IllegalStateException.class, () -> {
            ApplicationManifest.builder()
                .name("test-name")
                .docker(Docker.builder()
                    .image("test-docker-image")
                    .build())
                .path(Paths.get("test-application"))
                .build();
        });
    }

    @Test
    void dockerCredentialsNoImage() {
        assertThrows(IllegalStateException.class, () -> {
            ApplicationManifest.builder()
                .name("test-name")
                .docker(Docker.builder()
                    .password("test-password")
                    .username("test-username")
                    .build())
                .build();
        });
    }

    @Test
    void routesAndDomains() {
        assertThrows(IllegalStateException.class, () -> {
            ApplicationManifest.builder()
                .name("test-name")
                .route(Route.builder()
                    .route("test-route")
                    .build())
                .domain("test-domain")
                .build();
        });
    }

    @Test
    void routesAndHosts() {
        assertThrows(IllegalStateException.class, () -> {
            ApplicationManifest.builder()
                .name("test-name")
                .route(Route.builder()
                    .route("test-route")
                    .build())
                .host("test-host")
                .build();
        });
    }

    @Test
    void routesAndNoHostName() {
        assertThrows(IllegalStateException.class, () -> {
            ApplicationManifest.builder()
                .name("test-name")
                .route(Route.builder()
                    .route("test-route")
                    .build())
                .noHostname(true)
                .build();
        });
    }

    @Test
    void valid() {
        ApplicationManifest.builder()
            .name("test-name")
            .build();
    }
}
