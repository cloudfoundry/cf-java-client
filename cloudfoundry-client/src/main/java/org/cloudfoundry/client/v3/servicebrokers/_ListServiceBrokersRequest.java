package org.cloudfoundry.client.v3.servicebrokers;

import java.util.List;

import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.FilterParameter;
import org.cloudfoundry.client.v3.PaginatedRequest;
import org.immutables.value.Value;

@Value.Immutable
abstract class _ListServiceBrokersRequest extends PaginatedRequest {

    @FilterParameter("label_selector")
    @Nullable
    abstract String getLabelSelector();
    
    @FilterParameter("names")
    @Nullable
    abstract List<String> getNames();
    
    @FilterParameter("space_guids")
    @Nullable
    abstract List<String> getSpaceIds();
    
}
