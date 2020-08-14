package org.cloudfoundry.client.v3.spaces;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.immutables.value.Value;

/**
 * The request payload for the Delete Unmapped Routes operation
 */
@Value.Immutable
abstract class _DeleteSpaceRequest {

    /**
     * The space id
     */
    @JsonIgnore
    abstract String getSpaceId();

}
