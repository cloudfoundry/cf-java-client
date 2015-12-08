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

package org.cloudfoundry.client.spring.v2.organizations;

import lombok.ToString;
import org.cloudfoundry.client.spring.util.AbstractSpringOperations;
import org.cloudfoundry.client.spring.util.QueryBuilder;
import org.cloudfoundry.client.spring.v2.FilterBuilder;
import org.cloudfoundry.client.v2.organizations.AssociateAuditorRequest;
import org.cloudfoundry.client.v2.organizations.AssociateAuditorResponse;
import org.cloudfoundry.client.v2.organizations.AssociateBillingManagerRequest;
import org.cloudfoundry.client.v2.organizations.AssociateBillingManagerResponse;
import org.cloudfoundry.client.v2.organizations.AssociateSpaceManagerRequest;
import org.cloudfoundry.client.v2.organizations.AssociateSpaceManagerResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.organizations.Organizations;
import org.reactivestreams.Publisher;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.fn.Consumer;

import java.net.URI;

/**
 * The Spring-based implementation of {@link Organizations}
 */
@ToString(callSuper = true)
public final class SpringOrganizations extends AbstractSpringOperations implements Organizations {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     */
    public SpringOrganizations(RestOperations restOperations, URI root) {
        super(restOperations, root);
    }

    @Override
    public Publisher<AssociateAuditorResponse> associateAuditor(final AssociateAuditorRequest request) {
        return put(request, AssociateAuditorResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getOrganizationId(), "auditors",
                        request.getAuditorId());
            }

        });
    }

    @Override
    public Publisher<AssociateBillingManagerResponse> associateBillingManager(final AssociateBillingManagerRequest
                                                                                      request) {
        return put(request, AssociateBillingManagerResponse.class, new Consumer<UriComponentsBuilder>() {
            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getId(), "billing_managers", request
                        .getBillingManagerId());
            }

        });
    }

    @Override
    public Publisher<AssociateSpaceManagerResponse> associateManager(final AssociateSpaceManagerRequest request) {
        return put(request, AssociateSpaceManagerResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getId(), "managers", request.getManagerId());
            }

        });
    }

    @Override
    public Publisher<ListOrganizationsResponse> list(final ListOrganizationsRequest request) {
        return get(request, ListOrganizationsResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

}
