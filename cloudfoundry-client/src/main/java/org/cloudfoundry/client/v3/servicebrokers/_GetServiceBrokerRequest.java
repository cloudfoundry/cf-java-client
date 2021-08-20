package org.cloudfoundry.client.v3.servicebrokers;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Value.Immutable
abstract class _GetServiceBrokerRequest {

    @JsonIgnore
    abstract String getServiceBrokerId();
    
}
