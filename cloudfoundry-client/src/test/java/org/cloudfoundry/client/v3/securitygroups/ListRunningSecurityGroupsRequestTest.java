package org.cloudfoundry.client.v3.securitygroups;

import org.junit.Test;

public class ListRunningSecurityGroupsRequestTest {

    @Test(expected = IllegalStateException.class)
    public void noSpaceID() {
        ListRunningSecurityGroupsRequest.builder()
                .build();
    }

    @Test
    public void valid() {
        ListRunningSecurityGroupsRequest.builder()
                .spaceId("space-giud1")
                .build();
    }

}