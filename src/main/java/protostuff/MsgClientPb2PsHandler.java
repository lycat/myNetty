package protostuff;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import proto.Stock;

public class MsgClientPb2PsHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Stock.StockDef stock
                = Stock.StockDef.newBuilder()
                .setStockId("stockId").setPrice(123).build();
        byte[] data = stock.toByteArray();
        ByteBuf msg = Unpooled.buffer();

        msg.writeInt(data.length);
        msg.writeBytes(data);

        ctx.writeAndFlush(msg);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
