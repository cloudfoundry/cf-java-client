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

import okio.Buffer;
import org.cloudfoundry.bosh.deployments.CloudConfig;
import org.cloudfoundry.bosh.deployments.CreateDeploymentRequest;
import org.cloudfoundry.bosh.deployments.CreateDeploymentResponse;
import org.cloudfoundry.bosh.deployments.Deployment;
import org.cloudfoundry.bosh.deployments.GetDeploymentRequest;
import org.cloudfoundry.bosh.deployments.GetDeploymentResponse;
import org.cloudfoundry.bosh.deployments.ListDeploymentsRequest;
import org.cloudfoundry.bosh.deployments.ListDeploymentsResponse;
import org.cloudfoundry.bosh.deployments.Release;
import org.cloudfoundry.bosh.deployments.Stemcell;
import org.cloudfoundry.bosh.tasks.State;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.bosh.AbstractBoshApiTest;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.nio.charset.Charset;
import java.time.Duration;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.util.tuple.TupleUtils.consumer;

public final class ReactorDeploymentsTest extends AbstractBoshApiTest {

    private final ReactorDeployments deployments = new ReactorDeployments(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

    @Test
    public void create() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/deployments")
                .header("Content-Type", "text/yaml")
                .contents(consumer((headers, body) -> {
                    Buffer expected = TestRequest.getBuffer("fixtures/bosh/deployments/POST_request.yml");
                    assertThat(body.readString(Charset.defaultCharset())).isEqualTo(expected.readString(Charset.defaultCharset()));
                }))
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/bosh/deployments/POST_response.json")
                .build())
            .build());

        this.deployments
            .create(CreateDeploymentRequest.builder()
                .manifest("---\nname: cf-warden\n")
                .build())
            .as(StepVerifier::create)
            .expectNext(CreateDeploymentResponse.builder()
                .id(1180)
                .state(State.PROCESSING)
                .description("run errand acceptance_tests from deployment cf-warden")
                .timestamp(1447033291)
                .user("admin")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/deployments/cf-warden")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/bosh/deployments/GET_{name}_response.json")
                .build())
            .build());

        this.deployments
            .get(GetDeploymentRequest.builder()
                .deploymentName("cf-warden")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetDeploymentResponse.builder()
                .manifest("---\nname: cf-warden\n...")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

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
