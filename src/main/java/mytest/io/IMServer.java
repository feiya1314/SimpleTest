package mytest.io;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class IMServer {
    public static void main(String[] args) throws Exception {
        new IMServer().start();
    }

    public void start() throws Exception {
        ServerSocket serverSocket = new ServerSocket();
        InetSocketAddress socketAddress = new InetSocketAddress(13145);
        serverSocket.bind(socketAddress);
        while (true) {
            Socket socket = serverSocket.accept();
            new Thread(new ProccessClientMsg(socket)).start();
        }
    }

    private class ProccessClientMsg implements Runnable {
        Socket socket;

        public ProccessClientMsg(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {

            try {
                InputStream readClient = socket.getInputStream();
                OutputStream outClient = socket.getOutputStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(readClient);
                BufferedOutputStream bufferedoutClientStream = new BufferedOutputStream(outClient);
                byte[] msg = new byte[256];
                while (bufferedInputStream.read(msg) != -1) {
                    String s = new String(msg);
                    System.out.println(s);
                    bufferedoutClientStream.write("msg from server\n".getBytes());
                    bufferedoutClientStream.flush();
                }
            } catch (Exception e) {

            }
        }
    }
}
