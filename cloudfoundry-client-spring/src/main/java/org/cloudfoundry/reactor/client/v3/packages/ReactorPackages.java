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

package org.cloudfoundry.reactor.client.v3.packages;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.cloudfoundry.reactor.client.v3.AbstractClientV3Operations;
import org.cloudfoundry.reactor.util.AuthorizationProvider;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.io.netty.http.HttpClient;
import reactor.io.netty.http.HttpInbound;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

/**
 * The Reactor-based implementation of {@link Packages}
 */
public final class ReactorPackages extends AbstractClientV3Operations implements Packages {

    /**
     * Creates an instance
     *
     * @param authorizationProvider the {@link AuthorizationProvider} to use when communicating with the server
     * @param httpClient            the {@link HttpClient} to use when communicating with the server
     * @param objectMapper          the {@link ObjectMapper} to use when communicating with the server
     * @param root                  the root URI of the server.  Typically something like {@code https://uaa.run.pivotal.io}.
     */
    public ReactorPackages(AuthorizationProvider authorizationProvider, HttpClient httpClient, ObjectMapper objectMapper, Mono<String> root) {
        super(authorizationProvider, httpClient, objectMapper, root);
    }

    @Override
    public Mono<CopyPackageResponse> copy(CopyPackageRequest request) {
        return post(request, CopyPackageResponse.class, function((builder, validRequest) -> builder.pathSegment("v3", "apps", validRequest.getApplicationId(), "packages")));
    }

    @Override
    public Mono<CreatePackageResponse> create(CreatePackageRequest request) {
        return post(request, CreatePackageResponse.class, function((builder, validRequest) -> builder.pathSegment("v3", "apps", validRequest.getApplicationId(), "packages")));
    }

    @Override
    public Mono<Void> delete(DeletePackageRequest request) {
        return delete(request, Void.class, function((builder, validRequest) -> builder.pathSegment("v3", "packages", validRequest.getPackageId())));
    }

    @Override
    public Flux<byte[]> download(DownloadPackageRequest request) {
        return get(request, function((builder, validRequest) -> builder.pathSegment("v3", "packages", validRequest.getPackageId(), "download")))
            .flatMap(HttpInbound::receiveByteArray);
    }

    @Override
    public Mono<GetPackageResponse> get(GetPackageRequest request) {
        return get(request, GetPackageResponse.class, function((builder, validRequest) -> builder.pathSegment("v3", "packages", validRequest.getPackageId())));
    }

    @Override
    public Mono<ListPackagesResponse> list(ListPackagesRequest request) {
        return get(request, ListPackagesResponse.class, function((builder, validRequest) -> builder.pathSegment("v3", "packages")));
    }

    @Override
    public Mono<StagePackageResponse> stage(StagePackageRequest request) {
        return post(request, StagePackageResponse.class, function((builder, validRequest) -> builder.pathSegment("v3", "packages", validRequest.getPackageId(), "droplets")));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Mono<UploadPackageResponse> upload(UploadPackageRequest request) {
        return post(request, UploadPackageResponse.class, function((builder, validRequest) -> builder.pathSegment("v3", "packages", validRequest.getPackageId(), "upload")),
            function((outbound, validRequest) -> outbound
                .addPart(part -> part.setContentDispositionFormData("bits", "application.zip")
                    .addHeader(CONTENT_TYPE, APPLICATION_ZIP)
                    .sendInputStream(validRequest.getBits()))
                .done()));
    }

}
