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

import static org.cloudfoundry.util.OperationUtils.thenKeep;

public final class OrganizationQuotaDefinitionsTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @SuppressWarnings("deprecation")
    @Test
    public void create() {
        String quotaDefinitionName = this.nameFactory.getQuotaDefinitionName();

        @SuppressWarnings("deprecation")
        OrganizationQuotaDefinitionEntity expectedEntity = OrganizationQuotaDefinitionEntity.builder()
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
            .build();

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
            .subscribe(this.testSubscriber()
                .expectEquals(expectedEntity)
            );
    }

    @Test
    public void delete() {
        String quotaDefinitionName = this.nameFactory.getQuotaDefinitionName();

        requestCreateOrganizationQuotaDefinition(this.cloudFoundryClient, quotaDefinitionName)
            .map(ResourceUtils::getId)
            .as(thenKeep(organizationQuotaDefinitionId -> this.cloudFoundryClient.organizationQuotaDefinitions()
                .delete(DeleteOrganizationQuotaDefinitionRequest.builder()
                    .async(true)
                    .organizationQuotaDefinitionId(organizationQuotaDefinitionId)
                    .build())
                .then(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, job))))
            .then(organizationQuotaDefinitionId -> requestGetOrganizationQuotaDefinition(this.cloudFoundryClient, organizationQuotaDefinitionId))
            .subscribe(this.testSubscriber()
                .expectErrorMatch(CloudFoundryException.class, "CF-QuotaDefinitionNotFound\\([0-9]+\\): Quota Definition could not be found: .*"));
    }

    @Test
    public void get() {
        String quotaDefinitionName = this.nameFactory.getQuotaDefinitionName();

        @SuppressWarnings("deprecation")
        OrganizationQuotaDefinitionEntity expectedEntity = OrganizationQuotaDefinitionEntity.builder()
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
            .build();

        requestCreateOrganizationQuotaDefinition(this.cloudFoundryClient, quotaDefinitionName)
            .map(ResourceUtils::getId)
            .then(organizationQuotaDefinitionId -> this.cloudFoundryClient.organizationQuotaDefinitions()
                .get(GetOrganizationQuotaDefinitionRequest.builder()
                    .organizationQuotaDefinitionId(organizationQuotaDefinitionId)
                    .build()))
            .map(ResourceUtils::getEntity)
            .subscribe(this.testSubscriber()
                .expectEquals(expectedEntity));
    }

    @Test
    public void list() {
        String quotaDefinitionName = this.nameFactory.getQuotaDefinitionName();

        requestCreateOrganizationQuotaDefinition(this.cloudFoundryClient, quotaDefinitionName)
            .map(ResourceUtils::getId)
            .then(organizationQuotaDefinitionId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.organizationQuotaDefinitions()
                    .list(ListOrganizationQuotaDefinitionsRequest.builder()
                        .page(page)
                        .build()))
                .filter(quotaDefinition -> ResourceUtils.getId(quotaDefinition).equals(organizationQuotaDefinitionId))
                .single())
            .map(ResourceUtils::getEntity)
            .map(OrganizationQuotaDefinitionEntity::getName)
            .subscribe(this.testSubscriber()
                .expectEquals(quotaDefinitionName));
    }

    @Test
    public void update() {
        String quotaDefinitionName = this.nameFactory.getQuotaDefinitionName();

        @SuppressWarnings("deprecation")
        OrganizationQuotaDefinitionEntity expectedEntity = OrganizationQuotaDefinitionEntity.builder()
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
            .build();

        requestCreateOrganizationQuotaDefinition(this.cloudFoundryClient, quotaDefinitionName)
            .map(ResourceUtils::getId)
            .as(thenKeep(organizationQuotaDefinitionId -> this.cloudFoundryClient.organizationQuotaDefinitions()
                .update(UpdateOrganizationQuotaDefinitionRequest.builder()
                    .organizationQuotaDefinitionId(organizationQuotaDefinitionId)
                    .totalServices(10)
                    .memoryLimit(1000)
                    .build())))
            .then(organizationQuotaDefinitionId -> this.cloudFoundryClient.organizationQuotaDefinitions()
                .get(GetOrganizationQuotaDefinitionRequest.builder()
                    .organizationQuotaDefinitionId(organizationQuotaDefinitionId)
                    .build()))
            .map(ResourceUtils::getEntity)
            .subscribe(this.testSubscriber()
                .expectEquals(expectedEntity));
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
