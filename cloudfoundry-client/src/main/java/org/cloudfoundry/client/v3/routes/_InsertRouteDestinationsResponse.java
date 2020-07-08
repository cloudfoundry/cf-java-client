package org.cloudfoundry.client.v3.routes;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

/**
 * The response payload for the Insert Route Destinations operation
 */
@JsonDeserialize
@Value.Immutable
abstract class _InsertRouteDestinationsResponse extends RouteDestinations {

}
