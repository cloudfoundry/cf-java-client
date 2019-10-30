package org.cloudfoundry.client.v3.routes;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@JsonDeserialize
@Value.Immutable
public abstract class _UpdateRouteResponse extends Route {
}
