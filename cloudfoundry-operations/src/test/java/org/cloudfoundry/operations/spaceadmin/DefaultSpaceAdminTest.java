/*
 * Copyright 2013-2020 the original author or authors.
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

package org.cloudfoundry.operations.spaceadmin;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsResponse;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitionResource;
import org.cloudfoundry.operations.AbstractOperationsTest;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.operations.TestObjects.fill;
import static org.mockito.Mockito.when;

public final class DefaultSpaceAdminTest extends AbstractOperationsTest {

    private final DefaultSpaceAdmin spaceAdmin = new DefaultSpaceAdmin(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID));

    @Test
    public void get() {
        requestSpaceQuotaDefinitions(this.cloudFoundryClient, TEST_ORGANIZATION_ID);

        this.spaceAdmin
            .get(GetSpaceQuotaRequest.builder()
                .name("test-space-quota-definition-name")
                .build())
            .as(StepVerifier::create)
            .expectNext(fill(SpaceQuota.builder(), "space-quota-definition-")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getNotFound() {
        requestSpaceQuotaDefinitionsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID);

        this.spaceAdmin
            .get(GetSpaceQuotaRequest.builder()
                .name("test-space-quota-definition-name")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Space Quota test-space-quota-definition-name does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        requestSpaceQuotaDefinitions(this.cloudFoundryClient, TEST_ORGANIZATION_ID);

        this.spaceAdmin
            .listQuotas()
            .as(StepVerifier::create)
            .expectNext(fill(SpaceQuota.builder(), "space-quota-definition-")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    private static void requestSpaceQuotaDefinitions(CloudFoundryClient cloudFoundryClient, String organizationId) {
        when(cloudFoundryClient.organizations()
            .listSpaceQuotaDefinitions(ListOrganizationSpaceQuotaDefinitionsRequest.builder()
                .organizationId(organizationId)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationSpaceQuotaDefinitionsResponse.builder())
                    .resource(fill(SpaceQuotaDefinitionResource.builder(), "space-quota-definition-")
                        .build())
                    .build()));
    }

    private static void requestSpaceQuotaDefinitionsEmpty(CloudFoundryClient cloudFoundryClient, String organizationId) {
        when(cloudFoundryClient.organizations()
            .listSpaceQuotaDefinitions(ListOrganizationSpaceQuotaDefinitionsRequest.builder()
                .organizationId(organizationId)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationSpaceQuotaDefinitionsResponse.builder())
                    .build()));
    }

}
