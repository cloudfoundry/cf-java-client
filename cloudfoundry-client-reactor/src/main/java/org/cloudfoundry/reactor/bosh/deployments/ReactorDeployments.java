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

import io.netty.util.AsciiString;
import org.cloudfoundry.bosh.deployments.CreateDeploymentRequest;
import org.cloudfoundry.bosh.deployments.CreateDeploymentResponse;
import org.cloudfoundry.bosh.deployments.DeleteDeploymentRequest;
import org.cloudfoundry.bosh.deployments.DeleteDeploymentResponse;
import org.cloudfoundry.bosh.deployments.Deployments;
import org.cloudfoundry.bosh.deployments.GetDeploymentRequest;
import org.cloudfoundry.bosh.deployments.GetDeploymentResponse;
import org.cloudfoundry.bosh.deployments.ListDeploymentsRequest;
import org.cloudfoundry.bosh.deployments.ListDeploymentsResponse;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.bosh.AbstractBoshOperations;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

/**
 * The Reactor-based implementation of {@link Deployments}
 */
public final class ReactorDeployments extends AbstractBoshOperations implements Deployments {

    private static final AsciiString TEXT_YAML = new AsciiString("text/yaml");

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     */
    public ReactorDeployments(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider) {
        super(connectionContext, root, tokenProvider);
    }

    @Override
    public Mono<CreateDeploymentResponse> create(CreateDeploymentRequest request) {
        return post(request, CreateDeploymentResponse.class,
            builder -> builder.pathSegment("deployments"),
            outbound -> outbound
                .map(r -> r.header(CONTENT_TYPE, TEXT_YAML))
                .flatMap(r -> r.sendString(Mono.just(request.getManifest()), UTF_8)));
    }

    @Override
    public Mono<DeleteDeploymentResponse> delete(DeleteDeploymentRequest request) {
        return delete(request, DeleteDeploymentResponse.class, builder -> builder.pathSegment("deployments", request.getDeploymentName()));
    }

    @Override
    public Mono<GetDeploymentResponse> get(GetDeploymentRequest request) {
        return get(request, GetDeploymentResponse.class, builder -> builder.pathSegment("deployments", request.getDeploymentName()));
    }

    @Override
    public Mono<ListDeploymentsResponse> list(ListDeploymentsRequest request) {
        return get(request, ListDeploymentsResponse.class, builder -> builder.pathSegment("deployments"));
    }

}
