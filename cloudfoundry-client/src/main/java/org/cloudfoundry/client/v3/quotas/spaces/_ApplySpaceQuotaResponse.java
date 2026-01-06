package org.cloudfoundry.client.v3.quotas.spaces;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.AllowNulls;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.ToManyRelationship;
import org.immutables.value.Value;

import java.util.Map;

/**
 * The response payload for applying a Space Quota to a Space
 */
@JsonDeserialize
@Value.Immutable
abstract class _ApplySpaceQuotaResponse {

    /**
     * Relationships to the spaces where the quota is applied
     * Use of JsonUnwrapped to inline the space relationships as per the API spec
     */
    @JsonUnwrapped
    abstract ToManyRelationship spaceRelationships();

    /**
     * Links to related resources and actions for the resource
     */
    @AllowNulls
    @JsonProperty("links")
    public abstract Map<String, Link> getLinks();
}
