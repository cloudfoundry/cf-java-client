package org.cloudfoundry.client.v3.processes;

import org.junit.Test;

public class HealthCheckTypeTest {

    @Test
    public void ensureJsonBackwardsCompatibilityForNone() throws Exception {
    	HealthCheckType.from("none");
    }
}
