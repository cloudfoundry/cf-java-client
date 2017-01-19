/*
 * Copyright 2013-2017 the original author or authors.
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

package org.cloudfoundry.reactor.bosh.releases;

import org.cloudfoundry.bosh.releases.ListReleasesRequest;
import org.cloudfoundry.bosh.releases.ListReleasesResponse;
import org.cloudfoundry.bosh.releases.Release;
import org.cloudfoundry.bosh.releases.ReleaseVersion;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.bosh.AbstractBoshApiTest;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorReleasesTest extends AbstractBoshApiTest {

    private final ReactorReleases releases = new ReactorReleases(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/releases")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/bosh/releases/GET_response.json")
                .build())
            .build());

        this.releases
            .list(ListReleasesRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNext(ListReleasesResponse.builder()
                .release(Release.builder()
                    .name("bosh-warden-cpi")
                    .releaseVersion(ReleaseVersion.builder()
                        .version("28")
                        .commitHash("4c36884a")
                        .uncommittedChanges(false)
                        .currentlyDeployed(false)
                        .jobName("warden_cpi")
                        .build())
                    .build())
                .release(Release.builder()
                    .name("test")
                    .releaseVersion(ReleaseVersion.builder()
                        .version("0+dev.16")
                        .commitHash("31ef3167")
                        .uncommittedChanges(true)
                        .currentlyDeployed(false)
                        .jobName("http_server", "service")
                        .build())
                    .releaseVersion(ReleaseVersion.builder()
                        .version("0+dev.17")
                        .commitHash("e5416248")
                        .uncommittedChanges(true)
                        .currentlyDeployed(true)
                        .jobName("drain", "errand", "http_server", "pre_start", "service")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
