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

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

public class TranslatorProtobufClientInitializer extends ChannelInitializer<SocketChannel> {

    private BlockingQueue<SVMPProtocol.Request> receiveQueue;
    
    public TranslatorProtobufClientInitializer(BlockingQueue<Request> receiveQueue) {
        this.receiveQueue = receiveQueue;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipe = ch.pipeline();
        
        pipe.addLast("delimiterDecode", new ProtobufVarint32FrameDecoder());
        pipe.addLast("protobufDecode", new ProtobufDecoder(SVMPProtocol.Request.getDefaultInstance()));
        
        pipe.addLast("delimiterEncode", new ProtobufVarint32LengthFieldPrepender());
        pipe.addLast("protobufEncode", new ProtobufEncoder());
        
        pipe.addLast("protobufClientHandler", new TranslatorProtobufClientHandler(receiveQueue));
    }
}
