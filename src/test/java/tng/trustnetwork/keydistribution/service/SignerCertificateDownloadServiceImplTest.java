/*package tng.trustnetwork.keydistribution.service;

import tng.trustnetwork.keydistribution.entity.SignerInformationEntity;
import tng.trustnetwork.keydistribution.model.TrustListItem;
import tng.trustnetwork.keydistribution.repository.SignerInformationRepository;
import tng.trustnetwork.keydistribution.testdata.SignerInformationTestHelper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class SignerCertificateDownloadServiceImplTest {

    @Autowired
    SignerCertificateDownloadServiceImpl signerCertificateDownloadService;

    @Autowired
    SignerInformationRepository signerInformationRepository;

    @Autowired
    SignerInformationTestHelper signerInformationTestHelper;

    @MockBean
    DummyDownloadConnector dummyDownloadConnector;

    @Test
    void downloadEmptyCertificatesList() {
        ArrayList<TrustListItem> trustList = new ArrayList<>();
        Mockito.when(dummyDownloadConnector.getTrustedCertificates()).thenReturn(trustList);

        signerCertificateDownloadService.downloadCertificates();

        List<SignerInformationEntity> repositoryItems = signerInformationRepository.findAllByDeletedOrderByIdAsc(false);
        Assertions.assertEquals(0, repositoryItems.size());
    }
}
*/
