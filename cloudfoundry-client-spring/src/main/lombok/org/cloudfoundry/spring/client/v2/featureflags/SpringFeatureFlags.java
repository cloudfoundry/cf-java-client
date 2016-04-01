/*
 * Copyright 2013-2016 the original author or authors.
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

package org.cloudfoundry.spring.client.v2.featureflags;

import lombok.ToString;
import org.cloudfoundry.client.v2.featureflags.FeatureFlags;
import org.cloudfoundry.client.v2.featureflags.GetFeatureFlagRequest;
import org.cloudfoundry.client.v2.featureflags.GetFeatureFlagResponse;
import org.cloudfoundry.client.v2.featureflags.ListFeatureFlagsRequest;
import org.cloudfoundry.client.v2.featureflags.ListFeatureFlagsResponse;
import org.cloudfoundry.client.v2.featureflags.SetFeatureFlagRequest;
import org.cloudfoundry.client.v2.featureflags.SetFeatureFlagResponse;
import org.cloudfoundry.spring.util.AbstractSpringOperations;
import org.springframework.web.client.RestOperations;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SchedulerGroup;

import java.net.URI;

/**
 * The Spring-based implementation of {@link FeatureFlags}
 */
@ToString(callSuper = true)
public final class SpringFeatureFlags extends AbstractSpringOperations implements FeatureFlags {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     * @param schedulerGroup The group to use when making requests
     */
    public SpringFeatureFlags(RestOperations restOperations, URI root, SchedulerGroup schedulerGroup) {
        super(restOperations, root, schedulerGroup);
    }

    @Override
    public Mono<GetFeatureFlagResponse> get(GetFeatureFlagRequest request) {
        return get(request, GetFeatureFlagResponse.class, builder -> builder.pathSegment("v2", "config", "feature_flags", request.getName()));
    }

    @Override
    public Mono<ListFeatureFlagsResponse> list(ListFeatureFlagsRequest request) {
        return get(request, ListFeatureFlagsResponse.class, builder -> builder.pathSegment("v2", "config", "feature_flags"));
    }

    @Override
    public Mono<SetFeatureFlagResponse> set(SetFeatureFlagRequest request) {
        return put(request, SetFeatureFlagResponse.class, builder -> builder.pathSegment("v2", "config", "feature_flags", request.getName()));
    }

}
