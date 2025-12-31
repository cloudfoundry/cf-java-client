package org.cloudfoundry.client.v3.quotas.spaces;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.cloudfoundry.client.v3.ToManyRelationship;
import org.immutables.value.Value;

/**
 * The request payload to apply an Space Quota to a Space
 */
@JsonSerialize
@Value.Immutable
abstract class _ApplySpaceQuotaRequest {

    /**
     * The Space Quota id
     */
    @JsonIgnore
    abstract String getSpaceQuotaId();

    /**
     * Relationships to the spaces where the quota is applied
     * Use of JsonUnwrapped to inline the space relationships as per the API spec
     */
    @JsonUnwrapped
    abstract ToManyRelationship spaceRelationships();
}
