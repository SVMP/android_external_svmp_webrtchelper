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

package org.mitre.svmp.webrtc.http;

import java.util.concurrent.BlockingQueue;

import org.mitre.svmp.protocol.SVMPProtocol;
import org.mitre.svmp.protocol.SVMPProtocol.Request;
import org.mitre.svmp.protocol.SVMPProtocol.Response;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;

public class TranslatorHttpServerInitializer extends ChannelInitializer<SocketChannel> {
    
    // names are from the protobuf side's perspective, so
    //    sendQueue    = from the HTTP side, out the protobuf side
    //    receiveQueue = in the protobuf side, out the HTTP side 
    private BlockingQueue<SVMPProtocol.Response> sendQueue;
    private BlockingQueue<SVMPProtocol.Request> receiveQueue;
    
    public TranslatorHttpServerInitializer(BlockingQueue<Response> sendQueue, 
            BlockingQueue<Request> receiveQueue) 
    {
        this.sendQueue = sendQueue;
        this.receiveQueue = receiveQueue;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipe = ch.pipeline();
        
//        pipe.addLast(new HttpResponseDecoder());
//        pipe.addLast("encoder", new HttpResponseEncoder());
        pipe.addLast(new HttpServerCodec());
        pipe.addLast(new PeerConnectionServerHandler(sendQueue, receiveQueue));
    }

}
