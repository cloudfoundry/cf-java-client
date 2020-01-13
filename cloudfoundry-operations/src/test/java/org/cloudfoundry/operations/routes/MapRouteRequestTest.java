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

package org.cloudfoundry.operations.routes;

import org.junit.Test;

public final class MapRouteRequestTest {

    @Test(expected = IllegalStateException.class)
    public void noApplicationName() {
        MapRouteRequest.builder()
            .domain("test-domain")
            .host("test-host")
            .path("test-path")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noDomain() {
        MapRouteRequest.builder()
            .applicationName("test-applicationName")
            .host("test-host")
            .path("test-path")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void portConflict() {
        MapRouteRequest.builder()
            .domain("test-domain")
            .port(123)
            .randomPort(true)
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void setupConflict() {
        MapRouteRequest.builder()
            .domain("test-domain")
            .host("test-hostname")
            .port(123)
            .build();
    }

    @Test
    public void validMax() {
        MapRouteRequest.builder()
            .applicationName("test-applicationName")
            .domain("test-domain")
            .host("test-host")
            .path("test-path")
            .build();
    }

    @Test
    public void validMin() {
        MapRouteRequest.builder()
            .applicationName("test-applicationName")
            .domain("test-domain")
            .build();
    }


}
