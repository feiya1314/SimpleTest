package mytest.io.nio;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class NIOTest {
    public static void main(String[] args) throws Exception{
        FileChannelTest();
    }


    public static void FileChannelTest() throws Exception{
        int capacity = 2048 ;
        //ServerSocketChannel socketChannel =  new ServerS

        Charset charset = StandardCharsets.UTF_8;

        File ioTestFile = new File(NIOTest.class.getClassLoader().getResource("io_test_file.txt").getFile());
        FileInputStream fileInputStream = new FileInputStream(ioTestFile);
        FileChannel fileChannel = fileInputStream.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(capacity);
        //CharBuffer charBuffer = CharBuffer.allocate(256);

        while (fileChannel.read(byteBuffer) != -1){
            byteBuffer.flip();

            System.out.println(charset.decode(byteBuffer));
            byteBuffer.clear();
        }
    }
}
