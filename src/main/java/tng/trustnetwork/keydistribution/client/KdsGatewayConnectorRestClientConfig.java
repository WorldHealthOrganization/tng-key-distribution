package tng.trustnetwork.keydistribution.client;

//import eu.europa.ec.dgc.gateway.connector.config.DgcGatewayConnectorConfigProperties;
import feign.Client;
import feign.httpclient.ApacheHttpClient;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import javax.net.ssl.SSLContext;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty("kds.gateway.connector.tls-key-store.path")
@Configuration
@RequiredArgsConstructor
@EnableFeignClients
public class KdsGatewayConnectorRestClientConfig {

    private final KdsGatewayConnectorConfigProperties properties;

    @Qualifier("tlsKeyStore")
    private final KeyStore tlsKeyStore;

    @Qualifier("tlsTrustStore")
    private final KeyStore tlsTrustStore;

    /**
     * Feign Client for connection to DGC Gateway.
     *
     * @return Instance of HttpClient
     */
    @Bean
    public Client client() throws
        UnrecoverableKeyException, CertificateException,
        IOException, NoSuchAlgorithmException,
        KeyStoreException, KeyManagementException {

        return new ApacheHttpClient(HttpClientBuilder.create()
            .setSSLContext(getSslContext())
            .setDefaultHeaders(Arrays.asList(
                new BasicHeader("Accept-Encoding", "gzip, deflate, br"),
                new BasicHeader("Connection", "keep-alive")
            ))
            .setSSLHostnameVerifier(new DefaultHostnameVerifier())
            .setProxy(getProxy())
            .build());
    }

    private SSLContext getSslContext() throws
        IOException, UnrecoverableKeyException,
        CertificateException, NoSuchAlgorithmException,
        KeyStoreException, KeyManagementException {
        return SSLContextBuilder.create()
            .loadTrustMaterial(tlsTrustStore, null)
            .loadKeyMaterial(
                tlsKeyStore, properties.getTlsKeyStore().getPassword())
            .build();
    }

    private HttpHost getProxy() {
        if (properties.getProxy().isEnabled()) {
            return new HttpHost(properties.getProxy().getHost(), properties.getProxy().getPort());
        } else {
            return null;
        }
    }

}
