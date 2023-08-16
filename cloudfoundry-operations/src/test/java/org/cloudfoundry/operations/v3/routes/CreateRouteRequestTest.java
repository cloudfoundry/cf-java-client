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

package org.cloudfoundry.operations.v3.routes;

import org.cloudfoundry.operations.AbstractOperationsTest;
import java.lang.IllegalStateException;

import org.junit.Test;

import static org.cloudfoundry.operations.TestObjects.fill;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;
import org.mockito.MockedStatic;

public final class CreateRouteRequestTest extends AbstractOperationsTest {

        @Test(expected = IllegalStateException.class)
        public void noDomainRequest() {
                CreateRouteRequest.builder()
                                .space("test-space")
                                .build();
        }

        @Test(expected = IllegalStateException.class)
        public void noSpaceRequest() {
                CreateRouteRequest.builder()
                                .domain("test-domain")
                                .host("test-hostname")
                                .build();
        }

        @Test(expected = IllegalStateException.class)
        public void setupRequestConflict() {
                CreateRouteRequest.builder()
                                .domain("test-domain")
                                .host("test-hostname")
                                .port(123)
                                .space("test-space")
                                .build();
        }

        @Test
        public void validRequest() {
                CreateRouteRequest.builder()
                                .domain("test-domain")
                                .space("test-space")
                                .build();
        }

}
