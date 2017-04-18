package netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import proto.Stock;
import proto.Stock.StockDef;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageClientHandler extends SimpleChannelInboundHandler<Stock.ReturnMsg> {

    private volatile Channel channel;
    private final BlockingQueue<Stock.ReturnMsg> answer = new LinkedBlockingQueue<Stock.ReturnMsg>();

    public String getResult(StockDef stockDef) throws InterruptedException {

        Stock.ReturnMsg result;
        channel.writeAndFlush(stockDef);
        while (true) {
            if(!answer.isEmpty()) {
                result = answer.take();
                break;
            }
        }

        return result.getMsgContent();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        channel = ctx.channel();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Stock.ReturnMsg msg) throws Exception {
//        System.out.println(" " + msg.getStockId() + " " + msg.getPrice());
        System.out.println(msg);
        answer.add(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
