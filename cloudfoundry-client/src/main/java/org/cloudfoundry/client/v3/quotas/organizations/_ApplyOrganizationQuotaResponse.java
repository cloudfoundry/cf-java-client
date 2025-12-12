package org.cloudfoundry.client.v3.quotas.organizations;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.AllowNulls;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.ToManyRelationship;
import org.immutables.value.Value;

import java.util.Map;

/**
 * The response payload for applying an Organization Quota to an Organization
 */
@JsonDeserialize
@Value.Immutable
abstract class _ApplyOrganizationQuotaResponse {

    @JsonUnwrapped
    abstract ToManyRelationship organizationRelationships();

    /**
     * Links to related resources and actions for the resource
     */
    @AllowNulls
    @JsonProperty("links")
    public abstract Map<String, Link> getLinks();
}
