package org.cloudfoundry.client.v3.routes;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.client.v3.PaginatedResponse;
import org.immutables.value.Value;

/**
 * The response payload for the List Routes operation
 */
@JsonDeserialize
@Value.Immutable
abstract class _ListRoutesResponse extends PaginatedResponse<RouteResource> {

}
