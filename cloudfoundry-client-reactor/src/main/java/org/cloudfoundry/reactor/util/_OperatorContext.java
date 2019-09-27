package org.cloudfoundry.reactor.util;

import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable(copy = true)
public interface _OperatorContext {

    @Value.Parameter
    ConnectionContext getConnectionContext();

    Optional<ErrorPayloadMapper> getErrorPayloadMapper();

    @Value.Parameter
    String getRoot();

    Optional<TokenProvider> getTokenProvider();

}
