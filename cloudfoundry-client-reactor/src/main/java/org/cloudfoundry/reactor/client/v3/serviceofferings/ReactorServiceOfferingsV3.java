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

package org.cloudfoundry.reactor.client.v3.serviceofferings;

import org.cloudfoundry.client.v3.serviceofferings.DeleteServiceOfferingRequest;
import org.cloudfoundry.client.v3.serviceofferings.GetServiceOfferingRequest;
import org.cloudfoundry.client.v3.serviceofferings.GetServiceOfferingResponse;
import org.cloudfoundry.client.v3.serviceofferings.ListServiceOfferingsRequest;
import org.cloudfoundry.client.v3.serviceofferings.ListServiceOfferingsResponse;
import org.cloudfoundry.client.v3.serviceofferings.ServiceOfferingsV3;
import org.cloudfoundry.client.v3.serviceofferings.UpdateServiceOfferingRequest;
import org.cloudfoundry.client.v3.serviceofferings.UpdateServiceOfferingResponse;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v3.AbstractClientV3Operations;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The Reactor-based implementation of {@link ServiceOfferingsV3}
 */
public final class ReactorServiceOfferingsV3 extends AbstractClientV3Operations implements ServiceOfferingsV3 {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorServiceOfferingsV3(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<Void> delete(DeleteServiceOfferingRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("service_offerings", request.getServiceOfferingId()))
            .checkpoint();
    }

    @Override
    public Mono<GetServiceOfferingResponse> get(GetServiceOfferingRequest request) {
        return get(request, GetServiceOfferingResponse.class, builder -> builder.pathSegment("service_offerings", request.getServiceOfferingId()))
            .checkpoint();
    }

    @Override
    public Mono<ListServiceOfferingsResponse> list(ListServiceOfferingsRequest request) {
        return get(request, ListServiceOfferingsResponse.class, builder -> builder.pathSegment("service_offerings"))
            .checkpoint();
    }

    @Override
    public Mono<UpdateServiceOfferingResponse> update(UpdateServiceOfferingRequest request) {
        return patch(request, UpdateServiceOfferingResponse.class, builder -> builder.pathSegment("service_offerings", request.getServiceOfferingId()))
            .checkpoint();
    }

}
