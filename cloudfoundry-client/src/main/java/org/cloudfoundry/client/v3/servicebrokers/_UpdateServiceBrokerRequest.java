package org.cloudfoundry.client.v3.servicebrokers;

import org.cloudfoundry.AllowNulls;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.Metadata;
import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@Value.Immutable
abstract class _UpdateServiceBrokerRequest {

    @JsonProperty("authentication")
    @Nullable
    abstract Authentication getAuthentication();
    
    @JsonProperty("name")
    @Nullable
    abstract String getName();
    
    @AllowNulls
    @JsonProperty("metadata")
    @Nullable
    abstract Metadata getMetadata();
    
    @JsonIgnore
    abstract String getServiceBrokerId();
    
    @JsonProperty("url")
    @Nullable
    abstract String getUrl();
    
}
