package org.cloudfoundry.client.v3.routes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * Represents a Route Options
 */
@JsonDeserialize
@Value.Immutable
abstract class _RouteOptions {

    /**
     * The loadbalancing
     */
    @JsonProperty("loadbalancing")
    @Nullable
    public abstract String getLoadbalancing();

}
