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

package org.cloudfoundry.operations;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.operations.v2.Paginated;
import org.reactivestreams.Publisher;
import reactor.fn.Function;

final class DefaultOrganizations implements Organizations {

    private final CloudFoundryClient cloudFoundryClient;

    DefaultOrganizations(CloudFoundryClient cloudFoundryClient) {
        this.cloudFoundryClient = cloudFoundryClient;
    }

    @Override
    public Publisher<Organization> list() {
        return Paginated
                .requestResources(requestPage(this.cloudFoundryClient))
                .map(toOrganization());
    }

    private static Function<Integer, Publisher<ListOrganizationsResponse>> requestPage(final CloudFoundryClient cloudFoundryClient) {
        return new Function<Integer, Publisher<ListOrganizationsResponse>>() {

            @Override
            public Publisher<ListOrganizationsResponse> apply(Integer page) {
                ListOrganizationsRequest request = ListOrganizationsRequest.builder()
                        .page(page)
                        .build();

                return cloudFoundryClient.organizations().list(request);
            }

        };
    }

    private static Function<ListOrganizationsResponse.Resource, Organization> toOrganization() {
        return new Function<ListOrganizationsResponse.Resource, Organization>() {

            @Override
            public Organization apply(ListOrganizationsResponse.Resource resource) {
                return Organization.builder()
                        .id(resource.getMetadata().getId())
                        .name(resource.getEntity().getName())
                        .build();
            }

        };
    }

}
