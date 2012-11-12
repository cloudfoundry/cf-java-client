package org.cloudfoundry.maven;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

import static org.junit.Assert.*;

import org.apache.maven.plugin.MojoExecutionException;
import org.cloudfoundry.client.lib.CloudFoundryClient;

import org.cloudfoundry.client.lib.domain.CloudInfo;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.CloudServiceOffering;
import org.cloudfoundry.client.lib.domain.ServiceConfiguration;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ServiceCreationV2Test {

	//Subject under test
	private ServiceCreation serviceCreation;

	//Actors
	private List<CloudService> services;

	@Mock
	private CloudFoundryClient client;

	@Mock
	private CloudInfo cloudInfo;

	@Mock
	private CloudService service;

	@Mock
	private ServiceConfiguration serviceConfiguration;

	@Mock
	private CloudServiceOffering cloudServiceOffering;

	@Before
	public void setUp() {

		//Initializing the objects
		MockitoAnnotations.initMocks(this);

		services = new ArrayList<CloudService>();
		serviceCreation = new ServiceCreation();

		//Programming the mock(s)
		List<ServiceConfiguration> serviceConfigurations = new ArrayList<ServiceConfiguration>();

		when(cloudServiceOffering.getLabel()).thenReturn("mysql");
		when(cloudServiceOffering.getVersion()).thenReturn("5.1");

		when(serviceConfiguration.getType()).thenReturn("database");
		when(serviceConfiguration.getDescription()).thenReturn("MySQL database Service");
		when(serviceConfiguration.getCloudServiceOffering()).thenReturn(cloudServiceOffering);

		serviceConfigurations.add(serviceConfiguration);

		when(cloudInfo.getCloudControllerMajorVersion()).thenReturn(CloudInfo.CC_MAJOR_VERSION.V2);
		when(client.getCloudInfo()).thenReturn(cloudInfo);
		when(client.getServiceConfigurations()).thenReturn(serviceConfigurations);
	}

	@Test
	public void createServicesTest() throws MojoExecutionException {
		List<String> names = new ArrayList<String>();

		for (int i = 0; i < 4; i++) {
			Map<String, Object> servicesAsMap = new HashMap<String, Object>();
			servicesAsMap.put("name", "test" + i);

			CloudService service = new CloudService(servicesAsMap);
			service.setLabel("mysql");

			services.add(service);
			names.add(service.getName());
		}
		serviceCreation.setClient(client);
		serviceCreation.setServices(services);

		assertEquals(names, serviceCreation.createServices());
	}

	@Test
	public void createServicesWithoutVersionAndTierTest() throws MojoExecutionException {
		List<String> names = new ArrayList<String>();

		for (int i = 0; i < 4; i++) {
			Map<String, Object> servicesAsMap = new HashMap<String, Object>();
			servicesAsMap.put("name", "test" + i);

			CloudService service = new CloudService(servicesAsMap);
			service.setLabel("mysql");

			services.add(service);
			names.add(service.getName());
		}
		serviceCreation.setClient(client);
		serviceCreation.setServices(services);

		assertEquals(names, serviceCreation.createServices());
	}

	@Test
	public void createServicesWithoutVersionAndTierAndInvalidVendorTest() throws MojoExecutionException {
		List<String> names = new ArrayList<String>();

		for (int i = 0; i < 4; i++) {
			Map<String, Object> servicesAsMap = new HashMap<String, Object>();

			servicesAsMap.put("name", "test" + i);

			CloudService service = new CloudService(servicesAsMap);
			service.setLabel("asdfsaf");

			services.add(service);
			names.add(service.getName());
		}
		serviceCreation.setClient(client);
		serviceCreation.setServices(services);

		assertEquals(names, serviceCreation.createServices());
	}
}
