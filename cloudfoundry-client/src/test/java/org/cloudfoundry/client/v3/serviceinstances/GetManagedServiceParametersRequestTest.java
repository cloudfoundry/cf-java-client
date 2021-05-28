package org.cloudfoundry.client.v3.serviceinstances;

import org.junit.Test;

public class GetManagedServiceParametersRequestTest {

    @Test
    public void valid() {
	GetManagedServiceParametersRequest.builder()
		.serviceInstanceId("test-service-instance-id")
		.build();
    }
    
}
