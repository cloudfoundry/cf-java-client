/*
 * Copyright 2013-2015 the original author or authors.
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

package org.cloudfoundry.client.v2.events;

import org.reactivestreams.Publisher;

/**
 * Main entry point to the Cloud Foundry Events Client API
 */
public interface Events {

    /**
     * {@code app.crash} event type
     */
    public static final String APP_CRASH = "app.crash";

    /**
     * {@code audit.app.create} event type
     */
    public static final String AUDIT_APP_CREATE = "audit.app.create";

    /**
     * {@code audit.app.delete-request} event type
     */
    public static final String AUDIT_APP_DELETE_REQUEST = "audit.app.delete-request";

    /**
     * {@code audit.app.ssh-authorized} event type
     */
    public static final String AUDIT_APP_SSH_AUTHORIZED = "audit.app.ssh-authorized";

    /**
     * {@code audit.app.ssh-unauthorized} event type
     */
    public static final String AUDIT_APP_SSH_UNAUTHORIZED = "audit.app.ssh-unauthorized";

    /**
     * {@code audit.app.start} event type
     */
    public static final String AUDIT_APP_START = "audit.app.start";

    /**
     * {@code audit.app.stop} event type
     */
    public static final String AUDIT_APP_STOP = "audit.app.stop";

    /**
     * {@code audit.app.update} event type
     */
    public static final String AUDIT_APP_UPDATE = "audit.app.update";

    /**
     * {@code audit.service.create} event type
     */
    public static final String AUDIT_SERVICE_CREATE = "audit.service.create";

    /**
     * {@code audit.service.delete} event type
     */
    public static final String AUDIT_SERVICE_DELETE = "audit.service.delete";

    /**
     * {@code audit.service.update} event type
     */
    public static final String AUDIT_SERVICE_UPDATE = "audit.service.update";

    /**
     * {@code audit.service_binding.create} event type
     */
    public static final String AUDIT_SERVICE_BINDING_CREATE = "audit.service_binding.create";

    /**
     * {@code audit.service_binding.delete} event type
     */
    public static final String AUDIT_SERVICE_BINDING_DELETE = "audit.service_binding.delete";

    /**
     * {@code audit.service_broker.create} event type
     */
    public static final String AUDIT_SERVICE_BROKER_CREATE = "audit.service_broker.create";

    /**
     * {@code audit.service_broker.delete} event type
     */
    public static final String AUDIT_SERVICE_BROKER_DELETE = "audit.service_broker.delete";

    /**
     * {@code audit.service_broker.update} event type
     */
    public static final String AUDIT_SERVICE_BROKER_UPDATE = "audit.service_broker.update";

    /**
     * {@code audit.service_dashboard_client.create} event type
     */
    public static final String AUDIT_SERVICE_DASHBOARD_CLIENT_CREATE = "audit.service_dashboard_client.create";

    /**
     * {@code audit.service_dashboard_client.delete} event type
     */
    public static final String AUDIT_SERVICE_DASHBOARD_CLIENT_DELETE = "audit.service_dashboard_client.delete";

    /**
     * {@code audit.service_instance.create} event type
     */
    public static final String AUDIT_SERVICE_INSTANCE_CREATE = "audit.service_instance.create";

    /**
     * {@code audit.service_instance.delete} event type
     */
    public static final String AUDIT_SERVICE_INSTANCE_DELETE = "audit.service_instance.delete";

    /**
     * {@code audit.service_instance.update} event type
     */
    public static final String AUDIT_SERVICE_INSTANCE_UPDATE = "audit.service_instance.update";

    /**
     * {@code audit.service_key.create} event type
     */
    public static final String AUDIT_SERVICE_KEY_CREATE = "audit.service_key.create";

    /**
     * {@code audit.service_key.delete} event type
     */
    public static final String AUDIT_SERVICE_KEY_DELETE = "audit.service_key.delete";

    /**
     * {@code audit.service_plan.create} event type
     */
    public static final String AUDIT_SERVICE_PLAN_CREATE = "audit.service_plan.create";

    /**
     * {@code audit.service_plan.delete} event type
     */
    public static final String AUDIT_SERVICE_PLAN_DELETE = "audit.service_plan.delete";

    /**
     * {@code audit.service_plan.update} event type
     */
    public static final String AUDIT_SERVICE_PLAN_UPDATE = "audit.service_plan.update";

    /**
     * {@code audit.service_plan_visibility.create} event type
     */
    public static final String AUDIT_SERVICE_PLAN_VISIBILITY_CREATE = "audit.service_plan_visibility.create";

    /**
     * {@code audit.service_plan_visibility.delete} event type
     */
    public static final String AUDIT_SERVICE_PLAN_VISIBILITY_DELETE = "audit.service_plan_visibility.delete";

    /**
     * {@code audit.service_plan_visibility.update} event type
     */
    public static final String AUDIT_SERVICE_PLAN_VISIBILITY_UPDATE = "audit.service_plan_visibility.update";

    /**
     * {@code audit.space.create} event type
     */
    public static final String AUDIT_SPACE_CREATE = "audit.space.create";

    /**
     * {@code audit.space.delete-request} event type
     */
    public static final String AUDIT_SPACE_DELETE_REQUEST = "audit.space.delete-request";

    /**
     * {@code audit.space.update} event type
     */
    public static final String AUDIT_SPACE_UPDATE = "audit.space.update";

    /**
     * {@code audit.user_provided_service_instance.create} event type
     */
    public static final String AUDIT_USER_PROVIDED_SERVICE_INSTANCE_CREATE =
            "audit.user_provided_service_instance.create";

    /**
     * {@code audit.user_provided_service_instance.delete} event type
     */
    public static final String AUDIT_USER_PROVIDED_SERVICE_INSTANCE_DELETE =
            "audit.user_provided_service_instance.delete";

    /**
     * {@code audit.user_provided_service_instance.update} event type
     */
    public static final String AUDIT_USER_PROVIDED_SERVICE_INSTANCE_UPDATE =
            "audit.user_provided_service_instance.update";


    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/events/retrieve_a_particular_event.html">Get Event</a>
     * request
     *
     * @param request the Get Event request
     * @return the response from the Get Event request
     */
    Publisher<GetEventResponse> get(GetEventRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/events/list_all_events.html">List Events</a> request
     *
     * @param request the List Events request
     * @return the response from the List Events request
     */
    Publisher<ListEventsResponse> list(ListEventsRequest request);
}
