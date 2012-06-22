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
import org.springframework.scheduling.concurrent.ExecutorConfigurationSupport;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

/**
 * A class responsible for starting and stopping the TunnelAcceptor based on the configuration options.
 *
 * @author Thomas Risberg
 */
public class TunnelServer {

	protected final Log logger = LogFactory.getLog(getClass());

	// configuration options
	private final InetSocketAddress local;
	private final TunnelFactory tunnelFactory;
	private final TaskExecutor taskExecutor;

	// variables to keep state for server
	private final ServerSocket serverSocket;
	private TunnelAcceptor acceptor;

	public TunnelServer(InetSocketAddress local, TunnelFactory tunnelFactory) {
		this(local, tunnelFactory, getDefaultThreadExecutor());
	}

	public TunnelServer(InetSocketAddress local, TunnelFactory tunnelFactory, TaskExecutor taskExecutor) {
		this.local = local;
		this.tunnelFactory = tunnelFactory;
		this.taskExecutor = taskExecutor;
		try {
			this.serverSocket = new ServerSocket();
			serverSocket.setReuseAddress(true);
			serverSocket.bind(local);
		} catch (IOException e) {
			throw new TunnelException("Error configuring server socket", e);
		}
	}

	public void start() {
		logger.info("Starting server on " + local);
		initializeTaskExecutor(taskExecutor);
		synchronized (this) {
			if (acceptor == null) {
				this.acceptor = new TunnelAcceptor(serverSocket, tunnelFactory, taskExecutor);
				acceptor.start();
			}
			else {
				throw new TunnelException("Server already running.");
			}
		}
	}

	public void stop() {
		logger.info("Stopping server on " + local);
		synchronized (this) {
			shutdownTaskExecutor(taskExecutor);
			if (acceptor != null) {
				acceptor.stop();
				if (acceptor.isActive()) {
					logger.info("Server is actively servicing connections, waiting for client to close");
					while (acceptor.isActive()) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException ignore) {}
					}
				}
				try {
					serverSocket.close();
				} catch (IOException e) {
					logger.warn("Error while closing server socket" + e.getMessage());
				}
				logger.info("Server on " + local + " is now stopped");
			}
			else {
				throw new TunnelException("Server is not running.");
			}
		}
	}

	protected static void initializeTaskExecutor(TaskExecutor taskExecutor) {
		if (taskExecutor instanceof ExecutorConfigurationSupport) {
			((ExecutorConfigurationSupport)taskExecutor).initialize();
		}
	}

	protected static void shutdownTaskExecutor(TaskExecutor taskExecutor) {
		if (taskExecutor instanceof ExecutorConfigurationSupport) {
			((ExecutorConfigurationSupport)taskExecutor).shutdown();
		}
	}

	protected static TaskExecutor getDefaultThreadExecutor() {
		ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
		te.setCorePoolSize(5);
		te.setMaxPoolSize(10);
		te.setQueueCapacity(100);
		return te;
	}
}
