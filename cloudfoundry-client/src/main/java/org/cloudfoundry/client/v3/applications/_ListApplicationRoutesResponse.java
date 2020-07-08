package org.cloudfoundry.client.v3.applications;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.client.v3.PaginatedResponse;
import org.cloudfoundry.client.v3.routes.RouteResource;
import org.immutables.value.Value;

/**
 * The response payload for the List Application Routes operation
 */
@JsonDeserialize
@Value.Immutable
abstract class _ListApplicationRoutesResponse extends PaginatedResponse<RouteResource> {

}
