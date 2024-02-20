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

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class UpdateSecurityGroupRequestTest {

    @Test
    public void noName() {
        assertThrows(
                IllegalStateException.class,
                () -> {
                    UpdateSecurityGroupRequest.builder().build();
                });
    }

    @Test
    public void valid() {
        UpdateSecurityGroupRequest.builder()
                .name("my-group0")
                .securityGroupId("b85a788e-671f-4549-814d-e34cdb2f539a")
                .globallyEnabled(GloballyEnabled.builder().running(true).build())
                .rules(
                        Rule.builder()
                                .protocol(Protocol.TCP)
                                .destination("10.10.10.0/24")
                                .ports("443,80,8080")
                                .build())
                .rules(
                        Rule.builder()
                                .protocol(Protocol.ICMP)
                                .destination("10.10.10.0/24")
                                .description("Allow ping requests to private services")
                                .type(8)
                                .code(0)
                                .build())
                .build();
    }
}
