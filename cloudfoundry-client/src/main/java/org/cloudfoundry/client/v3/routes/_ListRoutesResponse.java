package org.cloudfoundry.client.v3.routes;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.client.v3.PaginatedResponse;
import org.immutables.value.Value;

@JsonDeserialize
@Value.Immutable
public abstract class _ListRoutesResponse extends PaginatedResponse<RouteResource> {
}
