package mytest.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class EchoServer {
    private  Selector selector;
    private  ServerSocketChannel serverSocketChannel;

    public EchoServer() {
    }

    private void init() {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(13145));
            serverSocketChannel.configureBlocking(false);

            // 将其注册到 Selector 中，监听 OP_ACCEPT 事件
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                if(selector.isOpen()) {
                    int readyChannels = selector.select();
                    if (readyChannels == 0) continue;

                    Set<SelectionKey> readyKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = readyKeys.iterator();

                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();

                        if (key.isAcceptable()) {
                            SocketChannel socketChannel = serverSocketChannel.accept();
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector, SelectionKey.OP_READ);
                            continue;
                        }

                        if (key.isValid() && key.isReadable()) {
                            // todo read msg
                            readFromClient(key);

                        }

                        if (key.isValid() && key.isWritable()) {
                            // todo write msg
                            //writeToClient(key);
                        }
                    }
                }else {
                    Thread.sleep(500);
                    System.out.println("slector is not open ");
                }
            }
        }catch (Exception e){
            //selector.close();
            e.printStackTrace();
        }finally {

        }
    }

    private void readFromClient(SelectionKey key) throws IOException{
        SocketChannel socketChannel = (SocketChannel)key.channel();
        ByteBuffer msg = ByteBuffer.allocate(1024);
        socketChannel.read(msg);// 这个其实是ByteBuffer写模式，因为需要往ByteBuffer中写数据
        //char[] chars  = msg.asCharBuffer().array();
        msg.flip();// 切换到读模式
        String readStr= msg.toString();
        byte[] byteRec = new byte[msg.limit()];
        msg.get(byteRec);
        //byte[] byteRec = msg.array();
        String rece = new String(byteRec);
        System.out.println("receive from client : " + rece);
        msg.flip();
        msg.clear();
        key.interestOps(SelectionKey.OP_WRITE);
        byte[] bytes = ("server response: " + rece).getBytes();
        socketChannel.write(ByteBuffer.wrap(bytes));
        //socketChannel.write(msg);
        //socketChannel.
        msg.clear();
    }

    private void writeToClient(SelectionKey key){

    }

    public static void main(String[] args) throws Exception{
        new EchoServer().init();
    }
}
