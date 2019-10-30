package org.cloudfoundry.client.v3.routes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
public abstract class _GetRouteRequest {

    /**
     * The route id
     */
    @JsonIgnore
    abstract String getRouteId();
}
