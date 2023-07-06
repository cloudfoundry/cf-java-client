package org.cloudfoundry.client.v3.securitygroups;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

/**
 * Controls if the group is applied globally to the lifecycle of all
 * applications
 */
@JsonDeserialize
@Value.Immutable
abstract class _GloballyEnabled {

    /**
     * Specifies whether the group should be applied globally to all running
     * applications
     */
    @JsonProperty("running")
    abstract Boolean getRunning();

    /**
     * Specifies whether the group should be applied globally to all staging
     * applications
     */
    @JsonProperty("staging")
    abstract Boolean getStaging();

}
