package org.cloudfoundry.client.v3.applications;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.FilterParameter;
import org.cloudfoundry.client.v3.PaginatedRequest;
import org.immutables.value.Value;
import org.immutables.value.internal.$processor$.meta.$BuilderMirrors;

import java.util.List;

@Value.Immutable
public abstract class _ListApplicationRoutesRequest extends PaginatedRequest {

    /**
     * The application id
     */
    @JsonIgnore
    abstract String getApplicationId();

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
    abstract List<String> getDomainGuids();

    /**
     * The space guids
     */
    @FilterParameter("space_guids")
    abstract List<String> getSpaceGuids();

    /**
     * The organization guids
     */
    @FilterParameter("organization_guids")
    abstract List<String> getOrganizationGuids();

    /**
     * The metadata query
     */
    @FilterParameter("label_selector")
    @Nullable
    abstract String getLabelSelector();
}
