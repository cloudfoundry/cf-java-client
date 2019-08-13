package org.cloudfoundry.client.v2.serviceinstances;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The request payload for the Get Parameters operation
 */
@Value.Immutable
abstract class _GetServiceInstanceParametersRequest {

    /**
     * The service instance id
     */
    @JsonIgnore
    abstract String getServiceInstanceId();

}
