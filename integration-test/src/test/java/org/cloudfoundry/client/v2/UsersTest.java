/*
 * Copyright 2013-2017 the original author or authors.
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

package org.cloudfoundry.client.v2;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.spaces.CreateSpaceRequest;
import org.cloudfoundry.client.v2.spaces.CreateSpaceResponse;
import org.cloudfoundry.client.v2.users.CreateUserRequest;
import org.cloudfoundry.client.v2.users.ListUsersRequest;
import org.cloudfoundry.client.v2.users.UserResource;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

public final class UsersTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> organizationId;

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/646
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/646")
    @Test
    public void associateAuditedOrganization() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/647
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/647")
    @Test
    public void associateAuditedSpace() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/648
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/648")
    @Test
    public void associateBillingManagedOrganization() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/649
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/649")
    @Test
    public void associateManagedOrganization() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/650
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/650")
    @Test
    public void associateManagedSpace() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/651
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/651")
    @Test
    public void associateOrganization() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/652
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/652")
    @Test
    public void associateSpace() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/653 - test works, but requires delete for cleanup
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/653")
    @Test
    public void create() throws TimeoutException, InterruptedException {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .then(spaceId -> this.cloudFoundryClient.users()
                .create(CreateUserRequest.builder()
                    .spaceId(spaceId)
                    .uaaId(userId)
                    .build())
                .then(Mono.just(spaceId)))
            .flatMap(spaceId -> requestListUsers(this.cloudFoundryClient))
            .filter(r -> userId.equals(r.getMetadata().getId()))
            .map(ResourceUtils::getId)
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/653
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/653")
    @Test
    public void delete() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/669
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/669")
    @Test
    public void get() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/643 to allow user creation
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/643 to allow user creation")
    @Test
    public void list() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/655
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/655")
    @Test
    public void listAuditedOrganizations() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/656
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/656")
    @Test
    public void listAuditedSpaces() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/657
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/657")
    @Test
    public void listBillingManagedOrganizations() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/658
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/658")
    @Test
    public void listManagedOrganizations() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/659
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/659")
    @Test
    public void listManagedSpaces() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/660
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/660")
    @Test
    public void listOrganizations() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/661
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/661")
    @Test
    public void listSpaces() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/662
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/662")
    @Test
    public void removeAuditedOrganization() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/663
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/663")
    @Test
    public void removeAuditedSpace() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/664
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/664")
    @Test
    public void removeBillingManagedOrganization() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/665
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/665")
    @Test
    public void removeManagedOrganization() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/666
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/666")
    @Test
    public void removeManagedSpace() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/667
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/667")
    @Test
    public void removeOrganization() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/668
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/668")
    @Test
    public void removeSpace() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/654
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/654")
    @Test
    public void summary() throws TimeoutException, InterruptedException {
        //
    }

    private static Mono<String> createSpaceId(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        return requestCreateSpace(cloudFoundryClient, organizationId, spaceName)
            .map(ResourceUtils::getId);
    }

    private static Mono<CreateSpaceResponse> requestCreateSpace(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        return cloudFoundryClient.spaces()
            .create(CreateSpaceRequest.builder()
                .organizationId(organizationId)
                .name(spaceName)
                .build());
    }

    private static Flux<UserResource> requestListUsers(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.users()
                .list(ListUsersRequest.builder()
                    .page(page)
                    .build()));
    }

}
