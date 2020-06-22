package org.cloudfoundry.client.v3.routes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.immutables.value.Value;

/**
 * The request payload for the Delete Route operation
 */
@Value.Immutable
abstract class _DeleteRouteRequest {

    /**
     * The route id
     */
    @JsonIgnore
    abstract String getRouteId();

}
