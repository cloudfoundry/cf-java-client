/*
 * Copyright 2013-2015 the original author or authors.
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

package org.cloudfoundry.client.spring.v2.domains;

import lombok.ToString;
import org.cloudfoundry.client.spring.util.AbstractSpringOperations;
import org.cloudfoundry.client.spring.util.QueryBuilder;
import org.cloudfoundry.client.spring.v2.FilterBuilder;
import org.cloudfoundry.client.v2.domains.CreateDomainRequest;
import org.cloudfoundry.client.v2.domains.CreateDomainResponse;
import org.cloudfoundry.client.v2.domains.DeleteDomainRequest;
import org.cloudfoundry.client.v2.domains.Domains;
import org.cloudfoundry.client.v2.domains.GetDomainRequest;
import org.cloudfoundry.client.v2.domains.GetDomainResponse;
import org.cloudfoundry.client.v2.domains.ListDomainSpacesRequest;
import org.cloudfoundry.client.v2.domains.ListDomainSpacesResponse;
import org.cloudfoundry.client.v2.domains.ListDomainsRequest;
import org.cloudfoundry.client.v2.domains.ListDomainsResponse;
import org.reactivestreams.Publisher;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.fn.Consumer;

import java.net.URI;

/**
 * The Spring-based implementation of {@link Domains}
 */
@ToString(callSuper = true)
public final class SpringDomains extends AbstractSpringOperations implements Domains {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     */
    public SpringDomains(RestOperations restOperations, URI root) {
        super(restOperations, root);
    }

    @Override
    public Publisher<CreateDomainResponse> create(final CreateDomainRequest request) {
        return post(request, CreateDomainResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "domains");
            }

        });
    }

    @Override
    public Publisher<Void> delete(final DeleteDomainRequest request) {
        return delete(request, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "domains", request.getId());
            }

        });
    }

    @Override
    public Publisher<GetDomainResponse> get(final GetDomainRequest request) {
        return get(request, GetDomainResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder uriComponentsBuilder) {
                uriComponentsBuilder.pathSegment("v2", "domains", request.getId());
            }

        });
    }

    @Override
    public Publisher<ListDomainsResponse> list(final ListDomainsRequest request) {
        return get(request, ListDomainsResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "domains");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Publisher<ListDomainSpacesResponse> listSpaces(final ListDomainSpacesRequest request) {
        return get(request, ListDomainSpacesResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "domains", request.getId(), "spaces");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

}
