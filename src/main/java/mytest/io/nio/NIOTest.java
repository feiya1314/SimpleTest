package mytest.io.nio;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class NIOTest {
    public static void main(String[] args) throws Exception {
        FileChannelTest();
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
    public static void FileChannelTest() throws Exception {
        int capacity = 256; // 至少为4，因为UTF-8最大为4字节
        //ServerSocketChannel socketChannel =  new ServerS

        Charset charset = StandardCharsets.UTF_8;

        File ioTestFile = new File(NIOTest.class.getClassLoader().getResource("io_test_file.txt").getFile());
        FileInputStream fileInputStream = new FileInputStream(ioTestFile);
        FileChannel fileChannel = fileInputStream.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(capacity);
        //CharBuffer charBuffer = CharBuffer.allocate(256);

        while (fileChannel.read(byteBuffer) != -1) {
            byte b;
            int idx; // 最后一个完整的 utf8 的下标位置，后面的字节是不完整的
            out:
            // 从后往前，找到最后一个 utf-8 所在的index，因为有可能最后的字符不完整，缺少字节
            for (idx = byteBuffer.position() - 1; idx >= 0; idx--) {
                b = byteBuffer.get(idx);
                // 判断是不是一字节的 utf8字符，0xxxxxxx ,如果是说明可以正确解码，跳过继续从新开始
                // 对于 UTF-8 编码中的任意字节 B ，如果 B 的第一位为0，则 B 为 ASCII 码，并且 B 独立的表示一个字符；
                if (b >> 7 == 0) {  // 0xxxxxxx
                    break;
                }

                // 如果 B 的第一位为1，第二位为0，则B为一个非 ASCII 字符（该字符由多个字节表示）中的一个字节，并且不是字符的第一个字节编码；
                // 如果 B 的前两位为1，第三位为0，则B为一个非 ASCII 字符（该字符由多个字节表示）中的第一个字节，并且该字符由两个字节表示；
                // 110xxxxx 或 1110xxxx 或 11110xxx开头，说明后面丢失，不够一个 utf8 编码
                // b & 11111111 & 1100 0000‬
                if ((b & 0xc0) == 0xc0) {   // 11xxxxxx，110xxxxx、1110xxxx、11110xxx
                    idx -= 1;
                    break;
                }

                // 1000 0000 。如果是第一位为1，第二位为0，则表示这个字节是一个完整utf8的后一部分
                if ((b & 0x80) == 0x80) {
                    for (int i = 1; i < 4; i++) {
                        b = byteBuffer.get(idx - i);
                        // 如果该字节是 11xxxxxx 则说明找到了utf8开头的字节，这个字节之前的都是完整的 utf8 字节
                        if ((b & 0xc0) == 0xc0) {
                            idx -= (i+1); // i+1之前才是最后一个完整 utf8 的最后一个字节
                            break out;
                        }
                    }
                }
            }
            //byteBuffer切换到读模式， position设置成0
            byteBuffer.flip();
            int limit = byteBuffer.limit();
            byteBuffer.limit(idx + 1);  // 阻止读取跨界数据，不读取不完整的utf8字符，只读取limit内的buffer
            System.out.println(charset.decode(byteBuffer));
            byteBuffer.limit(limit);  // 恢复limit
            byteBuffer.compact(); // 将未读的buffer数据（不完整的utf8字符）拷贝到Buffer起始处，将position设到最后一个未读元素正后面，下次往buffer写数据的时候不再从头开始写
            //byteBuffer.clear();
        }
    }
}
