package org.cloudfoundry.client.v3.servicebrokers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Map;

@JsonSerialize
abstract class Authentication {

    @JsonProperty("credentials")
    abstract Map<String, Object> getCredentials();

    @JsonProperty("type")
    abstract String getType();
}
