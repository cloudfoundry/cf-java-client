package org.cloudfoundry.client.v3.routes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.immutables.value.Value;

/**
 * The request payload for the Remove Route Destination operation
 */
@Value.Immutable
abstract class _RemoveRouteDestinationsRequest {

    /**
     * The destination id
     */
    @JsonIgnore
    abstract String getDestinationId();

    /**
     * The route id
     */
    @JsonIgnore
    abstract String getRouteId();

}
