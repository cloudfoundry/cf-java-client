package org.cloudfoundry.reactor;

import java.util.Optional;

import org.immutables.value.Value;

import reactor.netty.http.client.HttpClientResponse;

@Value.Immutable
public interface _HttpClientResponseWithParsedBody<T> {

    @Value.Parameter
    Optional<T> getBody();

    @Value.Parameter
    HttpClientResponse getResponse();
    
}
