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

package org.cloudfoundry.operations.applications;

import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

final class RouteUtils {

    private RouteUtils() {
    }

    static Mono<DecomposedRoute> decomposeRoute(List<DomainSummary> availableDomains, String route, String routePath) {
        String domain = null;
        String host = null;
        String path = null;
        Integer port = null;
        String routeWithoutSuffix = route;

        if (availableDomains.size() == 0) {
            throw new IllegalArgumentException(String.format("The route %s did not match any existing domains", route));
        }

        List<DomainSummary> sortedDomains = availableDomains.stream()
            .sorted(Comparator.<DomainSummary>comparingInt(domainSummary -> domainSummary.getName().length()).reversed())
            .collect(Collectors.toList());

        if (route.contains("/")) {
            int index = route.indexOf("/");
            path = routePath != null ? routePath : route.substring(index);
            routeWithoutSuffix = route.substring(0, index);
        } else if (hasPort(route)) {
            port = getPort(route);
            routeWithoutSuffix = route.substring(0, route.indexOf(":"));
        }

        for (DomainSummary item : sortedDomains) {
            if (isDomainMatch(routeWithoutSuffix, item.getName())) {
                domain = item.getName();
                if (domain.length() < routeWithoutSuffix.length()) {
                    host = routeWithoutSuffix.substring(0, routeWithoutSuffix.lastIndexOf(domain) - 1);
                }
                break;
            }
        }

        if (domain == null) {
            throw new IllegalArgumentException(String.format("The route %s did not match any existing domains", route));
        }

        if ((host != null || path != null) && port != null) {
            throw new IllegalArgumentException(String.format("The route %s is invalid: Host/path cannot be set with port", route));
        }

        return Mono.just(DecomposedRoute.builder()
            .domain(domain)
            .host(host)
            .path(path)
            .port(port)
            .build());
    }

    private static Integer getPort(String route) {
        Pattern pattern = Pattern.compile(":\\d+$");
        Matcher matcher = pattern.matcher(route);

        matcher.find();
        return Integer.valueOf(route.substring(matcher.start() + 1, matcher.end()));
    }

    private static Boolean hasPort(String route) {
        Pattern pattern = Pattern.compile("^.+?:\\d+$");
        Matcher matcher = pattern.matcher(route);

        return matcher.matches();
    }

    private static boolean isDomainMatch(String route, String domain) {
        return route.equals(domain) || route.endsWith(domain) && route.charAt(route.length() - domain.length() - 1) == '.';
    }

}
