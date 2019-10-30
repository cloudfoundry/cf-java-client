package org.cloudfoundry.client.v3.routes;

import org.cloudfoundry.client.v3.routes.GetRouteResponse;
import org.cloudfoundry.client.v3.routes.CreateRouteResponse;
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
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.77.0/index.html#get-a-route">Get Route</a> request
     *
     * @param request the Get Route request
     * @return the response from the Get Route request
     */
    Mono<GetRouteResponse> get(GetRouteRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.77.0/index.html#list-routes">List Routes</a> request
     *
     * @param request the List Route request
     * @return the response from the List Route request
     */
    Mono<ListRoutesResponse> list(ListRoutesRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.77.0/index.html#list-routes">List Routes</a> request
     *
     * @param request the List Route request
     * @return the response from the List Route request
     */
    Mono<UpdateRouteResponse> update(UpdateRouteRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.77.0/index.html#delete-route">Delete Route</a> request
     *
     * @param request the Delete Route request
     * @return the response from Delete Route request
     */
    Mono<String> delete(DeleteRouteRequest request);


}
