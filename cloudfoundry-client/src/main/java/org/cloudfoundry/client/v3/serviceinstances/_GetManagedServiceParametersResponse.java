package org.cloudfoundry.client.v3.serviceinstances;

import java.util.Map;

import org.cloudfoundry.AllowNulls;
import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize
abstract class _GetManagedServiceParametersResponse {

    /**
     * 
     * @return parameters as map
     */
    @JsonAnyGetter
    @AllowNulls
    abstract Map<String, Object> getParameters();
    
}
