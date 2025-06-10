/*
 * Copyright 2013-2021 the original author or authors.
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

import java.time.Duration;
import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.ApplicationUtils;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.blobstores.DeleteBlobstoreBuildpackCachesRequest;
import org.cloudfoundry.util.JobUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public final class BlobstoresTest extends AbstractIntegrationTest {

    @Autowired private CloudFoundryClient cloudFoundryClient;

    // The buildpacks cache needs to be non-empty for the DELETE call to succeed.
    // We pull the "testLogCacheApp" bean, which ensures the bean will be initialized,
    // the app will be pushed, and it will add some data to the cache.
    @Autowired private Mono<ApplicationUtils.ApplicationMetadata> testLogCacheApp;

    @Test
    public void delete() {
        this.cloudFoundryClient
                .blobstores()
                .deleteBuildpackCaches(DeleteBlobstoreBuildpackCachesRequest.builder().build())
                .flatMap(
                        job ->
                                JobUtils.waitForCompletion(
                                        this.cloudFoundryClient, Duration.ofMinutes(5), job))
                .as(StepVerifier::create)
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }
}
