package mytest.es;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.sniff.*;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import javax.net.ssl.SSLContext;
import javax.rmi.CORBA.Util;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

public class EsClientFactory {
    private TransportClient transportClient;
    private RestHighLevelClient restHighLevelClient;
    private Settings settings;
    private Sniffer sniffer;
    private ElasticSearchConfig esConfig;
    private int port = 9200;
    private int snifferInternalMillis = 60000;
    private int sniffAfterFailureDelayMillis = 30000;
    private long sniffRequestTimeoutMillis = 10000;
    private boolean enableSniffer = true;
    private String keyStore;
    private String trustStore;
    private String trustStorePwd;
    private String keyStorePwd;

    public EsClientFactory(ElasticSearchConfig esConfig) {
        this.esConfig = esConfig;
    }

    public TransportClient getTransportClient() {
        if (transportClient == null) {
            int seedsNum = esConfig.getSeeds().length;
            TransportAddress[] transportAddress = new TransportAddress[seedsNum];

            for (int i = 0; i < esConfig.getSeeds().length; i++) {
                try {
                    transportAddress[i] = new TransportAddress(InetAddress.getByName(esConfig.getSeeds()[i]), esConfig.getPort());
                } catch (UnknownHostException e) {
                    throw new ESConnectException("UnknownHostException");
                }
            }
            settings = Settings.builder().put("cluster.name", esConfig.getClusterName())
                    // .put("sniff",esConfig.getSniffer())
                    // .put("test","yetest")
                    .build();
            transportClient = new PreBuiltTransportClient(settings)
                    .addTransportAddresses(transportAddress);
        }
        return transportClient;
    }

    public RestHighLevelClient getRestHighLevelClient() {
        RestClientBuilder restClientBuilder = null;
        if (restHighLevelClient == null) {
            restClientBuilder = RestClient.builder(getHosts());

            if (trustStore != null) {
                setSSLContext(restClientBuilder);
            }

            restHighLevelClient = new RestHighLevelClient(restClientBuilder);

            if (enableSniffer) {
                setRestClientSniffer(restClientBuilder);
            }
        }
        return restHighLevelClient;
    }

    private HttpHost[] getHosts() {
        List<HttpHost> httpHosts = new ArrayList<>();
        for (String seed : esConfig.getSeeds()) {
            String[] seedSplit = StringUtils.split(seed, ":");
            if (seedSplit.length == 1) {
                httpHosts.add(new HttpHost(seedSplit[0], esConfig.getHttpPort(), trustStore == null ? "http" : "https"));
                continue;
            }
            if (seedSplit.length == 2) {
                httpHosts.add(new HttpHost(seedSplit[0], Integer.valueOf(seedSplit[1]), trustStore == null ? "http" : "https"));
            }
        }
        return httpHosts.toArray(new HttpHost[esConfig.getSeeds().length]);
    }

    private void setRestClientSniffer(RestClientBuilder restClientBuilder) {
        SniffOnFailureListener sniffOnFailureListener = new SniffOnFailureListener();
        restClientBuilder.setFailureListener(sniffOnFailureListener);

        sniffer = Sniffer.builder(restHighLevelClient.getLowLevelClient()).setSniffIntervalMillis(snifferInternalMillis)
                .setSniffAfterFailureDelayMillis(sniffAfterFailureDelayMillis)
                .setHostsSniffer(getHostSniffer(restHighLevelClient)).build();

        sniffOnFailureListener.setSniffer(sniffer);
    }

    private void setSSLContext(RestClientBuilder restClientBuilder) {
        URL keyStoreUrl = EsClientFactory.class.getResource('/' + keyStore);
        URL trustStoreUrl = EsClientFactory.class.getResource('/' + trustStore);
        try {
            try (InputStream tIn = new FileInputStream(Paths.get(trustStoreUrl.toURI()).toString());
                 InputStream kIn = new FileInputStream(Paths.get(keyStoreUrl.toURI()).toString());
            ) {
                KeyStore trustStore = KeyStore.getInstance("jks");
                KeyStore keyStore = KeyStore.getInstance("jks");

                trustStore.load(tIn, trustStorePwd.toCharArray());
                keyStore.load(kIn, keyStorePwd.toCharArray());

                SSLContextBuilder builder = SSLContexts.custom().loadTrustMaterial(trustStore, null)
                        .loadKeyMaterial(keyStore, keyStorePwd.toCharArray());
                SSLContext sslContext = builder.build();
                restClientBuilder.setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder
                        .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).setSSLContext(sslContext));
            }
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        } catch (URISyntaxException e) {

        } catch (KeyStoreException e) {

        } catch (CertificateException e) {

        } catch (NoSuchAlgorithmException e) {

        } catch (UnrecoverableKeyException e) {

        } catch (KeyManagementException e) {

        }
    }

    private HostsSniffer getHostSniffer(RestHighLevelClient restHighLevelClient) {
        ElasticsearchHostsSniffer hostsSniffer = null;
        if (keyStore != null) {
            hostsSniffer = new ElasticsearchHostsSniffer(restHighLevelClient.getLowLevelClient(), sniffRequestTimeoutMillis, ElasticsearchHostsSniffer.Scheme.HTTPS);
        }
        return hostsSniffer;
    }

    public ElasticSearchConfig getEsConfig() {
        return esConfig;
    }

    public void setEsConfig(ElasticSearchConfig esConfig) {
        this.esConfig = esConfig;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getSnifferInternalMillis() {
        return snifferInternalMillis;
    }

    public void setSnifferInternalMillis(int snifferInternalMillis) {
        this.snifferInternalMillis = snifferInternalMillis;
    }

    public int getSniffAfterFailureDelayMillis() {
        return sniffAfterFailureDelayMillis;
    }

    public void setSniffAfterFailureDelayMillis(int sniffAfterFailureDelayMillis) {
        this.sniffAfterFailureDelayMillis = sniffAfterFailureDelayMillis;
    }

    public long getSniffRequestTimeoutMillis() {
        return sniffRequestTimeoutMillis;
    }

    public void setSniffRequestTimeoutMillis(long sniffRequestTimeoutMillis) {
        this.sniffRequestTimeoutMillis = sniffRequestTimeoutMillis;
    }

    public boolean isEnableSniffer() {
        return enableSniffer;
    }

    public void setEnableSniffer(boolean enableSniffer) {
        this.enableSniffer = enableSniffer;
    }

    public String getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(String keyStore) {
        this.keyStore = keyStore;
    }

    public String getTrustStore() {
        return trustStore;
    }

    public void setTrustStore(String trustStore) {
        this.trustStore = trustStore;
    }

    public String getTrustStorePwd() {
        return trustStorePwd;
    }

    public void setTrustStorePwd(String trustStorePwd) {
        this.trustStorePwd = trustStorePwd;
    }

    public String getKeyStorePwd() {
        return keyStorePwd;
    }

    public void setKeyStorePwd(String keyStorePwd) {
        this.keyStorePwd = keyStorePwd;
    }
}
