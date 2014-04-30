package com.aimprosoft.chat.netty.client;

import io.netty.buffer.BufType;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class ChatClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {


        ch.pipeline().addLast("framer",
                new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()))
                .addLast("decoder", new StringDecoder(CharsetUtil.UTF_8))
                .addLast("encoder", new StringEncoder(BufType.BYTE, CharsetUtil.UTF_8))
                .addLast("handler", new ChatClientHandler());


    }
}
