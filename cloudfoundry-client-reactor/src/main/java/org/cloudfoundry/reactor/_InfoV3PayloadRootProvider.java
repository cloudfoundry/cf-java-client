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

/**
 * A {@link RootProvider} that returns endpoints extracted from the `/v3/info` API for the configured endpoint.
 */
@Value.Immutable
abstract class _InfoV3PayloadRootProvider extends AbstractRootProvider {

    protected Mono<UriComponents> doGetRoot(ConnectionContext connectionContext) {
        return Mono.just(getRoot());
    }

    protected Mono<UriComponents> doGetRoot(String key, ConnectionContext connectionContext) {
        return getInfo(connectionContext)
            .map(info -> {
                if (!info.containsKey(key)) {
                    throw new IllegalArgumentException(String.format("InfoV3 payload does not contain key '%s'", key));
                }

                return normalize(UriComponentsBuilder.fromUriString((String) info.get(key)));
            });
    }

    protected Mono<String> doGetRootKey(Queue<String> keyList, ConnectionContext connectionContext) {
    	String firstKey = keyList.poll();
    	
    	@SuppressWarnings("rawtypes")
		Mono<Map> payload = getInfo(connectionContext);
    	return payload
            .map(info -> {
                if (!info.containsKey(firstKey)) {
                    throw new IllegalArgumentException(String.format("InfoV3 payload does not contain key '%s'", firstKey));
                }
                return handleEntry(keyList,info.get(firstKey));
            });
    	
    }

    private String handleEntry(Queue<String> keyList, Object entry) {
    	if(entry==null) {
    		return "";
    	} else if(entry instanceof String) {
        	if(keyList.isEmpty()) {
        		return (String) entry;
        	}else {
        		 throw new IllegalArgumentException(String.format("InfoV3 payload does not contain key '%s'", keyList.peek()));
        	}
        }else if(entry instanceof Map) {
        	@SuppressWarnings("unchecked")
			Map<String, Object> entryMap = (Map<String, Object>) entry;
        	String key = keyList.poll();
        	return handleEntry(keyList, entryMap.get(key));
        } else{
        	throw new IllegalArgumentException(String.format("InfoV3 payload does contain unknown type '%s'", entry.getClass().getName()));
        }
    }

    abstract ObjectMapper getObjectMapper();

    private UriComponentsBuilder buildInfoUri(UriComponentsBuilder root) {
        return root.pathSegment("v3", "info");
    }

    @SuppressWarnings("rawtypes")
	@Value.Derived
    private Mono<Map> getInfo(ConnectionContext connectionContext) {
        return createOperator(connectionContext)
            .flatMap(operator -> operator.get()
                .uri(this::buildInfoUri)
                .response()
                .parseBody(Map.class))
            .switchIfEmpty(Mono.error(new IllegalArgumentException("InfoV3 endpoint does not contain a payload")))
            .checkpoint();
    }

}
