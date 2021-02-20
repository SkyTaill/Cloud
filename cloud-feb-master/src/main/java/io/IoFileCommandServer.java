package io;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class IoFileCommandServer {

    private String serverDir = "./";

    public IoFileCommandServer() {
        try (ServerSocket server = new ServerSocket(8189)){
            System.out.println("Server started on port 8189");
            while (true) {
                try {
                    Socket socket = server.accept(); // block
                    System.out.println("user accepted");
                    Handler handler = new Handler(this, socket);
                    new Thread(handler).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new IoFileCommandServer();
    }
}
