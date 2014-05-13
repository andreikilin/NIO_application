package com.aimprosoft.chat.netty.client;

public class ChatClientInit {
    public static void main(String[] args) {
        String userName = null;
        if(args.length > 0) {
            userName = args[0];
        } else {
            userName = "Anonymous";
        }
        new ChatClient("localhost", 8000, userName).run();
    }
}
