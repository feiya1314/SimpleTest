package mytest.io.nio;

import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class NIOTest {
    public static void main(String[] args) throws Exception {
        FileChannelTest(new Utf8BufferFilter());
    }

    /**
     * FileChannel 测试，读 UTF-8 文本，在 ByteBuffer.allocate 时分配空间，如果中英混合的文件中就会出现中文字符只读取了一部分的问题，
     * 如果文本为等长编码字符集的时候，可以根据编码集 byte 长度进行 allocate ，例如 GBK 为2 byte ，
     * 所以我们 allocate 时未2的倍数即可，但像 UTF-8 这类变长的编码字符集时则没那么简单了
     * UTF-8 的编码方式
     * <p>
     * 0xxxxxxx
     * 110xxxxx 10xxxxxx
     * 1110xxxx 10xxxxxx 10xxxxxx
     * 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
     * <p>
     * 对于 UTF-8 编码中的任意字节 B ，如果 B 的第一位为0，则 B 为 ASCII 码，并且 B 独立的表示一个字符；
     * 如果 B 的第一位为1，第二位为0，则B为一个非 ASCII 字符（该字符由多个字节表示）中的一个字节，并且不是字符的第一个字节编码；
     * 如果 B 的前两位为1，第三位为0，则B为一个非 ASCII 字符（该字符由多个字节表示）中的第一个字节，并且该字符由两个字节表示；
     * 如果 B 的前三位为1，第四位为0，则B为一个非 ASCII 字符（该字符由多个字节表示）中的第一个字节，并且该字符由三个字节表示；
     * 如果 B 的前四位为1，第五位为0，则B为一个非 ASCII 字符（该字符由多个字节表示）中的第一个字节，并且该字符由四个字节表示；
     */
    public static void FileChannelTest(CharsetBufferFilter filter) throws Exception {
        int capacity = 256; // 至少为4，因为UTF-8最大为4字节
        //ServerSocketChannel socketChannel =  new ServerS

        Charset charset = StandardCharsets.UTF_8;

        File ioTestFile = new File(NIOTest.class.getClassLoader().getResource("io_test_file.txt").getFile());
        FileInputStream fileInputStream = new FileInputStream(ioTestFile);
        FileChannel fileChannel = fileInputStream.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(capacity); // ByteBuffer.wrap(new byte[capacity]);
        //byteBuffer.slice();byteBuffer.reset()
        //CharBuffer charBuffer = CharBuffer.allocate(256);

        while (fileChannel.read(byteBuffer) != -1) {
            int limitSize = filter.filter(byteBuffer);
            //byteBuffer切换到读模式， position设置成0
            byteBuffer.flip();
            int limit = byteBuffer.limit();
            byteBuffer.limit(limitSize);  // 阻止读取跨界数据，不读取不完整的utf8字符，只读取limit内的buffer
            System.out.println(charset.decode(byteBuffer));
            byteBuffer.limit(limit);  // 恢复limit
            byteBuffer.compact(); // 将未读的buffer数据（不完整的utf8字符）拷贝到Buffer起始处，将position设到最后一个未读元素正后面，下次往buffer写数据的时候不再从头开始写
            //byteBuffer.clear();
        }
    }

    public static void SocketChannelTest() throws Exception{
        SocketChannel socketChannel;
        //socketChannel.configureBlocking()
    }
}
