package org.cloudfoundry.client.v3.routes;

import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.FilterParameter;
import org.cloudfoundry.client.v3.PaginatedRequest;
import org.immutables.value.Value;

import java.util.List;

/**
 * The request payload for the List Routes operation
 */
@Value.Immutable
abstract class _ListRoutesRequest extends PaginatedRequest {

    /**
     * The application ids filter
     */
    @FilterParameter("app_guids")
    abstract List<String> getApplicationIds();

    /**
     * The domain ids filter
     */
    @FilterParameter("domain_guids")
    abstract List<String> getDomainIds();

    /**
     * The hosts filter
     */
    @FilterParameter("hosts")
    abstract List<String> getHosts();

    /**
     * A query string containing a list of label selector requirements
     */
    @FilterParameter("label_selector")
    @Nullable
    abstract String getLabelSelector();

    /**
     * The organization ids filter
     */
    @FilterParameter("organization_guids")
    abstract List<String> getOrganizationIds();

    /**
     * The paths filter
     */
    @FilterParameter("paths")
    abstract List<String> getPaths();

    /**
     * The ports filter
     */
    @FilterParameter("ports")
    abstract List<Integer> getPorts();

    /**
     * The space ids filter
     */
    @FilterParameter("space_guids")
    abstract List<String> getSpaceIds();

}
