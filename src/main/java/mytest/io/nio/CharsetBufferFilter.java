package mytest.io.nio;

import java.nio.ByteBuffer;

public interface CharsetBufferFilter {
    int filter(ByteBuffer byteBuffer);
}
