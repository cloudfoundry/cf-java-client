package org.cloudfoundry.client.v3.servicebrokers;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.AllowNulls;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.Metadata;
import org.cloudfoundry.client.v3.Resource;

public abstract class ServiceBroker extends Resource {

    @AllowNulls
    @JsonProperty("metadata")
    @Nullable
    public abstract Metadata getMetadata();

    @JsonProperty("name")
    public abstract String getName();

    @JsonProperty("relationships")
    @Nullable
    public abstract ServiceBrokerRelationships getRelationships();

    @JsonProperty("url")
    public abstract String getUrl();
}
