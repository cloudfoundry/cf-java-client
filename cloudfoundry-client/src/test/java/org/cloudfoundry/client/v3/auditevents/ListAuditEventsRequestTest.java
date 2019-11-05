/*
 * Copyright 2013-2019 the original author or authors.
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

package org.cloudfoundry.client.v3.auditevents;

import org.junit.Test;

public class ListAuditEventsRequestTest {

    @Test
    public void validWithEmptyCollections() {
        ListAuditEventsRequest.builder()
            .build();
    }

    @Test(expected = Exception.class)
    public void invalidWithNullableCollection() {
        ListAuditEventsRequest.builder()
            .organizationGuids((String[]) null)
            .build();
    }

    @Test
    public void valid() {
        ListAuditEventsRequest.builder()
            .organizationGuids("organization-id-1", "organization-id-2")
            .type("test-type")
            .orderBy("nothing")
            .targetGuid("test-target-id")
            .build();
    }
}
