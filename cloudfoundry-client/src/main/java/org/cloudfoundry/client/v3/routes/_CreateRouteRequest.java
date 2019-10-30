package org.cloudfoundry.client.v3.routes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.Metadata;
import org.immutables.value.Value;

@JsonSerialize
@Value.Immutable
public abstract class _CreateRouteRequest {

    /**
     * The host
     */
    @Nullable
    @JsonProperty("host")
    abstract String getHost();

    /**
     * The path
     */
    @Nullable
    @JsonProperty("path")
    abstract String getPath();

    /**
     * The relationships
     */
    @JsonProperty("relationships")
    abstract RouteRelationships getRelationships();

    /**
     * The metadata
     */
    @Nullable
    @JsonProperty("metadata")
    abstract Metadata getMetadata();


}
