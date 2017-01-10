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

package org.cloudfoundry.reactor.client.v3.packages;

import org.cloudfoundry.client.v3.packages.CopyPackageRequest;
import org.cloudfoundry.client.v3.packages.CopyPackageResponse;
import org.cloudfoundry.client.v3.packages.CreatePackageRequest;
import org.cloudfoundry.client.v3.packages.CreatePackageResponse;
import org.cloudfoundry.client.v3.packages.DeletePackageRequest;
import org.cloudfoundry.client.v3.packages.DownloadPackageRequest;
import org.cloudfoundry.client.v3.packages.GetPackageRequest;
import org.cloudfoundry.client.v3.packages.GetPackageResponse;
import org.cloudfoundry.client.v3.packages.ListPackagesRequest;
import org.cloudfoundry.client.v3.packages.ListPackagesResponse;
import org.cloudfoundry.client.v3.packages.Packages;
import org.cloudfoundry.client.v3.packages.StagePackageRequest;
import org.cloudfoundry.client.v3.packages.StagePackageResponse;
import org.cloudfoundry.client.v3.packages.UploadPackageRequest;
import org.cloudfoundry.client.v3.packages.UploadPackageResponse;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v3.AbstractClientV3Operations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The Reactor-based implementation of {@link Packages}
 */
public final class ReactorPackages extends AbstractClientV3Operations implements Packages {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     */
    public ReactorPackages(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider) {
        super(connectionContext, root, tokenProvider);
    }

    @Override
    public Mono<CopyPackageResponse> copy(CopyPackageRequest request) {
        return post(request, CopyPackageResponse.class, builder -> builder.pathSegment("v3", "apps", request.getApplicationId(), "packages"));
    }

    @Override
    public Mono<CreatePackageResponse> create(CreatePackageRequest request) {
        return post(request, CreatePackageResponse.class, builder -> builder.pathSegment("v3", "apps", request.getApplicationId(), "packages"));
    }

    @Override
    public Mono<Void> delete(DeletePackageRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("v3", "packages", request.getPackageId()));
    }

    @Override
    public Flux<byte[]> download(DownloadPackageRequest request) {
        return get(request, builder -> builder.pathSegment("v3", "packages", request.getPackageId(), "download"))
            .flatMap(response -> response.receive().aggregate().asByteArray());
    }

    @Override
    public Mono<GetPackageResponse> get(GetPackageRequest request) {
        return get(request, GetPackageResponse.class, builder -> builder.pathSegment("v3", "packages", request.getPackageId()));
    }

    @Override
    public Mono<ListPackagesResponse> list(ListPackagesRequest request) {
        return get(request, ListPackagesResponse.class, builder -> builder.pathSegment("v3", "packages"));
    }

    @Override
    public Mono<StagePackageResponse> stage(StagePackageRequest request) {
        return post(request, StagePackageResponse.class, builder -> builder.pathSegment("v3", "packages", request.getPackageId(), "droplets"));
    }

    @Override
    public Mono<UploadPackageResponse> upload(UploadPackageRequest request) {
        return post(request, UploadPackageResponse.class, builder -> builder.pathSegment("v3", "packages", request.getPackageId(), "upload"),
            outbound -> outbound
                .flatMap(r -> r
                    .chunkedTransfer(false)
                    .sendForm(form -> form
                        .multipart(true)
                        .file("bits", "application.zip", request.getBits().toFile(), APPLICATION_ZIP)))
                .then());
    }

}
