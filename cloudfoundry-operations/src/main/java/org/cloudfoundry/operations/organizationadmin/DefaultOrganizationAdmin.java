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

package org.cloudfoundry.operations.organizationadmin;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.organizationquotadefinitions.AbstractOrganizationQuotaDefinition;
import org.cloudfoundry.client.v2.organizationquotadefinitions.CreateOrganizationQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.CreateOrganizationQuotaDefinitionResponse;
import org.cloudfoundry.client.v2.organizationquotadefinitions.DeleteOrganizationQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.DeleteOrganizationQuotaDefinitionResponse;
import org.cloudfoundry.client.v2.organizationquotadefinitions.ListOrganizationQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.OrganizationQuotaDefinitionEntity;
import org.cloudfoundry.client.v2.organizationquotadefinitions.OrganizationQuotaDefinitionResource;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.organizations.UpdateOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.UpdateOrganizationResponse;
import org.cloudfoundry.util.ExceptionUtils;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.cloudfoundry.util.ValidationUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class DefaultOrganizationAdmin implements OrganizationAdmin {

    private final CloudFoundryClient cloudFoundryClient;

    public DefaultOrganizationAdmin(CloudFoundryClient cloudFoundryClient) {
        this.cloudFoundryClient = cloudFoundryClient;
    }

    @Override
    public Mono<OrganizationQuota> createQuota(CreateQuotaRequest request) {
        return ValidationUtils
            .validate(request)
            .then(validRequest -> requestCreateOrganizationQuota(this.cloudFoundryClient, validRequest.getInstanceMemoryLimit(), validRequest.getMemoryLimit(), validRequest.getName(),
                validRequest.getAllowPaidServicePlans(), validRequest.getTotalRoutes(), validRequest.getTotalServices()))
            .map(DefaultOrganizationAdmin::toOrganizationQuota);
    }

    @Override
    public Mono<Void> deleteQuota(DeleteQuotaRequest request) {
        return ValidationUtils
            .validate(request)
            .then(request1 -> getOrganizationQuota(this.cloudFoundryClient, request1.getName()))
            .map(ResourceUtils::getId)
            .then(quotaId -> deleteOrganizationQuota(this.cloudFoundryClient, quotaId));
    }

    @Override
    public Mono<OrganizationQuota> getQuota(GetQuotaRequest request) {
        return ValidationUtils
            .validate(request)
            .then(request1 -> getOrganizationQuota(this.cloudFoundryClient, request1.getName()))
            .map(DefaultOrganizationAdmin::toOrganizationQuota);
    }

    @Override
    public Flux<OrganizationQuota> listQuotas() {
        return requestListOrganizationQuotas(this.cloudFoundryClient)
            .map(DefaultOrganizationAdmin::toOrganizationQuota);
    }

    @Override
    public Mono<Void> setQuota(SetQuotaRequest request) {
        return ValidationUtils.validate(request)
            .then(request1 -> Mono.when(
                getOrganizationId(this.cloudFoundryClient, request1.getOrganizationName()),
                getOrganizationQuotaId(this.cloudFoundryClient, request1.getQuotaName())
            ))
            .then(function(((organizationId, quotaDefinitionId) -> requestUpdateOrganization(this.cloudFoundryClient, organizationId, quotaDefinitionId))))
            .after();
    }

    private static Mono<Void> deleteOrganizationQuota(CloudFoundryClient cloudFoundryClient, String quotaId) {
        return requestDeleteOrganizationQuota(cloudFoundryClient, quotaId)
            .then(job -> JobUtils.waitForCompletion(cloudFoundryClient, job));
    }

    private static Mono<String> getOrganizationId(CloudFoundryClient cloudFoundryClient, String name) {
        return requestListOrganizations(cloudFoundryClient, name)
            .single()
            .otherwise(ExceptionUtils.replace(NoSuchElementException.class, () -> ExceptionUtils.illegalArgument("Organization %s does not exist", name)))
            .map(ResourceUtils::getId);
    }

    private static Mono<OrganizationQuotaDefinitionResource> getOrganizationQuota(CloudFoundryClient cloudFoundryClient, String name) {
        return requestListOrganizationQuotas(cloudFoundryClient, name)
            .single()
            .otherwise(ExceptionUtils.replace(NoSuchElementException.class, () -> ExceptionUtils.illegalArgument("Quota %s does not exist", name)));
    }

    private static Mono<String> getOrganizationQuotaId(CloudFoundryClient cloudFoundryClient, String name) {
        return getOrganizationQuota(cloudFoundryClient, name)
            .map(ResourceUtils::getId);
    }

    private static Mono<CreateOrganizationQuotaDefinitionResponse> requestCreateOrganizationQuota(CloudFoundryClient cloudFoundryClient, Integer instanceMemoryLimit, Integer memoryLimit, String name,
                                                                                                  Boolean nonBasicServicesAllowed, Integer totalRoutes, Integer totalServices) {
        return cloudFoundryClient.organizationQuotaDefinitions()
            .create(CreateOrganizationQuotaDefinitionRequest.builder()
                .instanceMemoryLimit(Optional.ofNullable(instanceMemoryLimit).orElse(-1))
                .memoryLimit(Optional.ofNullable(memoryLimit).orElse(0))
                .name(name)
                .nonBasicServicesAllowed(Optional.ofNullable(nonBasicServicesAllowed).orElse(false))
                .totalRoutes(Optional.ofNullable(totalRoutes).orElse(0))
                .totalServices(Optional.ofNullable(totalServices).orElse(0))
                .build());
    }

    private static Mono<DeleteOrganizationQuotaDefinitionResponse> requestDeleteOrganizationQuota(CloudFoundryClient cloudFoundryClient, String quotaId) {
        return cloudFoundryClient.organizationQuotaDefinitions()
            .delete(DeleteOrganizationQuotaDefinitionRequest.builder()
                .organizationQuotaDefinitionId(quotaId)
                .async(true)
                .build());
    }

    private static Flux<OrganizationQuotaDefinitionResource> requestListOrganizationQuotas(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.organizationQuotaDefinitions()
                .list(ListOrganizationQuotaDefinitionsRequest.builder()
                    .page(page)
                    .build()));
    }

    private static Flux<OrganizationQuotaDefinitionResource> requestListOrganizationQuotas(CloudFoundryClient cloudFoundryClient, String name) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.organizationQuotaDefinitions()
                .list(ListOrganizationQuotaDefinitionsRequest.builder()
                    .name(name)
                    .page(page)
                    .build()));
    }

    private static Flux<OrganizationResource> requestListOrganizations(CloudFoundryClient cloudFoundryClient, String name) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.organizations()
                .list(ListOrganizationsRequest.builder()
                    .name(name)
                    .page(page)
                    .build()));
    }

    private static Mono<UpdateOrganizationResponse> requestUpdateOrganization(CloudFoundryClient cloudFoundryClient, String organizationId, String quotaDefinitionId) {
        return cloudFoundryClient.organizations()
            .update(UpdateOrganizationRequest.builder()
                .organizationId(organizationId)
                .quotaDefinitionId(quotaDefinitionId)
                .build());
    }

    private static OrganizationQuota toOrganizationQuota(AbstractOrganizationQuotaDefinition resource) {
        OrganizationQuotaDefinitionEntity entity = ResourceUtils.getEntity(resource);

        return OrganizationQuota.builder()
            .allowPaidServicePlans(entity.getNonBasicServicesAllowed())
            .applicationInstanceLimit(entity.getApplicationInstanceLimit())
            .id(ResourceUtils.getId(resource))
            .instanceMemoryLimit(entity.getInstanceMemoryLimit())
            .memoryLimit(entity.getMemoryLimit())
            .name(entity.getName())
            .totalRoutes(entity.getTotalRoutes())
            .totalServices(entity.getTotalServices())
            .build();
    }

}
