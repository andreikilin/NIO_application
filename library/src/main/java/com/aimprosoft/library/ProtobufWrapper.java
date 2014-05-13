package com.aimprosoft.library;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.Date;

public class ProtobufWrapper {

    private ChatMessage.Date sysDate;
    private ExtensionRegistry registry;

    public ProtobufWrapper() {
        this.registry = ExtensionRegistry.newInstance();
        ChatMessage.registerAllExtensions(registry);
    }

    static <Type> ChatMessage.BaseData wrap(ChatMessage.BaseData.DataType type, GeneratedMessage.GeneratedExtension<ChatMessage.BaseData, Type> extension, Type cmd) {
        return ChatMessage.BaseData.newBuilder().setDataType(type).setExtension(extension, cmd).build();
    }

    public ChatMessage.BaseData buildServerData(String message) {
        sysDate = ChatMessage.Date.newBuilder().setNumberformat(new Date().getTime()).build();
        ChatMessage.Msg msg = ChatMessage.Msg.newBuilder().setText(message).build();
        ChatMessage.ServerData data = ChatMessage.ServerData.newBuilder().setDate(sysDate).setMsg(msg).build();
        return wrap(ChatMessage.BaseData.DataType.SERVERDATA, ChatMessage.ServerData.dt, data);
    }

    public ChatMessage.BaseData buildUserData(String userName, String message) {
        sysDate = ChatMessage.Date.newBuilder().setNumberformat(new Date().getTime()).build();
        ChatMessage.Msg msg = ChatMessage.Msg.newBuilder().setText(message).build();
        ChatMessage.User user = ChatMessage.User.newBuilder().setName(userName).build();
        ChatMessage.UserData data = ChatMessage.UserData.newBuilder().setDate(sysDate).setUser(user).setMsg(msg).build();
        return wrap(ChatMessage.BaseData.DataType.USERDATA, ChatMessage.UserData.dt, data);
    }

    public ChatMessage.BaseData decodeIt(byte[] bytes) throws InvalidProtocolBufferException {
        return ChatMessage.BaseData.newBuilder().mergeFrom(bytes, registry).build();
    }

    public Object returnData(Object msg) throws Exception {
        ChatMessage.BaseData obj = (ChatMessage.BaseData) msg;
        ChatMessage.BaseData data = this.decodeIt(obj.toByteArray());
        switch (data.getDataType()) {
            case USERDATA:
                ChatMessage.UserData userData = data.getExtension(ChatMessage.UserData.dt);
//                message= data.getExtension(ChatMessage.UserData.dt).getMsg().getText();
//                date = data.getExtension(ChatMessage.UserData.dt).getDate().getNumberformat();
//                String user = data.getExtension(ChatMessage.UserData.dt).getUser().getName();
//                System.out.println("[" + new Date(date) + "] " + user + ": " + message);
                return userData;
            case SERVERDATA:
                ChatMessage.ServerData serverData = data.getExtension(ChatMessage.ServerData.dt);
//                message= data.getExtension(ChatMessage.ServerData.dt).getMsg().getText();
//                date = data.getExtension(ChatMessage.ServerData.dt).getDate().getNumberformat();
//                System.out.println("[" + new Date(date) + "]Server : " + message);
                return serverData;
            default:
                return null;
        }
    }

}
