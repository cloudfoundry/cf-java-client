package org.cloudfoundry;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import java.time.Duration;
import org.cloudfoundry.reactor.uaa.ReactorUaaClient;
import org.cloudfoundry.uaa.UaaClient;
import org.cloudfoundry.uaa.authorizations.Authorizations;
import org.cloudfoundry.uaa.clients.Clients;
import org.cloudfoundry.uaa.groups.AddMemberRequest;
import org.cloudfoundry.uaa.groups.AddMemberResponse;
import org.cloudfoundry.uaa.groups.CheckMembershipRequest;
import org.cloudfoundry.uaa.groups.CheckMembershipResponse;
import org.cloudfoundry.uaa.groups.CreateGroupRequest;
import org.cloudfoundry.uaa.groups.CreateGroupResponse;
import org.cloudfoundry.uaa.groups.DeleteGroupRequest;
import org.cloudfoundry.uaa.groups.DeleteGroupResponse;
import org.cloudfoundry.uaa.groups.GetGroupRequest;
import org.cloudfoundry.uaa.groups.GetGroupResponse;
import org.cloudfoundry.uaa.groups.Groups;
import org.cloudfoundry.uaa.groups.ListExternalGroupMappingsRequest;
import org.cloudfoundry.uaa.groups.ListExternalGroupMappingsResponse;
import org.cloudfoundry.uaa.groups.ListGroupsRequest;
import org.cloudfoundry.uaa.groups.ListGroupsResponse;
import org.cloudfoundry.uaa.groups.ListMembersRequest;
import org.cloudfoundry.uaa.groups.ListMembersResponse;
import org.cloudfoundry.uaa.groups.MapExternalGroupRequest;
import org.cloudfoundry.uaa.groups.MapExternalGroupResponse;
import org.cloudfoundry.uaa.groups.RemoveMemberRequest;
import org.cloudfoundry.uaa.groups.RemoveMemberResponse;
import org.cloudfoundry.uaa.groups.UnmapExternalGroupByGroupDisplayNameRequest;
import org.cloudfoundry.uaa.groups.UnmapExternalGroupByGroupDisplayNameResponse;
import org.cloudfoundry.uaa.groups.UnmapExternalGroupByGroupIdRequest;
import org.cloudfoundry.uaa.groups.UnmapExternalGroupByGroupIdResponse;
import org.cloudfoundry.uaa.groups.UpdateGroupRequest;
import org.cloudfoundry.uaa.groups.UpdateGroupResponse;
import org.cloudfoundry.uaa.identityproviders.IdentityProviders;
import org.cloudfoundry.uaa.identityzones.IdentityZones;
import org.cloudfoundry.uaa.ratelimit.Ratelimit;
import org.cloudfoundry.uaa.serverinformation.ServerInformation;
import org.cloudfoundry.uaa.tokens.Tokens;
import org.cloudfoundry.uaa.users.ChangeUserPasswordRequest;
import org.cloudfoundry.uaa.users.ChangeUserPasswordResponse;
import org.cloudfoundry.uaa.users.CreateUserRequest;
import org.cloudfoundry.uaa.users.CreateUserResponse;
import org.cloudfoundry.uaa.users.DeleteUserRequest;
import org.cloudfoundry.uaa.users.DeleteUserResponse;
import org.cloudfoundry.uaa.users.ExpirePasswordRequest;
import org.cloudfoundry.uaa.users.ExpirePasswordResponse;
import org.cloudfoundry.uaa.users.GetUserVerificationLinkRequest;
import org.cloudfoundry.uaa.users.GetUserVerificationLinkResponse;
import org.cloudfoundry.uaa.users.InviteUsersRequest;
import org.cloudfoundry.uaa.users.InviteUsersResponse;
import org.cloudfoundry.uaa.users.ListUsersRequest;
import org.cloudfoundry.uaa.users.ListUsersResponse;
import org.cloudfoundry.uaa.users.LookupUserIdsRequest;
import org.cloudfoundry.uaa.users.LookupUserIdsResponse;
import org.cloudfoundry.uaa.users.UpdateUserRequest;
import org.cloudfoundry.uaa.users.UpdateUserResponse;
import org.cloudfoundry.uaa.users.UserInfoRequest;
import org.cloudfoundry.uaa.users.UserInfoResponse;
import org.cloudfoundry.uaa.users.Users;
import org.cloudfoundry.uaa.users.VerifyUserRequest;
import org.cloudfoundry.uaa.users.VerifyUserResponse;
import org.immutables.value.Value;
import reactor.core.publisher.Mono;

