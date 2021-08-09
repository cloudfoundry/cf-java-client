package org.cloudfoundry.reactor.client.v3.servicebrokers;

import java.util.Map;

import org.cloudfoundry.client.v3.servicebrokers.CreateServiceBrokerRequest;
import org.cloudfoundry.client.v3.servicebrokers.DeleteServiceBrokerRequest;
import org.cloudfoundry.client.v3.servicebrokers.GetServiceBrokerRequest;
import org.cloudfoundry.client.v3.servicebrokers.GetServiceBrokerResponse;
import org.cloudfoundry.client.v3.servicebrokers.ListServiceBrokersRequest;
import org.cloudfoundry.client.v3.servicebrokers.ListServiceBrokersResponse;
import org.cloudfoundry.client.v3.servicebrokers.ServiceBrokersV3;
import org.cloudfoundry.client.v3.servicebrokers.UpdateServiceBrokerRequest;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v3.AbstractClientV3Operations;

import reactor.core.publisher.Mono;

public final class ReactorServiceBrokersV3 extends AbstractClientV3Operations implements ServiceBrokersV3 {

    public ReactorServiceBrokersV3(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider,
	    Map<String, String> requestTags) {
	super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<String> create(CreateServiceBrokerRequest request) {
	return post(request, builder -> builder.pathSegment("service_brokers"))
		    .checkpoint();
    }

    @Override
    public Mono<String> delete(DeleteServiceBrokerRequest request) {
	return delete(request, builder -> builder.pathSegment("service_brokers", request.getServiceBrokerId()))
		    .checkpoint();
    }

    @Override
    public Mono<GetServiceBrokerResponse> get(GetServiceBrokerRequest request) {
	return get(request, GetServiceBrokerResponse.class, builder -> builder.pathSegment("service_brokers", request.getServiceBrokerId()))
		    .checkpoint();
    }

    @Override
    public Mono<ListServiceBrokersResponse> list(ListServiceBrokersRequest request) {
	return get(request, ListServiceBrokersResponse.class, builder -> builder.pathSegment("service_brokers"))
		    .checkpoint();
    }

    @Override
    public Mono<String> update(UpdateServiceBrokerRequest request) {
	return patch(request, builder -> builder.pathSegment("service_brokers", request.getServiceBrokerId()))
		    .checkpoint();
    }

}
