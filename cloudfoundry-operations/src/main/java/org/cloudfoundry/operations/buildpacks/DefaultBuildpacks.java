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

package org.cloudfoundry.operations.buildpacks;

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

import java.nio.file.Path;
import java.time.Duration;
import java.util.Optional;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class DefaultBuildpacks implements Buildpacks {

    private final Mono<CloudFoundryClient> cloudFoundryClient;

    public DefaultBuildpacks(Mono<CloudFoundryClient> cloudFoundryClient) {
        this.cloudFoundryClient = cloudFoundryClient;
    }

    @Override
    public Mono<Void> create(CreateBuildpackRequest request) {
        return this.cloudFoundryClient
            .flatMap(cloudFoundryClient -> Mono.when(
                Mono.just(cloudFoundryClient),
                requestCreateBuildpack(cloudFoundryClient, request.getName(), request.getPosition(), request.getEnable())
            ))
            .flatMap(function((cloudFoundryClient, response) -> requestUploadBuildpackBits(cloudFoundryClient, ResourceUtils.getId(response), request.getBuildpack())))
            .then()
            .transform(OperationsLogging.log("Create Buildpack"))
            .checkpoint();
    }

    @Override
    public Mono<Void> delete(DeleteBuildpackRequest request) {
        return this.cloudFoundryClient
            .flatMap(cloudFoundryClient -> Mono.when(
                getBuildPackId(cloudFoundryClient, request.getName()),
                Mono.just(cloudFoundryClient)
            ))
            .flatMap(function((buildpackId, cloudFoundryClient) -> deleteBuildpack(cloudFoundryClient, buildpackId, request.getCompletionTimeout())))
            .then()
            .transform(OperationsLogging.log("Delete Buildpack"))
            .checkpoint();
    }

    @Override
    public Flux<Buildpack> list() {
        return this.cloudFoundryClient
            .flatMapMany(DefaultBuildpacks::requestBuildpacks)
            .map(DefaultBuildpacks::toBuildpackResource)
            .transform(OperationsLogging.log("List Buildpacks"))
            .checkpoint();
    }

    @Override
    public Mono<Void> rename(RenameBuildpackRequest request) {
        return this.cloudFoundryClient
            .flatMap(cloudFoundryClient -> Mono.when(
                getBuildPackId(cloudFoundryClient, request.getName()),
                Mono.just(cloudFoundryClient)
            ))
            .flatMap(function((buildpackId, cloudFoundryClient) -> requestUpdateBuildpack(cloudFoundryClient, buildpackId, request.getNewName())))
            .then()
            .transform(OperationsLogging.log("Rename Buildpack"))
            .checkpoint();
    }

    @Override
    public Mono<Void> update(UpdateBuildpackRequest request) {
        return this.cloudFoundryClient
            .flatMap(cloudFoundryClient -> Mono.when(
                getBuildPackId(cloudFoundryClient, request.getName()),
                Mono.just(cloudFoundryClient)
            ))
            .flatMap(function((buildpackId, cloudFoundryClient) -> Mono.when(
                requestUpdateBuildpack(cloudFoundryClient, buildpackId, request),
                uploadBuildpackBits(cloudFoundryClient, buildpackId, request)
            )))
            .then()
            .transform(OperationsLogging.log("Update Buildpack"))
            .checkpoint();
    }

    private static Mono<Void> deleteBuildpack(CloudFoundryClient cloudFoundryClient, String buildpackId, Duration timeout) {
        return requestDeleteBuildpack(cloudFoundryClient, buildpackId)
            .flatMap(job -> JobUtils.waitForCompletion(cloudFoundryClient, timeout, job));
    }

    private static Mono<String> getBuildPackId(CloudFoundryClient cloudFoundryClient, String name) {
        return requestBuildpacks(cloudFoundryClient, name)
            .singleOrEmpty()
            .map(ResourceUtils::getId)
            .switchIfEmpty(ExceptionUtils.illegalArgument("Buildpack %s not found", name));
    }

    private static Flux<BuildpackResource> requestBuildpacks(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.buildpacks()
                .list(ListBuildpacksRequest.builder()
                    .page(page)
                    .build()));
    }

    private static Flux<BuildpackResource> requestBuildpacks(CloudFoundryClient cloudFoundryClient, String name) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.buildpacks()
                .list(ListBuildpacksRequest.builder()
                    .name(name)
                    .page(page)
                    .build()));
    }

    private static Mono<CreateBuildpackResponse> requestCreateBuildpack(CloudFoundryClient cloudFoundryClient, String buildpackName, Integer position, Boolean enable) {
        return cloudFoundryClient.buildpacks()
            .create(org.cloudfoundry.client.v2.buildpacks.CreateBuildpackRequest
                .builder()
                .name(buildpackName)
                .position(position)
                .enabled(Optional.ofNullable(enable).orElse(true))
                .build());
    }

    private static Mono<DeleteBuildpackResponse> requestDeleteBuildpack(CloudFoundryClient cloudFoundryClient, String buildpackId) {
        return cloudFoundryClient.buildpacks()
            .delete(org.cloudfoundry.client.v2.buildpacks.DeleteBuildpackRequest.builder()
                .async(true)
                .buildpackId(buildpackId)
                .build());
    }

    private static Mono<UpdateBuildpackResponse> requestUpdateBuildpack(CloudFoundryClient cloudFoundryClient, String buildpackId, UpdateBuildpackRequest request) {
        return cloudFoundryClient.buildpacks()
            .update(org.cloudfoundry.client.v2.buildpacks.UpdateBuildpackRequest.builder()
                .buildpackId(buildpackId)
                .enabled(request.getEnable())
                .locked(request.getLock())
                .position(request.getPosition())
                .build());
    }

    private static Mono<UpdateBuildpackResponse> requestUpdateBuildpack(CloudFoundryClient cloudFoundryClient, String buildpackId, String name) {
        return cloudFoundryClient.buildpacks()
            .update(org.cloudfoundry.client.v2.buildpacks.UpdateBuildpackRequest.builder()
                .buildpackId(buildpackId)
                .name(name)
                .build());
    }

    private static Mono<UploadBuildpackResponse> requestUploadBuildpackBits(CloudFoundryClient cloudFoundryClient, String buildpackId, Path buildpack) {
        return cloudFoundryClient.buildpacks()
            .upload(UploadBuildpackRequest.builder()
                .buildpackId(buildpackId)
                .filename(buildpack.getFileName().toString())
                .buildpack(buildpack)
                .build());
    }

    private static Buildpack toBuildpackResource(BuildpackResource resource) {
        BuildpackEntity entity = ResourceUtils.getEntity(resource);

        return Buildpack.builder()
            .enabled(entity.getEnabled())
            .filename(entity.getFilename())
            .id(ResourceUtils.getId(resource))
            .locked(entity.getLocked())
            .name(entity.getName())
            .position(entity.getPosition())
            .build();
    }

    private static Mono<UploadBuildpackResponse> uploadBuildpackBits(CloudFoundryClient cloudFoundryClient, String buildpackId, UpdateBuildpackRequest request) {
        if (request.getBuildpack() != null) {
            requestUploadBuildpackBits(cloudFoundryClient, buildpackId, request.getBuildpack());
        }

        return Mono.empty();
    }

}
