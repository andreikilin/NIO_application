package com.aimprosoft.chat.netty.client;

import com.aimprosoft.library.ChatMessage;
import com.aimprosoft.library.ProtobufWrapper;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ChatClient {

    private final String host;
    private final int port;
    private final String userName;
    private ProtobufWrapper pb;

    public ChatClient(String host, int port, String userName) {
        this.host = host;
        this.port = port;
        this.userName = userName;
        this.pb = new ProtobufWrapper();
    }

    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChatClientInitializer());

            Channel channel = b.connect(host, port).sync().channel();

            System.out.println("Your identity is " + channel.localAddress());

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while(true) {
                String message = in.readLine();
                ChatMessage.BaseData data = pb.buildUserData(userName, message);
                channel.writeAndFlush(data);
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
