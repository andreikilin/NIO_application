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

            // non blocking
            mySocket.configureBlocking(false);

            // connect to a running server
            mySocket.connect(new InetSocketAddress(InetAddress.getLocalHost(), 10523));

            // get a selector
            Selector selector = Selector.open();

            // register the client socket with "connect operation" to the selector
            mySocket.register(selector, SelectionKey.OP_CONNECT);

            // select() blocks until something happens on the underlying socket
            while (selector.select() > 0) {

                Set keys = selector.selectedKeys();
                Iterator it = keys.iterator();

                while (it.hasNext()) {

                    SelectionKey key = (SelectionKey) it.next();

                    SocketChannel myChannel = (SocketChannel) key.channel();

                    it.remove();

                    if (key.isConnectable()) {
                        if (myChannel.isConnectionPending()) {
                            myChannel.finishConnect();
                            System.out.println("Connection was pending but now is finished connecting.");
                        }

                        ByteBuffer bb = null;
                        BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
                        bb = ByteBuffer.wrap(new String("I am Client : " + myIdentity).getBytes());
                        myChannel.write(bb);
                        bb.clear();

                        while (true) {
                            String message = bufferRead.readLine();
                            bb = ByteBuffer.wrap(message.getBytes());
                            myChannel.write(bb);
                            bb.clear();

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

        Client client = new Client(args[0]);
        client.talkToServer();
    }


}
