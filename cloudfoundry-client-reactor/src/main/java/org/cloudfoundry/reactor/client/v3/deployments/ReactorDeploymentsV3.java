/*
 * Copyright 2013-2019 the original author or authors.
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

import org.cloudfoundry.client.v3.deployments.CancelDeploymentRequest;
import org.cloudfoundry.client.v3.deployments.CancelDeploymentResponse;
import org.cloudfoundry.client.v3.deployments.CreateDeploymentRequest;
import org.cloudfoundry.client.v3.deployments.CreateDeploymentResponse;
import org.cloudfoundry.client.v3.deployments.DeploymentsV3;
import org.cloudfoundry.client.v3.deployments.GetDeploymentRequest;
import org.cloudfoundry.client.v3.deployments.GetDeploymentResponse;
import org.cloudfoundry.client.v3.deployments.ListDeploymentsRequest;
import org.cloudfoundry.client.v3.deployments.ListDeploymentsResponse;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v3.AbstractClientV3Operations;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The Reactor-based implementation of {@link DeploymentsV3}
 */
public final class ReactorDeploymentsV3 extends AbstractClientV3Operations implements DeploymentsV3 {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorDeploymentsV3(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider,
                                Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<CancelDeploymentResponse> cancel(CancelDeploymentRequest request) {
        return post(request, CancelDeploymentResponse.class,
            builder -> builder.pathSegment("deployments", request.getDeploymentId(), "actions", "cancel")).checkpoint();
    }

    @Override
    public Mono<CreateDeploymentResponse> create(CreateDeploymentRequest request) {
        return post(request, CreateDeploymentResponse.class, builder -> builder.pathSegment("deployments")).checkpoint();
    }

    @Override
    public Mono<GetDeploymentResponse> get(GetDeploymentRequest request) {
        return get(request, GetDeploymentResponse.class,
            builder -> builder.pathSegment("deployments", request.getDeploymentId())).checkpoint();
    }

    @Override
    public Mono<ListDeploymentsResponse> list(ListDeploymentsRequest request) {
        return get(request, ListDeploymentsResponse.class, builder -> builder.pathSegment("deployments")).checkpoint();
    }

}
