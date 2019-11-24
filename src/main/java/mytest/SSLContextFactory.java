package mytest;

import mytest.netty.http.HttpHelloWorldServer;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.security.KeyStore;

public class SSLContextFactory {
    public static SSLContext getSSLContext() {
        SSLContext sslCtxs = null;
        String keyStore = "client-keystore.jks";
        String trustStore = "truststore.jks";
        String keyStorePwd = "234BOPatu";
        String trustStorePwd = "123BAUjil";

        URL keyStoreUrl = HttpHelloWorldServer.class.getResource('/' + keyStore);
        URL trustStoreUrl = HttpHelloWorldServer.class.getResource('/' + trustStore);
        try (InputStream tIn = new FileInputStream(Paths.get(trustStoreUrl.toURI()).toString());
             InputStream kIn = new FileInputStream(Paths.get(keyStoreUrl.toURI()).toString())
        ) {
            KeyStore trust = KeyStore.getInstance("jks");
            KeyStore key = KeyStore.getInstance("jks");

            trust.load(tIn, trustStorePwd.toCharArray());
            key.load(kIn, keyStorePwd.toCharArray());

            SSLContextBuilder builder = SSLContexts.custom()
                    .loadKeyMaterial(key, keyStorePwd.toCharArray());
            sslCtxs = builder.build();

        } catch (Exception e) {

        }
        return sslCtxs;
    }

    public static SSLContext getDoubleSSLContext() {
        SSLContext sslCtxs = null;
        String keyStore = "client-keystore.jks";
        String trustStore = "truststore.jks";
        String keyStorePwd = "234BOPatu";
        String trustStorePwd = "123BAUjil";

        URL keyStoreUrl = HttpHelloWorldServer.class.getResource('/' + keyStore);
        URL trustStoreUrl = HttpHelloWorldServer.class.getResource('/' + trustStore);
        try (InputStream tIn = new FileInputStream(Paths.get(trustStoreUrl.toURI()).toString());
             InputStream kIn = new FileInputStream(Paths.get(keyStoreUrl.toURI()).toString());
        ) {
            KeyStore trust = KeyStore.getInstance("jks");
            KeyStore key = KeyStore.getInstance("jks");

            trust.load(tIn, trustStorePwd.toCharArray());
            key.load(kIn, keyStorePwd.toCharArray());

            SSLContextBuilder builder = SSLContexts.custom().loadTrustMaterial(trust, null)
                    .loadKeyMaterial(key, keyStorePwd.toCharArray());
            sslCtxs = builder.build();

        } catch (Exception e) {

        }
        return sslCtxs;
    }
}
