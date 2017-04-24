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
import org.cloudfoundry.client.v2.organizationquotadefinitions.CreateOrganizationQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.CreateOrganizationQuotaDefinitionResponse;
import org.cloudfoundry.client.v2.organizationquotadefinitions.DeleteOrganizationQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.GetOrganizationQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.GetOrganizationQuotaDefinitionResponse;
import org.cloudfoundry.client.v2.organizationquotadefinitions.ListOrganizationQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.OrganizationQuotaDefinitionEntity;
import org.cloudfoundry.client.v2.organizationquotadefinitions.UpdateOrganizationQuotaDefinitionRequest;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

public final class OrganizationQuotaDefinitionsTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @SuppressWarnings("deprecation")
    @Test
    public void create() throws TimeoutException, InterruptedException {
        String quotaDefinitionName = this.nameFactory.getQuotaDefinitionName();

        this.cloudFoundryClient.organizationQuotaDefinitions()
            .create(CreateOrganizationQuotaDefinitionRequest.builder()
                .instanceMemoryLimit(1024)
                .memoryLimit(1024)
                .name(quotaDefinitionName)
                .nonBasicServicesAllowed(false)
                .totalRoutes(10)
                .totalServices(-1)
                .build())
            .map(ResourceUtils::getEntity)
            .as(StepVerifier::create)
            .expectNext(OrganizationQuotaDefinitionEntity.builder()
                .applicationInstanceLimit(-1)
                .applicationTaskLimit(-1)
                .instanceMemoryLimit(1024)
                .memoryLimit(1024)
                .name(quotaDefinitionName)
                .nonBasicServicesAllowed(false)
                .totalPrivateDomains(-1)
                .totalRoutes(10)
                .totalReservedRoutePorts(0)
                .totalServiceKeys(-1)
                .totalServices(-1)
                .trialDatabaseAllowed(false)
                .build())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() throws TimeoutException, InterruptedException {
        String quotaDefinitionName = this.nameFactory.getQuotaDefinitionName();

        requestCreateOrganizationQuotaDefinition(this.cloudFoundryClient, quotaDefinitionName)
            .map(ResourceUtils::getId)
            .delayUntil(organizationQuotaDefinitionId -> this.cloudFoundryClient.organizationQuotaDefinitions()
                .delete(DeleteOrganizationQuotaDefinitionRequest.builder()
                    .async(true)
                    .organizationQuotaDefinitionId(organizationQuotaDefinitionId)
                    .build())
                .flatMap(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, Duration.ofMinutes(5), job)))
            .flatMap(organizationQuotaDefinitionId -> requestGetOrganizationQuotaDefinition(this.cloudFoundryClient, organizationQuotaDefinitionId))
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(ClientV2Exception.class).hasMessageMatching("CF-QuotaDefinitionNotFound\\([0-9]+\\): Quota Definition could not be found: .*"))
            .verify(Duration.ofMinutes(5));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void get() throws TimeoutException, InterruptedException {
        String quotaDefinitionName = this.nameFactory.getQuotaDefinitionName();

        requestCreateOrganizationQuotaDefinition(this.cloudFoundryClient, quotaDefinitionName)
            .map(ResourceUtils::getId)
            .flatMap(organizationQuotaDefinitionId -> this.cloudFoundryClient.organizationQuotaDefinitions()
                .get(GetOrganizationQuotaDefinitionRequest.builder()
                    .organizationQuotaDefinitionId(organizationQuotaDefinitionId)
                    .build()))
            .map(ResourceUtils::getEntity)
            .as(StepVerifier::create)
            .expectNext(OrganizationQuotaDefinitionEntity.builder()
                .applicationInstanceLimit(-1)
                .applicationTaskLimit(-1)
                .instanceMemoryLimit(50)
                .memoryLimit(500)
                .name(quotaDefinitionName)
                .nonBasicServicesAllowed(false)
                .totalPrivateDomains(-1)
                .totalRoutes(10)
                .totalReservedRoutePorts(0)
                .totalServiceKeys(-1)
                .totalServices(5)
                .trialDatabaseAllowed(false)
                .build())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() throws TimeoutException, InterruptedException {
        String quotaDefinitionName = this.nameFactory.getQuotaDefinitionName();

        requestCreateOrganizationQuotaDefinition(this.cloudFoundryClient, quotaDefinitionName)
            .map(ResourceUtils::getId)
            .flatMap(organizationQuotaDefinitionId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.organizationQuotaDefinitions()
                    .list(ListOrganizationQuotaDefinitionsRequest.builder()
                        .page(page)
                        .build()))
                .filter(quotaDefinition -> ResourceUtils.getId(quotaDefinition).equals(organizationQuotaDefinitionId))
                .single())
            .map(ResourceUtils::getEntity)
            .map(OrganizationQuotaDefinitionEntity::getName)
            .as(StepVerifier::create)
            .expectNext(quotaDefinitionName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void update() throws TimeoutException, InterruptedException {
        String quotaDefinitionName = this.nameFactory.getQuotaDefinitionName();

        requestCreateOrganizationQuotaDefinition(this.cloudFoundryClient, quotaDefinitionName)
            .map(ResourceUtils::getId)
            .delayUntil(organizationQuotaDefinitionId -> this.cloudFoundryClient.organizationQuotaDefinitions()
                .update(UpdateOrganizationQuotaDefinitionRequest.builder()
                    .organizationQuotaDefinitionId(organizationQuotaDefinitionId)
                    .totalServices(10)
                    .memoryLimit(1000)
                    .build()))
            .flatMap(organizationQuotaDefinitionId -> this.cloudFoundryClient.organizationQuotaDefinitions()
                .get(GetOrganizationQuotaDefinitionRequest.builder()
                    .organizationQuotaDefinitionId(organizationQuotaDefinitionId)
                    .build()))
            .map(ResourceUtils::getEntity)
            .as(StepVerifier::create)
            .expectNext(OrganizationQuotaDefinitionEntity.builder()
                .applicationInstanceLimit(-1)
                .applicationTaskLimit(-1)
                .instanceMemoryLimit(50)
                .memoryLimit(1000)
                .name(quotaDefinitionName)
                .nonBasicServicesAllowed(false)
                .totalPrivateDomains(-1)
                .totalRoutes(10)
                .totalReservedRoutePorts(0)
                .totalServiceKeys(-1)
                .totalServices(10)
                .trialDatabaseAllowed(false)
                .build())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<CreateOrganizationQuotaDefinitionResponse> requestCreateOrganizationQuotaDefinition(CloudFoundryClient cloudFoundryClient, String quotaDefinitionName) {
        return cloudFoundryClient.organizationQuotaDefinitions()
            .create(CreateOrganizationQuotaDefinitionRequest.builder()
                .instanceMemoryLimit(50)
                .memoryLimit(500)
                .name(quotaDefinitionName)
                .nonBasicServicesAllowed(false)
                .totalRoutes(10)
                .totalServices(5)
                .build());
    }

    private static Mono<GetOrganizationQuotaDefinitionResponse> requestGetOrganizationQuotaDefinition(CloudFoundryClient cloudFoundryClient, String organizationQuotaDefinitionId) {
        return cloudFoundryClient.organizationQuotaDefinitions()
            .get(GetOrganizationQuotaDefinitionRequest.builder()
                .organizationQuotaDefinitionId(organizationQuotaDefinitionId)
                .build());
    }

}
