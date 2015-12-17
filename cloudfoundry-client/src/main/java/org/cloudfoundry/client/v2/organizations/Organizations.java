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

package org.cloudfoundry.client.v2.organizations;

import org.reactivestreams.Publisher;

/**
 * Main entry point to the Cloud Foundry Organizations Client API
 */
public interface Organizations {

    /**
     * Makes the
     * <a href="http://apidocs.cloudfoundry.org/214/organizations/associate_auditor_with_the_organization.html">Associate
     * Auditor with the Organization</a> request
     *
     * @param request the Associate Auditor request
     * @return the response from the Associate Auditor request
     */
    Publisher<AssociateAuditorResponse> associateAuditor(AssociateAuditorRequest request);

    /**
     * Makes the
     * <a href="http://apidocs.cloudfoundry.org/214/organizations/associate_billing_manager_with_the_organization.html">
     * Associate Billing Manager with the Organization</a> request
     *
     * @param request the Associate Billing Manager with the Organization request
     * @return the response from the Associate Billing Manager with the Organization request
     */
    Publisher<AssociateBillingManagerResponse> associateBillingManager(AssociateBillingManagerRequest request);

    /**
     * Makes the
     * <a href="http://apidocs.cloudfoundry.org/214/organizations/associate_manager_with_the_organization.html">Associate
     * Manager with the Organization</a> request
     *
     * @param request the Associate Manager with the Organization request
     * @return the response from the Associate Manager with the Organization request
     */
    Publisher<AssociateOrganizationManagerResponse> associateManager(AssociateOrganizationManagerRequest request);

    /**
     * Makes the
     * <a href="http://apidocs.cloudfoundry.org/214/organizations/associate_private_domain_with_the_organization.html">Associate
     * Private Domain with the Organization</a> request
     *
     * @param request the Associate Private Domain with the Organization request
     * @return the response from the Associate Private Domain with the Organization request
     */
    Publisher<AssociateOrganizationPrivateDomainResponse> associatePrivateDomain(
            AssociateOrganizationPrivateDomainRequest request);

