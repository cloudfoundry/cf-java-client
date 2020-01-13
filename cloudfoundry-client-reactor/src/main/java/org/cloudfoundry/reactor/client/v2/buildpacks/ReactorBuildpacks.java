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
import org.cloudfoundry.reactor.util.MultipartHttpClientRequest;
import org.cloudfoundry.util.FileUtils;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * The Reactor-based implementation of {@link Buildpacks}
 */
public final class ReactorBuildpacks extends AbstractClientV2Operations implements Buildpacks {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorBuildpacks(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<CreateBuildpackResponse> create(CreateBuildpackRequest request) {
        return post(request, CreateBuildpackResponse.class, builder -> builder.pathSegment("buildpacks"))
            .checkpoint();
    }

    @Override
    public Mono<DeleteBuildpackResponse> delete(DeleteBuildpackRequest request) {
        return delete(request, DeleteBuildpackResponse.class, builder -> builder.pathSegment("buildpacks", request.getBuildpackId()))
            .checkpoint();
    }

    @Override
    public Mono<GetBuildpackResponse> get(GetBuildpackRequest request) {
        return get(request, GetBuildpackResponse.class, builder -> builder.pathSegment("buildpacks", request.getBuildpackId()))
            .checkpoint();
    }

    @Override
    public Mono<ListBuildpacksResponse> list(ListBuildpacksRequest request) {
        return get(request, ListBuildpacksResponse.class, builder -> builder.pathSegment("buildpacks"))
            .checkpoint();
    }

    @Override
    public Mono<UpdateBuildpackResponse> update(UpdateBuildpackRequest request) {
        return put(request, UpdateBuildpackResponse.class, builder -> builder.pathSegment("buildpacks", request.getBuildpackId()))
            .checkpoint();
    }

    @Override
    public Mono<UploadBuildpackResponse> upload(UploadBuildpackRequest request) {
        Path buildpack = request.getBuildpack();

        if (buildpack.toFile().isDirectory()) {
            return FileUtils.compress(buildpack)
                .map(temporaryFile -> UploadBuildpackRequest.builder()
                    .from(request)
                    .buildpack(temporaryFile)
                    .build())
                .flatMap(requestWithTemporaryFile -> upload(requestWithTemporaryFile, request.getFilename() + ".zip", () -> {
                    try {
                        Files.delete(requestWithTemporaryFile.getBuildpack());
                    } catch (IOException e) {
                        throw Exceptions.propagate(e);
                    }
                }));
        } else {
            return upload(request, request.getFilename(), () -> {
            });
        }
    }

    private Mono<UploadBuildpackResponse> upload(UploadBuildpackRequest request, String filename, Runnable onTerminate) {
        return put(request, UploadBuildpackResponse.class, builder -> builder.pathSegment("buildpacks", request.getBuildpackId(), "bits"),
            multipartRequest -> upload(request.getBuildpack(), multipartRequest, filename), onTerminate)
            .checkpoint();
    }

    private void upload(Path buildpack, MultipartHttpClientRequest multipartRequest, String filename) {
        multipartRequest.addPart(part -> part.setName("buildpack")
            .setFilename(filename)
            .setContentType(APPLICATION_ZIP)
            .sendFile(buildpack))
            .done();
    }

}
