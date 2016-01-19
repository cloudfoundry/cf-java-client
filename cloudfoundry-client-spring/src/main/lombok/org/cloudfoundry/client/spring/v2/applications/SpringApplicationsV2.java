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

package org.cloudfoundry.client.spring.v2.applications;

import lombok.ToString;
import org.cloudfoundry.client.spring.util.AbstractSpringOperations;
import org.cloudfoundry.client.spring.util.QueryBuilder;
import org.cloudfoundry.client.spring.v2.FilterBuilder;
import org.cloudfoundry.client.v2.applications.ApplicationEnvironmentRequest;
import org.cloudfoundry.client.v2.applications.ApplicationEnvironmentResponse;
import org.cloudfoundry.client.v2.applications.ApplicationInstancesRequest;
import org.cloudfoundry.client.v2.applications.ApplicationInstancesResponse;
import org.cloudfoundry.client.v2.applications.ApplicationStatisticsRequest;
import org.cloudfoundry.client.v2.applications.ApplicationStatisticsResponse;
import org.cloudfoundry.client.v2.applications.ApplicationsV2;
import org.cloudfoundry.client.v2.applications.AssociateApplicationRouteRequest;
import org.cloudfoundry.client.v2.applications.AssociateApplicationRouteResponse;
import org.cloudfoundry.client.v2.applications.CopyApplicationRequest;
import org.cloudfoundry.client.v2.applications.CopyApplicationResponse;
import org.cloudfoundry.client.v2.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v2.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v2.applications.DeleteApplicationRequest;
import org.cloudfoundry.client.v2.applications.DownloadApplicationRequest;
import org.cloudfoundry.client.v2.applications.DownloadDropletRequest;
import org.cloudfoundry.client.v2.applications.GetApplicationRequest;
import org.cloudfoundry.client.v2.applications.GetApplicationResponse;
import org.cloudfoundry.client.v2.applications.ListApplicationRoutesRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationRoutesResponse;
import org.cloudfoundry.client.v2.applications.ListApplicationServiceBindingsRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationServiceBindingsResponse;
import org.cloudfoundry.client.v2.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationsResponse;
import org.cloudfoundry.client.v2.applications.RemoveApplicationRouteRequest;
import org.cloudfoundry.client.v2.applications.RemoveApplicationServiceBindingRequest;
import org.cloudfoundry.client.v2.applications.RestageApplicationRequest;
import org.cloudfoundry.client.v2.applications.RestageApplicationResponse;
import org.cloudfoundry.client.v2.applications.SummaryApplicationRequest;
import org.cloudfoundry.client.v2.applications.SummaryApplicationResponse;
import org.cloudfoundry.client.v2.applications.TerminateApplicationInstanceRequest;
import org.cloudfoundry.client.v2.applications.UpdateApplicationRequest;
import org.cloudfoundry.client.v2.applications.UpdateApplicationResponse;
import org.cloudfoundry.client.v2.applications.UploadApplicationRequest;
import org.cloudfoundry.client.v2.applications.UploadApplicationResponse;
import org.reactivestreams.Publisher;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ProcessorGroup;
import reactor.fn.Consumer;
import reactor.fn.Supplier;

import java.net.URI;

/**
 * The Spring-based implementation of {@link ApplicationsV2}
 */
@ToString(callSuper = true)
public final class SpringApplicationsV2 extends AbstractSpringOperations implements ApplicationsV2 {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     * @param processorGroup The group to use when making requests
     */
    public SpringApplicationsV2(RestOperations restOperations, URI root, ProcessorGroup<?> processorGroup) {
        super(restOperations, root, processorGroup);
    }

