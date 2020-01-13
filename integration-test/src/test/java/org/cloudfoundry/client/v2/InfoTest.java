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

package org.cloudfoundry.client.v2;

import com.github.zafarkhaja.semver.Version;
import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.info.GetInfoRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.client.CloudFoundryClient.SUPPORTED_API_VERSION;

public final class InfoTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Test
    public void info() {
        this.cloudFoundryClient.info()
            .get(GetInfoRequest.builder()
                .build())
            .as(StepVerifier::create)
            .consumeNextWith(response -> {
                Version expected = Version.valueOf(SUPPORTED_API_VERSION);
                Version actual = Version.valueOf(response.getApiVersion());

                assertThat(actual).isLessThanOrEqualTo(expected);
            })
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

}
