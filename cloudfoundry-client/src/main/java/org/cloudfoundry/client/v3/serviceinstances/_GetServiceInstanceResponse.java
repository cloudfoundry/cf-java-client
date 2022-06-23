package org.cloudfoundry.client.v3.serviceinstances;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize
abstract class _GetServiceInstanceResponse extends ServiceInstance {

}
