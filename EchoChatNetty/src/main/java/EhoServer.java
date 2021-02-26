import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EhoServer {
private ConcurrentLinkedDeque<ChannelHandlerContext>clients;



            //написание клиента на  1ю20 мин (аккуратно там надо в байты переводить)



    public EhoServer(){
        clients=new ConcurrentLinkedDeque<>();
        //вечные цыклы под копотом execut servic
        EventLoopGroup auth =new NioEventLoopGroup(1); //вечный цикл обработки входящих соединений (обработкой занимается отдельная луп группа -легковесная)
        // занимается авторизацией
        //по одному обрабатывает клиентов
        //----------------------------
        EventLoopGroup worker=new NioEventLoopGroup();//тяжелая группа циклов
        //auth и worker работает на экзекьют сервисах
        try { //если экзекьют сервис работает то программа висит по этому экзекют сервисы надо вырубать

            ServerBootstrap bootstrap=new ServerBootstrap();
            bootstrap.group(auth,worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline().addLast(
                                    new StringDecoder(),   //если прилитает то он в декодер
                                    new StringEncoder(),   //а если отправляем то в энкодер (байти в строки заварачивают)
                                    new EchoHandler(EhoServer.this)

                            ); //та самая труба котроая была в лекции и тут вешаем хендлеры

                        }
                    });
           ChannelFuture future= bootstrap.bind(8189).sync();
           System.out.println("server started");
           future.channel().closeFuture().sync(); //блокирующая операция
        } catch (InterruptedException e) {
            //e.printStackTrace();
            System.out.println("err");
        } finally {
            auth.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
    public ConcurrentLinkedDeque<ChannelHandlerContext> getClients(){
        return clients;
    }
    public static void main(String[] args){
        new EhoServer();
    }
}
