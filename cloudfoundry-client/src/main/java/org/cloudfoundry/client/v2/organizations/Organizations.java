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
    Publisher<AssociatePrivateDomainResponse> associatePrivateDomain(AssociatePrivateDomainRequest request);

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
     * <a href="http://apidocs.cloudfoundry.org/214/organizations/list_all_managers_for_the_organization.html">List
     * all Managers for the Organization</a> request
     *
     * @param request the List all Managers for the Organization request
     * @return the response from the List all Managers for the Organization request
     */
    Publisher<ListOrganizationManagersResponse> listManagers(ListOrganizationManagersRequest request);

}
