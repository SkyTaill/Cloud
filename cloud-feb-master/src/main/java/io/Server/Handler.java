package io.Server;

import io.Server.IoFileCommandServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class Handler implements Runnable {

    private final IoFileCommandServer server;
    private final Socket socket;
    private String serverDir = "./";
    private String userName;

    public Handler(IoFileCommandServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
        userName = "user";
    }

    @Override
    public void run() {
        try (DataOutputStream os = new DataOutputStream(socket.getOutputStream());
             DataInputStream is = new DataInputStream(socket.getInputStream())) {
            while (true) {
                String message = is.readUTF();
                System.out.println("received message: " + message);
                if (message.equals("ls")) {
                    File dir = new File(serverDir);
                    StringBuilder sb = new StringBuilder(userName).append(" files -> \n");
                    File[] files = dir.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            if (file == null) {
                                continue;
                            }
                            sb.append(file.getName()).append(" | ");
                            if (file.isFile()) {
                                sb.append("[FILE] | ").append(file.length()).append(" bytes.\n");
                            } else {
                                sb.append("[DIR]\n");
                            }
                        }
                    }
                    os.writeUTF(sb.toString());
                    os.flush();
                } else if (message.startsWith("cd ")) {
                    String path = message.split(" ", 2)[1];
                    File dir = new File(path);
                    File dirAcc = new File(serverDir + "/" + path);
                    if (dir.exists()) {
                        serverDir = path;
                    } else if (dirAcc.exists()) {
                        serverDir = serverDir + "/" + path;
                    } else if (path.equals("..")) {
                        serverDir = new File(serverDir).getParent();
                    } else {
                        os.writeUTF("user: wrong path\n");
                        os.flush();
                    }
                } else if (message.equals("/quit")) {
                    os.writeUTF(message);
                    os.flush();
                    break;
                } else {
                    os.writeUTF("user: UNKNOWN COMMAND\n");
                    os.flush();
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
