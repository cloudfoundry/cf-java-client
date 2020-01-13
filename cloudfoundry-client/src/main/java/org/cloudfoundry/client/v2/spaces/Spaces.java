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

package org.cloudfoundry.client.v2.spaces;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Spaces Client API
 */
public interface Spaces {

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/spaces/associate_auditor_with_the_space.html">Associate Auditor with the Space</a> request
     *
     * @param request the Associate Auditor request
     * @return the response from the Associate Auditor request
     */
    Mono<AssociateSpaceAuditorResponse> associateAuditor(AssociateSpaceAuditorRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/spaces/associate_auditor_with_the_space_by_username.html">Associate Auditor with the Space by Username</a> request
     *
     * @param request the Associate Auditor with the Space by Username request
     * @return the response from the Associate Auditor with the Space by Username request
     */
    Mono<AssociateSpaceAuditorByUsernameResponse> associateAuditorByUsername(AssociateSpaceAuditorByUsernameRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/spaces/associate_developer_with_the_space.html">Associate Developer with the Space</a> request
     *
     * @param request the Associate Developer request
     * @return the response from the Associate Developer request
     */
    Mono<AssociateSpaceDeveloperResponse> associateDeveloper(AssociateSpaceDeveloperRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/spaces/associate_developer_with_the_space_by_username.html">Associate Developer with the Space by Username</a> request
     *
     * @param request the Associate Developer with the Space by Username request
     * @return the response from the Associate Developer with the Space by Username request
     */
    Mono<AssociateSpaceDeveloperByUsernameResponse> associateDeveloperByUsername(AssociateSpaceDeveloperByUsernameRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/spaces/associate_manager_with_the_space.html">Associate Manager with the Space</a> request
     *
     * @param request the Associate Manager request
     * @return the response from the Associate Manager request
     */
    Mono<AssociateSpaceManagerResponse> associateManager(AssociateSpaceManagerRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/spaces/associate_manager_with_the_space_by_username.html">Associate Manager with the Space by Username</a> request
     *
     * @param request the Associate Manager with the Space by Username request
     * @return the response from the Associate Manager with the Space by Username request
     */
    Mono<AssociateSpaceManagerByUsernameResponse> associateManagerByUsername(AssociateSpaceManagerByUsernameRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/spaces/associate_security_group_with_the_space.html">Associate Security Group with the Space</a> request
     *
     * @param request the Associate Security Group request
     * @return the response from the Associate Security Group request
     */
    Mono<AssociateSpaceSecurityGroupResponse> associateSecurityGroup(AssociateSpaceSecurityGroupRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/spaces/creating_a_space.html">Create Space</a> request
     *
     * @param request the Create Space request
     * @return the response from the Create Space request
     */
    Mono<CreateSpaceResponse> create(CreateSpaceRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/spaces/delete_a_particular_space.html">Delete a Particular Space</a> request
     *
     * @param request the Delete a Space request
     * @return the response from the Delete a Space request
     */
    Mono<DeleteSpaceResponse> delete(DeleteSpaceRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/spaces/retrieve_a_particular_space.html">Get Space</a> request
     *
     * @param request the Get Space request
     * @return the response from the Get Space request
     */
    Mono<GetSpaceResponse> get(GetSpaceRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/spaces/get_space_summary.html">Get Space Summary</a> request
     *
     * @param request the Get Space Summary request
     * @return the response from the Get Space Summary request
     */
    Mono<GetSpaceSummaryResponse> getSummary(GetSpaceSummaryRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/spaces/list_all_spaces.html">List Spaces</a> request
     *
     * @param request the List Spaces request
     * @return the response from the List Spaces request
     */
    Mono<ListSpacesResponse> list(ListSpacesRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/spaces/list_all_apps_for_the_space.html">List all Apps for the Space</a> request
     *
     * @param request the List all Apps for the Space request
     * @return the response from the List all Apps for the Space request
     */
    Mono<ListSpaceApplicationsResponse> listApplications(ListSpaceApplicationsRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/spaces/list_all_auditors_for_the_space.html">List all Auditors for the Space</a> request
     *
     * @param request the List all Auditors for the Space request
     * @return the response from the List all Auditors for the Space request
     */
    Mono<ListSpaceAuditorsResponse> listAuditors(ListSpaceAuditorsRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/spaces/list_all_developers_for_the_space.html">List all Developers for the Space</a> request
     *
     * @param request the List all Developers for the Space request
     * @return the response from the List all Developers for the Space request
     */
    Mono<ListSpaceDevelopersResponse> listDevelopers(ListSpaceDevelopersRequest request);

    /**
     * Makes the deprecated <a href="https://apidocs.cloudfoundry.org/latest-release/spaces/list_all_domains_for_the_space_%28deprecated%29.html">List all Domains for the Space</a> request
     *
     * @param request the List all Domains for the Space request
     * @return the response from the List all Domains for the Space request
     */
    @Deprecated
    Mono<ListSpaceDomainsResponse> listDomains(ListSpaceDomainsRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/spaces/list_all_events_for_the_space.html">List all Events for the Space</a> request
     *
     * @param request the List all Events for the Space request
     * @return the response from the List all Events for the Space request
     */
    Mono<ListSpaceEventsResponse> listEvents(ListSpaceEventsRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/spaces/list_all_managers_for_the_space.html">List all Managers for the Space</a> request
     *
     * @param request the List all Managers for the Space request
     * @return the response from the List all Managers for the Space request
     */
    Mono<ListSpaceManagersResponse> listManagers(ListSpaceManagersRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/spaces/list_all_routes_for_the_space.html">List all Routes for the Space</a> request
     *
     * @param request the List all Routes for the Space request
     * @return the response from the List all Routes for the Space request
     */
    Mono<ListSpaceRoutesResponse> listRoutes(ListSpaceRoutesRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/spaces/list_all_security_groups_for_the_space.html">List all Security Groups for the Space</a> request
     *
     * @param request the List all Security Groups for the Space request
     * @return the response from the List all Security Groups for the Space request
     */
    Mono<ListSpaceSecurityGroupsResponse> listSecurityGroups(ListSpaceSecurityGroupsRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/spaces/list_all_service_instances_for_the_space.html">List all Service Instances for the Space</a> request
     *
     * @param request the List all Service Instances for the Space request
     * @return the response from the List all Service Instances for the Space request
     */
    Mono<ListSpaceServiceInstancesResponse> listServiceInstances(ListSpaceServiceInstancesRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/spaces/list_all_services_for_the_space.html">List all Services for the Space</a> request
     *
     * @param request the List all Services for the Space request
     * @return the response from the List all Services for the Space request
     */
    Mono<ListSpaceServicesResponse> listServices(ListSpaceServicesRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/spaces/retrieving_the_roles_of_all_users_in_the_space.html">Retrieving the roles of all Users in the Space</a> request
     *
     * @param request the Retrieving the roles of all Users in the Space request
     * @return the response from the Retrieving the roles of all Users in the Space request
     */
    Mono<ListSpaceUserRolesResponse> listUserRoles(ListSpaceUserRolesRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/spaces/remove_auditor_from_the_space.html">Remove Auditor from the Space</a> request
     *
     * @param request the Remove Auditor from the Space request
     * @return the response from the Remove Auditor from the Space request
     */
    Mono<Void> removeAuditor(RemoveSpaceAuditorRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/spaces/disassociate_auditor_with_the_space_by_username.html">Disassociate Auditor with the Space by Username</a> request
     *
     * @param request the Disassociate Auditor with the Space by Username request
     * @return the response from the Disassociate Auditor with the Space by Username request
     */
    Mono<RemoveSpaceAuditorByUsernameResponse> removeAuditorByUsername(RemoveSpaceAuditorByUsernameRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/spaces/remove_developer_from_the_space.html">Remove Developer from the Space</a> request
     *
     * @param request the Remove Developer from the Space request
     * @return the response from the Remove Developer from the Space request
     */
    Mono<Void> removeDeveloper(RemoveSpaceDeveloperRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/spaces/disassociate_developer_with_the_space_by_username.html">Disassociate Developer with the Space by Username</a> request
     *
     * @param request the Disassociate Developer with the Space by Username request
     * @return the response from the Disassociate Developer with the Space by Username request
     */
    Mono<RemoveSpaceDeveloperByUsernameResponse> removeDeveloperByUsername(RemoveSpaceDeveloperByUsernameRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/spaces/remove_manager_from_the_space.html">Remove Manager from the Space</a> request
     *
     * @param request the Remove Manager from the Space request
     * @return the response from the Remove Manager from the Space request
     */
    Mono<Void> removeManager(RemoveSpaceManagerRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/spaces/disassociate_manager_with_the_space_by_username.html">Disassociate Manager with the Space by Username</a> request
     *
     * @param request the Disassociate Manager with the Space by Username request
     * @return the response from the Disassociate Manager with the Space by Username request
     */
    Mono<RemoveSpaceManagerByUsernameResponse> removeManagerByUsername(RemoveSpaceManagerByUsernameRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/spaces/remove_security_group_from_the_space.html">Remove Security Group from the Space</a> request
     *
     * @param request the Remove Security Group from the Space request
     * @return the response from the Remove Security Group from the Space request
     */
    Mono<Void> removeSecurityGroup(RemoveSpaceSecurityGroupRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/spaces/update_a_space.html">Update a Space</a> request
     *
     * @param request the Update a Space request
     * @return the response from the Update a Space request
     */
    Mono<UpdateSpaceResponse> update(UpdateSpaceRequest request);

}