    @Override
    public Mono<AssociateApplicationRouteResponse> associateRoute(final AssociateApplicationRouteRequest request) {
        return put(request, AssociateApplicationRouteResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "apps", request.getId(), "routes", request.getRouteId());
            }

        });
    }

    @Override
    public Mono<CopyApplicationResponse> copy(final CopyApplicationRequest request) {
        return post(request, CopyApplicationResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "apps", request.getId(), "copy_bits");
            }

        });
    }

    @Override
    public Mono<CreateApplicationResponse> create(CreateApplicationRequest request) {
        return post(request, CreateApplicationResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "apps");
            }

        });
    }

    @Override
    public Mono<Void> delete(final DeleteApplicationRequest request) {
        return delete(request, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "apps", request.getId());
            }

        });
    }

    @Override
    public Publisher<byte[]> download(final DownloadApplicationRequest request) {
        return getStream(request,
                new Consumer<UriComponentsBuilder>() {
                    @Override
                    public void accept(UriComponentsBuilder builder) {
                        builder.pathSegment("v2", "apps", request.getId(), "download");
                    }
                });
    }

    @Override
    public Publisher<byte[]> downloadDroplet(final DownloadDropletRequest request) {
        return getStream(request,
                new Consumer<UriComponentsBuilder>() {
                    @Override
                    public void accept(UriComponentsBuilder builder) {
                        builder.pathSegment("v2", "apps", request.getId(), "droplet", "download");
                    }
                });
    }

    @Override
    public Mono<ApplicationEnvironmentResponse> environment(final ApplicationEnvironmentRequest request) {
        return get(request, ApplicationEnvironmentResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "apps", request.getId(), "env");
            }

        });
    }

    @Override
    public Mono<GetApplicationResponse> get(final GetApplicationRequest request) {
        return get(request, GetApplicationResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "apps", request.getId());
            }

        });
    }

    @Override
    public Mono<ApplicationInstancesResponse> instances(final ApplicationInstancesRequest request) {
        return get(request, ApplicationInstancesResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "apps", request.getId(), "instances");
            }

        });
    }

    @Override
    public Mono<ListApplicationsResponse> list(final ListApplicationsRequest request) {
        return get(request, ListApplicationsResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "apps");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Mono<ListApplicationRoutesResponse> listRoutes(final ListApplicationRoutesRequest request) {
        return get(request, ListApplicationRoutesResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "apps", request.getId(), "routes");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Mono<ListApplicationServiceBindingsResponse> listServiceBindings(final ListApplicationServiceBindingsRequest request) {
        return get(request, ListApplicationServiceBindingsResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "apps", request.getId(), "service_bindings");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Mono<Void> removeRoute(final RemoveApplicationRouteRequest request) {
        return delete(request, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "apps", request.getId(), "routes", request.getRouteId());
            }

        });
    }

    @Override
    public Mono<Void> removeServiceBinding(final RemoveApplicationServiceBindingRequest request) {
        return delete(request, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "apps", request.getId(), "service_bindings", request.getServiceBindingId());
            }

        });
    }

    @Override
    public Mono<RestageApplicationResponse> restage(final RestageApplicationRequest request) {
        return post(request, RestageApplicationResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "apps", request.getId(), "restage");
            }

        });
    }

    @Override
    public Mono<ApplicationStatisticsResponse> statistics(final ApplicationStatisticsRequest request) {
        return get(request, ApplicationStatisticsResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "apps", request.getId(), "stats");
            }

        });
    }

    @Override
    public Mono<SummaryApplicationResponse> summary(final SummaryApplicationRequest request) {
        return get(request, SummaryApplicationResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "apps", request.getId(), "summary");
            }

        });
    }

    @Override
    public Mono<Void> terminateInstance(final TerminateApplicationInstanceRequest request) {
        return delete(request, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "apps", request.getId(), "instances", request.getIndex());
            }

        });
    }

    @Override
    public Mono<UpdateApplicationResponse> update(final UpdateApplicationRequest request) {
        return put(request, UpdateApplicationResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "apps", request.getId());
            }

        });
    }

    @Override
    public Mono<UploadApplicationResponse> upload(final UploadApplicationRequest request) {
        return putWithBody(request, new Supplier<MultiValueMap<String, Object>>() {

                    @Override
                    public MultiValueMap<String, Object> get() {
                        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                        if (request.getAsync() != null) {
                            body.add("async", request.getAsync());
                        }
                        body.add("resources", request.getResources());
                        body.add("application", new FileSystemResource(request.getApplication()));
                        return body;
                    }

                }, UploadApplicationResponse.class, new Consumer<UriComponentsBuilder>() {

                    @Override
                    public void accept(UriComponentsBuilder builder) {
                        builder.pathSegment("v2", "apps", request.getId(), "bits");
                    }

                }
        );
    }

}
