package org.cloudfoundry.client.v3.users;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.client.v3.PaginatedResponse;
import org.cloudfoundry.client.v3.users.UserResource;
import org.immutables.value.Value;

/**
 * The response payload for the List Users operation.
 */
@JsonDeserialize
@Value.Immutable
abstract class _ListUsersResponse extends PaginatedResponse<UserResource> {

}
