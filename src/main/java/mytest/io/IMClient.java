package mytest.io;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class IMClient {
    Socket socket;
    public IMClient connect() throws IOException {
        socket = new Socket();
        InetSocketAddress socketAddress = new InetSocketAddress(13145);
        socket.connect(socketAddress);
        return this;
    }

    public void sendHello() throws IOException{
        OutputStream outputStream = socket.getOutputStream();
        byte[] hello = "hello".getBytes();
        outputStream.write(hello);
        outputStream.flush();
    }

    public void receive() throws IOException {

    }
    public static void main(String[] args) throws Exception{
        new IMClient().connect().sendHello();

    }
}
