/*
 * Copyright 2009-2011 the original author or authors.
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

package org.cloudfoundry.client.lib;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.InputStreamResource;

@Deprecated
public class InputStreamResourceWithName extends InputStreamResource {
	
	private long length;
	private String filename;
	
	public InputStreamResourceWithName(InputStream in, long length, String filename) {
		super(in);
		this.length = length;
		this.filename = filename;
	}

	@Override
	public long contentLength() throws IOException {
		return length;
	}
	
	@Override
	public String getFilename() throws IllegalStateException {
		return filename;
	}
	
}
