package org.cloudfoundry.client.v3.servicebrokers;

import org.junit.Test;

public final class CreateServiceBrokerRequestTest {

    @Test
    public void valid() {
	CreateServiceBrokerRequest.builder()
		.authentication(BasicAuthentication.builder()
					.username("test-username")
					.password("test-password")
					.build())
		.name("test-service-broker")
		.url("test-service-broker-url")
		.build();
    }
    
    @Test(expected = IllegalStateException.class)
    public void noAuthentication() {
	CreateServiceBrokerRequest.builder()
		.name("test-service-broker")
		.url("test-service-broker-url")
		.build();
    }
    
    @Test(expected = IllegalStateException.class)
    public void noName() {
	CreateServiceBrokerRequest.builder()
        	.authentication(BasicAuthentication.builder()
        		.username("test-username")
        		.password("test-password")
        		.build())
		.url("test-service-broker-url")
		.build();
    }
    
    @Test(expected = IllegalStateException.class)
    public void noUrl() {
	CreateServiceBrokerRequest.builder()
        	.authentication(BasicAuthentication.builder()
        				.username("test-username")
        				.password("test-password")
        				.build())
        	.name("test-service-broker")
        	.build();
    }
    
}
