package org.cloudfoundry.client.v3.routes;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.AllowNulls;
import org.immutables.value.Value;

/**
 * Represents a Route Options
 */
@JsonDeserialize
@Value.Immutable
abstract class _RouteOptions {

    /**
     * All route options, including unknown future keys.
     */
    @JsonAnyGetter
    @JsonProperty("options")
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @AllowNulls
    public abstract Map<String, Object> getValues();

    @JsonIgnore
    @Value.Derived
    public Optional<String> getLoadbalancing() {
        return getString("loadbalancing");
    }

    @JsonIgnore
    @Value.Derived
    public Optional<String> getHashHeader() {
        return getString("hash_header");
    }

    @JsonIgnore
    @Value.Derived
    public Optional<String> getHashBalance() {
        return getString("hash_balance");
    }

    @JsonIgnore
    public Optional<Object> get(String key) {
        return Optional.ofNullable(getValues().get(key));
    }

    private Optional<String> getString(String key) {
        Object value = getValues().get(key);
        return value instanceof String ? Optional.of((String) value) : Optional.empty();
    }

}
