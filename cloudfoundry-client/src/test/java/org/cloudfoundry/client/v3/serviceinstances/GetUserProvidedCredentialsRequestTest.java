package org.cloudfoundry.client.v3.serviceinstances;

import org.junit.jupiter.api.Test;

class GetUserProvidedCredentialsRequestTest {

    @Test
    void valid() {
        GetUserProvidedCredentialsRequest.builder()
                .serviceInstanceId("test-service-instance-id")
                .build();
    }
}
