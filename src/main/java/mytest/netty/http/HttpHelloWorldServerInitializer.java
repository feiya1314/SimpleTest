package mytest.netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

public class HttpHelloWorldServerInitializer extends ChannelInitializer<SocketChannel> {

    private SslContext sslCtx = null;
    private SSLContext sslCtxs = null;

    public HttpHelloWorldServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    public HttpHelloWorldServerInitializer(SSLContext sslCtxs) {
        this.sslCtxs = sslCtxs;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        if (sslCtx != null) {
            p.addLast(sslCtx.newHandler(ch.alloc()));
        }
        if (sslCtxs != null) {
            SSLEngine sslEngine = sslCtxs.createSSLEngine();
            sslEngine.setUseClientMode(false);
            p.addLast(new SslHandler(sslEngine));
        }
        p.addLast(new HttpServerCodec());
        p.addLast(new HttpServerExpectContinueHandler());
        p.addLast(new HttpHelloWorldServerHandler());
    }
}
