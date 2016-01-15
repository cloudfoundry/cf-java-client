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
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.fn.tuple.Tuple;

public final class OrganizationsTest extends AbstractIntegrationTest {

    @Test
    public void create() {
        CreateOrganizationRequest createOrganizationRequest = CreateOrganizationRequest.builder()
                .name("test-org")
                .build();

        this.cloudFoundryClient.organizations().create(createOrganizationRequest)
                .map(response -> Tuple.of(response.getEntity().getName(),response.getMetadata().getId()))
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
}
