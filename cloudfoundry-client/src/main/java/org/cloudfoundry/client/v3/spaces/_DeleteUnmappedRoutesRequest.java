package org.cloudfoundry.client.v3.spaces;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cloudfoundry.client.v3.FilterParameter;
import org.immutables.value.Value;

@Value.Immutable
public abstract  class _DeleteUnmappedRoutesRequest {
    /**
     * The space id
     */
    @JsonIgnore
    abstract String getSpaceId();

    @Value.Default
    @FilterParameter("unmapped")
    public boolean isUnmapped() {
        return true;
    }

}
