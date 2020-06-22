package org.cloudfoundry.client.v3.routes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.Metadata;
import org.immutables.value.Value;

/**
 * The request payload for the Create Route operation
 */
@JsonSerialize
@Value.Immutable
abstract class _CreateRouteRequest {

    /**
     * The host
     */
    @Nullable
    @JsonProperty("host")
    abstract String getHost();

    /**
     * The metadata
     */
    @Nullable
    @JsonProperty("metadata")
    abstract Metadata getMetadata();

    /**
     * The path
     */
    @Nullable
    @JsonProperty("path")
    abstract String getPath();

    /**
     * The port
     */
    @JsonProperty("port")
    @Nullable
    abstract Integer getPort();

    /**
     * The relationships
     */
    @JsonProperty("relationships")
    abstract RouteRelationships getRelationships();

}
