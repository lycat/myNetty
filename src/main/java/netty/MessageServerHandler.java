package netty;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import proto.Stock;
import proto.Stock.StockDef;

public class MessageServerHandler extends SimpleChannelInboundHandler<StockDef>{


    @Override
    public void channelRead0(ChannelHandlerContext ctx, StockDef msg) throws Exception {
        System.out.println("stockId:" + msg.getStockId() + ",price:" + msg.getPrice());
        Stock.ReturnMsg.Builder returnMsg = Stock.ReturnMsg.newBuilder();
        returnMsg.setMsgId("ok");
        returnMsg.setMsgContent( msg.getStockId() + "is bought at price: " + msg.getPrice());
        ctx.channel().writeAndFlush(returnMsg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
