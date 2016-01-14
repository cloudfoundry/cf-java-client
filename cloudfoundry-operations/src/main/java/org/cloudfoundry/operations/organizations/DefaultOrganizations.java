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

package org.cloudfoundry.operations.organizations;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.operations.util.v2.Paginated;
import org.cloudfoundry.operations.util.v2.Resources;
import org.reactivestreams.Publisher;
import reactor.Mono;
import reactor.fn.Function;

public final class DefaultOrganizations implements Organizations {

    private final CloudFoundryClient cloudFoundryClient;

    public DefaultOrganizations(CloudFoundryClient cloudFoundryClient) {
        this.cloudFoundryClient = cloudFoundryClient;
    }

    @Override
    public Publisher<Organization> list() {
        return Paginated
                .requestResources(requestPage(this.cloudFoundryClient))
                .map(toOrganization());
    }

    private static Function<Integer, Mono<ListOrganizationsResponse>> requestPage(final CloudFoundryClient cloudFoundryClient) {
        return new Function<Integer, Mono<ListOrganizationsResponse>>() {

            @Override
            public Mono<ListOrganizationsResponse> apply(Integer page) {
                ListOrganizationsRequest request = ListOrganizationsRequest.builder()
                        .page(page)
                        .build();

                return cloudFoundryClient.organizations().list(request);
            }

        };
    }

    private static Function<OrganizationResource, Organization> toOrganization() {
        return new Function<OrganizationResource, Organization>() {

            @Override
            public Organization apply(OrganizationResource resource) {
                return Organization.builder()
                        .id(Resources.getId(resource))
                        .name(Resources.getEntity(resource).getName())
                        .build();
            }

        };
    }

}
