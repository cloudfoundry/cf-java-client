package org.cloudfoundry.client.v3.routes;

import reactor.core.publisher.Mono;

public interface RoutesV3 {

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.77.0/index.html#create-a-route">Create Route</a> request
     *
     * @param request the Create Route request
     * @return the response from the Create Route request
     */
    Mono<CreateRouteResponse> create(CreateRouteRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.77.0/index.html#delete-route">Delete Route</a> request
     *
     * @param request the Delete Route request
     * @return the response from Delete Route request
     */
    Mono<String> delete(DeleteRouteRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.77.0/index.html#get-a-route">Get Route</a> request
     *
     * @param request the Get Route request
     * @return the response from the Get Route request
     */
    Mono<GetRouteResponse> get(GetRouteRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.86.0/index.html#insert-destinations-for-a-route">Insert Route Destinations</a> request
     *
     * @param request the Insert Route Destinations request
     * @return the response from the Insert Route Destinations request
     */
    Mono<InsertRouteDestinationsResponse> insertDestinations(InsertRouteDestinationsRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.77.0/index.html#list-routes">List Routes</a> request
     *
     * @param request the List Route request
     * @return the response from the List Route request
     */
    Mono<ListRoutesResponse> list(ListRoutesRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.86.0/index.html#list-destinations-for-a-route">List Route Destinations</a> request
     *
     * @param request the List Route Destinations request
     * @return the response from the List Route Destinations request
     */
    Mono<ListRouteDestinationsResponse> listDestinations(ListRouteDestinationsRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.86.0/index.html#remove-destination-for-a-route">Remove Route Destinations</a> request
     *
     * @param request the Remove Route Destinations request
     * @return the response from the Remove Route Destinations request
     */
    Mono<Void> removeDestinations(RemoveRouteDestinationsRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.86.0/index.html#replace-all-destinations-for-a-route">Replace Route Destinations</a> request
     *
     * @param request the Replace Route Destinations request
     * @return the response from the Replace Route Destinations request
     */
    Mono<ReplaceRouteDestinationsResponse> replaceDestinations(ReplaceRouteDestinationsRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.77.0/index.html#list-routes">List Routes</a> request
     *
     * @param request the List Route request
     * @return the response from the List Route request
     */
    Mono<UpdateRouteResponse> update(UpdateRouteRequest request);

}
