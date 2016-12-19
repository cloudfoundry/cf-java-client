/*
 * Copyright 2013-2016 the original author or authors.
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

package org.cloudfoundry.reactor.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import reactor.ipc.netty.http.client.HttpClientResponse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MultipartDecoderChannelHandler extends ChannelInboundHandlerAdapter {

    public static final String CLOSE_DELIMITER = "CLOSE_DELIMITER";

    public static final String DELIMITER = "DELIMITER";

    private static final Pattern BOUNDARY_PATTERN = Pattern.compile("multipart/.+; boundary=(.*)");

    private static final char[] CRLF = new char[]{'\r', '\n'};

    private static final char[] DOUBLE_DASH = new char[]{'-', '-'};

    private final char[] boundary;

    private int bodyPosition;

    private int boundaryPosition;

    private ByteBuf byteBuf;

    private int crlfPosition;

    private int delimiterPosition;

    private int doubleDashPosition;

    private int position;

    private Stage stage;

    public MultipartDecoderChannelHandler(HttpClientResponse response) {
        this.boundary = extractMultipartBoundary(response);
        reset();
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object message) throws Exception {
        if (!(message instanceof DefaultHttpContent)) {
            super.channelRead(context, message);
            return;
        }

        ByteBuf byteBuf = ((DefaultHttpContent) message).content();
        this.byteBuf = this.byteBuf != null ? Unpooled.wrappedBuffer(this.byteBuf, byteBuf) : byteBuf;

        while (this.position < this.byteBuf.readableBytes()) {
            char c = getChar();

            switch (this.stage) {
                case BODY:
                    body(c);
                    break;
                case BOUNDARY:
                    boundary(c);
                    break;
                case END_CRLF:
                    endCrLf(context, c);
                    break;
                case END_DOUBLE_DASH:
                    endDoubleDash(context, c);
                    break;
                case START_CRLF:
                    startCrLf(c);
                    break;
                case START_DOUBLE_DASH:
                    startDoubleDash(c);
                    break;
                case TRAILING_CRLF:
                    trailingCrLf(context, c);
                    break;
            }
        }

        if (Stage.BODY == this.stage) {
            sendTrailingBody(context);
            reset();
        }
    }

    private static char[] extractMultipartBoundary(HttpClientResponse response) {
        String contentType = response.responseHeaders().get(HttpHeaderNames.CONTENT_TYPE);
        Matcher matcher = BOUNDARY_PATTERN.matcher(contentType);
        if (matcher.matches()) {
            return matcher.group(1).toCharArray();
        } else {
            throw new IllegalStateException(String.format("Content-Type %s does not contain a valid multipart boundary", contentType));
        }
    }

    private void body(char c) {
        if (CRLF[0] == c) {
            this.delimiterPosition = this.position;
            this.stage = Stage.START_CRLF;
            this.crlfPosition = 1;
            this.position++;
        } else if (DOUBLE_DASH[0] == c) {
            this.delimiterPosition = this.position;
            this.stage = Stage.START_DOUBLE_DASH;
            this.doubleDashPosition = 1;
            this.position++;
        } else {
            this.position++;
        }
    }

    private void boundary(char c) {
        if (this.boundaryPosition < this.boundary.length) {
            if (this.boundary[this.boundaryPosition] == c) {
                this.boundaryPosition++;
                this.position++;
            } else {
                this.stage = Stage.BODY;
            }
        } else {
            if (CRLF[0] == c) {
                this.stage = Stage.END_CRLF;
                this.crlfPosition = 1;
                this.position++;
            } else if (DOUBLE_DASH[0] == c) {
                this.stage = Stage.END_DOUBLE_DASH;
                this.doubleDashPosition = 1;
                this.position++;
            } else {
                this.stage = Stage.BODY;
            }
        }
    }

    private void endCrLf(ChannelHandlerContext context, char c) {
        if (this.crlfPosition < CRLF.length) {
            if (CRLF[this.crlfPosition] == c) {
                this.crlfPosition++;
                this.position++;
            } else {
                this.stage = Stage.BODY;
            }
        } else {
            if (CRLF[0] == c) {
                this.stage = Stage.TRAILING_CRLF;
                this.crlfPosition = 1;
                this.position++;
            } else {
                sendBody(context);
                sendDelimiter(context);
            }
        }
    }

    private void endDoubleDash(ChannelHandlerContext context, char c) {
        if (this.doubleDashPosition < DOUBLE_DASH.length) {
            if (DOUBLE_DASH[this.doubleDashPosition] == c) {
                this.doubleDashPosition++;
                this.position++;
            } else {
                this.stage = Stage.BODY;
            }
        } else {
            sendBody(context);
            sendCloseDelimiter(context);
        }
    }

    private char getChar() {
        return (char) (this.byteBuf.getByte(this.position) & 0xFF);
    }

    private void reset() {
        if (this.byteBuf != null) {
            this.byteBuf.release();
            this.byteBuf = null;
        }

        this.bodyPosition = 0;
        this.position = 0;
        this.stage = Stage.BODY;
    }

    private void sendBody(ChannelHandlerContext context) {
        sendBody(context, this.bodyPosition, this.delimiterPosition);
        this.bodyPosition = this.position;
    }

    private void sendBody(ChannelHandlerContext context, int start, int end) {
        int length = end - start;
        if (length > 0) {
            context.fireChannelRead(this.byteBuf.slice(start, length).retain());
        }
    }

    private void sendCloseDelimiter(ChannelHandlerContext context) {
        context.fireChannelRead(CLOSE_DELIMITER);
        this.stage = Stage.BODY;
    }

    private void sendDelimiter(ChannelHandlerContext context) {
        context.fireChannelRead(DELIMITER);
        this.stage = Stage.BODY;
    }

    private void sendTrailingBody(ChannelHandlerContext context) {
        sendBody(context, this.bodyPosition, this.position);
    }

    private void startCrLf(char c) {
        if (this.crlfPosition < CRLF.length) {
            if (CRLF[this.crlfPosition] == c) {
                this.crlfPosition++;
                this.position++;
            } else {
                this.stage = Stage.BODY;
            }
        } else {
            if (DOUBLE_DASH[0] == c) {
                this.stage = Stage.START_DOUBLE_DASH;
                this.doubleDashPosition = 1;
                this.position++;
            } else {
                this.stage = Stage.BODY;
            }
        }
    }

    private void startDoubleDash(char c) {
        if (this.doubleDashPosition < DOUBLE_DASH.length) {
            if (DOUBLE_DASH[this.doubleDashPosition] == c) {
                this.doubleDashPosition++;
                this.position++;
            } else {
                this.stage = Stage.BODY;
            }
        } else {
            if (this.boundary[0] == c) {
                this.stage = Stage.BOUNDARY;
                this.boundaryPosition = 1;
                this.position++;
            } else {
                this.stage = Stage.BODY;
            }
        }
    }

    private void trailingCrLf(ChannelHandlerContext context, char c) {
        if (this.crlfPosition < CRLF.length) {
            if (CRLF[this.crlfPosition] == c) {
                this.crlfPosition++;
                this.position++;
            } else {
                this.stage = Stage.BODY;
            }
        } else {
            sendBody(context);
            sendDelimiter(context);
        }
    }

    private enum Stage {

        BODY,

        BOUNDARY,

        END_CRLF,

        END_DOUBLE_DASH,

        START_CRLF,

        START_DOUBLE_DASH,

        TRAILING_CRLF

    }

}
