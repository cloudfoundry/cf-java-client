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

package org.cloudfoundry.reactor.client.v3.organizationquotas;

import java.util.Map;
import org.cloudfoundry.client.v3.organizationquotas.*;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v3.AbstractClientV3Operations;
import reactor.core.publisher.Mono;

/**
 * The Reactor-based implementation of {@link ReactorOrganizationQuotasV3}
 */
public class ReactorOrganizationQuotasV3 extends AbstractClientV3Operations
        implements OrganizationQuotasV3 {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically, something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorOrganizationQuotasV3(
            ConnectionContext connectionContext,
            Mono<String> root,
            TokenProvider tokenProvider,
            Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<CreateOrganizationQuotaResponse> create(CreateOrganizationQuotaRequest request) {
        return post(
                        request,
                        CreateOrganizationQuotaResponse.class,
                        builder -> builder.pathSegment("organization_quotas"))
                .checkpoint();
    }

    @Override
    public Mono<GetOrganizationQuotaResponse> get(GetOrganizationQuotaRequest request) {
        return get(
                        request,
                        GetOrganizationQuotaResponse.class,
                        builder ->
                                builder.pathSegment(
                                        "organization_quotas", request.getOrganizationQuotaId()))
                .checkpoint();
    }

    @Override
    public Mono<ListOrganizationQuotasResponse> list(ListOrganizationQuotasRequest request) {
        return get(
                        request,
                        ListOrganizationQuotasResponse.class,
                        builder -> builder.pathSegment("organization_quotas"))
                .checkpoint();
    }

    @Override
    public Mono<UpdateOrganizationQuotaResponse> update(UpdateOrganizationQuotaRequest request) {
        return patch(
                        request,
                        UpdateOrganizationQuotaResponse.class,
                        builder ->
                                builder.pathSegment(
                                        "organization_quotas", request.getOrganizationQuotaId()))
                .checkpoint();
    }

    @Override
    public Mono<String> delete(DeleteOrganizationQuotaRequest request) {
        return delete(
                        request,
                        builder ->
                                builder.pathSegment(
                                        "organization_quotas", request.getOrganizationQuotaId()))
                .checkpoint();
    }
}
