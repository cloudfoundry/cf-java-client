package org.cloudfoundry.reactor.util;

import org.immutables.value.Value;

@Value.Immutable
public interface _UriQueryParameter {

    @Value.Parameter
    String getKey();

    @Value.Parameter
    Object getValue();

}
