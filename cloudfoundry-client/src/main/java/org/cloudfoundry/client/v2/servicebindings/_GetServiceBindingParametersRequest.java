package org.cloudfoundry.client.v2.servicebindings;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The request payload for the Get Service Binding Parameters operation
 */
@Value.Immutable
abstract class _GetServiceBindingParametersRequest {

    /**
     * The service binding id
     */
    @JsonIgnore
    abstract String getServiceBindingId();

}
