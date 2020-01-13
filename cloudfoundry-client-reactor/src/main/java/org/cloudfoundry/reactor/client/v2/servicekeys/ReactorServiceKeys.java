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

package org.cloudfoundry.reactor.client.v2.servicekeys;

import org.cloudfoundry.client.v2.serviceinstances.ServiceInstances;
import org.cloudfoundry.client.v2.servicekeys.CreateServiceKeyRequest;
import org.cloudfoundry.client.v2.servicekeys.CreateServiceKeyResponse;
import org.cloudfoundry.client.v2.servicekeys.DeleteServiceKeyRequest;
import org.cloudfoundry.client.v2.servicekeys.GetServiceKeyRequest;
import org.cloudfoundry.client.v2.servicekeys.GetServiceKeyResponse;
import org.cloudfoundry.client.v2.servicekeys.ListServiceKeysRequest;
import org.cloudfoundry.client.v2.servicekeys.ListServiceKeysResponse;
import org.cloudfoundry.client.v2.servicekeys.ServiceKeys;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v2.AbstractClientV2Operations;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The Reactor-based implementation of {@link ServiceInstances}
 */
public final class ReactorServiceKeys extends AbstractClientV2Operations implements ServiceKeys {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorServiceKeys(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<CreateServiceKeyResponse> create(CreateServiceKeyRequest request) {
        return post(request, CreateServiceKeyResponse.class, builder -> builder.pathSegment("service_keys"))
            .checkpoint();
    }

    @Override
    public Mono<Void> delete(DeleteServiceKeyRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("service_keys", request.getServiceKeyId()))
            .checkpoint();
    }

    @Override
    public Mono<GetServiceKeyResponse> get(GetServiceKeyRequest request) {
        return get(request, GetServiceKeyResponse.class, builder -> builder.pathSegment("service_keys", request.getServiceKeyId()))
            .checkpoint();
    }

    @Override
    public Mono<ListServiceKeysResponse> list(ListServiceKeysRequest request) {
        return get(request, ListServiceKeysResponse.class, builder -> builder.pathSegment("service_keys"))
            .checkpoint();
    }

}
