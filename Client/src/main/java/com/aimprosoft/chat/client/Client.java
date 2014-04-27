package com.aimprosoft.chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Client {
    String myIdentity;

    public Client(String pIdentity) {
        myIdentity = pIdentity;
    }

    void talkToServer() {
        try {
            SocketChannel mySocket = SocketChannel.open();
            mySocket.configureBlocking(false);
            mySocket.connect(new InetSocketAddress(InetAddress.getLocalHost(), 10523));
            Selector selector = Selector.open();
            mySocket.register(selector, SelectionKey.OP_CONNECT);
            while (selector.select() > 0) {
                Set keys = selector.selectedKeys();
                Iterator iterator = keys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey selectionKey = (SelectionKey) iterator.next();
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                    iterator.remove();
                    if (selectionKey.isConnectable()) {
                        if (socketChannel.isConnectionPending()) {
                            socketChannel.finishConnect();
                            System.out.println("Connection was pending but now is finished connecting.");
                        }
                        ByteBuffer byteBuffer = null;
                        BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
                        byteBuffer = ByteBuffer.wrap(new String("I am Client : " + myIdentity).getBytes());
                        socketChannel.write(byteBuffer);
                        byteBuffer.clear();
                        while (true) {
                            String message = bufferRead.readLine();
                            byteBuffer = ByteBuffer.wrap(message.getBytes());
                            socketChannel.write(byteBuffer);
                            byteBuffer.clear();
                            synchronized (this) {
                                try {
                                    wait(3000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            if(message.equals("close")){
                                mySocket.close();

                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            Client client = new Client(args[0]);
            client.talkToServer();
        } else {
            System.out.println("Write name");
        }
    }
}
