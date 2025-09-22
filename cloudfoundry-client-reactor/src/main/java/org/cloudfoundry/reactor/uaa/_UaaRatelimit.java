package org.cloudfoundry.reactor.uaa;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

@JsonDeserialize
@Value.Immutable
abstract class _UaaRatelimit {

    @JsonProperty("limiterMappings")
    @Nullable
    public abstract Integer getRatelimit();


}
