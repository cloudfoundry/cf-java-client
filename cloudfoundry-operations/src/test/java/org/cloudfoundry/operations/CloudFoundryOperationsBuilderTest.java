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
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse.ListOrganizationsResponseEntity;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse.ListOrganizationsResponseResource;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesResponse;
import org.cloudfoundry.client.v2.spaces.ListSpacesResponse.ListSpacesResponseResource;
import org.cloudfoundry.client.v2.spaces.SpaceResource.SpaceEntity;
import org.junit.Test;
import reactor.Publishers;

import static org.mockito.Mockito.when;

public final class CloudFoundryOperationsBuilderTest extends AbstractOperationsTest {

    private final CloudFoundryOperationsBuilder builder = new CloudFoundryOperationsBuilder();

    @Test
    public void buildWithClient() {
        this.builder
                .withCloudFoundryClient(this.cloudFoundryClient)
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildNoClient() {
        this.builder.build();
    }

    @Test
    public void buildWithOrganization() {
        ListOrganizationsRequest request = new ListOrganizationsRequest()
                .withName("test-organization");

        ListOrganizationsResponse response = new ListOrganizationsResponse()
                .withResource(new ListOrganizationsResponseResource()
                        .withMetadata(new Metadata().withId("test-organization-id"))
                        .withEntity(new ListOrganizationsResponseEntity()
                                .withName("test-name")))
                .withTotalPages(1);

        when(this.cloudFoundryClient.organizations().list(request)).thenReturn(Publishers.just(response));

        this.builder
                .withCloudFoundryClient(this.cloudFoundryClient)
                .withTarget("test-organization")
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildWithInvalidOrganization() {
        ListOrganizationsRequest request = new ListOrganizationsRequest()
                .withName("test-organization");

        ListOrganizationsResponse response = new ListOrganizationsResponse()
                .withTotalPages(1);

        when(this.cloudFoundryClient.organizations().list(request)).thenReturn(Publishers.just(response));

        this.builder
                .withCloudFoundryClient(this.cloudFoundryClient)
                .withTarget("test-organization")
                .build();
    }

    @Test
    public void buildWithOrganizationSpace() {
        ListOrganizationsRequest orgRequest = new ListOrganizationsRequest()
                .withName("test-organization");

        ListOrganizationsResponse orgResponse = new ListOrganizationsResponse()
                .withResource(new ListOrganizationsResponseResource()
                        .withMetadata(new Metadata().withId("test-organization-id"))
                        .withEntity(new ListOrganizationsResponseEntity()
                                .withName("test-name")))
                .withTotalPages(1);

        when(this.cloudFoundryClient.organizations().list(orgRequest)).thenReturn(Publishers.just(orgResponse));

        ListSpacesRequest spaceRequest = new ListSpacesRequest()
                .withOrganizationId("test-organization-id")
                .withName("test-name");

        ListSpacesResponse spaceResponse = new ListSpacesResponse()
                .withResource(new ListSpacesResponseResource()
                        .withMetadata(new Metadata().withId("test-space-id"))
                        .withEntity(new SpaceEntity()
                                .withName("test-name")))
                .withTotalPages(1);

        when(this.cloudFoundryClient.spaces().list(spaceRequest)).thenReturn(Publishers.just(spaceResponse));

        this.builder
                .withCloudFoundryClient(this.cloudFoundryClient)
                .withTarget("test-organization", "test-space")
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildWithOrganizationInvalidSpace() {
        ListOrganizationsRequest orgRequest = new ListOrganizationsRequest()
                .withName("test-organization");

        ListOrganizationsResponse orgResponse = new ListOrganizationsResponse()
                .withResource(new ListOrganizationsResponseResource()
                        .withMetadata(new Metadata().withId("test-organization-id"))
                        .withEntity(new ListOrganizationsResponseEntity()
                                .withName("test-name")))
                .withTotalPages(1);

        when(this.cloudFoundryClient.organizations().list(orgRequest)).thenReturn(Publishers.just(orgResponse));

        ListSpacesRequest spaceRequest = new ListSpacesRequest()
                .withOrganizationId("test-organization-id")
                .withName("test-name");

        ListSpacesResponse spaceResponse = new ListSpacesResponse()
                .withTotalPages(1);

        when(this.cloudFoundryClient.spaces().list(spaceRequest)).thenReturn(Publishers.just(spaceResponse));

        this.builder
                .withCloudFoundryClient(this.cloudFoundryClient)
                .withTarget("test-organization", "test-space")
                .build();
    }

}
