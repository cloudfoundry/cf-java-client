package org.cloudfoundry.client.v3.servicebrokers;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Service Brokers V3 Client API
 */
public interface ServiceBrokersV3 {

    /**
     * Makes the <a href="http://v3-apidocs.cloudfoundry.org/version/release-candidate/#create-a-service-broker">Create a service broker</a> request
     *
     * @param request the Create Service Broker request
     * @return the response from the Create Service Broker request
     */
    Mono<String> create(CreateServiceBrokerRequest request);
    
    /**
     * Makes the <a href="http://v3-apidocs.cloudfoundry.org/version/release-candidate/#delete-a-service-broker">Delete a service broker</a> request
     *
     * @param request the Delete Service Broker request
     * @return the response from the Delete Service Broker request
     */
    Mono<String> delete(DeleteServiceBrokerRequest request);
    
    /**
     * Makes the <a href="http://v3-apidocs.cloudfoundry.org/version/release-candidate/#get-a-service-broker">Get a service broker</a> request
     *
     * @param request the Get Service Broker request
     * @return the response from the Get Service Broker request
     */
    Mono<GetServiceBrokerResponse> get(GetServiceBrokerRequest request);
    
    /**
     * Makes the <a href="http://v3-apidocs.cloudfoundry.org/version/release-candidate/#list-service-brokers">List service brokers</a> request
     *
     * @param request the List Service Brokers request
     * @return the response from the List Service Brokers request
     */
    Mono<ListServiceBrokersResponse> list(ListServiceBrokersRequest request);
    
    /**
     * Makes the <a href="http://v3-apidocs.cloudfoundry.org/version/release-candidate/#update-a-service-broker">Update a service broker</a> request
     *
     * @param request the Update Service Broker request
     * @return the response from the Update Service Broker request
     */
    Mono<UpdateServiceBrokerResponse> update(UpdateServiceBrokerRequest request);
    
}
