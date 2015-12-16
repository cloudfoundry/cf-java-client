/*
 * Copyright 2013-2015 the original author or authors.
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

import org.cloudfoundry.client.v2.Resource.Metadata;
import org.cloudfoundry.client.v2.spacequotadefinitions.ListSpaceQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.spacequotadefinitions.ListSpaceQuotaDefinitionsResponse;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitionEntity;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitionResource;
import org.junit.Test;
import reactor.Publishers;
import reactor.rx.Streams;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public final class DefaultSpaceQuotasTest extends AbstractOperationsTest {

    private final SpaceQuotas spaceQuotas = new DefaultSpaceQuotas(this.cloudFoundryClient);

    private static ListSpaceQuotaDefinitionsResponse getListSpaceQuotaDefinitionsResponse(int page, int numPages) {
        return ListSpaceQuotaDefinitionsResponse.builder()
                .resource(getSpaceQuotaDefinitionResource(page))
                .totalPages(numPages)
                .build();
    }

    private static SpaceQuota getSpaceQuota(int index) {
        return SpaceQuota.builder()
                .id("test-id-" + index)
                .instanceMemoryLimit(1024)
                .paidServicePlans(true)
                .totalMemoryLimit(2048)
                .totalRoutes(10)
                .name("test-name-" + index)
                .organizationId("test-org-id-" + index)
                .build();
    }

    private static SpaceQuotaDefinitionResource getSpaceQuotaDefinitionResource(int resourceIndex) {
        return SpaceQuotaDefinitionResource.builder()
                .metadata(Metadata.builder()
                        .id("test-id-" + resourceIndex)
                        .build())
                .entity(SpaceQuotaDefinitionEntity.builder()
                        .instanceMemoryLimit(1024)
                        .memoryLimit(2048)
                        .name("test-name-" + resourceIndex)
                        .nonBasicServicesAllowed(true)
                        .organizationId("test-org-id-" + resourceIndex)
                        .totalRoutes(10)
                        .build())
                .build();
    }

    @Test
    public void list() {
        ListSpaceQuotaDefinitionsResponse page1 = getListSpaceQuotaDefinitionsResponse(1, 2);
        ListSpaceQuotaDefinitionsResponse page2 = getListSpaceQuotaDefinitionsResponse(2, 2);

        when(this.cloudFoundryClient.spaceQuotaDefinitions()
                .list(ListSpaceQuotaDefinitionsRequest.builder().page(1).build()))
                .thenReturn(Publishers.just(page1));

        when(this.cloudFoundryClient.spaceQuotaDefinitions()
                .list(ListSpaceQuotaDefinitionsRequest.builder().page(2).build()))
                .thenReturn(Publishers.just(page2));

        List<SpaceQuota> expected = Arrays.asList(getSpaceQuota(1), getSpaceQuota(2));

        List<SpaceQuota> actual = Streams.wrap(this.spaceQuotas.list()).toList().get();

        assertEquals(expected, actual);
    }

}
