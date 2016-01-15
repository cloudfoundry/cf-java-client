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

package org.cloudfoundry.client;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationAuditorByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationAuditorRequest;
import org.cloudfoundry.client.v2.organizations.CreateOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.DeleteOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationAuditorByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationAuditorRequest;
import org.cloudfoundry.client.v2.users.ListUsersRequest;
import org.cloudfoundry.operations.util.v2.Paginated;
import org.cloudfoundry.operations.util.v2.Resources;
import org.junit.Test;
import reactor.Mono;
import reactor.fn.tuple.Tuple;
import reactor.rx.Stream;

public final class OrganizationsTest extends AbstractIntegrationTest {

    @Test
    public void auditor() {
        getAdminId()
                .and(this.organizationId)
                .then(tuple -> {
                    AssociateOrganizationAuditorRequest request = AssociateOrganizationAuditorRequest.builder()
                            .auditorId(tuple.t1)
                            .organizationId(tuple.t2)
                            .build();

                    return this.cloudFoundryClient.organizations().associateAuditor(request)
                            .and(Mono.just(tuple.t1));
                })
                .then(tuple -> {
                    RemoveOrganizationAuditorRequest request = RemoveOrganizationAuditorRequest.builder()
                            .auditorId(tuple.t2)
                            .id(Resources.getId(tuple.t1))
                            .build();

                    return this.cloudFoundryClient.organizations().removeAuditor(request);
                })
                .subscribe(this.testSubscriber());
    }

    @Test
    public void auditorByUsername() {
        this.organizationId
                .then(orgId -> {
                    AssociateOrganizationAuditorByUsernameRequest request = AssociateOrganizationAuditorByUsernameRequest.builder()
                            .username("admin")
                            .id(orgId)
                            .build();

                    return this.cloudFoundryClient.organizations().associateAuditorByUsername(request);
                })
                .then(response -> {
                    RemoveOrganizationAuditorByUsernameRequest request = RemoveOrganizationAuditorByUsernameRequest.builder()
                            .username("admin")
                            .id(response.getMetadata().getId())
                            .build();

                    return this.cloudFoundryClient.organizations().removeAuditorByUsername(request);
                })
                .subscribe(this.testSubscriber());
    }

    @Test
    public void create() {
        CreateOrganizationRequest request = CreateOrganizationRequest.builder()
                .name("test-org")
                .build();

        this.cloudFoundryClient.organizations().create(request)
                .map(response -> Tuple.of(response.getEntity().getName(), response.getMetadata().getId()))
                .doOnSuccess(tuple -> deleteOrg(this.cloudFoundryClient, tuple.t2)) // avoid polluting test environment
                .map(tuple -> tuple.t1)
                .subscribe(this.testSubscriber().assertEquals("test-org"));
    }

    private static final void deleteOrg(CloudFoundryClient client, String orgId) {
        DeleteOrganizationRequest request = DeleteOrganizationRequest.builder()
                .id(orgId)
                .build();

        client.organizations().delete(request).get();
    }

    private Mono<String> getAdminId() {
        return Paginated.requestResources(
                page -> {
                    ListUsersRequest request = ListUsersRequest.builder()
                            .page(page)
                            .build();

                    return this.cloudFoundryClient.users().listUsers(request);
                })
                .filter(userResource -> Resources.getEntity(userResource).getUsername().equals("admin"))
                .single()
                .map(Resources::getId);
    }
}
