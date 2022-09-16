/*
 * Copyright 2013-2022 the original author or authors.
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

import org.junit.Test;

import java.nio.file.Paths;

public class ManifestV3ApplicationTest {

    @Test(expected = IllegalStateException.class)
    public void dockerAndBuildpack() {
        ManifestV3Application.builder()
            .name("test-name")
            .buildpack("test-buildpack")
            .docker(Docker.builder()
                .image("test-docker-image")
                .build())
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void dockerAndPath() {
        ManifestV3Application.builder()
            .name("test-name")
            .docker(Docker.builder()
                .image("test-docker-image")
                .build())
            .path(Paths.get("test-application"))
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void dockerCredentialsNoImage() {
        ManifestV3Application.builder()
            .name("test-name")
            .docker(Docker.builder()
                .password("test-password")
                .username("test-username")
                .build())
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void routesAndDomains() {
        ManifestV3Application.builder()
            .name("test-name")
            .route(Route.builder()
                .route("test-route")
                .build())
            .domain("test-domain")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void routesAndHosts() {
        ManifestV3Application.builder()
            .name("test-name")
            .route(Route.builder()
                .route("test-route")
                .build())
            .host("test-host")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void routesAndNoHostName() {
        ManifestV3Application.builder()
            .name("test-name")
            .route(Route.builder()
                .route("test-route")
                .build())
            .noHostname(true)
            .build();
    }

    @Test
    public void valid() {
        ManifestV3Application.builder()
            .name("test-name")
            .build();
    }
}