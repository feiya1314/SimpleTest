package mytest.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class EchoClient {

    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);

        SocketChannel client;

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        Selector selector = Selector.open();

        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        socketChannel.connect(new InetSocketAddress(13145));

        while (true){
            selector.select();
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                // 已经建立连接到服务器
                client = (SocketChannel)key.channel();
                if (key.isConnectable()){
                    if (socketChannel.isConnectionPending()){
                        socketChannel.finishConnect();
                    }
                    byteBuffer=ByteBuffer.wrap("i am client ,hello word".getBytes());
                    client.write(byteBuffer);
                    client.register(selector,SelectionKey.OP_READ);

                }else if (key.isReadable()){
                    //client = (SocketChannel)key.channel();
                    //byteBuffer=ByteBuffer.allocate(1024);
                    //readBuffer.flip();
                    readBuffer.clear();
                    client.read(readBuffer);
                    //while(client.read(readBuffer)!=-1) {
                        System.out.println("client receive from server : " + new String(readBuffer.array()));

                    //}
                    readBuffer.clear();

                }
            }
        }

    }

}
