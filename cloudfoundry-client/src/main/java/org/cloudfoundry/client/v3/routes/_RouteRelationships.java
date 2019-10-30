package org.cloudfoundry.client.v3.routes;

import org.cloudfoundry.client.v3.ToOneRelationship;
import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize
@Value.Immutable
public abstract class _RouteRelationships {

    /**
     * The domain relationship
     */
    @JsonProperty("domain")
    abstract ToOneRelationship getDomain();
    
    /**
     * The space relationship
     */
    @JsonProperty("space")
    abstract ToOneRelationship getSpace();
}
