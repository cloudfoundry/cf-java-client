package org.cloudfoundry.reactor.client.v3.routes;

import org.cloudfoundry.client.v3.routes.CreateRouteRequest;
import org.cloudfoundry.client.v3.routes.CreateRouteResponse;
import org.cloudfoundry.client.v3.routes.DeleteRouteRequest;
import org.cloudfoundry.client.v3.routes.GetRouteRequest;
import org.cloudfoundry.client.v3.routes.GetRouteResponse;
import org.cloudfoundry.client.v3.routes.ListRoutesRequest;
import org.cloudfoundry.client.v3.routes.ListRoutesResponse;
import org.cloudfoundry.client.v3.routes.RoutesV3;
import org.cloudfoundry.client.v3.routes.UpdateRouteRequest;
import org.cloudfoundry.client.v3.routes.UpdateRouteResponse;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v3.AbstractClientV3Operations;
import reactor.core.publisher.Mono;

import java.util.Map;

public class ReactorRoutesV3 extends AbstractClientV3Operations implements RoutesV3 {

    public ReactorRoutesV3(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<CreateRouteResponse> create(CreateRouteRequest request) {
        return post(request, CreateRouteResponse.class, uriComponentsBuilder -> uriComponentsBuilder.pathSegment("routes")).checkpoint();
    }

    @Override
    public Mono<String> delete(DeleteRouteRequest request) {
        return delete(request,
            uriComponentsBuilder -> uriComponentsBuilder.pathSegment("routes", request.getRouteId())).checkpoint();
    }

    @Override
    public Mono<GetRouteResponse> get(GetRouteRequest request) {
        return get(request, GetRouteResponse.class,
            uriComponentsBuilder -> uriComponentsBuilder.pathSegment("routes", request.getRouteId())).checkpoint();
    }

    @Override
    public Mono<ListRoutesResponse> list(ListRoutesRequest request) {
        return get(request, ListRoutesResponse.class, uriComponentsBuilder -> uriComponentsBuilder.pathSegment("routes")).checkpoint();
    }

    @Override
    public Mono<UpdateRouteResponse> update(UpdateRouteRequest request) {
        return patch(request, UpdateRouteResponse.class,
            uriComponentsBuilder -> uriComponentsBuilder.pathSegment("routes", request.getRouteId())).checkpoint();
    }
}
