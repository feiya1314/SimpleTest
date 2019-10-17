package mytest.io;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public void sendHello() throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        byte[] hello = "hello\n".getBytes();
        outputStream.write(hello);
        outputStream.flush();
    }

    public void receive() throws IOException {
        InputStream inputStream = socket.getInputStream();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        byte[] input = new byte[256];
        while (bufferedInputStream.read(input) != -1) {
            String receive = new String(input);
            System.out.println(receive);
        }

    }

    public static void main(String[] args) throws Exception {

        IMClient client = new IMClient().connect();
        new Thread(() -> {
            try {
                client.receive();
            } catch (Exception e) {

            }
        }).start();

        Thread.sleep(1000);
        for (int i = 0; i < 10; i++) {
            client.sendHello();
        }
    }


}
