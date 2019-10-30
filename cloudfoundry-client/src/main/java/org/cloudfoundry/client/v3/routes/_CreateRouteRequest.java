package org.cloudfoundry.client.v3.routes;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.Metadata;
import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonSerialize
@Value.Immutable
public abstract class _CreateRouteRequest {

    /**
     * The host
     */
    @Nullable
    @JsonProperty("host")
    public abstract String getHost();

    /**
     * The path
     */
    @Nullable
    @JsonProperty("path")
    public abstract String getPath();

    /**
     * The relationships
     */
    @JsonProperty("relationships")
    public abstract RouteRelationships getRelationships();

    /**
     * The metadata
     */
    @JsonProperty("metadata")
    @Nullable
    abstract Metadata getMetadata();

    
}
