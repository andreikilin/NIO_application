package com.aimprosoft.library;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.InvalidProtocolBufferException;
import com.aimprosoft.library.ChatMessage.UserData;
import com.aimprosoft.library.ChatMessage;

import java.util.Date;

//import com.google.protobuf.*;
public class ProtobufBilder {

    ChatMessage.User user = ChatMessage.User.newBuilder().setName("Chacky").build();
    ChatMessage.Date date = ChatMessage.Date.newBuilder().setNumberformat(new Date().getTime()).build();
    ChatMessage.Msg msg = ChatMessage.Msg.newBuilder().setText("Hello world").build();
    ExtensionRegistry registry = ExtensionRegistry.newInstance();


    UserData userData = UserData.newBuilder()
            .setUser(user)
            .setDate(date)
            .setMsg(msg)
            .build();


    byte[] bytes = new byte[]{1,2,3};

    public ProtobufBilder() throws InvalidProtocolBufferException {
        ChatMessage.registerAllExtensions(registry);
    }


}
