package org.cloudfoundry.client.v3.applications;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.immutables.value.Value;

/**
 * The request payload for the Restage an App operation
 */
@Value.Immutable
abstract class _RestageApplicationRequest {

    /**
     * The application id
     */
    @JsonIgnore
    abstract String getApplicationId();

}