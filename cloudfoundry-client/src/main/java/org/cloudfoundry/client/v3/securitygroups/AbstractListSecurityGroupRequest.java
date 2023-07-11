package org.cloudfoundry.client.v3.securitygroups;

import java.util.List;

import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v2.PaginatedRequest;
import org.cloudfoundry.client.v3.FilterParameter;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class AbstractListSecurityGroupRequest extends PaginatedRequest {

    /**
     * The Space id
     */
    @JsonIgnore
    abstract String getSpaceId();

    /**
     * The security group ids filter
     */
    @FilterParameter("guids")
    @Nullable
    abstract List<String> getSecurityGroupIds();

    /**
     * The security group names filter
     */
    @FilterParameter("names")
    @Nullable
    abstract List<String> getNames();

}
