/*
 * Copyright 2026 the original author or authors.
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

package org.cloudfoundry.reactor.client.v3.featureflags;

import java.util.Map;
import org.cloudfoundry.client.v3.featureflags.FeatureFlagsV3;
import org.cloudfoundry.client.v3.featureflags.GetFeatureFlagRequest;
import org.cloudfoundry.client.v3.featureflags.GetFeatureFlagResponse;
import org.cloudfoundry.client.v3.featureflags.ListFeatureFlagsRequest;
import org.cloudfoundry.client.v3.featureflags.ListFeatureFlagsResponse;
import org.cloudfoundry.client.v3.featureflags.UpdateFeatureFlagRequest;
import org.cloudfoundry.client.v3.featureflags.UpdateFeatureFlagResponse;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v3.AbstractClientV3Operations;
import reactor.core.publisher.Mono;

/**
 * The Reactor-based implementation of {@link FeatureFlagsV3}
 */
public final class ReactorFeatureFlagsV3 extends AbstractClientV3Operations
        implements FeatureFlagsV3 {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorFeatureFlagsV3(
            ConnectionContext connectionContext,
            Mono<String> root,
            TokenProvider tokenProvider,
            Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<GetFeatureFlagResponse> get(GetFeatureFlagRequest request) {
        return get(
                        request,
                        GetFeatureFlagResponse.class,
                        builder -> builder.pathSegment("feature_flags", request.getName()))
                .checkpoint();
    }

    @Override
    public Mono<ListFeatureFlagsResponse> list(ListFeatureFlagsRequest request) {
        return get(
                        request,
                        ListFeatureFlagsResponse.class,
                        builder -> builder.pathSegment("feature_flags"))
                .checkpoint();
    }

    @Override
    public Mono<UpdateFeatureFlagResponse> update(UpdateFeatureFlagRequest request) {
        return patch(
                        request,
                        UpdateFeatureFlagResponse.class,
                        builder -> builder.pathSegment("feature_flags", request.getName()))
                .checkpoint();
    }
}
