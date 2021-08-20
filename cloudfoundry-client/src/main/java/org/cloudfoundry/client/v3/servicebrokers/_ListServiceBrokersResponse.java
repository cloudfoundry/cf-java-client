package org.cloudfoundry.client.v3.servicebrokers;

import org.cloudfoundry.client.v3.PaginatedResponse;
import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize
@Value.Immutable
abstract class _ListServiceBrokersResponse extends PaginatedResponse<ServiceBrokerResource> {

}
