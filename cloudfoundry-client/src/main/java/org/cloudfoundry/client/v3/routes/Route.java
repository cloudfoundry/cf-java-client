package org.cloudfoundry.client.v3.routes;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.Metadata;
import org.cloudfoundry.client.v3.Resource;

import java.util.List;

/**
 * Base class for responses that are routes
 */
public abstract class Route extends Resource {

    /**
     * The destinations
     */
    @JsonProperty("destinations")
    @Nullable
    public abstract List<Destination> getDestinations();

    /**
     * The host
     */
    @JsonProperty("host")
    public abstract String getHost();

    /**
     * The metadata
     */
    @JsonProperty("metadata")
    @Nullable
    public abstract Metadata getMetadata();

    /**
     * The path
     */
    @JsonProperty("path")
    public abstract String getPath();

    /**
     * The port
     */
    @JsonProperty("port")
    @Nullable
    public abstract Integer getPort();

    /**
     * The protocol
     */
    @JsonProperty("protocol")
    @Nullable
    public abstract Protocol getProtocol();

    /**
     * The relationships
     */
    @JsonProperty("relationships")
    public abstract RouteRelationships getRelationships();

    /**
     * The url
     */
    @JsonProperty("url")
    public abstract String getUrl();

}
