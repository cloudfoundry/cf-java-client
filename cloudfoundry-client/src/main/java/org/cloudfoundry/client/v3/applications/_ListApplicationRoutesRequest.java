package org.cloudfoundry.client.v3.applications;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.FilterParameter;
import org.cloudfoundry.client.v3.PaginatedRequest;
import org.immutables.value.Value;

import java.util.List;

/**
 * The request payload for the List Application Routes operation
 */
@Value.Immutable
abstract class _ListApplicationRoutesRequest extends PaginatedRequest {

    /**
     * The application id
     */
    @JsonIgnore
    abstract String getApplicationId();

    /**
     * The domain guids
     */
    @FilterParameter("domain_guids")
    abstract List<String> getDomainIds();

    /**
     * The hosts
     */
    @FilterParameter("hosts")
    abstract List<String> getHosts();

    /**
     * The label selector
     */
    @FilterParameter("label_selector")
    @Nullable
    abstract String getLabelSelector();

    /**
     * The organization ids
     */
    @FilterParameter("organization_guids")
    abstract List<String> getOrganizationIds();

    /**
     * The paths
     */
    @FilterParameter("paths")
    abstract List<String> getPaths();

    /**
     * The ports
     */
    @FilterParameter("ports")
    abstract List<Integer> getPorts();

    /**
     * The space ids
     */
    @FilterParameter("space_guids")
    abstract List<String> getSpaceIds();
}
