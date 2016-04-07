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
import org.cloudfoundry.client.v2.buildpacks.BuildpackResource;
import org.cloudfoundry.client.v2.buildpacks.ListBuildpacksRequest;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import reactor.core.publisher.Flux;

public final class DefaultBuildpacks implements Buildpacks {

    private final CloudFoundryClient cloudFoundryClient;

    public DefaultBuildpacks(CloudFoundryClient cloudFoundryClient) {
        this.cloudFoundryClient = cloudFoundryClient;
    }

    @Override
    public Flux<BuildpackSummary> list() {
        return requestBuildpacks(this.cloudFoundryClient)
            .map(DefaultBuildpacks::toBuildpackResource);
    }

    private static Flux<BuildpackResource> requestBuildpacks(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.buildpacks()
                .list(ListBuildpacksRequest.builder()
                    .page(page)
                    .build()));
    }

    private static BuildpackSummary toBuildpackResource(BuildpackResource resource) {
        return BuildpackSummary.builder()
            .enabled(ResourceUtils.getEntity(resource).getEnabled())
            .filename(ResourceUtils.getEntity(resource).getFilename())
            .id(ResourceUtils.getId(resource))
            .locked(ResourceUtils.getEntity(resource).getLocked())
            .name(ResourceUtils.getEntity(resource).getName())
            .position(ResourceUtils.getEntity(resource).getPosition())
            .build();
    }

}