public class ThrottlingUaaClient implements UaaClient {

    private final UaaClient delegate;
    private final int maxRequestsPerSecond;
    private final RateLimiter rateLimiter;
    private final ThrottledUsers users;
    private Groups groups;

    /**
     * An {@link UaaClient} implementation that throttles calls to the UAA
     * {@code /Groups} and {@code /Users} endpoints. It uses a single "bucket"
     * for throttling requests to both endpoints.
     *
     * @see <a href="https://resilience4j.readme.io/docs/ratelimiter">resilience4j docs</a>
     */
    public ThrottlingUaaClient(ReactorUaaClient delegate, int maxRequestsPerSecond) {
        // uaaLimit is calls per second. We need the milliseconds for one call because
        // resilience4j uses sliced timeslots, while the uaa server uses a sliding window.
        int clockSkewMillis = 20; // 20ms clock skew is a save value for ~5 requests per second.
        int rateLimitRefreshPeriodMillis = (1000 / maxRequestsPerSecond) + clockSkewMillis;
        this.delegate = delegate;
        this.maxRequestsPerSecond = maxRequestsPerSecond;
        RateLimiterConfig config =
                RateLimiterConfig.custom()
                        .limitForPeriod(1)
                        .limitRefreshPeriod(Duration.ofMillis(rateLimitRefreshPeriodMillis))
                        .timeoutDuration(Duration.ofSeconds(10))
                        .build();
        this.rateLimiter = RateLimiter.of("uaa", config);

        this.users = new ThrottledUsers();
        this.groups = new ThrottledGroups();
    }

    @Override
    public Authorizations authorizations() {
        return this.delegate.authorizations();
    }

    @Override
    public Clients clients() {
        return this.delegate.clients();
    }

    @Override
    public Mono<String> getUsername() {
        return this.delegate.getUsername();
    }

    @Override
    public IdentityProviders identityProviders() {
        return this.delegate.identityProviders();
    }

    @Override
    public IdentityZones identityZones() {
        return this.delegate.identityZones();
    }

    @Override
    public ServerInformation serverInformation() {
        return this.delegate.serverInformation();
    }

    @Override
    public Tokens tokens() {
        return this.delegate.tokens();
    }

    @Override
    public Users users() {
        return users;
    }

    @Override
    public Groups groups() {
        return groups;
    }

    @Override
    @Value.Derived
    public Ratelimit rateLimit() {
        return this.delegate.rateLimit();
    }

    public int getMaxRequestsPerSecond() {
        return maxRequestsPerSecond;
    }

    public class ThrottledUsers implements Users {

        private final Users usersDelegate;

        public ThrottledUsers() {
            this.usersDelegate = delegate.users();
        }

        @Override
        public Mono<ChangeUserPasswordResponse> changePassword(ChangeUserPasswordRequest request) {
            return this.usersDelegate
                    .changePassword(request)
                    .transformDeferred(RateLimiterOperator.of(rateLimiter));
        }

        @Override
        public Mono<CreateUserResponse> create(CreateUserRequest request) {
            return this.usersDelegate
                    .create(request)
                    .transformDeferred(RateLimiterOperator.of(rateLimiter));
        }

        @Override
        public Mono<DeleteUserResponse> delete(DeleteUserRequest request) {
            return this.usersDelegate
                    .delete(request)
                    .transformDeferred(RateLimiterOperator.of(rateLimiter));
        }

        @Override
        public Mono<ExpirePasswordResponse> expirePassword(ExpirePasswordRequest request) {
            return this.usersDelegate
                    .expirePassword(request)
                    .transformDeferred(RateLimiterOperator.of(rateLimiter));
        }

        @Override
        public Mono<GetUserVerificationLinkResponse> getVerificationLink(
                GetUserVerificationLinkRequest request) {
            return this.usersDelegate
                    .getVerificationLink(request)
                    .transformDeferred(RateLimiterOperator.of(rateLimiter));
        }

        @Override
        public Mono<InviteUsersResponse> invite(InviteUsersRequest request) {
            return this.usersDelegate
                    .invite(request)
                    .transformDeferred(RateLimiterOperator.of(rateLimiter));
        }

        @Override
        public Mono<ListUsersResponse> list(ListUsersRequest request) {
            return this.usersDelegate
                    .list(request)
                    .transformDeferred(RateLimiterOperator.of(rateLimiter));
        }

