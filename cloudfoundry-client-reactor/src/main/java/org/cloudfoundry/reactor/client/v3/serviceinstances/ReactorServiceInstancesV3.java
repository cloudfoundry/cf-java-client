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

package org.cloudfoundry.reactor.client.v3.serviceinstances;

import org.cloudfoundry.client.v3.serviceinstances.GetManagedServiceParametersRequest;
import org.cloudfoundry.client.v3.serviceinstances.GetManagedServiceParametersResponse;
import org.cloudfoundry.client.v3.serviceinstances.GetUserProvidedCredentialsRequest;
import org.cloudfoundry.client.v3.serviceinstances.GetUserProvidedCredentialsResponse;
import org.cloudfoundry.client.v3.serviceinstances.ListServiceInstancesRequest;
import org.cloudfoundry.client.v3.serviceinstances.ListServiceInstancesResponse;
import org.cloudfoundry.client.v3.serviceinstances.ListSharedSpacesRelationshipRequest;
import org.cloudfoundry.client.v3.serviceinstances.ListSharedSpacesRelationshipResponse;
import org.cloudfoundry.client.v3.serviceinstances.ServiceInstancesV3;
import org.cloudfoundry.client.v3.serviceinstances.ShareServiceInstanceRequest;
import org.cloudfoundry.client.v3.serviceinstances.ShareServiceInstanceResponse;
import org.cloudfoundry.client.v3.serviceinstances.UnshareServiceInstanceRequest;
import org.cloudfoundry.client.v3.serviceinstances.UpdateServiceInstanceRequest;
import org.cloudfoundry.client.v3.serviceinstances.UpdateServiceInstanceResponse;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v3.AbstractClientV3Operations;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The Reactor-based implementation of {@link ServiceInstancesV3}
 */
public final class ReactorServiceInstancesV3 extends AbstractClientV3Operations implements ServiceInstancesV3 {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorServiceInstancesV3(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<GetManagedServiceParametersResponse> getManagedServiceParameters(GetManagedServiceParametersRequest request) {
	return get(request, GetManagedServiceParametersResponse.class, builder -> builder.pathSegment("service_instances", request.getServiceInstanceId(), "parameters"))
	    .checkpoint();
    }

    @Override
    public Mono<GetUserProvidedCredentialsResponse> getUserProvidedCredentials(GetUserProvidedCredentialsRequest request) {
	return get(request, GetUserProvidedCredentialsResponse.class, builder -> builder.pathSegment("service_instances", request.getServiceInstanceId(), "credentials"))
	    .checkpoint();
    }

    @Override
    public Mono<ListServiceInstancesResponse> list(ListServiceInstancesRequest request) {
        return get(request, ListServiceInstancesResponse.class, builder -> builder.pathSegment("service_instances"))
            .checkpoint();
    }

    @Override
    public Mono<ListSharedSpacesRelationshipResponse> listSharedSpacesRelationship(ListSharedSpacesRelationshipRequest request) {
        return get(request, ListSharedSpacesRelationshipResponse.class, builder -> builder.pathSegment("service_instances", request.getServiceInstanceId(), "relationships", "shared_spaces"))
            .checkpoint();
    }

    @Override
    public Mono<ShareServiceInstanceResponse> share(ShareServiceInstanceRequest request) {
        return post(request, ShareServiceInstanceResponse.class, builder -> builder.pathSegment("service_instances", request.getServiceInstanceId(), "relationships", "shared_spaces"))
            .checkpoint();
    }

    @Override
    public Mono<Void> unshare(UnshareServiceInstanceRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("service_instances", request.getServiceInstanceId(), "relationships", "shared_spaces", request.getSpaceId()))
            .checkpoint();
    }

    @Override
    public Mono<UpdateServiceInstanceResponse> update(UpdateServiceInstanceRequest request) {
        return patch(request, UpdateServiceInstanceResponse.class, builder -> builder.pathSegment("service_instances", request.getServiceInstanceId()));
    }
}
