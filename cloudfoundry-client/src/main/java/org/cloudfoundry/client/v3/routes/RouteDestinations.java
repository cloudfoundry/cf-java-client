package org.cloudfoundry.client.v3.routes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.AllowNulls;
import org.cloudfoundry.client.v3.Link;

import java.util.List;
import java.util.Map;

/**
 * The Route Destinations
 */
public abstract class RouteDestinations {

    /**
     * Links to related resources and actions for the resource
     */
    @AllowNulls
    @JsonProperty("links")
    public abstract Map<String, Link> getLinks();

    /**
     * The destinations
     */
    @JsonProperty("destinations")
    abstract List<Destination> getDestinations();

}
