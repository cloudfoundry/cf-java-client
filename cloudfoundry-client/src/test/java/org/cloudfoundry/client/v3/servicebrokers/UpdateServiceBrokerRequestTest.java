package org.cloudfoundry.client.v3.servicebrokers;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

final class UpdateServiceBrokerRequestTest {

    @Test
    void valid() {
        UpdateServiceBrokerRequest.builder().serviceBrokerId("test-service-broker-id").build();
    }

    @Test
    void noServiceBrokerId() {
        assertThrows(
                IllegalStateException.class,
                () -> {
                    UpdateServiceBrokerRequest.builder().build();
                });
    }
}
