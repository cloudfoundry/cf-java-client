package org.cloudfoundry.client.v3.routes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.Metadata;
import org.immutables.value.Value;

/**
 * The request payload for the Update Route operation
 */
@JsonSerialize
@Value.Immutable
abstract class _UpdateRouteRequest {

    /**
     * The metadata
     */
    @JsonProperty("metadata")
    @Nullable
    abstract Metadata getMetadata();

    /**
     * The route id
     */
    @JsonIgnore
    abstract String getRouteId();

}
