package org.cloudfoundry.client.v3.routes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

/**
 * The request payload for the Replace Route Destination operation
 */
@JsonSerialize
@Value.Immutable
abstract class _ReplaceRouteDestinationsRequest {

    /**
     * The destinations for the route
     */
    @JsonProperty("destinations")
    abstract List<Destination> getDestinations();

    /**
     * The route id
     */
    @JsonIgnore
    abstract String getRouteId();

}
