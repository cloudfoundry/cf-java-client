package org.cloudfoundry.client.v3.servicebrokers;

import org.cloudfoundry.AllowNulls;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.Metadata;
import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@Value.Immutable
abstract class _CreateServiceBrokerRequest {

    @JsonProperty("authentication")
    abstract Authentication getAuthentication();
    
    @JsonProperty("name")
    abstract String getName();
    
    @AllowNulls
    @JsonProperty("metadata")
    @Nullable
    abstract Metadata getMetadata();
    
    @JsonProperty("relationships")
    @Nullable
    abstract ServiceBrokerRelationships getRelationships();
    
    @JsonProperty("url")
    abstract String getUrl();
    
}
