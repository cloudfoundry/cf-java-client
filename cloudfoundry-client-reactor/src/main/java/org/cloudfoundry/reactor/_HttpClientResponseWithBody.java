package org.cloudfoundry.reactor;

import org.immutables.value.Value;
import reactor.netty.ByteBufFlux;
import reactor.netty.http.client.HttpClientResponse;

@Value.Immutable
public interface _HttpClientResponseWithBody {

    @Value.Parameter
    ByteBufFlux getBody();

    @Value.Parameter
    HttpClientResponse getResponse();

}
