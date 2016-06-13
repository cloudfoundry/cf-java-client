/*
 * Copyright 2013-2016 the original author or authors.
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
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#change-user-password">Change User Password</a> request
     *
     * @param request the Change User Password request
     * @return the response from the Change User Password request
     */
    Mono<ChangeUserPasswordResponse> changePassword(ChangeUserPasswordRequest request);

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#create48">Create User</a> request
     *
     * @param request the Create User request
     * @return the response from the Create User request
     */
    Mono<CreateUserResponse> create(CreateUserRequest request);

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#delete50">Delete User</a> request
     *
     * @param request the Delete User request
     * @return the response from the Delete User request
     */
    Mono<DeleteUserResponse> delete(DeleteUserRequest request);

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#get-user-verification-link">Get User Verification Link</a> request
     *
     * @param request the Get User Verification Link request
     * @return the response from the Get User Verification Link request
     */
    Mono<GetUserVerificationLinkResponse> getVerificationLink(GetUserVerificationLinkRequest request);

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#invite-users">Invite Users</a> request
     *
     * @param request the Invite Users request
     * @return the response from the Invite Users request
     */
    Mono<InviteUsersResponse> invite(InviteUsersRequest request);

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#list">List Users</a> request
     *
     * @param request the List Users request
     * @return the response from the List Users request
     */
    Mono<ListUsersResponse> list(ListUsersRequest request);

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#lookup-user-ids-usernames">Lookup User IDs/Usernames</a> request
     *
     * @param request the lookup userid and usernames request
     * @return the response from the lookup userid and usernames request
     */
    Mono<LookupUserIdsResponse> lookup(LookupUserIdsRequest request);

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#update49">Update User</a> request
     *
     * @param request the Update User request
     * @return the response from the Update User request
     */
    Mono<UpdateUserResponse> update(UpdateUserRequest request);

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#verify-user">Verify User</a> request
     *
     * @param request the Verify User request
     * @return the response from the Verify User request
     */
    Mono<VerifyUserResponse> verify(VerifyUserRequest request);

}
