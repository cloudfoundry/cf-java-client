package org.cloudfoundry.client.v2.serviceinstances;

import java.io.IOException;
import java.util.Map;

import org.cloudfoundry.AllowNulls;
import org.immutables.value.Value;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * The resource response payload for the Get Parameters Response
 */
@JsonDeserialize(using = _GetServiceInstanceParametersResponse.ServiceInstanceParametersResponseDeserializer.class)
@Value.Immutable
abstract class _GetServiceInstanceParametersResponse {

    /**
     * The service instance parameters
     */
    @AllowNulls
    abstract Map<String, Object> getParameters();

    static final class ServiceInstanceParametersResponseDeserializer extends StdDeserializer<GetServiceInstanceParametersResponse> {

        private static final long serialVersionUID = -2925663073415059473L;

        ServiceInstanceParametersResponseDeserializer() {
            super(GetServiceInstanceParametersResponse.class);
        }

        @Override
        public GetServiceInstanceParametersResponse deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return GetServiceInstanceParametersResponse.builder()
                .parameters(p.readValueAs(new TypeReference<Map<String, Object>>() {

                }))
                .build();
        }
    }

}
