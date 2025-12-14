/*
 * Copyright 2013-2025 the original author or authors.
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

package org.cloudfoundry.reactor.client.v3.quotas.spaces;

import java.util.Map;
import org.cloudfoundry.client.v3.quotas.spaces.*;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v3.AbstractClientV3Operations;
import reactor.core.publisher.Mono;

/**
 * The Reactor-based implementation of {@link ReactorSpaceQuotasV3}
 */
public class ReactorSpaceQuotasV3 extends AbstractClientV3Operations implements SpaceQuotasV3 {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically, something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorSpaceQuotasV3(
            ConnectionContext connectionContext,
            Mono<String> root,
            TokenProvider tokenProvider,
            Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<CreateSpaceQuotaResponse> create(CreateSpaceQuotaRequest request) {
        return post(
                        request,
                        CreateSpaceQuotaResponse.class,
                        builder -> builder.pathSegment("space_quotas"))
                .checkpoint();
    }

    @Override
    public Mono<GetSpaceQuotaResponse> get(GetSpaceQuotaRequest request) {
        return get(
                        request,
                        GetSpaceQuotaResponse.class,
                        builder -> builder.pathSegment("space_quotas", request.getSpaceQuotaId()))
                .checkpoint();
    }

    @Override
    public Mono<ListSpaceQuotasResponse> list(ListSpaceQuotasRequest request) {
        return get(
                        request,
                        ListSpaceQuotasResponse.class,
                        builder -> builder.pathSegment("space_quotas"))
                .checkpoint();
    }

    @Override
    public Mono<UpdateSpaceQuotaResponse> update(UpdateSpaceQuotaRequest request) {
        return patch(
                        request,
                        UpdateSpaceQuotaResponse.class,
                        builder -> builder.pathSegment("space_quotas", request.getSpaceQuotaId()))
                .checkpoint();
    }

    @Override
    public Mono<String> delete(DeleteSpaceQuotaRequest request) {
        return delete(
                        request,
                        builder -> builder.pathSegment("space_quotas", request.getSpaceQuotaId()))
                .checkpoint();
    }

    @Override
    public Mono<ApplySpaceQuotaResponse> apply(ApplySpaceQuotaRequest request) {
        return post(
                        request,
                        ApplySpaceQuotaResponse.class,
                        builder ->
                                builder.pathSegment(
                                        "space_quotas",
                                        request.getSpaceQuotaId(),
                                        "relationships",
                                        "spaces"))
                .checkpoint();
    }

    @Override
    public Mono<Void> remove(RemoveSpaceQuotaRequest request) {
        return delete(
                        request,
                        Void.class,
                        builder ->
                                builder.pathSegment(
                                        "space_quotas",
                                        request.getSpaceQuotaId(),
                                        "relationships",
                                        "spaces",
                                        request.getSpaceId()))
                .checkpoint();
    }
}
