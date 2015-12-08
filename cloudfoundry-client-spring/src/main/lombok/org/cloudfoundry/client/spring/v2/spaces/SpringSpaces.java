/*
 * Copyright 2013-2015 the original author or authors.
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

package org.cloudfoundry.client.spring.v2.spaces;

import lombok.ToString;
import org.cloudfoundry.client.spring.util.AbstractSpringOperations;
import org.cloudfoundry.client.spring.util.QueryBuilder;
import org.cloudfoundry.client.spring.v2.FilterBuilder;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceAuditorRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceAuditorResponse;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceDeveloperRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceDeveloperResponse;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceManagerRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceManagerResponse;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceSecurityGroupRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceSecurityGroupResponse;
import org.cloudfoundry.client.v2.spaces.CreateSpaceRequest;
import org.cloudfoundry.client.v2.spaces.CreateSpaceResponse;
import org.cloudfoundry.client.v2.spaces.DeleteSpaceRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceResponse;
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceAuditorsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceAuditorsResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceDevelopersRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceDevelopersResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceEventsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceEventsResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceManagersRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceManagersResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceRoutesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceRoutesResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceSecurityGroupsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceSecurityGroupsResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceServiceInstancesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceServiceInstancesResponse;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesResponse;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceAuditorRequest;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceDeveloperRequest;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceManagerRequest;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceSecurityGroupRequest;
import org.cloudfoundry.client.v2.spaces.Spaces;
import org.reactivestreams.Publisher;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.fn.Consumer;

import java.net.URI;

/**
 * The Spring-based implementation of {@link Spaces}
 */
@ToString(callSuper = true)
public final class SpringSpaces extends AbstractSpringOperations implements Spaces {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     */
    public SpringSpaces(RestOperations restOperations, URI root) {
        super(restOperations, root);
    }

    @Override
    public Publisher<AssociateSpaceAuditorResponse> associateAuditor(final AssociateSpaceAuditorRequest request) {
        return put(request, AssociateSpaceAuditorResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "spaces", request.getId(), "auditors", request.getAuditorId());
            }

        });
    }

    @Override
    public Publisher<AssociateSpaceDeveloperResponse> associateDeveloper(final AssociateSpaceDeveloperRequest request) {
        return put(request, AssociateSpaceDeveloperResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "spaces", request.getId(), "developers", request.getDeveloperId());
            }

        });
    }

    @Override
    public Publisher<AssociateSpaceManagerResponse> associateManager(final AssociateSpaceManagerRequest request) {
        return put(request, AssociateSpaceManagerResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "spaces", request.getId(), "managers", request.getManagerId());
            }

        });
    }

    @Override
    public Publisher<AssociateSpaceSecurityGroupResponse> associateSecurityGroup(
            final AssociateSpaceSecurityGroupRequest request) {

        return put(request, AssociateSpaceSecurityGroupResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "spaces", request.getId(), "security_groups", request.getSecurityGroupId());
            }

        });
    }

    @Override
    public Publisher<CreateSpaceResponse> create(final CreateSpaceRequest request) {
        return post(request, CreateSpaceResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "spaces");
            }

        });
    }

    @Override
    public Publisher<Void> delete(final DeleteSpaceRequest request) {
        return delete(request, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "spaces", request.getId());
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Publisher<GetSpaceResponse> get(final GetSpaceRequest request) {
        return get(request, GetSpaceResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "spaces", request.getId());
            }

        });
    }

    @Override
    public Publisher<GetSpaceSummaryResponse> getSummary(final GetSpaceSummaryRequest request) {
        return get(request, GetSpaceSummaryResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "spaces", request.getId(), "summary");
            }

        });
    }

    @Override
    public Publisher<ListSpacesResponse> list(final ListSpacesRequest request) {
        return get(request, ListSpacesResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "spaces");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Publisher<ListSpaceApplicationsResponse> listApplications(final ListSpaceApplicationsRequest request) {
        return get(request, ListSpaceApplicationsResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "spaces", request.getId(), "apps");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Publisher<ListSpaceAuditorsResponse> listAuditors(final ListSpaceAuditorsRequest request) {
        return get(request, ListSpaceAuditorsResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "spaces", request.getId(), "auditors");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Publisher<ListSpaceDevelopersResponse> listDevelopers(final ListSpaceDevelopersRequest request) {
        return get(request, ListSpaceDevelopersResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "spaces", request.getId(), "developers");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Publisher<ListSpaceEventsResponse> listEvents(final ListSpaceEventsRequest request) {
        return get(request, ListSpaceEventsResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "spaces", request.getId(), "events");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Publisher<ListSpaceManagersResponse> listManagers(final ListSpaceManagersRequest request) {
        return get(request, ListSpaceManagersResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "spaces", request.getId(), "managers");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Publisher<ListSpaceRoutesResponse> listRoutes(final ListSpaceRoutesRequest request) {
        return get(request, ListSpaceRoutesResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "spaces", request.getId(), "routes");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Publisher<ListSpaceSecurityGroupsResponse> listSecurityGroups(final ListSpaceSecurityGroupsRequest request) {
        return get(request, ListSpaceSecurityGroupsResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "spaces", request.getId(), "security_groups");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Publisher<ListSpaceServiceInstancesResponse> listServiceInstances(
            final ListSpaceServiceInstancesRequest request) {

        return get(request, ListSpaceServiceInstancesResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "spaces", request.getId(), "service_instances");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Publisher<Void> removeAuditor(final RemoveSpaceAuditorRequest request) {
        return delete(request, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "spaces", request.getId(), "auditors", request.getAuditorId());
            }

        });
    }

    @Override
    public Publisher<Void> removeDeveloper(final RemoveSpaceDeveloperRequest request) {
        return delete(request, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "spaces", request.getId(), "developers", request.getDeveloperId());
            }

        });
    }

    @Override
    public Publisher<Void> removeManager(final RemoveSpaceManagerRequest request) {
        return delete(request, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "spaces", request.getId(), "managers", request.getManagerId());
            }

        });
    }

    @Override
    public Publisher<Void> removeSecurityGroup(final RemoveSpaceSecurityGroupRequest request) {
        return delete(request, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "spaces", request.getId(), "security_groups", request.getSecurityGroupId());
            }

        });
    }

}
