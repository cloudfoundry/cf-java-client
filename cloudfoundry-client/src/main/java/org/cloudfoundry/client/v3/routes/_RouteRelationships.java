package org.cloudfoundry.client.v3.routes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.immutables.value.Value;

/**
 * The Route relationships
 */
@JsonDeserialize
@Value.Immutable
abstract class _RouteRelationships {

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
