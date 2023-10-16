package org.cloudfoundry.client.v3.servicebrokers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

final class DeleteServiceBrokerRequestTest {

    @Test
    void valid() {
	DeleteServiceBrokerRequest.builder()
		.serviceBrokerId("test-service-broker-id")
		.build();
    }

    @Test
    void noServiceBrokerId() {
        assertThrows(IllegalStateException.class, () -> {
            DeleteServiceBrokerRequest.builder().build();
        });
    }
    
}
