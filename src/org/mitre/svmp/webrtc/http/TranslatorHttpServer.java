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
import org.mitre.svmp.webrtc.http.TranslatorHttpServerInitializer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class TranslatorHttpServer implements Runnable {

    private final int listenPort;
    
    // names are from the protobuf side's perspective, so
    //    sendQueue    = from the HTTP side, out the protobuf side
    //    receiveQueue = in the protobuf side, out the HTTP side 
    private BlockingQueue<SVMPProtocol.Response> sendQueue;
    private BlockingQueue<SVMPProtocol.Request> receiveQueue;

    public TranslatorHttpServer(int listenPort, 
            BlockingQueue<Response> sendQueue, 
            BlockingQueue<Request> receiveQueue) 
    {
        this.listenPort = listenPort;
        this.sendQueue = sendQueue;
        this.receiveQueue = receiveQueue;
    }

    public void run() {
        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new TranslatorHttpServerInitializer(sendQueue, receiveQueue));

            Channel ch = b.bind(listenPort).sync().channel();
            
            // read from the receive queue, translate into JSON, send to the "wait" connection
            // could make that driven through the handler probably
            
            ch.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
