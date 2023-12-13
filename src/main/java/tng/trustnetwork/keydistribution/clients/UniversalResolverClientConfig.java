package tng.trustnetwork.keydistribution.clients;

import feign.Client;
import feign.httpclient.ApacheHttpClient;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import lombok.RequiredArgsConstructor;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class UniversalResolverClientConfig {

    // Create a custom TrustManager that trusts all certificates
    TrustManager[] trustAllCerts = new TrustManager[] {
        new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {

                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {

            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {

            }
        }
    };

    /**
     * Feign Client for connection to universal resolver.
     *
     * @return Instance of HttpClient
     */
    @Bean("feignClientUniversalResolver")
    public Client client() throws NoSuchAlgorithmException,
                                  KeyManagementException {

        //TODO for universal resolver https requests we skip TLS verification and trust all certs
        // this is temporary config used until self hosted resolver will be used
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        return new ApacheHttpClient(HttpClientBuilder.create()
                                                     .setSSLContext(sslContext)
                                                     .setDefaultHeaders(Arrays.asList(
                                                         new BasicHeader("Accept-Encoding", "gzip, deflate, br"),
                                                         new BasicHeader("Connection", "keep-alive")
                                                     ))
                                                     .setSSLHostnameVerifier(new DefaultHostnameVerifier())
                                                     .build());
    }
}
