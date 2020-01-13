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

package org.cloudfoundry.reactor.client.v2.blobstores;

import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.blobstores.DeleteBlobstoreBuildpackCachesRequest;
import org.cloudfoundry.client.v2.blobstores.DeleteBlobstoreBuildpackCachesResponse;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorBlobstoresTest extends AbstractClientApiTest {

    private ReactorBlobstores blobstores = new ReactorBlobstores(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void delete() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/blobstores/buildpack_cache")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/blobstores/DELETE_buildpack_cache_response.json")
                .build())
            .build());

        this.blobstores
            .deleteBuildpackCaches(DeleteBlobstoreBuildpackCachesRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNext(DeleteBlobstoreBuildpackCachesResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2016-06-08T16:41:31Z")
                    .id("919a6964-ea88-43cc-9ac1-0dbc3769f743")
                    .url("/v2/jobs/919a6964-ea88-43cc-9ac1-0dbc3769f743")
                    .build())
                .entity(JobEntity.builder()
                    .id("919a6964-ea88-43cc-9ac1-0dbc3769f743")
                    .status("queued")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
