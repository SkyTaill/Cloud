package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import static java.nio.channels.SelectionKey.OP_ACCEPT;
import static java.nio.channels.SelectionKey.OP_READ;

public class NioCalculatorServer {

    private ServerSocketChannel serverChannel;
    private Selector selector;
    private ByteBuffer buffer;

    public NioCalculatorServer() throws IOException {
        buffer = ByteBuffer.allocate(256);
        serverChannel = ServerSocketChannel.open(); //открыли серверный канал
        selector = Selector.open();
        serverChannel.bind(new InetSocketAddress(8189));  //забиндили порт
        serverChannel.configureBlocking(false);  //работает в неблокирующем режиме
        serverChannel.register(selector, OP_ACCEPT);  //сложн крч показывает что он делает только авторизацию подключения 1Ж51
        while (serverChannel.isOpen()) {    //пока канал открыт будем работать
            selector.select(); // block но получает группу событий (все события со всех каналов которые зарегестрированны те сбор всех каналов
            Set<SelectionKey> keys = selector.selectedKeys();  //ну и сюда перекидываются ключи тип храняться в этой коллекции
            Iterator<SelectionKey> keyIterator = keys.iterator();
            while (keyIterator.hasNext()) {  //по всей коллекции проходимся и делаем обработку ключей
                SelectionKey key = keyIterator.next();
                if (key.isAcceptable()) {  //если ключ запрос на соединение
                    handleAccept(key);      //обработка соединения
                }
                if (key.isReadable()) {  //если нам пришло чтение то сто проц эта функция
                    handleRead(key);
                }
                keyIterator.remove();
            }
        }
    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();  //точно мы его получили с сокетного канала
        int read = channel.read(buffer);  //прочитали в буфер
        if (read == -1) {
            return;
        }
        buffer.flip();
        StringBuilder msg = new StringBuilder();
        while (buffer.hasRemaining()) {
            msg.append((char) buffer.get());
        }
        buffer.clear();
        System.out.println("received: " + msg);
        String[] args = msg.toString().trim().split(" ");
        System.out.println(Arrays.toString(args));
        int sum = Integer.parseInt(args[0]) + Integer.parseInt(args[1]);
        System.out.println(sum);
        channel.write(ByteBuffer.wrap((sum + "\n").getBytes(StandardCharsets.UTF_8)));
    }

    private void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = channel.accept();   //создаем конал по аналогии с ио шным  (мож добавить в очередь для чата и рассылки) когда произошло соединение выделелся этот канал
        socketChannel.write(ByteBuffer.wrap(
                "Hello to calc server!\nInput two args for calculate sum:\n".getBytes(StandardCharsets.UTF_8)));
        socketChannel.configureBlocking(false);  //работаем не в блокируюшем режиме
        socketChannel.register(selector, OP_READ); //зарегестрировали на селекторе вот этот вот канал
    }

    public static void main(String[] args) throws IOException {
        new NioCalculatorServer();
    }
}
