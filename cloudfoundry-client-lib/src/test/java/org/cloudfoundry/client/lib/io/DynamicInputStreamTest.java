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

package org.cloudfoundry.client.lib.io;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.util.FileCopyUtils;

/**
 * Tests for {@link DynamicInputStream}.
 * 
 * @author Phillip Webb
 */
public class DynamicInputStreamTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private List<byte[]> writes = new ArrayList<byte[]>();

    private int index = 0;

    private int numberOfWriteMoreDataCalls;

    private DynamicInputStream inputStream = new DynamicInputStream() {

        @Override
        protected boolean writeMoreData() throws IOException {
            numberOfWriteMoreDataCalls++;
            if (index >= writes.size()) {
                return false;
            }
            getOutputStream().write(writes.get(index));
            index++;
            return (index < writes.size());
        }
    };

    private void mockWrite(byte[] data) {
        this.writes.add(data);
    }

    @Test
    public void shouldReadSingleByteWhenAvailable() throws Exception {
        mockWrite(new byte[] { -1 });
        assertThat(inputStream.read(), is(0x00FF));
        assertThat(inputStream.read(), is(-1));
    }

    @Test
    public void shouldNotReadSingleByteWhenNotAvailable() throws Exception {
        assertThat(inputStream.read(), is(-1));
    }

    @Test
    public void shouldNeedByteArray() throws Exception {
        thrown.expect(NullPointerException.class);
        inputStream.read(null, 0, 0);
    }

    @Test
    public void shouldNeedOffsetMoreThanZero() throws Exception {
        thrown.expect(IndexOutOfBoundsException.class);
        inputStream.read(new byte[] { 0x00 }, -1, 1);
    }

    @Test
    public void shouldNeedLengthMoreThanZero() throws Exception {
        thrown.expect(IndexOutOfBoundsException.class);
        inputStream.read(new byte[] { 0x00 }, 0, -1);
    }

    @Test
    public void shouldNeedLengthLessThanOrEqualToAvailableBuffer() throws Exception {
        thrown.expect(IndexOutOfBoundsException.class);
        inputStream.read(new byte[] { 0x00 }, 0, 2);
    }

    @Test
    public void shouldSupportSingleWriteMoreDataCall() throws Exception {
        mockWrite(new byte[] {0x00,0x01});
        byte[] b = readBytes();
        assertThat(b,is(equalTo(new byte[] {0x00,0x01})));
        assertThat(numberOfWriteMoreDataCalls, is(2));
    }

    @Test
    public void shouldSupportMultipleWriteMoreDataCalls() throws Exception {
        mockWrite(new byte[] {0x00,0x01});
        mockWrite(new byte[] {0x02,0x03});
        byte[] b = readBytes();
        assertThat(b,is(equalTo(new byte[] {0x00,0x01,0x02,0x03})));
        assertThat(numberOfWriteMoreDataCalls, is(3));

    }

    @Test
    public void shouldSupportCallThatWritesNoData() throws Exception {
        mockWrite(new byte[] {0x00,0x01});
        mockWrite(new byte[] {});
        mockWrite(new byte[] {0x02,0x03});
        mockWrite(new byte[] {});
        byte[] b = readBytes();
        assertThat(b,is(equalTo(new byte[] {0x00,0x01,0x02,0x03})));
        assertThat(numberOfWriteMoreDataCalls, is(4));
    }

    @Test
    public void shouldSupportZeroByteRead() throws Exception {
        byte[] b = new byte[1];
        int l = inputStream.read(b, 0, 0);
        assertThat(l, is(0));
    }
    
    private byte[] readBytes() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        FileCopyUtils.copy(inputStream, bos);
        return bos.toByteArray();
    }
}
