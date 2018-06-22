/*
 * Copyright 2013-2018 the original author or authors.
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

package org.cloudfoundry.reactor.client.v3.serviceinstances;

import org.cloudfoundry.client.v3.serviceInstances.ListServiceInstancesRequest;
import org.cloudfoundry.client.v3.serviceInstances.ListServiceInstancesResponse;
import org.cloudfoundry.client.v3.serviceInstances.ListSharedSpacesRelationshipRequest;
import org.cloudfoundry.client.v3.serviceInstances.ListSharedSpacesRelationshipResponse;
import org.cloudfoundry.client.v3.serviceInstances.ServiceInstancesV3;
import org.cloudfoundry.client.v3.serviceInstances.ShareServiceInstanceRequest;
import org.cloudfoundry.client.v3.serviceInstances.ShareServiceInstanceResponse;
import org.cloudfoundry.client.v3.serviceInstances.UnshareServiceInstanceRequest;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v3.AbstractClientV3Operations;
import reactor.core.publisher.Mono;

/**
 *The Reactor-based implementation of {@link ServiceInstancesV3}
 */
public final class ReactorServiceInstancesV3 extends AbstractClientV3Operations implements ServiceInstancesV3 {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     */
    public ReactorServiceInstancesV3(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider) {
        super(connectionContext, root, tokenProvider);
    }

    @Override
    public Mono<ListServiceInstancesResponse> list(ListServiceInstancesRequest request) {

        return get(request, ListServiceInstancesResponse.class, builder -> builder.pathSegment("service_instances"))
                .checkpoint();
    }

    @Override
    public Mono<ListSharedSpacesRelationshipResponse> listSharedSpacesRelationship(ListSharedSpacesRelationshipRequest request) {

        return get(request, ListSharedSpacesRelationshipResponse.class, builder -> builder.pathSegment("service_instances",request.getServiceInstanceId(),"relationships","shared_spaces"))
            .checkpoint();
    }

    @Override
    public Mono<ShareServiceInstanceResponse> share(ShareServiceInstanceRequest request) {

        return post(request,ShareServiceInstanceResponse.class, builder -> builder.pathSegment("service_instances",request.getServiceInstanceId(),
                "relationships","shared_spaces"));
    }

    @Override
    public Mono<Void> unshare(UnshareServiceInstanceRequest request) {

        return delete(request, Void.class, builder -> builder.pathSegment("service_instances",request.getServiceInstanceId(),
                "relationships","shared_spaces",request.getSpaceId()));
    }
}
