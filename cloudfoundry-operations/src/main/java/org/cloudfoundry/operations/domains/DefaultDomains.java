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
import org.cloudfoundry.operations.util.Validators;
import org.cloudfoundry.operations.util.v2.Paginated;
import org.cloudfoundry.operations.util.v2.Resources;
import reactor.core.publisher.Mono;
import reactor.fn.Function;
import reactor.fn.tuple.Tuple2;
import reactor.rx.Stream;

public final class DefaultDomains implements Domains {

    private final CloudFoundryClient cloudFoundryClient;

    public DefaultDomains(CloudFoundryClient cloudFoundryClient) {
        this.cloudFoundryClient = cloudFoundryClient;
    }

    public Mono<Void> create(CreateDomainRequest request) {
        return Validators
                .validate(request)
                .then(requestOrganizationResources(this.cloudFoundryClient))
                .then(requestCreateDomain(this.cloudFoundryClient));
    }

    private static String createInvalidOrganizationMessage(CreateDomainRequest createDomainRequest) {
        return String.format("Organization %s does not exist", createDomainRequest.getOrganizationName());
    }

    private static Function<Tuple2<String, String>, Mono<Void>> requestCreateDomain(final CloudFoundryClient cloudFoundryClient) {
        return new Function<Tuple2<String, String>, Mono<Void>>() {

            @Override
            public Mono<Void> apply(Tuple2<String, String> tuple) {
                String organizationId = tuple.t1;
                String domainName = tuple.t2;

                return cloudFoundryClient.privateDomains().create(
                        CreatePrivateDomainRequest.builder()
                                .owningOrganizationId(organizationId)
                                .domainName(domainName)
                                .wildcard(true)
                                .build())
                        .after();
            }

        };
    }

    private static Function<Integer, Mono<ListOrganizationsResponse>> requestOrganizationPage(final CloudFoundryClient cloudFoundryClient, final String organizationName) {
        return new Function<Integer, Mono<ListOrganizationsResponse>>() {

            @Override
            public Mono<ListOrganizationsResponse> apply(Integer page) {
                return cloudFoundryClient.organizations().list(
                        ListOrganizationsRequest.builder()
                                .name(organizationName)
                                .page(page)
                                .build());
            }

        };
    }

    private static Function<CreateDomainRequest, Mono<Tuple2<String, String>>> requestOrganizationResources(final CloudFoundryClient cloudFoundryClient) {
        return new Function<CreateDomainRequest, Mono<Tuple2<String, String>>>() {

            @Override
            public Mono<Tuple2<String, String>> apply(CreateDomainRequest createDomainRequest) {
                return Paginated
                        .requestResources(requestOrganizationPage(cloudFoundryClient, createDomainRequest.getOrganizationName()))
                        .switchIfEmpty(Stream.<OrganizationResource>fail(new IllegalArgumentException(createInvalidOrganizationMessage(createDomainRequest))))
                        .single()
                        .map(Resources.extractId())
                        .and(Mono.just(createDomainRequest.getDomainName()));
            }

        };
    }


}
