package tng.trustnetwork.keydistribution.service.did;


import com.apicatalog.jsonld.document.JsonDocument;
import com.apicatalog.jsonld.loader.DocumentLoader;
import foundation.identity.jsonld.ConfigurableDocumentLoader;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tng.trustnetwork.keydistribution.config.KdsConfigProperties;

@Slf4j
@Configuration
public class KdsDidContextDocumentLoaderConfig {

    public static final List<String> DID_CONTEXTS = List.of(
        "https://www.w3.org/ns/did/v1",
        "https://w3id.org/security/suites/jws-2020/v1");

    private static final String DID_CONTEXT_PATH = "did_contexts/";

    @Bean
    DocumentLoader kdsContextLoader(KdsConfigProperties configProperties) {

        Map<URI, JsonDocument> contextMap = new HashMap<>();
        for (String didContext : DID_CONTEXTS) {
            String didContextFile = configProperties.getDid().getContextMapping().get(didContext);

            if (didContextFile == null) {
                throw new BeanInitializationException("Failed to load DID-Context Document for " + didContext
                                                          + " : No Mapping to local JSON-File.");
            }

            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(
                DID_CONTEXT_PATH + didContextFile)) {
                if (inputStream != null) {
                    contextMap.put(URI.create(didContext), JsonDocument.of(inputStream));
                }
            } catch (Exception e) {
                throw new BeanInitializationException("Failed to load DID-Context Document", e);
            }
        }
        return new ConfigurableDocumentLoader(contextMap);
    }
}
