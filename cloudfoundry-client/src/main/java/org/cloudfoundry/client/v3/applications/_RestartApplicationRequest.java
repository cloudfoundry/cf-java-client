package org.cloudfoundry.client.v3.applications;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.immutables.value.Value;

/**
 * The request payload for the Start Application operation
 */
@Value.Immutable
abstract class _RestartApplicationRequest {

    /**
     * The application id
     */
    @JsonIgnore
    abstract String getApplicationId();

}