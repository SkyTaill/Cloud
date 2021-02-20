package nio;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ChannelExamples {

    public static void main(String[] args) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(5);
        buffer.put((byte) 'a');
        buffer.put((byte) 'b');
        buffer.put((byte) 'c');
        buffer.flip();  //буфер перевели в режим чтения
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
        buffer.rewind();
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
        buffer.clear();
        RandomAccessFile file = new RandomAccessFile("client/2.txt", "rw");
        FileChannel channel = file.getChannel();    //получаем файловый канал
        int read = 0;
        while ((read = channel.read(buffer)) > -1) {
            buffer.flip();
            while (buffer.hasRemaining()) {
                System.out.print((char) buffer.get());
            }
            buffer.clear();
        }
    }
}
