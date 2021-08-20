package org.cloudfoundry.client.v3.servicebrokers;

import org.junit.Test;

public final class DeleteServiceBrokerRequestTest {

    @Test
    public void valid() {
	DeleteServiceBrokerRequest.builder()
		.serviceBrokerId("test-service-broker-id")
		.build();
    }
    
    @Test(expected = IllegalStateException.class)
    public void noServiceBrokerId() {
	DeleteServiceBrokerRequest.builder().build();
    }
    
}
