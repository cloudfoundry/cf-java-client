/*
 * Copyright 2009-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.caldecott;

import org.cloudfoundry.caldecott.client.HttpTunnelFactory;
import org.cloudfoundry.caldecott.client.TunnelHelper;
import org.cloudfoundry.caldecott.client.TunnelServer;
import org.cloudfoundry.client.lib.CloudApplication;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.CloudService;

import java.io.Console;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

/**
 * A class used for testing tunnels. Starts a tunnel server based on provided connection parameters.
 *
 * @author Thomas Risberg
 */
public class JavaTunnel {

	private static final String CC_URL = System.getProperty("vcap.target", "https://api.cloudfoundry.com");
	private static String vcap_email = System.getProperty("vcap.email");
	private static String vcap_passwd = System.getProperty("vcap.passwd");
	private static String vcap_service = System.getProperty("vcap.service");
	public static final int LOCAL_PORT = 10000;
	public static final String LOCAL_HOST = "localhost";

	public static void main(String[] args) {

		Console console = System.console();
		//read user name, using java.util.Formatter syntax :
		if (vcap_email == null) {
			vcap_email = console.readLine("Login E-Mail? ");
        }
		//read the password, without echoing the output
		if (vcap_passwd == null) {
			vcap_passwd = new String(console.readPassword("Password? "));
        }

		CloudFoundryClient client =  clientInit();

		CloudApplication serverApp = null;
		try {
			serverApp = client.getApplication(TunnelHelper.getTunnelAppName());
		}
		catch (CloudFoundryException e) {}
		if (serverApp == null) {
			System.out.println("Deploying Caldecott server app");
			TunnelHelper.deployTunnelApp(client);
		}
		try {
			serverApp = client.getApplication(TunnelHelper.getTunnelAppName());
		}
		catch (CloudFoundryException e) {
			System.err.println("Unable to deploy Caldecott server app: " + e.getMessage());
			throw e;
		}
		if (!serverApp.getState().equals(CloudApplication.AppState.STARTED)) {
			System.out.println("Starting Caldecott server app");
			client.startApplication(serverApp.getName());
		}

		while (vcap_service == null) {
			System.out.println("You have the following services defined:");
			List<CloudService> services = client.getServices();
			int i = 0;
			for (CloudService svc : services) {
				i++;
				System.out.println(i + ": " + svc.getName());
			}
			if (i ==0) {
				System.err.println("It looks like you don't have any services defined. Please create one first!");
				System.exit(1);
			}
			String svc = console.readLine("Which Service to connect to (" + 1 + "-" + i +")? ");
			int svc_ix = 0;
			try {
				svc_ix = Integer.parseInt(svc);
			} catch (NumberFormatException e) {
				System.err.println(svc + " is not a valid choice!");
				continue;
			}
			if (svc_ix < 1 || svc_ix > i) {
				System.err.println(svc + " is not a valid choice!");
				continue;
			}
			vcap_service = services.get(svc_ix - 1).getName();
		}
		System.out.println("Starting tunnel on " + CC_URL + " to service " + vcap_service + " on behalf of " + vcap_email);

		TunnelHelper.bindServiceToTunnelApp(client, vcap_service);

		InetSocketAddress local = new InetSocketAddress(LOCAL_HOST, LOCAL_PORT);
		String url = TunnelHelper.getTunnelUri(client);
		Map<String, String> info = TunnelHelper.getTunnelServiceInfo(client, vcap_service);
		String host = info.get("hostname");
		int port = Integer.valueOf(info.get("port"));
		String auth = TunnelHelper.getTunnelAuth(client);

		String svc_username = info.get("username");
		String svc_passwd = info.get("password");
		String svc_dbname = info.get("db") != null ? info.get("db") : info.get("name");
		String txt_dbname = info.get("db") != null ? "db" : "name";
		String svc_vhost = info.get("vhost");

		TunnelServer server = new TunnelServer(local, new HttpTunnelFactory(url, host, port, auth));

		server.start();

		System.out.println("Tunnel is running on " + LOCAL_HOST +" port " + LOCAL_PORT + " with auth=" + auth);
		if (svc_vhost != null) {
			System.out.println("Connect client with username=" + svc_username +" password=" + svc_passwd + " " + "vhost=" + svc_vhost);
		}
		else {
			System.out.println("Connect client with username=" + svc_username +" password=" + svc_passwd + " " + txt_dbname + "=" + svc_dbname);
		}
		while (true) {
			String command = console.readLine("Enter exit to stop: ");
			if (command.toLowerCase().equals("exit")) {
				break;
			}
		}
		server.stop();

		finalize(client);
		System.out.println("DONE!");
	}

	public static CloudFoundryClient clientInit() {
		CloudFoundryClient client = null;
		try {
			client = new CloudFoundryClient(vcap_email, vcap_passwd, CC_URL);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		client.login();
		return client;
	}

	public static void finalize(CloudFoundryClient client) {
		client.logout();
	}
}
