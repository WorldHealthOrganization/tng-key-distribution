package tng.trustnetwork.keydistribution.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.dgc.gateway.connector.DgcGatewayDownloadConnector;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import tng.trustnetwork.keydistribution.model.DidDocument;

@SpringBootTest
class UniversalResolverServiceTest {

    @MockBean
    DgcGatewayDownloadConnector dgcGatewayDownloadConnector;

    @Autowired
    SignerCertificateDownloadService signerCertificateDownloadService;

    @Autowired
    ObjectMapper objectMapper;

    String did = """                                  
                 {
                   "@context": [
                     "https://www.w3.org/ns/did/v1",
                     {
                       "@base": "did:web:did.actor:mike",
                       "rating": "https://schema.org/Rating",
                       "publicAccess": "https://schema.org/publicAccess",
                       "additionalType": "https://schema.org/additionalType"
                     }
                   ],
                   "id": "did:web:did.actor:mike",
                   "rating": 4.5,
                   "publicAccess": true,
                   "additionalType": null,
                   "verificationMethod": [
                     {
                       "id": "#g1",
                       "controller": "did:web:did.actor:mike",
                       "type": "JsonWebKey2020",
                       "publicKeyJwk": {
                         "kty": "EC",
                         "crv": "BLS12381_G1",
                         "x": "hxF12gtsn9ju4-kJq2-nUjZQKVVWpcBAYX5VHnUZMDilClZsGuOaDjlXS8pFE1GG"
                       }
                     },
                     {
                       "id": "#g2",
                       "controller": "did:web:did.actor:mike",
                       "type": "JsonWebKey2020",
                       "publicKeyJwk": {
                         "kty": "EC",
                         "crv": "BLS12381_G2",
                         "x": "l4MeBsn_OGa2OEDtHeHdq0TBC8sYh6QwoI7QsNtZk9oAru1OnGClaAPlMbvvs73EABDB6GjjzybbOHarkBmP6pon8H1VuMna0nkEYihZi8OodgdbwReDiDvWzZuXXMl-"
                       }
                     }
                   ],
                   "authentication": [
                     "did:web:did.actor:mike#g1",
                     "did:web:did.actor:mike#g2"
                   ],
                   "assertionMethod": [
                     "did:web:did.actor:mike#g1",
                     "did:web:did.actor:mike#g2"
                   ]
                 }
                 """;

    @Test
    void resolveDIDDocumentNotNull() {

    }

    @Test
    void ttest() throws JsonProcessingException {


        DidDocument parsed = objectMapper.readValue(did, DidDocument.class);

        String newJson = objectMapper.writeValueAsString(parsed);

        newJson = newJson;

    }
}
