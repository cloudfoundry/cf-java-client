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

package org.cloudfoundry.client.v2.organizations;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Organizations Client API
 */
public interface Organizations {

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organizations/associate_auditor_with_the_organization.html">Associate Auditor with the Organization</a> request
     *
     * @param request the Associate Auditor request
     * @return the response from the Associate Auditor request
     */
    Mono<AssociateOrganizationAuditorResponse> associateAuditor(AssociateOrganizationAuditorRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organizations/associate_auditor_with_the_organization_by_username.html">Associate Auditor with the Organization by Username</a>
     * request
     *
     * @param request the Associate Auditor with an Organization by Username request
     * @return the response from the Associate Auditor with an Organization by Username request
     */
    Mono<AssociateOrganizationAuditorByUsernameResponse> associateAuditorByUsername(AssociateOrganizationAuditorByUsernameRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organizations/associate_billing_manager_with_the_organization.html">Associate Billing Manager with the Organization</a>
     * request
     *
     * @param request the Associate Billing Manager with the Organization request
     * @return the response from the Associate Billing Manager with the Organization request
     */
    Mono<AssociateOrganizationBillingManagerResponse> associateBillingManager(AssociateOrganizationBillingManagerRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organizations/associate_billing_manager_with_the_organization_by_username.html">Associate Billing Manager with the Organization
     * by Username</a> request
     *
     * @param request the Associate Billing Manager with the Organization by Username request
     * @return the response from the Associate Billing Manager with the Organization by Username request
     */
    Mono<AssociateOrganizationBillingManagerByUsernameResponse> associateBillingManagerByUsername(AssociateOrganizationBillingManagerByUsernameRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organizations/associate_manager_with_the_organization.html">Associate Manager with the Organization</a> request
     *
     * @param request the Associate Manager with the Organization request
     * @return the response from the Associate Manager with the Organization request
     */
    Mono<AssociateOrganizationManagerResponse> associateManager(AssociateOrganizationManagerRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organizations/associate_manager_with_the_organization_by_username.html">Associate Manager with the Organization by Username</a>
     * request
     *
     * @param request the Associate Manager with the Organization by Username request
     * @return the response from the Associate Manager with the Organization by Username request
     */
    Mono<AssociateOrganizationManagerByUsernameResponse> associateManagerByUsername(AssociateOrganizationManagerByUsernameRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organizations/associate_private_domain_with_the_organization.html">Associate Private Domain with the Organization</a> request
     *
     * @param request the Associate Private Domain with the Organization request
     * @return the response from the Associate Private Domain with the Organization request
     */
    Mono<AssociateOrganizationPrivateDomainResponse> associatePrivateDomain(AssociateOrganizationPrivateDomainRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organizations/associate_user_with_the_organization.html">Associate User with the Organization</a> request
     *
     * @param request the Associate User with the Organization request
     * @return the response from the Associate User with the Organization request
     */
    Mono<AssociateOrganizationUserResponse> associateUser(AssociateOrganizationUserRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organizations/associate_user_with_the_organization_by_username.html">Associate User with the Organization by Username</a>
     * request
     *
     * @param request the Associate User with the Organization by Username request
     * @return the response from the Associate User with the Organization by Username request
     */
    Mono<AssociateOrganizationUserByUsernameResponse> associateUserByUsername(AssociateOrganizationUserByUsernameRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organizations/creating_an_organization.html">Creating an Organization</a> request
     *
     * @param request the Creating an Organization request
     * @return the response from the Creating an Organization request
     */
    Mono<CreateOrganizationResponse> create(CreateOrganizationRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organizations/delete_a_particular_organization.html">Delete a Particular Organization</a> request
     *
     * @param request the Delete a Particular Organization request
     * @return the response from the Delete a Particular Organization request
     */
    Mono<DeleteOrganizationResponse> delete(DeleteOrganizationRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organizations/retrieve_a_particular_organization.html">Retrieve a Particular Organization</a> request
     *
     * @param request the Retrieve a Particular Organization request
     * @return the response from the Retrieve a Particular Organization request
     */
    Mono<GetOrganizationResponse> get(GetOrganizationRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organizations/retrieving_organization_instance_usage.html">Retrieving organization instance usage</a> request
     *
     * @param request the Retrieving organization instance usage request
     * @return the response from the Retrieving organization instance usage request
     */
    Mono<GetOrganizationInstanceUsageResponse> getInstanceUsage(GetOrganizationInstanceUsageRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organizations/retrieving_organization_memory_usage.html">Retrieving organization memory usage</a> request
     *
     * @param request the Retrieving organization memory usage request
     * @return the response from the Retrieving organization memory usage request
     */
    Mono<GetOrganizationMemoryUsageResponse> getMemoryUsage(GetOrganizationMemoryUsageRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organizations/retrieving_the_roles_of_all_users_in_the_organization.html">Retrieving the roles of all Users in the
     * Organization</a> request
     *
     * @param request the Retrieving the roles of all Users in the Organization request
     * @return the response from the Retrieving the roles of all Users in the Organization request
     */
    Mono<GetOrganizationUserRolesResponse> getUserRoles(GetOrganizationUserRolesRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organizations/list_all_organizations.html">List Organizations</a> request
     *
     * @param request the List Organizations request
     * @return the response from the List Organizations request
     */
    Mono<ListOrganizationsResponse> list(ListOrganizationsRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organizations/list_all_auditors_for_the_organization.html">List all Auditors for the Organization</a> request
     *
     * @param request the List all Auditors for the Organization request
     * @return the response from the List all Auditors for the Organization request
     */
    Mono<ListOrganizationAuditorsResponse> listAuditors(ListOrganizationAuditorsRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organizations/list_all_billing_managers_for_the_organization.html">List all Billing Managers for the Organization</a> request
     *
     * @param request the List all Billing Managers for the Organization request
     * @return the response from the List all Billing Managers for the Organization request
     */
    Mono<ListOrganizationBillingManagersResponse> listBillingManagers(ListOrganizationBillingManagersRequest request);

    /**
     * <a href="https://apidocs.cloudfoundry.org/latest-release/organizations/list_all_domains_for_the_organization_%28deprecated%29.html">List all Domains for the Organization</a> request
     *
     * @param request the List all Domains for the Organization request
     * @return the response from the List all Domains for the Organization request
     */
    @Deprecated
    Mono<ListOrganizationDomainsResponse> listDomains(ListOrganizationDomainsRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organizations/list_all_managers_for_the_organization.html">List all Managers for the Organization</a> request
     *
     * @param request the List all Managers for the Organization request
     * @return the response from the List all Managers for the Organization request
     */
    Mono<ListOrganizationManagersResponse> listManagers(ListOrganizationManagersRequest request);

    /**
     * <a href="https://apidocs.cloudfoundry.org/latest-release/organizations/list_all_private_domains_for_the_organization.html">List all Private Domains for the Organization</a> request
     *
     * @param request the List all Private Domains for the Organization request
     * @return the response from the List all Private Domains for the Organization request
     */
    Mono<ListOrganizationPrivateDomainsResponse> listPrivateDomains(ListOrganizationPrivateDomainsRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organizations/list_all_services_for_the_organization.html">List all Services for the Organization</a> request
     *
     * @param request the List all Services for the Organization request
     * @return the response from the List all Services for the Organization request
     */
    Mono<ListOrganizationServicesResponse> listServices(ListOrganizationServicesRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organizations/list_all_space_quota_definitions_for_the_organization.html">List all Space Quota Definitions for the
     * Organization</a> request
     *
     * @param request the List all Space Quota Definitions for the Organization request
     * @return the response from the List all Space Quota Definitions for the Organization request
     */
    Mono<ListOrganizationSpaceQuotaDefinitionsResponse> listSpaceQuotaDefinitions(ListOrganizationSpaceQuotaDefinitionsRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organizations/list_all_spaces_for_the_organization.html">List all Spaces for the Organization</a> request
     *
     * @param request the List all Spaces for the Organization request
     * @return the response from the List all Spaces for the Organization request
     */
    Mono<ListOrganizationSpacesResponse> listSpaces(ListOrganizationSpacesRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organizations/list_all_users_for_the_organization.html">List all Users for the Organization</a> request
     *
     * @param request the List all Users for the Organization request
     * @return the response from the List all Users for the Organization request
     */
    Mono<ListOrganizationUsersResponse> listUsers(ListOrganizationUsersRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organizations/remove_auditor_from_the_organization.html">Remove Auditor from the Organization</a> request
     *
     * @param request the Remove Auditor from the Organization request
     * @return the response from the Remove Auditor from the Organization request
     */
    Mono<Void> removeAuditor(RemoveOrganizationAuditorRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organizations/disassociate_auditor_with_the_organization_by_username.html">Disassociate Auditor with the Organization by
     * Username</a> request
     *
     * @param request the Remove Auditor with the Organization By Username request
     * @return the response from the Associate Billing Manager with the Organization request
     */
    Mono<Void> removeAuditorByUsername(RemoveOrganizationAuditorByUsernameRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organizations/remove_billing_manager_from_the_organization.html">Remove Billing Manager from the Organization</a> request
     *
     * @param request the Remove Billing Manager from the Organization request
     * @return the response from the Remove Billing Manager from the Organization request
     */
    Mono<Void> removeBillingManager(RemoveOrganizationBillingManagerRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organizations/disassociate_billing_manager_with_the_organization_by_username.html">Disassociate Billing Manager with the
     * Organization by Username</a> request
     *
     * @param request the Disassociate Billing Manager with the Organization by Username request
     * @return the response from the Disassociate Billing Manager with the Organization by Username request
     */
    Mono<Void> removeBillingManagerByUsername(RemoveOrganizationBillingManagerByUsernameRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organizations/remove_manager_from_the_organization.html">Remove Manager from the Organization</a> request
     *
     * @param request the Remove Manager from the Organization request
     * @return the response from the Remove Manager from the Organization request
     */
    Mono<Void> removeManager(RemoveOrganizationManagerRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organizations/disassociate_manager_with_the_organization_by_username.html">Disassociate Manager with the Organization by
     * Username</a> request
     *
     * @param request the Disassociate Manager with the Organization by Username request
     * @return the response from the Disassociate Manager with the Organization by Username request
     */
    Mono<Void> removeManagerByUsername(RemoveOrganizationManagerByUsernameRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organizations/remove_private_domain_from_the_organization.html">Remove Private Domain from the Organization</a> request
     *
     * @param request the Remove Private Domain from the Organization request
     * @return the response from the Remove Private Domain from the Organization request
     */
    Mono<Void> removePrivateDomain(RemoveOrganizationPrivateDomainRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organizations/remove_user_from_the_organization.html">Remove User from the Organization</a> request
     *
     * @param request the Remove User from the Organization request
     * @return the response from the Remove User from the Organization request
     */
    Mono<Void> removeUser(RemoveOrganizationUserRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organizations/disassociate_user_with_the_organization_by_username.html">Disassociate User with the Organization by Username</a>
     * request
     *
     * @param request the Disassociate User with the Organization by Username request
     * @return the response from the Disassociate User with the Organization by Username request
     */
    Mono<Void> removeUserByUsername(RemoveOrganizationUserByUsernameRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organizations/get_organization_summary.html">Get Organization summary</a> request
     *
     * @param request the Organization summary request
     * @return the response from the Organization summary request
     */
    Mono<SummaryOrganizationResponse> summary(SummaryOrganizationRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organizations/update_an_organization.html">Update an Organization</a> request
     *
     * @param request the Update an Organization request
     * @return the response from the Update an Organization request
     */
    Mono<UpdateOrganizationResponse> update(UpdateOrganizationRequest request);

}
