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

package org.cloudfoundry.client.v2.spaces;

import org.reactivestreams.Publisher;

/**
 * Main entry point to the Cloud Foundry Spaces Client API
 */
public interface Spaces {

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/spaces/associate_auditor_with_the_space.html">Associate
     * Auditor with the Space</a> request
     *
     * @param request the Associate Auditor request
     * @return the response from the Associate Auditor request
     */
    Publisher<AssociateSpaceAuditorResponse> associateAuditor(AssociateSpaceAuditorRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/spaces/associate_developer_with_the_space.html">Associate
     * Developer with the Space</a> request
     *
     * @param request the Associate Developer request
     * @return the response from the Associate Developer request
     */
    Publisher<AssociateSpaceDeveloperResponse> associateDeveloper(AssociateSpaceDeveloperRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/spaces/associate_manager_with_the_space.html">Associate
     * Manager with the Space</a> request
     *
     * @param request the Associate Manager request
     * @return the response from the Associate Manager request
     */
    Publisher<AssociateSpaceManagerResponse> associateManager(AssociateSpaceManagerRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/spaces/associate_security_group_with_the_space.html">
     * Associate Security Group with the Space</a> request
     *
     * @param request the Associate Security Group request
     * @return the response from the Associate Security Group request
     */
    Publisher<AssociateSpaceSecurityGroupResponse> associateSecurityGroup(AssociateSpaceSecurityGroupRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/spaces/creating_a_space.html">Create Space</a> request
     *
     * @param request the Create Space request
     * @return the response from the Create Space request
     */
    Publisher<CreateSpaceResponse> create(CreateSpaceRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/spaces/delete_a_particular_space.html">Delete a Particular
     * Space</a> request
     *
     * @param request the Delete a Space request
     * @return the response from the Delete a Space request
     */
    Publisher<Void> delete(DeleteSpaceRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/spaces/retrieve_a_particular_space.html">Get Space</a>
     * request
     *
     * @param request the Get Space request
     * @return the response from the Get Space request
     */
    Publisher<GetSpaceResponse> get(GetSpaceRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/spaces/get_space_summary.html">Get Space Summary</a>
     * request
     *
     * @param request the Get Space Summary request
     * @return the response from the Get Space Summary request
     */
    Publisher<GetSpaceSummaryResponse> getSummary(GetSpaceSummaryRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/spaces/list_all_spaces.html">List Spaces</a> request
     *
     * @param request the List Spaces request
     * @return the response from the List Spaces request
     */
    Publisher<ListSpacesResponse> list(ListSpacesRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/spaces/list_all_apps_for_the_space.html">List all Apps for
     * the Space</a> request
     *
     * @param request the List all Apps for the Space request
     * @return the response from the List all Apps for the Space request
     */
    Publisher<ListSpaceApplicationsResponse> listApplications(ListSpaceApplicationsRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/spaces/list_all_auditors_for_the_space.html">List all
     * Auditors for the Space</a> request
     *
     * @param request the List all Auditors for the Space request
     * @return the response from the List all Auditors for the Space request
     */
    Publisher<ListSpaceAuditorsResponse> listAuditors(ListSpaceAuditorsRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/spaces/list_all_developers_for_the_space.html">List all
     * Developers for the Space</a> request
     *
     * @param request the List all Developers for the Space request
     * @return the response from the List all Developers for the Space request
     */
    Publisher<ListSpaceDevelopersResponse> listDevelopers(ListSpaceDevelopersRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/spaces/list_all_events_for_the_space.html">List all Events
     * for the Space</a> request
     *
     * @param request the List all Events for the Space request
     * @return the response from the List all Events for the Space request
     */
    Publisher<ListSpaceEventsResponse> listEvents(ListSpaceEventsRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/spaces/list_all_managers_for_the_space.html">List all
     * Managers for the Space</a> request
     *
     * @param request the List all Managers for the Space request
     * @return the response from the List all Managers for the Space request
     */
    Publisher<ListSpaceManagersResponse> listManagers(ListSpaceManagersRequest request);

}
