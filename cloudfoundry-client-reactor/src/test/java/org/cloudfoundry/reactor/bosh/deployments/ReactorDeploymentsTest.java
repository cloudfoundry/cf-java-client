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

package org.cloudfoundry.reactor.bosh.deployments;

import org.cloudfoundry.bosh.deployments.CloudConfig;
import org.cloudfoundry.bosh.deployments.Deployment;
import org.cloudfoundry.bosh.deployments.ListDeploymentsRequest;
import org.cloudfoundry.bosh.deployments.ListDeploymentsResponse;
import org.cloudfoundry.bosh.deployments.Release;
import org.cloudfoundry.bosh.deployments.Stemcell;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.bosh.AbstractBoshApiTest;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorDeploymentsTest extends AbstractBoshApiTest {

    private final ReactorDeployments deployments = new ReactorDeployments(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/deployments")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/bosh/deployments/GET_response.json")
                .build())
            .build());

        this.deployments
            .list(ListDeploymentsRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNext(ListDeploymentsResponse.builder()
                .deployment(Deployment.builder()
                    .name("cf-warden")
                    .cloudConfig(CloudConfig.NONE)
                    .release(Release.builder()
                        .name("cf")
                        .version("222")
                        .build())
                    .release(Release.builder()
                        .name("cf")
                        .version("223")
                        .build())
                    .stemcell(Stemcell.builder()
                        .name("bosh-warden-boshlite-ubuntu-trusty-go_agent")
                        .version("2776")
                        .build())
                    .stemcell(Stemcell.builder()
                        .name("bosh-warden-boshlite-ubuntu-trusty-go_agent")
                        .version("3126")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }
}
