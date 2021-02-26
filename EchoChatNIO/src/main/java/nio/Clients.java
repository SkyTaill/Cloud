package nio;

import java.nio.channels.SocketChannel;

public class Clients {
    private SocketChannel socketChannel;
    private int userNumber;

    public Clients(SocketChannel socketChannel,int userNumber) {
        this.socketChannel=socketChannel;
        this.userNumber=userNumber;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public void setUserNumber(int userNumber) {
        this.userNumber = userNumber;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public int getUserNumber() {
        return userNumber;
    }
}
