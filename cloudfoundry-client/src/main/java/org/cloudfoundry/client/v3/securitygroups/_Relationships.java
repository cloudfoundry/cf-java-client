package org.cloudfoundry.client.v3.securitygroups;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.client.v3.ToManyRelationship;
import org.immutables.value.Value;

/**
 * Holds relationships to running/staging spaces where security groups is
 * applied
 */
@JsonDeserialize
@Value.Immutable
abstract class _Relationships {

    /**
     * A relationship to the spaces where the security_group is applied to
     * applications during runtime
     */
    @JsonProperty("running_spaces")
    abstract ToManyRelationship getRunningSpaces();

    /**
     * A relationship to the spaces where the security_group is applied to
     * applications during runtime
     */
    @JsonProperty("staging_spaces")
    abstract ToManyRelationship getStagingSpaces();

}
