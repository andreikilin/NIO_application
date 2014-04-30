package com.aimprosoft.chat.netty.server;

public class ChatServerInit {

    public static void main(String[] args) {
        new ChatServer(8000).run();
    }
}
