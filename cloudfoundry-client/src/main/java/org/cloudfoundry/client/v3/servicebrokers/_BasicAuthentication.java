package org.cloudfoundry.client.v3.servicebrokers;

import java.util.HashMap;
import java.util.Map;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Value.Immutable
abstract class _BasicAuthentication extends Authentication {

    @JsonIgnore
    abstract String getUsername();
    
    @JsonIgnore
    abstract String getPassword();
    
    @Value.Derived
    Map<String, Object> getCredentials() {
	Map<String, Object> credentials = new HashMap<>();
	credentials.put("username", getUsername());
	credentials.put("password", getPassword());
	return credentials;
    }

    @Value.Derived
    String getType() {
	return "basic";
    }

    
}
