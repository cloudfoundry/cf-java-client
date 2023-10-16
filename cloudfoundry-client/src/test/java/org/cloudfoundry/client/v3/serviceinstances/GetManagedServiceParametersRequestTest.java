package org.cloudfoundry.client.v3.serviceinstances;

import org.junit.jupiter.api.Test;

class GetManagedServiceParametersRequestTest {

    @Test
    void valid() {
        GetManagedServiceParametersRequest.builder()
            .serviceInstanceId("test-service-instance-id")
            .build();
    }

}
