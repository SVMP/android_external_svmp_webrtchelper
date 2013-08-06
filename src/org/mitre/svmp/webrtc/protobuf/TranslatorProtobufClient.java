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

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TranslatorProtobufClient implements Runnable {
    private final String host;
    private final int port;
    
    // names are from the protobuf side's perspective, so
    //    sendQueue    = from the HTTP side, out the protobuf side
    //    receiveQueue = in the protobuf side, out the HTTP side 
    private BlockingQueue<SVMPProtocol.Response> sendQueue;
    private BlockingQueue<SVMPProtocol.Request> receiveQueue;

    public TranslatorProtobufClient(String host, int port, 
            BlockingQueue<SVMPProtocol.Response> sendQueue, 
            BlockingQueue<SVMPProtocol.Request> receiveQueue) 
    {
        this.host = host;
        this.port = port;
        this.sendQueue = sendQueue;
        this.receiveQueue = receiveQueue;
    }

    public void run() {
        while (true) {
            EventLoopGroup group = new NioEventLoopGroup();

            try {
                Bootstrap b = new Bootstrap()
                        .group(group)
                        .channel(NioSocketChannel.class)
                        .handler(new TranslatorProtobufClientInitializer(receiveQueue));
    
                Channel ch = b.connect(host, port).sync().channel();
                System.out.println("Protobuf client connected.");
    
                ChannelFuture lastWriteFuture = null;
                while (true) {
                    lastWriteFuture = ch.writeAndFlush(sendQueue.take());
                }
                // unreachable until we add a way to break the while loop
                //if (lastWriteFuture != null) {
                //    lastWriteFuture.sync();
                //}
            } catch (Exception e) {
                System.err.println(e.getMessage());
            } finally {
                // Free resources from EventLoopGroup
                group.shutdownGracefully();
            }
            try {
                System.out.println("Protobuf client waiting 5 seconds before reconnecting to upstream");
                Thread.sleep(5000);
            } catch (InterruptedException e) {}
        }
    }
}
