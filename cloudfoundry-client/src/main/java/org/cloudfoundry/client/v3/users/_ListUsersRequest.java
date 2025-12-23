

package org.cloudfoundry.client.v3.users;

import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.FilterParameter;
import org.cloudfoundry.client.v3.PaginatedRequest;
import org.immutables.value.Value;

import java.util.List;

/**
 * The request payload for the List Users operation.
 */
@Value.Immutable
abstract class _ListUsersRequest extends PaginatedRequest {

    /**
     * Comma-delimited list of user guids to filter by
     */
    @FilterParameter("guids")
    @Nullable
    abstract List<String> getGuids();

    /**
     * Comma-delimited list of usernames to filter by. Mutually exclusive with partial_usernames
     */
    @FilterParameter("usernames")
    abstract List<String> getUsernames();

    /**
     * Comma-delimited list of strings to search by. When using this query parameter, all the users that contain the string provided in their username will be returned. Mutually exclusive with usernames
     */
    @FilterParameter("partial_usernames")
    abstract List<String> getPartialUsernames();

    /**
     * Comma-delimited list of user origins (user stores) to filter by, for example, users authenticated by UAA have the origin “uaa”; users authenticated by an LDAP provider have the origin “ldap”; when filtering by origins, usernames must be included
     */
    @FilterParameter("origins")
    abstract List<String> getOrigins();

    /**
     * A query string containing a list of label selector requirements
     */
    @FilterParameter("label_selector")
    @Nullable
    abstract String getLabelSelector();
}
