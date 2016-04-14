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
import org.cloudfoundry.client.v2.organizationquotadefinitions.ListOrganizationQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.OrganizationQuotaDefinitionEntity;
import org.cloudfoundry.client.v2.organizationquotadefinitions.OrganizationQuotaDefinitionResource;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import reactor.core.publisher.Flux;

public final class DefaultOrganizationAdmin implements OrganizationAdmin {

    private final CloudFoundryClient cloudFoundryClient;

    public DefaultOrganizationAdmin(CloudFoundryClient cloudFoundryClient) {
        this.cloudFoundryClient = cloudFoundryClient;
    }

    @Override
    public Flux<OrganizationQuota> listQuotas() {
        return requestListOrganizationQuotas(this.cloudFoundryClient)
            .map(DefaultOrganizationAdmin::toOrganizationQuota);
    }

    private static Flux<OrganizationQuotaDefinitionResource> requestListOrganizationQuotas(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.organizationQuotaDefinitions()
                .list(ListOrganizationQuotaDefinitionsRequest.builder()
                    .page(page)
                    .build()));
    }

    private static OrganizationQuota toOrganizationQuota(OrganizationQuotaDefinitionResource resource) {
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
