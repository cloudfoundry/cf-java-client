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

package org.cloudfoundry.reactor.client.v2.featureflags;

import org.cloudfoundry.client.v2.featureflags.FeatureFlags;
import org.cloudfoundry.client.v2.featureflags.GetFeatureFlagRequest;
import org.cloudfoundry.client.v2.featureflags.GetFeatureFlagResponse;
import org.cloudfoundry.client.v2.featureflags.ListFeatureFlagsRequest;
import org.cloudfoundry.client.v2.featureflags.ListFeatureFlagsResponse;
import org.cloudfoundry.client.v2.featureflags.SetFeatureFlagRequest;
import org.cloudfoundry.client.v2.featureflags.SetFeatureFlagResponse;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v2.AbstractClientV2Operations;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The Reactor-based implementation of {@link FeatureFlags}
 */
public final class ReactorFeatureFlags extends AbstractClientV2Operations implements FeatureFlags {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorFeatureFlags(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<GetFeatureFlagResponse> get(GetFeatureFlagRequest request) {
        return get(request, GetFeatureFlagResponse.class, builder -> builder.pathSegment("config", "feature_flags", request.getName()))
            .checkpoint();
    }

    @Override
    public Mono<ListFeatureFlagsResponse> list(ListFeatureFlagsRequest request) {
        return get(request, ListFeatureFlagsResponse.class, builder -> builder.pathSegment("config", "feature_flags"))
            .checkpoint();
    }

    @Override
    public Mono<SetFeatureFlagResponse> set(SetFeatureFlagRequest request) {
        return put(request, SetFeatureFlagResponse.class, builder -> builder.pathSegment("config", "feature_flags", request.getName()))
            .checkpoint();
    }

}
