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

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

/**
 */
public class SocketClient implements Client {

	protected final Log logger = LogFactory.getLog(getClass());

	// configuration options for the socket
	private final Socket socket;

	// variables to keep track of communication state with the client
	private boolean active = true;

	public SocketClient(Socket socket) {
		this.socket = socket;
	}

	public byte[] read() throws IOException {
		byte[] bytes = new byte[1024];
		int len;
		len = socket.getInputStream().read(bytes);
		if (len < 0) {
			if (len < 0) {
				if (logger.isDebugEnabled())
					logger.debug("[" + len + "] detected closed stream");
				active = false;
			}
			len = 0;
		}
		else {
			if (logger.isTraceEnabled())
				logger.trace("[" + len + " bytes] read from stream");
		}
		return Arrays.copyOfRange(bytes, 0, len);
	}

	public void write(byte[] data) throws IOException {
		OutputStream s = socket.getOutputStream();
		s.write(data);
		s.flush();
		if (logger.isTraceEnabled())
			logger.trace("[" + data.length + " bytes] written to stream");
	}

	public boolean isActive() {
		return active;
	}

}
