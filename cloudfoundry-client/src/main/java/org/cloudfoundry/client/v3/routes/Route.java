package org.cloudfoundry.client.v3.routes;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.Metadata;
import org.cloudfoundry.client.v3.Resource;

/**
 * Base class for responses that are routes
 */
public abstract class Route extends Resource {

    /**
     * The host
     */
    @JsonProperty("host")
    public abstract String getHost();

    /**
     * The path
     */
    @JsonProperty("path")
    public abstract String getPath();

    /**
     * The url
     */
    @JsonProperty("url")
    public abstract String getUrl();

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
    public abstract Metadata getMetadata();
}
