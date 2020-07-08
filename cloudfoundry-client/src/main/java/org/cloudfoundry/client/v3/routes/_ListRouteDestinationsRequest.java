package org.cloudfoundry.client.v3.routes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cloudfoundry.client.v3.FilterParameter;
import org.cloudfoundry.client.v3.PaginatedRequest;
import org.immutables.value.Value;

import java.util.List;

/**
 * The request payload for the List Route Destinations operation
 */
@Value.Immutable
abstract class _ListRouteDestinationsRequest extends PaginatedRequest {

    /**
     * The application ids filter
     */
    @FilterParameter("app_guids")
    abstract List<String> getApplicationIds();

    /**
     * The destination ids filter
     */
    @FilterParameter("guids")
    abstract List<String> getDestinationIds();

    /**
     * The route id
     */
    @JsonIgnore
    abstract String getRouteId();

}
