package mytest.io.nio;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class NIOTest {
    public static void main(String[] args) throws Exception{
        FileChannelTest();
    }


    /**
     * FileChannel 测试，读 UTF-8 文本，在 ByteBuffer.allocate 时分配空间，如果中英混合的文件中就会出现中文字符只读取了一部分的问题，
     * 如果文本为等长编码字符集的时候，可以根据编码集 byte 长度进行 allocate ，例如 GBK 为2 byte ，
     * 所以我们 allocate 时未2的倍数即可，但像 UTF-8 这类变长的编码字符集时则没那么简单了
     * UTF-8 的编码方式
     *
     * 0xxxxxxx
     * 110xxxxx 10xxxxxx
     * 1110xxxx 10xxxxxx 10xxxxxx
     * 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
     *
     * 对于 UTF-8 编码中的任意字节 B ，如果 B 的第一位为0，则 B 为 ASCII 码，并且 B 独立的表示一个字符；
     * 如果 B 的第一位为1，第二位为0，则B为一个非 ASCII 字符（该字符由多个字节表示）中的一个字节，并且不是字符的第一个字节编码；
     * 如果 B 的前两位为1，第三位为0，则B为一个非 ASCII 字符（该字符由多个字节表示）中的第一个字节，并且该字符由两个字节表示；
     * 如果 B 的前三位为1，第四位为0，则B为一个非 ASCII 字符（该字符由多个字节表示）中的第一个字节，并且该字符由三个字节表示；
     * 如果 B 的前四位为1，第五位为0，则B为一个非 ASCII 字符（该字符由多个字节表示）中的第一个字节，并且该字符由四个字节表示；
     * */
    public static void FileChannelTest() throws Exception{
        int capacity = 256 ;
        //ServerSocketChannel socketChannel =  new ServerS

        Charset charset = StandardCharsets.UTF_8;

        File ioTestFile = new File(NIOTest.class.getClassLoader().getResource("io_test_file.txt").getFile());
        FileInputStream fileInputStream = new FileInputStream(ioTestFile);
        FileChannel fileChannel = fileInputStream.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(capacity);
        //CharBuffer charBuffer = CharBuffer.allocate(256);

        while (fileChannel.read(byteBuffer) != -1){
            byte b;
            int idx;
            out :
            for (idx = byteBuffer.position()-1; idx >= 0; idx--) {
                b = byteBuffer.get(idx);

                if ((b & 0xff) >> 7 == 0) {  // 0xxxxxxx
                    break;
                }
                if ((b& 0xff & 0xc0) == 0xc0) {   // 11xxxxxx，110xxxxx、1110xxxx、11110xxx
                    idx -= 1;
                    break;
                }
                if ((b & 0xff & 0x80) == 0x80) {
                    for (int i = 1; i < 4; i++) {
                        b = byteBuffer.get(idx - i);
                        if ((b & 0xff & 0xc0) == 0xc0) {
                            if ((b & 0xff) >> (5 + 1 - i) == 0xf >> (3 - i)) {
                                break out;
                            } else {
                                idx = idx - 1 - i;
                                break out;
                            }
                        }
                    }
                }
            }
            //byteBuffer.
            byteBuffer.flip();
            int limit = byteBuffer.limit();
            byteBuffer.limit(idx+1);  // 阻止读取跨界数据
            System.out.println(charset.decode(byteBuffer));
            byteBuffer.limit(limit);  // 恢复limit
            byteBuffer.compact();
            //byteBuffer.clear();
        }
    }
}
