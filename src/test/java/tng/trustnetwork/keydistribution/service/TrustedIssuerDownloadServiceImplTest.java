package tng.trustnetwork.keydistribution.service;

import java.util.ArrayList;
import java.util.List;

import eu.europa.ec.dgc.gateway.connector.DgcGatewayDownloadConnector;
import eu.europa.ec.dgc.gateway.connector.model.TrustedIssuer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import tng.trustnetwork.keydistribution.entity.TrustedIssuerEntity;
import tng.trustnetwork.keydistribution.repository.TrustedIssuerRepository;
import tng.trustnetwork.keydistribution.testdata.TrustedIssuerTestHelper;

@SpringBootTest
@TestPropertySource(properties = {"dgc.trustedIssuerDownloader.enabled=true"})
class TrustedIssuerDownloadServiceImplTest {

    @Autowired
    TrustedIssuerDownloadServiceImpl trustedIssuerDownloadService;

    @Autowired
    TrustedIssuerRepository trustedIssuerRepository;

    @Autowired
    TrustedIssuerTestHelper trustedIssuerTestHelper;

    @MockBean
    DgcGatewayDownloadConnector dgcGatewayDownloadConnector;

    @Test
    void downloadEmptyIssuerList() {
        ArrayList<TrustedIssuer> trustList = new ArrayList<>();
        Mockito.when(dgcGatewayDownloadConnector.getTrustedIssuers()).thenReturn(trustList);

        trustedIssuerDownloadService.downloadTrustedIssuers();

        List<TrustedIssuerEntity> repositoryItems = trustedIssuerRepository.findAll();
        Assertions.assertEquals(0, repositoryItems.size());
    }
}
