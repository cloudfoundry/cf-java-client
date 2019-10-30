package org.cloudfoundry.client.v3.applications;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.client.v3.PaginatedResponse;
import org.cloudfoundry.client.v3.routes.RouteResource;
import org.immutables.value.Value;

@JsonDeserialize
@Value.Immutable
public abstract class _ListApplicationRoutesResponse extends PaginatedResponse<RouteResource> {
}
