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
public abstract class _ListRoutesRequest extends PaginatedRequest {

    /**
     * The hosts
     */
    @FilterParameter("hosts")
    abstract List<String> getHosts();

    /**
     * The paths
     */
    @FilterParameter("paths")
    abstract List<String> getPaths();


    /**
     * The domain guids
     */
    @FilterParameter("domain_guids")
    abstract List<String> getDomainIds();


    /**
     * The space guids
     */
    @FilterParameter("space_guids")
    abstract List<String> getSpaceIds();

    /**
     * The organization guids
     */
    @FilterParameter("organization_guids")
    abstract List<String> getOrganizationIds();

    /**
     * The metadata query
     */
    @FilterParameter("label_selector")
    @Nullable
    abstract String getLabelSelector();

}
