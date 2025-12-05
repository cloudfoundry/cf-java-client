/*
 * Copyright 2013-2021 the original author or authors.
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

package org.cloudfoundry.operations.buildpacks;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Optional;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.buildpacks.BuildpackEntity;
import org.cloudfoundry.client.v2.buildpacks.BuildpackResource;
import org.cloudfoundry.client.v2.buildpacks.CreateBuildpackResponse;
import org.cloudfoundry.client.v2.buildpacks.DeleteBuildpackResponse;
import org.cloudfoundry.client.v2.buildpacks.ListBuildpacksRequest;
import org.cloudfoundry.client.v2.buildpacks.UpdateBuildpackResponse;
import org.cloudfoundry.client.v2.buildpacks.UploadBuildpackRequest;
import org.cloudfoundry.client.v2.buildpacks.UploadBuildpackResponse;
import org.cloudfoundry.operations.util.OperationsLogging;
import org.cloudfoundry.util.ExceptionUtils;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public final class DefaultBuildpacks implements Buildpacks {

    private final CloudFoundryClient cloudFoundryClient;

    public DefaultBuildpacks(CloudFoundryClient cloudFoundryClient) {
        this.cloudFoundryClient = cloudFoundryClient;
    }

    /**
     * Create a new instance.
     *
     * @deprecated Use {@link DefaultBuildpacks(CloudFoundryClient)} instead.
     */
    @Deprecated
    public DefaultBuildpacks(Mono<CloudFoundryClient> cloudFoundryClient) {
        this.cloudFoundryClient =
                cloudFoundryClient.subscribeOn(Schedulers.boundedElastic()).block();
    }

    @Override
    public Mono<Void> create(CreateBuildpackRequest request) {
        return requestCreateBuildpack(
                        this.cloudFoundryClient,
                        request.getName(),
                        request.getPosition(),
                        request.getEnable())
                .flatMap(
                        response ->
                                requestUploadBuildpackBits(
                                        ResourceUtils.getId(response), request.getBuildpack()))
                .then()
                .transform(OperationsLogging.log("Create Buildpack"))
                .checkpoint();
    }

    @Override
    public Mono<Void> delete(DeleteBuildpackRequest request) {
        return getBuildPackId(request.getName())
                .flatMap(
                        buildpackId -> deleteBuildpack(buildpackId, request.getCompletionTimeout()))
                .then()
                .transform(OperationsLogging.log("Delete Buildpack"))
                .checkpoint();
    }

    @Override
    public Flux<Buildpack> list() {
        return requestBuildpacks(this.cloudFoundryClient)
                .map(this::toBuildpackResource)
                .transform(OperationsLogging.log("List Buildpacks"))
                .checkpoint();
    }

    @Override
    public Mono<Void> rename(RenameBuildpackRequest request) {
        return getBuildPackId(request.getName())
                .flatMap(buildpackId -> requestUpdateBuildpack(buildpackId, request.getNewName()))
                .then()
                .transform(OperationsLogging.log("Rename Buildpack"))
                .checkpoint();
    }

    @Override
    public Mono<Void> update(UpdateBuildpackRequest request) {
        return getBuildPackId(request.getName())
                .flatMap(
                        buildpackId ->
                                Mono.when(
                                        requestUpdateBuildpack(buildpackId, request),
                                        uploadBuildpackBits(buildpackId, request)))
                .then()
                .transform(OperationsLogging.log("Update Buildpack"))
                .checkpoint();
    }

    private Mono<Void> deleteBuildpack(String buildpackId, Duration timeout) {
        return requestDeleteBuildpack(buildpackId)
                .flatMap(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, timeout, job));
    }

    private Mono<String> getBuildPackId(String name) {
        return requestBuildpacks(name)
                .singleOrEmpty()
                .map(ResourceUtils::getId)
                .switchIfEmpty(ExceptionUtils.illegalArgument("Buildpack %s not found", name));
    }

    private Flux<BuildpackResource> requestBuildpacks(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils.requestClientV2Resources(
                page ->
                        cloudFoundryClient
                                .buildpacks()
                                .list(ListBuildpacksRequest.builder().page(page).build()));
    }

    private Flux<BuildpackResource> requestBuildpacks(String name) {
        return PaginationUtils.requestClientV2Resources(
                page ->
                        cloudFoundryClient
                                .buildpacks()
                                .list(
                                        ListBuildpacksRequest.builder()
                                                .name(name)
                                                .page(page)
                                                .build()));
    }

    private Mono<CreateBuildpackResponse> requestCreateBuildpack(
            CloudFoundryClient cloudFoundryClient,
            String buildpackName,
            Integer position,
            Boolean enable) {
        return cloudFoundryClient
                .buildpacks()
                .create(
                        org.cloudfoundry.client.v2.buildpacks.CreateBuildpackRequest.builder()
                                .name(buildpackName)
                                .position(position)
                                .enabled(Optional.ofNullable(enable).orElse(true))
                                .build());
    }

    private Mono<DeleteBuildpackResponse> requestDeleteBuildpack(String buildpackId) {
        return cloudFoundryClient
                .buildpacks()
                .delete(
                        org.cloudfoundry.client.v2.buildpacks.DeleteBuildpackRequest.builder()
                                .async(true)
                                .buildpackId(buildpackId)
                                .build());
    }

    private Mono<UpdateBuildpackResponse> requestUpdateBuildpack(
            String buildpackId, UpdateBuildpackRequest request) {
        return cloudFoundryClient
                .buildpacks()
                .update(
                        org.cloudfoundry.client.v2.buildpacks.UpdateBuildpackRequest.builder()
                                .buildpackId(buildpackId)
                                .enabled(request.getEnable())
                                .locked(request.getLock())
                                .position(request.getPosition())
                                .build());
    }

    private Mono<UpdateBuildpackResponse> requestUpdateBuildpack(String buildpackId, String name) {
        return cloudFoundryClient
                .buildpacks()
                .update(
                        org.cloudfoundry.client.v2.buildpacks.UpdateBuildpackRequest.builder()
                                .buildpackId(buildpackId)
                                .name(name)
                                .build());
    }

    private Mono<UploadBuildpackResponse> requestUploadBuildpackBits(
            String buildpackId, Path buildpack) {
        return cloudFoundryClient
                .buildpacks()
                .upload(
                        UploadBuildpackRequest.builder()
                                .buildpackId(buildpackId)
                                .filename(buildpack.getFileName().toString())
                                .buildpack(buildpack)
                                .build());
    }

    private Buildpack toBuildpackResource(BuildpackResource resource) {
        BuildpackEntity entity = ResourceUtils.getEntity(resource);

        return Buildpack.builder()
                .enabled(entity.getEnabled())
                .filename(entity.getFilename())
                .id(ResourceUtils.getId(resource))
                .locked(entity.getLocked())
                .name(entity.getName())
                .position(entity.getPosition())
                .stack(entity.getStack())
                .build();
    }

    private Mono<Void> uploadBuildpackBits(String buildpackId, UpdateBuildpackRequest request) {
        if (request.getBuildpack() != null) {
            return requestUploadBuildpackBits(buildpackId, request.getBuildpack())
                    .then(Mono.empty());
        }

        return Mono.empty();
    }
}
