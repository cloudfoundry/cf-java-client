/*
 * Copyright 2013-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.uaa.users;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the UAA Users Client API
 */
public interface Users {

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#change-user-password">Change User Password</a> request
     *
     * @param request the Change User Password request
     * @return the response from the Change User Password request
     */
    Mono<ChangeUserPasswordResponse> changePassword(ChangeUserPasswordRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#create64">Create User</a> request
     *
     * @param request the Create User request
     * @return the response from the Create User request
     */
    Mono<CreateUserResponse> create(CreateUserRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#delete67">Delete User</a> request
     *
     * @param request the Delete User request
     * @return the response from the Delete User request
     */
    Mono<DeleteUserResponse> delete(DeleteUserRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#force-user-password-to-expire">Force User Password to Expire</a> request
     *
     * @param request the Expire Password request
     * @return the response from the Expire Password request
     */
    Mono<ExpirePasswordResponse> expirePassword(ExpirePasswordRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#get-user-verification-link">Get User Verification Link</a> request
     *
     * @param request the Get User Verification Link request
     * @return the response from the Get User Verification Link request
     */
    Mono<GetUserVerificationLinkResponse> getVerificationLink(GetUserVerificationLinkRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#invite-users">Invite Users</a> request
     *
     * @param request the Invite Users request
     * @return the response from the Invite Users request
     */
    Mono<InviteUsersResponse> invite(InviteUsersRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#list63">List Users</a> request
     *
     * @param request the List Users request
     * @return the response from the List Users request
     */
    Mono<ListUsersResponse> list(ListUsersRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#lookup-user-ids-usernames">Lookup User IDs/Usernames</a> request
     *
     * @param request the lookup userid and usernames request
     * @return the response from the lookup userid and usernames request
     */
    Mono<LookupUserIdsResponse> lookup(LookupUserIdsRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#update49">Update User</a> request
     *
     * @param request the Update User request
     * @return the response from the Update User request
     */
    Mono<UpdateUserResponse> update(UpdateUserRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#user-info">Retrieve User Info</a> request
     *
     * @param request the User Info request
     * @return the response from the User Info request
     */
    Mono<UserInfoResponse> userInfo(UserInfoRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#verify-user">Verify User</a> request
     *
     * @param request the Verify User request
     * @return the response from the Verify User request
     */
    Mono<VerifyUserResponse> verify(VerifyUserRequest request);

}
