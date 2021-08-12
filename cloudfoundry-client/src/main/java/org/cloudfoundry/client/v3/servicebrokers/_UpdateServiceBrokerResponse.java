package org.cloudfoundry.client.v3.servicebrokers;

import java.util.Optional;

import org.immutables.value.Value;

@Value.Immutable
abstract class _UpdateServiceBrokerResponse {

    public abstract Optional<String> jobId();
    
    public abstract Optional<ServiceBrokerResource> serviceBroker();
    
}
