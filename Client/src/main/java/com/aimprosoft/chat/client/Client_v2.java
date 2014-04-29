package com.aimprosoft.chat.client;

import com.aimprosoft.library.ByteUtil;
import com.aimprosoft.library.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class Client_v2 implements Runnable{
    private final SocketChannel client ;
    private Selector sl = Selector.open();
    SelectionKey clientKey;
    private boolean doShutdown = false;

    public Client_v2(String host) throws IOException {
        client = SocketChannel.open();
        // nonblocking I/O
        client.configureBlocking(false);
        client.connect(new InetSocketAddress(host,10523));
        client.register(sl, SelectionKey.OP_CONNECT);
    }

    @Override
    public void run() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(2048);
        try {
            while (true) {
                if (doShutdown) {

                    Set<SelectionKey> set = sl.keys();
                    for (SelectionKey key : set) {
                        SocketChannel sc = (SocketChannel) key.channel();
                        try {
                            sc.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        key.cancel();
                    }
                    try {
                        sl.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (sl.isOpen()) {
                    int keys = 0;
                    keys = sl.select();
                    if (keys > 0) {
                        Set<SelectionKey> set = sl.selectedKeys();
                        for (SelectionKey sk : set) {
                            if (sk.isValid() && sk.isConnectable()) {
                                SocketChannel sc = (SocketChannel) sk.channel();
                                sc.finishConnect();
                                sk.interestOps(SelectionKey.OP_WRITE);
                            }
                            if (sk.isValid() && sk.isReadable()) {
                                SocketChannel sc = (SocketChannel) sk.channel();
                                int read = 0;
                                try {
                                    read = sc.read(byteBuffer);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    cancelKey(sk, sc);
                                    continue;
                                }
                                if (read == -1) {
                                    cancelKey(sk, sc);
                                    continue;
                                }
                                byteBuffer.clear();
                            }
                            if (sk.isValid() && sk.isWritable()) {
                                // send an login:pass string
                                ByteBuffer sendBuffer; // = ByteBuffer.allocate(256);
                                Message msg = new Message("Makaka", "Orangutang", new Date());
//                                Object ob = sk.attachment();
                                sendBuffer = ByteBuffer.wrap(ByteUtil.toByteArray(msg));
//                                if (ob instanceof String) {
//                                    String loginPass = (String) ob;
//                                    sendBuffer = ByteBuffer.allocate(loginPass.length() + 4);
//                                    sendBuffer.flip();
//                                } else if (ob instanceof ByteBuffer) {
//                                    sendBuffer = (ByteBuffer) ob;
//                                }
                                SocketChannel sc = (SocketChannel) sk.channel();
                                int written = 0;
                                try {
                                    written = sc.write(sendBuffer);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                    cancelKey(sk, sc);
                                    continue;
                                }
                                if (written == -1) {
                                    cancelKey(sk, sc);
                                }
                                sk.attach(sendBuffer);
                                assert sendBuffer != null;
                                if (sendBuffer.remaining() == 0) {
                                    sk.interestOps(SelectionKey.OP_READ);

                                }

                            }
                        }
                        set.clear();
                    }
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                sl.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }

    private void cancelKey(SelectionKey sk, SocketChannel sc) throws IOException {
        sc.close();
        sk.cancel();
    }


    public static void main(String[] args) throws IOException, InterruptedException {

        String adr;
        if (args.length > 0) {
            adr = args[0];
        } else {
            adr = "localhost";
        }
        Client_v2 hlgc = new Client_v2(adr);
        new Thread(hlgc).start();
    }
}
