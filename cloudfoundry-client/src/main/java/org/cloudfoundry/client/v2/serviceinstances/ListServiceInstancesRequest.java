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

package org.cloudfoundry.client.v2.serviceinstances;

import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;
import org.cloudfoundry.client.v2.FilterParameter;
import org.cloudfoundry.client.v2.PaginatedRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * The request payload for the List Service Instances operation
 *
 * <p><b>This class is NOT threadsafe.</b>
 */
public final class ListServiceInstancesRequest extends PaginatedRequest<ListServiceInstancesRequest>
        implements Validatable {

    private final List<String> gatewayNames = new ArrayList<>();

    private final List<String> names = new ArrayList<>();

    private final List<String> organizationIds = new ArrayList<>();

    private final List<String> serviceKeyIds = new ArrayList<>();

    private final List<String> serviceBindingIds = new ArrayList<>();

    private final List<String> servicePlanIds = new ArrayList<>();

    private final List<String> spaceIds = new ArrayList<>();

    /**
     * Returns the gateway names
     *
     * @return the gateway names
     */
    @FilterParameter("gateway_name")
    public List<String> getGatewayNames() {
        return this.gatewayNames;
    }

    /**
     * Configure the gateway name
     *
     * @param gatewayName a gateway name
     * @return {@code this}
     */
    public ListServiceInstancesRequest withGatewayName(String gatewayName) {
        this.gatewayNames.add(gatewayName);
        return this;
    }

    /**
     * Configure the gateway names
     *
     * @param gatewayNames the gateway names
     * @return {@code this}
     */
    public ListServiceInstancesRequest withGatewayNames(List<String> gatewayNames) {
        this.gatewayNames.addAll(gatewayNames);
        return this;
    }

    /**
     * Returns the names
     *
     * @return the names
     */
    @FilterParameter("name")
    public List<String> getNames() {
        return this.names;
    }

    /**
     * Configure the name
     *
     * @param name the name
     * @return {@code this}
     */
    public ListServiceInstancesRequest withName(String name) {
        this.names.add(name);
        return this;
    }

    /**
     * Configure the names
     *
     * @param names the names
     * @return {@code this}
     */
    public ListServiceInstancesRequest withNames(List<String> names) {
        this.names.addAll(names);
        return this;
    }

    /**
     * Returns the organization ids
     *
     * @return the organization ids
     */
    @FilterParameter("organization_guid")
    public List<String> getOrganizationIds() {
        return this.organizationIds;
    }

    /**
     * Configure the organization id
     *
     * @param organizationId the organization id
     * @return {@code this}
     */
    public ListServiceInstancesRequest withOrganizationId(String organizationId) {
        this.organizationIds.add(organizationId);
        return this;
    }

    /**
     * Configure the organization ids
     *
     * @param organizationIds the organization ids
     * @return {@code this}
     */
    public ListServiceInstancesRequest withOrganizationIds(List<String> organizationIds) {
        this.organizationIds.addAll(organizationIds);
        return this;
    }

    /**
     * Returns the service key ids
     *
     * @return the service key ids
     */
    @FilterParameter("service_key_guid")
    public List<String> getServiceKeyIds() {
        return this.serviceKeyIds;
    }

    /**
     * Configure the service key id
     *
     * @param serviceKeyId the service key id
     * @return {@code this}
     */
    public ListServiceInstancesRequest withServiceKeyId(String serviceKeyId) {
        this.serviceKeyIds.add(serviceKeyId);
        return this;
    }

    /**
     * Configure the service key ids
     *
     * @param serviceKeyIds the service key ids
     * @return {@code this}
     */
    public ListServiceInstancesRequest withServiceKeyIds(List<String> serviceKeyIds) {
        this.serviceKeyIds.addAll(serviceKeyIds);
        return this;
    }

    /**
     * Returns the service binding ids
     *
     * @return the service binding ids
     */
    @FilterParameter("service_binding_guid")
    public List<String> getServiceBindingIds() {
        return this.serviceBindingIds;
    }

    /**
     * Configure the service binding id
     *
     * @param serviceBindingId the service binding id
     * @return {@code this}
     */
    public ListServiceInstancesRequest withServiceBindingId(String serviceBindingId) {
        this.serviceBindingIds.add(serviceBindingId);
        return this;
    }

    /**
     * Configure the service binding ids
     *
     * @param serviceBindingIds the service binding ids
     * @return {@code this}
     */
    public ListServiceInstancesRequest withServiceBindingIds(List<String> serviceBindingIds) {
        this.serviceBindingIds.addAll(serviceBindingIds);
        return this;
    }

    /**
     * Returns the service plan ids
     *
     * @return the service plan ids
     */
    @FilterParameter("service_plan_guid")
    public List<String> getServicePlanIds() {
        return this.servicePlanIds;
    }

    /**
     * Configure the service plan id
     *
     * @param servicePlanId the service plan id
     * @return {@code this}
     */
    public ListServiceInstancesRequest withServicePlanId(String servicePlanId) {
        this.servicePlanIds.add(servicePlanId);
        return this;
    }

    /**
     * Configure the service plan ids
     *
     * @param servicePlanIds the service plan ids
     * @return {@code this}
     */
    public ListServiceInstancesRequest withServicePlanIds(List<String> servicePlanIds) {
        this.servicePlanIds.addAll(servicePlanIds);
        return this;
    }

    /**
     * Returns the space ids
     *
     * @return the space ids
     */
    @FilterParameter("space_guid")
    public List<String> getSpaceIds() {
        return this.spaceIds;
    }

    /**
     * Configure the space id
     *
     * @param spaceId the space id
     * @return {@code this}
     */
    public ListServiceInstancesRequest withSpaceId(String spaceId) {
        this.spaceIds.add(spaceId);
        return this;
    }

    /**
     * Configure the space ids
     *
     * @param spaceIds the space ids
     * @return {@code this}
     */
    public ListServiceInstancesRequest withSpaceIds(List<String> spaceIds) {
        this.spaceIds.addAll(spaceIds);
        return this;
    }

    @Override
    public ValidationResult isValid() {
        return new ValidationResult();
    }
}
