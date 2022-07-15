/*
 * Copyright 2013-2021 the original author or authors.
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

package org.cloudfoundry.reactor.client.v3.serviceplans;

import org.cloudfoundry.client.v3.serviceplans.DeleteServicePlanRequest;
import org.cloudfoundry.client.v3.serviceplans.GetServicePlanRequest;
import org.cloudfoundry.client.v3.serviceplans.GetServicePlanResponse;
import org.cloudfoundry.client.v3.serviceplans.ListServicePlansRequest;
import org.cloudfoundry.client.v3.serviceplans.ListServicePlansResponse;
import org.cloudfoundry.client.v3.serviceplans.ServicePlansV3;
import org.cloudfoundry.client.v3.serviceplans.UpdateServicePlanRequest;
import org.cloudfoundry.client.v3.serviceplans.UpdateServicePlanResponse;
import org.cloudfoundry.client.v3.serviceplans.UpdateServicePlanVisibilityRequest;
import org.cloudfoundry.client.v3.serviceplans.UpdateServicePlanVisibilityResponse;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v3.AbstractClientV3Operations;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The Reactor-based implementation of {@link ServicePlansV3}
 */
public final class ReactorServicePlansV3 extends AbstractClientV3Operations implements ServicePlansV3 {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorServicePlansV3(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<Void> delete(DeleteServicePlanRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("service_plans", request.getServicePlanId()))
            .checkpoint();
    }

    @Override
    public Mono<GetServicePlanResponse> get(GetServicePlanRequest request) {
        return get(request, GetServicePlanResponse.class, builder -> builder.pathSegment("service_plans", request.getServicePlanId()))
            .checkpoint();
    }

    @Override
    public Mono<ListServicePlansResponse> list(ListServicePlansRequest request) {
        return get(request, ListServicePlansResponse.class, builder -> builder.pathSegment("service_plans"))
            .checkpoint();
    }

    @Override
    public Mono<UpdateServicePlanResponse> update(UpdateServicePlanRequest request) {
        return patch(request, UpdateServicePlanResponse.class, builder -> builder.pathSegment("service_plans", request.getServicePlanId()))
            .checkpoint();
    }

    @Override
    public Mono<UpdateServicePlanVisibilityResponse> updateVisibility(UpdateServicePlanVisibilityRequest request) {
        return post(request, UpdateServicePlanVisibilityResponse.class, builder -> builder.pathSegment("service_plans", request.getServicePlanId(), "visibility"))
            .checkpoint();
    }
}
