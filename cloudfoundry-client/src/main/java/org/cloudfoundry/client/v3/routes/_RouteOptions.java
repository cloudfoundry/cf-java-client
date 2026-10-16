package org.cloudfoundry.client.v3.routes;

import com.fasterxml.jackson.annotation.JsonInclude;
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
     * The loadbalancing algorithm
     */
    @JsonProperty("loadbalancing")
    @Nullable
    public abstract String getLoadbalancing();

    /**
     * The hash header
     */
    @JsonProperty("hash_header")
    @Nullable
    public abstract String getHashHeader();

    /**
     * The hash balance
     */
    @JsonProperty("hash_balance")
    @Nullable
    public abstract String getHashBalance();
}
