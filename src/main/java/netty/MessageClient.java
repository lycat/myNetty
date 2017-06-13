package netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import proto.Stock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MessageClient {

    private final String host;
    private final int port;

    public MessageClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new MessageClientInitializer());
            Channel channel = b.connect(host,port).sync().channel();

            MessageClientHandler handler = channel.pipeline().get(MessageClientHandler.class);

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            Stock.StockDef.Builder stock;
            String result = "";
            while (true) {
                String line = in.readLine();
                if ("bye".equals(line.toLowerCase())) {
                    channel.closeFuture().sync();
                    break;
                }

                stock = Stock.StockDef.newBuilder();
                stock.setStockId(line.split(",")[0]);
                stock.setPrice(Integer.valueOf(line.split(",")[1]));
                result = handler.getResult(stock.build());
                System.out.println(result);
            }

            channel.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new MessageClient("localhost",8080).run();
    }
}
