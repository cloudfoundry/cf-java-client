package org.cloudfoundry.client.v3.securitygroups;

import org.junit.Test;

public class DeleteSecurityGroupRequestTest {

    @Test(expected = IllegalStateException.class)
    public void noSecurityGroupId() {
        DeleteSecurityGroupRequest.builder()
                .build();
    }

    @Test
    public void valid() {
        DeleteSecurityGroupRequest.builder()
                .securityGroupId("b85a788e-671f-4549-814d-e34cdb2f539a")
                .build();
    }
}