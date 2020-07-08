package org.cloudfoundry.client.v3.routes;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

/**
 * The response payload for the Get Route operation
 */
@JsonDeserialize
@Value.Immutable
abstract class _GetRouteResponse extends Route {

}
