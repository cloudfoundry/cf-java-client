package org.cloudfoundry.client.v3.securitygroups;

import org.junit.Test;

public class GetSecurityGroupRequestTest {

    @Test(expected = IllegalStateException.class)
    public void noSecurityGroupId() {
        GetSecurityGroupRequest.builder()
                .build();
    }

    @Test
    public void valid() {
        GetSecurityGroupRequest.builder()
                .securityGroupId("b85a788e-671f-4549-814d-e34cdb2f539a")
                .build();
    }
}