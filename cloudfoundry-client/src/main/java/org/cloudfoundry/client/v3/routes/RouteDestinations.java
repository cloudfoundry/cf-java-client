package org.cloudfoundry.client.v3.routes;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import org.cloudfoundry.AllowNulls;
import org.cloudfoundry.client.v3.Link;

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
