package com.hua.netty.nio;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class MultiplexerTimerServer {
    public static void main(String[] args) {
        int port = 50002;

        ServerSocketChannel ssc = null;
        Selector selector = null;

        try {
            ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            ssc.socket().bind(new InetSocketAddress(InetAddress.getByName("localhost"), port));

            selector = Selector.open();

            ssc.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("The time server is start on port: " + port);

            while (true) {
                selector.select(1000);

                Set<SelectionKey> selectionKeys = selector.selectedKeys();

                for (Iterator<SelectionKey> iterator = selectionKeys.iterator(); iterator.hasNext(); ) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();

                    if (selectionKey.isValid()) {
                        if (selectionKey.isAcceptable()) {
                            ServerSocketChannel serverSocketChannel = (ServerSocketChannel)selectionKey.channel();

                            SocketChannel socketChannel = serverSocketChannel.accept();
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector, SelectionKey.OP_READ);
                        }

                        if (selectionKey.isReadable()) {
                            SocketChannel socketChannel = (SocketChannel)selectionKey.channel();

                            ByteBuffer readBuffer = ByteBuffer.allocate(1024);

                            int readBytes = socketChannel.read(readBuffer);

                            if (readBytes > 0) {
                                readBuffer.flip();
                                byte[] bytes = new byte[readBuffer.remaining()];
                                readBuffer.get(bytes);
                                String command = new String(bytes, "UTF-8");
                                System.out.println("Time server received command: " + command);

                                String response;

                                command = command.replaceAll("\r", "");
                                command = command.replaceAll("\n", "");

                                if (StringUtils.isEmpty(command)) {
                                    break;
                                } else if ("QUERY TIME".equalsIgnoreCase(command)) {
                                    response = new SimpleDateFormat("yyy-MM-dd HH:mm:ss").format(new Date());
                                } else {
                                    response = "BAD COMMAND";
                                }

                                response = response + "\n";

                                Thread.sleep((int)(Math.random() * 10) * 1000);

                                byte[] responseBytes = response.getBytes();

                                ByteBuffer writeBuffer = ByteBuffer.allocate(responseBytes.length);
                                writeBuffer.put(responseBytes);
                                writeBuffer.flip();
                                socketChannel.write(writeBuffer);
                            } else if (readBytes < 0) {
                                selectionKey.cancel();
                                socketChannel.close();
                            } else {
                                System.out.println("Time server received command: ");
                            }
                        }
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (ssc != null) {
                try {
                    ssc.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
