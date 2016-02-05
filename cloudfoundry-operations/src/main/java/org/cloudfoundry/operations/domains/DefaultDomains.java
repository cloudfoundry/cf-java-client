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

package org.cloudfoundry.operations.domains;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.privatedomains.CreatePrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.CreatePrivateDomainResponse;
import org.cloudfoundry.utils.ExceptionUtils;
import org.cloudfoundry.utils.ValidationUtils;
import org.cloudfoundry.utils.tuple.Function2;
import org.cloudfoundry.utils.PaginationUtils;
import org.cloudfoundry.utils.ResourceUtils;
import reactor.core.publisher.Mono;
import reactor.fn.Function;
import reactor.fn.tuple.Tuple2;
import reactor.rx.Stream;

import static org.cloudfoundry.utils.tuple.TupleUtils.function;

public final class DefaultDomains implements Domains {

    private final CloudFoundryClient cloudFoundryClient;

    public DefaultDomains(CloudFoundryClient cloudFoundryClient) {
        this.cloudFoundryClient = cloudFoundryClient;
    }

    public Mono<Void> create(CreateDomainRequest request) {
        return ValidationUtils
            .validate(request)
            .then(new Function<CreateDomainRequest, Mono<Tuple2<String, CreateDomainRequest>>>() {

                @Override
                public Mono<Tuple2<String, CreateDomainRequest>> apply(CreateDomainRequest request) {
                    return getOrganizationId(DefaultDomains.this.cloudFoundryClient, request.getOrganization())
                        .and(Mono.just(request));
                }

            })
            .then(function(new Function2<String, CreateDomainRequest, Mono<CreatePrivateDomainResponse>>() {

                @Override
                public Mono<CreatePrivateDomainResponse> apply(String domainId, CreateDomainRequest request) {
                    return requestCreateDomain(DefaultDomains.this.cloudFoundryClient, request.getDomain(), domainId);
                }

            }))
            .after();
    }

    private static Mono<String> getOrganizationId(CloudFoundryClient cloudFoundryClient, String organization) {
        return requestOrganizations(cloudFoundryClient, organization)
            .single()
            .otherwise(ExceptionUtils.<OrganizationResource>convert("Organization %s does not exist", organization))
            .map(ResourceUtils.extractId());
    }

    private static Mono<CreatePrivateDomainResponse> requestCreateDomain(CloudFoundryClient cloudFoundryClient, String domain, String organizationId) {
        return cloudFoundryClient.privateDomains()
            .create(CreatePrivateDomainRequest.builder()
                .name(domain)
                .owningOrganizationId(organizationId)
                .build());
    }

    private static Stream<OrganizationResource> requestOrganizations(final CloudFoundryClient cloudFoundryClient, final String organization) {
        return PaginationUtils
            .requestResources(new Function<Integer, Mono<ListOrganizationsResponse>>() {

                @Override
                public Mono<ListOrganizationsResponse> apply(Integer page) {
                    return cloudFoundryClient.organizations().list(
                        ListOrganizationsRequest.builder()
                            .name(organization)
                            .page(page)
                            .build());
                }

            });
    }


}
