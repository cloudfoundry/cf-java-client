package org.cloudfoundry.client.v3.servicebrokers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

final class GetServiceBrokerRequestTest {

    @Test
    void valid() {
	GetServiceBrokerRequest.builder()
		.serviceBrokerId("test-service-broker-id")
		.build();
    }

    @Test
    void noServiceBrokerId() {
        assertThrows(IllegalStateException.class, () -> {
            GetServiceBrokerRequest.builder().build();
        });
    }
    
}
