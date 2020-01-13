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

package org.cloudfoundry.client.v2.users;

import reactor.core.publisher.Mono;

public interface Users {

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/users/associate_audited_organization_with_the_user.html">Associate Audited Organization with the User</a> request
     *
     * @param request the Associate Audited Organization with the User request
     * @return the response from the Associate Audited Organization with the User request
     */
    Mono<AssociateUserAuditedOrganizationResponse> associateAuditedOrganization(AssociateUserAuditedOrganizationRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/users/associate_audited_space_with_the_user.html">Associate Audited Space with the User</a> request
     *
     * @param request the Associate Audited Space with the User request
     * @return the response from the Associate Audited Space with the User request
     */
    Mono<AssociateUserAuditedSpaceResponse> associateAuditedSpace(AssociateUserAuditedSpaceRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/users/associate_billing_managed_organization_with_the_user.html">Associate Billing Managed Organization with the User</a>
     * request
     *
     * @param request the Associate Billing Managed Organization with the User request
     * @return the response from the Associate Billing Managed Organization with the User request
     */
    Mono<AssociateUserBillingManagedOrganizationResponse> associateBillingManagedOrganization(AssociateUserBillingManagedOrganizationRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/users/associate_managed_organization_with_the_user.html">Associate Managed Organization with the User</a> request
     *
     * @param request the Associate Managed Organization with the User request
     * @return the response from the Associate Managed Organization with the User request
     */
    Mono<AssociateUserManagedOrganizationResponse> associateManagedOrganization(AssociateUserManagedOrganizationRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/users/associate_managed_space_with_the_user.html">Associate Managed Space with the User</a> request
     *
     * @param request the Associate Managed Space with the User request
     * @return the response from the Associate Managed Space with the User request
     */
    Mono<AssociateUserManagedSpaceResponse> associateManagedSpace(AssociateUserManagedSpaceRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/users/associate_organization_with_the_user.html">Associate Organization with the User</a> request
     *
     * @param request the Associate Organization with the User request
     * @return the response from the Associate Organization with the User request
     */
    Mono<AssociateUserOrganizationResponse> associateOrganization(AssociateUserOrganizationRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/users/associate_space_with_the_user.html">Associate Space with the User</a> request
     *
     * @param request the Associate Space with the User request
     * @return the response from the Associate Space with the User request
     */
    Mono<AssociateUserSpaceResponse> associateSpace(AssociateUserSpaceRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/users/creating_a_user.html">Creating a User</a> request
     *
     * @param request the Creating a User request
     * @return the response from the Creating a User request
     */
    Mono<CreateUserResponse> create(CreateUserRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/users/delete_a_particular_user.html">Delete a Particular User</a> request
     *
     * @param request the Delete a Particular User request
     * @return the response from the Delete a Particular User request
     */
    Mono<DeleteUserResponse> delete(DeleteUserRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/users/retrieve_a_particular_user.html">Retrieve a Particular User</a> request
     *
     * @param request the Retrieve a Particular User request
     * @return the response from the Retrieve a Particular User request
     */
    Mono<GetUserResponse> get(GetUserRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/users/list_all_users.html">List all Users</a> request
     *
     * @param request the List all Users request
     * @return the response from the List all Users request
     */
    Mono<ListUsersResponse> list(ListUsersRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/users/list_all_audited_organizations_for_the_user.html">List all Audited Organizations for the User</a> request
     *
     * @param request the List all Audited Organizations for the User request
     * @return the response from the List all Audited Organizations for the User request
     */
    Mono<ListUserAuditedOrganizationsResponse> listAuditedOrganizations(ListUserAuditedOrganizationsRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/users/list_all_audited_spaces_for_the_user.html">List all Audited Spaces for the User</a> request
     *
     * @param request the List all Audited Spaces for the User request
     * @return the response from the List all Audited Spaces for the User request
     */
    Mono<ListUserAuditedSpacesResponse> listAuditedSpaces(ListUserAuditedSpacesRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/users/list_all_billing_managed_organizations_for_the_user.html">List all Billing Managed Organizations for the User</a> request
     *
     * @param request the List all Billing Managed Organizations for the User request
     * @return the response from the List all Billing Managed Organizations for the User request
     */
    Mono<ListUserBillingManagedOrganizationsResponse> listBillingManagedOrganizations(ListUserBillingManagedOrganizationsRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/users/list_all_managed_organizations_for_the_user.html">List all Managed Organizations for the User</a> request
     *
     * @param request the List all Managed Organizations for the User request
     * @return the response from the List all Managed Organizations for the User request
     */
    Mono<ListUserManagedOrganizationsResponse> listManagedOrganizations(ListUserManagedOrganizationsRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/users/list_all_managed_spaces_for_the_user.html">List all Managed Spaces for the User</a> request
     *
     * @param request the List all Managed Spaces for the User request
     * @return the response from the List all Managed Spaces for the User request
     */
    Mono<ListUserManagedSpacesResponse> listManagedSpaces(ListUserManagedSpacesRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/users/list_all_organizations_for_the_user.html">List all Organizations for the User</a> request
     *
     * @param request the List all Organizations for the User request
     * @return the response from the List all Organizations for the User request
     */
    Mono<ListUserOrganizationsResponse> listOrganizations(ListUserOrganizationsRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/users/list_all_spaces_for_the_user.html">List all Spaces for the User</a> request
     *
     * @param request the List all Spaces for the User request
     * @return the response from the List all Spaces for the User request
     */
    Mono<ListUserSpacesResponse> listSpaces(ListUserSpacesRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/users/remove_audited_organization_from_the_user.html">Remove Audited Organization from the User</a> request
     *
     * @param request the Remove Audited Organization from the User request
     * @return the response from the Remove Audited Organization from the User request
     */
    Mono<Void> removeAuditedOrganization(RemoveUserAuditedOrganizationRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/users/remove_managed_space_from_the_user.html">Remove Audited Space from the User</a> request
     *
     * @param request the Remove Audited Space from the User request
     * @return the response from the Remove Audited Space from the User request
     */
    Mono<Void> removeAuditedSpace(RemoveUserAuditedSpaceRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/users/remove_billing_managed_organization_from_the_user.html">Remove Managed Billing Organization from the User</a> request
     *
     * @param request the Remove Billing Managed Organization from the User request
     * @return the response from the Remove Billing Managed Organization from the User request
     */
    Mono<Void> removeBillingManagedOrganization(RemoveUserBillingManagedOrganizationRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/users/remove_managed_organization_from_the_user.html">Remove Managed Organization from the User</a> request
     *
     * @param request the Remove Managed Organization from the User request
     * @return the response from the Remove Managed Organization from the User request
     */
    Mono<Void> removeManagedOrganization(RemoveUserManagedOrganizationRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/users/remove_managed_space_from_the_user.html">Remove Managed Space from the User</a> request
     *
     * @param request the Remove Managed Space from the User request
     * @return the response from the Remove Managed Space from the User request
     */
    Mono<Void> removeManagedSpace(RemoveUserManagedSpaceRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/users/remove_organization_from_the_user.html">Remove Organization from the User</a> request
     *
     * @param request the Remove Organization from the User request
     * @return the response from the Remove Organization from the User request
     */
    Mono<Void> removeOrganization(RemoveUserOrganizationRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/users/associate_space_with_the_user.html">Remove Space from the User</a> request
     *
     * @param request the Remove Space from the User request
     * @return the response from the Remove Space from the User request
     */
    Mono<Void> removeSpace(RemoveUserSpaceRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/users/get_user_summary.html">Get User Summary</a> request
     *
     * @param request the Get User summary request
     * @return the response from the Get User summary request
     */
    Mono<SummaryUserResponse> summary(SummaryUserRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/users/updating_a_user.html">Updating a User</a> request
     *
     * @param request the Updating a User request
     * @return the response from the Updating a User request
     */
    Mono<UpdateUserResponse> update(UpdateUserRequest request);

}
