package com.hua.netty.bio;

import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TimeServer {
    public static void main(String args[]) {
        int port = 50001;

        if (args != null && args.length > 0) {
            port = NumberUtils.toInt(args[0]);
        }

        ServerSocket server = null;

        try {
            server = new ServerSocket(port);
            System.out.println("The time server is started in port: " + port);
            Socket socket;
            while (true) {
                socket = server.accept();
                System.out.println("Server accept one client socket");
                new Thread(new TimeServerHandler(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
