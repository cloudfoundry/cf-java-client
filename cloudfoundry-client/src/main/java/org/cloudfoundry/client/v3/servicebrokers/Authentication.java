package org.cloudfoundry.client.v3.servicebrokers;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
abstract class Authentication {

    @JsonProperty("credentials")
    abstract Map<String, Object> getCredentials();
    
    @JsonProperty("type")
    abstract String getType();
    
}
