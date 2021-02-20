package io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class Handler implements Runnable {

    private static final int BUFFER_SIZE = 1024;
    private final IoFileCommandServer server;
    private final Socket socket;
    private String serverDir = "./";
    private String userName;
    private final byte[] buffer;

    public Handler(IoFileCommandServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
        userName = "user";
        buffer = new byte[BUFFER_SIZE];
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
                } else if (message.startsWith("getFile")) {
                    String[] data = message.split(" +");
                    String fileName = data[1];
                    File file = new File(serverDir + fileName);
                    if (!file.exists()) {
                        os.writeUTF("user: File not exists\n");
                    } else {
                        os.writeUTF("file");
                        os.writeUTF(fileName);
                        long length = file.length();
                        os.writeLong(length);
                        try (FileInputStream fis = new FileInputStream(serverDir + fileName)) {
                            int read;
                            while ((read = fis.read(buffer)) != -1) {
                                os.write(buffer, 0 ,read);
                            }
                        }
                    }
                    os.flush();
                }
                else {
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
