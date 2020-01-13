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

package org.cloudfoundry.reactor.client.v3.packages;

import org.cloudfoundry.client.v3.packages.CopyPackageRequest;
import org.cloudfoundry.client.v3.packages.CopyPackageResponse;
import org.cloudfoundry.client.v3.packages.CreatePackageRequest;
import org.cloudfoundry.client.v3.packages.CreatePackageResponse;
import org.cloudfoundry.client.v3.packages.DeletePackageRequest;
import org.cloudfoundry.client.v3.packages.DownloadPackageRequest;
import org.cloudfoundry.client.v3.packages.GetPackageRequest;
import org.cloudfoundry.client.v3.packages.GetPackageResponse;
import org.cloudfoundry.client.v3.packages.ListPackageDropletsRequest;
import org.cloudfoundry.client.v3.packages.ListPackageDropletsResponse;
import org.cloudfoundry.client.v3.packages.ListPackagesRequest;
import org.cloudfoundry.client.v3.packages.ListPackagesResponse;
import org.cloudfoundry.client.v3.packages.Packages;
import org.cloudfoundry.client.v3.packages.UploadPackageRequest;
import org.cloudfoundry.client.v3.packages.UploadPackageResponse;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v3.AbstractClientV3Operations;
import org.cloudfoundry.reactor.util.MultipartHttpClientRequest;
import org.cloudfoundry.util.FileUtils;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * The Reactor-based implementation of {@link Packages}
 */
public final class ReactorPackages extends AbstractClientV3Operations implements Packages {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorPackages(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<CopyPackageResponse> copy(CopyPackageRequest request) {
        return post(request, CopyPackageResponse.class, builder -> builder.pathSegment("packages"))
            .checkpoint();
    }

    @Override
    public Mono<CreatePackageResponse> create(CreatePackageRequest request) {
        return post(request, CreatePackageResponse.class, builder -> builder.pathSegment("packages"))
            .checkpoint();
    }

    @Override
    public Mono<String> delete(DeletePackageRequest request) {
        return delete(request, builder -> builder.pathSegment("packages", request.getPackageId()))
            .checkpoint();
    }

    @Override
    public Flux<byte[]> download(DownloadPackageRequest request) {
        return get(request, builder -> builder.pathSegment("packages", request.getPackageId(), "download"), ByteBufFlux::asByteArray)
            .checkpoint();
    }

    @Override
    public Mono<GetPackageResponse> get(GetPackageRequest request) {
        return get(request, GetPackageResponse.class, builder -> builder.pathSegment("packages", request.getPackageId()))
            .checkpoint();
    }

    @Override
    public Mono<ListPackagesResponse> list(ListPackagesRequest request) {
        return get(request, ListPackagesResponse.class, builder -> builder.pathSegment("packages"))
            .checkpoint();
    }

    @Override
    public Mono<ListPackageDropletsResponse> listDroplets(ListPackageDropletsRequest request) {
        return get(request, ListPackageDropletsResponse.class, builder -> builder.pathSegment("packages", request.getPackageId(), "droplets"))
            .checkpoint();
    }

    @Override
    public Mono<UploadPackageResponse> upload(UploadPackageRequest request) {
        Path bits = request.getBits();

        if (bits.toFile().isDirectory()) {
            return FileUtils.compress(bits)
                .map(temporaryFile -> UploadPackageRequest.builder()
                    .from(request)
                    .bits(temporaryFile)
                    .build())
                .flatMap(requestWithTemporaryFile -> upload(requestWithTemporaryFile, () -> {
                    try {
                        Files.delete(requestWithTemporaryFile.getBits());
                    } catch (IOException e) {
                        throw Exceptions.propagate(e);
                    }
                }));
        } else {
            return upload(request, () -> {
            });
        }
    }

    private Mono<UploadPackageResponse> upload(UploadPackageRequest request, Runnable onTerminate) {
        return post(request, UploadPackageResponse.class, builder -> builder.pathSegment("packages", request.getPackageId(), "upload"), outbound -> upload(request.getBits(), outbound), onTerminate)
            .checkpoint();
    }

    private void upload(Path bits, MultipartHttpClientRequest r) {
        r.addPart(part -> part.setName("bits")
            .setContentType(APPLICATION_ZIP)
            .sendFile(bits))
            .done();
    }

}
