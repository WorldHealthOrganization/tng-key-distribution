package tng.trustnetwork.keydistribution.scheduler;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tng.trustnetwork.keydistribution.dto.CertificateTypeDto;
import tng.trustnetwork.keydistribution.dto.TrustListItemDto;
import tng.trustnetwork.keydistribution.service.InputServiceImpl;

@Slf4j
@Component
@ConditionalOnProperty("dgc.trustlistDownloader.enabled")
public class InputScheduler {

    @Autowired
    private InputServiceImpl inputServiceImpl;
    
    
    Logger log = LoggerFactory.getLogger(InputScheduler.class);

    @Scheduled(fixedDelayString = "${dgc.trustlistDownloader.timeInterval}")
    @SchedulerLock(name = "TrustListService_downloadTrustList", lockAtLeastFor = "PT0S",
        lockAtMostFor = "${dgc.trustlistDownloader.lockLimit}")
    public void downloadTrustList() throws InterruptedException {
        log.info("trust list call started");
        ResponseEntity<String> response = inputServiceImpl.getDataLoader();        
        log.info("Trust list call finished");
    }
}
