package com.aimprosoft.chat.netty.client;

import com.aimprosoft.library.ProtobufWrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import com.aimprosoft.library.ChatMessage.UserData;
import com.aimprosoft.library.ChatMessage.ServerData;

import java.util.Date;

public class ChatClientHandler extends ChannelInboundHandlerAdapter {

    private ProtobufWrapper pb = new ProtobufWrapper();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String user = null;
        String message = null;
        Long date = null;
        Object obj = pb.returnData(msg);
        if (obj instanceof UserData) {
            UserData data = (UserData) obj;
            user = data.getUser().getName();
            message =data.getMsg().getText();
            date = data.getDate().getNumberformat();
            System.out.println("[" + new Date(date) + "] " + user + ": " + message);
        } else if (obj instanceof ServerData) {
            ServerData data = (ServerData) obj;
            message =data.getMsg().getText();
            date = data.getDate().getNumberformat();
            System.out.println("[" + new Date(date) + "]Server : " + message);
        }else {
            System.err.println("Message received not instance of ChatMessage!");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        // TODO Auto-generated method stub
            super.exceptionCaught(ctx, cause);
    }

}
