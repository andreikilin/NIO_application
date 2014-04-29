package com.aimprosoft.chat.server;

import com.aimprosoft.library.ByteUtil;
import com.aimprosoft.library.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class Server implements Runnable {
    private final int port;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private ByteBuffer byteBuffer = ByteBuffer.allocate(256);
    private final ByteBuffer welcomeBuffer = ByteBuffer.wrap("Welcome to NioChat!\n".getBytes());
    private boolean running = true;

    Server(int port) throws IOException {
        this.port = port;
        this.serverSocketChannel = ServerSocketChannel.open();
        this.serverSocketChannel.socket().bind(new InetSocketAddress(port));
        this.serverSocketChannel.configureBlocking(false);
        this.selector = Selector.open();
        this.serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    @Override
    public void run() {

            try {
                System.out.println("Server starting on port " + this.port);

                Iterator<SelectionKey> iterator;
                SelectionKey key;
                while (this.running && this.serverSocketChannel.isOpen()) {
                    //Check activity
                    if(selector.select() == 0) {
                        continue;
                    }
                    iterator = this.selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        key = iterator.next();
                        iterator.remove();

                        if (key.isAcceptable()) this.handleAccept(key);
                        if (key.isReadable()) this.handleRead(key);
                    }
                }
            } catch (IOException e) {
                System.out.println("IOException, server of port " + this.port + " terminating. Stack trace:");
                e.printStackTrace();
            }

    }

    private void handleAccept(SelectionKey key) throws IOException {
        SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
        String address = (new StringBuilder(socketChannel.socket().getInetAddress().toString())).append(":").append(socketChannel.socket().getPort()).toString();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ, address);
        socketChannel.write(welcomeBuffer);
        welcomeBuffer.rewind();
        System.out.println("accepted connection from: " + address);
    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        StringBuilder stringBuilder = new StringBuilder();
        List<Message> msglist = new LinkedList<>();
        Message message = null;
        byteBuffer.clear();
//        Arrays
        int read = 0;
        while ((read = socketChannel.read(byteBuffer)) > 0) {
            byteBuffer.flip();
            byte[] bytes = new byte[byteBuffer.limit()];
            byteBuffer.get(bytes);
//            try {
//                message = (Message) ByteUtil.toObject(bytes);
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }
//            msglist.add(message);
            stringBuilder.append(new String(bytes));
            byteBuffer.clear();
        }
        String msg;
        if (read < 0) {
            msg = key.attachment() + " left the chat.\n";
            socketChannel.close();
        } else {
            msg = key.attachment() + ": " + stringBuilder.toString();
        }
        if (msg.equals("close")) {
            this.running = false;
        }
        System.out.println(msg);
        broadcast(msg);
    }

    private void broadcast(String msg) throws IOException {
        ByteBuffer msgBuf = ByteBuffer.wrap(msg.getBytes());
        for (SelectionKey key : selector.keys()) {
            if (key.isValid() && key.channel() instanceof SocketChannel) {
                SocketChannel sch = (SocketChannel) key.channel();
                sch.write(msgBuf);
                msgBuf.rewind();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(10523);
        (new Thread(server)).start();
    }
}
