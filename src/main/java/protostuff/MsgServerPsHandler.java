package protostuff;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

public class MsgServerPsHandler extends ChannelInboundHandlerAdapter{

    private Schema<StockDef> schema = RuntimeSchema.getSchema(StockDef.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf)msg;
        if (byteBuf.readableBytes() < 4) {
            return;
        }
        byteBuf.markReaderIndex();
        int dataLength = byteBuf.readInt();
        if (dataLength < 0) {
            ctx.close();
        }
        if (byteBuf.readableBytes() < dataLength) {
            byteBuf.resetReaderIndex();
        }

        System.out.println("datalength is : " + dataLength);
        System.out.println("byteBuf's readableBytes is : " + byteBuf.readableBytes());
//        byte[] data = new byte[dataLength];
        byte[] data = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(data);

        StockDef stockDef = new StockDef();
        ProtobufIOUtil.mergeFrom(data, stockDef, schema);

        System.out.println(stockDef.stockId + "," + stockDef.price);

        ctx.writeAndFlush(msg);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
