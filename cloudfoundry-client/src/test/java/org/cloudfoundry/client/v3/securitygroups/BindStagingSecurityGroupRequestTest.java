/*
 * Copyright 2013-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.cloudfoundry.client.v3.securitygroups;

import org.cloudfoundry.client.v3.Relationship;
import org.junit.Test;

public class BindStagingSecurityGroupRequestTest {

        @Test(expected = IllegalStateException.class)
        public void noSecurityGroupId() {
                BindStagingSecurityGroupRequest.builder().build();
        }

        @Test
        public void valid() {
                BindStagingSecurityGroupRequest.builder()
                                .securityGroupId("b85a788e-671f-4549-814d-e34cdb2f539a")
                                .boundSpaces(Relationship.builder().id("space-guid-1").build())

                                .build();
        }
}
