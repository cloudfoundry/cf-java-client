package org.cloudfoundry.client.v3.servicebrokers;

import org.junit.Test;

public final class UpdateServiceBrokerRequestTest {

    @Test
    public void valid() {
	UpdateServiceBrokerRequest.builder()
		.serviceBrokerId("test-service-broker-id")
		.build();
    }
    
    @Test(expected = IllegalStateException.class)
    public void noServiceBrokerId() {
	UpdateServiceBrokerRequest.builder().build();
    }
    
}
