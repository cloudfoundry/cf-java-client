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

package org.cloudfoundry.routing.v1.routergroups;

import org.junit.Test;

public class UpdateRouterGroupRequestTest {

    @Test(expected = IllegalStateException.class)
    public void noId() {
        UpdateRouterGroupRequest.builder()
            .reservablePorts("999-9999")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noReservablePorts() {
        UpdateRouterGroupRequest.builder()
            .routerGroupId("test-router-group-id")
            .build();
    }

    @Test
    public void valid() {
        UpdateRouterGroupRequest.builder()
            .reservablePorts("999-9999")
            .routerGroupId("test-router-group-id")
            .build();
    }

}
