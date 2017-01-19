/*
 * Copyright 2013-2017 the original author or authors.
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

package org.cloudfoundry.reactor.bosh.tasks;

import org.cloudfoundry.bosh.tasks.ListTasksRequest;
import org.cloudfoundry.bosh.tasks.ListTasksResponse;
import org.cloudfoundry.bosh.tasks.Tasks;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.bosh.AbstractBoshOperations;
import reactor.core.publisher.Mono;

/**
 * The Reactor-based implementation of {@link Tasks}
 */
public final class ReactorTasks extends AbstractBoshOperations implements Tasks {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     */
    public ReactorTasks(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider) {
        super(connectionContext, root, tokenProvider);
    }

    @Override
    public Mono<ListTasksResponse> list(ListTasksRequest request) {
        return get(request, ListTasksResponse.class, builder -> builder.pathSegment("tasks"));
    }

}
