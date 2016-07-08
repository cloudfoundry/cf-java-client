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

package org.cloudfoundry.reactor.client.v2.buildpacks;

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
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v2.AbstractClientV2Operations;
import reactor.core.publisher.Mono;

/**
 * The Reactor-based implementation of {@link Buildpacks}
 */
public final class ReactorBuildpacks extends AbstractClientV2Operations implements Buildpacks {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     */
    public ReactorBuildpacks(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider) {
        super(connectionContext, root, tokenProvider);
    }

    @Override
    public Mono<CreateBuildpackResponse> create(CreateBuildpackRequest request) {
        return post(request, CreateBuildpackResponse.class, builder -> builder.pathSegment("v2", "buildpacks"));
    }

    @Override
    public Mono<DeleteBuildpackResponse> delete(DeleteBuildpackRequest request) {
        return delete(request, DeleteBuildpackResponse.class, builder -> builder.pathSegment("v2", "buildpacks", request.getBuildpackId()));
    }

    @Override
    public Mono<GetBuildpackResponse> get(GetBuildpackRequest request) {
        return get(request, GetBuildpackResponse.class, builder -> builder.pathSegment("v2", "buildpacks", request.getBuildpackId()));
    }

    @Override
    public Mono<ListBuildpacksResponse> list(ListBuildpacksRequest request) {
        return get(request, ListBuildpacksResponse.class, builder -> builder.pathSegment("v2", "buildpacks"));
    }

    @Override
    public Mono<UpdateBuildpackResponse> update(UpdateBuildpackRequest request) {
        return put(request, UpdateBuildpackResponse.class, builder -> builder.pathSegment("v2", "buildpacks", request.getBuildpackId()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Mono<UploadBuildpackResponse> upload(UploadBuildpackRequest request) {
        return put(request, UploadBuildpackResponse.class, builder -> builder.pathSegment("v2", "buildpacks", request.getBuildpackId(), "bits"),
            outbound -> outbound
                .addPart(part -> part.setContentDispositionFormData("buildpack", request.getFilename())
                    .addHeader(CONTENT_TYPE, APPLICATION_ZIP)
                    .sendInputStream(request.getBuildpack()))
                .done());
    }

}
