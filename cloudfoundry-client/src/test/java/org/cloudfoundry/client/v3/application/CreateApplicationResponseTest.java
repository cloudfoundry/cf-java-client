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

package org.cloudfoundry.client.v3.application;

import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.LinkMatcher;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public final class CreateApplicationResponseTest {

    @Test
    public void test() {
        CreateApplicationResponse response = new CreateApplicationResponse()
                .withBuildpack("test-buildpack")
                .withCreatedAt("test-created-at")
                .withDesiredState("test-desired-state")
                .withEnvironmentVariable("test-key-1", "test-value-1")
                .withEnvironmentVariables(Collections.singletonMap("test-key-2", "test-value-2"))
                .withId("test-id")
                .withLink("test-rel-1", new Link().withHref("test-href-1"))
                .withLinks(Collections.singletonMap("test-rel-2", new Link().withHref("test-href-2")))
                .withName("test-name")
                .withTotalDesiredInstances(-1)
                .withUpdatedAt("test-updated-at");

        Map<String, String> environmentVariables = new HashMap<>();
        environmentVariables.put("test-key-1", "test-value-1");
        environmentVariables.put("test-key-2", "test-value-2");

        assertEquals("test-buildpack", response.getBuildpack());
        assertEquals("test-created-at", response.getCreatedAt());
        assertEquals("test-desired-state", response.getDesiredState());
        assertEquals(environmentVariables, response.getEnvironmentVariables());
        assertEquals("test-id", response.getId());

        assertEquals(2, response.getLinks().size());
        assertThat(response.getLink("test-rel-1"), LinkMatcher.linkMatches(new Link().withHref("test-href-1")));
        assertThat(response.getLink("test-rel-2"), LinkMatcher.linkMatches(new Link().withHref("test-href-2")));

        assertEquals("test-name", response.getName());
        assertEquals(Integer.valueOf(-1), response.getTotalDesiredInstances());
        assertEquals("test-updated-at", response.getUpdatedAt());
    }

}
