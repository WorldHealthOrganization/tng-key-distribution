package tng.trustnetwork.keydistribution.service;

import eu.europa.ec.dgc.gateway.connector.DgcGatewayDownloadConnector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import tng.trustnetwork.keydistribution.model.DidDocument;
import tng.trustnetwork.keydistribution.model.DidDocumentUnmarshal;

@SpringBootTest
class UniversalResolverServiceImplTest {

    @MockBean
    private UniversalResolverClient universalResolverClient;

    @Autowired
    private UniversalResolverService universalResolverService;

    @MockBean
    DgcGatewayDownloadConnector dgcGatewayDownloadConnector;

    @Autowired
    SignerCertificateDownloadServiceImpl signerCertificateDownloadService;

    @Test
    void resolveDIDDocumentNotNull() {

        DidDocument didDocument = new DidDocument();
        Mockito.when(universalResolverClient.getDidDocument("did:web:tng-cdn-dev.who.int:trustlist"))
               .thenReturn(didDocument);
        DidDocumentUnmarshal didDocumentUnmarshal =
            universalResolverService.universalResolverApiCall("did:web:tng-cdn-dev.who.int:trustlist");
        Assertions.assertNotNull(didDocumentUnmarshal);

    }

    @Test
    void resolveDIDDocumentNull() {

        DidDocument didDocument = new DidDocument();
        Mockito.when(universalResolverClient.getDidDocument("test")).thenReturn(null);
        DidDocumentUnmarshal didDocumentUnmarshal = universalResolverService.universalResolverApiCall("test");
        Assertions.assertNull(didDocumentUnmarshal);

    }

}
