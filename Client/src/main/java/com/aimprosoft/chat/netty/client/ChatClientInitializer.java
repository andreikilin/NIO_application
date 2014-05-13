package com.aimprosoft.chat.netty.client;

import com.aimprosoft.library.ChatMessage;
import com.aimprosoft.library.ChatMessage.UserData;
import com.aimprosoft.library.ChatMessage.ServerData;
import com.google.protobuf.ExtensionRegistry;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;


public class ChatClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ExtensionRegistry registry = ExtensionRegistry.newInstance();
        ChatMessage.registerAllExtensions(registry);
        ch.pipeline()
                .addLast("frameEncoder", new LengthFieldPrepender(4))
                .addLast("protobufEncoder", new ProtobufEncoder())
                .addLast("frameDecoder", new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4))
                .addLast("protobufServerMessageDecoder", new ProtobufDecoder(ChatMessage.BaseData.getDefaultInstance(), registry))
                .addLast("handler", new ChatClientHandler());


    }
}
