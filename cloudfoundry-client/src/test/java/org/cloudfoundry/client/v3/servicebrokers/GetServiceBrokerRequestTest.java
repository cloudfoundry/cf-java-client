package org.cloudfoundry.client.v3.servicebrokers;

import org.junit.Test;

public final class GetServiceBrokerRequestTest {

    @Test
    public void valid() {
	GetServiceBrokerRequest.builder()
		.serviceBrokerId("test-service-broker-id")
		.build();
    }
    
    @Test(expected = IllegalStateException.class)
    public void noServiceBrokerId() {
	GetServiceBrokerRequest.builder().build();
    }
    
}
