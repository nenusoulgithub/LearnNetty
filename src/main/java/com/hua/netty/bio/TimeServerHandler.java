package com.hua.netty.bio;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeServerHandler implements Runnable {
    private Socket socket;

    public TimeServerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String response;

            while (true) {
                String command = in.readLine();

                System.out.println("[Thread-" + Thread.currentThread().getId() + "]" + "Server receive one command: " + command);

                if (StringUtils.isEmpty(command)) {
                    break;
                } else if ("QUERY TIME".equalsIgnoreCase(command)) {
                    response = new SimpleDateFormat("yyy-MM-dd HH:mm:ss").format(new Date());
                } else {
                    response = "BAD COMMAND";
                }

                Thread.sleep((int)(Math.random() * 10) * 1000);

                System.out.println("[Thread-" + Thread.currentThread().getId() + "]" + "Server response one message: " + response);

                out.println(response);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                out.close();
            }
        }
    }
}
