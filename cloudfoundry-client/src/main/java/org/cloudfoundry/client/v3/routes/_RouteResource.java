package org.cloudfoundry.client.v3.routes;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

/**
 * The Resource response payload for the List routes operation
 */
@JsonDeserialize
@Value.Immutable
public abstract class _RouteResource extends Route {

}
