package org.cloudfoundry.client.v3.securitygroups;

import org.junit.Test;

public class UnbindStagingSecurityGroupRequestTest {

        @Test(expected = IllegalStateException.class)
        public void noSecurityGroupId() {
                UnbindStagingSecurityGroupRequest.builder()
                                .build();
        }

        @Test(expected = IllegalStateException.class)
        public void noSpaceId() {
                UnbindStagingSecurityGroupRequest.builder()
                                .securityGroupId("b85a788e-671f-4549-814d-e34cdb2f539a")
                                .build();
        }

        @Test
        public void valid() {
                UnbindStagingSecurityGroupRequest.builder()
                                .securityGroupId("b85a788e-671f-4549-814d-e34cdb2f539a")
                                .spaceId("space-guid2")
                                .build();
        }
}
