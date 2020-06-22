package org.cloudfoundry.reactor.client.v3.routes;

import org.cloudfoundry.client.v3.routes.CreateRouteRequest;
import org.cloudfoundry.client.v3.routes.CreateRouteResponse;
import org.cloudfoundry.client.v3.routes.DeleteRouteRequest;
import org.cloudfoundry.client.v3.routes.GetRouteRequest;
import org.cloudfoundry.client.v3.routes.GetRouteResponse;
import org.cloudfoundry.client.v3.routes.InsertRouteDestinationsRequest;
import org.cloudfoundry.client.v3.routes.InsertRouteDestinationsResponse;
import org.cloudfoundry.client.v3.routes.ListRouteDestinationsRequest;
import org.cloudfoundry.client.v3.routes.ListRouteDestinationsResponse;
import org.cloudfoundry.client.v3.routes.ListRoutesRequest;
import org.cloudfoundry.client.v3.routes.ListRoutesResponse;
import org.cloudfoundry.client.v3.routes.RemoveRouteDestinationsRequest;
import org.cloudfoundry.client.v3.routes.ReplaceRouteDestinationsRequest;
import org.cloudfoundry.client.v3.routes.ReplaceRouteDestinationsResponse;
import org.cloudfoundry.client.v3.routes.RoutesV3;
import org.cloudfoundry.client.v3.routes.UpdateRouteRequest;
import org.cloudfoundry.client.v3.routes.UpdateRouteResponse;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v3.AbstractClientV3Operations;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The Reactor-based implementation of {@link RoutesV3}
 */
public class ReactorRoutesV3 extends AbstractClientV3Operations implements RoutesV3 {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorRoutesV3(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<CreateRouteResponse> create(CreateRouteRequest request) {
        return post(request, CreateRouteResponse.class, uriComponentsBuilder -> uriComponentsBuilder.pathSegment("routes"))
            .checkpoint();
    }

    @Override
    public Mono<String> delete(DeleteRouteRequest request) {
        return delete(request, uriComponentsBuilder -> uriComponentsBuilder.pathSegment("routes", request.getRouteId()))
            .checkpoint();
    }

    @Override
    public Mono<GetRouteResponse> get(GetRouteRequest request) {
        return get(request, GetRouteResponse.class, uriComponentsBuilder -> uriComponentsBuilder.pathSegment("routes", request.getRouteId()))
            .checkpoint();
    }

    @Override
    public Mono<InsertRouteDestinationsResponse> insertDestinations(InsertRouteDestinationsRequest request) {
        return post(request, InsertRouteDestinationsResponse.class, uriComponentsBuilder -> uriComponentsBuilder.pathSegment("routes", request.getRouteId(), "destinations"))
            .checkpoint();
    }

    @Override
    public Mono<ListRoutesResponse> list(ListRoutesRequest request) {
        return get(request, ListRoutesResponse.class, uriComponentsBuilder -> uriComponentsBuilder.pathSegment("routes"))
            .checkpoint();
    }

    @Override
    public Mono<ListRouteDestinationsResponse> listDestinations(ListRouteDestinationsRequest request) {
        return get(request, ListRouteDestinationsResponse.class, uriComponentsBuilder -> uriComponentsBuilder.pathSegment("routes", request.getRouteId(), "destinations"))
            .checkpoint();
    }

    @Override
    public Mono<Void> removeDestinations(RemoveRouteDestinationsRequest request) {
        return delete(request, Void.class, uriComponentsBuilder -> uriComponentsBuilder.pathSegment("routes", request.getRouteId(), "destinations", request.getDestinationId()))
            .checkpoint();
    }

    @Override
    public Mono<ReplaceRouteDestinationsResponse> replaceDestinations(ReplaceRouteDestinationsRequest request) {
        return patch(request, ReplaceRouteDestinationsResponse.class, uriComponentsBuilder -> uriComponentsBuilder.pathSegment("routes", request.getRouteId(), "destinations"))
            .checkpoint();
    }

    @Override
    public Mono<UpdateRouteResponse> update(UpdateRouteRequest request) {
        return patch(request, UpdateRouteResponse.class, uriComponentsBuilder -> uriComponentsBuilder.pathSegment("routes", request.getRouteId()))
            .checkpoint();
    }

}
