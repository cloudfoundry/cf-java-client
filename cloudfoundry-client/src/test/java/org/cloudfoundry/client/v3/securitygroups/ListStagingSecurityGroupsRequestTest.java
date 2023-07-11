package org.cloudfoundry.client.v3.securitygroups;

import org.junit.Test;

public class ListStagingSecurityGroupsRequestTest {

    @Test(expected = IllegalStateException.class)
    public void noSpaceID() {
        ListStagingSecurityGroupsRequest.builder()
                .build();
    }

    @Test
    public void valid() {
        ListStagingSecurityGroupsRequest.builder()
                .spaceId("space-giud1")
                .build();
    }

}