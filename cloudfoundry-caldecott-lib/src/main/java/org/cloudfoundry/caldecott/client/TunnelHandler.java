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

package org.cloudfoundry.caldecott.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cloudfoundry.caldecott.TunnelException;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Observable;

/**
 * The class responsible for handling the actual tunneling communications between a data access client and the
 * Caldecott server app.
 *
 * @author Thomas Risberg
 */
public class TunnelHandler extends Observable {

	protected final Log logger = LogFactory.getLog(getClass());

	// configuration options
	private final Socket socket;
	private final TunnelFactory tunnelFactory;
	private final TaskExecutor taskExecutor;

	// variables to keep state for the tunnel setup
	private Client client;
	private Tunnel tunnel;

	// variable to keep handler active
	private volatile boolean shutdown = false;


	public TunnelHandler(Socket socket, TunnelFactory tunnelFactory, TaskExecutor taskExecutor) {
		this.socket = socket;
		this.tunnelFactory = tunnelFactory;
		this.taskExecutor = taskExecutor;
		try {
			this.socket.setSoTimeout(0);
		} catch (SocketException ignore) {}
	}

	public void start() {
		client = new SocketClient(socket);
		tunnel = tunnelFactory.createTunnel();
		taskExecutor.execute(new Writer());
		taskExecutor.execute(new Reader());
		if (logger.isDebugEnabled()) {
			logger.debug("Completed start of: " + this.getClass().getSimpleName() + " with " + countObservers() + " observers");
		}
	}

	public void poke() {
		if (client.isIdle()) {
			shutdown = true;
		}
	}

	public void stop() {
		try {
			InputStream is = socket.getInputStream();
			if (is != null) {
				is.close();
			}
		} catch (IOException ignore) {}
		try {
			OutputStream os = socket.getOutputStream();
			if (os != null) {
				os.close();
			}
		} catch (IOException ignore) {}
		if (logger.isDebugEnabled()) {
			logger.debug("Closing tunnel: " + tunnel.toString());
		}
		tunnel.close();
		if (logger.isDebugEnabled()) {
			logger.debug("Notifying observers: " + countObservers());
		}
		setChanged();
		notifyObservers("CLOSED");
	}


	private class Writer implements Runnable {

		public void run() {
			if (logger.isDebugEnabled()) {
				logger.debug("Starting new writer thread: " + this);
			}
			try {
				while (client.isOpen()) {
					byte[] in = client.read();
					if (in.length > 0) {
						tunnel.write(in);
					}
					if (shutdown && client.isIdle()) {
						if (logger.isDebugEnabled()) {
							logger.debug("Shutdown requested and idle connection thread will be closed: " + this);
						}
						client.forceClose();
						stop();
					}
				}
			} catch (IOException e) {
				throw new TunnelException("Error while processing streams", e);
			}
			if (!shutdown) {
				stop();
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Completed writer thread for: " + this);
			}
		}

	}

	private class Reader implements Runnable {
		public void run() {
			if (logger.isDebugEnabled()) {
				logger.debug("Starting new reader thread: " + this);
			}
			boolean retry = false;
			try {
				while (client.isOpen()) {
					try {
						byte[] out = tunnel.read(retry);
						retry = false;
						client.write(out);
					} catch (HttpStatusCodeException hsce) {
						if (hsce.getStatusCode().value() == 504) {
							retry = true;
							if (logger.isTraceEnabled()) {
								logger.trace("Retrying tunnel read after receiving " + hsce.getStatusCode().value());
							}
						}
						else if (hsce.getStatusCode().value() == 404) {
							retry = false;
							if (logger.isDebugEnabled()) {
								logger.debug("Tunnel error - [" + hsce.getStatusCode().value() + "] " + hsce.getStatusText());
							}
						}
						else if (hsce.getStatusCode().value() == 410) {
							retry = false;
							if (logger.isDebugEnabled()) {
								logger.debug("Tunnel error - [" + hsce.getStatusCode().value() + "] " + hsce.getStatusText());
							}
						}
						else {
							logger.warn("Received HTTP Error: [" + hsce.getStatusCode().value() + "] " + hsce.getStatusText());
							throw new TunnelException("Error while reading from tunnel", hsce);
						}
					}
				}
			} catch (IOException ioe) {
				throw new TunnelException("Error while processing streams", ioe);
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Completed reader thread for: " + this);
			}
		}

	}

}
