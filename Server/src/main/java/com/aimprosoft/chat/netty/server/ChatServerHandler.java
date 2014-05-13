package com.aimprosoft.chat.netty.server;

import com.aimprosoft.library.ChatMessage;
import com.aimprosoft.library.ChatMessage.BaseData;
import com.aimprosoft.library.ChatMessage.ServerData;
import com.aimprosoft.library.ProtobufWrapper;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.GeneratedMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Date;

public class ChatServerHandler extends ChannelInboundHandlerAdapter {

    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private ProtobufWrapper pb = new ProtobufWrapper();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof BaseData) {
            BaseData data = (BaseData) msg;
            Channel incomingChannel = ctx.channel();
            for (Channel channel : channelGroup) {
                if (channel != incomingChannel) {
                    channel.writeAndFlush(data);
                }
            }
//            System.out.println(data.getUser().getName() + "wrote: " + data.getMsg().getText());
        } else {
            System.err.println("Message received not instance of message!");
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        Channel incomingChannel = ctx.channel();
        BaseData serverData = null;
        for (Channel channel : channelGroup) {
            if (channel != incomingChannel) {
                serverData = pb.buildServerData(incomingChannel.remoteAddress() + " Joined\n");
            } else {
                serverData = pb.buildServerData("Welcome to Netty server working with protobuf");
            }
            channel.writeAndFlush(serverData);
        }
        channelGroup.add(incomingChannel);
    }

}
