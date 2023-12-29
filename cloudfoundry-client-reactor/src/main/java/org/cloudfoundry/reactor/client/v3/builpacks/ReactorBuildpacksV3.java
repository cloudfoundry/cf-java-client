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

package org.cloudfoundry.reactor.client.v3.builpacks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.cloudfoundry.client.v3.buildpacks.BuildpacksV3;
import org.cloudfoundry.client.v3.buildpacks.CreateBuildpackRequest;
import org.cloudfoundry.client.v3.buildpacks.CreateBuildpackResponse;
import org.cloudfoundry.client.v3.buildpacks.DeleteBuildpackRequest;
import org.cloudfoundry.client.v3.buildpacks.GetBuildpackRequest;
import org.cloudfoundry.client.v3.buildpacks.GetBuildpackResponse;
import org.cloudfoundry.client.v3.buildpacks.ListBuildpacksRequest;
import org.cloudfoundry.client.v3.buildpacks.ListBuildpacksResponse;
import org.cloudfoundry.client.v3.buildpacks.UpdateBuildpackRequest;
import org.cloudfoundry.client.v3.buildpacks.UpdateBuildpackResponse;
import org.cloudfoundry.client.v3.buildpacks.UploadBuildpackRequest;
import org.cloudfoundry.client.v3.buildpacks.UploadBuildpackResponse;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v3.AbstractClientV3Operations;
import org.cloudfoundry.reactor.util.MultipartHttpClientRequest;
import org.cloudfoundry.util.FileUtils;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

/**
 * The Reactor-based implementation of {@link BuildpacksV3}
 */
public final class ReactorBuildpacksV3 extends AbstractClientV3Operations implements BuildpacksV3 {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorBuildpacksV3(
            ConnectionContext connectionContext,
            Mono<String> root,
            TokenProvider tokenProvider,
            Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<CreateBuildpackResponse> create(CreateBuildpackRequest request) {
        return post(
                        request,
                        CreateBuildpackResponse.class,
                        builder -> builder.pathSegment("buildpacks"))
                .checkpoint();
    }

    @Override
    public Mono<String> delete(DeleteBuildpackRequest request) {
        return delete(
                        request,
                        builder -> builder.pathSegment("buildpacks", request.getBuildpackId()))
                .checkpoint();
    }

    @Override
    public Mono<GetBuildpackResponse> get(GetBuildpackRequest request) {
        return get(
                        request,
                        GetBuildpackResponse.class,
                        builder -> builder.pathSegment("buildpacks", request.getBuildpackId()))
                .checkpoint();
    }

    @Override
    public Mono<ListBuildpacksResponse> list(ListBuildpacksRequest request) {
        return get(
                        request,
                        ListBuildpacksResponse.class,
                        builder -> builder.pathSegment("buildpacks"))
                .checkpoint();
    }

    @Override
    public Mono<UpdateBuildpackResponse> update(UpdateBuildpackRequest request) {
        return patch(
                        request,
                        UpdateBuildpackResponse.class,
                        builder -> builder.pathSegment("buildpacks", request.getBuildpackId()))
                .checkpoint();
    }

    @Override
    public Mono<UploadBuildpackResponse> upload(UploadBuildpackRequest request) {
        Path bits = request.getBits();

        if (bits.toFile().isDirectory()) {
            return FileUtils.compress(bits)
                    .map(
                            temporaryFile ->
                                    UploadBuildpackRequest.builder()
                                            .from(request)
                                            .bits(temporaryFile)
                                            .build())
                    .flatMap(
                            requestWithTemporaryFile ->
                                    upload(
                                            requestWithTemporaryFile,
                                            () -> {
                                                try {
                                                    Files.delete(
                                                            requestWithTemporaryFile.getBits());
                                                } catch (IOException e) {
                                                    throw Exceptions.propagate(e);
                                                }
                                            }));
        } else {
            return upload(request, () -> {});
        }
    }

    private Mono<UploadBuildpackResponse> upload(
            UploadBuildpackRequest request, Runnable onTerminate) {
        return post(
                        request,
                        UploadBuildpackResponse.class,
                        builder ->
                                builder.pathSegment(
                                        "buildpacks", request.getBuildpackId(), "upload"),
                        outbound -> upload(request.getBits(), outbound),
                        onTerminate)
                .checkpoint();
    }

    private void upload(Path bits, MultipartHttpClientRequest r) {
        r.addPart(part -> part.setName("bits").setContentType(APPLICATION_ZIP).sendFile(bits))
                .done();
    }
}