        @Override
        public Mono<LookupUserIdsResponse> lookup(LookupUserIdsRequest request) {
            return this.usersDelegate
                    .lookup(request)
                    .transformDeferred(RateLimiterOperator.of(rateLimiter));
        }

        @Override
        public Mono<UpdateUserResponse> update(UpdateUserRequest request) {
            return this.usersDelegate
                    .update(request)
                    .transformDeferred(RateLimiterOperator.of(rateLimiter));
        }

        @Override
        public Mono<UserInfoResponse> userInfo(UserInfoRequest request) {
            return this.usersDelegate
                    .userInfo(request)
                    .transformDeferred(RateLimiterOperator.of(rateLimiter));
        }

        @Override
        public Mono<VerifyUserResponse> verify(VerifyUserRequest request) {
            return this.usersDelegate
                    .verify(request)
                    .transformDeferred(RateLimiterOperator.of(rateLimiter));
        }
    }

    public class ThrottledGroups implements Groups {

        public final Groups groupsDelegate;

        public ThrottledGroups() {
            this.groupsDelegate = delegate.groups();
        }

        @Override
        public Mono<AddMemberResponse> addMember(AddMemberRequest request) {
            return this.groupsDelegate
                    .addMember(request)
                    .transformDeferred(RateLimiterOperator.of(rateLimiter));
        }

        @Override
        public Mono<CheckMembershipResponse> checkMembership(CheckMembershipRequest request) {
            return this.groupsDelegate
                    .checkMembership(request)
                    .transformDeferred(RateLimiterOperator.of(rateLimiter));
        }

        @Override
        public Mono<CreateGroupResponse> create(CreateGroupRequest request) {
            return this.groupsDelegate
                    .create(request)
                    .transformDeferred(RateLimiterOperator.of(rateLimiter));
        }

        @Override
        public Mono<DeleteGroupResponse> delete(DeleteGroupRequest request) {
            return this.groupsDelegate
                    .delete(request)
                    .transformDeferred(RateLimiterOperator.of(rateLimiter));
        }

        @Override
        public Mono<GetGroupResponse> get(GetGroupRequest request) {
            return this.groupsDelegate
                    .get(request)
                    .transformDeferred(RateLimiterOperator.of(rateLimiter));
        }

        @Override
        public Mono<ListGroupsResponse> list(ListGroupsRequest request) {
            return this.groupsDelegate
                    .list(request)
                    .transformDeferred(RateLimiterOperator.of(rateLimiter));
        }

        @Override
        public Mono<ListExternalGroupMappingsResponse> listExternalGroupMappings(
                ListExternalGroupMappingsRequest request) {
            return this.groupsDelegate
                    .listExternalGroupMappings(request)
                    .transformDeferred(RateLimiterOperator.of(rateLimiter));
        }

        @Override
        public Mono<ListMembersResponse> listMembers(ListMembersRequest request) {
            return this.groupsDelegate
                    .listMembers(request)
                    .transformDeferred(RateLimiterOperator.of(rateLimiter));
        }

        @Override
        public Mono<MapExternalGroupResponse> mapExternalGroup(MapExternalGroupRequest request) {
            return this.groupsDelegate
                    .mapExternalGroup(request)
                    .transformDeferred(RateLimiterOperator.of(rateLimiter));
        }

        @Override
        public Mono<RemoveMemberResponse> removeMember(RemoveMemberRequest request) {
            return this.groupsDelegate
                    .removeMember(request)
                    .transformDeferred(RateLimiterOperator.of(rateLimiter));
        }

        @Override
        public Mono<UnmapExternalGroupByGroupDisplayNameResponse>
                unmapExternalGroupByGroupDisplayName(
                        UnmapExternalGroupByGroupDisplayNameRequest request) {
            return this.groupsDelegate
                    .unmapExternalGroupByGroupDisplayName(request)
                    .transformDeferred(RateLimiterOperator.of(rateLimiter));
        }

        @Override
        public Mono<UnmapExternalGroupByGroupIdResponse> unmapExternalGroupByGroupId(
                UnmapExternalGroupByGroupIdRequest request) {
            return this.groupsDelegate
                    .unmapExternalGroupByGroupId(request)
                    .transformDeferred(RateLimiterOperator.of(rateLimiter));
        }

        @Override
        public Mono<UpdateGroupResponse> update(UpdateGroupRequest request) {
            return this.groupsDelegate
                    .update(request)
                    .transformDeferred(RateLimiterOperator.of(rateLimiter));
        }
    }
}