    /**
     * Makes the
     * <a href="http://apidocs.cloudfoundry.org/214/organizations/associate_user_with_the_organization.html">Associate
     * User with the Organization</a> request
     *
     * @param request the Associate User with the Organization request
     * @return the response from the Associate User with the Organization request
     */
    Publisher<AssociateOrganizationUserResponse> associateUser(AssociateOrganizationUserRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/organizations/creating_an_organization.html">Creating an
     * Organization</a> request
     *
     * @param request the Creating an Organization request
     * @return the response from the Creating an Organization request
     */
    Publisher<CreateOrganizationResponse> create(CreateOrganizationRequest request);

    /**
     * Makes the
     * <a href="http://apidocs.cloudfoundry.org/214/organizations/delete_a_particular_organization.html">Delete
     * a Particular Organization</a> request
     *
     * @param request the Delete a Particular Organization request
     * @return the response from the Delete a Particular Organization request
     */
    Publisher<Void> delete(DeleteOrganizationRequest request);

    /**
     * Makes the
     * <a href="http://apidocs.cloudfoundry.org/214/organizations/retrieve_a_particular_organization.html">Retrieve
     * a Particular Organization</a> request
     *
     * @param request the Retrieve a Particular Organization request
     * @return the response from the Retrieve a Particular Organization request
     */
    Publisher<GetOrganizationResponse> get(GetOrganizationRequest request);

    /**
     * Makes the
     * <a href="http://apidocs.cloudfoundry.org/214/organizations/retrieving_organization_memory_usage.html">Retrieving
     * organization memory usage</a> request
     *
     * @param request the Retrieving organization memory usage request
     * @return the response from the Retrieving organization memory usage request
     */
    Publisher<GetOrganizationMemoryUsageResponse> getMemoryUsage(GetOrganizationMemoryUsageRequest request);

    /**
     * Makes the
     * <a href="http://apidocs.cloudfoundry.org/214/organizations/retrieving_the_roles_of_all_users_in_the_organization.html">Retrieving
     * the roles of all Users in the Organization</a> request
     *
     * @param request the Retrieving the roles of all Users in the Organization request
     * @return the response from the Retrieving the roles of all Users in the Organization request
     */
    Publisher<GetOrganizationUserRolesResponse> getUserRoles(GetOrganizationUserRolesRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/organizations/list_all_organizations.html">List
     * Organizations</a> request
     *
     * @param request the List Organizations request
     * @return the response from the List Organizations request
     */
    Publisher<ListOrganizationsResponse> list(ListOrganizationsRequest request);

    /**
     * Makes the
     * <a href="http://apidocs.cloudfoundry.org/214/organizations/list_all_auditors_for_the_organization.html">List
     * all Auditors for the Organization</a> request
     *
     * @param request the List all Auditors for the Organization request
     * @return the response from the List all Auditors for the Organization request
     */
    Publisher<ListOrganizationAuditorsResponse> listAuditors(ListOrganizationAuditorsRequest request);

    /**
     * Makes the
     * <a href="http://apidocs.cloudfoundry.org/214/organizations/list_all_billing_managers_for_the_organization.html">List
     * all Billing Managers for the Organization</a> request
     *
     * @param request the List all Billing Managers for the Organization request
     * @return the response from the List all Billing Managers for the Organization request
     */
    Publisher<ListOrganizationBillingManagersResponse> listBillingManagers(
            ListOrganizationBillingManagersRequest request);

    /**
     * Makes the
     * <a href="http://apidocs.cloudfoundry.org/214/organizations/list_all_managers_for_the_organization.html">List
     * all Managers for the Organization</a> request
     *
     * @param request the List all Managers for the Organization request
     * @return the response from the List all Managers for the Organization request
     */
    Publisher<ListOrganizationManagersResponse> listManagers(ListOrganizationManagersRequest request);

    /**
     * <a href="http://apidocs.cloudfoundry.org/214/organizations/list_all_private_domains_for_the_organization.html">List
     * all Private Domains for the Organization</a> request
     *
     * @param request the List all Private Domains for the Organization request
     * @return the response from the List all Private Domains for the Organization request
     */
    Publisher<ListOrganizationPrivateDomainsResponse> listPrivateDomains(ListOrganizationPrivateDomainsRequest request);

    /**
     * Makes the
     * <a href="http://apidocs.cloudfoundry.org/214/organizations/list_all_services_for_the_organization.html">List
     * all Services for the Organization</a> request
     *
     * @param request the List all Services for the Organization request
     * @return the response from the List all Services for the Organization request
     */
    Publisher<ListOrganizationServicesResponse> listServices(ListOrganizationServicesRequest request);

    /**
     * Makes the
     * <a href="http://apidocs.cloudfoundry.org/214/organizations/list_all_space_quota_definitions_for_the_organization.html">List
     * all Space Quota Definitions for the Organization</a> request
     *
     * @param request the List all Space Quota Definitions for the Organization request
     * @return the response from the List all Space Quota Definitions for the Organization request
     */
    Publisher<ListOrganizationSpaceQuotaDefinitionsResponse> listSpaceQuotaDefinitions(
            ListOrganizationSpaceQuotaDefinitionsRequest request);

    /**
     * Makes the
     * <a href="http://apidocs.cloudfoundry.org/214/organizations/list_all_spaces_for_the_organization.html">List
     * all Spaces for the Organization</a> request
     *
     * @param request the List all Spaces for the Organization request
     * @return the response from the List all Spaces for the Organization request
     */
    Publisher<ListOrganizationSpacesResponse> listSpaces(ListOrganizationSpacesRequest request);

    /**
     * Makes the
     * <a href="http://apidocs.cloudfoundry.org/214/organizations/list_all_users_for_the_organization.html">List
     * all Users for the Organization</a> request
     *
     * @param request the List all Users for the Organization request
     * @return the response from the List all Users for the Organization request
     */
    Publisher<ListOrganizationUsersResponse> listUsers(ListOrganizationUsersRequest request);

    /**
     * Makes the
     * <a href="http://apidocs.cloudfoundry.org/214/organizations/remove_auditor_from_the_organization.html">Remove
     * Auditor from the Organization</a> request
     *
     * @param request the Remove Auditor from the Organization request
     * @return the response from the Remove Auditor from the Organization request
     */
    Publisher<Void> removeAuditor(RemoveOrganizationAuditorRequest request);

    /**
     * <a href="http://apidocs.cloudfoundry.org/223/organizations/remove_billing_manager_from_the_organization.html">Remove
     * Billing Manager from the Organization</a> request
     *
     * @param request the Remove Billing Manager from the Organization request
     * @return the response from the Remove Billing Manager from the Organization request
     */
    Publisher<Void> removeBillingManager(RemoveOrganizationBillingManagerRequest request);

    /**
     * <a href="http://apidocs.cloudfoundry.org/214/organizations/remove_manager_from_the_organization.html">Remove
     * Manager from the Organization</a> request
     *
     * @param request the Remove Manager from the Organization request
     * @return the response from the Remove Manager from the Organization request
     */
    Publisher<Void> removeManager(RemoveOrganizationManagerRequest request);

    /**
     * Makes the
     * <a href="http://apidocs.cloudfoundry.org/223/organizations/remove_private_domain_from_the_organization.html">Remove
     * Private Domain from the Organization</a> request
     *
     * @param request the Remove Private Domain from the Organization request
     * @return the response from the Remove Private Domain from the Organization request
     */
    Publisher<Void> removePrivateDomain(RemoveOrganizationPrivateDomainRequest request);

    /**
     * Makes the
     * <a href="http://apidocs.cloudfoundry.org/223/organizations/remove_user_from_the_organization.html">Remove
     * User from the Organization</a> request
     *
     * @param request the Remove User from the Organization request
     * @return the response from the Remove User from the Organization request
     */
    Publisher<Void> removeUser(RemoveOrganizationUserRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/organizations/get_organization_summary.html">Get
     * Organization summary</a> request
     *
     * @param request the Organization summary request
     * @return the response from the Organization summary request
     */
    Publisher<SummaryOrganizationResponse> summary(SummaryOrganizationRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/organizations/update_an_organization.html">Update an
     * Organization</a> request
     *
     * @param request the Update an Organization request
     * @return the response from the Update an Organization request
     */
    Publisher<UpdateOrganizationResponse> update(UpdateOrganizationRequest request);

}
