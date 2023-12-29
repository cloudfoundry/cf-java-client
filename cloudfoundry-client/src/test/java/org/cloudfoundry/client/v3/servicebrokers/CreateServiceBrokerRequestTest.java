package org.cloudfoundry.client.v3.servicebrokers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

final class CreateServiceBrokerRequestTest {

    @Test
    void valid() {
	CreateServiceBrokerRequest.builder()
		.authentication(BasicAuthentication.builder()
					.username("test-username")
					.password("test-password")
					.build())
		.name("test-service-broker")
		.url("test-service-broker-url")
		.build();
    }

    @Test
    void noAuthentication() {
        assertThrows(IllegalStateException.class, () -> {
            CreateServiceBrokerRequest.builder()
                .name("test-service-broker")
                .url("test-service-broker-url")
                .build();
        });
    }

    @Test
    void noName() {
        assertThrows(IllegalStateException.class, () -> {
            CreateServiceBrokerRequest.builder()
                .authentication(BasicAuthentication.builder()
                    .username("test-username")
                    .password("test-password")
                    .build())
                .url("test-service-broker-url")
                .build();
        });
    }

    @Test
    void noUrl() {
        assertThrows(IllegalStateException.class, () -> {
            CreateServiceBrokerRequest.builder()
                .authentication(BasicAuthentication.builder()
                    .username("test-username")
                    .password("test-password")
                    .build())
                .name("test-service-broker")
                .build();
        });
    }
}
