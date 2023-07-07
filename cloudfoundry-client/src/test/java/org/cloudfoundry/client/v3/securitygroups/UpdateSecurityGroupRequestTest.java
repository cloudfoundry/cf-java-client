package org.cloudfoundry.client.v3.securitygroups;

import org.junit.Test;

public class UpdateSecurityGroupRequestTest {

        @Test(expected = IllegalStateException.class)
        public void noName() {
                UpdateSecurityGroupRequest.builder()
                                .build();
        }

        @Test()
        public void valid() {
                UpdateSecurityGroupRequest.builder()
                                .name("my-group0")
                                .securityGroupId("b85a788e-671f-4549-814d-e34cdb2f539a")
                                .globallyEnabled(GloballyEnabled
                                                .builder()
                                                .running(true)
                                                .build())
                                .rules(Rule.builder()
                                                .protocol(Protocol.TCP)
                                                .destination("10.10.10.0/24")
                                                .ports("443,80,8080")
                                                .build())
                                .rules(Rule.builder()
                                                .protocol(Protocol.ICMP)
                                                .destination("10.10.10.0/24")
                                                .description("Allow ping requests to private services")
                                                .type(8)
                                                .code(0)
                                                .build())
                                .build();

        }

}