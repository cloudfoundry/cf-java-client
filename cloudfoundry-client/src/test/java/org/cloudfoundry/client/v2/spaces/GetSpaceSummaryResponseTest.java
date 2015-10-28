/*
 * Copyright 2013-2015 the original author or authors.
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

package org.cloudfoundry.client.v2.spaces;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GetSpaceSummaryResponseTest {

    @Test
    public void test() {

        GetSpaceSummaryResponse response = new GetSpaceSummaryResponse()
                .withApplication(SpacesTestUtil.applicationSummary())
                .withId("test-space-id")
                .withName("test-space-name")
                .withService(SpacesTestUtil.serviceSummary());

        assertEquals(1, response.getApplications().size());
        SpacesTestUtil.verifyApplicationSummary(response.getApplications().get(0));

        assertEquals("test-space-id", response.getId());
        assertEquals("test-space-name", response.getName());

        assertEquals(1, response.getServices().size());
        SpacesTestUtil.verifyServiceSummary(response.getServices().get(0));

    }
}
