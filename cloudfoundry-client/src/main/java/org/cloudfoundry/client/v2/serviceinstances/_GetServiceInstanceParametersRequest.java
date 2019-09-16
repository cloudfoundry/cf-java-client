package org.cloudfoundry.client.v2.serviceinstances;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.immutables.value.Value;

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
