/*
 * Copyright 2013-2021 the original author or authors.
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

package org.cloudfoundry.reactor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.immutables.value.Value;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A {@link RootProvider} that returns endpoints extracted from the `/` API for the configured endpoint.
 */
@Value.Immutable
abstract class _RootPayloadRootProvider extends AbstractRootProvider {

    @Override
    protected Mono<UriComponents> doGetRoot(ConnectionContext connectionContext) {
        return Mono.just(getRoot());
    }

    @Override
    protected Mono<UriComponents> doGetRoot(String key, ConnectionContext connectionContext) {
        return getHrefPayload(connectionContext)
            .map(payload -> {
                if (!payload.containsKey(key)) {
                    throw new IllegalArgumentException(String.format("Root payload does not contain key '%s'", key));
                }
           	    return normalize(UriComponentsBuilder.fromUriString(payload.get(key)));
            });
    }

    @Override
    protected Mono<String> doGetRootKey(Queue<String> keyList, ConnectionContext connectionContext) {
        String key = keyList.poll();
        @SuppressWarnings("rawtypes")
		Mono<Map> payload = getPayload(connectionContext);
        return payload
            .map( root -> {
                if (!root.containsKey(key)) {
                    throw new IllegalArgumentException(String.format("Root payload does not contain key '%s'", key));
                }
                return handleEntry(keyList,root.get(key));
             });
    }

    @SuppressWarnings("unchecked")
    private String handleEntry(Queue<String> keyList, Object entry) {
    	if(entry==null) {
    		return "";
    	} else if(entry instanceof String) {
        	if(keyList.isEmpty()) {
        		return (String) entry;
        	}else {
        		 throw new IllegalArgumentException(String.format("root payload does not contain key '%s'", keyList.peek()));
        	}
        }else if(entry instanceof Map) {
        	Map<String, Object> entryMap = (Map<String, Object>) entry;
        	String key = keyList.poll();
         	return handleEntry(keyList, entryMap.get(key));
        } else{
        	throw new IllegalArgumentException(String.format("root payload does contain unhandled type '%s'", entry.getClass().getName()));
        }
    }

    abstract ObjectMapper getObjectMapper();

    @SuppressWarnings("unchecked")
    @Value.Derived
    private Mono<Map<String, String>> getHrefPayload(ConnectionContext connectionContext) {
        return createOperator(connectionContext)
            .flatMap(operator -> operator.get()
                .uri(Function.identity())
                .response()
                .parseBody(Map.class))
            .map(payload -> (Map<String, Map<String, Map<String, String>>>) payload)
            .map(this::processPayload)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Root endpoint does not contain a payload")))
            .checkpoint();
    }

    @SuppressWarnings("rawtypes")
	@Value.Derived
    private Mono<Map> getPayload(ConnectionContext connectionContext) {
        return createOperator(connectionContext)
            .flatMap(operator -> operator.get()
                .uri(Function.identity())
                .response()
                .parseBody(Map.class))
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Root endpoint does not contain a payload")))
            .checkpoint();
    }

    // Convert json payload into Map, keeping only "href" entries.
    private Map<String, String> processPayload(Map<String, Map<String, Map<String, String>>> payload) {
        return payload.get("links").entrySet().stream()
            .filter(item -> null != item.getValue())
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get("href")));
    }

}
