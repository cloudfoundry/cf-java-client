package org.cloudfoundry.maven;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

import static org.junit.Assert.*;

import org.apache.maven.plugin.MojoExecutionException;
import org.cloudfoundry.client.lib.CloudFoundryClient;

import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.ServiceConfiguration;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ServiceCreationTest {

	//Subject under test
	private ServiceCreation serviceCreation;

	//Actors
	private List<CloudService> services;

	@Mock
	private CloudFoundryClient client;

	@Mock
	private CloudService service;

	@Before
	public void setUp() {

		//Initializing the objects
		MockitoAnnotations.initMocks(this);

		services = new ArrayList<CloudService>();
		serviceCreation = new ServiceCreation();

		//Programming the mock(s)
		List<ServiceConfiguration> serviceConfigurations = new ArrayList<ServiceConfiguration>();

		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("type", "database");
		attributes.put("vendor", "mysql");
		attributes.put("version", "5.1");
		attributes.put("description", "MySQL database Service");

		serviceConfigurations.add(new ServiceConfiguration(attributes));

		when(client.getServiceConfigurations()).thenReturn(serviceConfigurations);
	}

	@Test
	public void createServicesTest() throws MojoExecutionException {
		List<String> names = new ArrayList<String>();

		for (int i = 0; i < 4; i++) {
			Map<String, Object> servicesAsMap = new HashMap<String, Object>();
			servicesAsMap.put("vendor", "mysql");
			servicesAsMap.put("name", "test" + i);
			servicesAsMap.put("version", "5.1");
			servicesAsMap.put("tier", "free");

			CloudService service = new CloudService(servicesAsMap);

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
			servicesAsMap.put("vendor", "mysql");
			servicesAsMap.put("name", "test" + i);

			CloudService service = new CloudService(servicesAsMap);

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
			servicesAsMap.put("vendor", "asdfsaf");
			servicesAsMap.put("name", "test" + i);

			CloudService service = new CloudService(servicesAsMap);

			services.add(service);
			names.add(service.getName());
		}
		serviceCreation.setClient(client);
		serviceCreation.setServices(services);

		assertEquals(names, serviceCreation.createServices());
	}
}
