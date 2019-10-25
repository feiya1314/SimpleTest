package mytest.io.nio;

import java.nio.ByteBuffer;

public class Utf8BufferFilter implements CharsetBufferFilter {
    @Override
    public int filter(ByteBuffer byteBuffer) {
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
                        idx -= (i + 1); // i+1之前才是最后一个完整 utf8 的最后一个字节
                        break out;
                    }
                }
            }
        }
        return idx + 1;
    }
}
