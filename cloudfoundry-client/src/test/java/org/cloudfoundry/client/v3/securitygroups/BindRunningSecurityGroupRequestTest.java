package org.cloudfoundry.client.v3.securitygroups;

import org.cloudfoundry.client.v3.ToManyRelationship;
import org.cloudfoundry.client.v3.Relationship;
import org.junit.Test;

public class BindRunningSecurityGroupRequestTest {

        @Test(expected = IllegalStateException.class)
        public void noSecurityGroupId() {
                BindRunningSecurityGroupRequest.builder()
                                .build();
        }

        @Test
        public void valid() {
                BindRunningSecurityGroupRequest.builder()
                                .securityGroupId("b85a788e-671f-4549-814d-e34cdb2f539a")
                                .boundSpaces(ToManyRelationship.builder()
                                                .data(Relationship.builder()
                                                                .id("space-guid-1")
                                                                .build())
                                                .build())
                                .build();
        }
}
