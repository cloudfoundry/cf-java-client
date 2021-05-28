package org.cloudfoundry.client.v3.serviceinstances;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Value.Immutable
abstract class _GetManagedServiceParametersRequest {

    /**
     * The managed service instance id
     */
    @JsonIgnore
    abstract String getServiceInstanceId();
    
}
