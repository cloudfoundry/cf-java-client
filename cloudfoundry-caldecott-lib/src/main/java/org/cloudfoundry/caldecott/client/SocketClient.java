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

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;

/**
 * The Socket implementation of a Client designed to interact with data access clients.
 *
 * @author Thomas Risberg
 */
public class SocketClient implements Client {

	private static final int SOCKET_TIMEOUT = 30000;

	protected final Log logger = LogFactory.getLog(getClass());

	// configuration options for the socket
	private final Socket socket;

	// variables to keep track of communication state with the client
	private volatile boolean open = true;

	private volatile boolean idle = false;


	public SocketClient(Socket socket) {
		this.socket = socket;
		try {
			this.socket.setSoTimeout(SOCKET_TIMEOUT);
		} catch (SocketException e) {
			throw new TunnelException("Unable to set timeout on socket " + e.getMessage());
		}
	}

	public byte[] read() throws IOException {
		if (!open) {
			return null;
		}
		byte[] bytes = new byte[1024];
		int len;
		try {
			len = socket.getInputStream().read(bytes);
			idle = false;
		}
		catch (SocketTimeoutException e) {
			len = 0;
			System.out.println(e);
			if (logger.isTraceEnabled()) {
				logger.trace("Timeout on read " + e);
			}
			idle = true;
		}
		if (len < 0) {
			if (len < 0) {
				if (logger.isDebugEnabled()) {
					logger.debug("[" + len + "] detected closed stream");
				}
				open = false;
			}
			len = 0;
		}
		else {
			if (logger.isTraceEnabled() && len > 0) {
				logger.trace("[" + len + " bytes] read from stream");
			}
		}
		return Arrays.copyOfRange(bytes, 0, len);
	}

	public void write(byte[] data) throws IOException {
		if (!open) {
			return;
		}
		idle = false;
		OutputStream s = socket.getOutputStream();
		s.write(data);
		s.flush();
		if (logger.isTraceEnabled()) {
			logger.trace("[" + data.length + " bytes] written to stream");
		}
	}

	public boolean isOpen() {
		return open;
	}

	public boolean isIdle() {
		return idle;
	}

	public void forceClose() {
		logger.debug("Force close requested for " + this);
		open = false;
	}

}
