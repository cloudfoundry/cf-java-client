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

package org.cloudfoundry.spring.client.v2.buildpacks;

import lombok.ToString;
import org.cloudfoundry.client.v2.buildpacks.Buildpacks;
import org.cloudfoundry.client.v2.buildpacks.CreateBuildpackRequest;
import org.cloudfoundry.client.v2.buildpacks.CreateBuildpackResponse;
import org.cloudfoundry.client.v2.buildpacks.DeleteBuildpackRequest;
import org.cloudfoundry.client.v2.buildpacks.DeleteBuildpackResponse;
import org.cloudfoundry.client.v2.buildpacks.GetBuildpackRequest;
import org.cloudfoundry.client.v2.buildpacks.GetBuildpackResponse;
import org.cloudfoundry.client.v2.buildpacks.ListBuildpacksRequest;
import org.cloudfoundry.client.v2.buildpacks.ListBuildpacksResponse;
import org.cloudfoundry.client.v2.buildpacks.UpdateBuildpackRequest;
import org.cloudfoundry.client.v2.buildpacks.UpdateBuildpackResponse;
import org.cloudfoundry.client.v2.buildpacks.UploadBuildpackRequest;
import org.cloudfoundry.client.v2.buildpacks.UploadBuildpackResponse;
import org.cloudfoundry.spring.client.v2.FilterBuilder;
import org.cloudfoundry.spring.util.AbstractSpringOperations;
import org.cloudfoundry.spring.util.CollectionUtils;
import org.cloudfoundry.spring.util.QueryBuilder;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestOperations;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.net.URI;

/**
 * The Spring-based implementation of {@link Buildpacks}
 */
@ToString(callSuper = true)
public final class SpringBuildpacks extends AbstractSpringOperations implements Buildpacks {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations } to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     * @param schedulerGroup The group to use when making requests
     */
    public SpringBuildpacks(RestOperations restOperations, URI root, Scheduler schedulerGroup) {
        super(restOperations, root, schedulerGroup);
    }

    @Override
    public Mono<CreateBuildpackResponse> create(CreateBuildpackRequest request) {
        return post(request, CreateBuildpackResponse.class, builder -> builder.pathSegment("v2", "buildpacks"));
    }

    @Override
    public Mono<DeleteBuildpackResponse> delete(DeleteBuildpackRequest request) {
        return delete(request, DeleteBuildpackResponse.class, builder -> {
            builder.pathSegment("v2", "buildpacks", request.getBuildpackId());
            QueryBuilder.augment(builder, request);
        });
    }

    @Override
    public Mono<GetBuildpackResponse> get(GetBuildpackRequest request) {
        return get(request, GetBuildpackResponse.class, builder -> builder.pathSegment("v2", "buildpacks", request.getBuildpackId()));
    }

    @Override
    public Mono<ListBuildpacksResponse> list(ListBuildpacksRequest request) {
        return get(request, ListBuildpacksResponse.class, builder -> {
            builder.pathSegment("v2", "buildpacks");
            FilterBuilder.augment(builder, request);
            QueryBuilder.augment(builder, request);
        });
    }

    @Override
    public Mono<UpdateBuildpackResponse> update(UpdateBuildpackRequest request) {
        return put(request, UpdateBuildpackResponse.class, builder -> builder.pathSegment("v2", "buildpacks", request.getBuildpackId()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Mono<UploadBuildpackResponse> upload(UploadBuildpackRequest request) {
        return putWithBody(request, () -> CollectionUtils.singletonMultiValueMap("buildpack", getBuildpackPart(request)), UploadBuildpackResponse.class,
            builder -> builder.pathSegment("v2", "buildpacks", request.getBuildpackId(), "bits"));
    }

    private static HttpEntity<Resource> getBuildpackPart(UploadBuildpackRequest request) {
        Resource body = new InputStreamResource(request.getBuildpack());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("buildpack", request.getFilename());
        headers.setContentType(MediaType.parseMediaType("application/zip"));

        return new HttpEntity<>(body, headers);
    }

}
