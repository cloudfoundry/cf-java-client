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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The class responsible for listening for client connection attempts and handing off to
 * a TunnelHandler for handling the actual tunneling communications.
 *
 * @author Thomas Risberg
 */
public class TunnelAcceptor implements Runnable {

	public static final int SOCKET_TIMEOUT = 10000;

	protected final Log logger = LogFactory.getLog(getClass());

	// configuration options
	private final TunnelFactory tunnelFactory;
	private final ServerSocket serverSocket;
	private final TaskExecutor taskExecutor;

	// variable to keep acceptor active
	// this is volatile since it can we altered by another thread via stop()
	private volatile boolean keepGoing = true;

	// concurrent map to keep track of handlers, will be modified from handler threads
	private ConcurrentHashMap<TunnelHandler, Boolean> handlers = new ConcurrentHashMap<TunnelHandler, Boolean>();

	public TunnelAcceptor(ServerSocket serverSocket, TunnelFactory tunnelFactory, TaskExecutor taskExecutor) {
		this.serverSocket = serverSocket;
		this.tunnelFactory = tunnelFactory;
		this.taskExecutor = taskExecutor;
		try {
			this.serverSocket.setSoTimeout(SOCKET_TIMEOUT);
		} catch (SocketException ignore) {}
	}

	public void start() {
		logger.info("Starting new acceptor thread: " + this);
		taskExecutor.execute(this);
		logger.debug("Completed start of: " + taskExecutor);
	}

	public boolean isActive() {
		return handlers.size() > 0;
	}

	public void stop() {
		logger.info("Stop requested for: " + this);
		keepGoing = false;
	}

	public void run() {
		while (keepGoing) {
			try {
				logger.trace("Waiting for client connection");
				Socket sourceSocket = serverSocket.accept();
				logger.debug("Accepted client connection");
				TunnelHandler handler = new TunnelHandler(sourceSocket, tunnelFactory, taskExecutor);
				handler.addObserver(new Observer() {
					public void update(Observable observable, Object o) {
						if (logger.isDebugEnabled()) {
							logger.debug("Notified that " + observable + " is now " + o);
						}
						handlers.remove(observable);
					}
				});
				handlers.put(handler, true);
				handler.start();
			}
			catch (SocketTimeoutException ste) {}
			catch (IOException e) {
				if (!keepGoing && serverSocket.isClosed()) {
					// time to quit so we can ignore this exception
				}
				else {
					throw new TunnelException("Error while accepting connections", e);
				}
			}
		}
		if (!handlers.isEmpty()) {
			while (!handlers.isEmpty()) {
				logger.debug("Waiting for " + handlers.size() + " client connections to close");
				for (TunnelHandler handler : handlers.keySet()) {
					logger.debug("Poking " + handler);
					handler.poke();
				}
				try {
					Thread.sleep(10000);
				} catch (InterruptedException ignore) {}
			}
		}
		logger.info("Completed acceptor thread for: " + this);
	}
}
