/*
 * Copyright 2013 The MITRE Corporation, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this work except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mitre.svmp.webrtc.protobuf;

import java.util.concurrent.BlockingQueue;

import org.mitre.svmp.protocol.SVMPProtocol;
import org.mitre.svmp.protocol.SVMPProtocol.Request;
import org.mitre.svmp.protocol.SVMPProtocol.Request.RequestType;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class TranslatorProtobufClientHandler extends SimpleChannelInboundHandler<SVMPProtocol.Request> implements
        ChannelHandler {

    private BlockingQueue<SVMPProtocol.Request> receiveQueue;

    public TranslatorProtobufClientHandler(BlockingQueue<Request> receiveQueue) {
        this.receiveQueue = receiveQueue;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SVMPProtocol.Request msg) throws Exception {
        // should be a Request with a WebRTCMessage inside it
        // ignore anything else
        if (msg.getType() == RequestType.WEBRTC && msg.hasWebrtcMsg()) {
            // just queue it, let the HTTP side deal with the JSON translation
            receiveQueue.put(msg);
        }
    }


}
