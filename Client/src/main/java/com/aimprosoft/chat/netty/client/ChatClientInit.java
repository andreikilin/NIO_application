package com.aimprosoft.chat.netty.client;

public class ChatClientInit {
    public static void main(String[] args) {
        new ChatClient("localhost", 8000).run();
    }
}
