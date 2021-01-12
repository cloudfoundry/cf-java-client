/*
 * Copyright 2013-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.client.v3.serviceinstances;

import org.cloudfoundry.client.v3.Metadata;
import org.junit.Test;

public class UpdateServiceInstanceRequestTest {

    @Test(expected = IllegalStateException.class)
    public void noMetadata() {
        UpdateServiceInstanceRequest.builder()
            .serviceInstanceId("test-service-instance-id")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noServiceInstanceId() {
        UpdateServiceInstanceRequest.builder()
            .metadata(Metadata.builder().build())
            .build();
    }

    @Test
    public void valid() {
        UpdateServiceInstanceRequest.builder()
            .metadata(Metadata.builder().build())
            .serviceInstanceId("test-service-instance-id")
            .build();
    }
}
