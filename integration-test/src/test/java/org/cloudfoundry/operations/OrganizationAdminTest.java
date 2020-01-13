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

package org.cloudfoundry.operations;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.operations.organizationadmin.CreateQuotaRequest;
import org.cloudfoundry.operations.organizationadmin.DeleteQuotaRequest;
import org.cloudfoundry.operations.organizationadmin.GetQuotaRequest;
import org.cloudfoundry.operations.organizationadmin.OrganizationQuota;
import org.cloudfoundry.operations.organizationadmin.SetQuotaRequest;
import org.cloudfoundry.operations.organizationadmin.UpdateQuotaRequest;
import org.cloudfoundry.operations.organizations.CreateOrganizationRequest;
import org.cloudfoundry.operations.organizations.OrganizationInfoRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public final class OrganizationAdminTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryOperations cloudFoundryOperations;

    @Test
    public void createQuota() {
        String quotaName = this.nameFactory.getQuotaDefinitionName();

        this.cloudFoundryOperations.organizationAdmin()
            .createQuota(CreateQuotaRequest.builder()
                .applicationInstanceLimit(9)
                .allowPaidServicePlans(false)
                .instanceMemoryLimit(64)
                .memoryLimit(512)
                .name(quotaName)
                .build())
            .then(requestGetQuota(this.cloudFoundryOperations, quotaName)
                .map(OrganizationQuota::getInstanceMemoryLimit))
            .as(StepVerifier::create)
            .expectNext(64)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void deleteQuota() {
        String quotaName = this.nameFactory.getQuotaDefinitionName();

        requestCreateQuota(this.cloudFoundryOperations, quotaName)
            .then(this.cloudFoundryOperations.organizationAdmin()
                .deleteQuota(DeleteQuotaRequest.builder()
                    .name(quotaName)
                    .build()))
            .then(requestGetQuota(this.cloudFoundryOperations, quotaName))
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Quota %s does not exist", quotaName))
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void getQuota() {
        String quotaName = this.nameFactory.getQuotaDefinitionName();

        requestCreateQuota(this.cloudFoundryOperations, quotaName)
            .then(this.cloudFoundryOperations.organizationAdmin()
                .getQuota(GetQuotaRequest.builder()
                    .name(quotaName)
                    .build())
                .map(OrganizationQuota::getApplicationInstanceLimit))
            .as(StepVerifier::create)
            .expectNext(9)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listQuotas() {
        String quotaName = this.nameFactory.getQuotaDefinitionName();

        requestCreateQuota(this.cloudFoundryOperations, quotaName)
            .thenMany(this.cloudFoundryOperations.organizationAdmin()
                .listQuotas())
            .filter(quota -> quotaName.equals(quota.getName()))
            .map(OrganizationQuota::getTotalReservedRoutePorts)
            .as(StepVerifier::create)
            .expectNext(4)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void setQuota() {
        String organizationName = this.nameFactory.getOrganizationName();
        String quotaName = this.nameFactory.getQuotaDefinitionName();

        Mono.when(
            requestCreateOrganization(this.cloudFoundryOperations, organizationName),
            requestCreateQuota(this.cloudFoundryOperations, quotaName)
        )
            .then(this.cloudFoundryOperations.organizationAdmin()
                .setQuota(SetQuotaRequest.builder()
                    .organizationName(organizationName)
                    .quotaName(quotaName)
                    .build()))
            .then(this.cloudFoundryOperations.organizations()
                .get(OrganizationInfoRequest.builder()
                    .name(organizationName)
                    .build())
                .map(organization -> organization.getQuota().getName()))
            .as(StepVerifier::create)
            .expectNext(quotaName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void updateQuota() {
        String quotaName = this.nameFactory.getQuotaDefinitionName();

        requestCreateQuota(this.cloudFoundryOperations, quotaName)
            .then(this.cloudFoundryOperations.organizationAdmin()
                .updateQuota(UpdateQuotaRequest.builder()
                    .name(quotaName)
                    .memoryLimit(513)
                    .build()))
            .then(requestGetQuota(this.cloudFoundryOperations, quotaName)
                .map(OrganizationQuota::getMemoryLimit))
            .as(StepVerifier::create)
            .expectNext(513)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<Void> requestCreateOrganization(CloudFoundryOperations cloudFoundryOperations, String organizationName) {
        return cloudFoundryOperations.organizations()
            .create(CreateOrganizationRequest.builder()
                .organizationName(organizationName)
                .build());
    }

    private static Mono<OrganizationQuota> requestCreateQuota(CloudFoundryOperations cloudFoundryOperations, String quotaName) {
        return cloudFoundryOperations.organizationAdmin()
            .createQuota(CreateQuotaRequest.builder()
                .applicationInstanceLimit(9)
                .allowPaidServicePlans(false)
                .instanceMemoryLimit(64)
                .memoryLimit(512)
                .totalReservedRoutePorts(4)
                .totalRoutes(9)
                .totalServices(9)
                .name(quotaName)
                .build());
    }

    private static Mono<OrganizationQuota> requestGetQuota(CloudFoundryOperations cloudFoundryOperations, String quotaName) {
        return cloudFoundryOperations.organizationAdmin()
            .getQuota(GetQuotaRequest.builder()
                .name(quotaName)
                .build());
    }

}
