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

package org.cloudfoundry.operations.applications;

import org.junit.Test;

import java.nio.file.Paths;

public final class PushApplicationRequestTest {

    @Test(expected = IllegalStateException.class)
    public void noApplication() {
        PushApplicationRequest.builder()
            .name("test-name")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noName() {
        PushApplicationRequest.builder()
            .application(Paths.get("test-application"))
            .build();
    }

    @Test
    public void valid() {
        PushApplicationRequest.builder()
            .application(Paths.get("test-application"))
            .name("test-name")
            .build();
    }

}
