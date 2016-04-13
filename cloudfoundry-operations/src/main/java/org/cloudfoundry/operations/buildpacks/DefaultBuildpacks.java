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

package org.cloudfoundry.operations.buildpacks;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.buildpacks.BuildpackEntity;
import org.cloudfoundry.client.v2.buildpacks.BuildpackResource;
import org.cloudfoundry.client.v2.buildpacks.CreateBuildpackRequest;
import org.cloudfoundry.client.v2.buildpacks.CreateBuildpackResponse;
import org.cloudfoundry.client.v2.buildpacks.ListBuildpacksRequest;
import org.cloudfoundry.util.ExceptionUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.cloudfoundry.util.ValidationUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class DefaultBuildpacks implements Buildpacks {

    private final CloudFoundryClient cloudFoundryClient;

    public DefaultBuildpacks(CloudFoundryClient cloudFoundryClient) {
        this.cloudFoundryClient = cloudFoundryClient;
    }

    @Override
    public Mono<Void> create(CreateBuildpackRequest request) {
        return ValidationUtils
            .validate(request)
            .then(request1 -> validateAdditionalArguments(request1.getFilename(), request1.getPosition()))
            .then(message -> ExceptionUtils.illegalArgument(message))
            .otherwiseIfEmpty(requestCreateBuildpack(this.cloudFoundryClient, request))
            .after();
    }

    @Override
    public Flux<Buildpack> list() {
        return requestBuildpacks(this.cloudFoundryClient)
            .map(DefaultBuildpacks::toBuildpackResource);
    }

    private static Mono<CreateBuildpackResponse> requestCreateBuildpack(CloudFoundryClient cloudFoundryClient, CreateBuildpackRequest request){
        return cloudFoundryClient.buildpacks()
            .create(request);
    }

    private static Flux<BuildpackResource> requestBuildpacks(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.buildpacks()
                .list(ListBuildpacksRequest.builder()
                    .page(page)
                    .build()));
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

    private static Mono<String> validateAdditionalArguments(String filename, Integer position){
        return filename == null ?
            (position == null ?
                Mono.just("Filename must be specified and position must be specified") :
                Mono.just("Filename must be specified"))
            : (position == null ?
                Mono.just("Position must be specified") :
                Mono.empty());
    }

}
