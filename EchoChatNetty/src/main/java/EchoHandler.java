import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.ConcurrentLinkedDeque;

public class EchoHandler extends SimpleChannelInboundHandler<String> {

    private EhoServer ehoServer;
    private String userName;
    private static int cnt=0;
    public EchoHandler(EhoServer ehoServer) {
        this.ehoServer = ehoServer;
        cnt++;
        userName="user#"+cnt;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        //бизнес логика
        //ctx.writeAndFlush() в контексте есть много методов этот позволяет любой тип данных выдавить в трубу
        //ctx.fireChenl позволяет передать в другой хендлер в другом типе данных
        ConcurrentLinkedDeque<ChannelHandlerContext> clients= ehoServer.getClients();
        s=s.trim(); //убираем лишние пробелы
        for(ChannelHandlerContext client:clients){
        client.writeAndFlush(userName+"-"+s);      //по всем клиентам рассылаем

        }
        System.out.println("email-"+s);

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception { //данный метод вызывается когда клиент подключается
       System.out.println("client join");
       ehoServer.getClients().add(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client disconect");
        ehoServer.getClients().remove(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();  //печатаем ошибочки
    }
}
