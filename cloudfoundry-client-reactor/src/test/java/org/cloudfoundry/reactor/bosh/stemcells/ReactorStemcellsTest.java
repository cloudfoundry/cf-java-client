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

package org.cloudfoundry.reactor.bosh.stemcells;

import org.cloudfoundry.bosh.stemcells.Deployment;
import org.cloudfoundry.bosh.stemcells.ListStemcellsRequest;
import org.cloudfoundry.bosh.stemcells.ListStemcellsResponse;
import org.cloudfoundry.bosh.stemcells.Stemcell;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.bosh.AbstractBoshApiTest;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorStemcellsTest extends AbstractBoshApiTest {

    private final ReactorStemcells stemcells = new ReactorStemcells(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/stemcells")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/bosh/stemcells/GET_response.json")
                .build())
            .build());

        this.stemcells
            .list(ListStemcellsRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNext(ListStemcellsResponse.builder()
                .stemcell(Stemcell.builder()
                    .name("bosh-warden-boshlite-ubuntu-trusty-go_agent")
                    .operatingSystem("ubuntu-trusty")
                    .version("3126")
                    .cid("c3705a0d-0dd3-4b67-52b5-50533a432244")
                    .deployment(Deployment.builder()
                        .name("cf-warden")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
