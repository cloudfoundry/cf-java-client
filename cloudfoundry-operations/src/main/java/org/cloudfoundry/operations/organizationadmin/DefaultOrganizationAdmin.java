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
import org.cloudfoundry.client.v2.organizationquotadefinitions.UpdateOrganizationQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.UpdateOrganizationQuotaDefinitionResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.organizations.UpdateOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.UpdateOrganizationResponse;
import org.cloudfoundry.operations.util.OperationsLogging;
import org.cloudfoundry.util.ExceptionUtils;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class DefaultOrganizationAdmin implements OrganizationAdmin {

    private final Mono<CloudFoundryClient> cloudFoundryClient;

    public DefaultOrganizationAdmin(Mono<CloudFoundryClient> cloudFoundryClient) {
        this.cloudFoundryClient = cloudFoundryClient;
    }

    @Override
    public Mono<OrganizationQuota> createQuota(CreateQuotaRequest request) {
        return this.cloudFoundryClient
            .flatMap(cloudFoundryClient -> createOrganizationQuota(cloudFoundryClient, request))
            .map(DefaultOrganizationAdmin::toOrganizationQuota)
            .transform(OperationsLogging.log("Create Organization Quota"))
            .checkpoint();
    }

    @Override
    public Mono<Void> deleteQuota(DeleteQuotaRequest request) {
        return this.cloudFoundryClient
            .flatMap(cloudFoundryClient -> Mono.when(
                Mono.just(cloudFoundryClient),
                Mono.just(request.getCompletionTimeout()),
                getOrganizationQuotaId(cloudFoundryClient, request.getName())
            ))
            .flatMap(function(DefaultOrganizationAdmin::deleteOrganizationQuota))
            .transform(OperationsLogging.log("Delete Organization Quota"))
            .checkpoint();
    }

    @Override
    public Mono<OrganizationQuota> getQuota(GetQuotaRequest request) {
        return this.cloudFoundryClient
            .flatMap(cloudFoundryClient -> getOrganizationQuota(cloudFoundryClient, request.getName()))
            .map(DefaultOrganizationAdmin::toOrganizationQuota)
            .transform(OperationsLogging.log("Get Organization Quota"))
            .checkpoint();
    }

    @Override
    public Flux<OrganizationQuota> listQuotas() {
        return this.cloudFoundryClient
            .flatMapMany(DefaultOrganizationAdmin::requestListOrganizationQuotas)
            .map(DefaultOrganizationAdmin::toOrganizationQuota)
            .transform(OperationsLogging.log("List Organization Quotas"))
            .checkpoint();
    }

    @Override
    public Mono<Void> setQuota(SetQuotaRequest request) {
        return this.cloudFoundryClient
            .flatMap(cloudFoundryClient -> Mono.when(
                Mono.just(cloudFoundryClient),
                getOrganizationId(cloudFoundryClient, request.getOrganizationName()),
                getOrganizationQuotaId(cloudFoundryClient, request.getQuotaName())
            ))
            .flatMap(function((DefaultOrganizationAdmin::requestUpdateOrganization)))
            .then()
            .transform(OperationsLogging.log("Set Organization Quota"))
            .checkpoint();
    }

    @Override
    public Mono<OrganizationQuota> updateQuota(UpdateQuotaRequest request) {
        return this.cloudFoundryClient
            .flatMap(cloudFoundryClient -> Mono.when(
                Mono.just(cloudFoundryClient),
                getOrganizationQuota(cloudFoundryClient, request.getName())
            ))
            .flatMap(function((cloudFoundryClient, exitingQuotaDefinition) -> updateOrganizationQuota(cloudFoundryClient, request, exitingQuotaDefinition)))
            .map(DefaultOrganizationAdmin::toOrganizationQuota)
            .transform(OperationsLogging.log("Update Organization Quota"))
            .checkpoint();
    }

    private static Mono<CreateOrganizationQuotaDefinitionResponse> createOrganizationQuota(CloudFoundryClient cloudFoundryClient, CreateQuotaRequest request) {
        return requestCreateOrganizationQuota(
            cloudFoundryClient,
            Optional.ofNullable(request.getInstanceMemoryLimit()).orElse(-1),
            Optional.ofNullable(request.getMemoryLimit()).orElse(0),
            request.getName(),
            Optional.ofNullable(request.getAllowPaidServicePlans()).orElse(false),
            Optional.ofNullable(request.getTotalRoutes()).orElse(0),
            Optional.ofNullable(request.getTotalServices()).orElse(0));
    }

    private static Mono<Void> deleteOrganizationQuota(CloudFoundryClient cloudFoundryClient, Duration completionTimeout, String quotaId) {
        return requestDeleteOrganizationQuota(cloudFoundryClient, quotaId)
            .flatMap(job -> JobUtils.waitForCompletion(cloudFoundryClient, completionTimeout, job));
    }

    private static Mono<String> getOrganizationId(CloudFoundryClient cloudFoundryClient, String name) {
        return requestListOrganizations(cloudFoundryClient, name)
            .single()
            .onErrorResume(NoSuchElementException.class, t -> ExceptionUtils.illegalArgument("Organization %s does not exist", name))
            .map(ResourceUtils::getId);
    }

    private static Mono<OrganizationQuotaDefinitionResource> getOrganizationQuota(CloudFoundryClient cloudFoundryClient, String name) {
        return requestListOrganizationQuotas(cloudFoundryClient, name)
            .single()
            .onErrorResume(NoSuchElementException.class, t -> ExceptionUtils.illegalArgument("Quota %s does not exist", name));
    }

    private static Mono<String> getOrganizationQuotaId(CloudFoundryClient cloudFoundryClient, String name) {
        return getOrganizationQuota(cloudFoundryClient, name)
            .map(ResourceUtils::getId);
    }

    private static Mono<CreateOrganizationQuotaDefinitionResponse> requestCreateOrganizationQuota(CloudFoundryClient cloudFoundryClient, Integer instanceMemoryLimit, Integer memoryLimit, String name,
                                                                                                  Boolean nonBasicServicesAllowed, Integer totalRoutes, Integer totalServices) {
        return cloudFoundryClient.organizationQuotaDefinitions()
            .create(CreateOrganizationQuotaDefinitionRequest.builder()
                .instanceMemoryLimit(instanceMemoryLimit)
                .memoryLimit(memoryLimit)
                .name(name)
                .nonBasicServicesAllowed(nonBasicServicesAllowed)
                .totalRoutes(totalRoutes)
                .totalServices(totalServices)
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
            .requestClientV2Resources(page -> cloudFoundryClient.organizationQuotaDefinitions()
                .list(ListOrganizationQuotaDefinitionsRequest.builder()
                    .page(page)
                    .build()));
    }

    private static Flux<OrganizationQuotaDefinitionResource> requestListOrganizationQuotas(CloudFoundryClient cloudFoundryClient, String name) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.organizationQuotaDefinitions()
                .list(ListOrganizationQuotaDefinitionsRequest.builder()
                    .name(name)
                    .page(page)
                    .build()));
    }

    private static Flux<OrganizationResource> requestListOrganizations(CloudFoundryClient cloudFoundryClient, String name) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.organizations()
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

    private static Mono<UpdateOrganizationQuotaDefinitionResponse> requestUpdateOrganizationQuota(CloudFoundryClient cloudFoundryClient, String organizationQuotaDefinitionId,
                                                                                                  Integer instanceMemoryLimit, Integer memoryLimit, String name, Boolean nonBasicServicesAllowed,
                                                                                                  Integer totalRoutes, Integer totalServices) {
        return cloudFoundryClient.organizationQuotaDefinitions()
            .update(UpdateOrganizationQuotaDefinitionRequest.builder()
                .instanceMemoryLimit(instanceMemoryLimit)
                .memoryLimit(memoryLimit)
                .name(name)
                .organizationQuotaDefinitionId(organizationQuotaDefinitionId)
                .nonBasicServicesAllowed(nonBasicServicesAllowed)
                .totalRoutes(totalRoutes)
                .totalServices(totalServices)
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

    private static Mono<UpdateOrganizationQuotaDefinitionResponse> updateOrganizationQuota(CloudFoundryClient cloudFoundryClient, UpdateQuotaRequest request,
                                                                                           OrganizationQuotaDefinitionResource resource) {
        OrganizationQuotaDefinitionEntity existing = ResourceUtils.getEntity(resource);

        return requestUpdateOrganizationQuota(
            cloudFoundryClient,
            ResourceUtils.getId(resource),
            Optional.ofNullable(request.getInstanceMemoryLimit()).orElse(existing.getInstanceMemoryLimit()),
            Optional.ofNullable(request.getMemoryLimit()).orElse(existing.getMemoryLimit()),
            Optional.ofNullable(request.getNewName()).orElse(existing.getName()),
            Optional.ofNullable(request.getAllowPaidServicePlans()).orElse(existing.getNonBasicServicesAllowed()),
            Optional.ofNullable(request.getTotalRoutes()).orElse(existing.getTotalRoutes()),
            Optional.ofNullable(request.getTotalServices()).orElse(existing.getTotalServices()));
    }

}
