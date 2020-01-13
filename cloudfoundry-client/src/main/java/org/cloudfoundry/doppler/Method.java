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

package org.cloudfoundry.doppler;

import java.util.Objects;

/**
 * HTTP Methods
 */
public enum Method {

    ACL,

    BASELINE_CONTROL,

    BIND,

    CHECKIN,

    CHECKOUT,

    CONNECT,

    COPY,

    DEBUG,

    DELETE,

    GET,

    HEAD,

    LABEL,

    LINK,

    LOCK,

    MERGE,

    MKACTIVITY,

    MKCALENDAR,

    MKCOL,

    MKREDIRECTREF,

    MKWORKSPACE,

    MOVE,

    OPTIONS,

    ORDERPATCH,

    PATCH,

    POST,

    PRI,

    PROPFIND,

    PROPPATCH,

    PUT,

    REBIND,

    REPORT,

    SEARCH,

    SHOWMETHOD,

    SPACEJUMP,

    TEXTSEARCH,

    TRACE,

    TRACK,

    UNBIND,

    UNCHECKOUT,

    UNLINK,

    UNLOCK,

    UPDATE,

    UPDATEREDIRECTREF,

    VERSION_CONTROL;

    static Method from(org.cloudfoundry.dropsonde.events.Method dropsonde) {
        switch (Objects.requireNonNull(dropsonde, "dropsonde")) {
            case ACL:
                return ACL;
            case BASELINE_CONTROL:
                return BASELINE_CONTROL;
            case BIND:
                return BIND;
            case CHECKIN:
                return CHECKIN;
            case CHECKOUT:
                return CHECKOUT;
            case CONNECT:
                return CONNECT;
            case COPY:
                return COPY;
            case DEBUG:
                return DEBUG;
            case DELETE:
                return DELETE;
            case GET:
                return GET;
            case HEAD:
                return HEAD;
            case LABEL:
                return LABEL;
            case LINK:
                return LINK;
            case LOCK:
                return LOCK;
            case MERGE:
                return MERGE;
            case MKACTIVITY:
                return MKACTIVITY;
            case MKCALENDAR:
                return MKCALENDAR;
            case MKCOL:
                return MKCOL;
            case MKREDIRECTREF:
                return MKREDIRECTREF;
            case MKWORKSPACE:
                return MKWORKSPACE;
            case MOVE:
                return MOVE;
            case OPTIONS:
                return OPTIONS;
            case ORDERPATCH:
                return ORDERPATCH;
            case PATCH:
                return PATCH;
            case POST:
                return POST;
            case PRI:
                return PRI;
            case PROPFIND:
                return PROPFIND;
            case PROPPATCH:
                return PROPPATCH;
            case PUT:
                return PUT;
            case REBIND:
                return REBIND;
            case REPORT:
                return REPORT;
            case SEARCH:
                return SEARCH;
            case SHOWMETHOD:
                return SHOWMETHOD;
            case SPACEJUMP:
                return SPACEJUMP;
            case TEXTSEARCH:
                return TEXTSEARCH;
            case TRACE:
                return TRACE;
            case TRACK:
                return TRACK;
            case UNBIND:
                return UNBIND;
            case UNCHECKOUT:
                return UNCHECKOUT;
            case UNLINK:
                return UNLINK;
            case UNLOCK:
                return UNLOCK;
            case UPDATE:
                return UPDATE;
            case UPDATEREDIRECTREF:
                return UPDATEREDIRECTREF;
            case VERSION_CONTROL:
                return VERSION_CONTROL;
            default:
                throw new IllegalArgumentException(String.format("Unknown method: %s", dropsonde));
        }
    }

}
