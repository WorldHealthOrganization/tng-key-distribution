package tng.trustnetwork.keydistribution.service;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import eu.europa.ec.dgc.gateway.connector.DgcGatewayDownloadConnector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import tng.trustnetwork.keydistribution.clients.UniversalResolverClient;
import tng.trustnetwork.keydistribution.model.EcPublicKeyJwk;
import tng.trustnetwork.keydistribution.model.JwkVerificationMethod;

@SpringBootTest
class UniversalResolverServiceTest {

    @MockBean
    DgcGatewayDownloadConnector dgcGatewayDownloadConnector;

    @MockBean
    UniversalResolverClient universalResolverClientMock;

    @Autowired
    UniversalResolverService universalResolverService;

    private static final String testDidId = "did:web:did.actor:mike";

    private static final String testDid = """                                  
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
                         "crv": "P-256",
                         "x": "xValue"
                       }
                     }
                   ],
                   "authentication": [
                     "did:web:did.actor:mike#g1"
                   ],
                   "assertionMethod": [
                     "did:web:did.actor:mike#g1"
                   ]
                 }
                 """;

    @Test
    void itShouldReturnParsedDidDocument() throws JsonProcessingException {

        when(universalResolverClientMock.getDidDocument(testDidId))
            .thenReturn(testDid);

        UniversalResolverService.DidDocumentWithRawResponse response =
            universalResolverService.universalResolverApiCall(testDidId);

        Assertions.assertEquals(testDid, response.raw());
        Assertions.assertEquals(testDidId, response.didDocument().getId());
        Assertions.assertEquals(1, response.didDocument().getVerificationMethod().size());
        Assertions.assertEquals(testDidId, response.didDocument().getVerificationMethod().get(0).getObjectValue().getController());
        Assertions.assertEquals("#g1", response.didDocument().getVerificationMethod().get(0).getObjectValue().getId());

        Assertions.assertInstanceOf(JwkVerificationMethod.class, response.didDocument().getVerificationMethod().get(0).getObjectValue());
        JwkVerificationMethod jwkVerificationMethod =
            (JwkVerificationMethod) response.didDocument().getVerificationMethod().get(0).getObjectValue();

        Assertions.assertInstanceOf(EcPublicKeyJwk.class, jwkVerificationMethod.getPublicKeyJwk());
        EcPublicKeyJwk ecPublicKeyJwk = (EcPublicKeyJwk) jwkVerificationMethod.getPublicKeyJwk();

        Assertions.assertEquals(EcPublicKeyJwk.Curve.P256 , ecPublicKeyJwk.getCrv());
        Assertions.assertEquals("xValue", ecPublicKeyJwk.getXvalue());
    }

    @Test
    void itShouldThrowAnExceptionIfJsonIsInvalid() {

        when(universalResolverClientMock.getDidDocument(testDidId))
            .thenReturn("noValidJson");

        Assertions.assertThrows(JsonProcessingException.class, () ->
            universalResolverService.universalResolverApiCall(testDidId));
    }
}
