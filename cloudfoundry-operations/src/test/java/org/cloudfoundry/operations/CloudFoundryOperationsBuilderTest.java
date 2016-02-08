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

package org.cloudfoundry.operations;

import org.cloudfoundry.client.v2.Resource.Metadata;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.organizations.OrganizationEntity;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesResponse;
import org.cloudfoundry.client.v2.spaces.SpaceEntity;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.junit.Test;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

public final class CloudFoundryOperationsBuilderTest extends AbstractOperationsTest {

    private final CloudFoundryOperationsBuilder builder = new CloudFoundryOperationsBuilder();

    @Test(expected = IllegalArgumentException.class)
    public void buildNoClient() {
        this.builder.build();
    }

    @Test
    public void buildWithClient() {
        this.builder
            .cloudFoundryClient(this.cloudFoundryClient)
            .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildWithInvalidOrganization() {
        ListOrganizationsRequest request = ListOrganizationsRequest.builder()
            .name("test-organization")
            .page(1)
            .build();

        ListOrganizationsResponse response = ListOrganizationsResponse.builder()
            .totalPages(1)
            .build();

        when(this.cloudFoundryClient.organizations().list(request)).thenReturn(Mono.just(response));

        this.builder
            .cloudFoundryClient(this.cloudFoundryClient)
            .target("test-organization")
            .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildWithInvalidSpace() {
        ListOrganizationsRequest orgRequest = ListOrganizationsRequest.builder()
            .name("test-organization")
            .page(1)
            .build();

        ListOrganizationsResponse orgResponse = ListOrganizationsResponse.builder()
            .resource(OrganizationResource.builder()
                .metadata(Metadata.builder()
                    .id("test-organization-id")
                    .build())
                .entity(OrganizationEntity.builder()
                    .name("test-name")
                    .build())
                .build())
            .totalPages(1)
            .build();

        when(this.cloudFoundryClient.organizations().list(orgRequest)).thenReturn(Mono.just(orgResponse));

        ListSpacesRequest spaceRequest = ListSpacesRequest.builder()
            .organizationId("test-organization-id")
            .name("test-space")
            .page(1)
            .build();

        ListSpacesResponse spaceResponse = ListSpacesResponse.builder()
            .totalPages(1)
            .build();

        when(this.cloudFoundryClient.spaces().list(spaceRequest)).thenReturn(Mono.just(spaceResponse));

        this.builder
            .cloudFoundryClient(this.cloudFoundryClient)
            .target("test-organization", "test-space")
            .build();
    }

    @Test
    public void buildWithOrganization() {
        ListOrganizationsRequest request = ListOrganizationsRequest.builder()
            .name("test-organization")
            .page(1)
            .build();

        ListOrganizationsResponse response = ListOrganizationsResponse.builder()
            .resource(OrganizationResource.builder()
                .metadata(Metadata.builder()
                    .id("test-organization-id")
                    .build())
                .entity(OrganizationEntity.builder()
                    .name("test-name")
                    .build())
                .build())
            .totalPages(1)
            .build();

        when(this.cloudFoundryClient.organizations().list(request)).thenReturn(Mono.just(response));

        this.builder
            .cloudFoundryClient(this.cloudFoundryClient)
            .target("test-organization")
            .build();
    }

    @Test
    public void buildWithSpace() {
        ListOrganizationsRequest orgRequest = ListOrganizationsRequest.builder()
            .name("test-organization")
            .page(1)
            .build();

        ListOrganizationsResponse orgResponse = ListOrganizationsResponse.builder()
            .resource(OrganizationResource.builder()
                .metadata(Metadata.builder()
                    .id("test-organization-id")
                    .build())
                .entity(OrganizationEntity.builder()
                    .name("test-name")
                    .build())
                .build())
            .totalPages(1)
            .build();

        when(this.cloudFoundryClient.organizations().list(orgRequest)).thenReturn(Mono.just(orgResponse));

        ListSpacesRequest spaceRequest = ListSpacesRequest.builder()
            .organizationId("test-organization-id")
            .name("test-space")
            .page(1)
            .build();

        ListSpacesResponse spaceResponse = ListSpacesResponse.builder()
            .resource(SpaceResource.builder()
                .metadata(Metadata.builder()
                    .id("test-space-id")
                    .build())
                .entity(SpaceEntity.builder()
                    .name("test-name")
                    .build())
                .build())
            .totalPages(1)
            .build();

        when(this.cloudFoundryClient.spaces().list(spaceRequest)).thenReturn(Mono.just(spaceResponse));

        this.builder
            .cloudFoundryClient(this.cloudFoundryClient)
            .target("test-organization", "test-space")
            .build();
    }

}
