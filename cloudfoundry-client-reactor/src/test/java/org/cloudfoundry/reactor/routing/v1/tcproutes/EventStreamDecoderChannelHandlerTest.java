/*
 * Copyright 2013-2017 the original author or authors.
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

package org.cloudfoundry.reactor.routing.v1.tcproutes;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpContent;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public final class EventStreamDecoderChannelHandlerTest {

    @Test
    public void allData() throws Exception {
        String message = "data: This is the first message.\n" +
            "\n" +
            "data: This is the second message, it\n" +
            "data: has two lines.\n" +
            "\n" +
            "data: This is the third message.\n" +
            "\n";

        assertRead(message,
            ServerSentEvent.builder().data("This is the first message.").build(),
            ServerSentEvent.builder().data("This is the second message, it\nhas two lines.").build(),
            ServerSentEvent.builder().data("This is the third message.").build());
    }

    @Test
    public void colonSpacing() throws Exception {
        String message = "data:test\n" +
            "\n" +
            "data: test\n" +
            "\n";

        assertRead(message,
            ServerSentEvent.builder().data("test").build(),
            ServerSentEvent.builder().data("test").build());
    }

    @Test
    public void randomColons() throws Exception {
        String message = "data\n" +
            "\n" +
            "data\n" +
            "data\n" +
            "\n" +
            "data:";

        assertRead(message,
            ServerSentEvent.builder().data("").build(),
            ServerSentEvent.builder().data("\n").build());
    }

    @Test
    public void threeLines() throws Exception {
        String message = "data: YHOO\n" +
            "data: +2\n" +
            "data: 10\n" +
            "\n";

        assertRead(message,
            ServerSentEvent.builder().data("YHOO\n+2\n10").build());
    }

    @Test
    public void withComment() throws Exception {
        String message = ": test stream\n" +
            "\n" +
            "data: first event\n" +
            "id: 1\n" +
            "\n" +
            "data:second event\n" +
            "id\n" +
            "\n" +
            "data:  third event\n" +
            "\n";

        assertRead(message,
            ServerSentEvent.builder().id("1").data("first event").build(),
            ServerSentEvent.builder().id("").data("second event").build(),
            ServerSentEvent.builder().data(" third event").build()
        );
    }

    @Test
    public void withEventTypes() throws Exception {
        String message = "event: add\n" +
            "data: 73857293\n" +
            "\n" +
            "event: remove\n" +
            "data: 2153\n" +
            "\n" +
            "event: add\n" +
            "data: 113411\n" +
            "\n";

        assertRead(message,
            ServerSentEvent.builder().eventType("add").data("73857293").build(),
            ServerSentEvent.builder().eventType("remove").data("2153").build(),
            ServerSentEvent.builder().eventType("add").data("113411").build());
    }

    private void assertRead(String message, ServerSentEvent... expected) throws Exception {
        ChannelHandlerContext context = mock(ChannelHandlerContext.class, RETURNS_SMART_NULLS);
        DefaultHttpContent content = new DefaultHttpContent(Unpooled.copiedBuffer(message.toCharArray(), Charset.forName("UTF-8")));

        new EventStreamDecoderChannelHandler().channelRead(context, content);

        ArgumentCaptor<ServerSentEvent> captor = ArgumentCaptor.forClass(ServerSentEvent.class);
        verify(context, times(expected.length)).fireChannelRead(captor.capture());
        assertThat(captor.getAllValues()).containsExactly(expected);
    }

}
