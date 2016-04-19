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

package org.cloudfoundry.spring.client.v3.packages;

import lombok.ToString;
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
import org.cloudfoundry.spring.util.AbstractSpringOperations;
import org.cloudfoundry.spring.util.CollectionUtils;
import org.cloudfoundry.spring.util.QueryBuilder;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

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
     * @param schedulerGroup The group to use when making requests
     */
    public SpringPackages(RestOperations restOperations, URI root, Scheduler schedulerGroup) {
        super(restOperations, root, schedulerGroup);
    }

    @Override
    public Mono<CopyPackageResponse> copy(CopyPackageRequest request) {
        return post(request, CopyPackageResponse.class, builder -> {
            builder.pathSegment("v3", "apps", request.getApplicationId(), "packages");
            QueryBuilder.augment(builder, request);
        });
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
        return getStream(request, builder -> builder.pathSegment("v3", "packages", request.getPackageId(), "download"));
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

    @SuppressWarnings("unchecked")
    @Override
    public Mono<UploadPackageResponse> upload(UploadPackageRequest request) {
        return postWithBody(request, () -> CollectionUtils.singletonMultiValueMap("bits", getApplicationPart(request)), UploadPackageResponse.class,
            builder -> builder.pathSegment("v3", "packages", request.getPackageId(), "upload"));
    }

    private static HttpEntity<Resource> getApplicationPart(UploadPackageRequest request) {
        Resource body = new InputStreamResource(request.getBits());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("bits", "application.zip");
        headers.setContentType(MediaType.parseMediaType("application/zip"));
        headers.setContentType(MediaType.parseMediaType("application/zip"));

        return new HttpEntity<>(body, headers);
    }

}
