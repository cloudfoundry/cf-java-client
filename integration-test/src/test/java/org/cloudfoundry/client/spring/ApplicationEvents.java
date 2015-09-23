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

package org.cloudfoundry.client.spring;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.PaginatedResponse;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesResponse;
import org.junit.Before;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.core.env.StandardEnvironment;
import reactor.fn.tuple.Tuple;
import reactor.fn.tuple.Tuple2;
import reactor.rx.Stream;
import reactor.rx.Streams;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class ApplicationEvents {

    private volatile CloudFoundryClient client;

    @Before
    public void configure() throws Exception {
        RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(new StandardEnvironment(), null);

        this.client = new SpringCloudFoundryClientBuilder()
                .withApi(resolver.getRequiredProperty("test.host"))
                .withCredentials(
                        resolver.getRequiredProperty("test.username"),
                        resolver.getRequiredProperty("test.password"))
                .build();
    }


    @Test
    public void applicationEvents() {
        listOrganizations()
                .flatMap(organizationsPage -> Streams.from(organizationsPage.getResources()))
                .filter(organizationResource -> organizationResource.getEntity().getName().startsWith("s1-scs-demo-"))
                .flatMap(this::listSpaces)
                .consume(this::printLine);
    }

    private <T extends PaginatedResponse, U> Stream<T> getAllPages(Function<Integer, U> requestProvider,
                                                                   Function<U, Publisher<T>> operationExecutor) {
        U initialRequest = requestProvider.apply(1);
        return Streams.wrap(operationExecutor.apply(initialRequest))
                .flatMap(response -> {
                    List<Publisher<T>> pages = new ArrayList<>();
                    pages.add(Streams.just(response));

                    for (int i = 2; i <= response.getTotalPages(); i++) {
                        U request = requestProvider.apply(i);
                        pages.add(operationExecutor.apply(request));
                    }

                    return Streams.concat(pages);
                });
    }

    private Stream<ListOrganizationsResponse> listOrganizations() {
        return getAllPages(page -> new ListOrganizationsRequest().withPage(page),
                request -> this.client.organizations().list(request));
    }

    private Publisher<Tuple2<Resource<ListOrganizationsResponse.Entity>, Resource<ListSpacesResponse.Entity>>>
    listSpaces(Resource<ListOrganizationsResponse.Entity> organizationResource) {

        return getAllPages(
                page -> new ListSpacesRequest().filterByOrganizationId(organizationResource.getMetadata()
                        .getId()).withPage(page),
                request -> this.client.spaces().list(request))

                .flatMap(spacesPage -> Streams.from(spacesPage.getResources()))
                .map(spaceResource -> Tuple.of(organizationResource, spaceResource));
    }

    private void printLine(Tuple2<Resource<ListOrganizationsResponse.Entity>,
            Resource<ListSpacesResponse.Entity>> tuple) {

        String organizationName = tuple.getT1().getEntity().getName();
        String spaceName = tuple.getT2().getEntity().getName();

        System.out.printf("%s/%s%n", organizationName, spaceName);
    }


}
