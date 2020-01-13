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

package org.cloudfoundry.util;

import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesResponse;
import org.cloudfoundry.client.v2.spaces.SpaceEntity;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.client.v2.spaces.Spaces;
import org.cloudfoundry.client.v3.Pagination;
import org.cloudfoundry.client.v3.packages.BitsData;
import org.cloudfoundry.client.v3.packages.ListPackagesRequest;
import org.cloudfoundry.client.v3.packages.ListPackagesResponse;
import org.cloudfoundry.client.v3.packages.PackageResource;
import org.cloudfoundry.client.v3.packages.PackageState;
import org.cloudfoundry.client.v3.packages.PackageType;
import org.cloudfoundry.client.v3.packages.Packages;
import org.cloudfoundry.uaa.users.ListUsersRequest;
import org.cloudfoundry.uaa.users.ListUsersResponse;
import org.cloudfoundry.uaa.users.Meta;
import org.cloudfoundry.uaa.users.Name;
import org.cloudfoundry.uaa.users.User;
import org.cloudfoundry.uaa.users.Users;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class PaginationUtilsTest {

    @Test
    public void requestClientV2Resources() {
        Spaces spaces = mock(Spaces.class, RETURNS_SMART_NULLS);

        requestListSpaces(spaces, 1, 3);
        requestListSpaces(spaces, 2, 3);
        requestListSpaces(spaces, 3, 3);

        PaginationUtils
            .requestClientV2Resources(page -> spaces
                .list(ListSpacesRequest.builder()
                    .page(page)
                    .build()))
            .as(StepVerifier::create)
            .expectNextCount(3)
            .expectComplete()
            .verify(Duration.ofSeconds(1));
    }

    @Test
    public void requestClientV2ResourcesEmpty() {
        Spaces spaces = mock(Spaces.class, RETURNS_SMART_NULLS);

        requestListSpacesEmpty(spaces);

        PaginationUtils
            .requestClientV2Resources(page -> spaces
                .list(ListSpacesRequest.builder()
                    .page(page)
                    .build()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(1));
    }

    @Test
    public void requestClientV3Empty() {
        Packages packages = mock(Packages.class, RETURNS_SMART_NULLS);

        requestListPackagesEmpty(packages);

        PaginationUtils
            .requestClientV3Resources(page -> packages
                .list(ListPackagesRequest.builder()
                    .page(page)
                    .build()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(1));
    }

    @Test
    public void requestClientV3Resources() {
        Packages packages = mock(Packages.class, RETURNS_SMART_NULLS);

        requestListPackages(packages, 1, 3);
        requestListPackages(packages, 2, 3);
        requestListPackages(packages, 3, 3);

        PaginationUtils
            .requestClientV3Resources(page -> packages
                .list(ListPackagesRequest.builder()
                    .page(page)
                    .build()))
            .as(StepVerifier::create)
            .expectNextCount(3)
            .expectComplete()
            .verify(Duration.ofSeconds(1));
    }

    @Test
    public void requestUaaResources() {
        Users users = mock(Users.class, RETURNS_SMART_NULLS);

        requestListUsers(users, 1, 100, 250);
        requestListUsers(users, 101, 100, 250);
        requestListUsers(users, 201, 100, 250);

        PaginationUtils
            .requestUaaResources(startIndex -> users
                .list(ListUsersRequest.builder()
                    .startIndex(startIndex)
                    .build()))
            .as(StepVerifier::create)
            .expectNextCount(3)
            .expectComplete()
            .verify(Duration.ofSeconds(1));
    }

    @Test
    public void requestUaaResourcesEmpty() {
        Users users = mock(Users.class, RETURNS_SMART_NULLS);

        requestListUsersEmpty(users, 1, 100);

        PaginationUtils
            .requestUaaResources(startIndex -> users
                .list(ListUsersRequest.builder()
                    .startIndex(startIndex)
                    .build()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(1));
    }

    private static void requestListPackages(Packages packages, Integer page, Integer totalPages) {
        when(packages
            .list(ListPackagesRequest.builder()
                .page(page)
                .build()))
            .thenReturn(Mono
                .just(ListPackagesResponse.builder()
                    .resource(PackageResource.builder()
                        .id(page.toString())
                        .createdAt("test-created-at")
                        .updatedAt("test-updated-at")
                        .type(PackageType.BITS)
                        .data(BitsData.builder()
                            .build())
                        .state(PackageState.READY)
                        .build())
                    .pagination(Pagination.builder()
                        .totalPages(totalPages)
                        .build())
                    .build()));
    }

    private static void requestListPackagesEmpty(Packages packages) {
        when(packages
            .list(ListPackagesRequest.builder()
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(ListPackagesResponse.builder()
                    .resources(Collections.emptyList())
                    .pagination(Pagination.builder()
                        .totalPages(1)
                        .build())
                    .build()));
    }

    private static void requestListSpaces(Spaces spaces, Integer page, Integer totalPages) {
        when(spaces
            .list(ListSpacesRequest.builder()
                .page(page)
                .build()))
            .thenReturn(Mono
                .just(ListSpacesResponse.builder()
                    .resource(SpaceResource.builder()
                        .entity(SpaceEntity.builder()
                            .name(page.toString())
                            .build())
                        .build())
                    .totalPages(totalPages)
                    .build()));
    }

    private static void requestListSpacesEmpty(Spaces spaces) {
        when(spaces
            .list(ListSpacesRequest.builder()
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(ListSpacesResponse.builder()
                    .resources(Collections.emptyList())
                    .totalPages(1)
                    .build()));
    }

    private static void requestListUsers(Users users, Integer startIndex, Integer itemsPerPage, Integer totalResults) {
        when(users
            .list(ListUsersRequest.builder()
                .startIndex(startIndex)
                .build()))
            .thenReturn(Mono
                .just(ListUsersResponse.builder()
                    .resource(User.builder()
                        .active(true)
                        .meta(Meta.builder()
                            .created("test-created")
                            .lastModified("test-last-modified")
                            .version(0)
                            .build())
                        .id(startIndex.toString())
                        .name(Name.builder()
                            .build())
                        .origin("test-origin")
                        .passwordLastModified("test-password-last-modified")
                        .verified(true)
                        .userName("test-user-name")
                        .zoneId("test-zone-id")
                        .build())
                    .itemsPerPage(itemsPerPage)
                    .startIndex(startIndex)
                    .totalResults(totalResults)
                    .build()));
    }

    private static void requestListUsersEmpty(Users users, Integer startIndex, Integer itemsPerPage) {
        when(users
            .list(ListUsersRequest.builder()
                .startIndex(startIndex)
                .build()))
            .thenReturn(Mono
                .just(ListUsersResponse.builder()
                    .resources(Collections.emptyList())
                    .itemsPerPage(itemsPerPage)
                    .startIndex(startIndex)
                    .totalResults(0)
                    .build()));
    }

}
