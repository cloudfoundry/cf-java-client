package org.cloudfoundry.client.v3.quotas.organizations;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.cloudfoundry.client.v3.ToManyRelationship;
import org.immutables.value.Value;

/**
 * The request payload to apply an Organization Quota to an Organization
 */
@JsonSerialize
@Value.Immutable
abstract class _ApplyOrganizationQuotaRequest {

    /**
     * The Organization Quota id
     */
    @JsonIgnore
    abstract String getOrganizationQuotaId();

    /**
     * A relationship to the organizations where the quota is applied
     * Use of JsonUnwrapped to inline the organization relationships as per the API spec
     */
    @JsonUnwrapped
    abstract ToManyRelationship organizationRelationships();
}
