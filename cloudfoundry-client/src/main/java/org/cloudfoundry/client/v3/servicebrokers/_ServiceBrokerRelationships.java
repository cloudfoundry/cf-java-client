package org.cloudfoundry.client.v3.servicebrokers;

import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize
@Value.Immutable
abstract class _ServiceBrokerRelationships {

    @JsonProperty("space")
    @Nullable
    abstract ToOneRelationship getSpace();
    
}
