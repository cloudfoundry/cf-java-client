/*

 * Copyright 2009-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.maven;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

import static org.junit.Assert.*;

import org.apache.maven.plugin.MojoExecutionException;
import org.cloudfoundry.client.lib.CloudFoundryClient;

import org.cloudfoundry.client.lib.domain.CloudInfo;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.CloudServiceOffering;

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
	private CloudInfo cloudInfo;

	@Mock
	private CloudService service;

	@Mock
	private CloudServiceOffering cloudServiceOffering;

	@Before
	public void setUp() {

		//Initializing the objects
		MockitoAnnotations.initMocks(this);

		services = new ArrayList<CloudService>();
		serviceCreation = new ServiceCreation();

		//Programming the mock(s)
		List<CloudServiceOffering> serviceOfferings = new ArrayList<CloudServiceOffering>();

		when(cloudServiceOffering.getLabel()).thenReturn("mysql");
		when(cloudServiceOffering.getVersion()).thenReturn("5.1");

		serviceOfferings.add(cloudServiceOffering);

		when(client.getCloudInfo()).thenReturn(cloudInfo);
		when(client.getServiceOfferings()).thenReturn(serviceOfferings);
	}

	@Test
	public void createServicesTest() throws MojoExecutionException {
		List<String> names = new ArrayList<String>();

		for (int i = 0; i < 4; i++) {
			CloudService service = new CloudService(null, "name");
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
			CloudService service = new CloudService(null, "name");
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
			CloudService service = new CloudService(null, "name");
			service.setLabel("asdfsaf");

			services.add(service);
			names.add(service.getName());
		}
		serviceCreation.setClient(client);
		serviceCreation.setServices(services);

		assertEquals(names, serviceCreation.createServices());
	}
}
