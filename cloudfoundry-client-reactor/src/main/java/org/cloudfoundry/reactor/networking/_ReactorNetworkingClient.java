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

package org.cloudfoundry.reactor.networking;

import org.cloudfoundry.networking.NetworkingClient;
import org.cloudfoundry.networking.v1.policies.Policies;
import org.cloudfoundry.networking.v1.tags.Tags;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.networking.v1.policies.ReactorPolicies;
import org.cloudfoundry.reactor.networking.v1.tags.ReactorTags;
import org.immutables.value.Value;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

/**
 * The Reactor-based implementation of {@link NetworkingClient}
 */
@Value.Immutable
abstract class _ReactorNetworkingClient implements NetworkingClient {

    @Override
    @Value.Derived
    public Policies policies() {
        return new ReactorPolicies(getConnectionContext(), getRoot(), getTokenProvider(), getRequestTags());
    }

    @Override
    @Value.Derived
    public Tags tags() {
        return new ReactorTags(getConnectionContext(), getRoot(), getTokenProvider(), getRequestTags());
    }

    /**
     * The connection context
     */
    abstract ConnectionContext getConnectionContext();

    @Value.Default
    Map<String, String> getRequestTags() {
        return Collections.emptyMap();
    }

    @Value.Default
    Mono<String> getRoot() {
        return getConnectionContext().getRootProvider().getRoot("network_policy_v1", getConnectionContext());
    }

    /**
     * The token provider
     */
    abstract TokenProvider getTokenProvider();

}
