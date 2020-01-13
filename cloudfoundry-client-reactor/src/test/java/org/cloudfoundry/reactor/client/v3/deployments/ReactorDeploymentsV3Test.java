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

package org.cloudfoundry.reactor.client.v3.deployments;

import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.Pagination;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.cloudfoundry.client.v3.deployments.CancelDeploymentRequest;
import org.cloudfoundry.client.v3.deployments.CreateDeploymentRequest;
import org.cloudfoundry.client.v3.deployments.CreateDeploymentResponse;
import org.cloudfoundry.client.v3.deployments.DeploymentRelationships;
import org.cloudfoundry.client.v3.deployments.DeploymentResource;
import org.cloudfoundry.client.v3.deployments.DeploymentState;
import org.cloudfoundry.client.v3.deployments.GetDeploymentRequest;
import org.cloudfoundry.client.v3.deployments.GetDeploymentResponse;
import org.cloudfoundry.client.v3.deployments.ListDeploymentsRequest;
import org.cloudfoundry.client.v3.deployments.ListDeploymentsResponse;
import org.cloudfoundry.client.v3.deployments.Process;
import org.cloudfoundry.client.v3.deployments.Revision;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static java.util.Collections.singletonList;

public class ReactorDeploymentsV3Test extends AbstractClientApiTest {

    private final ReactorDeploymentsV3 deployments = new ReactorDeploymentsV3(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void cancel() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/deployments/test-deployment-id/actions/cancel")
                .payload("fixtures/client/v3/deployments/POST_{id}_cancel_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .build())
            .build());

        this.deployments
            .cancel(CancelDeploymentRequest.builder()
                .deploymentId("test-deployment-id")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void create() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/deployments")
                .payload("fixtures/client/v3/deployments/POST_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/deployments/POST_response.json")
                .build())
            .build());

        this.deployments
            .create(CreateDeploymentRequest.builder()
                .droplet(Relationship.builder().id("44ccfa61-dbcf-4a0d-82fe-f668e9d2a962").build())
                .relationships(DeploymentRelationships.builder()
                    .app(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id("305cea31-5a44-45ca-b51b-e89c7a8ef8b2")
                            .build())
                        .build())
                    .build())
                .build())
            .as(StepVerifier::create)

            .expectNext(CreateDeploymentResponse.builder()
                .id("59c3d133-2b83-46f3-960e-7765a129aea4")
                .state(DeploymentState.DEPLOYING)
                .droplet(Relationship.builder()
                    .id("44ccfa61-dbcf-4a0d-82fe-f668e9d2a962")
                    .build())
                .previousDroplet(Relationship.builder()
                    .id("cc6bc315-bd06-49ce-92c2-bc3ad45268c2")
                    .build())
                .newProcesses(singletonList(Process.builder()
                    .id("fd5d3e60-f88c-4c37-b1ae-667cfc65a856")
                    .type("web-deployment-59c3d133-2b83-46f3-960e-7765a129aea4")
                    .build()))
                .revision(Revision.builder()
                    .id("56126cba-656a-4eba-a81e-7e9951b2df57")
                    .version(1)
                    .build())
                .createdAt("2018-04-25T22:42:10Z")
                .updatedAt("2018-04-25T22:42:10Z")
                .relationships(DeploymentRelationships.builder()
                    .app(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id("305cea31-5a44-45ca-b51b-e89c7a8ef8b2")
                            .build())
                        .build())
                    .build())
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/deployments/59c3d133-2b83-46f3-960e-7765a129aea4")
                    .build())
                .link("app", Link.builder()
                    .href("https://api.example.org/v3/apps/305cea31-5a44-45ca-b51b-e89c7a8ef8b2")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/deployments/test-deployment-id")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/deployments/GET_{id}_response.json")
                .build())
            .build());

        this.deployments
            .get(GetDeploymentRequest.builder()
                .deploymentId("test-deployment-id")
                .build())
            .as(StepVerifier::create)

            .expectNext(GetDeploymentResponse.builder()
                .id("59c3d133-2b83-46f3-960e-7765a129aea4")
                .state(DeploymentState.DEPLOYING)
                .droplet(Relationship.builder()
                    .id("44ccfa61-dbcf-4a0d-82fe-f668e9d2a962")
                    .build())
                .previousDroplet(Relationship.builder()
                    .id("cc6bc315-bd06-49ce-92c2-bc3ad45268c2")
                    .build())
                .newProcesses(singletonList(Process.builder()
                    .id("fd5d3e60-f88c-4c37-b1ae-667cfc65a856")
                    .type("web-deployment-59c3d133-2b83-46f3-960e-7765a129aea4")
                    .build()))
                .revision(Revision.builder()
                    .id("56126cba-656a-4eba-a81e-7e9951b2df57")
                    .version(1)
                    .build())
                .createdAt("2018-04-25T22:42:10Z")
                .updatedAt("2018-04-25T22:42:10Z")
                .relationships(DeploymentRelationships.builder()
                    .app(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id("305cea31-5a44-45ca-b51b-e89c7a8ef8b2")
                            .build())
                        .build())
                    .build())
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/deployments/59c3d133-2b83-46f3-960e-7765a129aea4")
                    .build())
                .link("app", Link.builder()
                    .href("https://api.example.org/v3/apps/305cea31-5a44-45ca-b51b-e89c7a8ef8b2")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/deployments")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/deployments/GET_response.json")
                .build())
            .build());

        this.deployments
            .list(ListDeploymentsRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNext(ListDeploymentsResponse.builder()
                .pagination(Pagination.builder()
                    .totalResults(1)
                    .totalPages(1)
                    .first(Link.builder()
                        .href("https://api.example.org/v3/deployments?page=1&per_page=2")
                        .build())
                    .last(Link.builder()
                        .href("https://api.example.org/v3/deployments?page=1&per_page=2")
                        .build())
                    .build())
                .resource(DeploymentResource.builder()
                    .id("59c3d133-2b83-46f3-960e-7765a129aea4")
                    .state(DeploymentState.DEPLOYING)
                    .droplet(Relationship.builder()
                        .id("44ccfa61-dbcf-4a0d-82fe-f668e9d2a962")
                        .build())
                    .previousDroplet(Relationship.builder()
                        .id("cc6bc315-bd06-49ce-92c2-bc3ad45268c2")
                        .build())
                    .newProcesses(singletonList(Process.builder()
                        .id("fd5d3e60-f88c-4c37-b1ae-667cfc65a856")
                        .type("web-deployment-59c3d133-2b83-46f3-960e-7765a129aea4")
                        .build()))
                    .revision(Revision.builder()
                        .id("56126cba-656a-4eba-a81e-7e9951b2df57")
                        .version(1)
                        .build())
                    .createdAt("2018-04-25T22:42:10Z")
                    .updatedAt("2018-04-25T22:42:10Z")
                    .relationships(DeploymentRelationships.builder()
                        .app(ToOneRelationship.builder()
                            .data(Relationship.builder()
                                .id("305cea31-5a44-45ca-b51b-e89c7a8ef8b2")
                                .build())
                            .build())
                        .build())
                    .link("self", Link.builder()
                        .href("https://api.example.org/v3/deployments/59c3d133-2b83-46f3-960e-7765a129aea4")
                        .build())
                    .link("app", Link.builder()
                        .href("https://api.example.org/v3/apps/305cea31-5a44-45ca-b51b-e89c7a8ef8b2")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
