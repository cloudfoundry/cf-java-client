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

package org.cloudfoundry.reactor.networking.v1.policies;

import org.cloudfoundry.networking.v1.policies.CreatePoliciesRequest;
import org.cloudfoundry.networking.v1.policies.DeletePoliciesRequest;
import org.cloudfoundry.networking.v1.policies.ListPoliciesRequest;
import org.cloudfoundry.networking.v1.policies.ListPoliciesResponse;
import org.cloudfoundry.networking.v1.policies.Policies;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.networking.AbstractNetworkingOperations;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The Reactor-based implementation of {@link Policies}
 */
public class ReactorPolicies extends AbstractNetworkingOperations implements Policies {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorPolicies(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<Void> create(CreatePoliciesRequest request) {
        return post(request, Object.class, builder -> builder.pathSegment("policies"))
            .then()
            .checkpoint();
    }

    @Override
    public Mono<Void> delete(DeletePoliciesRequest request) {
        return post(request, Object.class, builder -> builder.pathSegment("policies", "delete"))
            .then()
            .checkpoint();
    }

    @Override
    public Mono<ListPoliciesResponse> list(ListPoliciesRequest request) {
        return get(request, ListPoliciesResponse.class, builder -> builder.pathSegment("policies"))
            .checkpoint();
    }

}
