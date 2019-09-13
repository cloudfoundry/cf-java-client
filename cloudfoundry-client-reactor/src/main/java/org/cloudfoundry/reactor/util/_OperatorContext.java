package org.cloudfoundry.reactor.util;

import java.util.Optional;

import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.immutables.value.Value;

@Value.Immutable(copy = true)
public interface _OperatorContext {

    @Value.Parameter
    ConnectionContext getConnectionContext();

    @Value.Parameter
    String getRoot();

    Optional<ErrorPayloadMapper> getErrorPayloadMapper();

    Optional<TokenProvider> getTokenProvider();

}
