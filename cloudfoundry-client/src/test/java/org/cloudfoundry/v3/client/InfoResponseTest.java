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

package org.cloudfoundry.v3.client;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class InfoResponseTest {

    @Test
    public void name() {
        InfoResponse response = new InfoResponse()
                .withApiVersion("test-api-version")
                .withAppSshEndpoint("test-app-ssh-endpoint")
                .withAppSshHostKeyFingerprint("test-app-ssh-host-key-fingerprint")
                .withAuthorizationEndpoint("test-authorization-endpoint")
                .withBuild("test-build")
                .withDescription("test-description")
                .withDopplerLoggingEndpoint("test-doppler-logging-endpoint")
                .withLoggingEndpoint("test-logging-endpoint")
                .withMinCliVersion("test-min-cli-version")
                .withMinRecommendedCliVersion("test-min-recommended-cli-version")
                .withName("test-name")
                .withSupport("test-support")
                .withTokenEndpoint("test-token-endpoint")
                .withVersion(-1);

        assertEquals("test-api-version", response.getApiVersion());
        assertEquals("test-app-ssh-endpoint", response.getAppSshEndpoint());
        assertEquals("test-app-ssh-host-key-fingerprint", response.getAppSshHostKeyFingerprint());
        assertEquals("test-authorization-endpoint", response.getAuthorizationEndpoint());
        assertEquals("test-build", response.getBuild());
        assertEquals("test-description", response.getDescription());
        assertEquals("test-doppler-logging-endpoint", response.getDopplerLoggingEndpoint());
        assertEquals("test-logging-endpoint", response.getLoggingEndpoint());
        assertEquals("test-name", response.getName());
        assertEquals("test-min-cli-version", response.getMinCliVersion());
        assertEquals("test-min-recommended-cli-version", response.getMinRecommendedCliVersion());
        assertEquals("test-support", response.getSupport());
        assertEquals("test-token-endpoint", response.getTokenEndpoint());
        assertEquals(Integer.valueOf(-1), response.getVersion());
    }
}
