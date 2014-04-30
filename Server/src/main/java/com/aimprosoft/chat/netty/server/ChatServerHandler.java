package com.aimprosoft.chat.netty.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;

public class ChatServerHandler extends ChannelInboundMessageHandlerAdapter<String> {

    private static ChannelGroup channelGroup = new DefaultChannelGroup();


    @Override
    public void afterAdd (ChannelHandlerContext ctx) {
        Channel incomingChannel = ctx.channel();
        for (Channel channel : channelGroup) {
            if (channel != incomingChannel) {
                channel.write("[ Server has" + incomingChannel.remoteAddress() + " Joined\n");
            }
        }
        channelGroup.add(incomingChannel);
    }

    @Override
    public void afterRemove(ChannelHandlerContext ctx) throws Exception {
        Channel incomingChannel = ctx.channel();
        for (Channel channel : channelGroup) {
            if (channel != incomingChannel) {
                channel.write("[ Server has" + incomingChannel.remoteAddress() + " left\n");
            }
        }
        channelGroup.remove(incomingChannel);
    }

    public void messageReceived(ChannelHandlerContext ctx, String msg)
            throws Exception {
        Channel incomingChannel = ctx.channel();
        for (Channel channel : channelGroup) {
            if (channel != incomingChannel) {
                channel.write("[ " + channel.remoteAddress() + " ]" + msg + "\n");
            }
        }

    }

}
