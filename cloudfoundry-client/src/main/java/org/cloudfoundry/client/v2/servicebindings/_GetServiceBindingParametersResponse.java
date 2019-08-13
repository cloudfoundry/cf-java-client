package org.cloudfoundry.client.v2.servicebindings;

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
 * The response payload for the Get Service Binding Parameters operation
 */
@JsonDeserialize(using = _GetServiceBindingParametersResponse.ServiceBindingParametersResponseDeserializer.class)
@Value.Immutable
abstract class _GetServiceBindingParametersResponse {

    /**
     * The service binding parameters
     */
    @AllowNulls
    abstract Map<String, Object> getParameters();

    static final class ServiceBindingParametersResponseDeserializer extends StdDeserializer<GetServiceBindingParametersResponse> {

        private static final long serialVersionUID = -2925663073415059473L;

        ServiceBindingParametersResponseDeserializer() {
            super(GetServiceBindingParametersResponse.class);
        }

        @Override
        public GetServiceBindingParametersResponse deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return GetServiceBindingParametersResponse.builder()
                .parameters(p.readValueAs(new TypeReference<Map<String, Object>>() {

                }))
                .build();
        }
    }

}
