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

package org.cloudfoundry.client.spring.v3.packages;

import lombok.ToString;
import org.cloudfoundry.client.spring.util.AbstractSpringOperations;
import org.cloudfoundry.client.spring.util.CollectionUtils;
import org.cloudfoundry.client.spring.util.QueryBuilder;
import org.cloudfoundry.client.v3.packages.CopyPackageRequest;
import org.cloudfoundry.client.v3.packages.CopyPackageResponse;
import org.cloudfoundry.client.v3.packages.CreatePackageRequest;
import org.cloudfoundry.client.v3.packages.CreatePackageResponse;
import org.cloudfoundry.client.v3.packages.DeletePackageRequest;
import org.cloudfoundry.client.v3.packages.GetPackageRequest;
import org.cloudfoundry.client.v3.packages.GetPackageResponse;
import org.cloudfoundry.client.v3.packages.ListPackagesRequest;
import org.cloudfoundry.client.v3.packages.ListPackagesResponse;
import org.cloudfoundry.client.v3.packages.Packages;
import org.cloudfoundry.client.v3.packages.StagePackageRequest;
import org.cloudfoundry.client.v3.packages.StagePackageResponse;
import org.cloudfoundry.client.v3.packages.UploadPackageRequest;
import org.cloudfoundry.client.v3.packages.UploadPackageResponse;
import org.reactivestreams.Publisher;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.fn.Consumer;
import reactor.fn.Supplier;

import java.net.URI;

/**
 * The Spring-based implementation of {@link Packages}
 */
@ToString(callSuper = true)
public final class SpringPackages extends AbstractSpringOperations implements Packages {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     */
    public SpringPackages(RestOperations restOperations, URI root) {
        super(restOperations, root);
    }

    @Override
    public Publisher<CopyPackageResponse> copy(final CopyPackageRequest request) {
        return post(request, CopyPackageResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v3", "apps", request.getApplicationId(), "packages");
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Publisher<CreatePackageResponse> create(final CreatePackageRequest request) {
        return post(request, CreatePackageResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v3", "apps", request.getApplicationId(), "packages");
            }

        });
    }

    @Override
    public Publisher<Void> delete(final DeletePackageRequest request) {
        return delete(request, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v3", "packages", request.getId());
            }

        });
    }

    @Override
    public Publisher<GetPackageResponse> get(final GetPackageRequest request) {
        return get(request, GetPackageResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v3", "packages", request.getId());
            }

        });
    }

    @Override
    public Publisher<ListPackagesResponse> list(final ListPackagesRequest request) {
        return get(request, ListPackagesResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v3", "packages");
            }

        });
    }

    @Override
    public Publisher<StagePackageResponse> stage(final StagePackageRequest request) {
        return post(request, StagePackageResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v3", "packages", request.getId(), "droplets");
            }

        });
    }

    @Override
    public Publisher<UploadPackageResponse> upload(final UploadPackageRequest request) {
        return postWithBody(request, new Supplier<MultiValueMap<String, FileSystemResource>>() {

            @Override
            public MultiValueMap<String, FileSystemResource> get() {
                return CollectionUtils.singletonMultiValueMap("bits", new FileSystemResource(request.getFile
                        ()));
            }

        }, UploadPackageResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v3", "packages", request.getId(), "upload");
            }

        });
    }

}
